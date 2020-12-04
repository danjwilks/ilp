package uk.ac.ed.inf.aqmaps;

import java.util.HashSet;
import java.util.UUID;
import com.mapbox.geojson.Point;

public class DroneLocation {
	
	public double lon;
	public double lat;
	public Point point;
	public String id;
	public boolean isNearSensor;
	public HashSet<Sensor> nearbySensors;
	
	public DroneLocation(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
		this.point = Point.fromLngLat(lon, lat);
		this.id = UUID.randomUUID().toString();
		isNearSensor = false;
		nearbySensors = new HashSet<>();
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

		DroneLocation loc = (DroneLocation) obj;
		return id.equals(loc.id);
	}
	
	@Override
	public String toString() {
		return "(" + lon + "," + lat + ")";
	}

}
