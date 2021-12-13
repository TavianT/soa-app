package org.tavian.scc.soa;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;


public class ServiceUtils {
	public static final String ID_FILE = "unique_ids.txt";
	public static void requestUniqueIds() {
		HttpClient client = HttpClient.newHttpClient();
		
		Random rand = new Random();
		int min = rand.nextInt(100000,100000000);
		int max = min + 9999;
		String requestString = "https://www.random.org/sequences/?min=" + min + "&max=" + max + "&col=1&format=plain&rnd=new";
		System.out.println("requestString: " + requestString);
		
		HttpRequest request = HttpRequest.newBuilder(
				URI.create(requestString))
				.header("accept", "text/plain")
				.build();
		
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			System.out.println(response.body());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
