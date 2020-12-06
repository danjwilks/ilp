package uk.ac.ed.inf.aqmaps;

import com.google.gson.Gson;

public class What3Words {
	
	private String words;
	private Coordinates coordinates;
	public static class Coordinates {
		
		double lng;
		double lat;
		
		public double getLongitude() {
			return this.lng;
		}
		
		public double getLatitude() {
			return this.lat;
		}
	}
	
	public static What3Words fromJsonString(String what3WordsJsonString) {
		return new Gson().fromJson(what3WordsJsonString, What3Words.class);
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof What3Words)) {
			return false;
		}

		What3Words what3Words = (What3Words) obj;
		return words.equals(what3Words.words);
	}

	
	public Coordinates getCoordinates() {
		return this.coordinates;
	}
	
}
