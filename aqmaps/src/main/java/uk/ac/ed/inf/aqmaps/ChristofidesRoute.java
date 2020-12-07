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

/**
 * @author s1851664
 * 
 * Route calculated using Christofides algorithm
 *
 */
public class ChristofidesRoute implements Route {
	
	/**
	 * The locations the drone will visit, given in order.
	 * 
	 * Repeats the start location as the end location
	 * to form a closes loop.
	 */
	private final List<DroneLocation> droneLocationsToVisit;
	
	/**
	 * The sensors that the drone will not be able to visit. 
	 */
	private final SensorCollection unvisitedSensors;
	
	/**
	 * Route, only called by its static builder class.
	 * 
	 * @param builder object containing the information
	 *        required to build the route.
	 */
	private ChristofidesRoute(RouteBuilder builder) {
		this.droneLocationsToVisit = builder.droneLocationsToVisit;
		this.unvisitedSensors = builder.unvisitedSensors;
    }
	
	/**
	 * @return The locations the drone will visit, given in order.
	 */
	public List<DroneLocation> getDroneLocationsToVisit() {
		return droneLocationsToVisit;
	}
	
	/**
	 * @return The sensors that the drone will not be able 
	 *         to visit. 
	 */
	public SensorCollection getUnvisitedSensors() {
		return unvisitedSensors;
	}
	
	/**
	 * @author S1851664
	 * 
	 * Builder class used to create the Christofides Route.
	 * 
	 * Builds a complete graph of drone paths between sensors 
	 * and the start location. Then uses Christofides algorithm 
	 * to determine the minimum tour of this graph. We remove 
	 * sensors from the complete graph until the minimum tour 
	 * distance is within the predefined maximum.
	 */
	public static class RouteBuilder {
		
		/**
		 * The upper left longitude of the fly zone
		 */
		private static final double ULLON = -3.192473;
		
		/**
		 * The upper left latitude of the fly zone
		 */
		private static final double ULLAT = 55.946233;
		
		/**
		 * The lower right longitude of the fly zone
		 */
		private static final double LRLON = -3.184319;
		
		/**
		 * The lower right latitude of the fly zone
		 */
		private static final double LRLAT = 55.942617;
		
		/**
		 * The maximum number of moves the drone can 
		 * traverse.
		 */
		private static final int MAX_NUMBER_OF_MOVES = 1000;
		
		/**
		 * The maximum distance the drone can be to a
		 * sensor for it to get a reading.
		 */
		private static final double MAX_DIST_TO_SENSOR = 0.0002;
		
		/**
		 * The exact distance of a move of the drone.
		 */
		private static final double MOVE_DISTANCE = 0.0003;
		
		/**
		 * Collection of no fly zones.
		 */
		private NoFlyZoneCollection noFlyZoneCollection;
		
		/**
		 * All sensors that are available for the drone 
		 * to visit on the particular day. 
		 */
		private SensorCollection allAvailableSensors;
		
		/**
		 * Collection of sensors that are not visited
		 * on this route.	 
		 */
		private SensorCollection unvisitedSensors;
		
		/**
		 * The start and end location for the route.
		 */
		private DroneLocation startEndLocation;
		
		/**
		 * The upper left point of the fly zone.
		 */
		private Point upperLeftBoundaryPoint;
		
		/**
		 * The upper right point of the fly zone.
		 */
		private Point upperRightBoundaryPoint;
		
		/**
		 * The lower right point of the fly zone.
		 */
		private Point lowerRightBoundaryPoint;
		
		/**
		 * The lower left point of the fly zone.
		 */
		private Point lowerLeftBoundaryPoint;
		
		/**
		 * The points of the corners of the fly zone.
		 */
		private List<Point> boundaryPointsList;
		
		/**
		 * The fly zone allowed for the drone. 
		 */
		private Polygon flyZone;
		
		/**
		 * The locations the drone will visit, given in order.
		 */
		private List<DroneLocation>  droneLocationsToVisit;
		
		/**
		 * Starts the process of building the route.
		 */
		public RouteBuilder() {
			this.unvisitedSensors = new SensorCollection();
		}
		
