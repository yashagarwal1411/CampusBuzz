package com.desicoders.hardcode;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;


@SuppressWarnings("serial")
public class WebServicesServlet extends HttpServlet{
	public static final Logger _log = Logger.getLogger(WebServicesServlet.class.getName());
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {
	
		String action = Utils.getActionFromUrl(req);
		if(action.equalsIgnoreCase("send_message")){
			
			 String item_id = req.getParameter("item_id");
			 String source_user_id = req.getParameter("source_user_id");
			 String source_user_name = req.getParameter("source_user_name");
			 String destination_user_id = req.getParameter("destination_user_id");
			 String subject = req.getParameter("subject");
			 String message = req.getParameter("message");
			 String auth_token = req.getParameter("auth_token");
			 
			 
			 
			 resp.getWriter().print("Success");
			 return;
		}
		return;
		
	}
	
	@Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String action = Utils.getActionFromUrl(req);
		if(action.equalsIgnoreCase("search")){
			 
			 String query = req.getParameter("query");
			 String limitStr = req.getParameter("limit");
			 String offsetStr = req.getParameter("offset");
			 int limit = 250;
			 int offset = 0;
			 try
			 {
				 limit = Integer.parseInt(limitStr);
			 }
			 catch (Exception e) {
				//nothing
			 }
				 
			 try
			 {
				 offset = Integer.parseInt(offsetStr);
			 }
			 catch (Exception e) {
				//nothing
			 }
			 if(query == null)
			 {
				 return;
			 }
			 List<Entity> searchResults = SearchUtils.searchAll(query);
			 String json = ""; 
			 json += "{ \"success\":\"true\", \"message\": \"success\", \"total\": \""+searchResults.size()+"\",";
			 json += " \"sponsoredItems\" : [],";
			 json += " \"items\" : [ ";
			 if(searchResults != null)
			 {
			 for(int idx = offset;idx < searchResults.size() && idx<(offset+limit);idx++)
			 {
				 Entity item = searchResults.get(idx);
				 Entity owner = 	DatastoreUtils.getEntity((Key)item.getProperty("OwnerKey"));
				 json += 
				            "{"
				                + " \"id\": \" "+ item.getKey().getId() +"\","
				                + "\"title\" : \""+item.getProperty("Title")+"\","
				                + "\"description\" : \""+item.getProperty("Description")+"\","
				                + "\"seller\" : [{" 
				                +    	" \"username\" : \" "+item.getProperty(owner.getProperty("fname")+" "+owner.getProperty("lname"))+"\","
				                +		" \"id\" : \" "+ owner.getKey().getId()+"\""
				                +       "}],"				                
				                + "\"price\" : \""+item.getProperty("Price")+"\","
				                + "\"image\" : \""+ req.getServerName()+"/items/pic/"+item.getKey().getId()  +"\","
				                + "\"url\" : \""+req.getServerName()+"/items/details/"+item.getKey().getId()+"\""
				            + "},"; 
			 }
			 
			 json = json.substring(0, json.length()-1);
			 }
			 json += " ]} ";
			 resp.setContentType("application/json");
			 resp.getWriter().print(json);
			 return;
		}
		
 	}
		
}

