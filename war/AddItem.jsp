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
	<!-- TinyMCE -->
		<script type="text/javascript" src="/js/tiny_mce/tiny_mce.js"></script>
		<script type="text/javascript" src="/js/tiny_mce/rtf.js"></script>
		
		<%@include file="iframe-header.jsp"%>
		<style type="text/css">
		
		
		</style>
		
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
	<body >
		
		<br />
		<form method="post" onSubmit="return itemAddFormValidate();" action='<%= blobstoreService.createUploadUrl("/items/add/") %>' enctype="multipart/form-data">
		<div class="buzz-text">
			Title :
			<input type="text" name="title"  /><br /><br />
			Description :<textarea rows="15" cols="30" name="description" /></textarea><br />
			Price :<input type="text"  name="price" id="price" /> $<br /><br />
			Image(Opt.)(Max. Size: 1MB) :
			<input type="file" name="pic" accept="image/*" /><br /><br />
			<input type="submit" value="ADD ITEM" />
		</div>
		</form>
		
	</body>
</html>

