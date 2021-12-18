package org.tavian.scc.soa.resources;

import org.tavian.scc.soa.models.Proposal;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("proposal")
public class ProposalResource {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createProposal(Proposal proposal) {
		
	}
}
