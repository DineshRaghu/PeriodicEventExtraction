package org.iitd.eventextraction.data.kg;

import org.iitd.eventextraction.common.CommonFunctions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FreebaseRecurrentEventExtractor {
	
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
       	 
	         JSONObject result = FreebaseExtractor.QueryFreebase(query, true, cursorValue);
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
		        	 String eventURL = FreebaseExtractor.freeBaseURL + (String) resultJson.get("mid");
		        	 
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
		        			 instanceLink = FreebaseExtractor.freeBaseURL + (String) ((JSONObject)InstanceObject.get(0)).get("id");
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
