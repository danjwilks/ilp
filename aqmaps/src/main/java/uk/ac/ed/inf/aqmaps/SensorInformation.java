package uk.ac.ed.inf.aqmaps;

import static uk.ac.ed.inf.aqmaps.Colours.*;

public class SensorInformation {
	
	private final double longitude;
	private final double latitude;
	private final String location;
	private final String rgbString;
	private final String markerColor;
	private final String markerSymbol;
	
	private SensorInformation(SensorInformationBuilder builder) {
		
		this.longitude = builder.longitude;
		this.latitude = builder.latitude;
        this.location = builder.location;
        this.rgbString = builder.rgbString;
        this.markerColor = builder.markerColor;
        this.markerSymbol = builder.markerSymbol;
    }
	
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public String getLocation() {
		return location;
	}
	public String getRgbString() {
		return rgbString;
	}
	public String getMarkerColor() {
		return markerColor;
	}
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	
	public static class SensorInformationBuilder {
		
		private double longitude;
		private double latitude;
		private String location;
        private String rgbString;
        private String markerColor;
        private String markerSymbol;
 
        public SensorInformationBuilder() {
        	
        	// defaulted to gray for when builder called with unvisited
        	// sensors
        	this.rgbString = GRAY;
        	this.markerColor = GRAY;
        	this.markerSymbol = "";
        	
        }
        public SensorInformationBuilder setLongitude(double longitude) {
        	this.longitude = longitude;
        	return this;
        }
        public SensorInformationBuilder setLatitude(double latitude) {
        	this.latitude = latitude;
        	return this;
        } 
        public SensorInformationBuilder setLocation(String location) {
    		this.location = location;
    		return this;
    	}
        public SensorInformationBuilder setRgbString(String rgbString) {
			this.rgbString = rgbString;
    		return this;
		}
    	public SensorInformationBuilder setMarkerColor(String markerColor) {
    		this.markerColor = markerColor;
    		return this;
    	}
    	
    	public SensorInformationBuilder setMarkerSymbol(String markerSymbol) {
    		this.markerSymbol = markerSymbol;
    		return this;
    	}
    	
    	public SensorInformation build() {
    		SensorInformation sensorInformation = new SensorInformation(this);
    		return sensorInformation;
    	}
		
	}

}
