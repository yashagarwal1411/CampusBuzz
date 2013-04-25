package com.desicoders.hardcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.mortbay.log.Log;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

/*
B-C-01	The Application allows Registered and Non-Registered Users to search for Items based on any of the following Item fields:
Title
Description
Price
This will be a basic search functionality based on a single keyword or a single, exact string (exact match only).  That is, the search does not need to support the ability to parse multiple keywords or derivations of keywords.  For example, a search for the term ‘math tutor’ will ONLY match on ‘math tutor’ and NOT ‘math’ by itself, ‘tutor’ by itself, variations like ‘math tutors’ or ‘tutoring math’, etc.

The ability to parse multiple keywords and derivations of keywords is covered in the optional requirement O-C-01.
B-C-02	Search results will display the Title, Description, and Price for each Item found.
*/

public class SearchUtils {

	public static final int MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH = 5;  
    public static final int MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX = 200;
    public static final Logger _log = Logger.getLogger(WebServicesServlet.class.getName());
	
	@SuppressWarnings("deprecation")
	public static List<Entity> searchAll(String searchQuery)
	{
		Set<String> queryTokens = SearchFunction.getTokensForIndexingOrQuery(searchQuery,MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		//Set<String> queryTokensZ = new HashSet<String>();
		Query q = new Query("Item");
		if(queryTokens.isEmpty())
		{
			return null;
		}
		//for(String s:queryTokens)
		//{
			//q.addFilter("FTS", FilterOperator.EQUAL, s);
			//queryTokensZ.add(s+"z");
			//q.addFilter("FTS", FilterOperator.LESS_THAN_OR_EQUAL, s+"z");
			//q.addFilter("FTS", FilterOperator.GREATER_THAN_OR_EQUAL, s);
		//}
		//
		if(queryTokens.size()>0)
			q.addFilter("FTS", FilterOperator.IN, queryTokens);
		//Filter searchFTS1 = FilterOperator.GREATER_THAN_OR_EQUAL.of("FTS",queryTokens);
		//Filter searchFTS2 = FilterOperator.LESS_THAN_OR_EQUAL.of("FTS", queryTokensZ);
		//CompositeFilter filter = CompositeFilterOperator.and(searchFTS1,searchFTS2);
		//Query q = new Query("Item").setFilter(filter);
		
		try{		
		List<Entity> items = Utils.runQuery(q);
		List<Entity> visibleItems = new ArrayList<Entity>();
		for(Entity item:items)
		{
			if(ItemUtils.areDetailsVisible(item,null))
			{
				visibleItems.add(item);
			}
		}
		return visibleItems;
		}
		catch(Exception e)
		{
			return null;
		}							
	}
	
	public static Map<String, String> searchSuggestions(String query)
	{
		//List<String> itemId = new ArrayList<String>();
		//List<String> suggestions = new ArrayList<String>();
		Map<String, String> dictionary = new HashMap<String, String>();
		
		if(query.length()<4)
			return dictionary;
		Filter search1 = FilterOperator.GREATER_THAN_OR_EQUAL.of("Title",query);
		Filter search2 = FilterOperator.LESS_THAN_OR_EQUAL.of("Title", query+"z");
		CompositeFilter filter = CompositeFilterOperator.and(search1,search2);
		Query q = new Query("Item").setFilter(filter).addProjection(new PropertyProjection("Title",String.class));
		try
		{		
			List<Entity> results = Utils.runQuery(q);
			List<Entity> visibleItems = new ArrayList<Entity>();
			for(Entity item:results)
			{
				if(ItemUtils.areDetailsVisible(item,null))
				{
					visibleItems.add(item);
				}
			}
			for(Entity item : visibleItems)
			{
				_log.info("title is :" + item.getProperty("Title").toString());
				//suggestions.add(item.getProperty("Title").toString());
				//itemId.add(item.getKey().getId()+"");
				dictionary.put(item.getKey().getId()+"",item.getProperty("Title").toString());
				
			}
			//List<String>[] output = new ArrayList<String>()[2];
			return dictionary;
		}
		catch(Exception ex)
		{
			return dictionary;
		}
		
	}
	
	 public static void updateFTSStuffForItem(
             Entity item) {

     StringBuffer sb = new StringBuffer();
     
     sb.append(item.getProperty("Title")+" "+item.getProperty("Description"));
             
     Set<String> new_ftsTokens = SearchFunction.getTokensForIndexingOrQuery(
                     sb.toString(),
                     MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
     /*Set<String> new_ftsTokensForTitle = SearchFunction.getTokensForIndexingOrQuery(
    		 item.getProperty("Title").toString(),
             MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
     Set<String> new_ftsTokensForDesciption = SearchFunction.getTokensForIndexingOrQuery(
    		 item.getProperty("Description").toString(),
             MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
     
     Set<String> ftsTokens = new HashSet<String>(); //(Set<String>)item.getProperty("FTS");

             ftsTokens.clear();

             for (String token : new_ftsTokens) {
                     ftsTokens.add(token);

             }              
      */       
             item.setProperty("FTS",new_ftsTokens);
            // item.setProperty("titleFTS", new_ftsTokensForTitle);
             //item.setProperty("descriptionFTS", new_ftsTokensForDesciption);
}
	 
	 
	 @SuppressWarnings("deprecation")
	public static List<Entity> advancedSearch(String searchQuery,String maxPriceLimit,String leastPriceLimit)
	 {
		 	//Set<String> queryTokensTitle = SearchFunction.getTokensForIndexingOrQuery(searchQueryTitle,MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		 	//Set<String> queryTokensDescription = SearchFunction.getTokensForIndexingOrQuery(searchQueryDescription,MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		 	Query q = new Query("Item");
		 	float maxPrice=0,leastPrice = 0;
		 	try
			{
		 		if(!maxPriceLimit.equals("") && maxPriceLimit!=null)
		 		{
		 			maxPrice = Float.parseFloat(maxPriceLimit);
			 		q.addFilter("Price", FilterOperator.LESS_THAN_OR_EQUAL, maxPrice);
		 		}		 		
			}
			catch (Exception e) {
				
			}
		 	try
			{
		 		if(!leastPriceLimit.equals("") && leastPriceLimit!=null)
		 		{		 		
			 		leastPrice = Float.parseFloat(leastPriceLimit);
			 		q.addFilter("Price", FilterOperator.GREATER_THAN_OR_EQUAL, leastPrice);
		 		}
			}
			catch (Exception e) {
				
			}
		 	Set<String> queryTokens = SearchFunction.getTokensForIndexingOrQuery(searchQuery,MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		 	
		 	
			
			if(queryTokens.size()>0)
		 		q.addFilter("FTS", FilterOperator.IN, queryTokens);
		 	
			/*for(String s:queryTokensTitle)
			{
				q.addFilter("titleFTS", FilterOperator.EQUAL, s);				
			}
			for(String s:queryTokensDescription)
			{
				q.addFilter("descriptionFTS", FilterOperator.EQUAL, s);				
			}*/
		 	
						
			
			try{		
			List<Entity> items = Utils.runQuery(q);
			List<Entity> visibleItems = new ArrayList<Entity>();
			for(Entity item:items)
			{
				if(ItemUtils.areDetailsVisible(item,null))
				{
					visibleItems.add(item);
				}
			}
			return visibleItems;
			}
			catch(Exception e)
			{
				return null;
			}		
	 }
	 
    
     
    
     
	
}
