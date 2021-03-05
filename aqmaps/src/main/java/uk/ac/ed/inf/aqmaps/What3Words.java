package uk.ac.ed.inf.aqmaps;

import com.google.gson.Gson;

/**
 * @author S1851664
 *
 * Class representing a location using the 
 * What3Words representation and coordinates.
 */
public class What3Words {
	
	/**
	 * what3words representation of a location.
	 */
	private String words;
	/**
	 * Coordinates of the what3Words location.
	 */
	private Coordinates coordinates;
	/**
	 * @author S1851664
	 *
	 * Class used to represent the coordinates 
	 * corresponding to the what3words location.
	 */
	public static class Coordinates {
		
		/**
		 * longitude of the corresponding 
		 * what3words location
		 */
		private double lng;
		/**
		 * latitude of the corresponding 
		 * what3words location
		 */
		private double lat;
		
		public double getLongitude() {
			return this.lng;
		}
		
		public double getLatitude() {
			return this.lat;
		}
	}
	
	/**
	 * @param what3WordsJsonString
	 * @return the what3words object built from
	 *         the given json string.
	 */
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

	
	/**
	 * @return returns coordinates of this 
	 *         location.
	 */
	public Coordinates getCoordinates() {
		return this.coordinates;
	}
	
}
