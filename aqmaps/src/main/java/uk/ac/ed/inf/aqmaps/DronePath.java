package uk.ac.ed.inf.aqmaps;

import java.util.Arrays;

import com.mapbox.geojson.LineString;

public class DronePath {
	
	DroneLocation vertex1;
	DroneLocation vertex2;
	LineString lineString;
	
	public DronePath(DroneLocation vertex1, DroneLocation vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.lineString = LineString.fromLngLats(Arrays.asList(vertex1.point, vertex2.point));
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
