<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>

<html>
	<head>
		<%@include file="iframe-header.jsp"%>
	</head>
	
	<body>
		<p id="page-message"/>
		<form method="get" action="/users/adminInvite">
			<p class="buzz-text">Email : </p>	
			<input type="text" id="email" name="email" /><br>
			<input type="submit" class="button" value="invite" />
		</form>
		<!-- fill the outstanding reqs here and provide a delete,resend mail link with each -->
		<script type="text/javascript">
			var qString = location.search;
		
			if(qString.indexOf("success")!=-1)
			$("#page-message").html("invitation sent");
		else if(qString.indexOf("failure")!=-1)
			$("#page-message").html("invitation failed,Please try again !!");
		</script>
	</body>
</html>