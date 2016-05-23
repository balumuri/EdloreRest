package com.edlore.amazon.util;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.edlore.util.UploadManualSatus;

public class AmazonHttpClient {

	public static InputStream getFileFromAmazonws(String url, ArrayList<UploadManualSatus> list, UploadManualSatus status)
	{
		
		System.out.println("The requested url is -------- "+ url);
		DefaultHttpClient httpClient = null;
	    HttpGet getRequest = null;
	    InputStream inputStream = null;
	    
	    // Declare the local variables
	    httpClient = new DefaultHttpClient();
	    getRequest = new HttpGet(url);
	
	    
	    //Set the API media type in http accept header
	    getRequest.addHeader("accept", "application/pdf");
	          
	    //Send the request; It will immediately return the response in HttpResponse object
	    try {
	    	HttpResponse response = httpClient.execute(getRequest);
				
			if (response.getStatusLine().getStatusCode() != 200) {
				status.setMessage("Amazon server unavilable");
				status.setStatus("502");
				
				list.add(status);
				
			}else{
			//Now pull back the response object
			HttpEntity httpEntity = response.getEntity();
			
			Header[] headers =	response.getAllHeaders();
			System.out.println(headers);
			// get content from the http entity
		    inputStream = httpEntity.getContent();
			}
			} catch (Exception e) {
				status.setStatus("202");
				status.setMessage("problem in amazone service");
				
				list.add(status);
				
			}finally
			{
				
				
			}
	    return inputStream;
	}
}
