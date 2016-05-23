package com.edlore.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.edlore.box.util.EdloreBoxAPIConnection;

/**
 * @author Sowjanya B
 *
 */
@Path(value="/download")
public class DownloadService {

	
	/**
	 * @param manualId
	 * @return response 
	 * 
	 *  By taking input as manualId it connects to box and then returns 
	 *  the manual based on manual Id
	 */
	@GET
	@Path("/manual")
	@Produces("application/pdf")
	public Response getFile(@QueryParam("manualId") String manualId) {
		System.out.println("Enter get file method");
		// As now this url is hardcoded, from local file syatem it returns the pdf
		if(manualId == null)
		{
			return Response.status(400).entity("requested manual id unavilable").build();
		}
		//String ZIP_FILE_PATH = "C:\\edlorefolder\\"+ manualId;
	  //  File f = new File(ZIP_FILE_PATH);
			BoxFile file = null;
			BoxDeveloperEditionAPIConnection api = null;
			BoxFile.Info info=null;
			OutputStream outputStream = null;
			 FileInputStream fis = null;
			 try {
			EdloreBoxAPIConnection boxAPIConnection = new EdloreBoxAPIConnection();
			api = boxAPIConnection.getAppUserConnection();
			
			System.out.println("Connection created succesfully");
		  
		    file= new BoxFile(api, manualId);
			info = file.getInfo();
			
			  System.out.println("file name is:"+info.getName());
			  String filePath=info.getName();
			  
			
				outputStream = new FileOutputStream(filePath);
			
			 fis =new FileInputStream(filePath);
			 
			  file.download(outputStream);
			  System.out.println("download succesfully");
			 
			  } catch (Exception e) {
				  if(e instanceof BoxAPIException)
					{
						BoxAPIException exception = (BoxAPIException) e;						
						return Response.status(exception.getResponseCode()).entity(exception.getMessage()).build();
					}
										
			}finally {
				try {
					outputStream.close();
				}catch (Exception e) {
					
				}
				
			}
			

	     /** Return the reponse with status 200 and set the 
	      *  reponse header as content-disposition as inline so that browser client can able to disply 
	      *  the document or loads the document on browser 
	      *  
	      *  If content-disposition is attachment then it forces the browser to save document
	      *  in local system or it gets dowloaded
	      *  */
	    
	    return Response.ok(fis)
	            .header("Content-Disposition",
	                    "inline; filename="+info.getName()).build();
	}
	
	
}
