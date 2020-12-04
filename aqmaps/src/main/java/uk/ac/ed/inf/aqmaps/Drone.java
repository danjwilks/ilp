package uk.ac.ed.inf.aqmaps;


import static uk.ac.ed.inf.aqmaps.Colours.*;
import static uk.ac.ed.inf.aqmaps.MarkerSymbols.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class Drone {
	
	List<Feature> features;
	Set<Sensor> visitedSensors;
	DroneLocation startEndLocation;
	
	public Drone(DroneLocation startEndLocation) {
		this.features = new ArrayList<Feature>();
		this.visitedSensors = new HashSet<>();
		this.startEndLocation = startEndLocation;
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
//		sensor.reading = null;
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
	
	public void gatherSensorReading(DroneLocation droneLocation) {
		for (var sensor: droneLocation.nearbySensors) {
			var point = Point.fromLngLat(sensor.getLongitude(), sensor.getLatitude()); 
			var feature = Feature.fromGeometry(point);
			addProperties(feature, sensor);
			features.add(feature);
			visitedSensors.add(sensor);
		}
		
	}
	
	public void moveToNextDroneLocation(SensorPath sensorPath) {
		for (var dronePath: sensorPath.paths) {
			var sourcePoint = Point.fromLngLat(dronePath.source.lon, dronePath.source.lat);
			var sinkPoint = Point.fromLngLat(dronePath.sink.lon, dronePath.sink.lat);
			List<Point> points = Arrays.asList(sourcePoint, sinkPoint);
			var lineString = LineString.fromLngLats(points);
			var feature = Feature.fromGeometry(lineString);
			features.add(feature);
		}
	}
	
	public boolean isStartEndLocation(DroneLocation droneLocation) {
		
		double lonDiff = Math.abs(droneLocation.lon - startEndLocation.lon);
		double latDiff = Math.abs(droneLocation.lat - startEndLocation.lat);
		
		if (lonDiff < 0.0001 && latDiff < 0.0001) {
			return true;
		}
		return false;
		
	}
	
	public int findStart(List<DroneLocation> droneLocations) {
		for (int i = 0; i < droneLocations.size(); i++) {
			if (isStartEndLocation(droneLocations.get(i))) {
				return i;
			}
		}
		return -1;
	}
	
	public List<SensorPath> reorderEdges (List<SensorPath> originalEdges, int newStart) {
		
		var reorderedEdges = new ArrayList<SensorPath>();
		
		for (int i = newStart; i < originalEdges.size(); i++) {
			reorderedEdges.add(originalEdges.get(i));
		}
		
		for (int i = 0; i < newStart; i++) {
			reorderedEdges.add(originalEdges.get(i));
		}
		
		return reorderedEdges;
		
	}
	
	public List<DroneLocation> reorderVerticies (List<DroneLocation> originalVerticies, int newStart) {
		var reorderedEdges = new ArrayList<DroneLocation>();
		
		for (int i = newStart; i < originalVerticies.size() - 1; i++) {
			reorderedEdges.add(originalVerticies.get(i));
		}
		
		for (int i = 0; i < newStart; i++) {
			reorderedEdges.add(originalVerticies.get(i));
		}
		
		return reorderedEdges;
	}
	
	public void traverse(GraphPath<DroneLocation, SensorPath> simpleSensorGraph) {
		
		var edges = simpleSensorGraph.getEdgeList();
		var verticies = simpleSensorGraph.getVertexList();
		
		int startIndex = findStart(verticies);
		var edgesFromStart = reorderEdges(edges, startIndex);
		var verticiesFromStart = reorderVerticies(verticies, startIndex);
		
		for (int droneLocationToVisitIndex = 0; droneLocationToVisitIndex < verticies.size() - 1; droneLocationToVisitIndex++) {
			var droneLocationToVisit = verticiesFromStart.get(droneLocationToVisitIndex);
			if (droneLocationToVisit.isStart) {
				var point = Point.fromLngLat(droneLocationToVisit.lon, droneLocationToVisit.lat); 
				var feature = Feature.fromGeometry(point);
				features.add(feature);
			} else { // is near a sensor
				gatherSensorReading(verticiesFromStart.get(droneLocationToVisitIndex));
			}
			moveToNextDroneLocation(edgesFromStart.get(droneLocationToVisitIndex));
		}
		
    	FeatureCollection fc = FeatureCollection.fromFeatures(features);
    	System.out.println(fc.toJson());
		
//		var lon = -3.192214965820312;
//    	var lat = 55.944009105332775;
//    	var point = Point.fromLngLat(lon, lat);
//    	var feature = Feature.fromGeometry(point);
//    	feature.addStringProperty("marker-symbol", "lighthouse");
//    	feature.addStringProperty("location", "slips.mass.baking");
//    	feature.addStringProperty("marker-color", "#00ff00");
//    	feature.addStringProperty("color", "#00ff00");
//    	

		
	}

}