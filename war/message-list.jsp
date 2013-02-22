<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="java.util.List" %>

<html>
	<head>
		<style>
			table{
				width:100%;
			}
			
			th{
				text-align: center;
			}
			
			th td{
				overflow :hidden;
			}
			
			.email{
				width: 30%;
			}
			
			.sub{
				width: 40%;
			}
			
			.date{
				width:30%;
			}
			
			tr{
				cursor: pointer;
			}
			
			th{
				cursor:default;
			}
		</style>
		
		<script>
			function loadMsg(key){
				$("#right-col").load("/message.jsp?id="+key);
			}
		</script>
		
	</head>
	<body>
		<%@include file="iframe-header.jsp"%>
		<table id="message-tabel">
			<tbody>
				
			</tbody>
			
		</table>
		<%
			String type = request.getParameter("type");
			String header = null;
			List<Entity> messages = null;
			if(type.equalsIgnoreCase("inbox")){
				messages= Utils.getInbox(request);
				header = "<tr><th class='email'>From</th><th class='sub'>Subject</th><th class='date'>Time</th></tr>";
			}
			if(type.equalsIgnoreCase("sent")){
				messages= Utils.getSent(request);
				header = "<tr><th class='email'>To</th><th class='sub'>Subject</th><th class='date'>Time</th></tr>";
			} 
			
			
			if(messages.size()==0){
		%>
				<script>
					var msg = "<p id='page-message'>No Messages !!</p>";
					$(msg).appendTo("#right-col");
				</script>
		<%	}else{	%>
				<script>
					var header = "<%=header%>";
					$(header).appendTo("#message-tabel tbody");
				</script>
		<% } %>
		<%
			for(Entity e : messages){
			
				String email=null;
				if(type.equalsIgnoreCase("inbox")){
					email = (String) e.getProperty("from");
					
				}else{
					email = (String) e.getProperty("to");
					
				}
				
				String key = KeyFactory.keyToString(e.getKey());
				String subject = (String) e.getProperty("subject");
				if(subject.length()>30)
					subject = subject.substring(0,30)+"...";
				
				String date = (String) e.getProperty("date");
				
				
				
		%>
			<script type="text/javascript">
				var row = "<tr onClick=\x22loadMsg('<%=key%>')\x22><td class='email'><%=email%></td><td class='sub'><%=subject%></td><td class='date'><%=date%></td></tr>";
				$(row).appendTo("#message-tabel tbody")
			</script>
		 <%	}	%>
		
		
	</body>
</html>