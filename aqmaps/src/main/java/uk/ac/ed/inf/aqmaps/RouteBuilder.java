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
	private double[] startEndLocation;
	
	private DroneLocation source;
	private DroneLocation sink;
	
	
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
	public RouteBuilder setStartEndLocation(double[] startEndLocation) {
		this.startEndLocation = startEndLocation;
		return this;
	}
	
//	public DroneLocation buildFromPointInDirection(DroneLocation currentLocation, int degree) {
//		
//		double currentLat = currentLocation.getLatitude();
//		double currentLng = currentLocation.getLongitude();
//		
//		// adjLong = dist * cos(theta) + adjLong
//		
//		double adjacentLatitude = 
//		double adjacentLongitude =
//		
//		DroneLocation adjacentDroneLocation = new DroneLocation(adjacentLatitude, adjacentLongitude, ) 
//		
//	}
	
	public List<List<DroneLocation>> buildTriangleGrid() {
		
		ArrayList<List<DroneLocation>> triangleGrid = new ArrayList<>();
		
		double upperLeftLonStart = startEndLocation[0];
		while (upperLeftLonStart > flyzone[0]) {
			upperLeftLonStart -= 0.0003;
		}
		double upperLeftLatStart = startEndLocation[1];
		while (upperLeftLatStart < flyzone[1]) {
			upperLeftLatStart += 0.0003;
		}
		
		double lowerRightLonEnd = flyzone[2];
		double lowerRightLatEnd = flyzone[3];
		
		boolean isShiftedRow = false;
		
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
		
//		for (var edge : allPaths) {
//			System.out.println(edge);
//		}
		
		return allPaths;
		
	}
	
	public Graph<DroneLocation, DronePath> buildTriangleGraph() {
		
		var graph = new DefaultUndirectedWeightedGraph<DroneLocation, DronePath>(DronePath.class);
		
		List<List<DroneLocation>> allPossibleDroneLocations = buildAllPossibleLocations();
		HashSet<DronePath> allPossibleDronePaths = buildAllPossiblePaths(allPossibleDroneLocations);
		boolean isFirst = true;
		for (var rowOfDroneLocations : allPossibleDroneLocations) {
			for (var droneLocation : rowOfDroneLocations) {
				graph.addVertex(droneLocation);
				if (isFirst) {
					source = droneLocation;
					isFirst = false;
				}
				sink = droneLocation;
			}
		}
		
		for (var dronePath : allPossibleDronePaths) {
			graph.addEdge(dronePath.source, dronePath.sink, dronePath);
			graph.setEdgeWeight(dronePath, 1);
		}
		
		return graph;
		
	}
	
	public boolean isWithinDistance(Sensor sensor, DroneLocation droneLocation) {
		
		double distance = Math.sqrt(
				Math.pow(sensor.getLongitude() - droneLocation.lon, 2)
				+ Math.pow(sensor.getLatitude() - droneLocation.lat, 2)
				);
		
		double maxDist = 0.0002;
		
		return distance < maxDist;
		
	}
	
	public Set<DroneLocation> findDroneLocationsNearSensors(Graph<DroneLocation, DronePath> triangleGraph) {
		
		var droneLocationsNearSensors = new HashSet<DroneLocation>();
		
		for (var sensor : sensors) {
			for (var droneLocation : triangleGraph.vertexSet()) {
				if (isWithinDistance(sensor, droneLocation)) {
					droneLocationsNearSensors.add(droneLocation);
					droneLocation.isNearSensor = true;
					droneLocation.nearbySensors.add(sensor);
					break;
				}
			}
		}
//		System.out.println("must visit " + droneLocationsNearSensors.size() + " drones");
		return droneLocationsNearSensors;
	}
	
	private Graph<DroneLocation, SensorPath> buildShortestPaths(Graph<DroneLocation, DronePath> triangleGraph, Set<DroneLocation> droneLocationsNearSensorsSet) {
		
		var simpleSensorGraph = new DefaultUndirectedWeightedGraph<DroneLocation, SensorPath>(SensorPath.class);
		
		var droneLocationsNearSensorsList = new ArrayList<DroneLocation>();
		droneLocationsNearSensorsList.addAll(droneLocationsNearSensorsSet);
		
		for (int sourceIndex = 0; sourceIndex < droneLocationsNearSensorsList.size(); sourceIndex++) {
			for (int sinkIndex = sourceIndex + 1; sinkIndex < droneLocationsNearSensorsList.size(); sinkIndex++) {
				
				var sourceDroneLocation = droneLocationsNearSensorsList.get(sourceIndex);
				var sinkDroneLocation = droneLocationsNearSensorsList.get(sinkIndex);
				
				var dijk = new BidirectionalDijkstraShortestPath<DroneLocation, DronePath>(triangleGraph);
				var graphWalk = dijk.getPath(sourceDroneLocation, sinkDroneLocation);
				var vertices = graphWalk.getVertexList();
				var edges = graphWalk.getEdgeList();
				
				simpleSensorGraph.addVertex(sourceDroneLocation);
				simpleSensorGraph.addVertex(sinkDroneLocation);
				
				// new object adjacentSensor path
				var sensorPath = new SensorPath(vertices, edges, sourceDroneLocation, sinkDroneLocation);
				simpleSensorGraph.addEdge(sourceDroneLocation, sinkDroneLocation, sensorPath);
				simpleSensorGraph.setEdgeWeight(sensorPath, sensorPath.weight);
				
			}
		}
		
		return simpleSensorGraph;
	}
	
	public GraphPath<DroneLocation, SensorPath> buildBestRoute() {
		
		var triangleGraph = buildTriangleGraph();
		var droneLocationsNearSensors = findDroneLocationsNearSensors(triangleGraph);
		var simpleSensorGraph = buildShortestPaths(triangleGraph, droneLocationsNearSensors);
		var christofides = new ChristofidesThreeHalvesApproxMetricTSP<DroneLocation, SensorPath>();
		var routeFound = christofides.getTour(simpleSensorGraph);
		
//		var dijk = new BidirectionalDijkstraShortestPath<DroneLocation, DronePath>(triangleGraph);
//		var graphWalk = dijk.getPath(source, sink);
//		var vertices = graphWalk.getVertexList();
//		var edges = graphWalk.getEdgeList();
		
//		System.out.println(vertices);
//		System.out.println(edges);
//		System.out.println(graphWalk.getClass());
		
		System.out.println("routeFound edges size: " + routeFound.getEdgeList().size());
		System.out.println("routeFound vertex size: " + routeFound.getVertexList().size());
		
		return routeFound;
	}

}