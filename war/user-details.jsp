<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@ page import="com.desicoders.hardcode.ItemUtils"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="java.util.List" %>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>

<html>
	<head>
		<link href="/css/item.css" rel="stylesheet" type="text/css" />
		<link type="text/css" rel="stylesheet" href="/css/jquery.ratings.css" />
		
	</head>
	<body>
		<%@include file="/header.jsp"%>
		<div class="container">
		
		
		
		<style>
			body{
				background-color: white;
			}
			#left-column{
				
				background-image: none;
				background: transparent;
				color:black;
			}
		
		</style>
		
		<script>
			function chatToSeller(id){
				createChat(id);
				
			}
		</script>
		
		<%
		Entity user= (Entity)request.getAttribute("User");
		//Entity owner = 	Utils.getEntity((Key)item.getProperty("OwnerKey"));
		String userKey = KeyFactory.keyToString(user.getKey());
		String userEmail =(String) user.getProperty("Email");
		userEmail = Utils.getSafeHtml(userEmail);
		Long userId = user.getKey().getId();
		%>
		<div id="wrapper">	
		<div id="main-content">
			<div id="left-column">
				<div id="title">
				<%= Utils.getSafeHtml(user.getProperty("fname").toString())+" "+Utils.getSafeHtml(user.getProperty("lname").toString()) %>
				</div>
			<div class="box">
        		<h1>Email : <%= Utils.getSafeHtml(user.getProperty("Email").toString()) %></h1>  
        		<div id="example-2"></div> <br />
    			Your Rating: <span id="example-rating-2">not set</span>        		   		
			</div>
			<h2>DESCRIPTION</h2>
			<div class="item-desc">
				<%= Utils.getSafeHtml(user.getProperty("Description").toString()) %>
			</div>
			</div>
		<div id="right-column">
			<div id="main-image"><img src="/users/pic/<%=userId %>" onError="this.onerror=null;this.src='/css/images/noimageavailable.jpg';" width="160" height="188" /></div>
			<div class="sidebar">
					<br><br>
				<div class="box">
					<ul>
						<%if(Utils.getUserFromSession(request)!=null && !Utils.getUserFromSession(request).equals(user)){ 
							if(Utils.isOnline(userKey)){
						%>
							<li><button class="button" onClick="chatToSeller('<%=userKey %>')">Chat</button></li>
						<%}else{%>
							<li><button class="button">User Offline</button></li>
						<%	} %>
							<li><a class="button" href="/profile.jsp?url=compose.jsp&to=<%=userEmail%>">Send Message</a></li>
						<%}	
						
									
						if(Utils.isCurrentUserAdmin(request))
						{
							if(!user.hasProperty("Deactivated"))
							{
								%>
								<li><input type="button" class="button" value="Deactivate user" onClick='javascript:window.top.location.href = "/users/deactivate?email=<%=userEmail%>";' /></li>	
								<%
							}
							else
							{
								%>
								<li><input type="button" class="button" value="Activate user" onClick='javascript:window.top.location.href = "/users/activate?email=<%=userEmail%>";' /></li>
								<%			
							}
						}
								%>							
								
					</ul>
				</div>
			</div>
		</div>
		</div>
		</div>
		</div>
			
			<%@include file="chat.jsp"%>
			<%
			long rating = 0;
			if(user.hasProperty("Rating"))
				rating = Math.round(Double.parseDouble(user.getProperty("Rating").toString()));
			%>	
			<script>var itemrating=<%=rating%></script>	
		<script>
		$(document).ready(function() {	
			  $('#example-2').ratings(5,itemrating).bind('ratingchanged', function(event, data) {
			    $('#example-rating-2').text(data.rating);
			    $.get("/users/addrating"+data.rating+"/<%=userId%>");
			  });
			});
		</script>
    	<script src="/js/jquery.ratings.js"></script>
		<style>
			body{
				overflow: auto;
			}
		</style>
		
	</body>
</html>

