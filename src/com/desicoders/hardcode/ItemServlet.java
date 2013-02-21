package com.desicoders.hardcode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class ItemServlet extends HttpServlet{
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	public static final Logger _log = Logger.getLogger(ItemServlet.class.getName());
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		
		PrintWriter out = resp.getWriter();
		Entity currentUser = Utils.getUserFromSession(req);
		_log.info("==============="+currentUser.toString());
		String url = req.getRequestURL().toString();
		String[] urlPaths = url.split("/");
		if (urlPaths.length < 5) {
			out.print("<html><head> <meta http-equiv='refresh' content='3;url=/' /> No such page exists!!! </head></html>");
			return;
		}
		String action = urlPaths[4];

		String title ="",description="";
		float price = 0;
		
		if (action.equalsIgnoreCase("add")) {
			
			if(currentUser == null)
			{
				resp.setContentType("text/html");
				out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/profile.jsp' /> ");
				out.print(" You must be logged in to add Items... / Please try again... </head></html>");
				return;
			}
			BlobKey blobKey = null;
			Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	        blobKey = blobs.get("pic");
	        final BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
	        long size = blobInfo.getSize();
	        if(size <= 0 || size>(1024*1024))
	        {
	        	blobstoreService.delete(blobKey);
	        	blobKey = null;
	        }
						
	        title = req.getParameter("title");
			description = req.getParameter("description");
			try
			{
				price = Float.parseFloat(req.getParameter("price"));
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			ItemUtils.addItem(title, description, price,blobKey, currentUser);
			out.print("<html><head> <meta http-equiv='refresh' content='3;url=/profile.jsp?url=MyItems.jsp' /> Item added!!! </head></html>");
			//resp.sendRedirect("/profile.jsp"); //modify this later to app. link
			return;
		}		

		
		
		if(action.equalsIgnoreCase("editsubmit"))
		{
						
			title = req.getParameter("title");
			description = req.getParameter("description");
			try
			{
				price = Float.parseFloat(req.getParameter("price"));
			}
			catch (Exception e) {
				// TODO: handle exception
			}		
			boolean Active = req.getParameterMap().containsKey("active");
			
			int isActive = Active?1:0;
			Long itemId = Long.parseLong(urlPaths[5]);
			Entity item = DatastoreUtils.getEntityFromId(itemId,"Item");
			if(item == null)
			{
				resp.setContentType("text/html");
				out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/profile.jsp?url=MyItem.jsp' /> ");
				out.print(" No such item exists / Please try again  </head></html>");
				return;
			}
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
			boolean result = ItemUtils.editItem(title, description, price, isActive,blobKey, item,currentUser);
			if(result)
				resp.sendRedirect("/items/details/"+itemId);
			else
			{
				resp.setContentType("text/html");
				out.print("<html><head> <meta http-equiv='refresh' content='3;url=/' />");
				out.print(" Item not edited.\n Please check if you are authorized to edit this item then try again  </head></html>");
			}
				
			return;
		}
		
		out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/' /> ");
		out.print(" Sorry!! No such page exists... </head></html>");
		
		
	
		
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		PrintWriter out = resp.getWriter();
		Entity currentUser = Utils.getUserFromSession(req);
		
		String url = req.getRequestURL().toString();
		String[] urlPaths = url.split("/");
		if (urlPaths.length < 6) {
			out.print("<html><head> <meta http-equiv='refresh' content='3;url=/' /> No such page exists!!! </head></html>");
			return;
		}
		String action = urlPaths[4];

		
		
		Long itemId = Long.parseLong(urlPaths[5]);
		Entity item = DatastoreUtils.getEntityFromId(itemId,"Item");
		if(item == null)
		{
			resp.setContentType("text/html");
			out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/' /> ");
			out.print(" No such item exists / Please try again  </head></html>");
			return;
		}
		
		if (action.equalsIgnoreCase("delete")) {
			boolean result = ItemUtils.deleteItem(item, currentUser);
			resp.setContentType("text/html");
			out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/profile.jsp?url=MyItem.jsp' /> ");
			if(result)
				out.print(" Item successfully deleted </head></html>");
			else
				out.print(" Item not deleted.\n Please check if you are authorized to delete this item then try again </head></html>");
			return;
		}
		
		if (action.equalsIgnoreCase("details"))
		{
			boolean detailsVisible = ItemUtils.areDetailsVisible(item, currentUser);
			
			if(detailsVisible)
			{
				req.setAttribute("Item", item);
				RequestDispatcher rd = req.getRequestDispatcher("/item.jsp");
				try {
					rd.forward(req, resp);
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			else
			{				
				resp.setContentType("text/html");
				out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/' /> ");
				out.print(" The item has expired or inactive !! You can not view it.  </head></html>");
				return;
			}
		}
		
		if (action.equalsIgnoreCase("edit")) 
		{
			req.setAttribute("Item", item);			
			RequestDispatcher rd = req.getRequestDispatcher("/itemEdit.jsp");
			try {
				rd.forward(req, resp);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		if (action.equalsIgnoreCase("activate")) 
		{
			boolean result = ItemUtils.setActive(item,currentUser);
			if(result)
			{
				
				resp.sendRedirect("/items/details/"+itemId);
			}
			else
			{
				resp.setContentType("text/html");
				out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/items/details/"+itemId+"' />" );
				out.print("Please check if you are authorised to activate this item...  </head></html>");
				
			}
			return;
		}
		
		if(action.equalsIgnoreCase("deactivate"))
		{
			boolean result = ItemUtils.setInActive(item,currentUser);
			if(result)
				resp.sendRedirect("/items/details/"+itemId);
			else
			{
				resp.setContentType("text/html");
				out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/items/details/"+itemId+"' />");
				out.print(" You are not authorised to activate this item  </head></html>");
				
			}
			return;
		}
		
		if(action.equalsIgnoreCase("pic"))
		{
			boolean detailsVisible = ItemUtils.areDetailsVisible(item, currentUser);
			
			if(!detailsVisible)
			{ 
				resp.setContentType("text/html");
				out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/' /> ");
				out.print(" The item has expired or inactive !! You can not view it.  </head></html>");
				return;
			}
			String bk = item.getProperty("PicBlobKey").toString();
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
		out.print("<html><head>  <meta http-equiv='refresh' content='3;url=/' /> ");
		out.print(" Sorry!! No such page exists... </head></html>");
		
	}
	
	
	
	
	
	
	

}


