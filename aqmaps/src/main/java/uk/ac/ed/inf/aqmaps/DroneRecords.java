package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class DroneRecords {
	
	String date;
	List<Feature> features;
	List<String> flightPathTextFile;
	int lineCount = 0;
	
	public DroneRecords(String date) {
		this.date = date;
		this.features = new ArrayList<>();
		this.flightPathTextFile = new ArrayList<>();
	}
	
	public Feature buildPathFeature(DroneLocation source, DroneLocation sink) {
		var sourcePoint = Point.fromLngLat(source.lon, source.lat);
		var sinkPoint = Point.fromLngLat(sink.lon, sink.lat);
		var pathpoints = Arrays.asList(sourcePoint, sinkPoint);
		var lineString = LineString.fromLngLats(pathpoints);
		var feature = Feature.fromGeometry(lineString);
		return feature;
	}
	
	public String buildTextFileLine(DroneLocation source, DroneLocation sink) {
		
		String textFileLine = "";
		
		String nearBySensorLocation = "null";
		if (sink.isNearSensor) { 
			nearBySensorLocation = sink.nearbySensor.location;
		}
		
		var directionDegree = source.calcAngleTo(sink);
		
		textFileLine += lineCount                       + ",";
		textFileLine += String.valueOf(source.lon)      + ",";
		textFileLine += String.valueOf(source.lat)      + ",";
		textFileLine += String.valueOf(directionDegree) + ",";
		textFileLine += String.valueOf(sink.lon)        + ",";
		textFileLine += String.valueOf(sink.lat)        + ",";  
		textFileLine += nearBySensorLocation;
		
		return textFileLine;
		
	}
	
	public void addPath(DroneLocation source, DroneLocation sink) {
		
		lineCount++;
		
		var pathFeature = buildPathFeature(source, sink);
		features.add(pathFeature);
		
		var pathTextFileLine = buildTextFileLine(source, sink);
		flightPathTextFile.add(pathTextFileLine);
		
	}
	
	public void addProperties(Feature sensorFeature, SensorInformation reading) {
		
		sensorFeature.addStringProperty("location", reading.getLocation());
		sensorFeature.addStringProperty("rgb-string", reading.getRgbString());
		sensorFeature.addStringProperty("marker-color", reading.getMarkerColor());
		if (!reading.getMarkerSymbol().equals("")) {
			sensorFeature.addStringProperty("marker-symbol", reading.getMarkerSymbol());
		}
		
	}
	
	public void addSensorInformation(SensorInformation reading) {
		
		var sensorPoint = Point.fromLngLat(reading.getLongitude(), reading.getLatitude());
		var sensorFeature = Feature.fromGeometry(sensorPoint);
		addProperties(sensorFeature, reading);
		features.add(sensorFeature);
	}

}
