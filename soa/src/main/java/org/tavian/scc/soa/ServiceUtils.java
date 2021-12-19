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


public class ServiceUtils {
	//TODO: THIS IS TEMP FILE PATH CHANGE LATER!!!
	public static final String ID_FILE = "C:\\Users\\Tavian\\Desktop\\unique_ids.txt";
	public static void requestUniqueIds() {
		HttpClient client = HttpClient.newHttpClient();
		
		Random rand = new Random();
		int min = rand.nextInt(100000,100000000);
		int max = min + 9;
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
		}
	}
	
	public static int getUniqueId() {
		try {
			File idFile = new File(ID_FILE);
			//TODO: THIS IS TEMP FILE PATH CHANGE LATER!!!
			File tempFile = new File("C:\\\\Users\\\\Tavian\\\\Desktop\\\\unique_ids.txttemp_file.txt");
			BufferedReader br = new BufferedReader(new FileReader(idFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

			String idString = br.readLine();
			int id = Integer.parseInt(idString);
			String currentLine;
			while((currentLine = br.readLine()) != null) {
				bw.write(currentLine + System.getProperty("line.separator"));
			}
			bw.close();
			br.close();
			idFile.delete();
			tempFile.renameTo(idFile);
			return id;
		} catch (IOException | NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
