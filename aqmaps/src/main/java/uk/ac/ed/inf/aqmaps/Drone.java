package uk.ac.ed.inf.aqmaps;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Drone {
//	TODO plot sensors not visited
	Set<Sensor> visitedSensors;
	DroneLocation startEndLocation;
	String outputFile;
	SensorReader sensorReader;
	DroneRecords droneRecords;
	
	public Drone(DroneLocation startEndLocation, String date) {
		this.visitedSensors = new HashSet<>();
		this.startEndLocation = startEndLocation;
		this.outputFile = "flightpath-" + date + ".txt";
		this.sensorReader = new SensorReader();
		this.droneRecords = new DroneRecords(date);
	}
	
	public void recordSensorDetails(Sensor sensor) {
		
		var sensorReading = sensorReader.read(sensor);
		droneRecords.addSensorReading(sensorReading);
		
	}
	
	public void recordDronePath(DroneLocation source, DroneLocation sink) {
		
		droneRecords.addPath(source, sink);
		
	}

	public void moveToNextDroneLocation(DroneLocation source, DroneLocation sink) {
		recordDronePath(source, sink);
	}

	public void collectPollutionData(List<DroneLocation> droneLocationsToVisit) {
		
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

}