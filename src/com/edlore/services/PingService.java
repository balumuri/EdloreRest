package com.edlore.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Suresh K
 *
 * Used to test the either provider is working or not
 * a simple get method
 */
@Path(value="/ping")
public class PingService {

	
	@GET
	public Response ping()
	{
		return Response.status(200).entity("ping Successful....").build();
	}
	
}
