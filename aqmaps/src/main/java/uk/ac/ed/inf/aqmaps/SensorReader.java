package uk.ac.ed.inf.aqmaps;

import static uk.ac.ed.inf.aqmaps.Colours.*;
import static uk.ac.ed.inf.aqmaps.MarkerSymbols.*;

public class SensorReader {
	
	private static final double MAX_BATTERY = 100.0;
	private static final double TEN_PERCENT_BATTERY = MAX_BATTERY / 10.0;
	
	public SensorInformation getUnvisitedInfo(Sensor sensor) {
		
		var location = readWhat3Words(sensor);
		
		SensorInformation reading = new SensorInformation.SensorInformationBuilder()
				.setLongitude(sensor.getLongitude())
				.setLatitude(sensor.getLatitude())
				.setLocation(location)
				.build();
		
		return reading;
		
	}
	
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
	
	private String readWhat3Words(Sensor sensor) {
		return sensor.getLocation();
	}
	
	private String readFeatureRgbString(Sensor sensor) {
		return determineHexString(sensor);
	}
	
	private String readMarkerColor(Sensor sensor) {
		return determineHexString(sensor);
	}
	
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
