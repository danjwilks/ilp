package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Drone {
	
	private SensorReader sensorReader;
	private DroneRecords droneRecords;
	
	public Drone(DroneLocation startEndLocation, String date) {
		this.sensorReader = new SensorReader();
		this.droneRecords = new DroneRecords(date);
	}
	
	private void recordSensorDetails(Sensor sensor) {
		var sensorInformation = sensorReader.read(sensor);
		droneRecords.addSensorInformation(sensorInformation);
	}
	
	private void recordDronePath(DroneLocation source, DroneLocation sink) {
		droneRecords.addPath(source, sink);
	}

	private void moveToNextDroneLocation(DroneLocation source, DroneLocation sink) {
		recordDronePath(source, sink);
	}

	public void traverse(Route bestRoute) {
		
		recordUnvisitedSensors(bestRoute.getUnvisitedSensors());
		
		var droneLocationsToVisit = bestRoute.getDroneLocationsToVisit();
		
		for (int droneLocationToVisitIndex = 0; droneLocationToVisitIndex < droneLocationsToVisit.size() - 1; droneLocationToVisitIndex++) {
			var currentDroneLocation = droneLocationsToVisit.get(droneLocationToVisitIndex);
			var nextDroneLocation = droneLocationsToVisit.get(droneLocationToVisitIndex + 1);
			
			if (currentDroneLocation.isStart) { // delete later
				var point = Point.fromLngLat(currentDroneLocation.lon, currentDroneLocation.lat); 
				var feature = Feature.fromGeometry(point);
				droneRecords.features.add(feature);
			}
			
			moveToNextDroneLocation(currentDroneLocation, nextDroneLocation);
			currentDroneLocation = nextDroneLocation;
			if (currentDroneLocation.isNearSensor) {
				recordSensorDetails(currentDroneLocation.nearbySensor);
			}
		}
		
    	FeatureCollection fc = FeatureCollection.fromFeatures(droneRecords.features);
    	System.out.println("traversal: " + fc.toJson());
		
	}

	private void recordUnvisitedSensors(SensorCollection unvisitedSensors) {
		
		for (var sensor : unvisitedSensors.getSensors()) {
			droneRecords.addSensorInformation(sensorReader.getUnvisitedInfo(sensor));
		}
		
	}

	public DroneRecords getDroneRecords() {
		return droneRecords;
	}

}