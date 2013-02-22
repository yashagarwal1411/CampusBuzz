<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="java.util.List" %>

<html>
	<body>
		<%@include file="iframe-header.jsp"%>
		<%
			Entity user = Utils.getUserFromSession(request);
			if(user==null){
				out.print("<p id='page-message'>You must be logged in to view messages !! </p>");
				return;
			}
			String id = request.getParameter("id");
			Entity msg = Utils.getEntity(KeyFactory.stringToKey(id));
			String to = (String) msg.getProperty("to");
			String from = (String) msg.getProperty("from");
			String userEmail = (String) user.getProperty("Email");
			if(userEmail.equalsIgnoreCase(to)){
				out.print("<p class='buzz-text'>From : "+from+"</p>");
			}else if(userEmail.equalsIgnoreCase(from)){
				out.print("<p class='buzz-text'>To : "+to+"</p>");
			}else{
				out.print("<p id='page-message'>You are not authorised to view this message !! </p>");
				return;
			}
			String subject = (String) msg.getProperty("subject");
			String body = (String) msg.getProperty("body");
			String date = (String) msg.getProperty("date");
		%>
		
		<p class="buzz-text">Subject :</p>
		<%=subject %>
		<p class="buzz-text">Message :</p>
		<%=body %>
		<p class="buzz-text">Time :</p>
		<%=date %>
	</body>
</html>