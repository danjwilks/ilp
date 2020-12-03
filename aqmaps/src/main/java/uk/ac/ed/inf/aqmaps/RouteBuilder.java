package uk.ac.ed.inf.aqmaps;

import java.util.List;
import com.mapbox.geojson.FeatureCollection;

import org.jgrapht.Graph;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

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
	
	public void addVerticies(Graph<Station, DefaultEdge> graph) {
		for (Station station : stations) {
			graph.addVertex(station);
		}
	}
	
	public void addEdges(Graph<Station, DefaultEdge> graph) {
		for (int i = 0; i < stations.size(); i++) {
			for (int j = i + 1; j < stations.size(); j++) {
				Station station1 = stations.get(i);
				Station station2 = stations.get(j);
				graph.addEdge(station1, station2);
				graph.setEdgeWeight(station1, station2, 1);
			}
		}
	}
	
	public Graph<Station, DefaultEdge> buildGraph() {
		
		var graph = new DefaultUndirectedWeightedGraph<Station, DefaultEdge>(DefaultEdge.class);
		
		addVerticies(graph);
		addEdges(graph);
		
		return graph;
		
	}
	
	public String buildBestRoute() {
		
		var graph = buildGraph();
		var christofides = new ChristofidesThreeHalvesApproxMetricTSP<Station, DefaultEdge>();
		System.out.print(christofides.getTour(graph));
		
		return stations.toString();
	}

}