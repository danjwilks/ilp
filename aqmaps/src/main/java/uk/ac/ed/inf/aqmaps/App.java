package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;

import com.mapbox.geojson.FeatureCollection;

/**
 * @author S1851664
 *
 * Runs the main program. 
 * 
 * Parses input, gets the best route to traverse and 
 * tells the drone to traverse said route then 
 * records the records from the drone traversal.
 */
public class App {
	
	/**
	 * Validates the input program arguments.
	 * 
	 * @param args 	day, week, month, start longitude, start 
	 * 				latitude, start longitude, random seed,
	 * 				port number.  
	 */
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
			Double.parseDouble(args[3]); // start longitude
			Double.parseDouble(args[4]); // start latitude
		} catch (NumberFormatException e) {
			throw new NumberFormatException("X or Y starting coordinate should be doubles.");
		}
		
		if (!dateIsValid(day, month, year)) {
			throw new DateTimeException("Day or month or year is invalid");
		}
		
	}
	
	/**
	 * Validates if the given date is a valid date.
	 * 
	 * @param  day
	 * @param  month
	 * @param  year
	 * @return true if date is valid
	 */
	private static boolean dateIsValid(int day, int month, int year) {
		
	    boolean dateIsValid = true;
	    try {
	        LocalDate.of(year, month, day);
	    } catch (DateTimeException e) {
	        dateIsValid = false;
	    }
	    return dateIsValid;
	}
	
	/**
	 * Retrieves from the web client the no fly zones
	 * 
	 * @return a collection of no fly zones
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static NoFlyZoneCollection getNoFlyZoneCollection() throws IOException, InterruptedException {
		String noFlyZoneJsonString = WebClient.getNoFlyZonesJsonString();
		var noFlyZoneCollection = NoFlyZoneCollection.fromJsonString(noFlyZoneJsonString);
		System.out.println("no fly zones: " + FeatureCollection.fromJson(noFlyZoneJsonString).toJson());
		return noFlyZoneCollection;
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
	private static SensorCollection getSensorCollection(String day, String month, String year) {
		var sensorJsonString = WebClient.getSensorsJsonString(day, month, year);
    	var sensorCollection = SensorCollection.fromJsonString(sensorJsonString);
    	for (var sensor : sensorCollection.getSensors()) {
    		What3Words what3Words = getThreeWordLocation(sensor);
    		sensor.setLongitude(what3Words.getCoordinates().getLongitude());
    		sensor.setLatitude(what3Words.getCoordinates().getLatitude());
    	}
    	return sensorCollection;
	}
	
	/**
	 * Retrieves from the web client the what3words 
	 * location for the given sensor.
	 * 
	 * @param  sensor
	 * @return the what3Words corresponding to the given
	 * 		   sensor
	 */
	private static What3Words getThreeWordLocation(Sensor sensor) {
		String words = sensor.getLocation().replaceAll("\\.", "/");
		String what3WordsJsonString = WebClient.getWhat3WordsJsonString(words);
		What3Words what3Words = What3Words.fromJsonString(what3WordsJsonString);
		return what3Words;
		
	}

	/**
	 * How the whole program is called.
	 * 	
	 * Parses input, gets the best route to traverse and 
	 * tells the drone to traverse said route then 
	 * records the records from the drone traversal.
	 * 
	 * 
	 * @param args 	day, week, month, start longitude, start 
	 * 				latitude, start longitude, random seed,
	 * 				port number. 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main( String[] args ) {
//    	TODO system.exit(1); 
		try {
			validateArgs(args);
		} catch (Exception e) {
			System.out.println("Arguments are not valid.");
			e.printStackTrace();
			System.exit(1);
		}
    	
    	String day = args[0];
    	String month = args[1];
    	String year = args[2];
    	String date = day + "-" + month + "-" + year;
    	double startLatitude = Double.parseDouble(args[3]);
    	double startLongitude = Double.parseDouble(args[4]);
    	int randomSeed = Integer.parseInt(args[5]);
    	int portNumber = Integer.parseInt(args[6]);
    	
    	NoFlyZoneCollection noFlyZones = null;
    	SensorCollection sensors = null;
    	Route route = null;
    	DroneLocation startLocation = new DroneLocation(startLongitude, startLatitude);
    	
    	try {
    		noFlyZones = getNoFlyZoneCollection();
    	} catch (Exception e) {
    		System.out.println("Could not get no fly zones.");
    		e.printStackTrace();
			System.exit(1);
    	}
    	try {
    		sensors = getSensorCollection(day, month, year);
    	} catch (Exception e) {
    		System.out.println("Could not get sensor to visit.");
    		e.printStackTrace();
			System.exit(1);
    	}
    	
    	try {
	    	route = new ChristofidesRoute.RouteBuilder()
	    			.setNoFlyZones(noFlyZones)
	    			.setAvailableSensors(sensors)
	    			.setStartEndLocation(startLocation)
	    			.buildBestRoute();
    	} catch (Exception e) {
    		System.out.println("Could not build route for drone.");
    		e.printStackTrace();
			System.exit(1);
    	}
    	
    	var drone = new Drone(startLocation, date);
    	drone.traverse(route);
    	
    	try { 
    		FileHandler.writeToFile(drone.getDroneRecords());
    	} catch (Exception e) {
    		System.out.println("Could not write drone records to file.");
    		e.printStackTrace();
			System.exit(1);
    	}
    	
    }
}