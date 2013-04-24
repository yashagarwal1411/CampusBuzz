package com.desicoders.hardcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class DatastoreUtils {

	public static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public static final Logger _log = Logger.getLogger(DatastoreUtils.class.getName());
	
	public static Key put(Entity newEntity)
	{
		boolean shouldLog = !newEntity.getKind().equalsIgnoreCase("status");
		datastore.put(newEntity);
		if(shouldLog)
			//we don't want to flood the logs with user's online status
			_log.info("Entity saved #"+newEntity.toString());
		return newEntity.getKey();
	}
	
	public static List<Entity> getEntity(String kind)
	{
		List<Entity> result = new ArrayList<Entity>();
		Query query = new Query(kind);
		PreparedQuery pq = datastore.prepare(query);
		result = pq.asList(FetchOptions.Builder.withDefaults());
		return result;
	}
	
	public static Key createKey(String kind,String id){
		return KeyFactory.createKey(kind, id);
	}
	
	public static Entity createEntity(String kind){
		Entity e = new Entity(kind);
		return e;
	}
	
	
    public static List<Entity> runQuery(Query q){
    	return datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    }
    
    public static List<Entity> runQuery(Query q,int Limit){
    	return datastore.prepare(q).asList(FetchOptions.Builder.withLimit(Limit));
    }
	
    public static Entity getUserFromSession(HttpServletRequest req){
    	Key userKey = Utils.getSession(req);
    	if(userKey==null || userKey.equals(""))
    		return null;
    	
		Entity mUser = getEntity(userKey);
		return mUser;
		
    }
    
    public static String getUserName(String id){
    	
    	Entity mUser = getEntity(KeyFactory.stringToKey(id));
    	
		if(mUser.hasProperty("fname")&&!mUser.getProperty("fname").equals(""))
			return (String) mUser.getProperty("fname");
		else{
			String email = (String) mUser.getProperty("Email");
			String fname=email.split("@")[0];
			return fname;
		}
		
    }
    
    public static DatastoreService getDataStoreService(){
    	return datastore;
    }
    
    public static boolean deleteEntity(Key entityKey)
    {
    	boolean shouldLog = !Utils.getEntity(entityKey).getKind().equalsIgnoreCase("status");
    	datastore.delete(entityKey);
    	if(shouldLog)
			//we don't want to flood the logs with user's online status
    		_log.info("Entity deleted #"+entityKey.toString());
    	return true;//to indicate success
    }
    
    public static Entity getEntity(Key key)
    {
    	try{
    		return datastore.get(key);   
    	}
    	catch(EntityNotFoundException e)
    	{
    		
    		return null;
    	}
    		
    }
    
    public static Entity getEntityFromId(Long id,String kind)
    {
    	Key entityKey = KeyFactory.createKey(kind, id);
    	if(entityKey == null)
    		return null;
    	return getEntity(entityKey);    	
    }
    
    public static boolean  ifHardcodeAdminsExists(){
    	Filter email1 = FilterOperator.EQUAL.of("Email","hardcodetest1@gmail.com");
    	Filter email2 = FilterOperator.EQUAL.of("Email","hardcodetest2@gmail.com");
    	CompositeFilter filter = CompositeFilterOperator.or(email1,email2);
		Query q = new Query("User").setFilter(filter);
		try{
			List<Entity> users = Utils.runQuery(q);			
			if(users.size()<2)
				return false;
		}catch (Exception e) {
			return false;
		}
		
		return true;
    }
    
    public static void createHardcodedAdmins(){
    	Filter email1 = FilterOperator.EQUAL.of("Email","hardcodetest1@gmail.com");
    	Filter email2 = FilterOperator.EQUAL.of("Email","hardcodetest2@gmail.com");
    	CompositeFilter filter = CompositeFilterOperator.or(email1,email2);
		Query q = new Query("User").setFilter(filter);
		try{
			List<Entity> users = Utils.runQuery(q);
			for(Entity user : users)
			{
				ItemUtils.deleteAllItems(user);
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				if(!user.getProperty("PicBlobKey").toString().equals("null"))
			    {	
					
			    	BlobKey blobKey = new BlobKey(user.getProperty("PicBlobKey").toString());
			    	blobstoreService.delete(blobKey);
			    }
				deleteEntity(user.getKey());
			}
		}
		catch (Exception e) {
			
		}			
			
    	Entity user1 = Utils.createEntity("User");
		user1.setProperty("Email","hardcodetest1@gmail.com");
		user1.setProperty("Password","hardcode");
		user1.setProperty("fname", "");
		user1.setProperty("lname", "");
		user1.setProperty("isAdmin", "1");
		user1.setProperty("PicBlobKey", "null");
		user1.setProperty("Description", "");
		Utils.put(user1);
		 _log.info("Test User 'hardcodetest1@gmail.com 'created");
		
		Entity user2 = Utils.createEntity("User");
		user2.setProperty("Email","hardcodetest2@gmail.com");
		user2.setProperty("Password","hardcode");
		user2.setProperty("fname", "");
		user2.setProperty("lname", "");
		user2.setProperty("isAdmin", "1");
		user2.setProperty("PicBlobKey", "null");
		user2.setProperty("Description", "");
		Utils.put(user2);
		 _log.info("Test User 'hardcodetes2@gmail.com 'created");
    }
    
    public static Map<String,String> externalApps()
    {
    	List<Entity> externalApps = Utils.getEntity("ExternalApp");
    	Map<String,String> apps=new HashMap<String, String>();
    	for(Entity ent:externalApps)
    	{
    		apps.put(ent.getProperty("appId").toString(), ent.getProperty("token").toString());    		
    	}
    	return apps;
    			
    }
    
}