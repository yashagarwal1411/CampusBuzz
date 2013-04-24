package com.desicoders.hardcode;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class MessageServlet extends HttpServlet{
	public static final Logger _log = Logger.getLogger(MessageServlet.class.getName());
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String action = Utils.getActionFromUrl(req);
		if(action.equalsIgnoreCase("send")){
			try{
				String to = Utils.getSafeHtml(req.getParameter("to"));
				String[] toEmails = to.split(";");
				String from = (String) Utils.getUserFromSession(req).getProperty("Email");
				String subject = Utils.getSafeHtml(req.getParameter("subject"));
				String body = Utils.getSafeHtml(req.getParameter("body"));
				Text msgBody = new Text(body);
				for(String s: toEmails){
					s= s.trim();
					Entity message = Utils.createEntity("Message");
					message.setProperty("to",s);
					message.setProperty("from",from);
					message.setProperty("subject",subject);
					message.setProperty("body",msgBody);
					message.setProperty("date", new Date());
					
					Utils.put(message);
					_log.info("message saved #"+message.toString());
					resp.sendRedirect("/profile.jsp?url=compose.jsp&sent=true");
				}
			}catch (Exception e) {
					resp.sendRedirect("/profile.jsp?url=compose.jsp&sent=false");
			}
			
			
			return;
		}
		
		if(action.equalsIgnoreCase("send_message_ext")){
			try{
				String appId = Utils.getSafeHtml(req.getParameter("ext_app"));
				String to = Utils.getSafeHtml(req.getParameter("to"));
				String[] toEmails = to.split(";");
				String from = (String) Utils.getUserFromSession(req).getProperty("Email");
				String fromId = Utils.getIdFromEmail(from);
				String subject = Utils.getSafeHtml(req.getParameter("subject"));
				String body = Utils.getSafeHtml(req.getParameter("body"));
				Text msgBody = new Text(body);
				for(String s: toEmails){
					s= s.trim();
					Entity message = Utils.createEntity("Message");
					message.setProperty("to",s);
					message.setProperty("from",from);
					message.setProperty("subject",subject);
					message.setProperty("body",msgBody);
					message.setProperty("date", new Date());
					message.setProperty("ExtApp", appId);
					Utils.put(message);
					
					JsonUtils.readJsonFromUrl(appId+"/webservices/send_message?destination_user_id="+to
							+"&source_user_id="+fromId+"&subject="+subject+"$body="+body+"source_conversation_id="+message.getKey());
					
					
					_log.info("message saved #"+message.toString());
					resp.sendRedirect("/profile.jsp?url=compose.jsp&sent=true");
				}
			}catch (Exception e) {
					resp.sendRedirect("/profile.jsp?url=compose.jsp&sent=false");
			}
			
			
			return;
		}
		
		
	}
}
