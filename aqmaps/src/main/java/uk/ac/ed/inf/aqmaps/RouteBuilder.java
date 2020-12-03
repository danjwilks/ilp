package uk.ac.ed.inf.aqmaps;

import java.util.List;
import com.mapbox.geojson.FeatureCollection;
//import com.google.ortools.Loader;
//import com.google.ortools.constraintsolver.Assignment;
//import com.google.ortools.constraintsolver.FirstSolutionStrategy;
//import com.google.ortools.constraintsolver.RoutingIndexManager;
//import com.google.ortools.constraintsolver.RoutingModel;
//import com.google.ortools.constraintsolver.RoutingSearchParameters;
//import com.google.ortools.constraintsolver.main;
import java.util.logging.Logger;

public class RouteBuilder {
	
	private double[] flyzone;
	private FeatureCollection buildings;
	private List<Station> stations;
	private double[] startEndLocation;
	
	
	public RouteBuilder setFlyZone(double[] flyzone) {
		this.flyzone = flyzone;
		return this;
		
	}
	public RouteBuilder setBuildings(FeatureCollection buildings) {
		this.buildings = buildings;
		return this;
	}
	public RouteBuilder setStations(List<Station> stations) {
		this.stations = stations;
		return this;
	}
	public RouteBuilder setStartEndLocation(double[] startEndLocation) {
		this.startEndLocation = startEndLocation;
		return this;
	}
	
	public String buildBestRoute() {
		
//		TODO: implement
		
		
		
		return stations.toString();
	}

}