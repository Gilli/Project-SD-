// Authors Gherardi Andrea - Gilli Daniele

package project;

/*	TASKSERVLET.JAVA
 *   	
 *   Codice del thread che esegue la ricerca dei hashtag. 
 * 
 */


import java.util.*;
import java.io.IOException;
import javax.servlet.http.*;

//TWITTER
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

// DATASTORE
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

// CODA
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;



@SuppressWarnings("serial")
public class TaskServlet extends HttpServlet {
	
	//--------------------------------
	// DICHIARAZIONE E INIZIALIZZAZIONE
	//--------------------------------
	
	final static int num_tweet_page = 70;  // 5 pagine da 30  max 150 per ora in localhost
	public boolean ricerca = false;
	private boolean lim_superato = false;
	Queue queue_task;
	
	
	// inizializzazione servizi datastore
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	// creazione chiave per parametri util
	Key key_util = KeyFactory.createKey("Parametri", "util_param");
	Entity entity_util = new Entity("util",key_util);
	
	
	// dati per la ricerca
	String Upperhash, date_s, date_u;
	private long last_id;
	
	//------------------------------------------------------------
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
    	//----------------------------------
    	// CARICAMENTO ENTITY_UTIL
    	//----------------------------------
    	com.google.appengine.api.datastore.Query queryHash  = new com.google.appengine.api.datastore.Query("util",key_util ).setAncestor(key_util);;
		  List<Entity> resultquery2 = datastore.prepare(queryHash).asList(FetchOptions.Builder.withDefaults());
		  
							  
		  if (resultquery2.isEmpty())
		  {
			  System.out.println(	"ERRORE");
		  }
		  else
		  {

			  for (Entity post : resultquery2)
			  {
				  entity_util = post;
				  Upperhash = post.getProperty("Hashtag").toString();
				  date_s = post.getProperty("Since").toString();
				  date_u = post.getProperty("Until").toString();
				  if (post.getProperty("LastId")!=null)
					  last_id = (Long) post.getProperty("LastId");
				  else
					  last_id = -1;
			  }
		  }// fine else resultquery2.isEmpty()
		  //_________________________________________________
		  
		  String hash = Upperhash.toLowerCase(); // trasformazione hashtag per non fare distinzioni tra maiusc e minusc
		  
