<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>
<%@page import="com.google.appengine.api.datastore.Entity" %>

		<%@include file="iframe-header.jsp"%>
		<link rel="stylesheet" href="/css/jquery-ui.css" type="text/css" />
		<link rel="stylesheet" href="/css/searchbox.css" type="text/css" />
		<script type="text/javascript" src="/js/jquery.js"></script>
		<script type="text/javascript" src="/js/jquery-ui.js"></script>
		<script type="text/javascript" src="/js/jquery.autocomplete.js"></script>
		<title>CampusBUZZ</title>
		<style type="text/css">
			body {        
    			background-color: #eee;
				overflow: hidden;
		        font: 13px 'Lucida sans', Arial, Helvetica;
        		margin: 0px;
        		
       		}
			
			.headerLinks{
				float: right;
			}
			
			
			.no-underline a{
				text-decoration: none;
				color: rgb(216, 29, 29);
			}
			
			
			.headerLinks a{
				color:#ccc;
				font: 14px 'Lucida sans', Arial, Helvetica;
				font-weight: bold;
			}
			
			.topLogo{
				float :left;
			}
			
			.searchbox{
				width:800;
			}
			
			.container{
				width:800;
				margin:0 auto;
				
			}
			
			#left-col{
				float:left;
				width:150;
				
			}
			#right-col{
				float:right;
				width:650;
				
			}
			
			#header{
				background: #555 url("/css/images/searchBoxBackground.png");
				padding-top: 5px;
			}
			
			
		</style>
		
		<script>
			var session = 0;
			<%if(Utils.getUserFromSession(request)!=null){%>
				session = 1;
			<%}%>
		</script>
	
	<div id ="header">
	<div class="container">
		<div class = "topLogo">
			<a href="/"><img src="/css/images/logo.png" style="height:60px"/></a>
		</div>
		<div class="headerLinks no-underline">
			<%
			if(request.getSession().getAttribute("userKey")!=null){
				Entity user = Utils.getUserFromSession(request);
				if(user.hasProperty("isAdmin")){
			%>
				<a href="/admin.jsp">Admin | </a>
			<%} %>
			<a href="/profile.jsp?url=MyItems.jsp">My Account | </a>
			<%} %>
			<a href="/profile.jsp?url=MyItems.jsp" class="my-products">My products | </a>
			<a href="" id="google-login">Login via Google | </a>
			<a href="" class="login">Login | </a>
			<a href="/users/logout" id="logout">Logout</a>
			<a href="" class="signup">Signup</a>
		</div>
		
		<div id="searchBox">
			<br />
			<form action="/" method="get" class="form-wrapper cf">
				<%
					String str = ""+request.getParameter("searchQuery");
					str = Utils.getSafeHtml(str);
					if(str.equals("")) str="null";
				%>
				<input type="text" id="searchQuery" name="searchQuery" <% if(str.equals("null")) out.print("placeholder='Search here...' required"); else out.print("value='"+str+"' ");%> />			
				<button type="submit">Search</button>			
			</form>
		</div>
		<div id="dialog"></div>
	</div>
	</div>
		<script>
			$( ".login" ).click(function(){
				$( "#dialog" ).load("/login.html").dialog({autoOpen:false,title:"login"});
  				$( "#dialog" ).dialog( "open" );
  				return false;
			});
		
			$( ".signup" ).click(function() {
				$( "#dialog" ).load("/signup.html").dialog({autoOpen:false,title:"signup"});
  				$( "#dialog" ).dialog( "open" );
  				return false;
			});
			
			
			
			
			//check session
			<% 
			
			if(request.getSession().getAttribute("userKey")!=null){
			%>
				$("#google-login").hide();
				$(".login").hide();
				$(".signup").hide();
				$("#logout").show();
				$(".my-products").show();
				
			<%}else{
				String googleLogin = Utils.getLoginUrl(request);
			%>
				$("#google-login").attr("href","<%=googleLogin%>");
				$("#logout").hide();
				$(".my-products").hide();
			<%}%>
		
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
			
			var options, a;
			jQuery(function(){
			   options = { serviceUrl:'webservices/autocomplete' };
			   a = $('#searchQuery').autocomplete(options);
			});
					
		</script>
