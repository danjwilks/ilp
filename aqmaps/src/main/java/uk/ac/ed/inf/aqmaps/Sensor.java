package uk.ac.ed.inf.aqmaps;

public class Sensor {
	
	String location;
	double battery;
	String reading;
	private double longitude;
	private double latitude;
	private DroneLocation nearestDroneLocation;
	
	public void setLocation(String what3Words) {
		this.location = what3Words;
	}
	
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
	
	public void setNearestDroneLocation(DroneLocation droneLocation) {
		nearestDroneLocation = droneLocation;
	}
	
	public DroneLocation getNearestDroneLocation() {
		return nearestDroneLocation;
	}
}