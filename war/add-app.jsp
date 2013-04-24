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
				<td><input type="text" id="token" name="token" /></td>
			</tr>
			
			</table>
			<input type="button" class="button" value="Add App" onClick="addApp()" />
		
		
		<script type="text/javascript">
			
		
			function addApp(){
			   $.get('/users/addApp',{appId:$("#url").val(),token:$("#token").val()},
					   function(data){
				   		$("#page-message").html("sucessfully added app");
			   }
			   );	
			   
			   
			}

	
		</script>
	</body>
</html>