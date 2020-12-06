package uk.ac.ed.inf.aqmaps;

public class Sensor {
	
	private String location;
	private double battery;
	private String reading;
	private double longitude;
	private double latitude;
	private DroneLocation nearestDroneLocation;
	
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
		this.nearestDroneLocation = droneLocation;
	}
	
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
		return this.longitude;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public DroneLocation getNearestDroneLocation() {
		return this.nearestDroneLocation;
	}


	public String getLocation() {
		return this.location;
	}

	public double getBattery() {
		return this.battery;
	}

	
	public String getReading() {
		return this.reading;
	}

}