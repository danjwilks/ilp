package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

public class SensorPath {
	
	private List<DroneLocation> locations;
	private List<DronePath> vertex1ToVertex2;
	private List<DronePath> vertex2ToVertex1;
	private int weight;
	private DroneLocation vertex1;
	
	
	SensorPath(List<DroneLocation> locations, List<DronePath> paths, DroneLocation vertex1, DroneLocation vertex2) {
		this.locations = locations;
		this.vertex1ToVertex2 = paths;
		this.vertex2ToVertex1 = reverse(paths);
		this.weight = paths.size();
		this.vertex1 = vertex1;
	}
	
	private List<DronePath> reverse(List<DronePath> edges) {
		
		var reversed = new ArrayList<DronePath>();
		for (int i = edges.size() - 1; i >= 0; i--) {
			reversed.add(edges.get(i));
		}
		return reversed;
		
	}
	
	public List<DronePath> getLocationsFrom(DroneLocation startDroneLocation) {
		if (vertex1.equals(startDroneLocation)) {
			return vertex1ToVertex2;
		} else {
			return vertex2ToVertex1;
		}
	}
	
	@Override
	public int hashCode() {
		return locations.hashCode();
	}
	
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
		return locations.equals(sensorPath.locations);
	}

	public double getWeight() {
		return this.weight;
	}

	public List<DronePath> getVertex1ToVertex2() {
		return this.vertex1ToVertex2;
	}

}
