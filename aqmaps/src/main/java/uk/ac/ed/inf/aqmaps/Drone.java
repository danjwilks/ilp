package uk.ac.ed.inf.aqmaps;

import java.util.List;

/**
 * @author S1851664
 * 
 * Drone that is able to collect sensor readings.
 */
public class Drone {
	
	private SensorReader sensorReader;
	/**
	 * Stores sensor readings and path information.
	 */
	private DroneRecords droneRecords;
	
	/**
	 * @param startEndLocation
	 * @param date when the drone is called to traverse.
	 */
	public Drone(String date) {
		this.sensorReader = new SensorReader();
		this.droneRecords = new DroneRecords(date);
	}
	
	/**
	 * Stores sensor readings to the drone records.
	 * 
	 * @param sensor
	 */
	private void recordSensorDetails(Sensor sensor) {
		var sensorInformation = sensorReader.read(sensor);
		droneRecords.addSensorInformation(sensorInformation);
	}
	
	/**
	 * Stores path information to the drone records.
	 * 
	 * @param source
	 * @param sink
	 */
	private void recordDronePath(DroneLocation source, DroneLocation sink) {
		droneRecords.addPath(source, sink);
	}

	/**
	 * Moves the drone from the given source location
	 * to the sink location and records the drone 
	 * path.
	 * 
	 * @param source
	 * @param sink
	 */
	private void moveToNextDroneLocation(DroneLocation source, DroneLocation sink) {
		recordDronePath(source, sink);
	}

	/**
	 * Makes drone traverse the given route and collect
	 * sensor information as it passes sensors and 
	 * record sensor information.
	 * 
	 * @param route the path for the drone to follow.
	 */
	public void traverse(Route route) {
		
		try { 
			recordUnvisitedSensors(route.getUnvisitedSensors());
		} catch (Exception e) {
			System.out.println("Could not record unvisited sensor locations.");
			e.printStackTrace();
		}
		
		List<DroneLocation> droneLocationsToVisit = null;
		
		try {
			droneLocationsToVisit = route.getDroneLocationsToVisit();
		} catch (Exception e) {
			System.out.println("Could not get drone locations from route.");
			e.printStackTrace();
			return;
		}
		
		for (int droneLocationToVisitIndex = 0; droneLocationToVisitIndex < droneLocationsToVisit.size() - 1; droneLocationToVisitIndex++) {
			var currentDroneLocation = droneLocationsToVisit.get(droneLocationToVisitIndex);
			var nextDroneLocation = droneLocationsToVisit.get(droneLocationToVisitIndex + 1);
			
			moveToNextDroneLocation(currentDroneLocation, nextDroneLocation);
			currentDroneLocation = nextDroneLocation;
			if (currentDroneLocation.getIsNearSensor()) {
				try {
					recordSensorDetails(currentDroneLocation.getNearbySensor());
				} catch (Exception e) {
					System.out.println("Could not record sensor information for current sensor.");
					e.printStackTrace();
				}
			}
		}
		
	}

	/**
	 * Records the unvisited sensors to drone records.
	 * 
	 * @param unvisitedSensors
	 */
	private void recordUnvisitedSensors(SensorCollection unvisitedSensors) {
		
		for (var sensor : unvisitedSensors.getSensors()) {
			droneRecords.addSensorInformation(sensorReader.buildUnvisitedInfo(sensor));
		}
		
	}

	/**
	 * @return the drones records.
	 */
	public DroneRecords getDroneRecords() {
		return droneRecords;
	}

}