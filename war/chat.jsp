<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.desicoders.hardcode.Utils"%>
<%@page import="com.google.appengine.api.datastore.Entity" %>

		<link rel="stylesheet" href="/css/chat-style.css" type="text/css" />
		<script type="text/javascript" src="/_ah/channel/jsapi"></script>
		
		<script>

		function sendMsg(id){
			$.post('/chat', {msg:$("#"+id+" .chatbox").val(),remote:id});
		}

		//wrappers to pass to channel socket
		onOpened = function() {
			  //enable the chat box
		};
		onMessage = function(msg){
			var index = msg.data.indexOf("$##$");
			var id = msg.data.substring(0,index);
			var data = msg.data.substring(index+4);
			//send the msg to right window
			if($("#" + id).length == 0) {
				createChat(id);
			}
			$("#"+id+" .chatboard").html($("#"+id+" .chatboard").html()+$("#"+id+" .remoteName").html()+': '+data+'<br><br>');
		};
		onError = function(error){
			//alert(error.description+error.code);
			//remove the annoying alert and behave peacefully ,like, disable chat box
			
		};
		onClose = function() {
			<%if(Utils.getUserFromSession(request)!=null){
			%>
					createChannel();
			<%}%>
		};


		function createChat(id){
				
				var name="remote";
				jQuery.ajaxSetup({async:false});
				$.get('/chat/new',{client:id},function(data){
					name=data;
				});
				jQuery.ajaxSetup({async:true});
				var selector = "$('#"+id+"')";
		
				var chatHtml = '<div id="'+id+'" class="chat"><div class="chat-header"><div class="remoteName" style="float:left">'+name+'</div><div class="chat-box-btn"><a href="#" onClick="closeChat('+selector+')"> X </a></div></div><div class="chatboard"></div><textarea class="chatbox"></textarea></div>';
				$("#parent-chat-win").append(chatHtml);
				
				bindChat($("#"+id+" .chatbox"));
				
				
		}

		
		function bindChat(obj){
		
				$(obj).keypress(function(e) {
				
			    if(e.which == 13) {
			    	var id = $(this).parent().attr("id");
			    	$("#"+id+" .chatboard").html($("#"+id+" .chatboard").html()+'me: '+$("#"+id+" .chatbox").val()+'<br><br>');
			    	sendMsg(id);
			        $("#"+id+" .chatbox").val("");
			        e.preventDefault();
			        
			    }
				});
		}
		
		function closeChat(obj){
			obj.remove();
		}
		
		//create channel
		
		function createChannel(){
			$.get('/chat/create',
					function(token) {
						channel = new goog.appengine.Channel(token);
						socket = channel.open();
					    socket.onopen = onOpened;
					    socket.onmessage = onMessage;
					    socket.onerror = onError;
					    socket.onclose = onClose;
					}
			);
		}
		
		
			$('body').append('<div id="parent-chat-win"></div>');
			<%if(Utils.getUserFromSession(request)!=null){
			%>
				createChannel();
			<%}%>
			
		</script>
			
	</body>
	
</html>