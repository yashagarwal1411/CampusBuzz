<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>

<html>
	<head>
		<%@include file="iframe-header.jsp"%>
	</head>
	
	<body>
		<p id="page-message"/>
		
			<p class="buzz-text">Email : </p>	
			<input type="text" id="email" /><br>
			<input type="button" class="button" value="Deactivate" onClick="deActivate()" />
			<input type="button" class="button" value="Activate" onClick="activate()"/>
		<!-- TODO : provide all users list with filter functionality -->
		
		<script type="text/javascript">
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
			
			function format(str){
				return str.replace(/%20/g," ");
			}
			
			function deActivate(){
				$(location).attr('href',"/users/deactivate?email="+$("#email").val());
			}
			
			function activate(){
				$(location).attr('href',"/users/activate?email="+$("#email").val());
			}
			$("#page-message").html(format(getQueryStringParam("msg")));
		</script>
	</body>
</html>