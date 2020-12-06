package uk.ac.ed.inf.aqmaps;


import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;

public class Route {
	
	private final List<DroneLocation> droneLocationsToVisit;
	private final SensorCollection unvisitedSensors;
	
	private Route(RouteBuilder builder) {
		this.droneLocationsToVisit = builder.droneLocationsToVisit;
		this.unvisitedSensors = builder.unvisitedSensors;
    }
	
	public List<DroneLocation> getDroneLocationsToVisit() {
		return droneLocationsToVisit;
	}
	
	public SensorCollection getUnvisitedSensors() {
		return unvisitedSensors;
	}
	
		public static class RouteBuilder {
			
			private static final double ULLON = -3.192473;
			private static final double ULLAT = 55.946233;
			private static final double LRLON = -3.184319;
			private static final double LRLAT = 55.942617;
			private static final int MAX_NUMBER_OF_MOVES = 150;
			private static final double MAX_DIST_TO_SENSOR = 0.0002;
			
			private NoFlyZoneCollection noFlyZoneCollection;
			private SensorCollection allAvailableSensors;
			private SensorCollection unvisitedSensors;
			private DroneLocation startEndLocation;
			private Point upperLeftBoundaryPoint;
			private Point upperRightBoundaryPoint;
			private Point lowerRightBoundaryPoint;
			private Point lowerLeftBoundaryPoint;
			private List<Point> boundaryPointsList;
			private Polygon flyZone;
			private List<DroneLocation>  droneLocationsToVisit;
			
			public RouteBuilder() {
				this.unvisitedSensors = new SensorCollection();
			}
			
			public RouteBuilder setNoFlyZones(NoFlyZoneCollection noFlyZoneCollection) {
				this.noFlyZoneCollection = noFlyZoneCollection;
				return this;
			}
			public RouteBuilder setAvailableSensors(SensorCollection allAvailableSensorsCollection) {
				this.allAvailableSensors = allAvailableSensorsCollection;
				return this;
			}
			public RouteBuilder setStartEndLocation(DroneLocation startEndLocation) {
				this.startEndLocation = startEndLocation;
				return this;
			}
			
