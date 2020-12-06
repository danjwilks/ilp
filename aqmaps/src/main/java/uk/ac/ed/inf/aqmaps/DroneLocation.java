package uk.ac.ed.inf.aqmaps;

import com.mapbox.geojson.Point;

public class DroneLocation {
	
	private double lon;
	private double lat;
	private Point point;
	private boolean isNearSensor;
	private Sensor nearbySensor;
	private boolean isStart = false;
	
	public DroneLocation(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
		this.point = Point.fromLngLat(lon, lat);
		isNearSensor = false;
		nearbySensor = null;
	}
	
	public double getLongitude() {
		return this.lon;
	}
	
	public double getLatitude() {
		return this.lat;
	}
	
	public int calcAngleTo(DroneLocation adjacentDroneLocation) {
		
		double angle = Math.toDegrees(Math.atan2(adjacentDroneLocation.lon - lon, adjacentDroneLocation.lat - lat));

	    if(angle < 0){
	        angle += 360;
	    }
	    
	    return (int) Math.round(angle);
	}
	
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

	
	public boolean getIsStart() {
		return isStart;
	}

	public boolean getIsNearSensor() {
		return isNearSensor;
	}

	
	public Sensor getNearbySensor() {
		return nearbySensor;
	}

	public Point getPoint() {
		return this.point;
	}

	public void setIsNearSensor(boolean isNearSensor) {
		this.isNearSensor = isNearSensor;
	}

	public void setNearbySensor(Sensor nearbySensor) {
		this.nearbySensor = nearbySensor;
	}

	public void setIsStart(boolean isStart) {
//		 TODO delete this before submission
		this.isStart = isStart;
	}

}
