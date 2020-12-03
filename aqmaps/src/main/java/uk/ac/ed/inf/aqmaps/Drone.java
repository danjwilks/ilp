package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

public class Drone {
	
	public static void addSensorReading(List<Feature> features, Sensor nearbySensor) {
		
		Feature sensorReading = Feature.fromGeometry(nearbySensor.point);
		sensorReading.addStringProperty("marker-symbol", "lighthouse");
		sensorReading.addStringProperty("location", "slips.mass.baking");
		sensorReading.addStringProperty("marker-color", "#00ff00");
		sensorReading.addStringProperty("color", "#00ff00");
		features.add(sensorReading);
		
	}
	
	public static void traverse(GraphPath<Sensor, SensorPath> bestRoute) {
		
		System.out.println(bestRoute);
		
		var vertices = bestRoute.getVertexList();
		var edges = bestRoute.getEdgeList();
		int i = 0;
		
		var features = new ArrayList<Feature>();
		
		for (var sensorPath : edges) {
			addSensorReading(features, sensorPath.source);
			
			for (var dronePath : sensorPath.paths) {
				features.add(Feature.fromGeometry(dronePath.lineString));
			}
			i++;
		}
		System.out.println("number of sensors visited: " + i);
		
		var fc = FeatureCollection.fromFeatures(features);
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
//    	List<Feature> features = new ArrayList<Feature>();
//    	features.add(feature);
//    	FeatureCollection fc = FeatureCollection.fromFeatures(features);
//    	System.out.println(fc.toJson());
		
	}

}