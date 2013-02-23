<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Text"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="java.util.List" %>

<html>
	<body>
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<script type="text/javascript" src="/js/tiny_mce/tiny_mce.js"></script>
		<script type="text/javascript" src="/js/tiny_mce/rtf.js"></script>
		<%@include file="iframe-header.jsp"%>
		
		<p id="page-message" />
		
		<form method="post" action="/message/send">
		
		<table class="buzz-text">
			<tr>
				<td>To :</td>
				<td><input type="text" name="to" id="to" placeholder="separate emails by ;" /></td>
			</tr>
			<tr>
				<td>Subject :</td>
				<td><input type="text" name="subject" id="subject"  / > </td>
				
			</tr>
			
		</table>
		<p class="buzz-text">Message :</p>
		<textarea name="body" id="body"></textarea>  <br>
		<input type="submit" class="button" value ="send"/>
		
		</form>
		
		<script>
			if(getQueryStringParam("sent")=="true"){
				$("#page-message").html("Message sent !!");
			}else if((getQueryStringParam("sent")=="false")){
				$("#page-message").html("Failed to send Message !!");
			}
			
			$("#to").val(getQueryStringParam("to"));
			if(getQueryStringParam("title")!=null)
				$("#subject").val("In reference to your item '"+getQueryStringParam("title")+"' priced "+getQueryStringParam("price")+"$");
			
			
			//fill in message form if it's a reply message
			<%
				String id = request.getParameter("id");
				if(id!=null && !id.equals("")){
					Entity user = Utils.getUserFromSession(request);
					if(user==null){
						return;
					}
				
					Entity msg = Utils.getEntity(KeyFactory.stringToKey(id));
					if(msg ==null)
						return;
					String to = (String) msg.getProperty("to");
					String from = (String) msg.getProperty("from");
					String userEmail = (String) user.getProperty("Email");
					String email=null;
					if(userEmail.equalsIgnoreCase(to)){
						email = from;
					}else if(userEmail.equalsIgnoreCase(from)){
						email = to;
					}else{
						return;
					}
					
					String subject = (String) msg.getProperty("subject");
					Text body = (Text) msg.getProperty("body");
					String msgBody = body.getValue();
					String date = msg.getProperty("date").toString();
			%>
					$("#to").val("<%=to%>");
					$("#subject").val("Re: <%=subject%>");
					$("#body").html("<br>--<br><%=date%><br><%=msgBody%>");
					
			<% } %>
		</script>
	</body>
</html>