package project;

import java.io.IOException;


import javax.servlet.http.*;
import java.util.List;

import twitter4j.*;


//import java.lang.Exception;

@SuppressWarnings({ "serial" , "unused"})
public class ProjectServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	
		// Prende parametri inseriti
		String [] hashtags = request.getParameter("hashtag").split(",");
		int giorno_from, mese_from, anno_from, ore_from, minuti_from;
		int giorno_to, mese_to, anno_to, ore_to, minuti_to;
		
		
		
		
		for(String hash:hashtags) // per ogni hashtag
		{
		    if (hash.startsWith("#"))  // se inizia con '#' OK
		    {
		    	try {
		    		// Controllo sul data from
		    		giorno_from = Integer.parseInt(request.getParameter("giorno_from"));
		    		mese_from = Integer.parseInt(request.getParameter("mese_from"));
		    		anno_from = Integer.parseInt(request.getParameter("anno_from"));
		    		ore_from = Integer.parseInt(request.getParameter("ore_from"));
		    		minuti_from = Integer.parseInt(request.getParameter("minuti_from"));
		    		if (giorno_from < 1 || giorno_from > 31 || mese_from < 1 || mese_from > 12 || anno_from < 1 || ore_from < 0 || ore_from > 23 || minuti_from < 0 || minuti_from > 59 )
		    		{
		    			// Error nell'inserimento data from
		    			response.sendRedirect("/project.jsp?Error=Date from not correct!");
		    		}
		    	
		    		// Controllo sul data until
		    		giorno_to = Integer.parseInt(request.getParameter("giorno_to"));
		    		mese_to = Integer.parseInt(request.getParameter("mese_to"));
		    		anno_to = Integer.parseInt(request.getParameter("anno_to"));
		    		ore_to = Integer.parseInt(request.getParameter("ore_to"));
		    		minuti_to = Integer.parseInt(request.getParameter("minuti_to"));
		    		if (giorno_to < 1 || giorno_to > 31 || mese_to < 1 || mese_to > 12 || anno_to < 1 || ore_to < 0 || ore_to > 23 || minuti_to < 0 || minuti_to > 59)
		    		{
		    			// Error nell'inserimento data from
		    			response.sendRedirect("/project.jsp?Error=Date until not correct!");
		    		}
		    
		    		// DATI INSERITI CORRETTAMENTE
		    		
		    	}
		    	catch (Exception exc) {
		    		response.sendRedirect("/project.jsp?Error=Empty fields!");
		    		}    
		    }
		    else
		    {
		    	// Error nell'inserimento hashtag
		    	response.sendRedirect("/project.jsp?Error=No hashtag correct!");
		    	
		    }
		}
		/*
		Twitter twitter = new TwitterFactory().getInstance(); 
        String qrySearch = "#Fifa12"; 
        Query query = new Query(qrySearch); 
        query.since("2012-09-10");
       // query.until("2012-09-13");
        query.setRpp(1);   // max limit 100
        QueryResult result;
		try {
			result = twitter.search(query); 
			 System.out.println("*** hits: " + result.getTweets().size()); 
		        for (Tweet tweet : result.getTweets()) { 
		                System.out.println("GeoLocation: " + tweet.getGeoLocation()); 
		                //System.out.println("               ("+ tweet.getFromUser() +")"+ 
	//	":" + tweet.getText()); 
		             
		      Status status = twitter.showStatus(tweet.getId());          
		      System.out.println("status: " + status.getPlace()); 
           
		    
		      
		                 
		        } // fine for
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	*/
		//String a = request.getParameter("hashtag");	
		//response.sendRedirect("/project.jsp?Location=" + a);

		
		
}
}
