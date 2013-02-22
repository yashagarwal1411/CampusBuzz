package com.desicoders.hardcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
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
	
	 public static void updateFTSStuffForItem(
             Entity item) {

     StringBuffer sb = new StringBuffer();
     
     sb.append(item.getProperty("Title")+" "+item.getProperty("Description"));
             
     Set<String> new_ftsTokens = SearchFunction.getTokensForIndexingOrQuery(
                     sb.toString(),
                     MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
     Set<String> new_ftsTokensForTitle = SearchFunction.getTokensForIndexingOrQuery(
    		 item.getProperty("Title").toString(),
             MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
     Set<String> new_ftsTokensForDesciption = SearchFunction.getTokensForIndexingOrQuery(
    		 item.getProperty("Description").toString(),
             MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);
     /*
     Set<String> ftsTokens = new HashSet<String>(); //(Set<String>)item.getProperty("FTS");

             ftsTokens.clear();

             for (String token : new_ftsTokens) {
                     ftsTokens.add(token);

             }              
      */       
             item.setProperty("FTS",new_ftsTokens);
             item.setProperty("titleFTS", new_ftsTokensForTitle);
             item.setProperty("descriptionFTS", new_ftsTokensForDesciption);
}
	 
	 
	 @SuppressWarnings("deprecation")
	public static List<Entity> advancedSearch(String searchQueryTitle,String searchQueryDescription,String maxPriceLimit,String leastPriceLimit)
	 {
		 	Set<String> queryTokensTitle = SearchFunction.getTokensForIndexingOrQuery(searchQueryTitle,MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		 	Set<String> queryTokensDescription = SearchFunction.getTokensForIndexingOrQuery(searchQueryDescription,MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		 	float maxPrice=Float.MAX_VALUE,leastPrice = 0;
		 	try
			{
		 		maxPrice = Float.parseFloat(maxPriceLimit);		 		
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		 	try
			{
		 		leastPrice = Float.parseFloat(leastPriceLimit);
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		 	
		 	Query q = new Query("Item");
			
			for(String s:queryTokensTitle)
			{
				q.addFilter("titleFTS", FilterOperator.EQUAL, s);				
			}
			for(String s:queryTokensDescription)
			{
				q.addFilter("descriptionFTS", FilterOperator.EQUAL, s);				
			}
			q.addFilter("Price", FilterOperator.LESS_THAN_OR_EQUAL, maxPrice);
			q.addFilter("Price", FilterOperator.GREATER_THAN_OR_EQUAL, leastPrice);			
			
			try{		
			List<Entity> items = Utils.runQuery(q);
			return items;
			}
			catch(Exception e)
			{
				return null;
			}		
	 }
	 
    
     
    
     
	
}
