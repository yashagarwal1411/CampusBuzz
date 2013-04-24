<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>

<html>
	<head>
		<%@include file="iframe-header.jsp"%>
	</head>
	
	<body>
		<p id="page-message"/>
		
			<table>
			<tr>
				<td>App Url : </td>
				<td><input type="text" id="url" name="url" /></td>
			</tr>
			
			<tr>
				<td>Auth Token : </td>
				<td id="token"></td>
			</tr>
			
			</table>
			<input type="button" class="button" value="Create Auth Token" onClick="createToken()" />
		
		
		<script type="text/javascript">
			
		
			function createToken(){
			   $.get('/users/authApp',{appId:$("#url").val()},
					   function(data){
				   		$("#token").html(data);
			   }
			   );	
			   
			   
			}

	
		</script>
	</body>
</html>