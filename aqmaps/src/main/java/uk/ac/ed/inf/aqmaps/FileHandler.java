package uk.ac.ed.inf.aqmaps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;

/**
 * @author S1851664
 * 
 * Class to handle writing to file.
 */
public class FileHandler {

	private static final String SENSOR_READINGS_FILENAME_PREFIX = "readings-";
	private static final String SENSOR_READINGS_FILE_EXSTENTION = ".geojson";
	private static final String FLIGHTPATH_FILENAME_PREFIX = "flightpath-";
	private static final String FLIGHTPATH_FILE_EXSTENTION = ".txt";

	/**
	 * @param droneRecords the records to write to file.
	 */
	public static void writeToFile(DroneRecords droneRecords) {
		recordFlightPath(droneRecords);
		recordSensorReadings(droneRecords);
	}

	/**
	 * Writes given contents to given file path.
	 * 
	 * @param file path to write contents.
	 * @param contents information to write to file
	 *        path.
	 */
	private static void writeToFile(Path file, List<String> contents) {
		try {
			Files.write(file, contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the file path for the sensor readings.
	 * 
	 * @param  droneRecords information used to build 
	 *         file path.
	 * @return file path to write sensor readings.
	 */
	private static String buildSensorReadingsFilePath(DroneRecords droneRecords) {
		return SENSOR_READINGS_FILENAME_PREFIX + droneRecords.getDate() + SENSOR_READINGS_FILE_EXSTENTION;
	}

	/**
	 * Builds the file path for the flight path.
	 * 
	 * @param  droneRecords information used to build 
	 *         file path.
	 * @return file path to write flight path.
	 */
	private static String buildFlightPathFilePath(DroneRecords droneRecords) {
		return FLIGHTPATH_FILENAME_PREFIX + droneRecords.getDate() + FLIGHTPATH_FILE_EXSTENTION;
	}

	/**
	 * Writes drone records to file.
	 * 
	 * @param droneRecords containing information to
	 *        write to file.
	 */
	private static void recordSensorReadings(DroneRecords droneRecords) {

		var featureCollection = FeatureCollection.fromFeatures(droneRecords.getFeatures());
		var contents = Arrays.asList(featureCollection.toJson());
		var sensorReadingsFilePath = buildSensorReadingsFilePath(droneRecords);
		Path file = Paths.get(sensorReadingsFilePath);

		writeToFile(file, contents);

	}

	/**
	 * Writes drone flight path to file.
	 * 
	 * @param droneRecords containing information to 
	 *        write to file.
	 */
	private static void recordFlightPath(DroneRecords droneRecords) {

		var contents = droneRecords.getFlightPathTextFile();
		var flightPathFilePath = buildFlightPathFilePath(droneRecords);
		Path file = Paths.get(flightPathFilePath);

		writeToFile(file, contents);

	} 

}
