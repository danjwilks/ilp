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
	private static HttpClient client;
	/**
	 * The server URL that we will send requests to.
	 */
	private static final String SERVER = "http://localhost:";
	/**
	 * URL to retrieve geojson on no fly zones.
	 */
	private static final String NO_FLY_ZONES_SUFFIX = "/buildings/no-fly-zones.geojson";
	/**
	 * URL prefix to retrieve sensors json to visit.
	 */
	private static final String MAPS_PREFIX = "/maps/";
	/**
	 * URL suffix to retrieve sensors json to visit.
	 */
	private static final String MAPS_SUFFIX = "/air-quality-data.json";
	/**
	 * URL prefix to retrieve a what3Words json string.
	 */
	private static final String WHAT3WORDS_PREFIX = "/words/";
	/**
	 * URL suffix to retrieve a what3Words json string.
	 */
	private static final String WHAT3WORDS_SUFFIX = "/details.json";
	/**
	 * The port number that will be used for access the server.
	 */
	private int portNumber;
	
	public WebClient(int portNumber) {
		client = HttpClient.newHttpClient();
		this.portNumber = portNumber;
	}
	
	/**
	 * Gets a json string from a given URL string.
	 * 
	 * @param urlString to request data. 
	 * @return the retrieved json data.
	 */
	private String getJsonString(String urlString) {
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
	private String buildSensorsURL(String day, String month, String year) {
		return SERVER + portNumber + MAPS_PREFIX + year + "/" + month + "/" + day + MAPS_SUFFIX;
	}
	
	/**
	 * Builds the URL to get the what3Words json.
	 * 
	 * @param words
	 * @return the URL to get the what3Wrods json.
	 */
	private String buildWhat3WordsURL(String words) {
		return SERVER + portNumber + WHAT3WORDS_PREFIX + words + WHAT3WORDS_SUFFIX;
	}
	
	/**
	 * @param words
	 * @return the what3Words json string.
	 */
	public String getWhat3WordsJsonString(String words) {
		return getJsonString(buildWhat3WordsURL(words));
	}
	
	/**
	 * @param day
	 * @param month
	 * @param year
	 * @return the sensors json string.
	 */
	public String getSensorsJsonString(String day, String month, String year) {
		return getJsonString(buildSensorsURL(day, month, year));
	}
	
	private String buildNoFlyZonesURL() {
		return SERVER + portNumber + NO_FLY_ZONES_SUFFIX;
	}
	
	/**
	 * @return the no fly zones json string.
	 */
	private String getNoFlyZonesJsonString() {
		return getJsonString(buildNoFlyZonesURL());
	}
	
	/**
	 * Retrieves from the web client the no fly zones
	 * 
	 * @return a collection of no fly zones
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public NoFlyZoneCollection getNoFlyZoneCollection() throws IOException, InterruptedException {
		String noFlyZoneJsonString = getNoFlyZonesJsonString();
		var noFlyZoneCollection = NoFlyZoneCollection.fromJsonString(noFlyZoneJsonString);
		return noFlyZoneCollection;
	}
	
	/**
	 * Retrieves from the web client the what3words 
	 * location for the given sensor.
	 * 
	 * @param  sensor
	 * @return the what3Words corresponding to the given
	 * 		   sensor
	 */
	private What3Words getThreeWordLocation(Sensor sensor) {
		String words = sensor.getLocation().replaceAll("\\.", "/");
		String what3WordsJsonString = getWhat3WordsJsonString(words);
		What3Words what3Words = What3Words.fromJsonString(what3WordsJsonString);
		return what3Words;
		
	}
	
	/**
	 * Retrieves from web client the sensors to visit
	 * on the given date. 
	 * 
	 * @param  day
	 * @param  month
	 * @param  year
	 * @return a collection of sensors to visit
	 */
	public SensorCollection getSensorCollection(String day, String month, String year) {
		var sensorJsonString = getSensorsJsonString(day, month, year);
    	var sensorCollection = SensorCollection.fromJsonString(sensorJsonString);
    	for (var sensor : sensorCollection.getSensors()) {
    		What3Words what3Words = getThreeWordLocation(sensor);
    		sensor.setLongitude(what3Words.getCoordinates().getLongitude());
    		sensor.setLatitude(what3Words.getCoordinates().getLatitude());
    	}
    	return sensorCollection;
	}
}
