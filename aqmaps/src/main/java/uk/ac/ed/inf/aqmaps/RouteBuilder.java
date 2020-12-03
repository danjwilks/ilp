package uk.ac.ed.inf.aqmaps;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.mapbox.geojson.FeatureCollection;

import org.jgrapht.Graph;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.GraphWalk;

public class RouteBuilder {
	
	private double[] flyzone;
	private FeatureCollection buildings;
	private List<Sensor> sensors;
	private double[] startEndLocation;
	
	
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
	
	public Graph<String, DefaultEdge> buildTriangleGraph() {
		
		var graph = new DefaultUndirectedWeightedGraph<String, DefaultEdge>(DefaultEdge.class);

//		TODO implement
		var createdLocations = new HashSet<DroneLocation>();
		var createdDronePaths = new HashSet<DronePath>();
		
		var locationsToBuildPaths = new ArrayDeque<DroneLocation>();
		
		while (!locationsToBuildPaths.isEmpty()) {
			
			DroneLocation currentLocation = locationsToBuildPaths.remove();
			
			// create all 6 adj points
			// create all 6 adj edges
			// only add to graph if they're not seen already.
			
			// north east (30 degrees) -> north (90 degrees) -> etc
			
			for (int degree = 30; degree <= 330; degree += 60) {
				
				var adjacentLocation = buildFromPointInDirection(currentLocation, degree);
				if (!isValidLocation(adjacentLocation)) {
					continue;
				}
				
				var dronePath = buildDronePath(currentLocation, adjacentLocation);
				
				if (!graph.containsVertex(adjacentLocation)) {
//					haven't seen this location before
					graph.addVertex(adjacentLocation);
					locationsToBuildPaths.add(adjacentLocation);
				}
				
				if (!graph.containsEdge(dronePath)) {
					graph.addEdge(dronePath, 1);
				}	
			}
		}
		
		return graph;
		
	}
	
	public void addSensors(Graph<String, DefaultEdge> triangleGraph) {
//		TODO implement
		
		
		return;
	}
	
	private Graph<String, DefaultEdge> removeLongPathsFromSensors(Graph<String, DefaultEdge> triangleGraph) {
		// TODO Auto-generated method stub
		return triangleGraph;
	}
	
	public String buildBestRoute() {
		
		var triangleGraph = buildTriangleGraph();
		addSensors(triangleGraph);
		var shortestPathsGraph = removeLongPathsFromSensors(triangleGraph);
		
		var christofides = new ChristofidesThreeHalvesApproxMetricTSP<String, DefaultEdge>();
		var graphWalk = christofides.getTour(shortestPathsGraph);
		var vertices = graphWalk.getVertexList();
		var edges = graphWalk.getEdgeList();
		System.out.println(vertices);
		System.out.println(edges);
		
		
		return sensors.toString();
	}

}