		//---------------------------------------------
		// CONTROLLA SE E' UNA RICERCA CHE E' STATA INTERROTTA OPPURE DEVE RIPARTIRE O INIZIARE
		//------------------------------------------
		if (entity_util.getProperty("Fermata").toString().compareTo("true") == 0)
		{
			
			// Inizializzazione entity_util
			 entity_util.setProperty("Ricerca", "Libera");
			 entity_util.setProperty("Fermata", "false");
			 entity_util.setProperty("Hashtag","");
			 entity_util.setProperty("Since","");
			 entity_util.setProperty("Until","");
			 entity_util.setProperty("LastId",null);
			 datastore.put(entity_util);
		}
		else  // se deve ripartire o iniziare
		{
			  // imposta ricerca attiva
			  entity_util.setProperty("Ricerca", "Attiva");
			  datastore.put(entity_util);
			  
	
			  	// Impostazione della query su Twitter
		    	boolean finito = false;
				int count_page = 1;
				
				Twitter twitter = new TwitterFactory().getInstance();
				
				//creo la chiave radice
				Key HashKey = KeyFactory.createKey("Ricerche", hash + " " + date_s + " " + date_u);
				
				// Query su DB twitter
				Query twquery = new Query(hash);
				twquery.rpp(num_tweet_page);
				QueryResult result = null;
				twquery.since(date_s);
				twquery.until(date_u);
		
				finito=false;  
				//continuo_ricerca = false;
				lim_superato = false;
		
	    	
				
				// Verifica se continua ricerca precedente
				if (last_id != -1)    // non è una query che riparte
				{
				twquery.maxId(last_id-1);
				}
				
				// WHILE DELLE QUERY PAGINATE
				count_page =1;
				while (count_page <= 5)
				{
				
					twquery.setPage(count_page);
					try {
						result = twitter.search(twquery);
						
		
						System.out.println("size " + result.getTweets().size() + "  res page" + result.getResultsPerPage());
						
					
						// per ogni tweet trovato prende id, satus, e user
						for (Tweet tweet : result.getTweets()) {	
						last_id = tweet.getId();
						Status status = twitter.showStatus(tweet.getId());
						User xx =  twitter.showUser(tweet.getFromUserId());
						
						System.out.println(tweet.getGeoLocation() + tweet.getLocation() +"  ID TWEET "+ tweet.getId() +  " ID USER "+ tweet.getFromUserId() + tweet.getFromUser() + "  " +
								tweet.getText() + tweet.getCreatedAt() + tweet.getAnnotations());
						
						
						//salvo oggetto nel datastore
						Entity entity = new Entity(hash, HashKey );
						entity.setProperty("IDtweet", tweet.getId());
						entity.setProperty("IDuser", tweet.getFromUserId());
						
						//-------------------------------------------------
						// RICERCA E STIMA DELLA POSIZIONE
						//-------------------------------------------------
						
						//GEOLOCATION
						if ((tweet.getGeoLocation()!=null) && (tweet.getGeoLocation().getLatitude()!= (0.0) && tweet.getGeoLocation().getLongitude()!=(0.0)))
						{
							
							System.out.println( "GEOLOCATION" + tweet.getGeoLocation().getLatitude() +"  "+  tweet.getGeoLocation().getLongitude() );
							entity.setProperty("latitude", tweet.getGeoLocation().getLatitude());
							entity.setProperty("longitude", tweet.getGeoLocation().getLongitude());
						}
						
						//STATUS
						else if ( status.getPlace() !=null)  
						{
								String [] coord = GoogleGeoCode.getLocation(status.getPlace().getFullName());
								entity.setProperty("latitude", coord[0]);
								entity.setProperty("longitude", coord[1]);
								System.out.println( "STATUS  " + status.getPlace() +  "   COOOORD" +  status.getPlace().getGeometryCoordinates() );
								System.out.println( "STATUS LOCATION" +  "lat:" + coord[0]   + "   long" + coord[1] );				
						}
						
						//USER
						else if (xx.getLocation() != null &&  !xx.getLocation().isEmpty() ) //user
						{
							// Possibile exception con place inventati gestita
							try
							{
								System.out.println( "USER \n LOCATION" + xx.getLocation() );
								String [] coord = GoogleGeoCode.getLocation(xx.getLocation());
								entity.setProperty("latitude", coord[0]);
								entity.setProperty("longitude", coord[1]);
								System.out.println(	"GEOCODE SERVICE   lat:" + coord[0]   + "   long" + coord[1] );
							}
							catch(Exception e)
							{
								System.out.println( "Eccezione: nessuna posizione trovata");
							}
						}
						
						else
						{
							System.out.println( "nessuna posizione trovata");
						
						}
						
						datastore.put(entity);
						}
					}
							
					 catch (TwitterException e) {
						// TODO Auto-generated catch block
						 
						//Eccezione nel caso di limite superato 
						e.printStackTrace();
						count_page = 6; // si finisce il ciclo
						lim_superato =true; // si ripete la ricerca dall'ultimo last_id
					}
					
					// Se trovati meno di 70 quindi query finita
					System.out.println( "Fine pagina: " + count_page );
					count_page++;
					if (result.getTweets().size() < num_tweet_page && lim_superato == false)
					{
						count_page =6;
						finito = true;
					}// FINE IF 
					else // se non finita la ricerca
					{
						if (count_page == 5 || lim_superato == true)
						{
							// se non finita salva il last_id
							entity_util.setProperty("LastId",last_id);
							datastore.put(entity_util);
						}
					}
					
					
				} // FINE WHILE DELLE QUERY PAGINATE
				System.out.println("FINE query paginate");
				
				
				if (finito == false)  // ricerca bloccata per un'ora per limite raggiunto
				{	
		
						// Per orario prossima ricerca
						Calendar calendar = new GregorianCalendar();
						int minuti = calendar.get(Calendar.MINUTE);
						entity_util.setProperty("Minuti", minuti);	
						entity_util.setProperty("Ricerca", "Bloccata");
						entity_util.setProperty("LastId",last_id);
						datastore.put(entity_util);
					
						
						System.out.println("-------- 350 tweet, prossima ricerca fra un'ora -------");
						System.out.println("MINUTI:   " + minuti);
			
						
						// Viene messo nella coda il task stesso facendolo ripartire dopo un'ora
						queue_task = QueueFactory.getDefaultQueue();
						queue_task.add(withUrl("/taskRicerca").countdownMillis(4000000)); // aspetta un'ora e 1,40 minuti
						//queue_task.add(withUrl("/taskRicerca").countdownMillis(10000));  // localhost
					
				}
				else  // SE HA FINITO
				{
				
					System.out.println("FINE totale");
					
					// Inizializza entity_util
					entity_util.setProperty("Fermata", "false");
					entity_util.setProperty("Ricerca", "Libera");
					entity_util.setProperty("Minuti", null);	
					entity_util.setProperty("LastId",null);
					entity_util.setProperty("Since","");
					entity_util.setProperty("Until","");
					datastore.put(entity_util);
					finito = false;
				}
		}
    }
    
    
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
    	doPost(req,resp);
    	
    }
}