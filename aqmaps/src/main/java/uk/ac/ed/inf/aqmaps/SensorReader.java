package uk.ac.ed.inf.aqmaps;

import static uk.ac.ed.inf.aqmaps.Colours.*;
import static uk.ac.ed.inf.aqmaps.MarkerSymbols.*;

/**
 * @author S1851664
 *
 * Class representing a sensor reader. It can
 * read and interpret information from a sensor.
 */
public class SensorReader {
	
	/**
	 * The maximum battery reading of a sensor.
	 */
	private static final double MAX_BATTERY = 100.0;
	/**
	 * 10% of the maximum battery reading.
	 */
	private static final double TEN_PERCENT_BATTERY = MAX_BATTERY / 10.0;
	
	/**
	 * Builds information for a sensor that has not
	 * been visited.
	 * 
	 * @param sensor that has not been visited.
	 * @return the information for the unvisited 
	 * 		   sensor.
	 */
	public SensorInformation buildUnvisitedInfo(Sensor sensor) {
		
		var location = readWhat3Words(sensor);
		
		SensorInformation reading = new SensorInformation.SensorInformationBuilder()
				.setLongitude(sensor.getLongitude())
				.setLatitude(sensor.getLatitude())
				.setLocation(location)
				.build();
		
		return reading;
		
	}
	
	/**
	 * Reads sensor information from a given sensor.
	 * 
	 * @param sensor to read information from.
	 * @return the sensor information read from
	 *         the corresponding sensor.
	 */
	public SensorInformation read(Sensor sensor) {
		
		var location = readWhat3Words(sensor);
		var rgbString = readFeatureRgbString(sensor);
		var markerColor = readMarkerColor(sensor);
		var markerSymbol = readMarkerSymbol(sensor);
		
		SensorInformation reading = new SensorInformation.SensorInformationBuilder()
				.setLongitude(sensor.getLongitude())
				.setLatitude(sensor.getLatitude())
				.setLocation(location)
				.setRgbString(rgbString)
				.setMarkerColor(markerColor)
				.setMarkerSymbol(markerSymbol)
				.build();
		
		return reading;
		
	}
	
	/**
	 * Reads the what3Words from the sensor.
	 * 
	 * @param sensor to read from.
	 * @return what3words location read from sensor.
	 */
	private String readWhat3Words(Sensor sensor) {
		return sensor.getLocation();
	}
	
	/**
	 * Reads and interprets a rgb value 
	 * corresponding to the sensor.
	 * 
	 * @param sensor to read from.
	 * @return rgb string interpreted from the 
	 *         sensor.
	 */
	private String readFeatureRgbString(Sensor sensor) {
		return determineHexString(sensor);
	}
	
	/**
	 * Reads and interprets the marker color
	 * of the sensor.
	 * 
	 * @param sensor to read from.
	 * @return marker color interpreted from sensor.
	 */
	private String readMarkerColor(Sensor sensor) {
		return determineHexString(sensor);
	}
	
	/**
	 * Reads and interprets the marker symbol
	 * of the sensor.
	 * 
	 * @param sensor to read from.
	 * @return the marker symbol.
	 */
	private String readMarkerSymbol(Sensor sensor) {
		String markerSymbol = "";
		if (sensor.getBattery() < TEN_PERCENT_BATTERY) {
			return CROSS;
		}
		double polutionLevel;
		try {
			polutionLevel = Double.parseDouble(sensor.getReading());
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
	
	/**
	 * determines the corresponding hex string
	 * for a given sensor reading.
	 * 
	 * @param sensor to read from.
	 * @return corresponding hex string.
	 */
	private String determineHexString(Sensor sensor) {
		
		String hexString = "";
		double tenPercent = 10.0;
		if (sensor.getBattery() < tenPercent) {
			return BLACK;
		}
		double polutionLevel = Double.parseDouble(sensor.getReading());
		
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

}
