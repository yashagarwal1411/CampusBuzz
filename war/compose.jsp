<html>
	<body>
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<script type="text/javascript" src="/js/tiny_mce/tiny_mce.js"></script>
		<script type="text/javascript" src="/js/tiny_mce/rtf.js"></script>
		<%@include file="iframe-header.jsp"%>
		<p id="page-message" />
		
		<form method="post" action="/message/send">
		
		<table class="buzz-text">
			<tr>
				<td>To :</td>
				<td><input type="text" name="to" id="to" placeholder="separate emails by ;"/></td>
			</tr>
			<tr>
				<td>Subject :</td>
				<td><input type="text" name="subject" id="subject" / > </td>
				
			</tr>
			
		</table>
		<p class="buzz-text">Message :</p>
		<textarea name="body" id="body"></textarea>  <br>
		<input type="submit" class="button" value ="send"/>
		
		</form>
		
		<script>
			if(getQueryStringParam("sent")=="true"){
				$("#page-message").html("Message sent !!");
			}
			
			$("#to").val(getQueryStringParam("to"));
			if(getQueryStringParam("title")!=null)
				$("#subject").val("In reference to your item '"+getQueryStringParam("title")+"' priced "+getQueryStringParam("price")+"$");
			
		</script>
	</body>
</html>