		/**
		 * Sets the no fly zones of the route.
		 * 
		 * @param  noFlyZoneCollection no fly zones of the 
		 *         drone.
		 * @return itself, following the builder pattern
		 */
		public RouteBuilder setNoFlyZones(NoFlyZoneCollection noFlyZoneCollection) {
			this.noFlyZoneCollection = noFlyZoneCollection;
			return this;
		}
		/**
		 * Sets the available sensors to visit on the
		 * route.
		 * 
		 * @param allAvailableSensorsCollection
		 * @return itself, following the builder pattern
		 */
		public RouteBuilder setAvailableSensors(SensorCollection allAvailableSensorsCollection) {
			this.allAvailableSensors = allAvailableSensorsCollection;
			return this;
		}
		/**
		 * Sets the start and end location of the route.
		 * 
		 * @param startEndLocation
		 * @return itself, following the builder pattern
		 */
		public RouteBuilder setStartEndLocation(DroneLocation startEndLocation) {
			this.startEndLocation = startEndLocation;
			return this;
		}
		
		/**
		 * sets the valid fly zone of the route.
		 */
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
		
		/**
		 * Helper function for creating the triangle graph.
		 * 
		 * @return true if the drone locations within the 
		 * 		   first row within the rows of all the 
		 * 		   drone locations should have their 
		 * 		   latitude shifted to the right.
		 */
		private boolean isFirstRowShifted() {
			
			var upperLeftLatStart = startEndLocation.getLatitude();
			int numberOfRowsToTop = 0;
			while (upperLeftLatStart < ULLAT) {
				upperLeftLatStart = upperLeftLatStart += MOVE_DISTANCE;
				numberOfRowsToTop++;
			}
			
			boolean isShiftedRow = false;
			if (numberOfRowsToTop % 2 == 0) {
				isShiftedRow = true;
			}
			System.out.println(isShiftedRow);
			System.out.println(numberOfRowsToTop);
			return isShiftedRow;
		}
		
	 	/**
	 	 * Determines the height of the equilateral triangles 
	 	 * used within the equilateral triangle graph.
	 	 * 
	 	 * @return the triangle height used for the triangle
	 	 * 		   graph.
	 	 */
	 	private double getTriangleHeight() {
	 		
			double triangleEdgeLength = MOVE_DISTANCE;
	 		double halfTriangleEdgeLength = triangleEdgeLength / 2;
			double squaredDifference = Math.pow(triangleEdgeLength,2) - Math.pow(halfTriangleEdgeLength,2);
			double triangleHeight = Math.sqrt(squaredDifference);
			return triangleHeight;
	 	}
		
