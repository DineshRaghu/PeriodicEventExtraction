package org.iitd.eventextraction.data.kg;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class FreebaseExtractor {

	//static String FREEBASE_KEY = "AIzaSyDgHab5xJli2zZ5ZLv38yONzmXcE_rFopU"; //srikanth
	static String FREEBASE_KEY = "AIzaSyDlulVaJQ-DpEBgKzg1W_CoPX2q-5c55qY"; // alternate
	//static String FREEBASE_KEY = "AIzaSyCboevfksVYDngVj4-9hhdI790thY4RH0A";

	static String freeBaseURL = "https://www.freebase.com";
	public static JSONObject QueryFreebase(String query, boolean cursor, String cursorValue) {
		
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
		     
		     System.out.println(cursorValue);
	         HttpRequest request = requestFactory.buildGetRequest(url);
	         HttpResponse httpResponse = request.execute();
	         
	         result = (JSONObject)parser.parse(httpResponse.parseAsString());
	         
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    	return result;
	    }
		return result;
	}
}
