package com.desicoders.hardcode;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class UserServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String action = Utils.getActionFromUrl(req);
		
		if (action.equalsIgnoreCase("logout")) {
			req.getSession().removeAttribute("userKey");
			resp.sendRedirect("/");
			return;
		}
		
		if (action.equalsIgnoreCase("createhardcodeadmins")) {
			Utils.createHardcodedAdmins();
			resp.sendRedirect("/");
			return;
		}
		
		if (action.equalsIgnoreCase("delete")) {
			ItemUtils.deleteAllItems(Utils.getUserFromSession(req));			
			Utils.getDataStoreService().delete(Utils.getSession(req));			
			req.getSession().removeAttribute("hardcode");
			Utils.print("success", resp);
			return;
		}
		
		if (action.equalsIgnoreCase("cleardata")) {
			ItemUtils.deleteAllItems(Utils.getUserFromSession(req));
			Entity user = Utils.getUserFromSession(req);
			user.setProperty("fname","");
			user.setProperty("lname","");
			Utils.put(user);
			Utils.print("success", resp);
			return;
		}
		
		if (action.equalsIgnoreCase("adminInvite")) {
			
			String email = req.getParameter("email");
			try {
				Utils.inviteAdmin(email);
				resp.sendRedirect("/invite-admin.jsp?msg=success");
			} catch (Exception e) {
				resp.sendRedirect("/invite-admin.jsp?msg=failure");
			}
			return;
		}
		
		if (action.equalsIgnoreCase("deactivate")) {
			//TODO : check for admin session
			try {
				Filter emailFilter = FilterOperator.EQUAL.of("Email",req.getParameter("email"));
				Query q = new Query("User").setFilter(emailFilter);
				Entity user = Utils.runQuery(q).get(0);
				user.setProperty("Deactivated","true");
				Utils.put(user);
				//TODO :mail to user
				resp.sendRedirect("/userops.jsp?msg=User Deactivated");
			} catch (Exception e) {
				resp.sendRedirect("/userops.jsp?msg=request failed,Please try again !!");
			}
			return;
		}
		
		if (action.equalsIgnoreCase("activate")) {
			//TODO : check for admin session
			try {
				Filter emailFilter = FilterOperator.EQUAL.of("Email",req.getParameter("email"));
				Query q = new Query("User").setFilter(emailFilter);
				Entity user = Utils.runQuery(q).get(0);
				user.removeProperty("Deactivated");
				Utils.put(user);
				//TODO :mail to user
				resp.sendRedirect("/userops.jsp?msg=User Activated");
			} catch (Exception e) {
				resp.sendRedirect("/userops.jsp?msg=request failed,Please try again !!");
			}
			return;
		}
		
		if (action.equalsIgnoreCase("verifysignup")) {
			String email = req.getParameter("email");
			Filter emailFilter = FilterOperator.EQUAL.of("Email",email);
			Filter codeFilter = FilterOperator.EQUAL.of("Code",req.getParameter("code"));
			CompositeFilter filter = CompositeFilterOperator.and(emailFilter,codeFilter);
			Query q = new Query("SignupVerification").setFilter(filter);
			List<Entity> invites = Utils.runQuery(q);
			if(invites.size()!=0){
				String password = (String) invites.get(0).getProperty("Password");
				Entity user = Utils.createEntity("User");
				user.setProperty("Email",email);
				user.setProperty("Password",password);
				user.setProperty("fname", "");
				user.setProperty("lname", "");
				Utils.put(user);
				Utils.deleteEntity(invites.get(0).getKey());
				resp.sendRedirect("/");
				return;
			}
			else
				Utils.print("invalid request", resp);
			return;
		}
	
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String action = Utils.getActionFromUrl(req);
		
		if (action.equalsIgnoreCase("login")) {
			// check the password and redirect to ....
			Filter email = FilterOperator.EQUAL.of("Email",req.getParameter("email"));
			Filter password = FilterOperator.EQUAL.of("Password",req.getParameter("password"));
			CompositeFilter filter = CompositeFilterOperator.and(email,password);
			Query q = new Query("User").setFilter(filter);
			List<Entity> user = Utils.runQuery(q);
			if(user.size()!=0){
				if(user.get(0).hasProperty("Deactivated"))
					Utils.print("deactivated", resp);
				else{
					req.getSession().setAttribute("userKey",user.get(0).getKey());
					Utils.print("logged in", resp);
				}
			}
			else
				Utils.print("login failed", resp);
			return;
		}

		if (action.equalsIgnoreCase("changepassword")) {
			Entity user = Utils.getUserFromSession(req);
			String passwd = (String) user.getProperty("Password");
			if(!req.getParameter("oldpassword").equals(passwd)){
				resp.sendRedirect("/profile.jsp?url=changepasswd.jsp&msg=wrongpassword");
				return;
			}
			user.setProperty("Password",req.getParameter("password"));
			Utils.put(user);
			resp.sendRedirect("/profile.jsp?url=changepasswd.jsp&msg=successPwd");
			return;
		}
		
		if (action.equalsIgnoreCase("edit")) {
			Entity user = Utils.getUserFromSession(req);
			user.setProperty("fname",req.getParameter("fname"));
			user.setProperty("lname",req.getParameter("lname"));
			Utils.put(user);
			resp.sendRedirect("/profile.jsp?url=personal.jsp&msg=success");
			return;
		}


		if (action.equalsIgnoreCase("signup")) {
			 String email = req.getParameter("email");
			 String password = req.getParameter("password"); 
			 try {
				//Utils.createSignupVerification(email, password);
				Entity user = Utils.createEntity("User");
				user.setProperty("Email",email);
				user.setProperty("Password",password);
				user.setProperty("fname", "");
				user.setProperty("lname", "");
				Utils.put(user);
				Utils.print("success", resp);
			} catch (Exception e) {
				Utils.print("failure", resp);
			}
			 return;
		}
		
		if (action.equalsIgnoreCase("adminSignup")) {
			Filter emailFilter = FilterOperator.EQUAL.of("Email",req.getParameter("email"));
			Filter code = FilterOperator.EQUAL.of("Code",req.getParameter("code"));
			CompositeFilter filter = CompositeFilterOperator.and(emailFilter,code);
			Query q = new Query("AdminInvite").setFilter(filter);
			List<Entity> invites = Utils.runQuery(q);
			if(invites.size()!=0){
				Entity user = Utils.createEntity("User");
				user.setProperty("Email",req.getParameter("email"));
				user.setProperty("Password",req.getParameter("password"));
				user.setProperty("isAdmin","true");
				Utils.put(user);
				Utils.deleteEntity(invites.get(0).getKey());
				resp.sendRedirect("/");
				return;
			}
			else
				Utils.print("invalid request", resp);
			return;
		}
		
		
	}

}
