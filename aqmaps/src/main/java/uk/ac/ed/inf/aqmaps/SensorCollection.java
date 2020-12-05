package uk.ac.ed.inf.aqmaps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SensorCollection {
	
	private List<Sensor> sensors;
	
	private SensorCollection (List<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	public List<Sensor> getSensors() {
		return sensors;
	}
	
	public static SensorCollection fromJsonString(String jsonString) {
		
		if (jsonString.equals("")) {
			return new SensorCollection(new ArrayList<Sensor>());
		}
		
		Type listType = new TypeToken<ArrayList<Sensor>>() {}.getType();
    	List<Sensor> sensors = new Gson().fromJson(jsonString, listType);
    	return new SensorCollection(sensors);
		
	}

}
