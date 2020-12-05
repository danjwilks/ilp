package uk.ac.ed.inf.aqmaps;


import static uk.ac.ed.inf.aqmaps.Colours.*;
import static uk.ac.ed.inf.aqmaps.MarkerSymbols.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Drone {
	
	List<Feature> features;
	Set<Sensor> visitedSensors;
	DroneLocation startEndLocation;
	String outputFile;
	int lineCount;
	
	public Drone(DroneLocation startEndLocation, String date) {
		this.features = new ArrayList<Feature>();
		this.visitedSensors = new HashSet<>();
		this.startEndLocation = startEndLocation;
		this.outputFile = "flightpath-" + date + ".txt";
		this.lineCount = 1;

	}
	
	public String determineWhat3Words(Sensor sensor) {
		return sensor.location;
	}
	
	public String determineFeatureRgbString(Sensor sensor) {
		return getHexString(sensor);
	}
	
	public String determineMarkerColor(Sensor sensor) {
		return getHexString(sensor);
	}
	
	public String determineMarkerSymbol(Sensor sensor) {
		String markerSymbol = "";
		double tenPercent = 10.0;
		if (sensor.battery < tenPercent) {
			return CROSS;
		}
		double polutionLevel;
		try {
			polutionLevel = Double.parseDouble(sensor.reading);
		} catch (NumberFormatException e) {
			return CROSS;
		} catch (NullPointerException e) {
			return CROSS;
		}
		
		if (0 <= polutionLevel && polutionLevel < 128) {
			markerSymbol = LIGHTHOUSE;
		} else if (128 <= polutionLevel && polutionLevel < 256) {
			markerSymbol = DANGER;
		}
		return markerSymbol;
	}
	
	public String getHexString(Sensor sensor) {
		
		String hexString = "";
		double tenPercent = 10.0;
		if (sensor.battery < tenPercent) {
			return BLACK;
		}
		double polutionLevel = Double.parseDouble(sensor.reading);
		
		if (0 <= polutionLevel && polutionLevel < 32) {
			hexString = GREEN;
		} else if (32 <= polutionLevel && polutionLevel < 64) {
			hexString = MEDIUM_GREEN;
		} else if (64 <= polutionLevel && polutionLevel < 96) {
			hexString = LIGHT_GREEN;
		} else if (96 <= polutionLevel && polutionLevel < 128) {
			hexString = LIME_GREEN;
		} else if (128 <= polutionLevel && polutionLevel < 160) {
			hexString = GOLD;
		} else if (160 <= polutionLevel && polutionLevel < 192) {
			hexString = ORANGE;
		} else if (192 <= polutionLevel && polutionLevel < 224) {
			hexString = RED_ORANGE;
		} else if (224 <= polutionLevel && polutionLevel < 256) {
			hexString = RED;
		}
		return hexString;
	}
	
	public void addProperties(Feature feature, Sensor sensor) {
		
		var location = determineWhat3Words(sensor);
		var rgbString = determineFeatureRgbString(sensor);
		var markerColor = determineMarkerColor(sensor);
		var markerSymbol = determineMarkerSymbol(sensor);
		
		feature.addStringProperty("location", location);
		feature.addStringProperty("rgb-string", rgbString);
		feature.addStringProperty("marker-color", markerColor);
		if (!markerSymbol.equals("")) {
			feature.addStringProperty("marker-symbol", markerSymbol);
		}
		
	}
	
	public void recordSensorInfo(DroneLocation droneLocation) {
		var sensor = droneLocation.nearbySensor;
		var point = Point.fromLngLat(sensor.getLongitude(), sensor.getLatitude()); 
		var feature = Feature.fromGeometry(point);
		addProperties(feature, sensor);
		features.add(feature);
		visitedSensors.add(sensor);
		
	}
	
	public void addPathInfoToFeatures(DroneLocation source, DroneLocation sink) {
		var sourcePoint = Point.fromLngLat(source.lon, source.lat);
		var sinkPoint = Point.fromLngLat(sink.lon, sink.lat);
		List<Point> points = Arrays.asList(sourcePoint, sinkPoint);
		var lineString = LineString.fromLngLats(points);
		var feature = Feature.fromGeometry(lineString);
		features.add(feature);
	}
	
	public boolean droneLocationsAreEqual(DroneLocation d1, DroneLocation d2) {
		double lonDiff = Math.abs(d1.lon - d2.lon);
		double latDiff = Math.abs(d1.lat - d2.lat);
		
		if (lonDiff < 0.0001 && latDiff < 0.0001) {
			return true;
		}
		return false;
	}
	
	public boolean isStartEndLocation(DroneLocation droneLocation) {
		
		return droneLocationsAreEqual(startEndLocation, droneLocation);
		
	}
	
	public int findStart(List<DroneLocation> droneLocations) {
		for (int i = 0; i < droneLocations.size(); i++) {
			if (isStartEndLocation(droneLocations.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public List<SensorPath> reorderSensorPaths (List<SensorPath> originalEdges, int newStart) {
		
		var reorderedEdges = new ArrayList<SensorPath>();
		
		for (int i = newStart; i < originalEdges.size(); i++) {
			reorderedEdges.add(originalEdges.get(i));
		}
		
		for (int i = 0; i < newStart; i++) {
			reorderedEdges.add(originalEdges.get(i));
		}
		
		return reorderedEdges;
		
	}
	
	public List<DroneLocation> reorderDroneLocations (List<DroneLocation> originalVerticies, int newStart) {
		var reorderedEdges = new ArrayList<DroneLocation>();
		
		DroneLocation startEndLocation = originalVerticies.get(newStart);
		
		for (int i = newStart; i < originalVerticies.size() - 1; i++) {
			reorderedEdges.add(originalVerticies.get(i));
		}
		
		for (int i = 0; i < newStart; i++) {
			reorderedEdges.add(originalVerticies.get(i));
		}
		
		reorderedEdges.add(startEndLocation);
		
		return reorderedEdges;
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
	
	public void writePathToFile(DroneLocation source, DroneLocation sink, Boolean checkSinkForSensor) {
		
		try {
			var output = new BufferedWriter(new FileWriter(outputFile, true));
			
			String nearBySensorLocation = "null";
			if (checkSinkForSensor && sink.isNearSensor) { 
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
	
	private void addPathInfoToLogFile(DroneLocation currentDronePathSource, DroneLocation currentDronePathSink,
			DroneLocation trueSensorPathSink) {
		
		boolean currentNodeIsSinkNode = currentDronePathSink.equals(trueSensorPathSink);
		boolean checkForSensor = currentNodeIsSinkNode & currentDronePathSink.isNearSensor;
		writePathToFile(currentDronePathSource, currentDronePathSink, checkForSensor);
	}
	
	private void recordMovementDetails(DroneLocation currentDronePathSource, DroneLocation currentDronePathSink,
			DroneLocation trueSensorPathSink) {
		
		addPathInfoToFeatures(currentDronePathSource, currentDronePathSink);
		addPathInfoToLogFile(currentDronePathSource, currentDronePathSink, trueSensorPathSink);
		
		
	}

	public void moveToNextDroneLocation(DroneLocation trueSensorPathSource, DroneLocation trueSensorPathSink, SensorPath sensorPath) {
		
		var currentSensorPathSource = sensorPath.source;
		var edgeToTraverse = sensorPath.paths;
		
		
		if (edgeIsInReverseOrder(trueSensorPathSource, currentSensorPathSource)) {
			edgeToTraverse = reverseSensorPathEdges(edgeToTraverse);
			currentSensorPathSource = trueSensorPathSource;
		}
		
		var currentDronePathSource = trueSensorPathSource;
		
		for (var dronePath: edgeToTraverse) {
			var currentDronePathSink = currentDronePathSource.equals(dronePath.vertex1) ? dronePath.vertex2 : dronePath.vertex1;
			recordMovementDetails(currentDronePathSource, currentDronePathSink, trueSensorPathSink);
			currentDronePathSource = currentDronePathSink;
		}
	}

	public void traverse(GraphPath<DroneLocation, SensorPath> simpleSensorGraph) {
		
		var edges = simpleSensorGraph.getEdgeList();
		var verticies = simpleSensorGraph.getVertexList();
		
		int startIndex = findStart(verticies);
		var correctOrderSensorPaths = reorderSensorPaths(edges, startIndex);
		var correctOrderDroneLocations = reorderDroneLocations(verticies, startIndex);
		
		for (int droneLocationToVisitIndex = 0; droneLocationToVisitIndex < verticies.size() - 1; droneLocationToVisitIndex++) {
			var currentDroneLocation = correctOrderDroneLocations.get(droneLocationToVisitIndex);
			var nextDroneLocation = correctOrderDroneLocations.get(droneLocationToVisitIndex + 1);
			var dronePathsToTraverse = correctOrderSensorPaths.get(droneLocationToVisitIndex);
			
			if (currentDroneLocation.isStart) { // delete later
				var point = Point.fromLngLat(currentDroneLocation.lon, currentDroneLocation.lat); 
				var feature = Feature.fromGeometry(point);
				features.add(feature);
			}
			
			moveToNextDroneLocation(currentDroneLocation, nextDroneLocation, dronePathsToTraverse);
			currentDroneLocation = nextDroneLocation;
			if (currentDroneLocation.isNearSensor) {
				recordSensorInfo(currentDroneLocation);
			}
		}
		
		
    	FeatureCollection fc = FeatureCollection.fromFeatures(features);
    	System.out.println("traversal: " + fc.toJson());
		
	}

}