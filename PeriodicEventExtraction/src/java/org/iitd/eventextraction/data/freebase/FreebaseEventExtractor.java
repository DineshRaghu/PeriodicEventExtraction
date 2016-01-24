package org.iitd.eventextraction.data.freebase;

import org.iitd.eventextraction.common.CommonFunctions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FreebaseEventExtractor {

		public void GetAllEvents() {
			
			String query = "[{" +
					  			"\"mid\": null," +
					  			"\"name\": null," +
					  			"\"type\": \"/time/event\"," +
					  			"\"/wikipedia/topic/en_id\": []," +
					  			"\"/time/event/instance_of_recurring_event\": {" +
					  				"\"name\": null," +
					  				"\"mid\": null," +
					  				"\"/wikipedia/topic/en_id\": []," +
					  				"\"optional\": true" +
					  			"}" +
					  		"}]";
			
			boolean cursor = true;
	        String cursorValue = null;
	        
	        StringBuffer sb = new StringBuffer();
	        int count = 1;
	        
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
		        	 
		        	 if(count%1000==0)
		        		 System.out.println(count);
		        	 count++;
		        	 
		        	 try {
		        	 
			        	 JSONObject resultJson = (JSONObject) resultList.get(i);
			        	 
			        	 String eventName = (String) resultJson.get("name");
			        	 String eventMid = (String) resultJson.get("mid");
			        	 JSONArray eventWikiIdArray = (JSONArray) resultJson.get("/wikipedia/topic/en_id");
			        	 String eventWikiId = "";
			        	 for (int j = 0; j < eventWikiIdArray.size(); j++) {
			        		 eventWikiId = (String) eventWikiIdArray.get(0);
						 }
			        	 
			        	 JSONObject recurrentEvent = (JSONObject) resultJson.get("/time/event/instance_of_recurring_event");
					     
			        	 String recurrentEventName = "";
			        	 String recurrentEventMid = "";
			        	 String recurrentEventWikiId = "";
			        	 
			        	 if(recurrentEvent != null) {
				        	 recurrentEventName = (String) recurrentEvent.get("name");
				        	 recurrentEventMid = (String) recurrentEvent.get("mid");
				        	 JSONArray recurrentEventWikiIdArray = (JSONArray) recurrentEvent.get("/wikipedia/topic/en_id");
				        	 for (int j = 0; j < recurrentEventWikiIdArray.size(); j++) {
				        		 recurrentEventWikiId = (String) recurrentEventWikiIdArray.get(0);
							 }
			        	 }
						
			        	 sb.append(eventName + "\t" +
			        			 eventMid + "\t" +
			        			 eventWikiId + "\t" +
			        			 recurrentEventName + "\t" +
			        			 recurrentEventMid + "\t" +
			        			 recurrentEventWikiId + "\n");
			        	
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
