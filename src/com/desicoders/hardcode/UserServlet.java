package com.desicoders.hardcode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class UserServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	public static final Logger _log = Logger.getLogger(UserServlet.class.getName());
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
		
		if (action.equalsIgnoreCase("details"))
		{
			Long userId = Long.parseLong(Utils.getActionFromUrl(req, 5));
			Entity user = DatastoreUtils.getEntityFromId(userId,"User");
			req.setAttribute("User", user);
			RequestDispatcher rd = req.getRequestDispatcher("/user-details.jsp");
			try {
				rd.forward(req, resp);
			} catch (ServletException e) {
				e.printStackTrace();
			}
			return;			
		}
		
		if(action.equalsIgnoreCase("pic"))
		{
			Long userId = Long.parseLong(Utils.getActionFromUrl(req, 5));
			Entity user = DatastoreUtils.getEntityFromId(userId,"User");
			String bk = user.getProperty("PicBlobKey").toString();
			if(bk.equals("null"))
			{
				
			}
			else
			{
				BlobKey blobKey = new BlobKey(bk);
				blobstoreService.serve(blobKey, resp);
			}
			return;
		}

		if (action.equalsIgnoreCase("delete")) {
			Entity user = Utils.getUserFromSession(req);
			if(user == null){
				resp.setContentType("text/html");
				Utils.print("<html><head> <meta http-equiv='refresh' content='3;url=/' />",resp);
				Utils.print(" You must be logged in!!!  </head></html>",resp);
				return;
			}
			ItemUtils.deleteAllItems(user);
			user.setProperty("Description", "");
			if(!user.getProperty("PicBlobKey").toString().equals("null"))
		    {				
		    	BlobKey blobKey = new BlobKey(user.getProperty("PicBlobKey").toString());
		    	blobstoreService.delete(blobKey);
		    }
			Utils.getDataStoreService().delete(Utils.getSession(req));
			_log.info("User Deleted "+user.toString());
			req.getSession().removeAttribute("hardcode");
			Utils.print("success", resp);
			return;
		}

		if (action.equalsIgnoreCase("cleardata")) {
			Entity user = Utils.getUserFromSession(req);
			if(user == null){
				resp.setContentType("text/html");
				Utils.print("<html><head> <meta http-equiv='refresh' content='3;url=/' />",resp);
				Utils.print(" You must be logged in!!!  </head></html>",resp);
				return;
			}
			ItemUtils.deleteAllItems(user);
			
			user.setProperty("fname", "");
			user.setProperty("lname", "");
			user.setProperty("Description", "");
			if(!user.getProperty("PicBlobKey").toString().equals("null"))
		    {				
		    	BlobKey blobKey = new BlobKey(user.getProperty("PicBlobKey").toString());
		    	blobstoreService.delete(blobKey);
		    }
			Utils.put(user);
			_log.info("Data Clear for user "+user.toString());
			Utils.print("success", resp);
			return;
		}


		if (action.equalsIgnoreCase("deactivate")) {
			Entity admin = Utils.getUserFromSession(req);
			if(admin == null){
				resp.setContentType("text/html");
				Utils.print("<html><head> <meta http-equiv='refresh' content='3;url=/' />",resp);
				Utils.print(" You must be logged in!!!  </head></html>",resp);
				return;
			}
			if(admin.hasProperty("isAdmin")){
				try {
					Filter emailFilter = FilterOperator.EQUAL.of("Email",
							req.getParameter("email"));
					Query q = new Query("User").setFilter(emailFilter);
					Entity user = Utils.runQuery(q).get(0);
					user.setProperty("Deactivated", "true");
					Utils.put(user);
					_log.info("User Deactivated "+user.toString());
					resp.sendRedirect("/userops.jsp?msg=User Deactivated");
				} catch (Exception e) {
					resp.sendRedirect("/userops.jsp?msg=request failed,Please try again !!");
				}
			}
			return;
		}

		if (action.equalsIgnoreCase("activate")) {
			Entity admin = Utils.getUserFromSession(req);
			if(admin == null){
				resp.setContentType("text/html");
				Utils.print("<html><head> <meta http-equiv='refresh' content='3;url=/' />",resp);
				Utils.print(" You must be logged in!!!  </head></html>",resp);
				return;
			}
			if(admin.hasProperty("isAdmin")){
				try {
					Filter emailFilter = FilterOperator.EQUAL.of("Email",
							req.getParameter("email"));
					Query q = new Query("User").setFilter(emailFilter);
					Entity user = Utils.runQuery(q).get(0);
					user.removeProperty("Deactivated");
					Utils.put(user);
					_log.info("User Activated "+user.toString());
					resp.sendRedirect("/profile.jsp?msg=User Activated");
				} catch (Exception e) {
					resp.sendRedirect("/profile.jsp?msg=request failed,Please try again !!");
				}
			}
			return;
		}

		
		if (action.equalsIgnoreCase("googlelogin")) {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			if (user != null) {
				String email = user.getEmail();
				Filter f = FilterOperator.EQUAL.of("Email", email);
				Query q = new Query("User").setFilter(f);
				List<Entity> users = Utils.runQuery(q);
				Entity mUser = null;
				if (users.size() > 0) {
					mUser = users.get(0);
					if(mUser.hasProperty("deactivated"))
					{
						resp.setContentType("text/html");
						Utils.print("<html><head> <meta http-equiv='refresh' content='3;url=/' />",resp);
						Utils.print(" Sorry you are deactivated. Please contact admin !!!  </head></html>",resp);
					}
					else
					{
					req.getSession().setAttribute("userKey", mUser.getKey());
					resp.sendRedirect("/profile.jsp?url=MyItems.jsp");
					}
				} else {
					mUser = Utils.createEntity("User");
					mUser.setProperty("Email", email);
					mUser.setProperty("Password",Utils.getRandomString());
					mUser.setProperty("fname", user.getNickname());
					mUser.setProperty("lname", "");
					mUser.setProperty("PicBlobKey", "null");
					mUser.setProperty("Description", "");
					Utils.put(mUser);
					_log.info("SignUp via google account using user service "+user.toString());
					req.getSession().setAttribute("userKey", mUser.getKey());
					resp.sendRedirect("/");					
				}

			} else {
				resp.sendRedirect("/");
			}
			return;
		}
		
		if (action.equalsIgnoreCase("authApp")) {
			Entity admin = Utils.getUserFromSession(req);
			if(admin.hasProperty("isAdmin")){
				String appId = req.getParameter("appId");
				try{
				String token=Utils.getRandomString();
				Filter appIdFilter = FilterOperator.EQUAL.of("appId",
						appId);
				Query q = new Query("AuthorizedApp").setFilter(appIdFilter);
				Entity authorizedApp;
				List<Entity> apps = Utils.runQuery(q);
				if (apps.size() != 0)
					authorizedApp = apps.get(0);
				else
					authorizedApp = Utils.createEntity("AuthorizedApp");
				
				authorizedApp.setProperty("id",appId);
				authorizedApp.setProperty("token",token);
				Utils.put(authorizedApp);
				resp.getWriter().print(token);
				
				} catch (Exception e) {
					resp.getWriter().print("Please try again !!");
				}
			}
			return;
		}

		
		if (action.equalsIgnoreCase("addApp")) {
			Entity admin = Utils.getUserFromSession(req);
			if(admin.hasProperty("isAdmin")){
				String appId = req.getParameter("appId");
				String token = req.getParameter("token");
				Filter appIdFilter = FilterOperator.EQUAL.of("appId",
						appId);
				Query q = new Query("AuthorizedApp").setFilter(appIdFilter);
				Entity externalApp;
				List<Entity> apps = Utils.runQuery(q);
				if (apps.size() != 0)
					externalApp = apps.get(0);
				else
					externalApp = Utils.createEntity("AuthorizedApp");
				externalApp = Utils.createEntity("ExternalApp");
				externalApp.setProperty("appId",appId);
				externalApp.setProperty("token",token);
				Utils.put(externalApp);
				
			}
			return;
		}
		
		if (action.equalsIgnoreCase("export")) {
			// JSON content to post
			String userJson = Utils.getUserDataJson(req);
			String appId= req.getParameter("appId");
			String authToken = Utils.getAuthToken(appId);
			JSONObject exportResult;
			try {
				exportResult = JsonUtils.readJsonFromUrl(appId+"?auth_token="+authToken+"user_data="+userJson);
				resp.getWriter().print(exportResult.toString());
			} catch (JSONException e) {
				resp.getWriter().print("failed");
			}
			
		}
	}
	

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String action = Utils.getActionFromUrl(req);

		if (action.equalsIgnoreCase("login")) {
			// check the password and redirect to ....
			Filter email = FilterOperator.EQUAL.of("Email",
					req.getParameter("email"));
			Filter password = FilterOperator.EQUAL.of("Password",
					req.getParameter("password"));
			CompositeFilter filter = CompositeFilterOperator.and(email,
					password);
			Query q = new Query("User").setFilter(filter);
			List<Entity> user = Utils.runQuery(q);
			if (user.size() != 0) {
				if (user.get(0).hasProperty("Deactivated"))
					Utils.print("deactivated", resp);
				else {
					req.getSession().setAttribute("userKey",
							user.get(0).getKey());
					Utils.print("logged in", resp);
				}
			} else
				Utils.print("login failed", resp);
			return;
		}

		if (action.equalsIgnoreCase("changepassword")) {
			Entity user = Utils.getUserFromSession(req);
			String passwd = (String) user.getProperty("Password");
			if (!req.getParameter("oldpassword").equals(passwd)) {
				resp.sendRedirect("/profile.jsp?url=changepasswd.jsp&msg=wrongpassword");
				return;
			}
			user.setProperty("Password", req.getParameter("password"));
			Utils.put(user);
			_log.info("Password changed for user  "+user.toString());
			resp.sendRedirect("/profile.jsp?url=changepasswd.jsp&msg=successPwd");
			return;
		}

		if (action.equalsIgnoreCase("edit")) {
			Entity user = Utils.getUserFromSession(req);
			user.setProperty("fname", Utils.getSafeHtml(req.getParameter("fname").toString()));
			user.setProperty("lname", Utils.getSafeHtml(req.getParameter("lname").toString()));
			user.setProperty("Description", Utils.getSafeHtml(req.getParameter("description").toString()));
			BlobKey blobKey = null;
			@SuppressWarnings("deprecation")
			Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	        blobKey = blobs.get("pic");		
	        final BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
	        long size = blobInfo.getSize();
	        if(size <= 0 || size>(1024*1024))
	        {
	        	blobstoreService.delete(blobKey);
	        	blobKey = null;
	        }
	        if (blobKey != null)
			 {
				    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				    if(!user.getProperty("PicBlobKey").toString().equals("null"))
				    {
				    	BlobKey blobKey2 = new BlobKey(user.getProperty("PicBlobKey").toString());
				    	blobstoreService.delete(blobKey2);
				    }
				 	user.setProperty("PicBlobKey", blobKey.getKeyString());
		     }
			Utils.put(user);
			_log.info("User Edited "+user.toString());
			resp.sendRedirect("/profile.jsp?url=personal.jsp&msg=success");
			return;
		}

		if (action.equalsIgnoreCase("signup")) {
			String email = Utils.getSafeHtml(req.getParameter("email").toString());
			String password = req.getParameter("password");
			try {
				Entity user = Utils.createEntity("User");
				user.setProperty("Email", email);
				user.setProperty("Password", password);
				user.setProperty("fname", "");
				user.setProperty("lname", "");
				user.setProperty("PicBlobKey", "null");
				user.setProperty("Description", "");
				Utils.put(user);
				_log.info("SignUp "+user.toString());
				Utils.print("success", resp);
			} catch (Exception e) {
				Utils.print("failure", resp);
			}
			return;
		}

		
		if (action.equalsIgnoreCase("createAdmin")) {
			Entity admin = Utils.getUserFromSession(req);
			if(admin.hasProperty("isAdmin")){
				String email = req.getParameter("email");
				String password = req.getParameter("password");
				try {
					Entity user = Utils.createEntity("User");
					user.setProperty("Email", email);
					user.setProperty("Password", password);
					user.setProperty("fname", "");
					user.setProperty("lname", "");
					user.setProperty("PicBlobKey", "null");
					user.setProperty("Description", "");
					Utils.put(user);
					_log.info("Admin created "+user.toString());
					Utils.print("success", resp);
					resp.sendRedirect("/invite-admin.jsp?msg=success");
				} catch (Exception e) {
					resp.sendRedirect("/invite-admin.jsp?msg=failure");
				}
			}
			return;
		}
		
		
		

	}

}
