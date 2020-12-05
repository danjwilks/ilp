package uk.ac.ed.inf.aqmaps;


import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;

public class RouteBuilder {
	
	private static final double ULLON = -3.192473;
	private static final double ULLAT = 55.946233;
	private static final double LRLON = -3.184319;
	private static final double LRLAT = 55.942617;
	
	private List<Polygon> buildings;
	private List<Sensor> sensors;
	private DroneLocation startEndLocation;
	private Polygon flyZone;
	private Point upperLeftBoundaryPoint;
	private Point upperRightBoundaryPoint;
	private Point lowerRightBoundaryPoint;
	private Point lowerLeftBoundaryPoint;
	private List<Point> boundaryPointsList;
	
	public RouteBuilder setBuildings(List<Polygon> buildings) {
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
	
 	public List<List<DroneLocation>> buildTriangleGridDroneLocations() {
		
		ArrayList<List<DroneLocation>> triangleGrid = new ArrayList<>();
		
		double upperLeftLonStart = startEndLocation.lon;
		int numberOfRowsToLeft = 0;
		while (upperLeftLonStart > ULLON) {
			numberOfRowsToLeft++;
			upperLeftLonStart -= 0.0003;
		}
		double upperLeftLatStart = startEndLocation.lat;
		while (upperLeftLatStart < ULLAT) {
			upperLeftLatStart += 0.0003;
		}
		
		double lowerRightLonEnd = LRLON;
		double lowerRightLatEnd = LRLAT;
		
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
					droneLocationsRow.add(new DroneLocation(round(currLon + rowShift), round(currLat)));
				} else {
					droneLocationsRow.add(new DroneLocation(round(currLon), round(currLat)));
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
 	
 	public double round(double num) {
 		return (double)Math.round(num * 10000d) / 10000d;
// 		return num;
 	}
	
	public HashSet<DronePath> buildTriangleGridDronePaths(List<List<DroneLocation>> triangleGridDroneLocations) {
		
		// add horizontal
		// add vertical
		var allPaths = new HashSet<DronePath>();
		// all row
		
		boolean isRowShifted = false;
		
		for (int row = 0; row < triangleGridDroneLocations.size(); row++) {
			for (int column = 0; column < triangleGridDroneLocations.get(row).size(); column++) {
				
				if (row > 1) {
					allPaths.add(new DronePath(
							triangleGridDroneLocations.get(row).get(column),
							triangleGridDroneLocations.get(row - 1).get(column)
							));
				}
				
				if (row < triangleGridDroneLocations.size() - 1) {
					allPaths.add(new DronePath(
							triangleGridDroneLocations.get(row).get(column),
							triangleGridDroneLocations.get(row + 1).get(column))
							);
				}
				
				if (column > 1) {
					allPaths.add(new DronePath(
							triangleGridDroneLocations.get(row).get(column),
							triangleGridDroneLocations.get(row).get(column - 1))
							);
				}
				
				if (column < triangleGridDroneLocations.get(row).size() - 1) {
					allPaths.add(new DronePath(
							triangleGridDroneLocations.get(row).get(column),
							triangleGridDroneLocations.get(row).get(column + 1))
							);
				}
				
				if (isRowShifted) {
					if (row < triangleGridDroneLocations.size() - 1 && column < triangleGridDroneLocations.get(row).size() - 1) {
						allPaths.add(new DronePath(
								triangleGridDroneLocations.get(row).get(column),
								triangleGridDroneLocations.get(row + 1).get(column + 1))
								);
						allPaths.add(new DronePath(
								triangleGridDroneLocations.get(row).get(column),
								triangleGridDroneLocations.get(row - 1).get(column + 1))
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
		
		List<List<DroneLocation>> triangleGridDroneLocations = buildTriangleGridDroneLocations();
		HashSet<DronePath> triangleGridDronePaths = buildTriangleGridDronePaths(triangleGridDroneLocations);
		for (var rowOfDroneLocations : triangleGridDroneLocations) {
			for (var droneLocation : rowOfDroneLocations) {
				graph.addVertex(droneLocation);
			}
		}
		
		for (var dronePath : triangleGridDronePaths) {
			graph.addEdge(dronePath.vertex1, dronePath.vertex2, dronePath);
			graph.setEdgeWeight(dronePath, 1);
		}
		
		return graph;
		
	}
	
	private boolean locationOverBuildings(DroneLocation droneLocation) {
		
		var dronePoint = droneLocation.point;
		
		for (var building : buildings) {
			if (TurfJoins.inside(dronePoint, building)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean linesIntersect(Point line1Start, Point line1End, Point line2Start, Point line2End) {
		
		return Line2D.linesIntersect(
				line1Start.longitude(), line1Start.latitude(),
				line1End.longitude(), line1End.latitude(),
				line2Start.longitude(), line2Start.latitude(),
				line2End.longitude(), line2End.latitude()
				);
		
	}
	
	private boolean dronePathIsOverBuildings(DronePath dronePath) {
		
		var startDroneLocation = dronePath.vertex1;
		var endDroneLocation = dronePath.vertex2;
		
		for (var building : buildings) {
			
			if (locationOverBuildings(startDroneLocation) || locationOverBuildings(endDroneLocation)) {
				return true;
			}
			
			var buildingPoints = building.coordinates().get(0);
			for (int startPointIndex = 0; startPointIndex < buildingPoints.size() -1; startPointIndex++) {
				var buildingEdgeStartPoint = buildingPoints.get(startPointIndex);
				var buildingEdgeEndPoint = buildingPoints.get(startPointIndex + 1);
				
				if (linesIntersect(startDroneLocation.point, endDroneLocation.point, buildingEdgeStartPoint, buildingEdgeEndPoint)) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	private boolean locationInsideFlyZone(DroneLocation droneLocation) {
		
		var dronePoint = droneLocation.point;
		
		if (TurfJoins.inside(dronePoint, flyZone)) {
			return true;
		}
		
		return false;
	}
	
	private void buildFlyZone() {
		
		this.upperLeftBoundaryPoint = Point.fromLngLat(ULLON, ULLAT);
		this.upperRightBoundaryPoint = Point.fromLngLat(LRLON, ULLAT);
		this.lowerRightBoundaryPoint = Point.fromLngLat(LRLON, LRLAT);
		this.lowerLeftBoundaryPoint = Point.fromLngLat(ULLON, LRLAT);
		
		this.boundaryPointsList = Arrays.asList(
				upperLeftBoundaryPoint, upperRightBoundaryPoint,
				lowerRightBoundaryPoint, lowerLeftBoundaryPoint,
				upperLeftBoundaryPoint);
		
		this.flyZone = Polygon.fromLngLats(Arrays.asList(boundaryPointsList));
	}
	
	private boolean dronePathInsideFlyZone(DronePath dronePath) {
		
		var startDroneLocation = dronePath.vertex1;
		var endDroneLocation = dronePath.vertex2;
		
		if (!locationInsideFlyZone(startDroneLocation) 
				|| !locationInsideFlyZone(endDroneLocation)) {
			return false;
		}
		
		for (int startPointIndex = 0; startPointIndex < boundaryPointsList.size() - 1; startPointIndex++) {
			var boundaryStartPoint = boundaryPointsList.get(startPointIndex);
			var boundaryEndPoint = boundaryPointsList.get(startPointIndex + 1);
			
			if (linesIntersect(startDroneLocation.point, endDroneLocation.point, boundaryStartPoint, boundaryEndPoint)) {
				return false;
			}
			
		}
		
		return true;
	}
	
	private boolean isValidDronePath(DronePath dronePath) {
		
		if (dronePathIsOverBuildings(dronePath)) {
			return false;
		}
		
		if (!dronePathInsideFlyZone(dronePath)) {
			return false;
		}
		
		return true;
	}
	
	private boolean isValidDroneLocation(DroneLocation droneLocation) {
		return locationInsideFlyZone(droneLocation) && !locationOverBuildings(droneLocation);
	}
	
	private Graph<DroneLocation, DronePath> buildValidDroneLocationsGraph(Graph<DroneLocation, DronePath> triangleGraph) {
		
		var validDroneLocationsGraph = new DefaultUndirectedWeightedGraph<DroneLocation, DronePath>(DronePath.class);
		
		var droneLocations = triangleGraph.vertexSet();
		
		for (var droneLocation : droneLocations) {
			if (isValidDroneLocation(droneLocation)) {
				validDroneLocationsGraph.addVertex(droneLocation);
			}
		}
		
		var dronePaths = triangleGraph.edgeSet();
		
		for (var dronePath : dronePaths) {
			if (isValidDronePath(dronePath)) {
				validDroneLocationsGraph.addEdge(dronePath.vertex1, dronePath.vertex2, dronePath);
				validDroneLocationsGraph.setEdgeWeight(dronePath, 1);
			}
		}
		
		return validDroneLocationsGraph;
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
				}
			}
		}
		
		for (var droneLocation : triangleGraph.vertexSet()) {
			if (isStartEndLocation(droneLocation)) {
				droneLocation.isStart = true;
				droneLocationsToVisit.add(droneLocation);
				break;
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

	public GraphPath<DroneLocation, DronePath> setStartEndLocation(
			GraphPath<DroneLocation, DronePath> randomStartDronePathRoute) {
		
		var edges = randomStartDronePathRoute.getEdgeList();
		System.out.println("all edges in setStart: " + edges);
		var verticies = randomStartDronePathRoute.getVertexList();
		
		var randomStartGraph = new DefaultUndirectedWeightedGraph<DroneLocation, DronePath>(DronePath.class);
		
		for (var vertex : verticies) {
			randomStartGraph.addVertex(vertex);
		}
		for (var edge : edges) {
			System.out.println("edge: " + edge);
			randomStartGraph.addEdge(edge.vertex1, edge.vertex2, edge);
		}
		
		return new GraphWalk<DroneLocation, DronePath>(randomStartGraph, startEndLocation, startEndLocation,
				verticies, edges, 0.1);
	}
	
	public Graph<DroneLocation, DronePath> convertToDronePathsGraph(
			GraphPath<DroneLocation, SensorPath> sensorRoute) {
		
		var edges = sensorRoute.getEdgeList();
		
		var droneRouteGraph = new DefaultUndirectedWeightedGraph<DroneLocation, DronePath>(DronePath.class);
		
		System.out.println("sensor route size:  " + sensorRoute.getLength());
//		System.out.println("sensor route size:  " + sensorRoute.get());
		
		for (var sensorEdge : edges) {
//			System.out.println("sensorePath edge size: " + sensorEdge.locations.size());
			for (var droneLocation : sensorEdge.locations) {
				droneRouteGraph.addVertex(droneLocation);
				if (droneLocation.equals(startEndLocation)) {
					System.out.println("drone added equals start end");
				}
//				System.out.println(droneRouteGraph.containsVertex(droneLocation));
//				if (startEndLocation.equals(droneLocation)) {
//					System.out.println("we meet start end location");
//					System.out.println("contains start: " + droneRouteGraph.containsVertex(startEndLocation));
//				}
			}
		}
		
//		System.out.println("vertex set: " + droneRouteGraph.vertexSet());
		
		for (var vertex : droneRouteGraph.vertexSet()) {
			if (startEndLocation.equals(vertex)) {
				System.out.println("we meet start end location");
				System.out.println("contains start: " + droneRouteGraph.containsVertex(startEndLocation));
			}
		}
		
//		System.out.println("contains start: " + droneRouteGraph.containsVertex(startEndLocation));
		
		for (var sensorEdge : edges) {
			for (var dronePath : sensorEdge.paths) {
				droneRouteGraph.addEdge(dronePath.vertex1, dronePath.vertex2, dronePath);
				droneRouteGraph.setEdgeWeight(dronePath, 1);
			}
		}
		
		
		return droneRouteGraph;
		
	}
	
	private List<DroneLocation> getGraphWalk(Graph<DroneLocation, DronePath> droneRouteGraph) {
		
		var seenLocations = new HashSet<DroneLocation>();
		var graphWalk = new ArrayList<DroneLocation>();
		System.out.println("vertex size: " + droneRouteGraph.vertexSet().size());
		System.out.println("edge size: " + droneRouteGraph.edgeSet().size());
		
//		var features = new ArrayList<Feature>();
//		for (var edge : droneRouteGraph.edgeSet()) {
//			features.add(Feature.fromGeometry(edge.lineString));
//			System.out.println(Math.sqrt(
//					Math.pow(edge.vertex1.lon - edge.vertex2.lon, 2)
//					+ Math.pow(edge.vertex1.lat - edge.vertex2.lat, 2)
//					));
//		}
//		for (var vertex : droneRouteGraph.vertexSet()) {
//			features.add(Feature.fromGeometry(vertex.point));
//		}
//		System.out.println("fc: " + FeatureCollection.fromFeatures(features).toJson());
		
		DroneLocation currentLocation = startEndLocation;
		while (!seenLocations.contains(currentLocation)) {
			graphWalk.add(currentLocation);
			seenLocations.add(currentLocation);
			
			var droneLocationEdges = droneRouteGraph.edgesOf(currentLocation);
			
			for (var droneLocationEdge : droneLocationEdges) {
				var droneLocation1 = droneLocationEdge.vertex1;
				var droneLocation2 = droneLocationEdge.vertex2;
				
				if (seenLocations.contains(droneLocation1) && seenLocations.contains(droneLocation2)) {
					continue;
				} else if (seenLocations.contains(droneLocation1)) {
					currentLocation = droneLocation2;
					break;
				} else if (seenLocations.contains(droneLocation2)) {
					currentLocation = droneLocation1;
					break;
				}
			}
		}
		return graphWalk;
		
	}
	
	public List<DroneLocation> buildBestRoute() {
		
		buildFlyZone();
		var triangleGraph = buildTriangleGraph();
		var validDroneLocationsGraph = buildValidDroneLocationsGraph(triangleGraph);
		var droneLocationsToVisit = findDroneLocationsToVisit(validDroneLocationsGraph);
		var simpleSensorGraph = buildShortestPaths(validDroneLocationsGraph, droneLocationsToVisit);
		var christofides = new ChristofidesThreeHalvesApproxMetricTSP<DroneLocation, SensorPath>();
		var sensorRoute = christofides.getTour(simpleSensorGraph);
		
		// I have graph of sensor edges
		// I want to create a graph of drone edges
		// add all drone edges and drone locations to new graph
		
		var droneRouteGraph = convertToDronePathsGraph(sensorRoute);
		
		// I have a graph of the points
		// just get a walk from start to end node
		
		var droneRouteGraphWalk = getGraphWalk(droneRouteGraph);
		
		
//		return droneRouteGraphWalk;
		return droneRouteGraphWalk;
	}

}