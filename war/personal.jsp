<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
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
		<%@include file="iframe-header.jsp"%>
		
	</head>
	<body>
		<% Entity user = Utils.getUserFromSession(request);
		   Long userId = user.getKey().getId();
		   String email = (String) user.getProperty("Email");
		   String fname= "";
		   String lname= "";
		   if(user.hasProperty("fname"))
		   		fname = (String) user.getProperty("fname");
		   if(user.hasProperty("fname"))
		   		lname = (String) user.getProperty("lname");
		   
		   
		%>
		
		<p id="page-message"></p>
		<form method="post"  action='<%= blobstoreService.createUploadUrl("/users/edit/"+userId) %>' enctype="multipart/form-data">
		  <div class="buzz-text">
			
				Email :
				<%=email %>
				<br /><br />
				First Name :
				<input type="text" name="fname" value="<%=fname %>"></input> 
				
				<br /><br />
				Last Name :
				<input type="text" name="lname" value="<%=lname %>"></input>
				<br /><br />
				Description :		
			
			<textarea rows="15" cols="80" style="width: 80%" name="description" ><%=user.getProperty("Description") %></textarea>
				<br /><br />
				<div style="">
				Current Image :
				<%
				String imgSrc = "/users/pic/"+userId;
				if(user.getProperty("PicBlobKey").toString().equalsIgnoreCase("null"))
				{
					imgSrc = "/css/images/noimageavailable.jpg";
				}
				%>
				<img src="<%=imgSrc %>" onError="this.onerror=null;this.src='/css/images/noimageavailable.jpg';" width="160" height="188" />
				</div>
				<br /><br />
				Image(Opt.)(Max. Size: 1MB) :
				<input type="file" name="pic" accept="image/*" />
			
		  </div>
		</form>
		<br>
		
		<input type="button" class="button" value="Save" onClick="$('form').submit()" style="margin-left:20px"/>
		<input type="button" class="button" value="Remove all Items & Data" onClick="clearData()"/>
		<input type="button" class="button" value="Delete account" onClick="deleteAccount()" />
		
		<br><br>
		<script>
			var qString = location.search;
			if(qString.indexOf("success")!=-1)
				$("#page-message").html("Saved");
			
			function deleteAccount(){
				
					if (confirm('Are you sure you want to delete this account , this can not be undone ?')) {
						$.get('/users/delete',
								  function(data){
										if(data.indexOf("success")!=-1){
											alert("account deleted");
											window.top.location.href = "/users/logout"; 
										}else{
											$(location).attr('href',"/profile.jsp?url=personal.jsp");
											$("#page-message").html("Error deleting account,please try again!!");
										}
						});
					} else {
						   
					}
				
			}
			
			function clearData(){
				if (confirm('Are you sure you want to clear all data associated with this account , this can not be undone ?')) {
					$.get('/users/cleardata',
							  function(data){
									if(data.indexOf("success")!=-1){
										$(location).attr('href',"/profile.jsp?url=personal.jsp");
										$("#page-message").html("All data removed !!"); 
									}else{
										$(location).attr('href',"/profile.jsp?url=personal.jsp");
										$("#page-message").html("error removing data,please try again !!");
									}
					});
				}else{
					
				}
				
			}
			
			
		</script>
	</body>
</html>