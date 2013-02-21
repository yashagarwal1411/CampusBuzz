
function sendMsg(id){
	$.post('/chat', {msg:$("#"+id+" .chatbox").val(),remote:$("#"+id+" .remote").html()});
}

//wrappers to pass to channel socket
onOpened = function() {
	  //enable the chat box
};
onMessage = function(msg){
	var index = msg.data.indexOf("$##$");
	var remoteEmail = msg.data.substring(0,index);
	var data = msg.data.substring(index+4);
	//send the msg to remote email window
	$("#chatboard").html($("#chatboard").html()+name+': '+data+'<br><br>');
};
onError = function(error){
	alert(error.description+error.code);
	//remove the annoying alert and behave peacefully ,like, disable chat box
};
onClose = function() {
	 //
};


function createChat(email){
	alert("hell");
	if(email!=null){
		var name="remote";
		jQuery.ajaxSetup({async:false});
		$.get('/chat/new',{mail:email},function(data){
			name=data;
		});
		jQuery.ajaxSetup({async:true});
		var chatHtml = '<div id="'+email+'" class="chat"><div class="chat-header"><div style="display:none" class="remoteName">'+name+'</div><div class="remote" style="float:left">'+email+'</div><div class="chat-box-btn">- + X </div></div><div class="chatboard"></div><textarea class="chatbox"></textarea></div>';
	}else
		var chatHtml =	'<div class="chat"><div class="chat-header"><input type="text" class="chat-header-text" placeholder="enter email here..."></div><div class="chatboard"></div><textarea class="chatbox"></textarea></div>';
		$("body").append(chatHtml);
	
}

$(".chatbox").keypress(function(e) {
	
    if(e.which == 13) {
    	var id = $(this).parent().attr("id");
    	
		if(typeof $(".remote").html()=="undefined"){
    		alert("please enter email of user to chat");
    		e.preventDefault();
    		return;
    	}
    	
    	$("#chatboard").html($("#chatboard").html()+'me: '+$("#chatbox").val()+'<br><br>');
    	sendMsg();
        $("#chatbox").val("");
        e.preventDefault();
    }
});


$(".chat-header-text").keypress(function(e) {
	
    if(e.which == 13) {
    	var email = $(this).val();
    	var name="remote";
    	jQuery.ajaxSetup({async:false});
    	$.get('/chat/new',{mail:email},function(data){
    		name=data;
    	});
    	jQuery.ajaxSetup({async:true});
    	var chatHtml = '<div style="display:none" class="remoteName">'+name+'</div><div class="remote" style="float:left">'+email+'</div><div class="chat-box-btn">- + X </div>';
    	$(this).parent().parent().attr('id',email);
    	$(this).parent().html(chatHtml);
    	e.preventDefault();
    	
    }
});


//create channel
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