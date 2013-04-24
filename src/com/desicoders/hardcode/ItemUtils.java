package com.desicoders.hardcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;


import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;


/*B-B-01	Registered Users can post Items and delete Items that they posted.
B-B-02	Registered Users can edit Items.
B-B-03	Administrators can delete any Item posted to the Application.
B-B-04	When Items are created, the following attributes are created for each Item:
Title
Description
Price
Item Creation Time (when the Item was posted to the Application)
B-B-05	Items are assigned an expiration date.  An expired Item should only be visible to the Registered User who created the Item and Administrators.
B-B-06	All items should be associated with a Registered User.
B-B-07	Registered and Non-Registered Users can view Item details by clicking on links shown in search results (see B-C) and where Items are listed (for example, if the optional Virtual Shops are implemented, by clicking on displayed Items).  Item details will include:
Title
Description
Price
Item Creation Time
Seller Name
A button to communicate / message the Seller as described in B-D
*/

public class ItemUtils {

	public static final Logger _log = Logger.getLogger(ItemUtils.class.getName());
	public static List<Entity> listPostedItems(Entity user)
	{
		if(user == null)
			return null;
				
		Filter itemFilter = FilterOperator.EQUAL.of("OwnerKey",user.getKey());
		try
		{
			Query q = new Query("Item").setFilter(itemFilter); 
			List<Entity> items = Utils.runQuery(q);
			return items;
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	static boolean deleteItem(Entity item,Entity user)
	{
		if(user==null)
		{
			return false;
		}
		if(isUserOwner(item, user) || user.hasProperty("isAdmin") )
		{			
			if(!item.getProperty("PicBlobKey").toString().equals("null"))
		    {
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		    	BlobKey blobKey = new BlobKey(item.getProperty("PicBlobKey").toString());
		    	blobstoreService.delete(blobKey);
		    }
			DatastoreUtils.deleteEntity(item.getKey());
			_log.info("item deleted #"+item.toString());
			return true;
		}
		return false; //if the item is  not deleted
	}
	
	static boolean deleteAllItems(Entity user)
	{
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		if(user==null)
		{
			return false;
		}
		List<Entity> items = ItemUtils.listPostedItems(user);
		if(items == null)
			return true;
		for(Entity item:items)
		{
			if(!item.getProperty("PicBlobKey").toString().equals("null"))
		    {				
		    	BlobKey blobKey = new BlobKey(item.getProperty("PicBlobKey").toString());
		    	blobstoreService.delete(blobKey);
		    }
			DatastoreUtils.deleteEntity(item.getKey());
			_log.info("item deleted #"+item.toString());
		}		
		return true; 
	}
	
	
	static boolean addItem(String title,String description,float price,BlobKey blobKey,Entity user,HttpServletRequest req) throws IOException, JSONException
	{	 
		
		 if(user==null)
		 {
			 return false;
		 }
		 Date creationDate = new Date();
		 Calendar calendar = new GregorianCalendar();
		 calendar.setTime(creationDate);
		 calendar.add(Calendar.DATE, 30); //HERE I AM ADDING 30 DAYS FOR EXPIRATION 
		 Date expirationDate = calendar.getTime();
		 Set<String> ftsTokens = new HashSet<String>();
		 Entity item = Utils.createEntity("Item");
		 item.setProperty("Title", title);
		 item.setProperty("Description", description);
		 item.setProperty("Price",price);
		 item.setProperty("CreationTime",creationDate);
		 item.setProperty("ExpirationTime", expirationDate);
		 item.setProperty("OwnerKey",user.getKey() );
		 item.setProperty("FTS",ftsTokens);
		 item.setProperty("isActive", 1);
		 item.setProperty("Rating", 0.0);
		 item.setProperty("VotersNumber",0);
		 item.setProperty("Feedback", null);
		 
		 SearchUtils.updateFTSStuffForItem(item);
		 
		 if (blobKey == null)
		 {
	            item.setProperty("PicBlobKey", "null");
		 }
		 else
		 {
			 	item.setProperty("PicBlobKey", blobKey.getKeyString());
	     }
		 
		 Utils.put(item);		 
		 Map<String,String> externalApps=Utils.externalApps();
		 for(String appId : externalApps.keySet())
		 {
			 String data = "[{\"id\":\" "+ item.getKey().getId()+"\"";//check this as this is a recently added item key might not be complete
			 data += Utils.getJSONforItem(item, req)+"]";
			 String url = appId + "webservices/new_item=" + data;
			 JSONObject json = JsonUtils.readJsonFromUrl(url);
			 //add code to handle success response
		 }
		 
		 _log.info("item added #"+item.toString());
		 return true;//to indicate success
	}
	
	//to check if item is pass its expiration date
	static public boolean isExpired(Entity item)
	{
		Date currentDate = new Date();
		if(currentDate.after((Date)item.getProperty("ExpirationTime")))
		{
			return true; //is expired
		}
		return false;
	}
	
	static boolean editItem(String title,String description,float price,int isActive,BlobKey blobKey,Entity item,Entity user)
	{		
		if(isUserOwner(item, user) )	
		{
			 item.setProperty("Title", title);
			 item.setProperty("Description", description);
			 item.setProperty("Price",price);
			 item.setProperty("isActive",isActive);
			 SearchUtils.updateFTSStuffForItem(item);
			 if (blobKey != null)
			 {
				    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				    if(!item.getProperty("PicBlobKey").toString().equals("null"))
				    {
				    	BlobKey blobKey2 = new BlobKey(item.getProperty("PicBlobKey").toString());
				    	blobstoreService.delete(blobKey2);
				    }
				 	item.setProperty("PicBlobKey", blobKey.getKeyString());
		     }
			 Utils.put(item);
			 _log.info("item edited #"+item.toString());
			 return true;//as the request was completed
			
		}
		return false;
	}
	
	
	static public boolean doesCurrentUserOwnItem(Entity item,HttpServletRequest req)
	{
		Entity user = Utils.getUserFromSession(req);
		if(user==null)
		{
			return false;
		}
		if(user.getKey().equals((Key)item.getProperty("OwnerKey")) )	
		{
			return true;
		}
		return false;
	}
	
	static public boolean isActive(Entity item)
	{
		return ((Long)item.getProperty("isActive"))==1;
	}
	
	static public boolean  setActive(Entity item,Entity user)
	{
		if(user==null)
		{
			return false;
		}
		if(isUserOwner(item,user))
		{
			item.setProperty("isActive",1);
			Utils.put(item);
			 _log.info("item activated #"+item.toString());
			return true;
		}
		return false;
	}
	
	static public boolean  setInActive(Entity item,Entity user)
	{
		if(user==null)
		{
			return false;
		}
		if(isUserOwner(item,user))
		{
			item.setProperty("isActive",0);
			Utils.put(item);
			 _log.info("item deactivated #"+item.toString());
			return true;
		}
		return false;
	}
	
	static boolean isUserOwner(Entity item,Entity user)
	{
		if(user==null)
		{
			return false;
		}
		return user.getKey().equals(item.getProperty("OwnerKey"));
	}
	
	public static List<Entity> listNewItems(HttpServletRequest req )
	{
		
		try
		{
			Query q = new Query("Item").addSort("CreationTime"); 
			List<Entity> items = Utils.runQuery(q,20);
			List<Entity> visibleItems = new ArrayList<Entity>();
			for(Entity item:items)
			{
				if(areDetailsVisible(item,null))
				{
					visibleItems.add(item);
				}
			}
			return visibleItems;
		}
		catch(Exception e)
		{
			//do something
			return null;
		}
		
	}
	
	public static boolean areDetailsVisible(Entity item,Entity currentUser)
	{
		boolean detailsVisible = false;
		if(currentUser == null)
		{
			detailsVisible = (!ItemUtils.isExpired(item) && ItemUtils.isActive(item));
		}
		else
		{
			detailsVisible = currentUser.hasProperty("isAdmin") || currentUser.getKey().equals(item.getProperty("OwnerKey")) || (!ItemUtils.isExpired(item) && ItemUtils.isActive(item));
		}
		return detailsVisible;
	}
	
	
	public static boolean addRating(Entity item, double rating)
	{
		double value = 0;
		if(item.hasProperty("Rating"))
			value = Double.parseDouble(item.getProperty("Rating").toString());
		
		long numberOfVoters = (long) 0;
		if(item.hasProperty("VotersNumber"))
			numberOfVoters = (Long)item.getProperty("VotersNumber");
		
		
		value = (value*numberOfVoters+rating)/(numberOfVoters+1);
		item.setProperty("Rating", value);
		item.setProperty("VotersNumber",numberOfVoters+1);
		Utils.put(item);
		return true;
	}
	
	
	
	
}
