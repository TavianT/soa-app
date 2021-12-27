package org.tavian.scc.soa.resources;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.messagequeues.Publisher;
import org.tavian.scc.soa.models.ErrorMessage;
import org.tavian.scc.soa.models.Proposal;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("proposal")
public class ProposalResource {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProposal(Proposal proposal) {
		//debug
		System.out.println("userId: " + proposal.getUserId());
		System.out.println("tripProposalDate: " + proposal.getTripProposalDate());
		System.out.println("location: " + proposal.getLocation().getName());
		System.out.println("lon: " + proposal.getLocation().getLongitude());
		System.out.println("lat: " + proposal.getLocation().getLatitude());
		
		//error checks
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date tripDate = sdf.parse(proposal.getTripProposalDate());
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 14);
			Date dateFourteenDaysFromCurrent = calendar.getTime();
			Date currentDate = new Date();
			//check if proposal is within 14 days
			if (tripDate.after(dateFourteenDaysFromCurrent)) {
				ErrorMessage errorMessage = new ErrorMessage("Trip date is not within 14 days of current date. Please select a valid date.", 400);
				Response response = Response.status(Status.BAD_REQUEST)
						.entity(errorMessage)
						.build();
				throw new WebApplicationException(response);
			}
			if(tripDate.before(currentDate)) {
				ErrorMessage errorMessage = new ErrorMessage("Trip date is before current date. Please select a valid date.", 400);
				Response response = Response.status(Status.BAD_REQUEST)
						.entity(errorMessage)
						.build();
				throw new WebApplicationException(response);
			}
			//check if latitude is within -90 and 90
			if (Double.parseDouble(proposal.getLocation().getLatitude()) < -90 || Double.parseDouble(proposal.getLocation().getLatitude()) > 90) {
				ErrorMessage errorMessage = new ErrorMessage("Latitude invalid. Please input a correct", 400);
				Response response = Response.status(Status.BAD_REQUEST)
						.entity(errorMessage)
						.build();
				throw new WebApplicationException(response);
			}
			//check if longitude is within -180 and 180
			if (Double.parseDouble(proposal.getLocation().getLongitude()) < -180 || Double.parseDouble(proposal.getLocation().getLongitude()) > 180) {
				ErrorMessage errorMessage = new ErrorMessage("Longitutde invalid. Please input a correct", 400);
				Response response = Response.status(Status.BAD_REQUEST)
						.entity(errorMessage)
						.build();
				throw new WebApplicationException(response);
			}
			
			File idFile = new File(ServiceUtils.ID_FILE);
			//check if file exists or exists and is empty
			if(!idFile.isFile() || idFile.length() == 0) {
				ServiceUtils.requestUniqueIds();
			}
			int msgId = ServiceUtils.getUniqueId();
			if (msgId < 0) {
				ErrorMessage errorMessage = new ErrorMessage("Unable to generate msgId. Please Try again later.", 500);
				Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(errorMessage)
						.build();
				throw new WebApplicationException(response);
			}
			proposal.setMsgId(String.valueOf(msgId));
			
			System.out.println("msgId: " + proposal.getMsgId());
			
			//Publish message to message queue
			Publisher publisher = new Publisher();
			publisher.setTopic("proposals");
			publisher.publishProposal(proposal);
			
			return Response.status(Status.CREATED)
					.entity(proposal)
					.build();
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		ErrorMessage errorMessage = new ErrorMessage("Unable to submit proposal. Please Try again later.", 500);
		Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(errorMessage)
				.build();
		throw new WebApplicationException(response);
	}
}
