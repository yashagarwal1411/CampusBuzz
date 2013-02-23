<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Text"%>
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
				out.print("<div class='buzz-text'>From : "+from+"");
			}else if(userEmail.equalsIgnoreCase(from)){
				out.print("<div class='buzz-text'>To : "+to+"");
			}else{
				out.print("<p id='page-message'>You are not authorised to view this message !! </p>");
				return;
			}
			
			String subject = (String) msg.getProperty("subject");
			Text body = (Text) msg.getProperty("body");
			String msgBody = body.getValue();
			String date = msg.getProperty("date").toString();
		%>
		<a onClick='reply()' class="button" style="float:right">Reply</a>
		</div>
		<p class="buzz-text">Subject :</p>
		<%=subject %>
		<p class="buzz-text">Message :</p>
		<%=msgBody %>
		<p class="buzz-text">Time :</p>
		<%=date %>
		
		<script>
		function reply(){
			$('#right-col').html('<p id="page-message">loading...</p>');
			$("#right-col").load("/compose.jsp?id=<%=id%>");
		}
		</script>
	</body>
</html>