package com.desicoders.hardcode;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class ChannelTracker extends HttpServlet {
	  
	  ChannelService channelService = ChannelServiceFactory.getChannelService();
	  public static final Logger _log = Logger.getLogger(ChannelTracker.class.getName());
	  
	  
	  @Override
	  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		  
		 String action = Utils.getActionFromUrl(req,5);
		 
		 if(action.equalsIgnoreCase("connected")){
			 ChannelPresence presence = channelService.parsePresence(req);
			 Entity status = Utils.createEntity("Status");
			 status.setProperty("UserId",presence.clientId());
			 Utils.put(status);
			 
		 }
		 
		 if(action.equalsIgnoreCase("disconnected")){
			 ChannelPresence presence = channelService.parsePresence(req);
			 Filter f = FilterOperator.EQUAL.of("UserId",presence.clientId());
			 Query q = new Query("Status").setFilter(f);
			 List<Entity> status = Utils.runQuery(q);
			 if(status.size()!=0)
				 Utils.deleteEntity(status.get(0).getKey());
		 }
		  
	  }
}