package com.edlore.box.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.EncryptionAlgorithm;
import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;
import com.box.sdk.JWTEncryptionPreferences;
//import java.nio.file.Paths;

public class EdloreBoxAPIConnection {

	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("com/edlore/resourses/applicationProperties");
	   private static final int MAX_DEPTH = 1;
	   private static final int MAX_CACHE_ENTRIES = 100;
	   
	   
	    public BoxDeveloperEditionAPIConnection getAppUserConnection(){
	    	BoxDeveloperEditionAPIConnection api = null;
	    	
		try {
			String privateKey = new String(Files.readAllBytes(Paths.get(resourceBundle.getString("PRIVATE_KEY_FILE"))));
		//	File file = new File(resourceBundle.getString("PRIVATE_KEY_FILE"));
			System.out.println("File created");
		//	byte[] bytes=read(file);
			
		//	String privateKey = new String(bytes);

	        JWTEncryptionPreferences encryptionPref = new JWTEncryptionPreferences();
	        encryptionPref.setPublicKeyID(resourceBundle.getString("PUBLIC_KEY_ID"));
	        encryptionPref.setPrivateKey(privateKey);
	        encryptionPref.setPrivateKeyPassword(resourceBundle.getString("PRIVATE_KEY_PASSWORD"));
	        encryptionPref.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256);
	        
	        System.out.println("Private Key :::"+privateKey);
	        //It is a best practice to use an access token cache to prevent unneeded requests to Box for access tokens.
	        //For production applications it is recommended to use a distributed cache like Memcached or Redis, and to
	        //implement IAccessTokenCache to store and retrieve access tokens appropriately for your environment.
	        IAccessTokenCache accessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES);
	        
	        System.out.println(":::resourceBundle.getString(USER_ID)::"+resourceBundle.getString("USER_ID"));
	        System.out.println(":::resourceBundle.getString(CLIENT_ID)::"+resourceBundle.getString("CLIENT_ID"));
	        System.out.println(":::resourceBundle.getString(CLIENT_SECRET)::"+resourceBundle.getString("CLIENT_SECRET"));
	         api = BoxDeveloperEditionAPIConnection.getAppUserConnection(resourceBundle.getString("USER_ID"), resourceBundle.getString("CLIENT_ID"),
	        		resourceBundle.getString("CLIENT_SECRET"), encryptionPref, accessTokenCache);
	        
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		return api;
	}
	    
	  
}
