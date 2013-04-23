<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@ page import="com.desicoders.hardcode.ItemUtils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="java.util.List" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<script type="text/javascript" src="/js/tiny_mce/tiny_mce.js"></script>
		<script type="text/javascript" src="/js/tiny_mce/rtf.js"></script>		
		<script>
		function itemAddFormValidate()
		{
		  var dValidate=$('#price').val();
		  if(dValidate=="")
		  {
		    alert("Price field is empty")
		    return false;
		  }
		  else if(isDigits(dValidate)==false)
		  {
		   alert("Price field is not numeric")
		   return false;
		  }
		}

		function isDigits(num)
		{
			return !isNaN(num);
		}
		    
		</script>
	</head>
	<body>
	<%@include file="header.jsp"%>
	<div class="container">
		
		<%
		Entity item= (Entity)request.getAttribute("Item");
		Long itemId = item.getKey().getId();
		
		if(ItemUtils.doesCurrentUserOwnItem(item, request))
		{
			%>
			<form method="post" onSubmit="return itemAddFormValidate();" action='<%= blobstoreService.createUploadUrl("/items/editsubmit/"+itemId) %>' enctype="multipart/form-data">
			<table >
			<tr>
				<td>Item Id:</td>
				<td><input type="text" disabled="disabled" name="title" value="<%=itemId%>" /></td>
			</tr>
			<tr>
				<td>Title :</td>
				<td><input type="text" name="title" value="<%=Utils.getSafeHtml(item.getProperty("Title").toString()) %>" /></td>
			</tr>
			<tr>
				<td>Description :</td>			
			</tr>
			<tr>
			<td></td>
			<td><textarea rows="15" cols="80" style="width: 80%" name="description" ><%=Utils.getSafeHtml(item.getProperty("Description").toString()) %></textarea></td>
			</tr>
			<tr>
				<td>Price :</td>
				<td><input id="price" type="text"  name="price"  value="<%=Utils.getSafeHtml(""+item.getProperty("Price"))%>"/> $</td> 
			</tr>
			<tr>	
				<td>Active :</td>
				<td><input type="checkbox" name="active" value="true" <%if(ItemUtils.isActive(item)) out.print("checked='checked'"); %>/></td>
			</tr>
			<tr>	
				<td>Current Image :</td>
				<%
				String imgSrc = "/items/pic/"+itemId;
				if(item.getProperty("PicBlobKey").toString().equalsIgnoreCase("null"))
				{
					imgSrc = "/css/images/noimageavailable.jpg";
				}
				%>
				<td><img src="<%=imgSrc %>" onError="this.onerror=null;this.src='/css/images/noimageavailable.jpg';" width="160" height="188" /></td>
			</tr>
			<tr>	
				<td>Image(Opt.)(Max. Size: 1MB) :</td>
				<td><input type="file" name="pic" accept="image/*" /></td>
			</tr>
			<tr>	
				<td><input type="submit" value="EDIT ITEM" class="button"/></td>
			</tr>
			</table>
			</form>
			<%
		}
		else
		{	
			response.setHeader("Refresh", "5;url=/");
			%>
			<p>You are not authorised to edit this item</p>
			<%			
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

