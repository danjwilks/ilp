package uk.ac.ed.inf.aqmaps;

import java.util.List;

public class SensorPath {
	
	List<DroneLocation> locations;
	List<DronePath> paths;
	Sensor source;
	int weight;
	
	SensorPath(List<DroneLocation> locations, List<DronePath> paths, Sensor source) {
		this.locations = locations;
		this.paths = paths;
		this.source = source;
		this.weight = paths.size();
	}
	
	@Override
	public int hashCode() {
		return paths.hashCode() + locations.hashCode();
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

		SensorPath edge = (SensorPath) obj;
		return locations.equals(edge.locations) && paths.equals(edge.paths);
	}

}
