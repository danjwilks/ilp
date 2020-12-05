package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

public class App {
	
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
		
		try {
			Double.parseDouble(args[3]);
			Double.parseDouble(args[4]);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("X or Y starting coordinate should be doubles.");
		}
		
		if (!dateIsValid(day, month, year)) {
			throw new DateTimeException("Day or month or year is invalid");
		}
		
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
	
	public static List<Polygon> getBuildings() throws IOException, InterruptedException {
		
		String buildingsJsonString = WebClient.getBuildingJsonString();
		System.out.println("buildings: " + FeatureCollection.fromJson(buildingsJsonString).toJson());
		var features = FeatureCollection.fromJson(buildingsJsonString).features();
		var buildings = new ArrayList<Polygon>();
		for (var feature : features) {
			if (feature.geometry().getClass().equals(Polygon.class)) {
				buildings.add((Polygon) feature.geometry());
			}
		}
		return buildings;
	}
	
	public static List<Sensor> getSensors(String day, String month, String year) {
		String sensorsJSONString = WebClient.getSensorsJsonString(day, month, year);
		List<Sensor> sensors = buildSensors(sensorsJSONString);
		return sensors;
	}
	
	public static What3Words getThreeWordLocation(Sensor sensor) {
		String words = sensor.location.replaceAll("\\.", "/");
		String what3WordsJsonString = WebClient.getWhat3WordsJsonString(words);
		What3Words what3Words = What3Words.fromJsonString(what3WordsJsonString);
		return what3Words;
		
	}
	
	public static List<Sensor> buildSensors(String sensorsJSONString) {
		
		if (sensorsJSONString.equals("")) {
			return new ArrayList<>();
		}
		
		Type listType = new TypeToken<ArrayList<Sensor>>() {}.getType();
    	List<Sensor> sensors = new Gson().fromJson(sensorsJSONString, listType);
    	
    	for (var sensor : sensors) {
    		What3Words what3Words = getThreeWordLocation(sensor);
    		sensor.setLongitude(what3Words.coordinates.lng);
    		sensor.setLatitude(what3Words.coordinates.lat);
    	}
    	
    	return sensors;
	}
	
    public static void main( String[] args ) throws IOException, InterruptedException {
    	
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
    			.setBuildings(buildings)
    			.setSensors(sensors)
    			.setStartEndLocation(startLocation)
    			.buildBestRoute();
    	
    	var drone = new Drone(startLocation, date);
    	drone.collectPollutionData(bestRoute);
    	
    }
}