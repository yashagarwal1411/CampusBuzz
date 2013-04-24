package com.desicoders.hardcode;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
			 String json;
			 resp.setContentType("application/json");
			 
			 if(!Utils.isAppAuthorized(req)){
				 json = 		"{\"success\": \"false\" ,"
							+		" \"message\" : \"unauthorized\","
							+		"\"conversation_id\": "+"\"null\""
							+	"}";
				 resp.getWriter().print(json);
				 return ; //false json response
			 }
			 
			 String item_id = Utils.getSafeHtml(req.getParameter("item_id"));
			 String source_user_id = Utils.getSafeHtml(req.getParameter("source_user_id"));
			 String source_user_name = Utils.getSafeHtml(req.getParameter("source_user_name"));
			 String destination_user_id = Utils.getSafeHtml(req.getParameter("destination_user_id"));
			 String subject = Utils.getSafeHtml(req.getParameter("subject"));
			 String message = Utils.getSafeHtml(req.getParameter("message"));
			 String source_conversation_id = Utils.getSafeHtml(req.getParameter("source_conversation_id"));
			
			Entity msg = Utils.getEntity(KeyFactory.stringToKey(source_conversation_id));
			if(msg==null)
				msg = Utils.createEntity("Message");
			else
				subject = subject + "\n--\n" + msg.getProperty("body");
			
			msg.setProperty("ExtApp",req.getServerName());
			Entity user = Utils.getEntity(KeyFactory.stringToKey(destination_user_id));
			if(user==null){
				json = 		"{\"success\": \"false\", "
						+		" \"message\" : \"no such user\","
						+		"\"conversation_id\": "+"\"null\""
						+	"}";
				 resp.getWriter().print(json);
				 return ; //false json response
			}
				
				
			msg.setProperty("to",(String) user.getProperty("Email"));
			msg.setProperty("from",source_user_id);
			msg.setProperty("subject","Message from "+source_user_name+" using "+req.getServerName()+" "+subject);
			msg.setProperty("body",message);
			msg.setProperty("date", new Date());
			
			Utils.put(msg);
			_log.info("message saved #"+msg.toString());
			 
			json = 		"{\"success\": \"true\" ,"
						+		" \"message\" : \"success\","
						+		"\"conversation_id\": "+"\""+ msg.getKey()+"\""
						+	"}";
						
			 resp.getWriter().print(json); //send json here
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
			 json += "{ \"success\":true, \"message\": \"success123\", \"total\": \""+searchResults.size()+"\",";
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
		

		
		if(action.equalsIgnoreCase("send_message")){
			 String json;
			 resp.setContentType("application/json");
			 
//			 if(!Utils.isAppAuthorized(req)){
//				 json = 		"{\"success\": \"false\" ,"
//							+		" \"message\" : \"unauthorized\","
//							+		"\"conversation_id\": "+"\"null\""
//							+	"}";
//				 resp.getWriter().print(json);
//				 return ; //false json response
//			 }
//			 
			 String item_id = req.getParameter("item_id");
			 String source_user_id = req.getParameter("source_user_id");
			 String source_user_name = req.getParameter("source_user_name");
			 String destination_user_id = req.getParameter("destination_user_id");
			 String subject = req.getParameter("subject");
			 String message = req.getParameter("message");
			 Text messageBody = new Text(message);
			 String source_conversation_id = req.getParameter("source_conversation_id");
			 Entity msg = null;
			 if(source_conversation_id!=null)
				 msg= Utils.getEntity(KeyFactory.stringToKey(source_conversation_id));
			
			 if(msg==null)
				msg = Utils.createEntity("Message");
			else
				subject = subject + "\n--\n" + msg.getProperty("body");
			
			msg.setProperty("ExtApp",req.getServerName());
			if(destination_user_id==null){
				json = 		"{\"success\": \"false\", "
						+		" \"message\" : \"no destination user provided\","
						+		"\"conversation_id\": "+"\"null\""
						+	"}";
				 resp.getWriter().print(json);
				 return ; //false json response
			}
			Key destinationUserKey ;
			try{
				destinationUserKey = KeyFactory.stringToKey(destination_user_id);
			}catch (Exception e) {
				json = 		"{\"success\": \"false\", "
						+		" \"message\" : \"invalid key for destination_user_id\","
						+		"\"conversation_id\": "+"\"null\""
						+	"}";
				 resp.getWriter().print(json);
				 return ; //false json response
				
			}
			Entity user = Utils.getEntity(destinationUserKey);
			if(user==null){
				json = 		"{\"success\": \"false\", "
						+		" \"message\" : \"no such user\","
						+		"\"conversation_id\": "+"\"null\""
						+	"}";
				 resp.getWriter().print(json);
				 return ; //false json response
			}
				
				
			msg.setProperty("to",(String) user.getProperty("Email"));
			msg.setProperty("from",source_user_id);
			msg.setProperty("subject",subject);
			msg.setProperty("body",messageBody);
			msg.setProperty("date", new Date());
			
			Utils.put(msg);
			_log.info("message saved #"+msg.toString());
			 
			json = 		"{\"success\": \"true\" ,"
						+		" \"message\" : \"success\","
						+		"\"conversation_id\": "+"\""+ msg.getKey()+"\""
						+	"}";
						
			 resp.getWriter().print(json); //send json here
			 return;
		}
		

		if(action.equalsIgnoreCase("item"))
		{
			String auth_token = req.getParameter("auth_token");
			Long item_id = Long.parseLong(req.getParameter("item_id"));
			Entity item = Utils.getEntityFromId(item_id, "Item");
			if(item == null)
			{
				return;
			}
			Entity owner = 	DatastoreUtils.getEntity((Key)item.getProperty("OwnerKey"));
			String json ="";
			json += "{ \"title\":\""+item.getProperty("Title")+"\","
				+	"\"description\":\""+item.getProperty("Description")+"\","
				+ 	"\"seller\" : [{" 
	            +    	" \"username\" : \" "+item.getProperty(owner.getProperty("fname")+" "+owner.getProperty("lname"))+"\","
	            +		" \"id\" : \" "+ owner.getKey().getId()+"\""
	            +       "}],"
	            + 	"\"price\" : \""+item.getProperty("Price")+"\","
                + 	"\"image\" : \""+ req.getServerName()+"/items/pic/"+item.getKey().getId()  +"\","
                + 	"\"url\" : \""+req.getServerName()+"/items/details/"+item.getKey().getId()+"\""
            + "}"; 
			resp.setContentType("application/json");
			 resp.getWriter().print(json);
			 return;
		}
		
		if(action.equalsIgnoreCase("search_suggestions"))
		{
			String auth_token = req.getParameter("auth_token");
			String query = req.getParameter("query");
			Map<String,String> suggestions = SearchUtils.searchSuggestions(query);
			String json ="";
			json += "{ \"success\":true, \"message\": \"success123\","
				+ 	" \"items\" : [ ";
			for(String title:suggestions.keySet())
				json += " {\"fullstring\" : \" "+suggestions.get(title)+"\",\"itemId\" : \" "+title+"\"} ,";
			json = json.substring(0, json.length()-1);
			json += "]}";
			resp.setContentType("application/json");
			resp.getWriter().print(json);
			return;
		}
		
		

 	}
		
}

