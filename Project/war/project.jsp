<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.Integer" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.ThreadManager" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%!
	String Ricerca = "Libera";
	
  %>


<%


/*
		class Task implements Runnable {
    
		String str;
        Task(String s) { str = s; }
        public void run() {
             {
					  try 
					  {
						  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
						  String Ricerca_thread = "Libera";
						  Query dataquery  = new Query ("util");
							List<Entity> resultdata = datastore.prepare(dataquery).asList(FetchOptions.Builder.withDefaults());
						  	
						  	while (Ricerca_thread != "Libera") 
						  	{
								
								
								System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
							  	Thread.sleep(5000);
								
								if (resultdata.isEmpty())
								{
									System.out.println(	"LISTA VUOTA");
								}
								else
								{	
									for (Entity post : resultdata) 
									{
										Ricerca_thread = post.getProperty("Ricerca").toString();  // prende il parametro ricerca
										//System.out.println(	"ID TWEET:" + post.getProperty("IDtweet") + "  ID USER:"+ post.getProperty("IDuser") +
										//	"  COORD:" + post.getProperty("latitude") +"  "+  post.getProperty("longitude") + "KEY" + post.getKey() );
									}			
								}
							  
						  }
					  } 
					  catch (InterruptedException ex) 
					  {
						  throw new RuntimeException("Interrupted in loop:", ex);
					  } 
				  }
        }
    }

*/
%>