		/**
		 * Builds a equilateral triangle grid of drone locations 
		 * that define the valid locations that the drone can take 
		 * in the path.
		 * 
		 * @return equilateral triangle grid of locations that the 
		 *         drone can visit.
		 */
		private List<List<DroneLocation>> buildTriangleGridDroneLocations() {
			
			ArrayList<List<DroneLocation>> triangleGrid = new ArrayList<>();
			var triangleHeight = getTriangleHeight();
			
			var upperLeftLonStart = startEndLocation.getLongitude();
			while (upperLeftLonStart > ULLON) {
				upperLeftLonStart = upperLeftLonStart - MOVE_DISTANCE;
			}
			var upperLeftLatStart = startEndLocation.getLatitude();
			while (upperLeftLatStart < ULLAT) {
				upperLeftLatStart = upperLeftLatStart += triangleHeight;
			}
			
			var lowerRightLonEnd = LRLON;
			var lowerRightLatEnd = LRLAT;
			
			boolean isShiftedRow = isFirstRowShifted();
			
			System.out.println("isShiftedRow: " + isShiftedRow);
			
			
			System.out.println("triangleHeight: " + triangleHeight);
			var triangleEdgeLength = MOVE_DISTANCE;
			var halfTriangleEdgeLength = triangleEdgeLength / 2;
			
			for (var currLat = upperLeftLatStart; currLat > lowerRightLatEnd; currLat = currLat -= triangleHeight) {
				var droneLocationsRow = new ArrayList<DroneLocation>();
				for (var currLon = upperLeftLonStart; currLon < lowerRightLonEnd; currLon = currLon += triangleEdgeLength) {
					if (isShiftedRow) {
						droneLocationsRow.add(new DroneLocation(currLon + halfTriangleEdgeLength, currLat));
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
			var fs = new ArrayList<Feature>();
			for (var a : triangleGrid) {
				for (var b : a) {
					
					fs.add(Feature.fromGeometry(b.getPoint()));
					
				}
			}
			System.out.println("hello my guy " + FeatureCollection.fromFeatures(fs).toJson());
			return triangleGrid;
		}
		
		/**
		 * Builds all the equilateral triangle paths 
		 * between the drone locations within the triangle 
		 * grid passed as the parameter. 
		 * 
		 * @param  triangleGridDroneLocations the 2D grid of 
		 * 		   valid drone locations.
		 * @return the paths of the triangles edges of the 
		 *         triangle grid.
		 */
		private HashSet<DronePath> buildTriangleGridDronePaths(List<List<DroneLocation>> triangleGridDroneLocations) {
			
			var allPaths = new HashSet<DronePath>();
			
			boolean isRowShifted = isFirstRowShifted();
			
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
//				System.out.println("triangle grid: " + FeatureCollection.fromFeatures(fToPrint).toJson());
			return allPaths;
			
		}
		
		/**
		 * Builds a graph of connecting equilateral triangles.
		 * 
		 * @return a graph of connecting equilateral triangles.
		 */
		private Graph<DroneLocation, DronePath> buildTriangleGraph() {
			
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
				graph.setEdgeWeight(dronePath, MOVE_DISTANCE);
			}
			
			return graph;
			
		}
		
		/**
		 * Determines if the given drone location is over 
		 * a no fly zone.
		 * 
		 * @param  droneLocation
		 * @return true if drone location is over a no 
		 *         fly zone.
		 */
		private boolean locationOverNoFlyZone(DroneLocation droneLocation) {
			
			var dronePoint = droneLocation.getPoint();
			
			for (var noFlyZone : noFlyZoneCollection.getNoFlyZones()) {
				if (TurfJoins.inside(dronePoint, noFlyZone)) {
					return true;
				}
			}
			
			return false;
		}
		
		/**
		 * Determines if two line segments intersect.
		 * 
		 * @param line1Start vertex of line1 segment.
		 * @param line1End   other vertex of line1 segment.
		 * @param line2Start vertex of line2 segment.
		 * @param line2End   other vertex of line2 segment.
		 * @return true if lines segments intersect.
		 */
		private boolean linesIntersect(Point line1Start, Point line1End, Point line2Start, Point line2End) {
			
			return Line2D.linesIntersect(
					line1Start.longitude(), line1Start.latitude(),
					line1End.longitude(), line1End.latitude(),
					line2Start.longitude(), line2Start.latitude(),
					line2End.longitude(), line2End.latitude()
					);
			
		}
		
		/**
		 * Determines if drone path passes through a no fly zone.
		 * 
		 * @param  dronePath
		 * @return true if drone path passes through a no fly
		 *         zone.
		 */
		private boolean dronePathIsOverNoFlyZone(DronePath dronePath) {
			
			var startDroneLocation = dronePath.getVertex1();
			var endDroneLocation = dronePath.getVertex2();
			
			for (var building : noFlyZoneCollection.getNoFlyZones()) {
				
				if (locationOverNoFlyZone(startDroneLocation) || locationOverNoFlyZone(endDroneLocation)) {
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
		
		/**
		 * Determines if given drone location is within a boundary fly zone.
		 * 
		 * @param droneLocation
		 * @return true if given drone location is within a the 
		 *         fly zone boundary.
		 */
		private boolean locationInsideFlyZone(DroneLocation droneLocation) {
			
			var dronePoint = droneLocation.getPoint();
			
			if (TurfJoins.inside(dronePoint, flyZone)) {
				return true;
			}
			
			return false;
		}
		
		/**
		 * Determines if given drone location is within a the fly zone
		 * boundary.
		 * 
		 * @param dronePath
		 * @return true if the drone path is inside the fly zone.
		 */
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
		
		/**
		 * Determines if the drone path is a valid drone path.
		 * 
		 * @param dronePath
		 * @return true if the drone path is valid.
		 */
		private boolean isValidDronePath(DronePath dronePath) {
			
			if (dronePathIsOverNoFlyZone(dronePath)) {
				return false;
			}
			
			if (!dronePathInsideFlyZone(dronePath)) {
				return false;
			}
			
			return true;
		}
		
		/**
		 * Determines if drone location is valid.
		 * 
		 * @param droneLocation
		 * @return true if drone location is valid.
		 */
		private boolean isValidDroneLocation(DroneLocation droneLocation) {
			return locationInsideFlyZone(droneLocation) && !locationOverNoFlyZone(droneLocation);
		}
		
		/**
		 * Builds a valid drone location graph.
		 * 
		 * @param  triangleGraph containing all possible
		 * 		   drone locations within.
		 * @return a graph containing all the valid paths
		 *         and locations of the triangle graph.
		 */
		private Graph<DroneLocation, DronePath> buildValidDroneLocationsGraph(Graph<DroneLocation, DronePath> triangleGraph) {
			
			var validDroneLocationsGraph = new DefaultUndirectedWeightedGraph<DroneLocation, DronePath>(DronePath.class);
			
			var droneLocations = triangleGraph.vertexSet();
			
			for (var droneLocation : droneLocations) {
				if (isValidDroneLocation(droneLocation)) {
					validDroneLocationsGraph.addVertex(droneLocation);
				}
			}
			
			var dronePaths = triangleGraph.edgeSet();
			var arbitraryEdgeWeight = 1;
			for (var dronePath : dronePaths) {
				if (isValidDronePath(dronePath)) {
					validDroneLocationsGraph.addEdge(dronePath.getVertex1(), dronePath.getVertex2(), dronePath);
					validDroneLocationsGraph.setEdgeWeight(dronePath, arbitraryEdgeWeight);
				}
			}
			
			return validDroneLocationsGraph;
		}
		
		/**
		 * Calculates the distance between two points
		 * 
		 * @param lon1 longitude of point 1.
		 * @param lat1 latitude of point 1.
		 * @param lon2 longitude of point 2.
		 * @param lat2 latitude of point 2.
		 * @return the distance between the two points.
		 */
		private double calcDist(double lon1, double lat1, double lon2, double lat2) {
			return Math.sqrt(Math.pow(lon1 - lon2, 2) + Math.pow(lat1 - lat2, 2));
		}
		
		/**
		 * Determines if the given sensor is within
		 * distance of the drone location such that the
		 * drone would be able to retrieve its sensor 
		 * information.
		 * 
		 * @param sensor
		 * @param droneLocation
		 * @return true if the sensor is within distance
		 *         to the drone location such that the 
		 *         drone can read the sensor information.
		 */
		private boolean sensorIsWithinDistance(Sensor sensor, DroneLocation droneLocation) {
			
			double distance = calcDist(
					sensor.getLongitude(), sensor.getLatitude(),
					droneLocation.getLongitude(), droneLocation.getLatitude()
					);
			
			return distance <= MAX_DIST_TO_SENSOR;
			
		}
		
		/**
		 * Determines the important drone locations that 
		 * the drone should visit. These include the start
		 * and end location, and the drone locations that 
		 * are near to sensors.
		 * 
		 * @param  validDroneLocations
		 * @return collection of important drone locations 
		 *         that the drone should visit.
		 */
		private Set<DroneLocation> findDroneLocationsToVisit(Graph<DroneLocation, DronePath> validDroneLocations) {
			
			var droneLocationsToVisit = new HashSet<DroneLocation>();
			
			for (var sensor : allAvailableSensors.getSensors()) {
				for (var droneLocation : validDroneLocations.vertexSet()) {
					if (sensorIsWithinDistance(sensor, droneLocation)) {
						droneLocationsToVisit.add(droneLocation);
						droneLocation.setIsNearSensor(true);
						droneLocation.setNearbySensor(sensor);
						break;
					}
				}
			}
			
			for (var droneLocation : validDroneLocations.vertexSet()) {
				if (droneLocation.equals(startEndLocation)) {
					droneLocationsToVisit.add(droneLocation);
					break;
				}
			}
			return droneLocationsToVisit;
		}
		
		/**
		 * Builds a complete graph of drone locations and
		 * paths between sensors.
		 * 
		 * @param  triangleGraph
		 * @param  sortedDroneLocationsToVisit
		 * @return a complete graph of drone locations 
		 *         and paths between sensors.
		 */
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
					simpleSensorGraph.setEdgeWeight(sensorPath, sensorPath.getWeight());
					count++;
				}
				
			}
			System.out.println(count);
			return simpleSensorGraph;
		}
		
		/**
		 * Builds a ordered list of indexes such that each 
		 * index corresponds to the drone location that the 
		 * drone should visit to read from a sensor. Sorted
		 * in order that the drone should visit. I.e. the 
		 * minimum tour, as given by the input graph path.
		 * The start location is the first index and the 
		 * last index forming a tour from the start location.
		 * 
		 * @param  sensorRouteGraph a tour of sensors.
		 * @return an list of sensor indexes, sorted in the
		 *         order that the drone should visit. 
		 */
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
		
		/**
		 * Finds the index of the tour given by the input
		 * graph path that corresponds to the start location.
		 * 
		 * @param  sensorRouteGraph
		 * @return the index of the tour given by the input
		 *         graph path that corresponds to the start 
		 *         location.
		 */
		private int findStartEndIndex(GraphPath<DroneLocation, SensorPath> sensorRouteGraph) {
			
			var sensorDroneLocations = sensorRouteGraph.getVertexList();
			
			for (int i = 0; i < sensorDroneLocations.size(); i++) {
				if (sensorDroneLocations.get(i).equals(startEndLocation)) {
					return i;
				}
			}
			
			return 0;
		}
		
		/**
		 * Parses the drone locations to visit from the input 
		 * sensor route. The sensor route is the tour of each 
		 * sensor and the start location.
		 * 
		 * @param  sensorRoute
		 * @return the in order traversal of drone locations
		 *         within the given sensor route.
		 */
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
					var nextDroneLocation = dronePath.getConnectingDroneLocation(currentDroneLocation);
					currentDroneLocation = nextDroneLocation;
				}
				
			}
			// start of path should be equal to the end of the part to 
			// form a closed loop tour.
			orderedDroneLocationTour.add(orderedDroneLocationTour.get(0));
			
