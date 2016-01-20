package org.iitd.eventextraction.data.kgextractor;

import org.iitd.eventextraction.common.CommonFunctions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class FreebaseEventExtractor {

		//String FREEBASE_KEY = "AIzaSyDgHab5xJli2zZ5ZLv38yONzmXcE_rFopU"; //srikanth
		String FREEBASE_KEY = "AIzaSyDlulVaJQ-DpEBgKzg1W_CoPX2q-5c55qY"; // alternate
		//String FREEBASE_KEY = "AIzaSyCboevfksVYDngVj4-9hhdI790thY4RH0A";

		String freeBaseURL = "https://www.freebase.com";
		public JSONObject QueryFreebase(String query, boolean cursor, String cursorValue) {
			  
			  JSONObject result = null;
			  try {
			    	 HttpTransport httpTransport = new NetHttpTransport();
			         HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			         JSONParser parser = new JSONParser();
			         GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
			         
			         url.put("lang", "/lang/en");
			         if(cursor) {
					     if(cursorValue == null)
					    	 url.put("cursor", "");
					     else
					    	 url.put("cursor", cursorValue);
				     }
			         url.put("query", query);
				     url.put("key", FREEBASE_KEY);
				     
			         HttpRequest request = requestFactory.buildGetRequest(url);
			         HttpResponse httpResponse = request.execute();
			         
			         result = (JSONObject)parser.parse(httpResponse.parseAsString());
			         
			    } catch (Exception ex) {
			      ex.printStackTrace();
			      return result;
			    }
			
			  return result;
		  }
		
		public void GetAllEvents() {
			
			String query = "[{" +
					  			"\"mid\": null," +
					  			"\"name\": null," +
					  			"\"type\": \"/time/event\"," +
					  			"\"/common/topic/alias\": [{}]" +
					  		"}]";
			
			boolean cursor = true;
	        String cursorValue = null;
	        
	        StringBuffer sb = new StringBuffer();
	        int count = 0;
	        
	        while(cursor) {
	       	 
		         JSONObject result = QueryFreebase(query, true, cursorValue);
		         if(result == null)
		        	 break;
		         
		         JSONArray resultList = (JSONArray) result.get("result");
		         
		         if(result.get("cursor") == null || result.get("cursor").toString().equalsIgnoreCase("false"))
		        	 cursor = false;
		         else
		        	 cursorValue = result.get("cursor").toString();
		         
		         for (int i = 0; i < resultList.size(); i++) {
		        	 
		        	 System.out.println(++count);
		        	 
		        	 try {
		        	 
			        	 JSONObject resultJson = (JSONObject) resultList.get(i);
			        	 
			        	 sb.append((String) resultJson.get("name")+"\n");
			        	 
			        	 JSONArray aliasList = (JSONArray) resultJson.get("/common/topic/alias");
			        	 if(aliasList != null) {
				        	 for (int j = 0; j < aliasList.size(); j++) {
				        		 
				        		 JSONObject aliasJson = (JSONObject) aliasList.get(j);
					        	 
					        	 String aliasValue = (String) aliasJson.get("value");
					        	 String aliasLang =  (String) aliasJson.get("lang");
					        	 if(aliasLang.equals("/lang/en"))
					        		 sb.append(aliasValue + "\n");
							 }
			        	 }
			        	 
		        	 } catch (Exception e) {
		        		 e.printStackTrace();
		        	 }
		         }
	        }
	        
	        CommonFunctions.WriteToFile("data//events.txt", sb.toString());
		}
		
	public static void main(String[] args) {
		FreebaseEventExtractor freebaseEventExtractor = new FreebaseEventExtractor();
		freebaseEventExtractor.GetAllEvents();
	}
}
