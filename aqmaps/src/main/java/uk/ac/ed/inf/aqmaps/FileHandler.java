package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;

public class FileHandler {
	
	private static final String SENSOR_READINGS_FILENAME_PREFIX = "readings-";
	private static final String SENSOR_READINGS_FILE_EXSTENTION = ".geojson";
	private static final String FLIGHTPATH_FILENAME_PREFIX = "flightpath-";
	private static final String FLIGHTPATH_FILE_EXSTENTION = ".txt";
	
	public static void writeToFile(DroneRecords droneRecords) {
		
		recordFlightPath(droneRecords);
		recordSensorReadings(droneRecords);
		
	}

	private static void writeToFile(Path file, List<String> contents) {
		try {
			Files.write(file, contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String buildSensorReadingsFilePath(DroneRecords droneRecords) {
		return SENSOR_READINGS_FILENAME_PREFIX + droneRecords.date
				+ SENSOR_READINGS_FILE_EXSTENTION;
	}
	
	private static String buildFlightPathFilePath(DroneRecords droneRecords) {
		return FLIGHTPATH_FILENAME_PREFIX + droneRecords.date
		+ FLIGHTPATH_FILE_EXSTENTION;
	}
	
	private static void recordSensorReadings(DroneRecords droneRecords) {
		
		var featureCollection = FeatureCollection.fromFeatures(droneRecords.features);
		var contents = Arrays.asList(featureCollection.toJson());
		var sensorReadingsFilePath = buildSensorReadingsFilePath(droneRecords);
		Path file = Paths.get(sensorReadingsFilePath);
		
		writeToFile(file, contents);
		
	}

	private static void recordFlightPath(DroneRecords droneRecords) {
		
		var contents = droneRecords.flightPathTextFile;
		var flightPathFilePath = buildFlightPathFilePath(droneRecords);
		Path file = Paths.get(flightPathFilePath);
		
		writeToFile(file, contents);
		
	}

	
	
}


