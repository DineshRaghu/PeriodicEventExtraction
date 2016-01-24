package org.iitd.eventextraction.data.freebase;

import org.iitd.eventextraction.common.CommonFunctions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FreebaseWikiIdExtractor {

	public void WriteAllWikiIds(String type, int numOfResults) {
		
		String query = "[{" +
				  			"\"type\": \"" + type + "\"," +
				  			"\"name\": null," +
				  			"\"mid\": null," +
				  			"\"/wikipedia/topic/en_id\": []" +
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
	        	  
	        	 try {
	        	 
		        	 JSONObject resultJson = (JSONObject) resultList.get(i);
		        	 
		        	 JSONArray wikiIdArray = (JSONArray) resultJson.get("/wikipedia/topic/en_id");
		        	 for (int j = 0; j < wikiIdArray.size(); j++) {
		        		 sb.append((String) wikiIdArray.get(0)+"\n");
		        		 count++;
		        		 if(numOfResults == count)
		        			 break;
					 }
		        	
	        	 } catch (Exception e) {
	        		 e.printStackTrace();
	        	 }
	        	 
	        	 if(numOfResults == count)
        			 break;
	         }
	         
	         if(numOfResults == count)
    			 break;
        }
        
        CommonFunctions.AppendToFile("data//non-events.txt", sb.toString());
	}
	
	public static void main(String[] args) {
		
		CommonFunctions.SetProxy();
		String[] types = {"/people/person","/organization/organization",
						  "/location/location","/music/album",
						  "/book/book","/sports/sports_team",
						  "/government/political_party","/education/field_of_study",
						  "/law/invention","/sports/sport"};
		
		FreebaseWikiIdExtractor freebaseWikiIdExtractor = new FreebaseWikiIdExtractor();
		for (int i = 0; i < types.length; i++) {
			System.out.println(types[i]);
			freebaseWikiIdExtractor.WriteAllWikiIds(types[i], 100);
		}
	}
}
