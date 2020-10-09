package uk.ac.ed.inf.heatmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import static uk.ac.ed.inf.heatmap.Colours.*;

/**
 * Builds a geojson string object given the
 * correct parameters
 */

public class GeoJsonBuilder {
	
	// Opacity of the heatmap.
	private static final double OPACITY = 0.75;
	private List<List<Integer>> predictions;
	
	/** 
	 *  Coordinates of the upper left and lower right
	 *  corners of the area that the drone can visit.
	 *  
	 *  First character stands for upper or lower.
	 *  Second character stands for left or right.
	 *  Final characters stand for longitude or
	 *  latitude.
	 */
	
	private double ullat;
	private double ullon;
	private double lrlat;
	private double lrlon;
	
	/** 
	 *  lons represent the longitudinal values
	 *  at the corners of squares in the heatmap.
	 *  
	 *  lats represent the latitudinal values
	 *  at the corners of squares in the heatmap.
	 *  
	 *  For example if there was one square:
	 *  
	 *  (lon=1,lat=2)-----(lon=2,lat=2)
	 *       |		            |
	 *       |		GREEN       |
	 *       |		            |
	 *  (lon=1,lat=1)-----(lon=2,lat=1)
	 *  
	 *  then lons = [1,2] and lats = [1,2]
	 */
	
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
	
	/** 
	 *  Builds the geojson string object
	 *  
	 *  First builds the lons and lats and then
	 *  using this, creates feature collection
	 *  and from this, returns the geojson string.
	 */
	
	public String build() {
		
		buildLats();
		buildLons();
		var featureCollection = buildFeatureCollection();
		return featureCollection.toJson();
		
	}
	
	/** 
	 *  Builds the lats array. 
	 *  
	 *  lats represent the latitudinal values
	 *  at the corners of squares in the heatmap.
	 *  
	 *  For example if there was one square:
	 *  
	 *  (lon=1,lat=2)-----(lon=2,lat=2)
	 *       |		            |
	 *       |		GREEN       |
	 *       |		            |
	 *  (lon=1,lat=1)-----(lon=2,lat=1)
	 *  
	 *  then lats = [1,2]
	 */
	
	public void buildLats() {
		
		/* 
		 * The number of different latitudinal points
		 * in a heatmap. In the above example, since
		 * there would be 1 single row of predictions to 
		 * require one square, there are two different
		 * latitudinal points we need to store.
		 */ 
		int noLatitudes = noColumns() + 1;
		
		double[] latitudes = new double[noLatitudes];
		
		double latLen = polyLatLength();
		
		/* 
		 * iterates through the number of latitude points
		 * and sets latitudes[i] to the latitude value
		 * at that latitude point.
		*/
		for (int i = 0; i < noLatitudes; i++) {
			latitudes[i] = ullat + (i * latLen);
		}
		this.lats = latitudes;
	}
	
	/** 
	 *  Builds the lons array. 
	 *  
	 *  lons represent the longitudinal values
	 *  at the corners of squares in the heatmap.
	 *  
	 *  For example if there was one square:
	 *  
	 *  (lon=1,lat=2)-----(lon=2,lat=2)
	 *       |		            |
	 *       |		GREEN       |
	 *       |		            |
	 *  (lon=1,lat=1)-----(lon=2,lat=1)
	 *  
	 *  then lons = [1,2]
	 */
	
	public void buildLons() {
		
		/* 
		 * The number of different longitudinal points
		 * in a heatmap. In the above example, since
		 * there would be 1 single row of predictions to 
		 * require one square, there are two different
		 * longitudinal points we need to store.
		 */
		int noLongitudes = noRows() + 1;
		
		double[] longitudes = new double[noLongitudes];
		
		double lonLen = polyLonLength();
		
		/* 
		 * iterates through the number of longitude points
		 * and sets longitude[i] to the longitude value
		 * at that longitude point.
		*/
		for (int i = 0; i < noLongitudes; i++) {
			longitudes[i] = ullon - (i * lonLen);
		}
		
		this.lons = longitudes;
		
	}
	
	/** 
	 *  Calculates the number of rows in the 
	 *  predictions parameter.
	 */
	
