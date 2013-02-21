<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>

<html>
	<head>
		<%@include file="iframe-header.jsp"%>
		
	</head>
	<body>
		<% Entity user = Utils.getUserFromSession(request);
		   String email = (String) user.getProperty("Email");
		   String fname= "";
		   String lname= "";
		   if(user.hasProperty("fname"))
		   		fname = (String) user.getProperty("fname");
		   if(user.hasProperty("fname"))
		   		lname = (String) user.getProperty("lname");
		   
		   
		%>
		<p id="page-message"></p>
		<form method="post" action="/users/edit">
		  <table class="buzz-text">
			<tr>
				<td>Email :</td>
				<td> <%=email %></td>
			</tr>
			<tr>
				<td>First Name :</td>
				<td><input type="text" name="fname" value="<%=fname %>"></input></td>
				
			</tr>
			<tr>
				<td>Last Name :</td>
				<td><input type="text" name="lname" value="<%=lname %>"></input></td>
			</tr>
			<tr>
				
			</tr>
		  </table>
		</form>
		<br>
		
		<input type="button" class="button" value="Save" onClick="$('form').submit()" style="margin-left:20px"/>
		<input type="button" class="button" value="Remove all Items & Data" onClick="clearData()"/>
		<input type="button" class="button" value="Delete account" onClick="deleteAccount()" />
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