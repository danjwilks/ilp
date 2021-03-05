package uk.ac.ed.inf.aqmaps;

/**
 * @author S1851664
 * 
 * Class to represent a flight path between two
 * drone locations. This path is undirected.
 */
public class DronePath {
	
	/**
	 * First vertex of the flight path.
	 */
	private DroneLocation vertex1;
	
	/**
	 * Second vertex of the flight path. 
	 */
	private DroneLocation vertex2;
	
	/**
	 * @param vertex1
	 * @param vertex2
	 */
	public DronePath(DroneLocation vertex1, DroneLocation vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
	}
	
	/**
	 * Returns the vertex connected to the given vertex.
	 * 
	 * @param from is vertex we want to get the adjacent 
	 *        vertex from.
	 * @return the connected vertex to 'from'.
	 */
	public DroneLocation getConnectingDroneLocation(DroneLocation from) {
		if (vertex1.equals(from)) {
			return vertex2;
		}
		return vertex1;
	}
	
	@Override
	public String toString() {
		return "edge between " + this.vertex1.toString() + " and " + this.vertex2.toString();
	}
	
	@Override
	public int hashCode() {
		return vertex1.hashCode() + vertex2.hashCode();
	}
	
	/**
	 * Two drone paths are equal if the vertices 
	 * are equal. The path is undirected.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DronePath)) {
			return false;
		}

		DronePath edge = (DronePath) obj;
		return vertex1.equals(edge.vertex1) && vertex2.equals(edge.vertex2)
				|| vertex2.equals(edge.vertex1) && vertex1.equals(edge.vertex2);
	}

	/**
	 * @return vertex1
	 */
	public DroneLocation getVertex1() {
		return this.vertex1;
	}
	
	/**
	 * @return vertex2
	 */
	public DroneLocation getVertex2() {
		return this.vertex2;
	}

}
