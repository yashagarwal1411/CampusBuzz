

package com.desicoders.hardcode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
		  
}
