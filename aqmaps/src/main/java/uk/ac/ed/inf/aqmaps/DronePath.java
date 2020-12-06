package uk.ac.ed.inf.aqmaps;

public class DronePath {
	
	private DroneLocation vertex1;
	private DroneLocation vertex2;
	
	public DronePath(DroneLocation vertex1, DroneLocation vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
	}
	
	public DroneLocation connectingDroneLocation(DroneLocation from) {
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

	
	public DroneLocation getVertex1() {
		return vertex1;
	}
	
	public DroneLocation getVertex2() {
		return vertex2;
	}

}
