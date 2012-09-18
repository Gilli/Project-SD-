<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="/stylesheets/project.css" />
    <title>Progetto</title>
  </head>

  <body class="sfondo">
  
  <%
  		
    	String Location = request.getParameter("Location");
    	if (Location == null) {
        	Location = "default";
    	}
    	pageContext.setAttribute("Location", Location);
	%>
  
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
                <div class="riga_dati">HashTags: <input type="text" size="50" name="hashtag" placeholder="#example,#example2"/></div>
                <div class="riga_dati"><div>Since Date</div>
                Day: <input type="text" size="2" name="giorno_from" maxlength="2" placeholder="00"/> Month: <input type="text" size="2" name="mese_from" maxlength="2" placeholder="00"/> Year: <input type="text" size="4" name="anno_from" maxlength="4" placeholder="0000"/> hour: <input type="text" size="2" name="ore_from" maxlength="2" placeholder="hh"/>:<input type="text" size="2" name="minuti_from" maxlength="2" placeholder="mm"/></div>
                <div class="riga_dati"> <div>Until Date</div>
                Day: <input type="text" size="2" name="giorno_to" maxlength="2" placeholder="00"/> Month: <input type="text" size="2" name="mese_to" maxlength="2" placeholder="00"/> Year: <input type="text" size="4" name="anno_to" maxlength="4" placeholder="0000"/> hour: <input type="text" size="2" name="ore_to" maxlength="2" placeholder="hh"/>:<input type="text" size="2" name="minuti_to" maxlength="2" placeholder="mm"/> </div>
                <div class="riga_dati"><input type="submit" value="Ricerca" /></div>
                </form>
        	</div>
        
        </div>
  
    
       <%
	  		String Error = request.getParameter("Error");
			if (Error != null) {
        		%>
                	<p style="font-style:italic; color:red; font-size:16px;"> ERROR: <%=Error%></p>
				<%
    		}
  
	   %>
    
    <p>Hello, ${fn:escapeXml(Location)}!</p>
    
    </div> 
    </div>
    
  </body>
</html>