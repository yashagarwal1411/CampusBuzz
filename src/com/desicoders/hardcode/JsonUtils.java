

package com.desicoders.hardcode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
	

		  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
			    		    
			     JSONObject json = new JSONObject(readPlainFromUrl(url));
			     return json;
		    
		  }

		  public static String readPlainFromUrl(String url) throws IOException{
			  String[] parts = url.split("\\?");
			    String urlParameters="";
				String request = parts[0];
				if(parts.length>1)
					urlParameters = parts[1];
				
				URL endpoint = new URL(request); 
				HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();           
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setInstanceFollowRedirects(false); 
				connection.setRequestMethod("POST"); 
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				connection.setUseCaches (false);

				DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
				connection.disconnect();
			     // Read the response
			    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			    StringBuffer sb = new StringBuffer();
			    String str = br.readLine();
			     while(str != null){
			           sb.append(str);
			           str = br.readLine();
			     }
			     br.close();
			     String responseString = sb.toString();
			     return responseString;

		  }

		  public static String getPlainFromUrl(String url) throws IOException{

			  URL mUrl = new URL(url);
			  URLConnection conn = mUrl.openConnection();
			  InputStream is = conn.getInputStream() ;
			  BufferedReader br = new BufferedReader(new InputStreamReader(is));
			  StringBuffer sb = new StringBuffer();
			  String str = br.readLine();
			  while(str != null){
			  sb.append(str);
			  str = br.readLine();
			  }
			  br.close();
			  String responseString = sb.toString();
			  return responseString;
			  }

			  public static JSONObject getJsonFromUrl(String url) throws IOException, JSONException {

			  JSONObject json = new JSONObject(getPlainFromUrl(url));
			  return json;

			  }


		public static Map<String, String> parseSearchSuggestionsFromDomain(String query, String appId) throws IOException, JSONException {
			String url = appId+"webservices/search_suggestions?query=" + query;
			JSONObject json = JsonUtils.getJsonFromUrl(url);
			Map<String,String> suggestions = new HashMap<String, String>();
			if(json.getBoolean("success") == false)
			{
				return suggestions;
			}
			JSONArray itemsForeign = json.getJSONArray("items");
			for (int idx = 0; idx < itemsForeign.length(); idx++)
			{ 
				JSONObject item = itemsForeign.getJSONObject(idx);
				if(item.has("itemId"))
					suggestions.put(item.getString("itemId"), item.getString("fullString"));
				else
					suggestions.put(idx+" N/A", item.getString("fullString"));
			}	
			return suggestions;
		}
		  
		  public static String getPlainFromUrl(String url) throws IOException{
			  
			  URL mUrl = new URL(url);
			  URLConnection conn = mUrl.openConnection();
			  InputStream is = conn.getInputStream() ;
			  BufferedReader br = new BufferedReader(new InputStreamReader(is));
			    StringBuffer sb = new StringBuffer();
			    String str = br.readLine();
			     while(str != null){
			           sb.append(str);
			           str = br.readLine();
			     }
			     br.close();
			     String responseString = sb.toString();
			     return responseString;
		  }
		  
		  public static JSONObject getJsonFromUrl(String url) throws IOException, JSONException {
  		    
			     JSONObject json = new JSONObject(getPlainFromUrl(url));
			     return json;
		    
		  }
		  
		  public static Map<String, String> parseSearchSuggestionsFromDomain(String query, String appId) throws IOException, JSONException {
			  String url = appId+"webservices/search_suggestions?query=" + query;
			  JSONObject json = JsonUtils.getJsonFromUrl(url);
			  Map<String,String> suggestions = new HashMap<String, String>();
			  if(json.getBoolean("success") == false)
			  {
			  return suggestions;
			  }
			  JSONArray itemsForeign = json.getJSONArray("items");
			  for (int idx = 0; idx < itemsForeign.length(); idx++)
			  {
			  JSONObject item = itemsForeign.getJSONObject(idx);
			  if(item.has("itemId"))
			  suggestions.put(item.getString("itemId"), item.getString("fullString"));
			  else
			  suggestions.put(idx+" N/A", item.getString("fullString"));
			  }
			  return suggestions;
			  }


		  
}
