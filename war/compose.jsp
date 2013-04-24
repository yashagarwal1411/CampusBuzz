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
		
		<%
				String id = request.getParameter("id");
			
				String email="";
				String msgBody="";
				String subject="";
				String extApp = "";
				if(id!=null && !id.equals("")){
					Entity user = Utils.getUserFromSession(request);
					if(user==null){
						return;
					}
				
					Entity msg = Utils.getEntity(KeyFactory.stringToKey(id));
					if(msg ==null)
						return;
					
					if(msg.hasProperty("ExtApp")){
						extApp = (String) msg.getProperty("ExtApp");
						
					}
					String to = (String) msg.getProperty("to");
					to = Utils.getSafeHtml(to);
					String from = (String) msg.getProperty("from");
					from = Utils.getSafeHtml(from);
					String userEmail = (String) user.getProperty("Email");
					userEmail = Utils.getSafeHtml(userEmail);
					
					if(userEmail.equalsIgnoreCase(to)){
						email = from;
					}else if(userEmail.equalsIgnoreCase(from)){
						email = to;
					}else{
						return;
					}
					
					subject = (String) msg.getProperty("subject");
					subject = Utils.getSafeHtml(subject);
					Text body = (Text) msg.getProperty("body");
					msgBody = body.getValue();
					msgBody = Utils.getSafeHtml(msgBody);
					String date = msg.getProperty("date").toString();
					date = Utils.getSafeHtml(date);
					msgBody = "--<br>"+date +"<br>"+msgBody;
					
				}
				
				
	
			%>
				
			
			
		<p id="page-message" />
		
		<form method="post" action="/message/send" i="form">
		
		<table class="buzz-text">
			<tr>
				<td>To :</td>
				<td><input type="text" name="to" id="to" placeholder="separate emails by ;"  value="<%=email%>" /></td>
			</tr>
			<tr>
				<td>Subject :</td>
				<td><input type="text" name="subject" id="subject" value="<%=subject%>" / > </td>
				
			</tr>
			
		</table>
		<p class="buzz-text">Message :</p>
		<textarea name="body" id="body"><%=msgBody%></textarea>  <br>
		<input type="submit" class="button" value ="send"/>
		
		</form>
		
		<script>
			if(getQueryStringParam("sent")=="true"){
				$("#page-message").html("Message sent !!");
			}else if((getQueryStringParam("sent")=="false")){
				$("#page-message").html("Failed to send Message !!");
			}
			
			if(getQueryStringParam("to")!=null)
				$("#to").val(getQueryStringParam("to"));
			
			if(getQueryStringParam("title")!=null)
				$("#subject").val("In reference to your item '"+getQueryStringParam("title")+"' priced "+getQueryStringParam("price")+"$");
			
			var extApp = "<%=extApp%>";
			if(extApp!=""){
				//change the form details
				$("#form").attr("action","/message/send_message_ext?ext_app="+extpp);
			}
		</script>
	</body>
</html>