	public int noRows() {
		
		int noRows = predictions.size();
		return noRows;
	}
	
	/** 
	 *  Calculates the number of columns in the 
	 *  predictions parameter.
	 */
	
	public int noColumns() {
		
		int noColumns = predictions.get(0).size();
		return noColumns;
	}
	
	/** 
	 *  Calculates the longitudinal length
	 *  of each square in the heatmap.
	 */
	
	public double polyLonLength() {
		
		/* 
		 * Drone confinement is the square where the drone
		 * is allowed to travel in.
		 */
		double droneConfinementLonLength = ullon - lrlon;
		return droneConfinementLonLength / noRows();
	}
	
	/** 
	 *  Calculates the latitudinal length
	 *  of each square in the heatmap.
	 */
	
	public double polyLatLength() {
		
		/* 
		 * Drone confinement is the square where the drone
		 * is allowed to travel in.
		 */
		double droneConfinementLatLength = lrlat - ullat;
		return droneConfinementLatLength / noColumns();
	}
	
	/** 
	 *  Builds the a collection of features.
	 *  
	 *  From the created FeatureCollection, 
	 *  we can create a geojson string.
	 */
	
	public FeatureCollection buildFeatureCollection() {
 		
 		var features = buildFeatures();
 		var featureCollection = FeatureCollection.fromFeatures(features);
 		return featureCollection;
 	}
	
	/** 
	 *  Builds the a collection of features.
	 *  
	 *  From the created list of features, 
	 *  we can create a FeatureCollection Object.
	 */
	
	public List<Feature> buildFeatures() {
		
		var features = new ArrayList<Feature>();
		
		/* 
		 * We specify each square in the heatmap by the upper left point.
		 * 
		 * E.g. we have a Heatmap and lons / lats as below, 
		 *  
		 * 		* -- * -- *
		 * 		|  1 |	2 |  	lons = [1.01, 2.01, 3.01]
		 * 		* -- * -- *
		 * 		|  3 |	4 |		lats = [1.01, 2.01, 3.01]
		 *  	* -- * -- * 
		 *  
		 *  then to specify square 1, we would need the 
		 *  upper left longitude and upper left latitude of square 1.
		 *  
		 *  Since we have lons and lats which hold these values already,
		 *  we can specify the square 1 by the INDEX of the upper left
		 *  longitude and upper left latitude which would allow us to 
		 *  access lons and lats values for the square.
		 *  
		 *  So, we just need to pass the upper left latitude and upper left
		 *  longitude index to buildFeature function to specify which square
		 *  we want to build.
		 *  
		 *  For the iteration:
		 *  We use lons.length - 1 since we are specifying only the 
		 *  upper left points of a square, so we do not need the final
		 *  right point of the square.
		 */
		
		for (int lonsIndex = 0; lonsIndex < lons.length - 1; lonsIndex++) {
			for (int latsIndex = 0; latsIndex < lats.length - 1; latsIndex++) {
				
				var feature = buildFeature(lonsIndex, latsIndex);
				features.add(feature);
				
			}
		}
		return features;
	}
	
	/** 
	 *  Builds a feature.
	 *  
	 *  A feature is essentially a heatmap square
	 *  or Polygon with the properties that 
	 *  determine the colour and opacity.
	 *  
	 *  The feature has location attributes dependent on 
	 *  the upper left longitudinal Index specifying which
	 *  index of the lons attribute we should use and also
	 *  the upper left longitudinal Index specifying which
	 *  index of the lons attribute we should use.
	 */
	
	public Feature buildFeature(int lonsIndex, int latsIndex) {
		
		var polygon = buildPolygon(lonsIndex, latsIndex);
		var feature = Feature.fromGeometry(polygon);		
		addProperties(feature, lonsIndex, latsIndex);
		
		return feature;
	}
	
	/** 
	 *  Adds the required properties to a feature.
	 *  
	 *  The required properties are the opacity,
	 *  fill and rgb string. 
	 *  
	 *  The feature has location attributes dependent on 
	 *  the upper left longitudinal Index specifying which
	 *  index of the lons attribute we should use and also
	 *  the upper left longitudinal Index specifying which
	 *  index of the lons attribute we should use.
	 *  
	 *  We can use these indexes to access the correct
	 *  prediction, since each lons and lats pair map to a 
	 *  specific prediction.
	 */
	
