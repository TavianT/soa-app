package org.tavian.scc.soa.resources;

import java.util.ArrayList;
import java.util.List;

import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.messagequeues.Subscriber;
import org.tavian.scc.soa.models.Intent;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("read-intents")
public class ReadIntentResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Intent> getIntents(@QueryParam("userId") int userId) {
		List<Intent> intents = new ArrayList<Intent>();
		Subscriber subscriber = new Subscriber();
		subscriber.setTopic("intents_" + String.valueOf(userId));
		subscriber.consumeIntents(userId);
		intents = ServiceUtils.getIntents(userId);
		
		return intents;
	}
}
