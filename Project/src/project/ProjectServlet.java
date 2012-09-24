package project;

import java.io.IOException;


import javax.servlet.http.*;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import com.google.appengine.api.ThreadManager;
//import com.google.appengine.api.search.query.QueryParser.query_return;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
//import java.lang.Exception;

import java.util.*;
import java.lang.*;


@SuppressWarnings({ "serial" })
public class ProjectServlet extends HttpServlet {
	
	
	
	final static int num_tweet_page = 1;
	private Thread thread;
	private boolean fermato = false;
	public boolean ricerca = false;
	
	// Per orario
	Calendar calendar = new GregorianCalendar();
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key key_util = KeyFactory.createKey("Parametri", "util_param");
	Entity entity_util = new Entity("util",key_util);
	
	private boolean continuo_ricerca = false;
	private long last_id;
	
	private boolean lim_superato = false;
	

	
	
	class Task implements Runnable {
		String hash, date_s, date_u;
		
		 Task(String hashtag, String date_since, String date_until) {
			 hash = hashtag;
			 date_s = date_since;
			 date_u = date_until;
		 }

		  public void run() {
				boolean finito = false;
				int count_page = 1;
				
				// Modifica Paramatro Ricerca nel DS
				entity_util.setProperty("Ricerca", "Libera");
				datastore.put(entity_util);
				
				//creo il datastore
			//	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				
				Twitter twitter = new TwitterFactory().getInstance();
				//creo la chiave radice
				//Key HashKey = KeyFactory.createKey("Ricerche", "#progettosd 2012-09-10 2012-09-15");
				Key HashKey = KeyFactory.createKey("Ricerche", hash + " " + date_s + " " + date_u);
				
				// Query su DB twitter
				Query twquery = new Query(hash);
				twquery.rpp(num_tweet_page);
				QueryResult result = null;
				twquery.since(date_s);
				twquery.until(date_u);
			
					// WHILE RICERCA TOTALE CON TIMER
					// inizializzazione parametri per ogni hashtags
					finito=false;  
					continuo_ricerca = false;
					lim_superato = false;
					last_id = 0;
					
					while (finito == false)
					{
						// Verifica se continua ricerca precedente
						if (continuo_ricerca == true)
						{
						twquery.maxId(last_id-1);
						}
						
						// WHILE DELLE QUERY PAGINATE
						count_page =1;
						while (count_page <= 5)
						{
							entity_util.setProperty("Ricerca", "Attiva");
							datastore.put(entity_util);
		
							
							//page 1
							//System.out.println("PAGINA 11111111111111111111111111111111111111111111111111111111111");
							twquery.setPage(count_page);
							try {
								result = twitter.search(twquery);
								
				
								System.out.println("size " + result.getTweets().size() + "  res page" + result.getResultsPerPage());
								
								int count =0;
								
								for (Tweet tweet : result.getTweets()) {	count ++;
								last_id = tweet.getId();
								Status status = twitter.showStatus(tweet.getId());
								User xx =  twitter.showUser(tweet.getFromUserId());
								
								System.out.println(	"Numero di elementi:" + count);
								System.out.println(tweet.getGeoLocation() + tweet.getLocation() +"  ID TWEET "+ tweet.getId() +  " ID USER "+ tweet.getFromUserId() + tweet.getFromUser() + "  " +
										tweet.getText() + tweet.getCreatedAt() + tweet.getAnnotations());
								
								
								//salvo oggetto nel datastore
								Entity entity = new Entity(hash, HashKey );
								entity.setProperty("IDtweet", tweet.getId());
								entity.setProperty("IDuser", tweet.getFromUserId());
								
								//System.out.println( "   TW PLACE  " + tweet.getPlace());
								
								
								//GEOLOCATION
								if ((tweet.getGeoLocation()!=null) && (tweet.getGeoLocation().getLatitude()!= (0.0) && tweet.getGeoLocation().getLongitude()!=(0.0)))
								{
									
									System.out.println( "GEOLOCATION" + tweet.getGeoLocation().getLatitude() +"  "+  tweet.getGeoLocation().getLongitude() );
									entity.setProperty("latitude", tweet.getGeoLocation().getLatitude());
									entity.setProperty("longitude", tweet.getGeoLocation().getLongitude());
									//datastore.put(entity);
								}
								
								//STATUS
								else if ( status.getPlace() !=null)  
								{
										
										String [] coord = GoogleGeoCode.getLocation(status.getPlace().getFullName());
										entity.setProperty("latitude", coord[0]);
										entity.setProperty("longitude", coord[1]);
										System.out.println( "STATUS  " + status.getPlace() +  "   COOOORD" +  status.getPlace().getGeometryCoordinates() );
										//System.out.println( "STATUS  " + status.getPlace() +  "   GEOLOC" +  status.getGeoLocation());
										System.out.println( "STATUS LOCATION" +  "lat:" + coord[0]   + "   long" + coord[1] );
										//datastore.put(entity);
										
									
								}
								
								//USER
								else if (xx.getLocation() != null &&  !xx.getLocation().isEmpty() ) //user
								{
									// Possibile exception con place inventati
									try
									{
										System.out.println( "USER \n LOCATION" + xx.getLocation() );
										String [] coord = GoogleGeoCode.getLocation(xx.getLocation());
										entity.setProperty("latitude", coord[0]);
										entity.setProperty("longitude", coord[1]);
										System.out.println(	"GEOCODE SERVICE   lat:" + coord[0]   + "   long" + coord[1] );
										//datastore.put(entity);
									}
									catch(Exception e)
									{
										System.out.println( "Eccezione: nessuna posizione trovata");
									}
								}
								
								else
								{
									System.out.println( "nessuna posizione trovata");
									//salvataggio
									//datastore.put(entity);
								}
								
								datastore.put(entity);
								}
							}
									
							 catch (TwitterException e) {
								// TODO Auto-generated catch block
								 
								// limite superato 
								e.printStackTrace();
								count_page = 6;
								lim_superato =true;
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
									continuo_ricerca = true;
								}
							}
							
							
						} // FINE WHILE DELLE QUERY PAGINATE
						System.out.println("FINE query paginate");
						
						if (finito == false)
						{
							
							try {
								entity_util.setProperty("Ricerca", "Bloccata");
								datastore.put(entity_util);
								
								
								System.out.println("-------- 350 tweet, prossima ricerca fra un'ora -------");
								
								// salva i minuti
								 int minuti = calendar.get(Calendar.MINUTE);
								 entity_util.setProperty("Minuti", minuti);
									datastore.put(entity_util);
					
								
									
								Thread.sleep(300000); // dopo un'ora
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						if (fermato == true)
						{
							finito = true;
						}
						
					}// FINE WHILE RICERCA TOTALE CON TIMER
					System.out.println("FINE totale");
				
				
				entity_util.setProperty("Ricerca", "Libera");
				datastore.put(entity_util);
				fermato = false;
				finito = false;
				
				
		  }

	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	
	
		
			
		//------------------------------------------------------------------
		// FINE SOTTO-THREAD
		//------------------------------------------------------------------
		
		if (request.getParameter("ferma") != null)
		{
			fermato = true;
			System.out.println("INTERROTTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			response.sendRedirect("/project.jsp?Ricerca=incorso");
			
		}
		else
		{	
			// Controlli hashtag
			if (request.getParameter("hashtag") == "") // se vuoto
			{
				response.sendRedirect("/project.jsp?Error=Empty hashtag field!");
			}
			
			String hashtag = request.getParameter("hashtag"); // divido gli hashtag
			if (!hashtag.startsWith("#"))  // se non corretto l'hashtag
			{
				response.sendRedirect("/project.jsp?Error=No hashtag correct!");
			}
	
			
			int giorno_from, mese_from, anno_from;      //, ore_from, minuti_from;
			int giorno_to, mese_to, anno_to;    //, ore_to, minuti_to;
			
			try {
	    		// Controllo sul data from
	    		giorno_from = Integer.parseInt(request.getParameter("giorno_from"));
	    		mese_from = Integer.parseInt(request.getParameter("mese_from"));
	    		anno_from = Integer.parseInt(request.getParameter("anno_from"));
	    		//ore_from = Integer.parseInt(request.getParameter("ore_from"));
	    		//minuti_from = Integer.parseInt(request.getParameter("minuti_from"));
	    		if (giorno_from < 1 || giorno_from > 31 || mese_from < 1 || mese_from > 12 || anno_from < 1)
	    		{
	    			// Error nell'inserimento data from
	    			response.sendRedirect("/project.jsp?Error=Date from not correct!");
	    		}
	    	
	    		// Controllo sul data until
	    		giorno_to = Integer.parseInt(request.getParameter("giorno_to"));
	    		mese_to = Integer.parseInt(request.getParameter("mese_to"));
	    		anno_to = Integer.parseInt(request.getParameter("anno_to"));
	    		//ore_to = Integer.parseInt(request.getParameter("ore_to"));
	    		//minuti_to = Integer.parseInt(request.getParameter("minuti_to"));
	    		if (giorno_to < 1 || giorno_to > 31 || mese_to < 1 || mese_to > 12 || anno_to < 1)
	    		{
	    			// Error nell'inserimento data from
	    			response.sendRedirect("/project.jsp?Error=Date until not correct!");
	    		}
	    
	    		// DATI INSERITI CORRETTAMENTE
	    		// hash  la hashtag
	    		String date_since = anno_from + "-" + mese_from + "-" + giorno_from;
	    		String date_until = anno_to + "-" + mese_to + "-" + giorno_to;
				 
	    	
	    		thread = ThreadManager.createBackgroundThread(new Task(hashtag,date_since,date_until));
	    		thread.start();
	    		
	    		//s.stop();
	    		
	    		//s.service(request, response);
	    	}
	    	catch (Exception exc) {
	    		response.sendRedirect("/project.jsp?Error=Empty fields!");
	    		}    
			
			response.sendRedirect("/project.jsp?Ricerca=Attivata");
		}
		
		//s = new SearchServlet();
		//s.init();
	
	}
	
}

