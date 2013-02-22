package com.desicoders.hardcode;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class MessageServlet extends HttpServlet{
	public static final Logger _log = Logger.getLogger(MessageServlet.class.getName());
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String action = Utils.getActionFromUrl(req);
		if(action.equalsIgnoreCase("send")){
			String to = req.getParameter("to");
			String[] toEmails = to.split(";");
			String from = (String) Utils.getUserFromSession(req).getProperty("Email");
			String subject = req.getParameter("subject");
			String body = req.getParameter("body");
		
			for(String s: toEmails){
				s= s.trim();
				Entity message = Utils.createEntity("Message");
				message.setProperty("to",s);
				message.setProperty("from",from);
				message.setProperty("subject",subject);
				message.setProperty("body",body);
				message.setProperty("date", new Date());
				
				Utils.put(message);
				_log.info("message saved #"+message.toString());
			}
			resp.sendRedirect("/profile.jsp?url=compose.jsp&sent=true");
			return;
		}
		
		
	}
}
