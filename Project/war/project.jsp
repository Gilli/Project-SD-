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

	/*
		INIZIALIZZAZIONE CHIAVI E VARIABILI
	*/
	
	String Ricerca = "Libera";
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key key_util = KeyFactory.createKey("Parametri", "util_param");
	Key IndiceRicerche = KeyFactory.createKey("Indice", "Ricerche");
	
  %>


<html>
  <head>
  <link type="text/css" rel="stylesheet" href="/Stylesheets/project.css" />
  
   <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  
  <%
  
	String Avvio_Ricerca;
	
	// Controllo sulla ricerca
	if (request.getParameter("Ricerca") != null)
	{
		Avvio_Ricerca = request.getParameter("Ricerca");
		if (Avvio_Ricerca.compareTo("Attivata") == 0)
		{
			Ricerca = "Attiva";  // Indica che è stata attivata una ricerca
    	}
	}
	else
	{
		
		
	// Vedere se vi è una ricerca attiva
			Query dataquery_stato  = new Query ("util",key_util).setAncestor(key_util);
			List<Entity> resultdata_stato = datastore.prepare(dataquery_stato).asList(FetchOptions.Builder.withDefaults());
			
			if (resultdata_stato.isEmpty())
			{
				System.out.println(	"LISTA VUOTA");
			}
			else
			{	
				for (Entity post : resultdata_stato) 
				{
					Ricerca = post.getProperty("Ricerca").toString();  // prende il parametro ricerca
				}			
			}
			
	}
	
  	
	%>
   
    
    
    <!--   INCLUSIONE DELLA GOOGLE MAP   -->
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
 	<script type="text/javascript" src="/markerclusterer.js" ></script>
	<script type="text/javascript">
	var initialize = function() {
		
 
 	
 
 	 // fornisce latitudine e longitudine
  	var latlng = new google.maps.LatLng(42.745334,12.738430);
   
  	// imposta le opzioni di visualizzazione
  	var options = { zoom: 1,
                  center: latlng,
                  mapTypeId: google.maps.MapTypeId.ROADMAP
                };
				
	var map = new google.maps.Map(document.getElementById('map'), options);
				
	var mcOptions = {gridSize:50, maxZoom: 15}; // Opzioni per il clusterizzatore
	
	var markers= [];


	
    <%
		// per visualizzazione marker
	
				// Se premuto il tasto Cancella Ricerche
				if (request.getParameter("Visualizza_ricerche") != null)
				{
					// Se ci sono ricerche
					if (request.getParameterValues("elenco_ricerche") != null)
					{
						  String[] values = request.getParameterValues("elenco_ricerche");
						  
						  // CONTROLLO SUL NUMERO DI RICERCHE
						  if (values.length > 3) 
						  {
							  %>
									 window.alert("You can only select at most 3 research!");    
							  <%
						  }
						  else
						  {
							  // array di stringhe con i percorsi immagini marker
								  String[] url_marker={"/images/marker_azzurro.png","/images/marker_rosa.png","/images/marker_verde.png"};
							  
								  int i=0;
						  
							  for (String v : values)
							  {
								  
								  // PRENDO IL NOME DELL'HASHTAG
								  String [] Hash = v.split("\\|");
								  String new_v = v.replace("|"," ");
								  
								  
								  Key temp= KeyFactory.createKey("Ricerche", new_v);
													  
								  Query queryHash  = new Query(Hash[0] , temp);
								  List<Entity> resultquery2 = datastore.prepare(queryHash).asList(FetchOptions.Builder.withDefaults());
								  
													  
								  if (resultquery2.isEmpty())
								  {
									  System.out.println(	"NESSUN TWEET TROVATO");
								  }
								  else
								  {
						  
									  for (Entity has : resultquery2)  // per ogni tweet prendo la posizione se c'è
									  {
										  if (has.getProperty("latitude") != null && has.getProperty("longitude") != null)	
										  {
										  double lat = Double.parseDouble(has.getProperty("latitude").toString());
										  double lon = Double.parseDouble(has.getProperty("longitude").toString());
										  
									  
										  %>
											  var latlng2 = new google.maps.LatLng(<%=lat%>,<%=lon%>);
											  var marker= new google.maps.Marker({ position: latlng2,
												  //map: map,
												  title: '<%=Hash[0]%> since <%=Hash[1]%> until <%=Hash[2]%>',
												  icon: '<%=url_marker[i]%>'
	   
												});	
												markers.push(marker);  // aggiunge il marker all'array
										  <%
							  
										  } // fine if
															  
															  
									  }
								  }// fine else resultquery2.isEmpty()
								  i++; // indice per i marker
							  }// fine for string values
						  }// fine else ricerca max 3 values
					}
				} // fine if request parameter  
				%>
				 var markerCluster  = new MarkerClusterer(map,markers, mcOptions);   // crea il clusterizzatore
  // crea l'oggetto mappa
 
} // fine function
 
