package uk.ac.ed.inf.aqmaps;

import static uk.ac.ed.inf.aqmaps.Colours.*;
import static uk.ac.ed.inf.aqmaps.MarkerSymbols.*;

public class SensorReader {
	
	public SensorReading read(Sensor sensor) {
		
		var location = readWhat3Words(sensor);
		var rgbString = readFeatureRgbString(sensor);
		var markerColor = readMarkerColor(sensor);
		var markerSymbol = readMarkerSymbol(sensor);
		
		SensorReading reading = new SensorReading.SensorReadingBuilder()
				.setLongitude(sensor.getLongitude())
				.setLatitude(sensor.getLatitude())
				.setLocation(location)
				.setRgbString(rgbString)
				.setMarkerColor(markerColor)
				.setMarkerSymbol(markerSymbol)
				.build();
		
		return reading;
		
	}
	
	public String readWhat3Words(Sensor sensor) {
		return sensor.location;
	}
	
	public String readFeatureRgbString(Sensor sensor) {
		return determineHexString(sensor);
	}
	
	public String readMarkerColor(Sensor sensor) {
		return determineHexString(sensor);
	}
	
	public String readMarkerSymbol(Sensor sensor) {
		String markerSymbol = "";
		double tenPercent = 10.0;
		if (sensor.battery < tenPercent) {
			return CROSS;
		}
		double polutionLevel;
		try {
			polutionLevel = Double.parseDouble(sensor.reading);
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
	
	public String determineHexString(Sensor sensor) {
		
		String hexString = "";
		double tenPercent = 10.0;
		if (sensor.battery < tenPercent) {
			return BLACK;
		}
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
	

}
