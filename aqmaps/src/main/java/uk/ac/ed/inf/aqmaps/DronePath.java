package uk.ac.ed.inf.aqmaps;

import java.util.Arrays;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class DronePath {
	
	DroneLocation location1;
	DroneLocation location2;
	LineString lineString;
	
	public DronePath(DroneLocation l1, DroneLocation l2) {
		this.location1 = l1;
		this.location2 = l2;
		
		List<Point> points = Arrays.asList(l1.point, l2.point);
		this.lineString = LineString.fromLngLats(points);
	}
	
	@Override
	public String toString() {
		return "edge between " + this.location1.toString() + " and " + this.location2.toString();
	}
	
	@Override
	public int hashCode() {
		return location1.hashCode() + location2.hashCode();
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
		return location1.equals(edge.location1) && location2.equals(edge.location2)
				|| location2.equals(edge.location1) && location1.equals(edge.location2);
	}

}
