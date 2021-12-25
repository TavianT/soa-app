package org.tavian.scc.soa.resources;

import org.tavian.scc.soa.messagequeues.Publisher;
import org.tavian.scc.soa.models.Intent;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("create_intent")
public class CreateIntentResource {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createIntent(Intent intent) {
		Publisher publisher = new Publisher();
		publisher.setTopic(String.valueOf(intent.getProposalUserId()));
		publisher.publishIntent(intent);
		return Response.status(Status.CREATED)
				.entity(intent)
				.build();
	}
}
