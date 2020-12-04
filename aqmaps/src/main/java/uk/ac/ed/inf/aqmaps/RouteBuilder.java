package uk.ac.ed.inf.aqmaps;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;

public class RouteBuilder {
	
	private double[] flyzone;
	private FeatureCollection buildings;
	private List<Sensor> sensors;
	private DroneLocation startEndLocation;
	
	public RouteBuilder setFlyZone(double[] flyzone) {
		this.flyzone = flyzone;
		return this;
		
	}
	public RouteBuilder setBuildings(FeatureCollection buildings) {
		this.buildings = buildings;
		return this;
	}
	public RouteBuilder setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
		return this;
	}
	public RouteBuilder setStartEndLocation(DroneLocation startEndLocation) {
		this.startEndLocation = startEndLocation;
		return this;
	}
	
	public List<List<DroneLocation>> buildTriangleGrid() {
		
		ArrayList<List<DroneLocation>> triangleGrid = new ArrayList<>();
		
		double upperLeftLonStart = startEndLocation.lon;
		int numberOfRowsToLeft = 0;
		while (upperLeftLonStart > flyzone[0]) {
			numberOfRowsToLeft++;
			upperLeftLonStart -= 0.0003;
		}
		double upperLeftLatStart = startEndLocation.lat;
		while (upperLeftLatStart < flyzone[1]) {
			upperLeftLatStart += 0.0003;
		}
		
		double lowerRightLonEnd = flyzone[2];
		double lowerRightLatEnd = flyzone[3];
		
		boolean isShiftedRow = false;
		
		if (numberOfRowsToLeft % 2 == 1) {
			isShiftedRow = true;
		}
		
		double latDecrement = Math.sqrt(Math.pow(0.0003, 2) - Math.pow(0.00015, 2)); // figure out what is the triangle height
		double lonIncrement = 0.0003;
		double rowShift = 0.00015;
		
		for (double currLat = upperLeftLatStart; currLat > lowerRightLatEnd; currLat -= latDecrement) {
			var droneLocationsRow = new ArrayList<DroneLocation>();
			for (double currLon = upperLeftLonStart; currLon < lowerRightLonEnd; currLon += lonIncrement) {
				if (isShiftedRow) {
					droneLocationsRow.add(new DroneLocation(currLon + rowShift, currLat));
				} else {
					droneLocationsRow.add(new DroneLocation(currLon, currLat));
				}
			}
			if (isShiftedRow) {
				isShiftedRow = false;
			} else {
				isShiftedRow = true;
			}
			triangleGrid.add(droneLocationsRow);
		}
		
		return triangleGrid;
	}
	
	public List<List<DroneLocation>> buildAllPossibleLocations() {
		
		List<List<DroneLocation>> triangleGrid = buildTriangleGrid();
		
		return triangleGrid;
	}
	
	public HashSet<DronePath> buildAllPossiblePaths(List<List<DroneLocation>> allPossibleDroneLocations) {
		
		// add horizontal
		// add vertical
		var allPaths = new HashSet<DronePath>();
		// all row
		
		boolean isRowShifted = false;
		
		for (int row = 0; row < allPossibleDroneLocations.size(); row++) {
			for (int column = 0; column < allPossibleDroneLocations.get(row).size(); column++) {
				
				if (row > 1) {
					allPaths.add(new DronePath(
							allPossibleDroneLocations.get(row).get(column),
							allPossibleDroneLocations.get(row - 1).get(column)
							));
				}
				
				if (row < allPossibleDroneLocations.size() - 1) {
					allPaths.add(new DronePath(
							allPossibleDroneLocations.get(row).get(column),
							allPossibleDroneLocations.get(row + 1).get(column))
							);
				}
				
				if (column > 1) {
					allPaths.add(new DronePath(
							allPossibleDroneLocations.get(row).get(column),
							allPossibleDroneLocations.get(row).get(column - 1))
							);
				}
				
				if (column < allPossibleDroneLocations.get(row).size() - 1) {
					allPaths.add(new DronePath(
							allPossibleDroneLocations.get(row).get(column),
							allPossibleDroneLocations.get(row).get(column + 1))
							);
				}
				
				if (isRowShifted) {
					if (row < allPossibleDroneLocations.size() - 1 && column < allPossibleDroneLocations.get(row).size() - 1) {
						allPaths.add(new DronePath(
								allPossibleDroneLocations.get(row).get(column),
								allPossibleDroneLocations.get(row + 1).get(column + 1))
								);
						allPaths.add(new DronePath(
								allPossibleDroneLocations.get(row).get(column),
								allPossibleDroneLocations.get(row - 1).get(column + 1))
								);
					}
				} 
				
			}
			
			if (isRowShifted) {
				isRowShifted = false;
			} else {
				isRowShifted = true;
			}
			
		}
		
		return allPaths;
		
	}
	
	public Graph<DroneLocation, DronePath> buildTriangleGraph() {
		
		var graph = new DefaultUndirectedWeightedGraph<DroneLocation, DronePath>(DronePath.class);
		
		List<List<DroneLocation>> allPossibleDroneLocations = buildAllPossibleLocations();
		HashSet<DronePath> allPossibleDronePaths = buildAllPossiblePaths(allPossibleDroneLocations);
		for (var rowOfDroneLocations : allPossibleDroneLocations) {
			for (var droneLocation : rowOfDroneLocations) {
				graph.addVertex(droneLocation);
			}
		}
		
		for (var dronePath : allPossibleDronePaths) {
			graph.addEdge(dronePath.vertex1, dronePath.vertex2, dronePath);
			graph.setEdgeWeight(dronePath, 1);
		}
		
		return graph;
		
	}
	
	public boolean sensorIsWithinDistance(Sensor sensor, DroneLocation droneLocation) {
		
		double distance = Math.sqrt(
				Math.pow(sensor.getLongitude() - droneLocation.lon, 2)
				+ Math.pow(sensor.getLatitude() - droneLocation.lat, 2)
				);
		
		double maxDist = 0.0002;
		
		return distance < maxDist;
		
	}
	
	public boolean isStartEndLocation(DroneLocation droneLocation) {
		
		double lonDiff = Math.abs(droneLocation.lon - startEndLocation.lon);
		double latDiff = Math.abs(droneLocation.lat - startEndLocation.lat);
		
		if (lonDiff < 0.0001 && latDiff < 0.0001) {
			return true;
		}
		return false;
		
	}
	
	public Set<DroneLocation> findDroneLocationsToVisit(Graph<DroneLocation, DronePath> triangleGraph) {
		
		var droneLocationsToVisit = new HashSet<DroneLocation>();
		
		for (var sensor : sensors) {
			for (var droneLocation : triangleGraph.vertexSet()) {
				if (sensorIsWithinDistance(sensor, droneLocation)) {
					droneLocationsToVisit.add(droneLocation);
					droneLocation.isNearSensor = true;
					droneLocation.nearbySensor = sensor;
					break;
				} else if (isStartEndLocation(droneLocation)) {
					droneLocation.isStart = true;
					droneLocationsToVisit.add(droneLocation);
				}
			}
		}
		return droneLocationsToVisit;
	}
	
	private Graph<DroneLocation, SensorPath> buildShortestPaths(Graph<DroneLocation, DronePath> triangleGraph, Set<DroneLocation> droneLocationsToVisitSet) {
		
		var simpleSensorGraph = new DefaultUndirectedWeightedGraph<DroneLocation, SensorPath>(SensorPath.class);
		
		var droneLocationsToVisitList = new ArrayList<DroneLocation>();
		droneLocationsToVisitList.addAll(droneLocationsToVisitSet);
		
		for (int sourceIndex = 0; sourceIndex < droneLocationsToVisitList.size(); sourceIndex++) {
			for (int sinkIndex = sourceIndex + 1; sinkIndex < droneLocationsToVisitList.size(); sinkIndex++) {
				
				var sourceDroneLocation = droneLocationsToVisitList.get(sourceIndex);
				var sinkDroneLocation = droneLocationsToVisitList.get(sinkIndex);
				
				var dijk = new BidirectionalDijkstraShortestPath<DroneLocation, DronePath>(triangleGraph);
				var graphWalk = dijk.getPath(sourceDroneLocation, sinkDroneLocation);
				var vertices = graphWalk.getVertexList();
				var edges = graphWalk.getEdgeList();
				
				simpleSensorGraph.addVertex(sourceDroneLocation);
				simpleSensorGraph.addVertex(sinkDroneLocation);
				
				var sensorPath = new SensorPath(vertices, edges, sourceDroneLocation, sinkDroneLocation);
				simpleSensorGraph.addEdge(sourceDroneLocation, sinkDroneLocation, sensorPath);
				simpleSensorGraph.setEdgeWeight(sensorPath, sensorPath.weight);
				
			}
		}
		
		return simpleSensorGraph;
	}
	
	public GraphPath<DroneLocation, SensorPath> buildBestRoute() {
		
		var triangleGraph = buildTriangleGraph();
		var droneLocationsToVisit = findDroneLocationsToVisit(triangleGraph);
		var simpleSensorGraph = buildShortestPaths(triangleGraph, droneLocationsToVisit);
		var christofides = new ChristofidesThreeHalvesApproxMetricTSP<DroneLocation, SensorPath>();
		var routeFound = christofides.getTour(simpleSensorGraph);
		
		return routeFound;
	}

}