package org.tavian.scc.soa.resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
		//error checks
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
		try {
			Date tripDate = sdf.parse(proposal.getTripProposalDate());
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 14);
			Date dateFourteenDaysFromCurrent = calendar.getTime();
			if (tripDate.after(dateFourteenDaysFromCurrent)) {
				throw new WebApplicationException(Response.Status.BAD_REQUEST);
			}
			if (Integer.parseInt(proposal.getLocation().getLatitude()) < -90 && Integer.parseInt(proposal.getLocation().getLatitude()) > 90) {
				throw new WebApplicationException(Response.Status.BAD_REQUEST);
			}
			if (Integer.parseInt(proposal.getLocation().getLongitude()) < -180 && Integer.parseInt(proposal.getLocation().getLongitude()) > 180) {
				throw new WebApplicationException(Response.Status.BAD_REQUEST);
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
	}
}
