<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.desicoders.hardcode.SearchUtils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="java.util.List" %>

<html>
	<head>
		
		<style type="text/css">
			
		</style>
		
	</head>
	<body>
		<%@include file="header.jsp"%>
		<div class= "container">
			
			<p id="page-message"/>
			<form method="post" action="/users/adminSignup">
		  		<table>
					<tr>
						<td>Email :</td>
						<td><input type="text" name = "email" id="email"/></td>
					</tr>
					<tr>
						<td>Password :</td>
						<td><input type="text" name="password" id="password"></input></td>
					</tr>
					<tr>
						<td>Confirm Password :</td>
						<td><input type="text" name="confirmpassword" id="confirmpassword"></input></td>
					</tr>
					<tr>
						<td><input type="text" name="code" style="visibility: hidden;" id="code"></input></td>
					</tr>
		  		</table>
		  		<input type="button" class="button" value="signup" style="margin-left: 20px" onClick="mSubmit()"/>
		  	</form>
			
		</div>
		<script type="text/javascript">
			//fill email and code from querystring
			
			function getQueryStringParam(sParam){
				var sPageURL = window.location.search.substring(1);
				var sURLVariables = sPageURL.split('&');
    			
    			for (var i = 0; i < sURLVariables.length; i++) 
    			{
        			var sParameterName = sURLVariables[i].split('=');
        			if (sParameterName[0] == sParam) 
        			{
            			return sParameterName[1];
        			}
    			}
			}
			
			function mSubmit(){
				$("#page-message").html("");
				var pwd = $("#password").val();
				var confirmPwd = $("#confirmpassword").val();
				if(pwd!=confirmPwd)
					$("#page-message").html("passwords don't match.please try again!!")
				else
					$("form").submit();
			}
			
			var email = getQueryStringParam("email");
			var code =  getQueryStringParam("code");
			
			$("#email").val(email);
			$("#code").val(code);
		</script>
	</body>
</html>