package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

/**
 * @author S1851664
 * 
 * Class used to represent locations that the
 * drone can be. Particularly useful when 
 * creating graphs that the drone can traverse.
 *
 */
public class DroneLocation {
	
	/**
	 * Longitude of the drone location.
	 */
	private double lon;
	
	/**
	 * Latitude of the drone location.
	 */
	private double lat;
	
	/**
	 * Point of the drone location.
	 */
	private Point point;
	
	/**
	 * True if current location is near a sensor.
	 */
	private boolean isNearSensor;
	
	/**
	 * The nearby sensor, null if there is none.
	 */
	private Sensor nearbySensor;
	
	/**
	 * Creates Drone Location object and sets
	 * variables to default.
	 * 
	 * @param lon longitude of the drone location.
	 * @param lat latitude of the drone location.
	 */
	public DroneLocation(double lon, double lat) {
		this.lon = lon;;
		this.lat = lat;
		this.point = Point.fromLngLat(this.lon, this.lat);
		isNearSensor = false;
		nearbySensor = null;
	}
	
	/**
	 * Calculates the angle between the current location
	 * and the given adjacent drone location.
	 * 
	 * @param  adjacentDroneLocation
	 * @return the angle between current location and 
	 *         the adjacent drone location.
	 */
	public int calcAngleTo(DroneLocation adjacentDroneLocation) {
		
		double angle = Math.toDegrees(Math.atan2(adjacentDroneLocation.lon - lon, adjacentDroneLocation.lat - lat));
		
		// angle should not be negative.
	    if(angle < 0){
	        angle += 360;
	    }
	    
	    return (int) Math.round(angle);
	}
	
	/**
	 * Calculates distance between this drone location
	 * and given drone location.
	 * 
	 * @param  droneLocation
	 * @return distance between this location and 
	 *         given location.
	 */
	public double calcDistTo(DroneLocation droneLocation) {
		
		double distance = Math.sqrt(
				Math.pow(lon - droneLocation.lon, 2)
				+ Math.pow(lat - droneLocation.lat, 2)
				);
		return distance;
	}
		
	@Override
	public int hashCode() {
		return point.hashCode();
	}
	
	/**
	 * Locations are equal if the point of the locations
	 * are equal since the point is made from the 
	 * coordinates of the locations. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DroneLocation)) {
			return false;
		}

		DroneLocation droneLocation = (DroneLocation) obj;
		
		return point.equals(droneLocation.point);
		
	}
	
	@Override
	public String toString() {
		return "(" + lon + "," + lat + ")";
	}
	
	/**
	 * @return longitude of current location.
	 */
	public double getLongitude() {
		return this.lon;
	}
	
	/**
	 * @return latitude of current location.
	 */
	public double getLatitude() {
		return this.lat;
	}

	/**
	 * @return returns true if current location
	 *         is near a sensor.
	 */
	public boolean getIsNearSensor() {
		return isNearSensor;
	}

	/**
	 * @return nearby sensor.
	 */
	public Sensor getNearbySensor() {
		return nearbySensor;
	}

	/**
	 * @return locations point.
	 */
	public Point getPoint() {
		return this.point;
	}

	/**
	 * @param isNearSensor value to update local 
	 * 		  variable.
	 */
	public void setIsNearSensor(boolean isNearSensor) {
		this.isNearSensor = isNearSensor;
	}

	/**
	 * @param nearbySensor sensor object to update 
	 * 		  local sensor object.
	 */
	public void setNearbySensor(Sensor nearbySensor) {
		this.nearbySensor = nearbySensor;
	}

}
