package uk.ac.ed.inf.aqmaps;

public class DronePath {
	
	DroneLocation source;
	DroneLocation sink;
	
	public DronePath(DroneLocation source, DroneLocation sink) {
		this.source = source;
		this.sink = sink;
	}
	
	@Override
	public String toString() {
		return "edge between " + this.source.toString() + " and " + this.sink.toString();
	}
	
	@Override
	public int hashCode() {
		return source.hashCode() + sink.hashCode();
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
		return source.equals(edge.source) && sink.equals(edge.sink)
				|| sink.equals(edge.source) && source.equals(edge.sink);
	}

}
