package org.tavian.scc.soa.resources;

import java.util.Random;

import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.User;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("user")
public class UserResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserId() {
		User user = new User();
		//Temp code delete later
//		Random rand = new Random();
//		int lowerBound = 10000;
//		int upperBound = 20000;
//		int randId = rand.nextInt(lowerBound, upperBound);
		ServiceUtils.requestUniqueIds();
		user.setId(0);
		return user;
	}
}