	public void addProperties(Feature feature, int lonsIndex, int latsIndex) {
		
		var prediction = predictions.get(latsIndex).get(lonsIndex);
		var fill = getFeatureFill(prediction);
		var rgbString = getFeatureRgbString(prediction);
		
		feature.addStringProperty("fill", fill);
		feature.addStringProperty("rgb-string", rgbString);
		feature.addNumberProperty("fill-opacity", OPACITY);
		
	}
	
	/** 
	 *  Determines the fill colour for the inputed
	 *  prediction value. 
	 */
	
	public String getFeatureFill(int prediction) {
		
		String fill = getPredictionHexString(prediction);
		return fill;
	}
	
	/** 
	 *  Determines the hex string colour for the inputed
	 *  prediction value. 
	 */
	
	public String getPredictionHexString(int prediction) {
		
		String hexString = "";
		
		if (0 <= prediction && prediction < 32) {
			hexString = GREEN;
		} else if (32 <= prediction && prediction < 64) {
			hexString = MEDIUM_GREEN;
		} else if (64 <= prediction && prediction < 96) {
			hexString = LIGHT_GREEN;
		} else if (96 <= prediction && prediction < 128) {
			hexString = LIME_GREEN;
		} else if (128 <= prediction && prediction < 160) {
			hexString = GOLD;
		} else if (160 <= prediction && prediction < 192) {
			hexString = ORANGE;
		} else if (192 <= prediction && prediction < 224) {
			hexString = RED_ORANGE;
		} else if (224 <= prediction && prediction < 256) {
			hexString = RED;
		}
		return hexString;
	}
	
	/** 
	 *  Determines the fill colour for the inputed
	 *  prediction value. 
	 */
	
	public String getFeatureRgbString(int prediction) {
		
		String rgbString = getPredictionHexString(prediction);
		return rgbString;
	}
	
	/** 
	 *  Builds a Polygon.
	 *  
	 *  The Polygon is built from a list of points using
	 *  the upper left longitudinal Index for lons 
	 *  and the upper left latitudinal Index for lats.
	 *  
	 */
	
	public Polygon buildPolygon(int lonsIndex, int latsIndex) {
		
		var points = buildPoints(lonsIndex, latsIndex);
		var polygon = Polygon.fromLngLats(Arrays.asList(points));
		
		return polygon;
		
	}
	
	/** 
	 *  Builds a list of Points.
	 *  
	 *  The Points are created by gathering the indexes of the lons
	 *  and lats arguments that are needed to create the correct 
	 *  Points.
	 *  
	 *  Since we are given the upper left longitude and latitude
	 *  indexes we can find the other longitude and latitude indexes
	 *  using simple logic.
	 */
	
	public List<Point> buildPoints(int lonsIndex, int latsIndex) {
		
		/*
		 * We first want the upper left point,
		 * then the upper right point,
		 * then the lower right point,
		 * then the lower left point.
		 * 
		 * So we can specify this by calculating the index
		 * of the points in relation to the upper left point
		 * index.
		 * 
		 * The first index of the index diff arrays is 
		 * responsible for the upper left and the 
		 * second index is responsible for the upper 
		 * right point and so on.
		 */
		var lonIndexDiff = new int[] {0, 1, 1, 0};
		var latIndexDiff = new int[] {0, 0, 1, 1};
		
		var points = new ArrayList<Point>();
		
		// iterating through each of the points of the Polygon
		for (int i = 0; i < 4; i++) {
			
			int lonIndex = lonsIndex + lonIndexDiff[i];
			int latIndex = latsIndex + latIndexDiff[i];
			
			var point = buildPoint(lonIndex, latIndex);
			points.add(point);
		}
		
		return points;
	}
	
	/** 
	 *  Builds a Point.
	 *  
	 *  Using the longitudinal and latitudinal values
	 *  from lons and lats, we can build the point.
	 */
	
	public Point buildPoint(int lonIndex, int latIndex) {
		
		double lon = lons[lonIndex];
		double lat = lats[latIndex];
		var point = Point.fromLngLat(lon, lat);
		return point;
	}

}
