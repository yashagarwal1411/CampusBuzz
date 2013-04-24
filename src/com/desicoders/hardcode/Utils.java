package com.desicoders.hardcode;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Utils extends DatastoreUtils{

	static void print(String toPrint,HttpServletResponse resp) throws IOException
	{
		resp.getWriter().print(toPrint);
	}
	
	static void println(String toPrint,HttpServletResponse resp) throws IOException
	{
		resp.getWriter().println(toPrint);
	}
	
	static Key getSession(HttpServletRequest req){
		return (Key) req.getSession().getAttribute("userKey");
	}
	
	static String getActionFromUrl(HttpServletRequest req){
		String url = req.getRequestURL().toString();
		String[] urlPaths = url.split("/");
		if (urlPaths.length < 5) {
			return null;
		}
		String action = urlPaths[4];
		return action;
	}
	
	static String getActionFromUrl(HttpServletRequest req,int index){
		String url = req.getRequestURL().toString();
		String[] urlPaths = url.split("/");
		if (urlPaths.length < index) {
			return null;
		}
		String action = urlPaths[index];
		return action;
	}
	
	static class Email{
		String to;
	    String from;
		String subject;
		String body;
		
		void sendMail() throws AddressException, MessagingException{
			//Call the GAEJ Email Service
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			msg.addRecipient(Message.RecipientType.TO,
			new InternetAddress(to));
			msg.setSubject(subject);
			msg.setText(body);
			Transport.send(msg);
		}
		
	}
	
	static String getRandomString(){
		SecureRandom random = new SecureRandom();
		 return new BigInteger(130, random).toString(32);
	}
	
	static public boolean isCurrentUserAdmin(HttpServletRequest req)
	{
		Entity user = Utils.getUserFromSession(req);
		if(user == null)
			return false;
		if(user.hasProperty("isAdmin"))
		{
			return true;
		}
		return false;
		
	}

	static public List<Entity> getInbox(HttpServletRequest req){
		String myEmail = (String) getUserFromSession(req).getProperty("Email");
		Filter to = FilterOperator.EQUAL.of("to",myEmail);
		Query q = new Query("Message").setFilter(to);
		List<Entity> inbox = Utils.runQuery(q);
		return inbox;
	}
	
	static public List<Entity> getSent(HttpServletRequest req){
		String myEmail = (String) getUserFromSession(req).getProperty("Email");
		Filter from = FilterOperator.EQUAL.of("from",myEmail);
		Query q = new Query("Message").setFilter(from);
		List<Entity> sent = Utils.runQuery(q);
		return sent;
		
	}
	
	static public boolean isOnline(String userId){
		 Filter f = FilterOperator.EQUAL.of("UserId",userId);
		 Query q = new Query("Status").setFilter(f);
		 List<Entity> status = Utils.runQuery(q);
		 if(status.size()!=0)
			 return true;
		 else
			 return false;
	}
	
	static public String getLoginUrl(HttpServletRequest req){
		 UserService userService = UserServiceFactory.getUserService();
		 return userService.createLoginURL("/users/googlelogin");
	}
	
	/**
	 * prevent cross site forgery
	 * 
	 * @param untrustedHTML
	 * @return
	 */
	public static String getSafeHtml(String untrustedHTML){
		PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
		String safeHTML = policy.sanitize(untrustedHTML);
		return safeHTML;
	}
	
	public static boolean isAppAuthorized(HttpServletRequest req){
		String domain = req.getServerName();
		Filter appIdFilter = FilterOperator.EQUAL.of("appId",
				domain);
		Query q = new Query("AuthorizedApp").setFilter(appIdFilter);
		Entity authorizedApp;
		List<Entity> apps = Utils.runQuery(q);
		if (apps.size() != 0){
			authorizedApp = apps.get(0);
			String token = (String) authorizedApp.getProperty("token");
			if(req.getParameter("authToken").equals(token))
				return true;
		}
		else
			return false;
		return false;
	}
	
	public static String getIdFromEmail(String email){
		Filter emailFilter = FilterOperator.EQUAL.of("Email",
				email);
		Query q = new Query("User").setFilter(emailFilter);
		List<Entity> users = Utils.runQuery(q);
		if(users.size()==0)
			return null;
		String id = String.valueOf(users.get(0).getKey().getId());
		return id;
	}
	
	public static String getUserDataJson(HttpServletRequest req){
		Entity user = getUserFromSession(req);
		String json;
		json = "{"
				
				+"\"ga_id\" : \"\","
				
				+"\"name\" : \""+(String)user.getProperty("fname")+"\","
				
				+"\"bio\" : \""+(String)user.getProperty("Description")+"\","
					
				
			    
				+"\"items\":[";
				
				
		
		
				List<Entity> items = ItemUtils.listPostedItems(user);
				for(Entity item : items){
					
					json+=getJSONforItem(item, req)+",";
				}
				if(items.size()!=0)
					json = json.substring(0, json.length()-1);
				json+="]," 
				+"\"email\" : \""+(String)user.getProperty("Email")+"\","
				+"\"google_user_id\" : \"\""
				+"}" ;

		return json;
	}
	
	public static String getJSONforItem(Entity item,HttpServletRequest req)
	{
		Entity owner = DatastoreUtils.getEntity((Key)item.getProperty("OwnerKey"));
		String json ="";
		json += "{ \"title\":\""+item.getProperty("Title")+"\","
		+ "\"description\":\""+item.getProperty("Description")+"\","
		+ "\"seller\" : [{"
		+ " \"username\" : \" "+item.getProperty(owner.getProperty("fname")+" "+owner.getProperty("lname"))+"\","
		+ " \"id\" : \" "+ owner.getKey().getId()+"\""
		+ "}],"
		+ "\"price\" : \""+item.getProperty("Price")+"\","
		+ "\"image\" : \""+ req.getServerName()+"/items/pic/"+item.getKey().getId() +"\","
		+ "\"url\" : \""+req.getServerName()+"/items/details/"+item.getKey().getId()+"\""
		+ "}";
		return json;
	}

	
	public static String getAuthToken(String appId){
		Filter appFilter = FilterOperator.EQUAL.of("appId",appId);
		Query q = new Query("ExternalApp").setFilter(appFilter);
		List<Entity> apps = Utils.runQuery(q);
		if(apps.size()==0)
			return null;
		return (String)apps.get(0).getProperty("token");
	}
}
