package uk.ac.ed.inf.aqmaps;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Drone {
//	TODO plot sensors not visited
	Set<Sensor> visitedSensors;
	DroneLocation startEndLocation;
	String outputFile;
	int lineCount;
	SensorReader sensorReader;
	DroneRecords droneRecords;
	
	public Drone(DroneLocation startEndLocation, String date) {
		this.visitedSensors = new HashSet<>();
		this.startEndLocation = startEndLocation;
		this.outputFile = "flightpath-" + date + ".txt";
		this.lineCount = 1;
		this.sensorReader = new SensorReader();
		this.droneRecords = new DroneRecords();
	}
	
	public void recordSensorDetails(Sensor sensor) {
		
		var sensorReading = sensorReader.read(sensor);
		droneRecords.addSensorReading(sensorReading);
		
	}
	
	public void recordDronePath(DroneLocation source, DroneLocation sink) {
		
		droneRecords.addPath(source, sink);
		
	}
	
	public List<DronePath> reverseSensorPathEdges(List<DronePath> edges) {
		
		var reversed = new ArrayList<DronePath>();
		for (int i = edges.size() - 1; i >= 0; i--) {
			reversed.add(edges.get(i));
		}
		return reversed;
		
	}
	
	public boolean edgeIsInReverseOrder(DroneLocation startDroneLocation, DroneLocation currentSensorPathSource) {
		
		boolean isReversed = !startDroneLocation.equals(currentSensorPathSource);
		
		return isReversed;
		
	}
	
	public int calcDirectionDegree(DroneLocation source, DroneLocation sink) {
		
	    double angle = Math.toDegrees(Math.atan2(sink.lon - source.lon, sink.lat - source.lat));

	    if(angle < 0){
	        angle += 360;
	    }
	    
	    return (int) Math.round(angle);
		
	}
	
	public void writePathToFile(DroneLocation source, DroneLocation sink) {
		
		try {
			var output = new BufferedWriter(new FileWriter(outputFile, true));
			
			String nearBySensorLocation = "null";
			if (sink.isNearSensor) { 
				nearBySensorLocation = sink.nearbySensor.location;
			}
			
			var directionDegree = calcDirectionDegree(source, sink);
			
			output.append(lineCount                       + ",");
			output.append(String.valueOf(source.lon)      + ",");
			output.append(String.valueOf(source.lat)      + ",");
			output.append(String.valueOf(directionDegree) + ",");
			output.append(String.valueOf(sink.lon)        + ",");
			output.append(String.valueOf(sink.lat)        + ",");  
			output.append(nearBySensorLocation);
			output.newLine();
			output.close();
			
			lineCount++;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void addPathInfoToLogFile(DroneLocation source, DroneLocation sink) {
		
		writePathToFile(source, sink);
	}
	
	private void recordMovementDetails(DroneLocation source, DroneLocation sink) {
		
//		boolean currentNodeIsSinkNode = currentDronePathSink.equals(trueSensorPathSink);
//		boolean checkForSensor = currentNodeIsSinkNode & currentDronePathSink.isNearSensor;
		
		
		recordDronePath(source, sink);
//		TODO: merge below with above.
//		we need to add sensor location.
		addPathInfoToLogFile(source, sink);
		
		
	}

	public void moveToNextDroneLocation(DroneLocation source, DroneLocation sink) {
		
		recordMovementDetails(source, sink);
		
	}

	public void collectPollutionData(List<DroneLocation> bestRoute) {
		

		
		for (int droneLocationToVisitIndex = 0; droneLocationToVisitIndex < bestRoute.size() - 1; droneLocationToVisitIndex++) {
			var currentDroneLocation = bestRoute.get(droneLocationToVisitIndex);
			var nextDroneLocation = bestRoute.get(droneLocationToVisitIndex + 1);
			
			if (currentDroneLocation.isStart) { // delete later
				var point = Point.fromLngLat(currentDroneLocation.lon, currentDroneLocation.lat); 
				var feature = Feature.fromGeometry(point);
				droneRecords.features.add(feature);
			}
			
			moveToNextDroneLocation(currentDroneLocation, nextDroneLocation);
			if (currentDroneLocation.isNearSensor) {
				recordSensorDetails(currentDroneLocation.nearbySensor);
			}
			
		}
		
		
    	FeatureCollection fc = FeatureCollection.fromFeatures(droneRecords.features);
    	System.out.println("traversal: " + fc.toJson());
		
	}

}