<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.lang.Integer" %>
<%@ page import="java.lang.Number" %>
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
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key key_util = KeyFactory.createKey("Parametri", "util_param");
	Key IndiceRicerche = KeyFactory.createKey("Indice", "Ricerche");
	
	
	
	
  %>


<html>
  <head>
  <link type="text/css" rel="stylesheet" href="/Stylesheets/project.css" />
  
   <meta http-equiv="content-type" content="text/html; charset=UTF-8">
   
   <script language="javascript">
<!--  Funzione per chiedere conferma -->
	function confirmSubmit()
	{
		var agree=confirm("Sicuro di voler eliminare le ricerche selezionate?");
		if (agree) 
		{
			return true ;
		}
		else 
		{	
			return false ;
		}
	}
</script>
   
   
   <%
   if (request.getParameter("Cancella_Ricerche") != null)
	{
		if (request.getParameterValues("elenco_ricerche") != null)
					{
						  String[] values = request.getParameterValues("elenco_ricerche");
						  // CONTROLLO SUL NUMERO DI RICERCHE
						  
						  
							  
							  
								  
						  
							  for (String v : values)
							  {
								  
								  // PRENDO IL NOME DELL'HASHTAG
								  String [] Hash = v.split("\\|");
								  String new_v = v.replace("|"," ");
								  
								  
								  
								  Key temp= KeyFactory.createKey("Ricerche", new_v);
									
						 		 //cancello tutti i tweet della ricerca
								  Query queryHash  = new Query(Hash[0] , temp);
								  List<Entity> resultquery2 = datastore.prepare(queryHash).asList(FetchOptions.Builder.withDefaults());
								  
													  
								  if (resultquery2.isEmpty())
								  {
									  System.out.println(	"NESSUN TWEET TROVATO");
								  }
								  else
								  {
						  
									  for (Entity has : resultquery2)
									  {
	
										 datastore.delete(has.getKey());					  
															  
									  }
								  }// fine else resultquery2.isEmpty()
						
						
					
						
							// cancella indice ricerche  
							  Query paramquery  = new Query("Parametri", IndiceRicerche);
							List<Entity> resultq = datastore.prepare(paramquery).asList(FetchOptions.Builder.withDefaults());
								
							if (resultq.isEmpty())
								{
									//System.out.println(	"NESSUNA RICERCA TROVATA");
									//empty_elenco_ricerche = true;
								}
							else
								{
								
									for (Entity post : resultq) 
										{
											String hash = post.getProperty("Hashtag").toString();
											String since = post.getProperty("Since").toString();
											String until = post.getProperty("Until").toString();
											String value_input = hash + " " + since + " " + until;
											
											if (value_input.compareTo(new_v) == 0)
											{
												datastore.delete(post.getKey());
											}
										}
								}
						
							  }// fine for string values
							
					}
	}
	%>
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
        	<div class="ricerche">
                
                	<form action="/Options.jsp" method="post">
               		<%	
						
						boolean empty_elenco_ricerche = false;  
							//interrogo il DS

							Query paramquery  = new Query("Parametri", IndiceRicerche);
							List<Entity> resultq = datastore.prepare(paramquery).asList(FetchOptions.Builder.withDefaults());
								
							if (resultq.isEmpty())
								{
									//System.out.println(	"NESSUNA RICERCA TROVATA");
									empty_elenco_ricerche = true;
								}
							else
								{
								
									for (Entity post : resultq) 
										{
											//System.out.println(post.getKey());
											String hash = post.getProperty("Hashtag").toString();
											String since = post.getProperty("Since").toString();
											String until = post.getProperty("Until").toString();
											String value_input = hash + "|" + since + "|" + until;
										 %> <!-- RIGA CON SINGOLA RICERCA -->
                                         <div class="riga_ricerche">
                                         	<!-- Input -->
                                            <input type="checkbox" name="elenco_ricerche" value=<%=value_input%>  />
                                         	<!-- Testo -->
                                         	HASH: <%=hash%> SINCE: <%=since%> UNTIL: <%=until%>
                                            
                                         </div>
										 <%
											
									
										}
								
								
								}
					
				
					// Controllo sull'elenco se vuoto non visualizza il bottone ricerche
					if (empty_elenco_ricerche != true) // se elenco ricerche non vuoto
					{
					%>
 	                   <div class="riga_ricerche" style="margin-bottom:20px;"> <input type="submit" value="Cancella_Ricerche" name="Cancella_Ricerche" onClick="return confirmSubmit()" /></div>
                    <%
                    }
					
                    %>
                    </form>
               </div> <!-- fine div ricerche -->
    	</div>
        
         <div class="Opzioni"> <a style="text-decoration:underline;" href="project.jsp">Indietro</a> </div>
            	
    </div> 
    </div>
    
  </body>
</html>