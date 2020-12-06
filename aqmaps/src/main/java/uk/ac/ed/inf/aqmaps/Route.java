package uk.ac.ed.inf.aqmaps;

import java.util.List;

public interface Route {
	
	public List<DroneLocation> getDroneLocationsToVisit();
	
	public SensorCollection getUnvisitedSensors();
	
}
