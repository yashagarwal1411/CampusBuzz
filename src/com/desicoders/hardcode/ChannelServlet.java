package com.desicoders.hardcode;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ChannelServlet extends HttpServlet {
	  
	  ChannelService channelService = ChannelServiceFactory.getChannelService();
	
	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String action = Utils.getActionFromUrl(req);
		if(action.equalsIgnoreCase("create")){
			 String userId = KeyFactory.keyToString(Utils.getUserFromSession(req).getKey());;
			 String token = channelService.createChannel(userId);
			 Utils.print(token,resp);
			 return;
		}
		
		if(action.equalsIgnoreCase("new")){
			String id = req.getParameter("client");
			Utils.print(Utils.getUserName(id), resp);
			return;
		}
	   
	  }
	  
	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		  String msg = req.getParameter("msg");
		  String remote = req.getParameter("remote");
		 
		  String userId = KeyFactory.keyToString(Utils.getUserFromSession(req).getKey());
		  channelService.sendMessage(new ChannelMessage(remote,userId+"$##$"+msg));
		  
	  }
}