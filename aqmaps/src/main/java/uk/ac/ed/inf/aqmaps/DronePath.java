package uk.ac.ed.inf.aqmaps;

public class DronePath {
	
	DroneLocation vertex1;
	DroneLocation vertex2;
	
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
	
	public int directionDegreeFrom(DroneLocation from) {
		
	    return calcDirectionDegree(from, this.connectingDroneLocation(from));
		
	}
	
	private int calcDirectionDegree(DroneLocation source, DroneLocation sink) {
		
	    double angle = Math.toDegrees(Math.atan2(sink.lon - source.lon, sink.lat - source.lat));

	    if(angle < 0){
	        angle += 360;
	    }
	    
	    return (int) Math.round(angle);
		
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

}
