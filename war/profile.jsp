<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="java.util.List" %>

<html>
	<%
		if(Utils.getUserFromSession(request)==null){
			response.sendRedirect("/");
			return;
		}
	%>
	<head>
		<style type="text/css">
			#iframe{
				width : 100%;
				height : 300;
				border: hidden;
			}
			
			.category ul{
				list-style-type: none;
				padding:0;
			}
			
			.category li{
				padding-bottom: 5px;
			}
			
			
			
		</style>
		<script>
			function reloadDiv(page){
				$('#right-col').html('<p id="page-message">loading...</p>');
				$('#right-col').load(page);
				
			}
			
			
		</script>
		
	</head>
	<body>
	
	<%@include file="header.jsp"%>
		<div class= "container">
			
			<div id="left-col" class="no-underline">
			    <div class="category">
			    	<h3 style="margin-top:0;">Account Settings</h3>
			    	<ul>
					<li><a href="#" onClick="reloadDiv('/personal.jsp')">Personal Details</a></li>
					<li><a href="#" onClick="reloadDiv('/changepasswd.jsp')">Change Password</a></li>
					<li><a href="#" onClick="reloadDiv('/export.jsp')">Export Account</a></li>
					</ul>
			    </div >
			    <div class="category">
			    	<h3>Items</h3>
			    	<ul>
					<li><a href="#" onClick="reloadDiv('/MyItems.jsp')">My Items</a></li>
					<li><a href="#" onClick="reloadDiv('/AddItem.jsp')">Add Item</a></li>
					</ul>
			    </div>
			    <div class="category">
			    	<h3>Messages</h3>
			    	<ul>
			    	<li><a href="#" onClick="reloadDiv('/compose.jsp')">Compose</a></li>
					<li><a href="#" onClick="reloadDiv('/message-list.jsp?type=inbox')">Inbox</a></li>
					<li><a href="#" onClick="reloadDiv('/message-list.jsp?type=sent')">Sent</a></li>
					</ul>
			    </div>
				
			</div>
			<div id="right-col">
				
			</div>
		
			<%
				String url = request.getParameter("url");
				if(url!=null){
			%>
					<script>reloadDiv("<%=url%>");</script>
			<%   }	%>
			
			
		</div>
		<%@include file="chat.jsp"%>	
		<style>
			body{
				overflow: auto;
			}
		</style>
	</body>
</html>