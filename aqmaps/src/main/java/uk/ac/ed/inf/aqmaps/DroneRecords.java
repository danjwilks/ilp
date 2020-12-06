package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class DroneRecords {
	
	private String date;
	private List<Feature> features;
	private List<String> flightPathTextFile;
	private int lineCount = 0;
	
	public DroneRecords(String date) {
		this.date = date;
		this.features = new ArrayList<>();
		this.flightPathTextFile = new ArrayList<>();
	}
	
	private Feature buildPathFeature(DroneLocation source, DroneLocation sink) {
		var sourcePoint = Point.fromLngLat(source.getLongitude(), source.getLatitude());
		var sinkPoint = Point.fromLngLat(sink.getLongitude(), sink.getLatitude());
		var pathpoints = Arrays.asList(sourcePoint, sinkPoint);
		var lineString = LineString.fromLngLats(pathpoints);
		var feature = Feature.fromGeometry(lineString);
		return feature;
	}
	
	private String buildTextFileLine(DroneLocation source, DroneLocation sink) {
		
		String textFileLine = "";
		
		String nearBySensorLocation = "null";
		if (sink.getIsNearSensor()) { 
			nearBySensorLocation = sink.getNearbySensor().getLocation();
		}
		
		var directionDegree = source.calcAngleTo(sink);
		
		textFileLine += lineCount                             + ",";
		textFileLine += String.valueOf(source.getLongitude()) + ",";
		textFileLine += String.valueOf(source.getLatitude())  + ",";
		textFileLine += String.valueOf(directionDegree)       + ",";
		textFileLine += String.valueOf(sink.getLongitude())   + ",";
		textFileLine += String.valueOf(sink.getLatitude())    + ",";  
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

	public List<Feature> getFeatures() {
		return this.features;
	}

	public List<String> getFlightPathTextFile() {
		return this.flightPathTextFile;
	}

	public String getDate() {
		return this.date;
	}
	
}
