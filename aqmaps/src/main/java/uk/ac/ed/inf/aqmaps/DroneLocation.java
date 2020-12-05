package uk.ac.ed.inf.aqmaps;

import java.util.UUID;
import com.mapbox.geojson.Point;

public class DroneLocation {
	
	public double lon;
	public double lat;
	public Point point;
	public String id;
	public boolean isNearSensor;
	public Sensor nearbySensor;
	public boolean isStart = false;
	
	public DroneLocation(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
		this.point = Point.fromLngLat(lon, lat);
		this.id = UUID.randomUUID().toString();
		isNearSensor = false;
		nearbySensor = null;
	}
	
	@Override
	public int hashCode() {
		return point.hashCode();
	}
	
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
		
		double lonDiff = Math.abs(lon - droneLocation.lon);
		double latDiff = Math.abs(lat - droneLocation.lat);
		
		if (lonDiff < 0.0001 && latDiff < 0.0001) {
			return true;
		}
		return false;
		
	}
	
	@Override
	public String toString() {
		return "(" + lon + "," + lat + ")";
	}

}
