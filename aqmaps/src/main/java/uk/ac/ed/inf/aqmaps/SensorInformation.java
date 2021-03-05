package uk.ac.ed.inf.aqmaps;

import static uk.ac.ed.inf.aqmaps.Colours.*;

/**
 * @author S1851664
 *
 * Class to represent the sensor information
 * gained from a sensor.
 */
public class SensorInformation {

	/**
	 * Longitude of corresponding sensor.
	 */
	private final double longitude;
	/**
	 * Latitude of corresponding sensor.
	 */
	private final double latitude;
	/**
	 * Location of corresponding sensor.
	 */
	private final String location;
	/**
	 * rgbString value of corresponding sensor.
	 */
	private final String rgbString;
	/**
	 * marker color of corresponding sensor
	 */
	private final String markerColor;
	/**
	 * marker color of corresponding sensor.
	 */
	private final String markerSymbol;

	/**
	 * Only called by a its builder object.
	 * 
	 * @param builder to build the class.
	 */
	private SensorInformation(SensorInformationBuilder builder) {

		this.longitude = builder.longitude;
		this.latitude = builder.latitude;
		this.location = builder.location;
		this.rgbString = builder.rgbString;
		this.markerColor = builder.markerColor;
		this.markerSymbol = builder.markerSymbol;
	}

	/**
	 * @return longitude of corresponding sensor.
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @return latitude of corresponding sensor.
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return location of corresponding sensor.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return rgb string of corresponding sensor.
	 */
	public String getRgbString() {
		return rgbString;
	}

	/**
	 * @return marker color of of corresponding sensor.
	 */
	public String getMarkerColor() {
		return markerColor;
	}

	/**
	 * @return marker symbol of corresponding sensor.
	 */
	public String getMarkerSymbol() {
		return markerSymbol;
	}

	/**
	 * @author S1851664
	 *
	 * Builder class to build sensor information.
	 */
	public static class SensorInformationBuilder {

		/**
		 * longitude of corresponding sensor.
		 */
		private double longitude;
		/**
		 * latitude of corresponding sensor.
		 */
		private double latitude;
		/**
		 * location of corresponding sensor.
		 */
		private String location;
		/**
		 * rgb string of corresponding sensor.
		 */
		private String rgbString;
		/**
		 * marker color of of corresponding sensor.
		 */
		private String markerColor;
		/**
		 * marker symbol of corresponding sensor.
		 */
		private String markerSymbol;

		/**
		 * Called when building a sensor information instance.
		 */
		public SensorInformationBuilder() {

			// defaulted to gray for when builder called with unvisited
			// sensors
			this.rgbString = GRAY;
			this.markerColor = GRAY;
			this.markerSymbol = "";

		}

		/**
		 * @param longitude of corresponding sensor.
		 * @return this builder object.
		 */
		public SensorInformationBuilder setLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		/**
		 * @param latitude of corresponding sensor.
		 * @return this builder object.
		 */
		public SensorInformationBuilder setLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		/**
		 * @param location of corresponding sensor.
		 * @return this builder object.
		 */
		public SensorInformationBuilder setLocation(String location) {
			this.location = location;
			return this;
		}

		/**
		 * @param rgbString of corresponding sensor.
		 * @return this builder object.
		 */
		public SensorInformationBuilder setRgbString(String rgbString) {
			this.rgbString = rgbString;
			return this;
		}

		/**
		 * @param markerColor of corresponding sensor.
		 * @return this builder object.
		 */
		public SensorInformationBuilder setMarkerColor(String markerColor) {
			this.markerColor = markerColor;
			return this;
		}

		/**
		 * @param markerSymbol of corresponding sensor.
		 * @return this builder object.
		 */
		public SensorInformationBuilder setMarkerSymbol(String markerSymbol) {
			this.markerSymbol = markerSymbol;
			return this;
		}

		/**
		 * @return a sensor information instance build with
		 *         the specified information contained
		 *         within this builder object. 
		 */
		public SensorInformation build() {
			SensorInformation sensorInformation = new SensorInformation(this);
			return sensorInformation;
		}

	}

}
