package org.tavian.scc.soa.resources;

import org.tavian.scc.soa.messagequeues.Publisher;
import org.tavian.scc.soa.models.Acknowledgement;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("create-acknowledgement")
public class CreateAcknowledgementResource {
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response createAcknowledgement(Acknowledgement acknowledgement) {
		System.out.println(acknowledgement.getMsgId());
		Publisher publisher = new Publisher();
		publisher.setTopic("acknowledgements_" + String.valueOf(acknowledgement.getIntentUserId()));
		publisher.publishAcknowledgement(acknowledgement);
		return Response.status(Status.CREATED)
				.entity(acknowledgement)
				.build();
	}
}
