package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * @author S1851664
 *
 * Class to deal with API requests.
 */
public class WebClient {
	
	/**
	 * Client to handle api request.
	 */
	private static final HttpClient client = HttpClient.newHttpClient();
//	TODO port number
	/**
	 * URL to retrieve geojson on no fly zones.
	 */
	private static final String NO_FLY_ZONES_URL = "http://localhost:/buildings/no-fly-zones.geojson";
	/**
	 * URL prefix to retrieve sensors json to visit.
	 */
	private static final String MAPS_PREFIX = "http://localhost/maps/";
	/**
	 * URL suffix to retrieve sensors json to visit.
	 */
	private static final String MAPS_SUFFIX = "/air-quality-data.json";
	/**
	 * URL prefix to retrieve a what3Words json string.
	 */
	private static final String WHAT3WORDS_PREFIX = "http://localhost/words/";
	/**
	 * URL suffix to retrieve a what3Words json string.
	 */
	private static final String WHAT3WORDS_SUFFIX = "/details.json";
	
	/**
	 * Gets a json string from a given URL string.
	 * 
	 * @param urlString to request data. 
	 * @return the retrieved json data.
	 */
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
	
	/**
	 * Builds the URL to get sensor json.
	 * 
	 * @param day
	 * @param month
	 * @param year
	 * @return the URL to get the sensor json.
	 */
	private static String buildSensorsURL(String day, String month, String year) {
		return MAPS_PREFIX + year + "/" + month + "/" + day + MAPS_SUFFIX;
	}
	
	/**
	 * Builds the URL to get the what3Words json.
	 * 
	 * @param words
	 * @return the URL to get the what3Wrods json.
	 */
	private static String buildWhat3WordsURL(String words) {
		return WHAT3WORDS_PREFIX + words + WHAT3WORDS_SUFFIX;
	}
	
	/**
	 * @param words
	 * @return the what3Words json string.
	 */
	public static String getWhat3WordsJsonString(String words) {
		return getJsonString(buildWhat3WordsURL(words));
	}
	
	/**
	 * @param day
	 * @param month
	 * @param year
	 * @return the sensors json string.
	 */
	public static String getSensorsJsonString(String day, String month, String year) {
		return getJsonString(buildSensorsURL(day, month, year));
	}
	
	/**
	 * @return the no fly zones json string.
	 */
	public static String getNoFlyZonesJsonString() {
		return getJsonString(NO_FLY_ZONES_URL);
	}
}
