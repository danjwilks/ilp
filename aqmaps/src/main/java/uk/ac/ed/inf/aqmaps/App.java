package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;

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
	 * @param args 	{day, week, month, start longitude, start 
	 * 				latitude, start longitude, random seed,
	 * 				port number}.  
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
    	var webClient = new WebClient(portNumber); 
    	
    	try {
    		noFlyZones = webClient.getNoFlyZoneCollection();
    	} catch (Exception e) {
    		System.out.println("Could not get no fly zones.");
    		e.printStackTrace();
			System.exit(1);
    	}
    	try {
    		sensors = webClient.getSensorCollection(day, month, year);
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
    	
    	var drone = new Drone(date);
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