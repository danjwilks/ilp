package uk.ac.ed.inf.aqmaps;

import java.util.UUID;
import com.mapbox.geojson.Point;

public class DroneLocation {
	
	public double lon;
	public double lat;
	public Point point;
	public boolean isNearSensor;
	public Sensor nearbySensor;
	public boolean isStart = false;
	
	public DroneLocation(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
		this.point = Point.fromLngLat(lon, lat);
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
		
		return droneLocation.point.equals(point);
		
	}
	
	@Override
	public String toString() {
		return "(" + lon + "," + lat + ")";
	}

}
