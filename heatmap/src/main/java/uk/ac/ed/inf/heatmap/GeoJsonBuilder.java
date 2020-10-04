package uk.ac.ed.inf.heatmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import static uk.ac.ed.inf.heatmap.Colours.*;

public class GeoJsonBuilder {
	
	private static final double OPACITY = 0.75;
	private List<List<Integer>> predictions;
	private double ullat;
	private double ullon;
	private double lrlat;
	private double lrlon;
	private double[] lons;
	private double[] lats;
	
	public GeoJsonBuilder (List<List<Integer>> predictions) {
		
		this.predictions = predictions;
		
	}
	
	public GeoJsonBuilder ullon(double ullon) {
		
		this.ullon = ullon;
		return this;
		
	}
	public GeoJsonBuilder ullat(double ullat) {
		
		this.ullat = ullat;
		return this;
		
	}
	public GeoJsonBuilder lrlon(double lrlon) {
		
		this.lrlon = lrlon;
		return this;
		
	}
	public GeoJsonBuilder lrlat(double lrlat) {
		
		this.lrlat = lrlat;
		return this;
		
	}
	
	public String build() {
		
		buildLats();
		buildLons();
		FeatureCollection featureCollection = buildFeatureCollection();
		return featureCollection.toJson();
		
	}
	
	public void buildLats() {
		
		int noLatitudes = noColumns() + 1;
		
		double[] latitudes = new double[noLatitudes];
		
		double latLen = polyLatLength();
		
		for (int i = 0; i < noLatitudes; i++) {
			latitudes[i] = ullat + (i * latLen);
		}
		this.lats = latitudes;
	}
	
	public void buildLons() {
		
		int noLongitudes = noRows() + 1;
		
		double[] longitudes = new double[noLongitudes];
		
		double lonLen = polyLonLength();
		
		for (int i = 0; i < noLongitudes; i++) {
			longitudes[i] = ullon - (i * lonLen);
		}
		
		this.lons = longitudes;
		
	}
	
	public int noRows() {
		int noRows = predictions.size();
		return noRows;
	}
	
	public int noColumns() {
		int noColumns = predictions.get(0).size();
		return noColumns;
	}
	
	public int numOfPredictions() {
		return noRows() * noColumns();
	}
	
	public double polyLonLength() {
		double droneConfinementLonLength = ullon - lrlon;
		return droneConfinementLonLength / noRows();
	}
	
	public double polyLatLength() {
		double droneConfinementLatLength = lrlat - ullat;
		return droneConfinementLatLength / noColumns();
	}
	
	public FeatureCollection buildFeatureCollection() {
 		
 		List<Feature> features = buildFeatures();
 		FeatureCollection fc = FeatureCollection.fromFeatures(features);
 		return fc;
 		
 	}
	
	public List<Feature> buildFeatures() {
		
		List<Feature> features = new ArrayList<>();
		
		int noRows = noRows();
		int noColumns = noColumns();
		
		for (int ullonIndex = 0; ullonIndex < noRows; ullonIndex++) {
			for (int ullatIndex = 0; ullatIndex < noColumns; ullatIndex++) {
				
				Feature feature = buildFeature(ullonIndex, ullatIndex);
				features.add(feature);
				
			}
		}
		
		return features;
	}
	
	public Feature buildFeature(int ullonIndex, int ullatIndex) {
		
		Polygon polygon = buildPolygon(ullonIndex, ullatIndex);
		Feature feature = Feature.fromGeometry(polygon);		
		addProperties(feature, ullonIndex, ullatIndex);
		
		return feature;
		
	}
	
	public void addProperties(Feature feature, int ullonIndex, int ullatIndex) {
		
		int prediction = predictions.get(ullatIndex).get(ullonIndex);
		String fill = getFeatureFill(prediction);
		String rgbString = getFeatureRgbString(prediction);
		
		feature.addStringProperty("fill", fill);
		feature.addStringProperty("rgb-string", rgbString);
		feature.addNumberProperty("fill-opacity", OPACITY);
		
	}
	
	public String getFeatureFill(int prediction) {
		
		String fill = "";
		
		if (0 <= prediction && prediction < 32) {
			fill = GREEN;
		} else if (32 <= prediction && prediction < 64) {
			fill = MEDIUM_GREEN;
		} else if (64 <= prediction && prediction < 96) {
			fill = LIGHT_GREEN;
		} else if (96 <= prediction && prediction < 128) {
			fill = LIME_GREEN;
		} else if (128 <= prediction && prediction < 160) {
			fill = GOLD;
		} else if (160 <= prediction && prediction < 192) {
			fill = ORANGE;
		} else if (192 <= prediction && prediction < 224) {
			fill = RED_ORANGE;
		} else if (224 <= prediction && prediction < 256) {
			fill = RED;
		}
		return fill;
	}
	
	public String getFeatureRgbString(int prediction) {
		
		return getFeatureFill(prediction);
		
	}
	
	public Polygon buildPolygon(int ullonIndex, int ullatIndex) {
		
		List<Point> points = buildPoints(ullonIndex, ullatIndex);
		Polygon polygon = Polygon.fromLngLats(Arrays.asList(points));
		
		return polygon;
		
	}
	
	public List<Point> buildPoints(int ullonIndex, int ullatIndex) {
		
		int[] lonIndexDiff = new int[] {0, 1, 1, 0};
		int[] latIndexDiff = new int[] {0, 0, 1, 1};
		
		List<Point> points = new ArrayList<>();
		
		for (int i = 0; i < 4; i++) {
			
			int lonIndex = ullonIndex + lonIndexDiff[i];
			int latIndex = ullatIndex + latIndexDiff[i];
			
			Point p = buildPoint(lonIndex, latIndex);
			points.add(p);
		}
		
		return points;
	}
	
	public Point buildPoint(int lonIndex, int latIndex) {
		
		double lon = lons[lonIndex];
		double lat = lats[latIndex];
		Point point = Point.fromLngLat(lon, lat);
		return point;
	}

}
