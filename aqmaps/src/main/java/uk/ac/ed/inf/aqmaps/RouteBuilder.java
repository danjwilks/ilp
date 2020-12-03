package uk.ac.ed.inf.aqmaps;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
					}
				} else {
					if (column > 1 && row < allPossibleDroneLocations.size() - 1) {
						allPaths.add(new DronePath(
								allPossibleDroneLocations.get(row).get(column),
								allPossibleDroneLocations.get(row + 1).get(column - 1))
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
			graph.addEdge(dronePath.location1, dronePath.location2, dronePath);
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
	
	public void addSensors(Graph<DroneLocation, DronePath> triangleGraph) {
		
		for (var sensor : sensors) {
			for (var droneLocation : triangleGraph.vertexSet()) {
				if (isWithinDistance(sensor, droneLocation)) {
					droneLocation.isNearSensor = true;
					droneLocation.nearbySensor = sensor;
					sensor.setNearbyDroneLocation(droneLocation);
					break;
				}
			}
		}
		
		return;
	}
	
	private Graph<Sensor, SensorPath> removeLongPathsFromSensors(Graph<DroneLocation, DronePath> triangleGraph) {
		var simpleSensorGraph = new DefaultUndirectedWeightedGraph<Sensor, SensorPath>(SensorPath.class);
		
		for (int sourceIndex = 0; sourceIndex < sensors.size(); sourceIndex++) {
			for (int sinkIndex = sourceIndex + 1; sinkIndex < sensors.size(); sinkIndex++) {
				
//				get shortest path and add all nodes and edges to graph.
				
				var sourceSensor = sensors.get(sourceIndex);
				var sinkSensor = sensors.get(sinkIndex);
				var sourceDroneLocation = sourceSensor.getNearbyDroneLocation();
				var sinkDroneLocation = sinkSensor.getNearbyDroneLocation();
				
				
				var dijk = new BidirectionalDijkstraShortestPath<DroneLocation, DronePath>(triangleGraph);
				var graphWalk = dijk.getPath(sourceDroneLocation, sinkDroneLocation);
				var vertices = graphWalk.getVertexList();
				var edges = graphWalk.getEdgeList();
				
				simpleSensorGraph.addVertex(sourceSensor);
				simpleSensorGraph.addVertex(sinkSensor);
				
				// new object adjacentSensor path
				var sensorPath = new SensorPath(vertices, edges, sourceSensor);
				simpleSensorGraph.addEdge(sourceSensor, sinkSensor, sensorPath);
				simpleSensorGraph.setEdgeWeight(sensorPath, sensorPath.weight);
				
			}
		}
		
		return simpleSensorGraph;
	}
	
	public GraphPath<Sensor, SensorPath> buildBestRoute() {
		
		var triangleGraph = buildTriangleGraph();
		addSensors(triangleGraph);
		var shortestPathsGraph = removeLongPathsFromSensors(triangleGraph);
		
		var christofides = new ChristofidesThreeHalvesApproxMetricTSP<Sensor, SensorPath>();
		var routeFound = christofides.getTour(shortestPathsGraph);
		
//		var dijk = new BidirectionalDijkstraShortestPath<DroneLocation, DronePath>(triangleGraph);
//		var graphWalk = dijk.getPath(source, sink);
//		var vertices = graphWalk.getVertexList();
//		var edges = graphWalk.getEdgeList();
		
//		System.out.println(vertices);
//		System.out.println(edges);
//		System.out.println(graphWalk.getClass());
		
		return routeFound;
	}

}