package uk.ac.ed.inf.aqmaps;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author S1851664
 *
 * Class to represent a collection of sensors. 
 */
public class SensorCollection {
	
	/**
	 * collection of sensors.
	 */
	private List<Sensor> sensors;
	
	/**
	 * Only called by static method.
	 * 
	 * @param sensors to group together.
	 */
	private SensorCollection (List<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	/**
	 * Creates an empty sensor collection.
	 */
	public SensorCollection() {
		this.sensors = new ArrayList<>();
	}
	
	/**
	 * Adds sensor to the collection.
	 * 
	 * @param sensor to add.
	 */
	public void add(Sensor sensor) {
		this.sensors.add(sensor);
	}
	
	/**
	 * @return the collection of sensors.
	 */
	public List<Sensor> getSensors() {
		return sensors;
	}
	
	/**
	 * Builds a collection of sensors from 
	 * a given json string.
	 * 
	 * @param jsonString to build sensors from.
	 * @return built sensor collection.
	 */
	public static SensorCollection fromJsonString(String jsonString) {
		
		if (jsonString.equals("")) {
			return new SensorCollection(new ArrayList<Sensor>());
		}
		
		Type listType = new TypeToken<ArrayList<Sensor>>() {}.getType();
    	List<Sensor> sensors = new Gson().fromJson(jsonString, listType);
    	return new SensorCollection(sensors);
		
	}

}
