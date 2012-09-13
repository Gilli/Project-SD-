package project;

import java.io.IOException;


import javax.servlet.http.*;
import java.util.List;

import twitter4j.*;


//import java.lang.Exception;

@SuppressWarnings({ "serial"})
public class ProjectServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		 // The factory instance is re-useable and thread safe.
		// The factory instance is re-useable and thread safe.
	    Twitter twitter = new TwitterFactory().getInstance();
	    List<Status> statuses;
		try {
			statuses = twitter.getHomeTimeline();
			  System.out.println("Showing friends timeline.");
			    for (Status status : statuses) {
			        System.out.println(status.getUser().getName() + ":" +
			                           status.getText());
			    }
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
		
		
		
		response.setContentType("text/plain");
		response.getWriter().println("Hello, world");
	
}
}
