package uk.ac.ed.inf.aqmaps;

import java.math.BigDecimal;

public class Sensor {
	
	String location;
	double battery;
	String reading;
	
	private BigDecimal longitude;
	private BigDecimal latitude;
	
	public void setLongitude(BigDecimal lng) {
		this.longitude = lng;
	}
	public void setLatitude(BigDecimal lat) {
		this.latitude = lat;
	}
	
//	  "location": "shut.stands.media",
//    "battery": 61.31742,
//    "reading": "89.3"
	@Override
	public String toString() {
		return location;
	}
}