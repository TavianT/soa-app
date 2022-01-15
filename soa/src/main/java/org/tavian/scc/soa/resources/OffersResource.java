package org.tavian.scc.soa.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tavian.scc.soa.ServiceUtils;
import org.tavian.scc.soa.messagequeues.Subscriber;
import org.tavian.scc.soa.models.ErrorMessage;
import org.tavian.scc.soa.models.Offer;
import org.tavian.scc.soa.models.Proposal;
import org.tavian.scc.soa.models.Weather;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("offers")
public class OffersResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Offer> getOffers(@QueryParam("userId") int userId) {
		List<Offer> offers = new ArrayList<Offer>();
		Subscriber subscriber = new Subscriber();
		subscriber.setTopic("proposals");
		subscriber.consumeProposals(userId);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		Date todaysDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 14);
		Date dateFourteenDaysFromCurrent = calendar.getTime();
		List<Proposal> proposals = ServiceUtils.getProposals(userId);
		if(proposals.isEmpty()) {
			System.out.println("No proposals");
			return offers;
		}
		Iterator<Proposal> i = proposals.iterator();
		while(i.hasNext()) {
			Proposal proposal = i.next();
			try {
				Date tripDate = sdf.parse(proposal.getTripProposalDate());
				
				if(tripDate.before(todaysDate) || tripDate.after(dateFourteenDaysFromCurrent)) {
					i.remove();
					System.out.println("Invalid date on" + proposal.getLocation().getName() + "-" + proposal.getTripProposalDate());
				}
				
				if(proposal.getUserId() == userId) {
					i.remove();
					System.out.println("users own trip proposal");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(Proposal proposal : proposals) {
			String lat = proposal.getLocation().getLatitude();
			String lon = proposal.getLocation().getLongitude();
			try {
				Date tripDate = sdf.parse(proposal.getTripProposalDate());
				//calendar2.setTime(tripDate);
				String dateAsString = sdf2.format(tripDate);
				String jsonFileName = ServiceUtils.JSON_FOLDER + lat + "_" + lon + "_" + dateAsString + ".json";
				JSONObject weatherJson = null;
				File jsonFile = new File(jsonFileName);
				//check if file exists or exists and is empty
				if(jsonFile.isFile()) {
					weatherJson = new JSONObject(new String(Files.readAllBytes(Paths.get(jsonFileName))));
				} else {
					weatherJson = new JSONObject(ServiceUtils.getWeatherFromDate(lat, lon, dateAsString));
					if(weatherJson.isEmpty()) {
						ErrorMessage errorMessage = new ErrorMessage("Unable to get weather for " + proposal.getLocation().getName() + " Please Try again later.", 500);
						Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
								.entity(errorMessage)
								.build();
						throw new WebApplicationException(response);
					}
				}
				JSONArray weatherArr = weatherJson.getJSONObject("data").getJSONArray("weather");
				JSONObject weatherInfo = weatherArr.getJSONObject(0).getJSONArray("hourly").getJSONObject(4); //get weather a 12pm
				int temp = Integer.parseInt(weatherInfo.getString("tempC"));
				int windSpeed = Integer.parseInt(weatherInfo.getString("windspeedKmph"));
				String weatherDesc = weatherInfo.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
				Weather weather = new Weather();
				weather.setTemp(temp);
				weather.setWindSpeed(windSpeed);
				weather.setWeatherDesc(weatherDesc);
				System.out.println("Temperature: " + weather.getTemp());
				System.out.println("wind speed: " + weather.getWindSpeed());
				System.out.println("weather description" + weather.getWeatherDesc());
				Offer offer = new Offer();
				offer.setProposal(proposal);
				offer.setWeather(weather);
				offers.add(offer);
			} catch (ParseException | IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return offers;
		
	}
}
