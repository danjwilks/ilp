package uk.ac.ed.inf.aqmaps;

/**
 * @author university
 *
 */
public class Sensor {
	
	/**
	 * What3Words location of this sensor.
	 */
	private String location;
	/**
	 * Battery of the sensor when visiting it.
	 */
	private double battery;
	/**
	 * Pollution level recorded by the sensor.
	 */
	private String reading;
	/**
	 * Longitude of this sensor.
	 */
	private double longitude;
	/**
	 * Latitude of this sensor.
	 */
	private double latitude;
	
	/**
	 * Two Sensors are equal if their location is 
	 * equal since we know that only one sensor is 
	 * within a what3words square.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Sensor)) {
			return false;
		}

		Sensor sensor = (Sensor) obj;
		return location.equals(sensor.location);
	}
	
	@Override
	public String toString() {
		return location;
	}
	
	/**
	 * @param what3Words location representation.
	 */
	public void setLocation(String what3Words) {
		this.location = what3Words;
	}
	
	/**
	 * @param lng of the sensor. 
	 */
	public void setLongitude(double lng) {
		this.longitude = lng;
	}
	
	/**
	 * @param lat of the sensor.
	 */
	public void setLatitude(double lat) {
		this.latitude = lat;
	}
		
	/**
	 * @return longitude of the sensor.
	 */
	public double getLongitude() {
		return this.longitude;
	}
	
	/**
	 * @return latitude of the sensor.
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * @return what3words of the sensor.
	 */
	public String getLocation() {
		return this.location;
	}

	/**
	 * @return battery reading of the sensor.
	 */
	public double getBattery() {
		return this.battery;
	}

	
	/**
	 * @return pollution level reading of the
	 *         sensor.
	 */
	public String getReading() {
		return this.reading;
	}

}