			System.out.println("orderedDroneLocationPath vertex list size " + orderedDroneLocationTour.size());
			
			return orderedDroneLocationTour;
			
		}
		
		/** TODO delete
		 * @param sensorRoute
		 */
		private void printTour(GraphPath<DroneLocation, SensorPath> sensorRoute) {
			
			var fs = new ArrayList<Feature>(); 
			for (var n : sensorRoute.getVertexList()) {
				fs.add(Feature.fromGeometry(n.getPoint()));
			}
			for (var e : sensorRoute.getEdgeList()) {
				for (var ps : e.getVertex1ToVertex2()) {
					var line = LineString.fromLngLats(Arrays.asList(ps.getVertex1().getPoint(), ps.getVertex2().getPoint()));
					fs.add(Feature.fromGeometry(line));
				}
			}
			System.out.println("tour geojson: " + FeatureCollection.fromFeatures(fs).toJson());
		}
		
		/**
		 * Determines if given tour of sensors is within
		 * the legal number of moves.
		 * 
		 * The number of moves to traverse orderedDroneLocationTour
		 * is equal to the number of drone locations - 1 since
		 * the start location is repeated at the end of the list
		 * and because the number of edges in a tour is equal to
		 * the number of vertices in the tour.
		 * 
		 * @param  orderedDroneLocationTour
		 * @return true if the tour is within the legal
		 *         number of moves.
		 */
		private boolean hasLegalNumberOfMoves(List<DroneLocation> orderedDroneLocationTour) {
			if (orderedDroneLocationTour.size() - 1 <= MAX_NUMBER_OF_MOVES) {
				return true;
			}
			return false;
		}
		
