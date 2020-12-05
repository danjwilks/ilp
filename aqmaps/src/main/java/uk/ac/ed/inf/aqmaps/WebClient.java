package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class WebClient {
	
	private static final HttpClient client = HttpClient.newHttpClient();
	private static final String BUILDINGSURL = "http://localhost/buildings/no-fly-zones.geojson";
	private static final String MAPSURL = "http://localhost/maps/";
	private static final String WHAT3WORDSURL = "http://localhost/words/";
	
	private static String getJsonString(String urlString) {
    	var request = HttpRequest.newBuilder()
    	.uri(URI.create(urlString))
    	.build();
    	HttpResponse<String> response;
		try {
			response = client.send(request, BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return response.body();
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getBuildingJsonString() {
		return getJsonString(BUILDINGSURL);
	}
	
	private static String buildSensorsURL(String day, String month, String year) {
		return MAPSURL + year + "/" + month + "/" + day + "/air-quality-data.json";
	}
	
	public static String getSensorsJsonString(String day, String month, String year) {
		return getJsonString(buildSensorsURL(day, month, year));
	}
	
	private static String buildWhat3WordsURL(String words) {
		return WHAT3WORDSURL + words + "/details.json";
	}
	
	public static String getWhat3WordsJsonString(String words) {
		return getJsonString(buildWhat3WordsURL(words));
	}

}
