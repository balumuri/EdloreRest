package com.edlore.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem.Info;
import com.box.sdk.Metadata;
import com.edlore.amazon.util.AmazonHttpClient;
import com.edlore.box.util.EdloreBoxAPIConnection;
import com.edlore.config.FileSystemConfig;
import com.edlore.util.UploadManual;
import com.edlore.util.UploadManualSatus;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * @author Sowjanya B
 * 
 *  Used to get request as json and make request to amazon ws for doc, 
 *  store the received doc into local system 
 *
 */
@Path(value="/upload")
public class UploadService {

	/* If the requested method signature is matches to method signature then method will 
	 * start to execute	and every request one service obj will creates*/
	@POST
	@Path(value ="/manual")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadManuals(JSONArray jsonArray, @QueryParam("assetId") String assetId)
	{
		// Declare the local variables
		List<UploadManual> listUploadManuals = null;
		UploadManual uploadManual = null;
		ArrayList<UploadManualSatus> list = null;
		
		if(assetId == null || assetId.equals("")){
			return Response.status(400).entity("Please provide valid Asset Id").build();
		}
		
		if(jsonArray == null)
		{
			return Response.status(400).entity("json file can't be null").build();
		}
		
			listUploadManuals = parseTheJson(jsonArray);
			
			/* Iterate the list of upload manual and make req to amazone web services 
			   and store it in local system*/
			list = new ArrayList<UploadManualSatus>();
			
			for(int i=0; i<listUploadManuals.size(); i++)
			{
				String url = null;
				InputStream inputStream = null;
				UploadManualSatus status = null;
				
				status = new UploadManualSatus();
				
				uploadManual = listUploadManuals.get(i);
				url = uploadManual.getManual_url();
				status.setFile_Id(uploadManual.getUpload_file_name());
				inputStream = AmazonHttpClient.getFileFromAmazonws(url, list, status);
				
				if(inputStream != null)
				{
					String modifiedFileName=writeIntoFileSystem(uploadManual.getId(), uploadManual.getUpload_file_name(), inputStream);
					EdloreBoxAPIConnection boxAPIConnection = new EdloreBoxAPIConnection();
					
					uploadToBox(boxAPIConnection.getAppUserConnection(),"7728640417",modifiedFileName,assetId);
					status.setStatus("200");
					status.setMessage("success");
					list.add(status);
				}
			}
		// If request is successfully processed then the success response will return 
		return Response.status(200).entity(list).build();
	}


/*	
	
	
	 * method define upload file to a folder by calling 
	 * the uploadFile(InputStream, String) method.
	 
	public void uploadToBox(BoxDeveloperEditionAPIConnection api, String folderId, String uploadFileName){
		System.out.println("inside uplaod file method");
		
		
		//--------- uploading the files to folder code starts ----------
		
		// Creating box object for Assets folder
        BoxFolder boxFolder = new BoxFolder(api, folderId);
		
		
		//System.out.println("GetInfo:::"+folder.getInfo());
		try {
			
			//BoxFolder boxFolder2 = new BoxFolder(api, existedItemId);
						
			FileInputStream fileInputStream = new FileInputStream("C:\\edlorefolder\\"+uploadFileName);
			System.out.println("uploading the file into the folder ::"+boxFolder.getInfo().getName());
			boxFolder.uploadFile(fileInputStream, uploadFileName);
			
			fileInputStream.close();
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	*/
	
