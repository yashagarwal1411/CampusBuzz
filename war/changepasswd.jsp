<html>
	<head>
		<%@include file="iframe-header.jsp"%>
	</head>
	
	<body>
	<p id="page-message"></p>	
	
	 <form method="post" action="/users/changepassword">	
		<table class="buzz-text">
			<tr>
				<td>Old password :</td>
				<td><input type="password" name="oldpassword" id="oldpassword"/></td>
			</tr>
			<tr>
				<td>New Password :</td>
				<td><input type="password" name="password" id="password" /></td>
				
			</tr>
			<tr>
				<td>Confirm Password :</td>
				<td><input type="password" name="confirmpassword" id="confirmpassword" /></td>
			</tr>
			<tr>
				
			</tr>
		</table>
		
		<br>
		<input type="button" class="button" value="Change" style="margin-left:20px" onClick='changePasswd()'/>
		<input type="button" class="button" value="Cancel" />
		
	 </form>	
	 
	 <script>
			function changePasswd(){
				$("#page-message").html("");
				var passwd = $("#password").val();
				var confPasswd = $("#confirmpassword").val();
				if(passwd!=confPasswd)
					$("#page-message").html("New password does not match confirm password !!");
				else{
					$("form").submit();
				}
			}
			
			var qString = location.search;
		
			if(qString.indexOf("wrongpassword")!=-1)
				$("#page-message").html("Password incorrect !!");
			else if(qString.indexOf("successPwd")!=-1)
				$("#page-message").html("password changed !!");
			
			
	 </script>
	</body>
</html>