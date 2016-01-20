package org.iitd.eventextraction.data.kgextractor;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.iitd.eventextraction.common.CommonFunctions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FreebaseRecurrentEventExtractor {
	
	//String FREEBASE_KEY = "AIzaSyDgHab5xJli2zZ5ZLv38yONzmXcE_rFopU"; //srikanth
	//String FREEBASE_KEY = "AIzaSyDlulVaJQ-DpEBgKzg1W_CoPX2q-5c55qY"; // alternate
	String FREEBASE_KEY = "AIzaSyCboevfksVYDngVj4-9hhdI790thY4RH0A";

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
	
	public void GetAllRecurrentEvents() {
		
		String query = "[{" +
				  			"\"mid\": null," +
				  			"\"name\": null," +
				  			"\"type\": \"/time/recurring_event\"," +
				  			"\"/time/recurring_event/current_frequency\": {" +
				  				"\"name\": null," +
				  				"\"id\": null," +
				  				"\"optional\": true" +
				  			"}," +
				  			"\"/time/recurring_event/instances\": [{" +
				  				"\"name\": null," +
				  				"\"id\": null," +
				  				"\"limit\": 1," +
				  				"\"optional\": true" +
				  			"}]" +
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
		        	 
		        	 String eventName = (String) resultJson.get("name");
		        	 String eventURL = freeBaseURL + (String) resultJson.get("mid");
		        	 
		        	 String frequency = "";
		        	 JSONObject frequencyObject = (JSONObject) resultJson.get("/time/recurring_event/current_frequency");
		        	 if(frequencyObject != null) {
		        		 frequency = (String) frequencyObject.get("name");
		        	 }
		        	 
		        	 String instanceName = "";
		        	 String instanceLink = "";
		        	 JSONArray InstanceObject 	= (JSONArray) resultJson.get("/time/recurring_event/instances");
		        	 if(InstanceObject != null) {
		        		 if(InstanceObject.size() > 0) {
		        			 instanceName = (String) ((JSONObject)InstanceObject.get(0)).get("name");
		        			 instanceLink = freeBaseURL + (String) ((JSONObject)InstanceObject.get(0)).get("id");
		        		 }
		        	 }
		        	 
		        	 sb.append(eventName + "\t" +
		        			 eventURL + "\t" +
		        			 frequency + "\t" +
		        			 instanceName + "\t" +
		        			 instanceLink + "\n" );
		        	 
	        	 } catch (Exception e) {
	        		 e.printStackTrace();
	        	 }
	         }
        }
        
        CommonFunctions.WriteToFile("data//recurrent-events.tsv", sb.toString());
	}
	
	public static void main(String[] args) {
		
		FreebaseRecurrentEventExtractor free = new FreebaseRecurrentEventExtractor();
		free.GetAllRecurrentEvents();
	}
}
