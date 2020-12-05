package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class DroneRecords {
	
	List<Feature> features;
	StringBuilder pathTextFile;
	
	public DroneRecords() {
		this.features = new ArrayList<>();
		this.pathTextFile = new StringBuilder();
	}
	
	public Feature buildPathFeature(DroneLocation source, DroneLocation sink) {
		var sourcePoint = Point.fromLngLat(source.lon, source.lat);
		var sinkPoint = Point.fromLngLat(sink.lon, sink.lat);
		var pathpoints = Arrays.asList(sourcePoint, sinkPoint);
		var lineString = LineString.fromLngLats(pathpoints);
		var feature = Feature.fromGeometry(lineString);
		return feature;
	}
	
	public String buildPathTextLine(DroneLocation source, DroneLocation sink) {
		
		return "";
		
	}
	
	public void addPath(DroneLocation source, DroneLocation sink) {
		
		var pathFeature = buildPathFeature(source, sink);
		features.add(pathFeature);
		
		var pathTextLine = buildPathTextLine(source, sink);
		pathTextFile.append(pathTextLine);
		
	}
	
	public void addProperties(Feature sensorFeature, SensorReading reading) {
		
		sensorFeature.addStringProperty("location", reading.getLocation());
		sensorFeature.addStringProperty("rgb-string", reading.getRgbString());
		sensorFeature.addStringProperty("marker-color", reading.getMarkerColor());
		if (!reading.getMarkerSymbol().equals("")) {
			sensorFeature.addStringProperty("marker-symbol", reading.getMarkerSymbol());
		}
		
	}
	
	public void addSensorReading(SensorReading reading) {
		
		var sensorPoint = Point.fromLngLat(reading.getLongitude(), reading.getLatitude());
		var sensorFeature = Feature.fromGeometry(sensorPoint);
		addProperties(sensorFeature, reading);
		features.add(sensorFeature);
	}

}
