<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="java.util.List" %>
<%@page import="com.google.appengine.api.datastore.Entity" %>

<html>
	<head>
		<%@include file="iframe-header.jsp"%>
	</head>
	
	<body>
	<p id="page-message"></p>	
	
	 <form method="post" action="/users/export">	
		Please select a site where you want to export your  account :<br><br>
		Please note this will delete your account from this site
		
		<br><br>
		<select id="apps">
			<%
				List<Entity> apps = Utils.getEntity("ExternalApp");
			    String opts="";
			    for(Entity app : apps){
			    	String id= (String) app.getProperty("appId");
			    	opts += "<option value='"+id+"'>"+id+"</option>";
			     }
			%>
			<%=opts %>
		</select><br><br>
		<input type="button" class="button" value="Export" style="margin-left:20px" onClick='exportMe()'/>
		
		
	 </form>	
	 
	 <script>
			function exportMe(){
				
				$("form").attr("action","/users/export?appId="+$('#apps').val());
				$("form").submit();
			}
			
	 </script>
	</body>
</html>