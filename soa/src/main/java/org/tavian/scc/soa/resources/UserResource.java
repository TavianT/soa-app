package org.tavian.scc.soa.resources;

import java.io.File;
import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.models.User;

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
		File idFile = new File(ServiceUtils.ID_FILE);
		//check if file exists or exists and is empty
		if(!idFile.isFile() || idFile.length() == 0) {
			ServiceUtils.requestUniqueIds();
		}
		user.setId(ServiceUtils.getUniqueId());
		return user;
	}
}