		/**
		 * Removes the farthest point from the start location.
		 * 
		 * Since input list of drone locations contains the 
		 * current drone locations that are close to sensors,
		 * when we remove a drone location from the list, we 
		 * are essentially making it impossible to visit the
		 * sensor that is near the drone location. This is 
		 * because there is at max one sensor assigned to a 
		 * drone location.
		 * 
		 * @param sortedDroneLocationsToVisit sorted from closest
		 *        to farthest. Contains only locations that are
		 *        near sensors.
		 */
		private void removeFarthestFromStartEnd(List<DroneLocation> sortedDroneLocationsToVisit) {
			var droneLocation = sortedDroneLocationsToVisit.remove(sortedDroneLocationsToVisit.size() -1);
			unvisitedSensors.add(droneLocation.getNearbySensor());
			
		}
		
		/**
		 * Sorts the important drone location to visit by
		 * their distance to the start/end location.
		 * 
		 * @param droneLocationsToVisitSet
		 * @return the sorted list.
		 */
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
		
		/**
		 * 
		 * 
		 * @param graph
		 */
		private void printGraph(Graph<DroneLocation, DronePath> graph) {
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
		
		/**
		 * Builds the route using Christofides algorithm.
		 * 
		 * First builds a equilateral triangle graph and 
		 * then uses this to build a graph of valid drone
		 * locations. We build a complete graph of the 
		 * shortest routes between drone locations that 
		 * are near sensors. We use this graph to find the
		 * minimum tour of sensors. This could be over the
		 * limit of maximum moves, so we remove drone locations 
		 * that are the farthest away from the start location
		 * until we can find a tour of sensors that is within 
		 * our max number of moves. 
		 * 
		 * @return a route corresponding to a tour of sensors
		 */
		public ChristofidesRoute buildBestRoute() {
			
			setFlyZone();
			var triangleGraph = buildTriangleGraph();
			printGraph(triangleGraph);
			var validDroneLocationsGraph = buildValidDroneLocationsGraph(triangleGraph);
			printGraph(validDroneLocationsGraph);
			var droneLocationsToVisit = findDroneLocationsToVisit(validDroneLocationsGraph);
			System.out.println("look for size: " + droneLocationsToVisit.size());
			var sortedDroneLocationsToVisit = sortDroneLocationsToVisit(droneLocationsToVisit);
			
			do {
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
			
			return new ChristofidesRoute(this);
			
	}
	}
}