window.onload = initialize;
</script>   
    
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
        
        	<!--  PARTE SINISTRA DELLA PAGINA  -->
        	<div class="sinistra">
        		
                <div class="mappa">
               		<div id="map" style="width:600px;height:400px;border:1px solid black; position:relative;"> </div> 
                </div> <!-- Fine div mappa -->
                
                <div class="ricerche">
                
                	<form action="/project.jsp?Visualizza_ricerche=true" method="post">
               		<%	
					
						boolean empty_elenco_ricerche = false;  
						
							//interrogo il DS per l'elenco ricerche
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
 	                   <div class="riga_ricerche" style="margin-bottom:20px;"> <input type="submit" value="Visualizza_Ricerche" name="" /></div>
                    <%
                    }
					
                    %>
                    </form>
                </div> <!-- fine div ricerche -->
                
        	</div>
        
        	<div class="ins_dati">
            	<!-- Inserimento dati per query su twitter -->
       			<form action="/project" method="post">
                <div class="riga_dati">HashTag: <input type="text" size="50" name="hashtag" placeholder="#example"/></div>
                <div class="riga_dati"><div>Since Date*</div>
                Day: <input type="text" size="2" name="giorno_from" maxlength="2" placeholder="00"/> Month: <input type="text" size="2" name="mese_from" maxlength="2" placeholder="00"/> Year: <input type="text" size="4" name="anno_from" maxlength="4" placeholder="0000"/>  </div>
                <!-- hour: <input type="text" size="2" name="ore_from" maxlength="2" placeholder="hh"/>:<input type="text" size="2" name="minuti_from" maxlength="2" placeholder="mm"/></div> -->
                <div class="riga_dati"> <div>Until Date</div>
                Day: <input type="text" size="2" name="giorno_to" maxlength="2" placeholder="00"/> Month: <input type="text" size="2" name="mese_to" maxlength="2" placeholder="00"/> Year: <input type="text" size="4" name="anno_to" maxlength="4" placeholder="0000"/> </div>
                <!-- hour: <input type="text" size="2" name="ore_to" maxlength="2" placeholder="hh"/>:<input type="text" size="2" name="minuti_to" maxlength="2" placeholder="mm"/> </div> -->		
                <div class="indicazioni_ricerca"> *Vengono restituiti solamente tweet non più vecchi di una settimana</div>
                <div class="riga_dati"><input type="submit" value="Ricerca" <% if (Ricerca.compareTo("Libera") != 0 ) { %> disabled <% }  %> /></div>
                </form>
                
                
                
                <% // RIQUADRO RICERCA
				String fermata = "false";
					if (Ricerca.compareTo("Libera") != 0 )
					{
					%>
       				<div class="ricerca_attiva">
                	<div class="ricerca_attiva_riga" style="text-align:center;">RICERCA IN CORSO</div>
            		
                    <!-- Controllo sullo stato ricerca -->
                    <div class="ricerca_attiva_riga">Stato della ricerca: 
                     	<%
						if (Ricerca.compareTo("Attiva") == 0)
						{
                     	%>
                        	Query su twitter in corso...
                        <%
						}
						else if (Ricerca.compareTo("Bloccata") == 0)
						{
						%>
                        	Raggiunto limite max di 350 tweet all'ora... 
                        <%
						}
						%>
                    </div>
                    
                    <%  
						
						if  (Ricerca.compareTo("Bloccata") == 0) 
						{
							
							
							 Query dataquery_block  = new Query ("util",key_util).setAncestor(key_util);
							List<Entity> resultdata_block = datastore.prepare(dataquery_block).asList(FetchOptions.Builder.withDefaults());
							
							 Calendar calendar = new GregorianCalendar();
			
							int dif_minuti =0;  
							
							if (resultdata_block.isEmpty())
							{
								System.out.println(	"LISTA VUOTA");
							}
							else
							{	
								for (Entity post : resultdata_block) 
								{
									fermata = post.getProperty("Fermata").toString();
									int minuti_att = calendar.get(Calendar.MINUTE);
									int minuti_ric = Integer.parseInt(post.getProperty("Minuti").toString());
									dif_minuti = (65 - minuti_att +  minuti_ric);  // prende il parametro ricerca uno in più per sicurezza
									//System.out.println(dif_minuti);
										//System.out.println(	"ID TWEET:" + post.getProperty("IDtweet") + "  ID USER:"+ post.getProperty("IDuser") +
										//	"  COORD:" + post.getProperty("latitude") +"  "+  post.getProperty("longitude") + "KEY" + post.getKey() );
										
								}	
								
								// Se fermata o no
								if (fermata.compareTo("true") == 0 )
								{
									%>  <div class="ricerca_attiva_riga"> Ricerca attuale fermata! Prossima disponibile tra circa <%=dif_minuti%> minuti</div>  <%	
								}
								else // quindi non fermata
								{
									%>  <div class="ricerca_attiva_riga"> Ricerca già avviata, prossima query fra <%=dif_minuti%> minuti circa </div>  <%	
								}
							}
							%>
                            <!-- TASTO STOP SE BLOCCATA -->  
							<form action="/project" method="post">
                    		<div class="ricerca_attiva_riga"><input type="submit" value="Stop" <% if (fermata.compareTo("true") == 0 ) { %> disabled <% }  %>  /></div>
                    		<input type="hidden" name="ferma" value="1"/>   <!-- 1 ferma -->
                    		</form>
							<%	
						}  
					%>
                    
                    <!-- TASTO AGGIORNA SE RICERCA AVVIATA -->  
                    <form action="/project.jsp" method="post">
                    	<div class="ricerca_attiva_riga" style="font-size:10px; text-align:right; margin-top:50px; border-top-style:solid; border-top-width:1px;">
                        <div style="margin-top:5px; margin-bottom:5px;">Premi
                        <input type="submit"   value="Aggiorna"/> per aggiornare pagina e stato della ricerca </div>
                        </div>
                    </form>

                    <%
						
					}
				%>
                </div>
        	</div>
        
        </div>
  
    
       <%
	   
	   		// CONTROLLO SE AVVENUTO ERRORE NELL'INSERIMENTO
			if (request.getParameter("Error") != null) {
				String Error = request.getParameter("Error");
        		%>
                	<p style="font-style:italic; color:red; font-size:16px;"> ERROR: <%=Error%></p>
				<%
    		}  
  
   			
		%>
        

	<!-- LINK ALLA PAGINA OPZIONI PER CANCELLARE RICERCHE -->      
    <div class="Opzioni"> <a style="text-decoration:underline;" href="Options.jsp">Opzioni</a> </div>
    
    </div> 
    </div>
    
  </body>
</html>