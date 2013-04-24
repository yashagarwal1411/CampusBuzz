<%@page import="com.desicoders.hardcode.JsonUtils"%>
<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.desicoders.hardcode.SearchUtils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="java.util.List" %>
<%@page import="org.json.*" %>

<html>
	<head>
		<link rel="stylesheet" href="css/itemObject.css" type="text/css" />
		<style type="text/css">
			
		</style>
		
		<script>
			function createAdimns(){
				<%
					
				%>
			}
		</script>
	</head>
	<body>
	<%@include file="header.jsp"%>
	
		<div class= "container">
			<div>
			<a href="/advancedSearch.jsp" >Advanced Search</a>
			</div>
				<%
					//setup admin users on empty datastore
					if(!Utils.ifHardcodeAdminsExists()){
				%>
					<div style="text-align:center">
						<a href="/users/createhardcodeadmins" class="button">Create Hardcode Admins</a>
						<p >
							The following admins will be created
							<br><br>
								hardcodetest1@gmail.com  : hardcode<br><br>
								hardcodetest2@gmail.com  : hardcode
							
						</p>
					</div>
				<%		
					}
				%>
			
				<% 
					List<Entity> items = null;
					if(!str.equals("null")){
						out.print("<p id='page-message'>Search Results :</p>");
						items = SearchUtils.searchAll(str);
					}
					else
					{
						
						items = ItemUtils.listNewItems(request);
						if(items.size()!=0)
						{
							out.print("<p id='page-message'>Recently Added Items :</p>");
						}
					}
					%>
					<%@include file="item-object.txt"%>
					
				<%
				    if(request.getParameter("searchQuery") != null)
				    {
					String[] compatibleApps = {"https://astudyhall.appspot.com/"};
					for(int i=0;i<compatibleApps.length;i++)
					{
						
					    String domainName = compatibleApps[i];
						compatibleApps[i] = compatibleApps[i] + "webservices/search?query=" + request.getParameter("searchQuery");
						compatibleApps[i] += "&limit=5&offset=0&auth_token=";
						JSONObject json = JsonUtils.readJsonFromUrl(compatibleApps[i]);
						
						%>
						<div style="clear: both;">
						<hr>
						Results from <a href="<%=domainName%>"><%=domainName%></a>
						<br><br>
						<%@include file="item-object-foreign.txt"%> 						
						</div>
						<%						
					}
				    }
				%>
					
					
			</div>
			
			<%@include file="chat.jsp"%>	
		<style>
			body{
				overflow: auto;
			}
		</style>
	</body>
</html>