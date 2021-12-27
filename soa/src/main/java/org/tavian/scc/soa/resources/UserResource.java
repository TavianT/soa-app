package org.tavian.scc.soa.resources;

import java.io.File;
import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.messagequeues.Subscriber;
import org.tavian.scc.soa.models.ErrorMessage;
import org.tavian.scc.soa.models.User;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("user")
public class UserResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserId() {
		User user = new User();
		File idFile = new File(ServiceUtils.ID_FILE);
		//check if file exists or exists and is empty
		if(!idFile.isFile() || idFile.length() == 0) {
			ServiceUtils.requestUniqueIds();
		}
		int id = ServiceUtils.getUniqueId();
		if (id < 0) {
			ErrorMessage errorMessage = new ErrorMessage("Unable to get UserID. Please Try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
		user.setId(id);
		return Response.status(Status.OK)
				.entity(user)
				.build();
	}
}
