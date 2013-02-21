<html>
	<head>
	</head>
	<body>
	<%@include file="header.jsp"%>
	<script>
		function itemAddFormValidate()
		{
		  var ValidateMaxPriceLimit=$('#maxPriceLimit').val();	
		  var ValidateLeastPriceLimit=$('#leastPriceLimit').val();
		  if(ValidateMaxPriceLimit=="" || ValidateLeastPriceLimit=="")
		  {
		    alert("Price field is empty")
		    return false;
		  }
		  else if(isDigits(ValidateMaxPriceLimit)==false || isDigits(ValidateLeastPriceLimit)==false)
		  {
		   alert("Price field is not numeric")
		   return false;
		  }
		}

		function isDigits(num)
		{
			return !isNaN(num);
		}
		    
	</script>
			<div class="container"> 
			
			<form method="post" onSubmit="return itemAddFormValidate();" action="/advancedSearchResults.jsp" >
			<table >
			<tr>
				<td>Search in Title:</td>
				<td><input type="text" name="titleQuery" /></td>
			</tr>			
			<tr>
				<td>Search in Description :</td>	
				<td><input type="text" name="descriptionQuery" /></td>		
			</tr>	
			<tr>
				<td>Limit Max Price :</td>	
				<td><input type="text" id="maxPriceLimit" name="maxPriceLimit" /></td>		
			</tr>	
			<tr>
				<td>Limit Least Price :</td>	
				<td><input type="text" id="leastPriceLimit" name="leastPriceLimit" value="0"/></td>		
			</tr>	
			<tr>
				<td><input type="submit" value="Search" />
			</tr>		
			</form>
			
									
			</div>
		
		
		
	</body>
</html>