	/*
	 * method define upload file to a folder by calling 
	 * the uploadFile(InputStream, String) method.
	 */
	public static void uploadToBox(BoxDeveloperEditionAPIConnection api, String folderId,String uploadFileName,String assertId){
		System.out.println("inside uploadToBox file method");
		//--------- uploading the files to folder code starts ----------
		
		// Creating box object for Assets folder
        BoxFolder boxFolder = new BoxFolder(api, folderId);
		
		System.out.println("GetInfo:::"+boxFolder.getInfo());
		try {
			
			BoxFolder.Info parentFolderInfo=boxFolder.getInfo();
			
			Iterable<Info> list =parentFolderInfo.getResource().getChildren();
			
			Iterator iterator= list.iterator();
			BoxFolder.Info folderInfo=null;
			String itemId=null;
			String existedItemId=null;
			boolean folderExists=false;
			//Path p = Paths.get(path);
				while (iterator.hasNext()) {
				BoxFolder.Info item = (BoxFolder.Info) iterator.next();
				//System.out.println("folder name::"+item.getName()+"folderid:::"+item.getID());
				//System.out.println("assertId.equals(item.getName()):::"+assertId.equals(item.getName()) + "assertId:::"+assertId+";;;;item.getName():::"+item.getName());
				if(assertId.equals(item.getName())){
					itemId=item.getID();
					folderExists=true;
					System.out.println("folder already exists and breaking up the loop");
					break;
				}
				
			}
				BoxFolder folder =null;
				BoxFile.Info uploadedFileInfo = null;
				Metadata fileMetadata=null;
				Metadata customMetadata=null;
				if(folderExists){
					System.out.println("inside if where folder is already created and the id is ::"+itemId);
					 folder = new BoxFolder(api, itemId);
					
					
					BoxFolder.Info parentFolder=folder.getInfo();
					System.out.println("parent folder is :::"+parentFolder.getName());
					Iterable<Info> iterableList =parentFolder.getResource().getChildren();
					Iterator iterator1= iterableList.iterator();
					while (iterator1.hasNext()) {
						BoxFolder.Info item2 = (BoxFolder.Info) iterator1.next();
						System.out.println("folder name 1::"+item2.getName()+":::folderid 1:::"+item2.getID());
						System.out.println("assertId.equals(item.getName()) 1:::"+assertId.equals(item2.getName()) + ":::assertId 1:::"+assertId+";;;;item.getName() 1:::"+item2.getName());
						//System.out.println("path.contains(.pdf) 1::"+path.contains(".mp3")+":::pathh is 1::"+path);
						if(uploadFileName.contains(".pdf")){
							System.out.println("This is a PDF file");
							if("Manuals".equals(item2.getName())){
								//System.out.println("inside Manual folder");
								existedItemId=item2.getID();
								folderExists=true;
								System.out.println("folder already exists and breaking up the loop inside folders");
								break;
							}else{
								//System.out.println("inside else condition in PDF");
								folderInfo = folder.createFolder("Manuals");
								System.out.println("folder is created with Name ::"+folderInfo.getName()+" and Id is:::"+folderInfo.getID()+"::parent::"+folderInfo.getParent().getName());
								existedItemId=folderInfo.getID();
								break;
							}
							
						}
						else if(uploadFileName.contains(".docx")){
							System.out.println("This is a docx file");
							if("Works".equals(item2.getName())){
								System.out.println("inside Manuals folder");
								existedItemId=item2.getID();
								folderExists=true;
								System.out.println("folder already exists and breaking up the loop inside folders");
								break;
							}else{
								folderInfo = folder.createFolder("Works");
								System.out.println("folder is created with Name ::"+folderInfo.getName()+" and Id is:::"+folderInfo.getID()+"::parent::"+folderInfo.getParent().getName());
								existedItemId=folderInfo.getID();
								break;
							}
						}
						else if(uploadFileName.contains(".mp3")){
							System.out.println("This is a mp3 file");
							if("Music".equals(item2.getName())){
								System.out.println("inside Audio folder");
								existedItemId=item2.getID();
								folderExists=true;
								System.out.println("folder already exists and breaking up the loop inside folders");
								break;
							}else{
								System.out.println("inside else condition in Audio");
								folderInfo = folder.createFolder("Music");
								System.out.println("folder is created with Name ::"+folderInfo.getName()+" and Id is:::"+folderInfo.getID()+"::parent::"+folderInfo.getParent().getName());
								existedItemId=folderInfo.getID();
								break;
							}
						}
						else{
							System.out.println("This is a miscellaneous file");
							if("miscellaneous".equals(item2.getName())){
								System.out.println("inside Audio folder");
								existedItemId=item2.getID();
								folderExists=true;
								System.out.println("folder already exists and breaking up the loop inside folders");
								break;
							}else{
								System.out.println("inside else condition in Audio");
								folderInfo = folder.createFolder("miscellaneous");
								System.out.println("folder is created with Name ::"+folderInfo.getName()+" and Id is:::"+folderInfo.getID()+"::parent::"+folderInfo.getParent().getName());
								existedItemId=folderInfo.getID();
								break;
							}
						}
						
					}
					if(existedItemId != null){
						System.out.println("existedItemId:::"+existedItemId+":::itemId:::"+itemId);
						BoxFolder boxFolder2 = new BoxFolder(api, existedItemId);
						
						FileInputStream fileInputStream = new FileInputStream("C:\\edlorefolder\\"+uploadFileName);
						System.out.println("uploading the file into the folder ::"+boxFolder2.getInfo().getName());
						
						uploadedFileInfo=boxFolder2.uploadFile(fileInputStream, uploadFileName);
						fileInputStream.close();
						BoxFile file=uploadedFileInfo.getResource();
						System.out.println("file:::"+file.getID());
						Metadata metadata = new Metadata();
						metadata.add("/Asset Id", assertId);
						file.createMetadata(metadata);
					}
				}else{
					// creating a folder inside assets folder with assertId
				folderInfo = boxFolder.createFolder(assertId);
				System.out.println("folder is created with Name ::"+folderInfo.getName()+" and Id is:::"+folderInfo.getID()+"::parent::"+folderInfo.getParent().getName());
				//create fileInputStream object to read the file
				FileInputStream fileInputStream = new FileInputStream("C:\\edlorefolder\\"+uploadFileName);
				
				// Creating box object for AssertId folder
		        BoxFolder assertIdBoxFolder = new BoxFolder(api, folderInfo.getID());
		        
				BoxFolder.Info folderInfo1=null;
				if(uploadFileName.endsWith(".docx")){
					folderInfo1 = assertIdBoxFolder.createFolder("Works");
				}else if(uploadFileName.endsWith(".pdf")){
					folderInfo1 = assertIdBoxFolder.createFolder("Manuals");
				}else if(uploadFileName.endsWith(".mp3")){
					folderInfo1 = assertIdBoxFolder.createFolder("Music");
				}else{
					folderInfo1 = assertIdBoxFolder.createFolder("miscellaneous");
				}
				
				
				System.out.println("folder is created with Name ::"+folderInfo1.getName()+" and Id is:::"+folderInfo1.getID()+"::parent::"+folderInfo1.getParent().getName());
				// creating object for subfolder created above
				BoxFolder boxFolder1 = new BoxFolder(api, folderInfo1.getID());
//				Path p = Paths.get(path);
				//rootFolder.uploadFile(fileInputStream,"newUploadedFile.txt");
				System.out.println("uploading the file into the folder ::"+boxFolder1.getInfo().getName());
				uploadedFileInfo=boxFolder1.uploadFile(fileInputStream, uploadFileName);
				fileInputStream.close();
				System.out.println("File uploaded successfully with file Name:::"+uploadedFileInfo.getName()+";; and Id is::"+uploadedFileInfo.getID());
				BoxFile file=uploadedFileInfo.getResource();
				System.out.println("file:::"+file.getID());
				Metadata metadata = new Metadata();
				metadata.add("/Asset Id", assertId);
				file.createMetadata(metadata);
				//Metadata metadata=file.getMetadata();
				System.out.println("Metadata:::"+metadata.getID());
				System.out.println("type:::"+file.getMetadata().getTypeName());
				/*BoxFile boxFile = new BoxFile(api, uploadedFileInfo.getID());
				
				fileMetadata=boxFile.getInfo().getResource().getMetadata();
				System.out.println("metadata type:::"+fileMetadata.getTypeName());*/
				//customMetadata=fileMetadata.add("Asset Id", assertId);
				
				//boxFile.updateMetadata(customMetadata);
				
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	

	private List<UploadManual> parseTheJson(JSONArray jsonArray)
	{
		JSONObject jsonObject = null;
		List<UploadManual> listOfUploadManuals = null;
		UploadManual uploadManual = null;
		
		listOfUploadManuals = new ArrayList<UploadManual>();
		
			// iterate the json array
			for(int i=0; i < jsonArray.size(); i++)
			{
				uploadManual = new UploadManual();
				jsonObject =  (JSONObject)jsonArray.get(i);
				
				// set the all values to upload manuals
				uploadManual.setId(String.valueOf(jsonObject.get("id")));
				uploadManual.setUpload_file_name(String.valueOf(jsonObject.get("upload_file_name")));
				uploadManual.setResource_type(String.valueOf(jsonObject.get("resource_type")));
				uploadManual.setManual_url(String.valueOf(jsonObject.get("manual_url")));
				// add the upload manual to list
				listOfUploadManuals.add(uploadManual);
			}
			
		return listOfUploadManuals;
	}
	
	private String writeIntoFileSystem(String appender, String fileName, InputStream inputStream)
	{
		FileOutputStream fileOutputStream = null;
		String filePath = FileSystemConfig.FILE_PATH;
		
		
		String modifiedFileName = appender+"_"+String.valueOf(Calendar.getInstance().getTimeInMillis())+"_"+fileName;
		
		File file = new File(filePath+modifiedFileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			fileOutputStream = new FileOutputStream(file);
			 byte[] buffer = new byte[1024];
	           int bytesRead;
	           //read from is to buffer
	           while((bytesRead = inputStream.read(buffer)) !=-1){
	               fileOutputStream.write(buffer, 0, bytesRead);
	           }
	           inputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			try {
				fileOutputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return modifiedFileName;
	}
	
	
}