<html>
  <head>
  
   <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  
  <%
  
  String Avvio_Ricerca = request.getParameter("Ricerca");
	
	// Controllo sulla ricerca
	
	if (Avvio_Ricerca != null)
	{
		if (Avvio_Ricerca.compareTo("Attivata") == 0)
		{
		
			Ricerca = "Attiva";  // Indica che è stata attivata una ricerca
			%>
			<meta http-equiv="refresh" content="10; URL='/project.jsp?Ricerca=incorso'">
			<%
    	}
		
		else if (Avvio_Ricerca.compareTo("incorso") == 0)
		{
		  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Key key_util = KeyFactory.createKey("Parametri", "util_param");
			// Vedere se vi è una ricerca attiva
			Query dataquery  = new Query ("util",key_util).setAncestor(key_util);
			List<Entity> resultdata = datastore.prepare(dataquery).asList(FetchOptions.Builder.withDefaults());
			
			if (resultdata.isEmpty())
			{
				System.out.println(	"LISTA VUOTA");
			}
			else
			{	
				for (Entity post : resultdata) 
				{
					Ricerca = post.getProperty("Ricerca").toString();  // prende il parametro ricerca
					System.out.println(Ricerca);
					//System.out.println(	"ID TWEET:" + post.getProperty("IDtweet") + "  ID USER:"+ post.getProperty("IDuser") +
					//	"  COORD:" + post.getProperty("latitude") +"  "+  post.getProperty("longitude") + "KEY" + post.getKey() );
				}			
			}
			if (Ricerca.compareTo("Libera") != 0)
			{
				%>
					<meta http-equiv="refresh" content="10; URL='/project.jsp?Ricerca=incorso'">
				<%
    		}
			else
			{
				%>
					<meta http-equiv="refresh" content="1; URL='/project.jsp'">
				<%
    		}
		
		}
		
	}
	
	
  	
	%>
   
    <link type="text/css" rel="stylesheet" href="/stylesheets/project.css" />
    <title>Progetto</title>
    
    
    
  </head>

  <body class="sfondo">
  
  
	<div class="container">
	<div class="container_2">
  
  		<!-- titolo in alto -->
  		<div class="top">
        	PROGETTO SISTEMI DISTRIBUITI
        </div>
  
  		<!-- Centro della pagina con mappa a sinistra e inserimento dati a destra -->
  		<div class="center">
        
        	<div class="mappa">
        		
        	</div>
        
        	<div class="ins_dati">
            	<!-- Inserimento dati per query su twitter -->
       			<form action="/project" method="post">
                <div class="riga_dati">HashTag: <input type="text" size="50" name="hashtag" placeholder="#example"/></div>
                <div class="riga_dati"><div>Since Date</div>
                Day: <input type="text" size="2" name="giorno_from" maxlength="2" placeholder="00"/> Month: <input type="text" size="2" name="mese_from" maxlength="2" placeholder="00"/> Year: <input type="text" size="4" name="anno_from" maxlength="4" placeholder="0000"/>  </div>
                <!-- hour: <input type="text" size="2" name="ore_from" maxlength="2" placeholder="hh"/>:<input type="text" size="2" name="minuti_from" maxlength="2" placeholder="mm"/></div> -->
                <div class="riga_dati"> <div>Until Date</div>
                Day: <input type="text" size="2" name="giorno_to" maxlength="2" placeholder="00"/> Month: <input type="text" size="2" name="mese_to" maxlength="2" placeholder="00"/> Year: <input type="text" size="4" name="anno_to" maxlength="4" placeholder="0000"/> </div>
                <!-- hour: <input type="text" size="2" name="ore_to" maxlength="2" placeholder="hh"/>:<input type="text" size="2" name="minuti_to" maxlength="2" placeholder="mm"/> </div> -->
                <div class="riga_dati"><input type="submit" value="Ricerca" <% if (Ricerca.compareTo("Libera") != 0 ) { %> disabled <% }  %> /></div>
                </form>
                
                
                
                <% // RIQUADRO RICERCA
					if (Ricerca.compareTo("Libera") != 0 )
					{
					%>
       				<div class="ricerca_attiva">
                	<p>RICERCA IN CORSO</p>
            		
                    
                    <%  if  (Ricerca.compareTo("Bloccata") == 0) 
						{
							DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
							Key key_util = KeyFactory.createKey("Parametri", "util_param");
							 Query dataquery  = new Query ("util",key_util).setAncestor(key_util);
							List<Entity> resultdata = datastore.prepare(dataquery).asList(FetchOptions.Builder.withDefaults());
							
							 Calendar calendar = new GregorianCalendar();
			
							int dif_minuti =0;  
							
							if (resultdata.isEmpty())
							{
								System.out.println(	"LISTA VUOTA");
							}
							else
							{	
								for (Entity post : resultdata) 
								{
									
									int minuti_att = calendar.get(Calendar.MINUTE);
									int minuti_ric = Integer.parseInt(post.getProperty("Minuti").toString());
									dif_minuti = (60 - minuti_att +  minuti_ric);  // prende il parametro ricerca
									//System.out.println(dif_minuti);
										//System.out.println(	"ID TWEET:" + post.getProperty("IDtweet") + "  ID USER:"+ post.getProperty("IDuser") +
										//	"  COORD:" + post.getProperty("latitude") +"  "+  post.getProperty("longitude") + "KEY" + post.getKey() );
										
								}	
								
								%>  <div class="riga_dati"> Mancano <%=dif_minuti%> minuti alla prossima ricerca </div>  <%		
							}
						}  
					%>
                    
      				</div>
        		
                <form action="/project" method="post">
                <div class="riga_dati"><input type="submit" value="Stop" /></div>
                 <input type="hidden" name="ferma" value="1"/>   <!-- 1 ferma -->
                </form>
                
                	<%
					}
				%>
                
        	</div>
        
        </div>
  
    
       <%
	   
	   		// CONTROLLO SE AVVENUTO ERRORE NELL'INSERIMENTO
	  		String Error = request.getParameter("Error");
			if (Error != null) {
        		%>
                	<p style="font-style:italic; color:red; font-size:16px;"> ERROR: <%=Error%></p>
				<%
    		}  
  
   			
				%>
        
        
   
    <!-- <p>Hello, ${fn:escapeXml(ricerca)}!</p> -->
    
    </div> 
    </div>
    
  </body>
</html>