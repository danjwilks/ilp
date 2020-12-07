package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

/**
 * @author S1851664
 *
 */
public class DroneRecords {
	
	/**
	 * Date of the drone recording.
	 */
	private String date;
	
	/**
	 * The recorded drone features. Includes sensors
	 * and drone movements. 
	 */
	private List<Feature> features;
	
	/**
	 * The recorded drone movements as a list 
	 * of lines.
	 */
	private List<String> flightPathTextFile;
	
	/**
	 * The number of lines present in the text file.
	 */
	private int lineCount = 0;
	
	/**
	 * @param date of drone recording.
	 */
	public DroneRecords(String date) {
		this.date = date;
		this.features = new ArrayList<>();
		this.flightPathTextFile = new ArrayList<>();
	}
	
	/**
	 * Builds a feature of the path between two 
	 * drone locations.
	 * 
	 * @param source of the path.
	 * @param sink of the path.
	 * @return created feature of the path.
	 */
	private Feature buildPathFeature(DroneLocation source, DroneLocation sink) {
		var sourcePoint = Point.fromLngLat(source.getLongitude(), source.getLatitude());
		var sinkPoint = Point.fromLngLat(sink.getLongitude(), sink.getLatitude());
		var pathpoints = Arrays.asList(sourcePoint, sinkPoint);
		var lineString = LineString.fromLngLats(pathpoints);
		var feature = Feature.fromGeometry(lineString);
		return feature;
	}
	
	/**
	 * Builds text line for the drone path text file.
	 * 
	 * @param source of the drone path.
	 * @param sink of the drone path.
	 * @return the build text line.
	 */
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
	
	/**
	 * Records path specified by two drone locations.
	 * 
	 * @param source of the path.
	 * @param sink of the path.
	 */
	public void addPath(DroneLocation source, DroneLocation sink) {
		
		var pathFeature = buildPathFeature(source, sink);
		features.add(pathFeature);
		
		var pathTextFileLine = buildTextFileLine(source, sink);
		lineCount++;
		flightPathTextFile.add(pathTextFileLine);
		
	}
	
	/**
	 * Adds properties to the sensor reading feature.
	 * 
	 * @param sensorFeature feature to add properties to.
	 * @param reading to get properties from.
	 */
	private void addProperties(Feature sensorFeature, SensorInformation reading) {
		
		sensorFeature.addStringProperty("location", reading.getLocation());
		sensorFeature.addStringProperty("rgb-string", reading.getRgbString());
		sensorFeature.addStringProperty("marker-color", reading.getMarkerColor());
		if (!reading.getMarkerSymbol().equals("")) {
			sensorFeature.addStringProperty("marker-symbol", reading.getMarkerSymbol());
		}
		
	}
	
	/**
	 * Records the sensor information.
	 * 
	 * @param reading is the sensor information
	 *        to record.
	 */
	public void addSensorInformation(SensorInformation reading) {
		
		var sensorPoint = Point.fromLngLat(reading.getLongitude(), reading.getLatitude());
		var sensorFeature = Feature.fromGeometry(sensorPoint);
		addProperties(sensorFeature, reading);
		features.add(sensorFeature);
	}

	/**
	 * @return the list of features of the drone records.
	 */
	public List<Feature> getFeatures() {
		return this.features;
	}

	/**
	 * @return the flight path text file
	 */
	public List<String> getFlightPathTextFile() {
		return this.flightPathTextFile;
	}

	/**
	 * @return the date the drone recorded the 
	 *         information.
	 */
	public String getDate() {
		return this.date;
	}
	
}
