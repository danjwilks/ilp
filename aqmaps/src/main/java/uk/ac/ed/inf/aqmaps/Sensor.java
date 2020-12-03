package uk.ac.ed.inf.aqmaps;

import java.math.BigDecimal;

import com.mapbox.geojson.Point;

public class Sensor {
	
	String location;
	double battery;
	String reading;
	public Point point;
	private double longitude;
	private double latitude;
	private DroneLocation nearbyDroneLocation;
	
	public void setLongitude(double lng) {
		this.longitude = lng;
	}
	public void setLatitude(double lat) {
		this.latitude = lat;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public Point getPoint() {
		return Point.fromLngLat(longitude, latitude);
	}
	
//	  "location": "shut.stands.media",
//    "battery": 61.31742,
//    "reading": "89.3"
	
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
	public void setNearbyDroneLocation(DroneLocation droneLocation) {
		nearbyDroneLocation = droneLocation;
	}
	public DroneLocation getNearbyDroneLocation() {
		return nearbyDroneLocation;
	}
}