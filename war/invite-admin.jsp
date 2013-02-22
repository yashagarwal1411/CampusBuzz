<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>

<html>
	<head>
		<%@include file="iframe-header.jsp"%>
	</head>
	
	<body>
		<p id="page-message"/>
		<form method="post" action="/users/createAdmin" class="buzz-text">
			<table>
			<tr>
				<td>Email : </td>
				<td><input type="text" id="email" name="email" /></td>
			</tr>
			<tr>
				<td>Password :</td>
				<td><input type="password" id="password" name="password"/></td>
			</tr>
			<tr>
				<td>Confirm Password :</td>
				<td><input type="password" id="confirm-password" /></td>
			</tr>

			</table>
			<input type="button" class="button" value="Create Admin" onClick="createAdmin()" />
		</form>
		
		<script type="text/javascript">
			var qString = location.search;
		
			if(qString.indexOf("success")!=-1)
				$("#page-message").html("Admin Created");
			else if(qString.indexOf("failure")!=-1)
				$("#page-message").html("Admin creation failed,Please try again !!");
			
		
			function createAdmin(){
			   $("#page-message").html("");
			   if($("#password").val()!=$("#confirm-password").val()){
				   $("#page-message").html("Passwords don't match !!");
			   }else{
					$('form').submit();
				}
			}

	
		</script>
	</body>
</html>