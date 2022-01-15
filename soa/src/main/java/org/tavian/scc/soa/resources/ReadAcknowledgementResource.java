package org.tavian.scc.soa.resources;

import java.util.ArrayList;
import java.util.List;

import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.messagequeues.Subscriber;
import org.tavian.scc.soa.models.Acknowledgement;
import org.tavian.scc.soa.models.Intent;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("read-acknowledgements")
public class ReadAcknowledgementResource {
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Acknowledgement> getAcknowledgements(@QueryParam("userId") int userId) {
		List<Acknowledgement> acks = new ArrayList<Acknowledgement>();
		Subscriber subscriber = new Subscriber();
		subscriber.setTopic("acknowledgements_" + String.valueOf(userId));
		subscriber.consumeAcknowledgements(userId);
		acks = ServiceUtils.getAcks(userId);
		return acks;
	}
}