			private void setFlyZone() {
				
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
			
			public boolean isFirstRowShifted() {
				double upperLeftLatStart = startEndLocation.getLatitude();
				int numberOfRowsToTop = 0;
				while (upperLeftLatStart < ULLAT) {
					numberOfRowsToTop++;
					upperLeftLatStart += 0.0003;
				}
				
				boolean isShiftedRow = false;
				if (numberOfRowsToTop % 2 == 1) {
					isShiftedRow = true;
				}
				return isShiftedRow;
			}
			
		 	public List<List<DroneLocation>> buildTriangleGridDroneLocations() {
				
				ArrayList<List<DroneLocation>> triangleGrid = new ArrayList<>();
				
				double upperLeftLonStart = startEndLocation.getLongitude();
				while (upperLeftLonStart > ULLON) {
					upperLeftLonStart -= 0.0003;
				}
				double upperLeftLatStart = startEndLocation.getLatitude();
				while (upperLeftLatStart < ULLAT) {
					upperLeftLatStart += 0.0003;
				}
				
				double lowerRightLonEnd = LRLON;
				double lowerRightLatEnd = LRLAT;
				
				boolean isShiftedRow = isFirstRowShifted();
				
				System.out.println("isShiftedRow: " + isShiftedRow);
				double latDecrement = Math.sqrt(Math.pow(0.0003, 2) - Math.pow(0.00015, 2));
				double lonIncrement = 0.0003;
				double lonRowShift = 0.00015;
				
				for (double currLat = upperLeftLatStart; currLat > lowerRightLatEnd; currLat -= latDecrement) {
					var droneLocationsRow = new ArrayList<DroneLocation>();
					for (double currLon = upperLeftLonStart; currLon < lowerRightLonEnd; currLon += lonIncrement) {
						if (isShiftedRow) {
							droneLocationsRow.add(new DroneLocation(round(currLon + lonRowShift), round(currLat)));
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
				var fs = new ArrayList<Feature>();
				for (var a : triangleGrid) {
					for (var b : a) {
						
						fs.add(Feature.fromGeometry(b.getPoint()));
						
					}
				}
				System.out.println("hello my guy " + FeatureCollection.fromFeatures(fs).toJson());
				return triangleGrid;
			}
		 	
		 	private double round(double num) {
		 		return (double)Math.round(num * 100000d) / 100000d;
//		 		return num;
		 	}
			
			public HashSet<DronePath> buildTriangleGridDronePaths(List<List<DroneLocation>> triangleGridDroneLocations) {
				
				var allPaths = new HashSet<DronePath>();
				
				boolean isRowShifted = isFirstRowShifted(); // not always true, can be other way around
				
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
							if (row > 0 && row < triangleGridDroneLocations.size() - 1 && column < triangleGridDroneLocations.get(row).size() - 1) {
								allPaths.add(new DronePath(
										triangleGridDroneLocations.get(row).get(column),
										triangleGridDroneLocations.get(row + 1).get(column + 1))
										);
								allPaths.add(new DronePath(
										triangleGridDroneLocations.get(row).get(column),
										triangleGridDroneLocations.get(row - 1).get(column + 1))
										);
							} else if (row == 0 && column < triangleGridDroneLocations.get(row).size() - 1) {
								
								allPaths.add(new DronePath(
										triangleGridDroneLocations.get(row).get(column),
										triangleGridDroneLocations.get(row + 1).get(column + 1))
										);
							} else if (row == triangleGridDroneLocations.size() - 1 && column < triangleGridDroneLocations.get(row).size() - 1){
								
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
				
				
				var fToPrint = new ArrayList<Feature>();
				for (var path : allPaths) {
					
					var dist = Math.sqrt(
							Math.pow(path.getVertex1().getLongitude() 
									- path.getVertex2().getLongitude(), 2)
							+ Math.pow(path.getVertex1().getLatitude()
									- path.getVertex2().getLatitude(), 2)
							);
					if (dist < 0.00029 || dist > 0.00031) {
//						System.out.println("error, triangle dist is wrong");
//						break;
					}
					
					var line = LineString.fromLngLats(Arrays.asList(path.getVertex1().getPoint(),
							path.getVertex2().getPoint()));
					fToPrint.add(Feature.fromGeometry(line));
					
				}
				System.out.println("triangle grid: " + FeatureCollection.fromFeatures(fToPrint).toJson());
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
					graph.addEdge(dronePath.getVertex1(), dronePath.getVertex2(), dronePath);
					graph.setEdgeWeight(dronePath, 1);
				}
				
				return graph;
				
			}
			
			private boolean locationOverBuildings(DroneLocation droneLocation) {
				
				var dronePoint = droneLocation.getPoint();
				
				for (var noFlyZone : noFlyZoneCollection.getNoFlyZones()) {
					if (TurfJoins.inside(dronePoint, noFlyZone)) {
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
				
				var startDroneLocation = dronePath.getVertex1();
				var endDroneLocation = dronePath.getVertex2();
				
				for (var building : noFlyZoneCollection.getNoFlyZones()) {
					
					if (locationOverBuildings(startDroneLocation) || locationOverBuildings(endDroneLocation)) {
						return true;
					}
					
					var buildingPoints = building.coordinates().get(0);
					for (int startPointIndex = 0; startPointIndex < buildingPoints.size() -1; startPointIndex++) {
						var buildingEdgeStartPoint = buildingPoints.get(startPointIndex);
						var buildingEdgeEndPoint = buildingPoints.get(startPointIndex + 1);
						
						if (linesIntersect(startDroneLocation.getPoint(), endDroneLocation.getPoint(), buildingEdgeStartPoint, buildingEdgeEndPoint)) {
							return true;
						}
					}
				}
				return false;
				
			}
			
			private boolean locationInsideFlyZone(DroneLocation droneLocation) {
				
				var dronePoint = droneLocation.getPoint();
				
				if (TurfJoins.inside(dronePoint, flyZone)) {
					return true;
				}
				
				return false;
			}
			
			private boolean dronePathInsideFlyZone(DronePath dronePath) {
				
				var startDroneLocation = dronePath.getVertex1();
				var endDroneLocation = dronePath.getVertex2();
				
				if (!locationInsideFlyZone(startDroneLocation) 
						|| !locationInsideFlyZone(endDroneLocation)) {
					return false;
				}
				
				for (int startPointIndex = 0; startPointIndex < boundaryPointsList.size() - 1; startPointIndex++) {
					var boundaryStartPoint = boundaryPointsList.get(startPointIndex);
					var boundaryEndPoint = boundaryPointsList.get(startPointIndex + 1);
					
					if (linesIntersect(startDroneLocation.getPoint(), endDroneLocation.getPoint(), boundaryStartPoint, boundaryEndPoint)) {
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
						validDroneLocationsGraph.addEdge(dronePath.getVertex1(), dronePath.getVertex2(), dronePath);
						validDroneLocationsGraph.setEdgeWeight(dronePath, 1);
					}
				}
				
				return validDroneLocationsGraph;
			}
			
			public double calcDist(double lon1, double lat1, double lon2, double lat2) {
				return Math.sqrt(Math.pow(lon1 - lon2, 2) + Math.pow(lat1 - lat2, 2));
			}
			
			public boolean sensorIsWithinDistance(Sensor sensor, DroneLocation droneLocation) {
				
				double distance = calcDist(
						sensor.getLongitude(), sensor.getLatitude(),
						droneLocation.getLongitude(), droneLocation.getLatitude()
						);
				
				return distance <= MAX_DIST_TO_SENSOR;
				
			}
			
			public boolean isStartEndLocation(DroneLocation droneLocation) {
				
				double lonDiff = Math.abs(droneLocation.getLongitude() - startEndLocation.getLongitude());
				double latDiff = Math.abs(droneLocation.getLatitude() - startEndLocation.getLatitude());
				
				if (lonDiff < 0.0001 && latDiff < 0.0001) {
					return true;
				}
				return false;
				
			}
			
			public Set<DroneLocation> findDroneLocationsToVisit(Graph<DroneLocation, DronePath> triangleGraph) {
				
				var droneLocationsToVisit = new HashSet<DroneLocation>();
				
				for (var sensor : allAvailableSensors.getSensors()) {
					for (var droneLocation : triangleGraph.vertexSet()) {
						if (sensorIsWithinDistance(sensor, droneLocation)) {
							droneLocationsToVisit.add(droneLocation);
							droneLocation.setIsNearSensor(true);
							droneLocation.setNearbySensor(sensor);
							break;
						}
					}
				}
				
				for (var droneLocation : triangleGraph.vertexSet()) {
					if (isStartEndLocation(droneLocation)) {
						droneLocation.setIsStart(true);
						droneLocationsToVisit.add(droneLocation);
						break;
					}
				}
				return droneLocationsToVisit;
			}
			
			private Graph<DroneLocation, SensorPath> buildShortestPathCompleteSensorGraph(
					Graph<DroneLocation, DronePath> triangleGraph, List<DroneLocation> sortedDroneLocationsToVisit) {
				
				var simpleSensorGraph = new DefaultUndirectedWeightedGraph<DroneLocation, SensorPath>(SensorPath.class);
				System.out.println(sortedDroneLocationsToVisit.size());
				
				int count = 0;
				for (int sourceIndex = 0; sourceIndex < sortedDroneLocationsToVisit.size(); sourceIndex++) {
					for (int sinkIndex = sourceIndex + 1; sinkIndex < sortedDroneLocationsToVisit.size(); sinkIndex++) {
						
						var sourceDroneLocation = sortedDroneLocationsToVisit.get(sourceIndex);
						var sinkDroneLocation = sortedDroneLocationsToVisit.get(sinkIndex);
						
						var dijk = new BidirectionalDijkstraShortestPath<DroneLocation, DronePath>(triangleGraph);
						var graphWalk = dijk.getPath(sourceDroneLocation, sinkDroneLocation);
						var vertices = graphWalk.getVertexList();
						var edges = graphWalk.getEdgeList();
						
						simpleSensorGraph.addVertex(sourceDroneLocation);
						simpleSensorGraph.addVertex(sinkDroneLocation);
						
						var sensorPath = new SensorPath(vertices, edges, sourceDroneLocation, sinkDroneLocation);
						simpleSensorGraph.addEdge(sourceDroneLocation, sinkDroneLocation, sensorPath);
						simpleSensorGraph.setEdgeWeight(sensorPath, sensorPath.weight);
						count++;
					}
					
				}
				System.out.println(count);
				return simpleSensorGraph;
			}
			
			private List<Integer> buildCorrectOrderSensorIndexes(GraphPath<DroneLocation, SensorPath> sensorRouteGraph) {
				var startEndIndex = findStartEndIndex(sensorRouteGraph);
				
				var indexes = new ArrayList<Integer>();
				
				for (int i = startEndIndex; i < sensorRouteGraph.getLength(); i++) {
					indexes.add(i);
				}
				
				for (int i = 0; i < startEndIndex; i++) {
					indexes.add(i);
				}
				
				return indexes;
			}
			
			private int findStartEndIndex(GraphPath<DroneLocation, SensorPath> sensorRouteGraph) {
				
				var sensorDroneLocations = sensorRouteGraph.getVertexList();
				
				for (int i = 0; i < sensorDroneLocations.size(); i++) {
					if (sensorDroneLocations.get(i).equals(startEndLocation)) {
						return i;
					}
				}
				
				return 0;
			}
			
			private List<DroneLocation> parseDroneLocations(GraphPath<DroneLocation, SensorPath> sensorRoute) {
				
				var orderedDroneLocationTour = new ArrayList<DroneLocation>();
				var sensorPaths = sensorRoute.getEdgeList();
				var sensorDroneLocations = sensorRoute.getVertexList();
		
				var sensorIndexes = buildCorrectOrderSensorIndexes(sensorRoute);
				System.out.println(sensorIndexes);
						
				for (var sensorIndex : sensorIndexes) {
					
					var sourceSensorDroneLocation = sensorDroneLocations.get(sensorIndex);
					
		
					var sensorPath = sensorPaths.get(sensorIndex);
					var dronePaths = sensorPath.getLocationsFrom(sourceSensorDroneLocation);
					
					var currentDroneLocation = sourceSensorDroneLocation;
					
					for (int droneIndex = 0; droneIndex < dronePaths.size(); droneIndex++) {
						
						orderedDroneLocationTour.add(currentDroneLocation);
						var dronePath = dronePaths.get(droneIndex);
						var nextDroneLocation = dronePath.connectingDroneLocation(currentDroneLocation);
						currentDroneLocation = nextDroneLocation;
					}
					
				}
				// start of path should be equal to the end of the part to 
				// form a closed loop tour.
				orderedDroneLocationTour.add(orderedDroneLocationTour.get(0));
				
				System.out.println("orderedDroneLocationPath vertex list size " + orderedDroneLocationTour.size());
				
				return orderedDroneLocationTour;
				
			}
			
			public void printTour(GraphPath<DroneLocation, SensorPath> sensorRoute) {
				
				var fs = new ArrayList<Feature>(); 
				for (var n : sensorRoute.getVertexList()) {
					fs.add(Feature.fromGeometry(n.getPoint()));
				}
				for (var e : sensorRoute.getEdgeList()) {
					for (var ps : e.vertex1ToVertex2) {
						var line = LineString.fromLngLats(Arrays.asList(ps.getVertex1().getPoint(), ps.getVertex2().getPoint()));
						fs.add(Feature.fromGeometry(line));
					}
				}
				System.out.println("tour geojson: " + FeatureCollection.fromFeatures(fs).toJson());
			}
			
			public boolean hasLegalNumberOfMoves(List<DroneLocation> orderedDroneLocationTour) {
				if (orderedDroneLocationTour.size() - 1 <= MAX_NUMBER_OF_MOVES) {
					// the number of moves to traverse orderedDroneLocationTour
					// is equal to the number of drone locations - 1 since
					// the start location is repeated at the end of the list
					// and because the number of edges in a tour is equal to
					// the number of vertices in the tour.
					return true;
				}
				return false;
			}
			
			private void removeFarthestFromStartEnd(List<DroneLocation> sortedDroneLocationsToVisit) {
				var droneLocation = sortedDroneLocationsToVisit.remove(sortedDroneLocationsToVisit.size() -1);
				unvisitedSensors.add(droneLocation.getNearbySensor());
				
			}
			
			private List<DroneLocation> sortDroneLocationsToVisit(Set<DroneLocation> droneLocationsToVisitSet) {
				 ArrayList<DroneLocation> droneLocationsToVisitList = new ArrayList<DroneLocation>(droneLocationsToVisitSet);
				 System.out.println("number of droneLocations in the set: " + droneLocationsToVisitSet.size());
				 Collections.sort(droneLocationsToVisitList, 
						 (a, b) -> Double.compare(
								 a.calcDistTo(startEndLocation),
						         b.calcDistTo(startEndLocation)
				 	     ));
				
				 return droneLocationsToVisitList;
			}
			
			void printGraph(Graph<DroneLocation, DronePath> graph) {
				var fs = new ArrayList<Feature>(); 
				for (var n : graph.vertexSet()) {
					fs.add(Feature.fromGeometry(n.getPoint()));
				}
				for (var ps : graph.edgeSet()) {
					var line = LineString.fromLngLats(Arrays.asList(ps.getVertex1().getPoint(), ps.getVertex2().getPoint()));
					fs.add(Feature.fromGeometry(line));
				}
				System.out.println("graph tour geojson: " + FeatureCollection.fromFeatures(fs).toJson());
			}
			
			public Route buildBestRoute() {
				
				setFlyZone();
				var triangleGraph = buildTriangleGraph();
				printGraph(triangleGraph);
				var validDroneLocationsGraph = buildValidDroneLocationsGraph(triangleGraph);
				printGraph(validDroneLocationsGraph);
				var droneLocationsToVisit = findDroneLocationsToVisit(validDroneLocationsGraph);
				System.out.println("look for size: " + droneLocationsToVisit.size());
				var sortedDroneLocationsToVisit = sortDroneLocationsToVisit(droneLocationsToVisit);
				
				do {
					System.out.println("hello bitch");
					var completeSensorGraph = buildShortestPathCompleteSensorGraph(validDroneLocationsGraph, 
							sortedDroneLocationsToVisit);
					
					var christofides = new ChristofidesThreeHalvesApproxMetricTSP<DroneLocation, SensorPath>();
					var sensorTour = christofides.getTour(completeSensorGraph);
					
					printTour(sensorTour);
					
					System.out.println("chrisofides algo path size: " + sensorTour.getEdgeList().size());
					var orderedDroneLocationTour = parseDroneLocations(sensorTour);
					
					if (hasLegalNumberOfMoves(orderedDroneLocationTour)) {
						this.droneLocationsToVisit = orderedDroneLocationTour;
						System.out.println("resulting number of vertices: " + droneLocationsToVisit.size());
						break;
					} 
					// over the limit so must remove a droneLocation to visit.
					// choose the farthest from startEnd location.
					removeFarthestFromStartEnd(sortedDroneLocationsToVisit);
				
				} while (!droneLocationsToVisit.isEmpty());
//				
				return new Route(this);
				
		}
	}
}