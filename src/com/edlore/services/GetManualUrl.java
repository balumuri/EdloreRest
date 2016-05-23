package com.edlore.services;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.edlore.box.util.EdloreBoxAPIConnection;
import com.edlore.util.Asset;
import com.edlore.util.ListOfAsset;

/**
 * @author Naresh Duggena
 *
 */
@Path(value="/getmanuallink")
public class GetManualUrl {
	 private static final int MAX_DEPTH = 2;
	/**
	 * @param assetId
	 * @return response
	 */
	@GET
	@Path(value="/url")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_manual_url(@QueryParam("assetId") String assetId)
	{
		List<ListOfAsset> assetList=null;
		try{
			/*
			 *This Condition returns an error code 400, if the provided assetid is null 
			 */			
			if("".equals(assetId) || assetId==null){
				return Response.status(400).entity("Please provide valid Asset Id").build();
			}
			
			//Creating the object for EdloreBoxApiConnection to get the App user Connection.
			//this connection is required to perform content API operations
			EdloreBoxAPIConnection boxAPIConnection = new EdloreBoxAPIConnection();
			
			// This message is used to retrieve the files inside the Asset Id folder 
			//which takes parameters of BoxApIConnection and AssetId and returns a list of files
			assetList=retrieveAllFiles(boxAPIConnection.getAppUserConnection(), assetId);
		}catch (Exception e) {
			
			if(e instanceof BoxAPIException)
			{
				BoxAPIException exception = (BoxAPIException) e;
				
				return Response.status(exception.getResponseCode()).entity(exception.getMessage()).build();
			}
			
			System.out.println(e.getClass());
			return Response.status(404).entity(e.getMessage()).build();
		}
		// this will return a code of 200 and the list of AssetFolder info. i.e the files inside the assetid folder
		return Response.status(200).entity(assetList).build();
	}
	
	
	
	/**
	 * @param folder object,@param int depth
	 * @return ListOfAsset Object
	 */
    private ListOfAsset listFolder(BoxFolder folder, int depth) {
   	 int i=0;
		  String folderType="";
		  ListOfAsset listAssetObj=null;
		  ListOfAsset listAssetObj1=null;
		  List<Asset> assets=new ArrayList<Asset>();
       for (BoxItem.Info itemInfo : folder) {
       	if(listAssetObj==null){
       		listAssetObj=new ListOfAsset();
       	}
           if(depth>0){
       		folderType="File";
       	}else if(depth>1){
       		folderType="Child Folder";
       	}
           Asset asset = null;
           if(listAssetObj.getFolderName()==null){
           	listAssetObj.setFolderName(itemInfo.getName());
           }
           if("File".equals(folderType)){
       		asset = new Asset();
       		asset.setId(itemInfo.getID());
       		asset.setUpload_file_name(itemInfo.getName());
       		if(itemInfo.getParent() != null){
       			listAssetObj.setFolderName(itemInfo.getParent().getName());
       		}
       	}
       	assets.add(asset);
       	listAssetObj.setListAsset(assets);
           if (itemInfo instanceof BoxFolder.Info) {
               BoxFolder childFolder = (BoxFolder) itemInfo.getResource();
               if (depth < MAX_DEPTH) {
               	 listAssetObj1=listFolder(childFolder, depth + 1);
               }
           }
           i++;
       }
       if(listAssetObj1 !=null){
       	listAssetObj.setListAsset(listAssetObj1.getListAsset());
       }
       System.out.println("listAssetObj:::"+listAssetObj);
		return listAssetObj;
   }
   

	/**
	 * @param BoxDeveloperEditionAPIConnection object,@param assetId
	 * @return List object
	 */
   public List<ListOfAsset> retrieveAllFiles(BoxDeveloperEditionAPIConnection api, String assetId){
   	System.out.println("inside copy folder method");
   	
   	// Creating the instance of BoxFolder i.e Asset folder
   	BoxFolder boxFolder = new BoxFolder(api, "7728640417");
   	 List<ListOfAsset> assetList=new ArrayList<ListOfAsset>();
   	 Boolean isAssetId=false;
   	 // iterating the items avalilable inside the boxfolder
       for (BoxItem.Info itemInfo : boxFolder) {
    	   System.out.println("itemInfo.getName():::"+itemInfo.getName()+"::assetId::"+assetId);
    	   // This condition is used to check whether the given assetid is valid or not. If not valid throws an exception
       	if(assetId.equals(itemInfo.getName())){
       		// Checking whether the item is of instance BoxFolder.Info
       		if (itemInfo instanceof BoxFolder.Info) {
       			BoxFolder childFolder = (BoxFolder) itemInfo.getResource();
       			if (0 < MAX_DEPTH) {
       				ListOfAsset listOfAsset= listFolder(childFolder, 0 + 1);
       				assetList.add(listOfAsset);
       			}
       		}
       		isAssetId=true;
       	}
       }
       if(!isAssetId){
    	   throw new RuntimeException("cannot find given Asset Id..Please try with another assetId");
       }
       
       System.out.println("AssetList:::"+assetList);
	return assetList;
   }

}
