package com.edlore.config;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.edlore.services.DownloadService;
import com.edlore.services.GetManualUrl;
import com.edlore.services.PingService;



/**
 * @author Sowjanya B
 *
 * This configuration class will used by runtime rest servlet as long as project is
 * deployed the runtime rest servlet will read this class make available all resources
 * which are configured in this class otherwise rest servlet doesn't know about the 
 * resource classes and will not make it accessble
 * 
 */
public class EdloreApplicationConfig extends Application{

	 /** The default life cycle for resource class instances is per-request. 
	   *  The default life cycle for providers is singleton.
	   * */
	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(PingService.class);
		classes.add(com.edlore.services.UploadService.class);
		classes.add(DownloadService.class);
		classes.add(GetManualUrl.class);
		return classes;
	}

	/** Fields and properties of returned instances are injected with their declared
	  * dependencies (see Context) by the runtime prior to use.
	  * */ 
	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getSingletons()
	 */
	@Override
	public Set<Object> getSingletons() {
		
		return super.getSingletons();
	}
	
}
