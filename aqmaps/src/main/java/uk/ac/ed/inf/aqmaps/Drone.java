package uk.ac.ed.inf.aqmaps;


import static uk.ac.ed.inf.aqmaps.Colours.*;
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
	
//	public void addSensorReading(List<Feature> features, Sensor nearbySensor) {
//		               
//		Feature sensorReading = Feature.fromGeometry(nearbySensor.getPoint());
//		sensorReading.addStringProperty("marker-symbol", "lighthouse");
//		sensorReading.addStringProperty("location", "slips.mass.baking");
//		sensorReading.addStringProperty("marker-color", "#00ff00");
//		sensorReading.addStringProperty("color", "#00ff00");
//		features.add(sensorReading);
//	}
	
	List<Feature> features;
	Set<Sensor> visitedSensors;
	
	public Drone() {
		this.features = new ArrayList<Feature>();
		this.visitedSensors = new HashSet<>();
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
		return "lighthouse";
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
		feature.addStringProperty("marker-symbol", markerSymbol);
		
	}
	
	public void gatherSensorReading(DroneLocation droneLocation) {
		for (var sensor: droneLocation.nearbySensors) {
			var point = Point.fromLngLat(sensor.getLongitude(), sensor.getLatitude()); 
			var feature = Feature.fromGeometry(point);
			addProperties(feature, sensor);
			features.add(feature);
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
	
	public void traverse(GraphPath<DroneLocation, SensorPath> simpleSensorGraph) {
		
		var edges = simpleSensorGraph.getEdgeList();
		var verticies = simpleSensorGraph.getVertexList();

		
		for (int droneNearSensorIndex = 0; droneNearSensorIndex < verticies.size() - 1; droneNearSensorIndex++) {
			gatherSensorReading(verticies.get(droneNearSensorIndex));
			moveToNextDroneLocation(edges.get(droneNearSensorIndex));
		}
		
//		for (var edge: edges) {
//			for (var droneLocation : edge.locations) {
//				if (droneLocation.isNearSensor) {
//					for (var sensor : droneLocation.nearbySensors) {
//						var point = Point.fromLngLat(sensor.getLongitude(), sensor.getLatitude()); 
//						var feature = Feature.fromGeometry(point);
//						features.add(feature);
//					}
//				}
//				
//			}
////			var sourceDroneLocation = edge.source;
////			var feature = Feature.fromGeometry(sourceDroneLocation.point);
////			features.add(feature);
//			
//		}
		
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