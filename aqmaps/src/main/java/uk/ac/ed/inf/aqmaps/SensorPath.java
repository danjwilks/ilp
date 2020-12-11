package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

/**
 * @author S1851664
 *
 * Class to represent the path between two sensors.
 * The path is made up of all the individual drone 
 * paths within the route between the two sensors.
 */
public class SensorPath {
	
	/**
	 * Ordered list of drone locations from vertex1
	 * to vertex2.
	 */
	private List<DronePath> vertex1ToVertex2;
	/**
	 * Ordered list of drone locations from vertex2
	 * to vertex1.
	 */
	private List<DronePath> vertex2ToVertex1;
	/**
	 * The number of drone paths within the sensor
	 * path.
	 */
	private int weight;
	/**
	 * Start vertex of the sensor path.
	 */
	private DroneLocation vertex1;
	
	
	/**
	 * Creates instance of a sensor path.
	 * 
	 * @param locations an ordered list of drone
	 *                  locations from vertex1 to vertex2.
	 * @param paths     drone paths from vertex1 to vertex2.
	 * @param vertex1   start drone location of locations 
	 * 					list.
	 */
	public SensorPath(List<DroneLocation> locations, List<DronePath> paths, DroneLocation vertex1) {
		this.vertex1ToVertex2 = paths;
		this.vertex2ToVertex1 = reverse(paths);
		this.weight = paths.size();
		this.vertex1 = vertex1;
	}
	
	/**
	 * Reverses the edge not in place.
	 * 
	 * @param edges the list of paths to reverse.
	 * @return the reversed version of the list 
	 *         of drone paths.
	 */
	private List<DronePath> reverse(List<DronePath> edges) {
		
		var reversed = new ArrayList<DronePath>();
		for (int i = edges.size() - 1; i >= 0; i--) {
			reversed.add(edges.get(i));
		}
		return reversed;
		
	}
	
	/**
	 * @param startDroneLocation source location wanted.
	 * @return the list of drone paths from the start
	 *         drone location specified.
	 */
	public List<DronePath> getLocationsFrom(DroneLocation startDroneLocation) {
		if (vertex1.equals(startDroneLocation)) {
			return vertex1ToVertex2;
		} else {
			return vertex2ToVertex1;
		}
	}
	
	@Override
	public int hashCode() {
		return vertex1ToVertex2.hashCode();
	}
	
	/**
	 * Two sensor paths are equal if their vertex
	 * lists are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SensorPath)) {
			return false;
		}

		SensorPath sensorPath = (SensorPath) obj;
		return vertex1ToVertex2.equals(sensorPath.vertex1ToVertex2) ||
				vertex2ToVertex1.equals(sensorPath.vertex1ToVertex2);
	}

	/**
	 * @return weight of the sensor path.
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * @return the list of drone paths from 
	 *         vertex1 to vertex2.
	 */
	public List<DronePath> getVertex1ToVertex2() {
		return this.vertex1ToVertex2;
	}

}
