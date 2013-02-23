<%@page import="com.desicoders.hardcode.ItemUtils"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.desicoders.hardcode.SearchUtils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="java.util.List" %>

<html>
	<head>
		<link rel="stylesheet" href="css/itemObject.css" type="text/css" />
		<style type="text/css">
			
		</style>
		
	</head>
	<body>
		<%@include file="header.jsp"%>
		<div class= "container">
			
			
				<% 
					List<Entity> items = SearchUtils.advancedSearch(request.getParameter("searchQuery"),request.getParameter("maxPriceLimit") ,request.getParameter("leastPriceLimit") );
					if(items!=null){
						
				%>
						<%@include file="item-object.txt"%>	
				<%
					}		
				%>
			</div>
		
	</body>
</html>