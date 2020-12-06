package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class WebClient {
	
	private static final HttpClient client = HttpClient.newHttpClient();
//	TODO port number
	private static final String BUILDINGS_URL = "http://localhost:/buildings/no-fly-zones.geojson";
	private static final String MAPS_PREFIX = "http://localhost/maps/";
	private static final String MAPS_SUFFIX = "/air-quality-data.json";
	private static final String WHAT3WORDS_PREFIX = "http://localhost/words/";
	private static final String WHAT3WORDS_SUFFIX = "/details.json";
	
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
	
	private static String buildSensorsURL(String day, String month, String year) {
		return MAPS_PREFIX + year + "/" + month + "/" + day + MAPS_SUFFIX;
	}
	
	private static String buildWhat3WordsURL(String words) {
		return WHAT3WORDS_PREFIX + words + WHAT3WORDS_SUFFIX;
	}
	
	public static String getWhat3WordsJsonString(String words) {
		return getJsonString(buildWhat3WordsURL(words));
	}
	
	public static String getSensorsJsonString(String day, String month, String year) {
		return getJsonString(buildSensorsURL(day, month, year));
	}
	
	public static String getBuildingJsonString() {
		return getJsonString(BUILDINGS_URL);
	}
}
