package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class App {
	
	private static final double ULLON = -3.192473;
	private static final double ULLAT = 55.946233;
	private static final double LRLON = -3.184319;
	private static final double LRLAT = 55.942617;
	private static final double[] FLYZONE = {ULLON, ULLAT, LRLON, LRLAT}; 
	
	private static final HttpClient client = HttpClient.newHttpClient();
	
	private static void validateArgs(String[] args) {
		
		if (args.length != 7) {
			throw new IllegalArgumentException("Wrong number of arguments. Expected 7 arguments.");
		}
		
		int day = -1;
		int month = -1;
		int year = -1;
		int randomSeed = -1;
		int portNumber = -1;
		
		try{
			day = Integer.parseInt(args[0]);
			month = Integer.parseInt(args[1]);
			year = Integer.parseInt(args[2]);
			randomSeed = Integer.parseInt(args[5]);
			portNumber = Integer.parseInt(args[6]);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Day, month, year, random seed and port number should be integer values.");
		}
		
		double xCoordinate = -1.0;
		double yCoordinate = -1.0;
		
		try {
			xCoordinate = Double.parseDouble(args[3]);
			yCoordinate = Double.parseDouble(args[4]);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("X or Y starting coordinate should be doubles.");
		}
		
		if (!dateIsValid(day, month, year)) {
			throw new DateTimeException("Day or month or year is invalid");
		}
		
		if (!coordinatesAreValid(xCoordinate, yCoordinate)) {
			throw new IllegalArgumentException("X and Y coordinate are invalid.");
		}
		
	}
	
	public static boolean coordinatesAreValid(double xCoordinate, double yCoordinate) {
		
		// TODO see if starting coordinates are within a no fly zone.
		
		return coordintesAreWithinBounds(xCoordinate, yCoordinate);
		
	}
	
	public static boolean coordintesAreWithinBounds(double xCoordinate, double yCoordinate) {
		
		// TODO see if coordinates are within boundaries.
		
		return true;
	}
	
	public static boolean dateIsValid(int day, int month, int year) {
		
	    boolean dateIsValid = true;
	    try {
	        LocalDate.of(year, month, day);
	    } catch (DateTimeException e) {
	        dateIsValid = false;
	    }
	    return dateIsValid;
	}
	
	public static FeatureCollection getBuildings() throws IOException, InterruptedException {
		
		String buildingsJSONString = getJSONString("http://localhost/buildings/no-fly-zones.geojson");
		return FeatureCollection.fromJson(buildingsJSONString);		
	}
	
	public static List<Sensor> getSensors(String day, String month, String year) {
		
		String sensorsJSONString = getJSONString("http://localhost/maps/" + year + "/" + month + "/" + day + "/air-quality-data.json");
		List<Sensor> sensors = buildSensors(sensorsJSONString);
		return sensors;
	}
	
	public static ThreeWordLocation getThreeWordLocation(Sensor sensor) {
		String words = sensor.location.replaceAll("\\.", "/");
		String threeWordLocationURL = "http://localhost/words/" + words + "/details.json";
		String locationJSONString = getJSONString(threeWordLocationURL);
		ThreeWordLocation threeWordLocation = buildThreeWordLocation(locationJSONString);
		return threeWordLocation;
		
	}
	
	public static ThreeWordLocation buildThreeWordLocation(String locationJSONString) {
		
		var threeWordLocation = new Gson().fromJson(locationJSONString, ThreeWordLocation.class);
		return threeWordLocation;
	}
	
	public static List<Sensor> buildSensors(String sensorsJSONString) {
		
		if (sensorsJSONString.equals("")) {
			return new ArrayList<>();
		}
		
		Type listType = new TypeToken<ArrayList<Sensor>>() {}.getType();
    	List<Sensor> sensors = new Gson().fromJson(sensorsJSONString, listType);
    	
    	for (var sensor : sensors) {
    		ThreeWordLocation location = getThreeWordLocation(sensor);
    		sensor.setLongitude(location.coordinates.lng);
    		sensor.setLatitude(location.coordinates.lat);
    	}
    	
    	return sensors;
	}
	
	public static String getJSONString(String urlString) {
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
	
    public static void main( String[] args ) throws IOException, InterruptedException {
    	
//    	args = 15 06 2021 55.9444 -3.1878 5678 80
    	
    	validateArgs(args);
    	
    	String day = args[0];
    	String month = args[1];
    	String year = args[2];
    	String date = day + "-" + month + "-" + year;
    	double startLatitude = Double.parseDouble(args[3]);
    	double startLongitude = Double.parseDouble(args[4]);
    	int randomSeed = Integer.parseInt(args[5]);
    	int portNumber = Integer.parseInt(args[6]);
    	

    	var buildings = getBuildings();
    	var sensors = getSensors(day, month, year);
    	DroneLocation startLocation = new DroneLocation(startLongitude, startLatitude);
    	
    	var bestRoute = new RouteBuilder()
    			.setFlyZone(FLYZONE)
    			.setBuildings(buildings)
    			.setSensors(sensors)
    			.setStartEndLocation(startLocation)
    			.buildBestRoute();
    	
    	var drone = new Drone(startLocation, date);
    	drone.traverse(bestRoute);
//    	
//    	var lon = -3.192214965820312;
//    	var lat = 55.944009105332775;
//    	var point = Point.fromLngLat(lon, lat);
//    	var feature = Feature.fromGeometry(point);
//    	feature.addStringProperty("marker-symbol", "lighthouse");
//    	feature.addStringProperty("location", "slips.mass.baking");
//    	feature.addStringProperty("marker-color", "#00ff00");
//    	feature.addStringProperty("color", "#00ff00");
//    	
//    	List<Feature> features = new ArrayList<Feature>();
//    	features.add(feature);
//    	FeatureCollection fc = FeatureCollection.fromFeatures(features);
//    	System.out.println(fc.toJson());
    	
//    	words = slips mass baking
//    	color = #00ff00
//    	marker-color = #00ff00
//    	marker-symbol = lighthouse, but what value?
    	
    	
    	
    	
    	
    }
    
    
    
//    markers have: 
    
//    location — the What3Words location string;
//    • rgb-string — the HTML colour of this marker represented as a hexadecimal Red-Green-Blue string;
//    • marker-color — identical to rgb-string, but will be rendered by http://geojson.io; and
//    • marker-symbol — a symbol from the Maki icon set (https://labs.mapbox.com/maki-icons/)
}
