<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="java.util.List" %>

<html>
	<%
		if(Utils.getUserFromSession(request)==null || !Utils.getUserFromSession(request).hasProperty("isAdmin")){
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
			function reloadIframe(page){
				$("#iframe").attr('src',page);
			}
		</script>
	</head>
	<body>
	<%@include file="header.jsp"%>
		<div class= "container">
			
			<div id="left-col" class="no-underline">
			  
			    <div class="category">
			    	<h3 style="margin-top:0;">Administration</h3>
			    	<ul>
			    	<li><a href="#" onClick="reloadIframe('userops.jsp')">User Operation</a></li>
					<li><a href="#" onClick="reloadIframe('invite-admin.jsp')">Create Admin</a></li>
					</ul>
			    </div >
				
			</div>
			<div id="right-col">
				<iframe id ="iframe">
					<p>Iframe not supported,go get a new browser</p>
				</iframe>
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