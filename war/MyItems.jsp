<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@ page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="java.util.List" %>

<html>
	<head>
		
		<link rel="stylesheet" href="css/itemObject.css" type="text/css" />
		<%@include file="iframe-header.jsp"%>
		
	</head>
	<body>
			<%
			Entity user = Utils.getUserFromSession(request);
			List<Entity> items = ItemUtils.listPostedItems(user);
			 %>
			<%@include file="item-object.txt"%>
				
	</body>
</html>

