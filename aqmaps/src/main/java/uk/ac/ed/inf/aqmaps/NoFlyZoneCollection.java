package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

/**
 * @author S1851664
 * 
 * Class used to represent the no fly zones.
 */
public class NoFlyZoneCollection {
	
	/**
	 * Zones that the drone can not fly into.
	 */
	private List<Polygon> noFlyZones;
	
	/**
	 * Called only by the static from json function.
	 * 
	 * @param noFlyZones - zones that drone can
	 *        not fly into.
	 */
	private NoFlyZoneCollection (List<Polygon> noFlyZones) {
		this.noFlyZones = noFlyZones;
	}
	
	/**
	 * @return list of files that drone can not
	 *         fly into.
	 */
	public List<Polygon> getNoFlyZones() {
		return noFlyZones;
	}
	
	/**
	 * Creates list of polygons that represent 
	 * locations drone can not fly into.
	 * 
	 * @param jsonString
	 * @return collection of no fly zones.
	 */
	public static NoFlyZoneCollection fromJsonString(String jsonString) {
		
		var features = FeatureCollection.fromJson(jsonString).features();
		var noFlyZones = new ArrayList<Polygon>();
		for (var feature : features) {
			if (feature.geometry().getClass().equals(Polygon.class)) {
				noFlyZones.add((Polygon) feature.geometry());
			}
		}
		return new NoFlyZoneCollection(noFlyZones);
		
	}

}
