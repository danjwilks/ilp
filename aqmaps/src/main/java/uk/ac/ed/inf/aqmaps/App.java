package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;

public class App {
	
	private static void validateArgs(String[] args) {
		
		if (args.length != 7) {
			throw new IllegalArgumentException("Wrong number of arguments. Expected 7 arguments.");
		}
		
		int day = -1;
		int month = -1;
		int year = -1;
		
		try{
			day = Integer.parseInt(args[0]);
			month = Integer.parseInt(args[1]);
			year = Integer.parseInt(args[2]);
			Integer.parseInt(args[5]); // randomSeed
			Integer.parseInt(args[6]); // portNumber
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
	
	public static NoFlyZoneCollection getNoFlyZoneCollection() throws IOException, InterruptedException {
		String noFlyZoneJsonString = WebClient.getBuildingJsonString();
		var noFlyZoneCollection = NoFlyZoneCollection.fromJsonString(noFlyZoneJsonString);
		return noFlyZoneCollection;
	}
	
	public static SensorCollection getSensorCollection(String day, String month, String year) {
		var sensorJsonString = WebClient.getSensorsJsonString(day, month, year);
    	var sensorCollection = SensorCollection.fromJsonString(sensorJsonString);
    	for (var sensor : sensorCollection.getSensors()) {
    		What3Words what3Words = getThreeWordLocation(sensor);
    		sensor.setLongitude(what3Words.coordinates.lng);
    		sensor.setLatitude(what3Words.coordinates.lat);
    	}
    	return sensorCollection;
	}
	
	public static What3Words getThreeWordLocation(Sensor sensor) {
		String words = sensor.location.replaceAll("\\.", "/");
		String what3WordsJsonString = WebClient.getWhat3WordsJsonString(words);
		What3Words what3Words = What3Words.fromJsonString(what3WordsJsonString);
		return what3Words;
		
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
    	
    	var noFlyZones = getNoFlyZoneCollection();
    	var sensors = getSensorCollection(day, month, year);
    	
    	DroneLocation startLocation = new DroneLocation(startLongitude, startLatitude);
    	
    	var bestRoute = new RouteBuilder()
    			.setNoFlyZones(noFlyZones)
    			.setSensors(sensors)
    			.setStartEndLocation(startLocation)
    			.buildBestRoute();
    	
    	var drone = new Drone(startLocation, date);
    	drone.collectPollutionData(bestRoute);
    	
    	FileHandler.writeToFile(drone.droneRecords);
    	
    }
}