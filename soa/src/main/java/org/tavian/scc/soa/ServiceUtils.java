package org.tavian.scc.soa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;

import org.tavian.scc.soa.models.ErrorMessage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


public class ServiceUtils {
	//TODO: THIS IS TEMP FILE PATH CHANGE LATER!!!
	public static final String ID_FILE = "C:\\Users\\Tavian\\Desktop\\unique_ids.txt";
	public static final String JSON_FOLDER = "C:\\Users\\Tavian\\Desktop\\json_files\\";
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
}
