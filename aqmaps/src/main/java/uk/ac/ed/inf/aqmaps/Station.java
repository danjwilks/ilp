package uk.ac.ed.inf.aqmaps;

public class Station {
	
	String location;
	double battery;
	String reading;
	
//	  "location": "shut.stands.media",
//    "battery": 61.31742,
//    "reading": "89.3"
	@Override
	public String toString() {
		return location;
	}
}