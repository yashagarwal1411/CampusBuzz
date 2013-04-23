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
		String safeItemID = Utils.getSafeHtml(""+itemId);
		itemId = Long.parseLong(safeItemID); 
				
		%>
		<div id="wrapper">	
		<div id="main-content">
			<div id="left-column">
				<div id="title">
				<%= Utils.getSafeHtml(item.getProperty("Title").toString()) %>
				</div>
			<div class="box">
        		<h1>PRICE : <%=Utils.getSafeHtml( ""+item.getProperty("Price")) %> $</h1>  
        		<ul style="margin-top:10px;">
					<li><a href="/users/details/<%=owner.getKey().getId()%>" style='text-decoration:none'>OWNER : <% out.print(Utils.getSafeHtml(owner.getProperty("fname").toString())+" "+Utils.getSafeHtml(owner.getProperty("lname").toString())); %></a></li>
					<li>CREATION TIME : <%= Utils.getSafeHtml(item.getProperty("CreationTime").toString()) %></li>
					<li>EXPIRATION TIME : <%= Utils.getSafeHtml(item.getProperty("ExpirationTime").toString()) %></li>
				</ul>      		
			</div>
			<h2>DESCRIPTION</h2>
			<div class="item-desc">
				<%= Utils.getSafeHtml(item.getProperty("Description").toString()) %>
			</div>
			</div>
		<div id="right-column">
			<div id="main-image"><img src="/items/pic/<%=Utils.getSafeHtml(""+itemId) %>" onError="this.onerror=null;this.src='/css/images/noimageavailable.jpg';" width="160" height="188" /></div>
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
							<li><a class="button" href="/profile.jsp?url=compose.jsp&to=<%=ownerEmail %>&title=<%= Utils.getSafeHtml(item.getProperty("Title").toString()) %>&price=<%= Utils.getSafeHtml(item.getProperty("Price").toString() )%>">Send Message</a></li>
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

