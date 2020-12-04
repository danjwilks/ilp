package uk.ac.ed.inf.aqmaps;

import java.util.List;

public class SensorPath {
	
	List<DroneLocation> locations;
	List<DronePath> paths;
	int weight;
	DroneLocation source;
	DroneLocation sink;
	
	SensorPath(List<DroneLocation> locations, List<DronePath> paths, DroneLocation source, DroneLocation sink) {
		this.locations = locations;
		this.paths = paths;
		this.weight = paths.size();
		this.source = source;
		this.sink = sink;
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

		SensorPath sensorPath = (SensorPath) obj;
		return locations.equals(sensorPath.locations) && paths.equals(sensorPath.paths);
	}

}
