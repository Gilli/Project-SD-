// Authors Gherardi Andrea - Gilli Daniele

package project;

/*	PROJECTSERVLET.JAVA
 *   	
 *   Servlet principale che gestisce le richieste lato-client. 
 * 
 */


import java.io.IOException;


import javax.servlet.http.*;


//import com.google.appengine.api.search.query.QueryParser.query_return;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;


import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;


import java.util.*;



@SuppressWarnings({ "serial" })
public class ProjectServlet extends HttpServlet {

	public boolean ricerca = false;
	
	
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//creo la chiave delle ricerche
	Key IndiceRicerche = KeyFactory.createKey("Indice", "Ricerche");
	
	Key key_util = KeyFactory.createKey("Parametri", "util_param");
	Entity entity_util = new Entity("util",key_util);

	
	Queue queue_task;
	
	
	//------------------------------------------------------------------
	// INIZIALIZZAZIONE DS NEL COSTRUTTORE
	//------------------------------------------------------------------
	public ProjectServlet()
	  {
		// INIZIALIZZAIONE ENTITA' UTIL
		com.google.appengine.api.datastore.Query queryHash  = new com.google.appengine.api.datastore.Query("util",key_util);
		 List<Entity> resultquery = datastore.prepare(queryHash).asList(FetchOptions.Builder.withDefaults());
		  
							  
		  if (resultquery.isEmpty())
		  {
			  System.out.println("Lista vuota");
		  }
		  else
		  {

			  for (Entity util : resultquery)
			  {

				 datastore.delete(util.getKey());					  
									  
			  }
		  }// fine else resultquery2.isEmpty()
		  
		
		  // inizializzazione tutto
		entity_util.setProperty("Fermata", "false");
		entity_util.setProperty("Ricerca", "Libera");
		entity_util.setProperty("Hashtag","");
		entity_util.setProperty("Since","");
		entity_util.setProperty("Until","");
		entity_util.setProperty("LastId",null);
		datastore.put(entity_util);
		
		
	    queue_task = QueueFactory.getDefaultQueue();  // coda per i task delle ricerche
		
	  }
	// ----------------------------------
	
	
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	
		//----------------------------------
    	// CARICAMENTO ENTITY_UTIL
    	//----------------------------------
		com.google.appengine.api.datastore.Query queryHash  = new com.google.appengine.api.datastore.Query("util",key_util ).setAncestor(key_util);;
		  List<Entity> resultquery2 = datastore.prepare(queryHash).asList(FetchOptions.Builder.withDefaults());
		  
							  
		  if (resultquery2.isEmpty())
		  {
			  System.out.println("ERRORE");
		  }
		  else
		  {

			  for (Entity post : resultquery2)
			  {
				  entity_util = post;
			  }
		  }// fine else resultquery2.isEmpty()
		
		
		if (request.getParameter("ferma") != null)
		{
			//queue_task.purge();
			
			// indico nel DS che è stata fermata
			//entity_util.setProperty("Ricerca", "Bloccata");
			entity_util.setProperty("Fermata", "true");
			datastore.put(entity_util);
			
			response.sendRedirect("/project.jsp");
			
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
	    		// hash è la hashtag
	    		String date_since = anno_from + "-" + mese_from + "-" + giorno_from;
	    		String date_until = anno_to + "-" + mese_to + "-" + giorno_to;
				 
	    		
	    		entity_util.setProperty("Fermata", "false");
	    		entity_util.setProperty("Ricerca", "Attiva");
	    		entity_util.setProperty("Hashtag",hashtag);
	    		entity_util.setProperty("Since",date_since);
	    		entity_util.setProperty("Until",date_until);
	    		entity_util.setProperty("LastId",null);
	    		datastore.put(entity_util);
	    		
	    		// salva l'indice ricerca
				Entity ParametriRicerche = new Entity ("Parametri", IndiceRicerche);
				ParametriRicerche.setProperty("Hashtag", hashtag);
				ParametriRicerche.setProperty("Since", date_since);
				ParametriRicerche.setProperty("Until", date_until);
				datastore.put(ParametriRicerche);
	    		
	    		// Si aggiunge alla coda il task per la ricerca che si avvia subito
	    		queue_task.add(withUrl("/taskRicerca"));
	    		
	    		
	    	}
	    	catch (Exception exc) { // eccezione nel caso di errori di inserimento dati
	    		exc.printStackTrace();
	    		response.sendRedirect("/project.jsp?Error=Wrong input data!");
	    		}    
			
			response.sendRedirect("/project.jsp?Ricerca=Attivata");  // Quando si avvia la ricerca
		}

	
	}
	
}

