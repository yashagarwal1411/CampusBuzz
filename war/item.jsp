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
		Entity item= (Entity)request.getAttribute("Item");
		Entity owner = 	Utils.getEntity((Key)item.getProperty("OwnerKey"));
		String ownerId = KeyFactory.keyToString(owner.getKey());
		String ownerEmail =(String) owner.getProperty("Email");
		Long itemId = item.getKey().getId();
		%>
		<div id="wrapper">	
		<div id="main-content">
			<div id="left-column">
				<div id="title">
				<%= item.getProperty("Title") %>
				</div>
			<div class="box">
        		<h1>PRICE : <%= item.getProperty("Price") %> $</h1>  
        		<ul style="margin-top:10px;">
					<li>OWNER : <% out.print(owner.getProperty("fname")+" "+owner.getProperty("lname")); %></li>
					<li>CREATION TIME : <%= item.getProperty("CreationTime") %></li>
					<li>EXPIRATION TIME : <%= item.getProperty("ExpirationTime") %></li>
				</ul>      		
			</div>
			<h2>DESCRIPTION</h2>
			<div class="item-desc">
				<%= item.getProperty("Description") %>
			</div>
			</div>
		<div id="right-column">
			<div id="main-image"><img src="/items/pic/<%=itemId %>" onError="this.onerror=null;this.src='/css/images/noimageavailable.jpg';" width="160" height="188" /></div>
			<div class="sidebar">
					<br><br>
				<div class="box">
					<ul>
						<%if(Utils.getUserFromSession(request)!=null && !Utils.getUserFromSession(request).equals(owner)){ 
							if(Utils.isOnline(ownerId)){
						%>
							<li><button class="button" onClick="chatToSeller('<%=ownerId %>')">Chat To Seller</button></li>
						<%}else{%>
							<li><button class="button">Seller Offline</button></li>
						<%	} %>
							<li><a class="button" href="/profile.jsp?url=compose.jsp&to=<%=ownerEmail %>&title=<%= item.getProperty("Title") %>&price=<%= item.getProperty("Price") %>">Send Message</a></li>
						<%}	
						
									
						if(ItemUtils.doesCurrentUserOwnItem(item, request))
						{
							if(ItemUtils.isActive(item))
							{
								%>
								<li><input type="button" class="button" value="Deactivate item" onClick='javascript:window.top.location.href = "/items/deactivate/<%=itemId%>";' /></li>	
								<%
							}
							else
							{
								%>
								<li><input type="button" class="button" value="Activate item" onClick='javascript:window.top.location.href = "/items/activate/<%=itemId%>";' /></li>
								<%			
							}
								%>
							
								<li><input type="button" class="button" value="Edit item" onClick='javascript:window.top.location.href = "/items/edit/<%=itemId%>";' /></li>
								<li><input type="button" class="button" value="Delete item" onClick='javascript:window.top.location.href = "/items/delete/<%=itemId%>";' /></li>
								<%				
						}
						else
						{
							if(Utils.isCurrentUserAdmin(request))
							{
								%>					
								<li><input type="button" class="button" value="Delete item" onClick='javascript:window.top.location.href = "/items/delete/<%=itemId%>";' /></li>
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
		<style>
			body{
				overflow: auto;
			}
		</style>
		
	</body>
</html>

