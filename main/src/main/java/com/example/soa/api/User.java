package com.example.soa.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("user")
public class User {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createUser() {
		
	}
}
