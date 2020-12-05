package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

public class NoFlyZoneCollection {
	
	private List<Polygon> noFlyZones;
	
	private NoFlyZoneCollection (List<Polygon> noFlyZones) {
		this.noFlyZones = noFlyZones;
	}
	
	public List<Polygon> getNoFlyZones() {
		return noFlyZones;
	}
	
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
