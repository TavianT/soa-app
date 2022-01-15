package org.tavian.scc.soa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tavian.scc.soa.models.Acknowledgement;
import org.tavian.scc.soa.models.Acknowledgements;
import org.tavian.scc.soa.models.ErrorMessage;
import org.tavian.scc.soa.models.Intent;
import org.tavian.scc.soa.models.Proposal;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;


public class ServiceUtils {
	//TODO: THIS IS TEMP FILE PATH CHANGE LATER!!!
	public static final String ROOT = "C:\\Users\\Tavian\\Desktop\\";
	public static final String ID_FILE = ROOT + "unique_ids.txt";
	public static final String JSON_FOLDER = ROOT + "json_files\\";
	public static final String PROPOSAL_FOLDER = ROOT + "proposals\\";
	public static final String INTENT_FOLDER = ROOT + "intents\\";
	public static final String ACKNOWLEDGEMENT_FOLDER = ROOT + "acknowledgements\\";
	
	
	public static void requestUniqueIds() {
		HttpClient client = HttpClient.newHttpClient();
		
		Random rand = new Random();
		int min = rand.nextInt(100000,100000000);
		int max = min + 999;
		String requestString = "https://www.random.org/sequences/?min=" + min + "&max=" + max + "&col=1&format=plain&rnd=new";
		System.out.println("requestString: " + requestString);
		
		HttpRequest request = HttpRequest.newBuilder(
				URI.create(requestString))
				.header("accept", "text/plain")
				.build();
		
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
			if(response.statusCode() == 200) {
				File idFile = new File(ID_FILE);
				idFile.createNewFile();
				FileWriter writer = new FileWriter(ID_FILE);
				writer.write(response.body());
				writer.close();
				System.out.println("Saved to file");
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorMessage errorMessage = new ErrorMessage("Unable to generate UserID. Please Try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
	}
	
	public static int getUniqueId() {
		int id = -1;
		
		HttpClient client = HttpClient.newHttpClient();
		
		Random rand = new Random();
		int min = rand.nextInt(100000,100000000);
		int max = min + 999;
		
		String requestString = "https://www.random.org/integers/?num=1&min=" + min + "&max="+ max + "&col=1&base=10&format=plain&rnd=new";
		System.out.println("requestString: " + requestString);
		
		HttpRequest request = HttpRequest.newBuilder(
				URI.create(requestString))
				.header("accept", "text/plain")
				.build();
		
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			System.out.println(response.body().trim());
			if(response.statusCode() == 200) {
				id = Integer.parseInt(response.body().trim());
			} else {
				id =  getIdFromFile();
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			id =  getIdFromFile();
			
		}
		return id;
	}
	
	private static int getIdFromFile() {
		int id = -1;
		try {
			File idFile = new File(ID_FILE);
			//TODO: THIS IS TEMP FILE PATH CHANGE LATER!!!
			File tempFile = new File("C:\\Users\\Tavian\\Desktop\\temp_file.txt");
			BufferedReader br = new BufferedReader(new FileReader(idFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

			String idString = br.readLine();
			id = Integer.parseInt(idString);
			String currentLine;
			while((currentLine = br.readLine()) != null) {
				bw.write(currentLine + System.getProperty("line.separator"));
			}
			bw.close();
			br.close();
			idFile.delete();
			tempFile.renameTo(idFile);
		} catch (IOException | NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorMessage errorMessage = new ErrorMessage("Unable to generate UserID. Please Try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
		return id;
	}
	
	public static String getWeatherFromDate(String lat, String lon, String date) {
		String requestString = "http://api.worldweatheronline.com/premium/v1/weather.ashx?key=3100d91cf2d844059fd220850211912&q=" + lat + "," + lon + "&format=json&date=" + date;
		String jsonFileName = lat + "_" + lon + "_" + date + ".json";
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder(
				URI.create(requestString))
				.header("accept", "application/json")
				.build();
		
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
			if(response.statusCode() == 200) {
				File jsonFile = new File(JSON_FOLDER + jsonFileName);
				jsonFile.createNewFile();
				FileWriter writer = new FileWriter(jsonFile);
				writer.write(response.body());
				writer.close();
				System.out.println("Saved to file: " + jsonFile);
				return response.body();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void addProposal(int userId, Proposal proposal) {
		final String PROPOSAL_FILE = String.valueOf(userId) + "_proposals.json";
		File proposalFile = new File(PROPOSAL_FOLDER + PROPOSAL_FILE);
		ObjectMapper mapper = new ObjectMapper();
		List<Proposal> proposals;
		//check if file exists or exists and is empty
		if(!proposalFile.isFile() || proposalFile.length() == 0) {
			proposals = new ArrayList<Proposal>();
			proposals.add(proposal);
			try {
				mapper.writeValue(proposalFile, proposals);
			} catch (StreamWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatabindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				proposals = mapper.readValue(proposalFile, new TypeReference<List<Proposal>>(){});
				proposals.add(proposal);
				mapper.writeValue(proposalFile, proposals);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static List<Proposal> getProposals(int userId)
	{
		final String PROPOSAL_FILE = String.valueOf(userId) + "_proposals.json";
		File proposalFile = new File(PROPOSAL_FOLDER + PROPOSAL_FILE);
		List<Proposal> proposals = null;
		if(!proposalFile.isFile() || proposalFile.length() == 0) {
			return null;
		} else {
			ObjectMapper mapper = new ObjectMapper();
			try {
				proposals = mapper.readValue(proposalFile, new TypeReference<List<Proposal>>(){});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return proposals;
	}
	
	public static void addIntent(int userId, Intent intent) {
		final String INTENT_FILE = String.valueOf(userId) + "_intents.json";
		File intentFile = new File(INTENT_FOLDER + INTENT_FILE);
		ObjectMapper mapper = new ObjectMapper();
		List<Intent> intents;
		//check if file exists or exists and is empty
		if(!intentFile.isFile() || intentFile.length() == 0) {
			intents = new ArrayList<Intent>();
			intents.add(intent);
			try {
				mapper.writeValue(intentFile, intents);
			} catch (StreamWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatabindException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				intents = mapper.readValue(intentFile, new TypeReference<List<Intent>>(){});
				intents.add(intent);
				mapper.writeValue(intentFile, intents);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static List<Intent> getIntents(int userId)
	{
		final String INTENT_FILE = String.valueOf(userId) + "_intents.json";
		File intentFile = new File(INTENT_FOLDER + INTENT_FILE);
		List<Intent> intents = null;
		if(!intentFile.isFile() || intentFile.length() == 0) {
			return null;
		} else {
			ObjectMapper mapper = new ObjectMapper();
			try {
				intents = mapper.readValue(intentFile, new TypeReference<List<Intent>>(){});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return intents;
	}
	
	public static void addAcknowledgement(int userId, Acknowledgement ack) {
		final String ACK_FILE = String.valueOf(userId) + "_acknowledgements.xml";
		File ackFile = new File(ACKNOWLEDGEMENT_FOLDER + ACK_FILE);
		Acknowledgements acks = new Acknowledgements();
		acks.setAcknowledgements(new ArrayList<Acknowledgement>());
		//check if file exists or exists and is empty
		if(!ackFile.isFile() || ackFile.length() == 0) {
			acks.getAcknowledgements().add(ack);
			try {
				JAXBContext jc = JAXBContext.newInstance(Acknowledgements.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(acks, ackFile);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				JAXBContext jc = JAXBContext.newInstance(Acknowledgements.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				acks = (Acknowledgements) unmarshaller.unmarshal(ackFile);
				acks.getAcknowledgements().add(ack);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(acks, ackFile);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static List<Acknowledgement> getAcks(int userId) {
		final String ACK_FILE = String.valueOf(userId) + "_acknowledgements.xml";
		File ackFile = new File(ACKNOWLEDGEMENT_FOLDER + ACK_FILE);
		Acknowledgements acks = new Acknowledgements();
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(Acknowledgements.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			acks = (Acknowledgements) unmarshaller.unmarshal(ackFile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorMessage errorMessage = new ErrorMessage("Unable to receive intents. Please try again later.", 500);
			Response response = Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
		return acks.getAcknowledgements();
	}
}
