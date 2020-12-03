package uk.ac.ed.inf.aqmaps;

import java.util.UUID;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

public class DroneLocation {
	
	public double lon;
	public double lat;
	public Point point;
	public String id;
	public boolean isNearSensor;
	public Sensor nearbySensor;
	
	public DroneLocation(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
		this.point = Point.fromLngLat(lon, lat);
		this.id = UUID.randomUUID().toString();
		isNearSensor = false;
		nearbySensor = null;
	}
	
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
