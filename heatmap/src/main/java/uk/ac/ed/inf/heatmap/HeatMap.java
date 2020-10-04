package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HeatMap {

	private final List<List<Integer>> predictions;
	private final double ullat;
	private final double ullon;
	private final double lrlat;
	private final double lrlon;
	private final String geoJSON;
	
	private HeatMap(HeatMapBuilder builder) {
		this.predictions = builder.predictions;
		this.ullat = builder.ullat;
		this.ullon = builder.ullon;
		this.lrlat = builder.lrlat;
		this.lrlon = builder.lrlon;
		this.geoJSON = builder.geoJSON;
	}
	
	public String getGeoJSON() {
		return this.geoJSON;
	}
	
 	public static class HeatMapBuilder {
		private List<List<Integer>> predictions;
		private double ullat;
		private double ullon;
		private double lrlat;
		private double lrlon;
		private String geoJSON;
		
		public HeatMapBuilder(List<List<Integer>> predictions) {
			this.predictions = predictions;
		}
		public HeatMapBuilder ullat(double ullat) {
			this.ullat = ullat;
			return this;
		}
		public HeatMapBuilder ullon(double ullon) {
			this.ullon = ullon;
			return this;
		}
		public HeatMapBuilder lrlat(double lrlat) {
			this.lrlat = lrlat;
			return this;
		}
		public HeatMapBuilder lrlon(double lrlon) {
			this.lrlon = lrlon;
			return this;
		}
		public String buildGeoJSON() {
			
			return "Hello, world!";
			
		}
		public void validateHeatMapObject(HeatMap geoJSON) {
			
			
			
		}
		public HeatMap build() {
			this.geoJSON = buildGeoJSON();
			HeatMap heatMap = new HeatMap(this);
			validateHeatMapObject(heatMap);
			return heatMap;
		}
	}
}
