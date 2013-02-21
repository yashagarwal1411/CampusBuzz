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
					List<Entity> items = SearchUtils.advancedSearch(request.getParameter("titleQuery"),request.getParameter("descriptionQuery"),request.getParameter("maxPriceLimit") ,request.getParameter("leastPriceLimit") );
					
					if(items!=null){
						for(Entity item:items){
							Long itemId = item.getKey().getId();
				%>
							<div class = "item">
							<div class="itemTitle" >
							<a  href='javascript:window.top.location.href = "/items/details/<%=item.getKey().getId()%>";'><%=item.getProperty("Title")%></a><br />
							</div>
							<div class="itemImage">
							<%
							String imgSrc = "/items/pic/"+itemId;
							if(item.getProperty("PicBlobKey").toString().equalsIgnoreCase("null"))
							{
								imgSrc = "/css/images/noimageavailable.jpg";
							}
							%>
							<img src="<%=imgSrc %>" onError="this.onerror=null;this.src='/css/images/noimageavailable.jpg';" width="100" height="120" />
							<br />
							</div>
							<div class="itemPrice" >
							Price : <%=item.getProperty("Price")%> <br />
							</div>
							<div class="itemDescription" >
							<span>
							<%
							String description = item.getProperty("Description").toString();
							if(description.length()>50)
								out.println(description.subSequence(0, 50)+"....");
							else
								out.println(description);
							%>
							</span>
							</div>
							</div>
							<%
						}
					
					}		
				%>
			</div>
		
	</body>
</html>