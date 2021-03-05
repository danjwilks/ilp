package uk.ac.ed.inf.aqmaps;

import java.util.List;

/**
 * @author S1851664
 *
 * Interface for routes that a drone can
 * traverse.
 */
public interface Route {
	
	/**
	 * @return list of drone locations that a drone can 
	 *         traverse.
	 */
	public List<DroneLocation> getDroneLocationsToVisit();
	
	/**
	 * @return collection of unvisited sensors when the
	 *         drone traverses this route.
	 */
	public SensorCollection getUnvisitedSensors();
	
}
