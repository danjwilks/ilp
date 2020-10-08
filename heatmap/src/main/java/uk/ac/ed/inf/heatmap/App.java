package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Parses predictions.txt, creates and writes geojson to file.
 */

public class App {
	
	/** 
	 *  Coordinates of the upper left and lower right
	 *  corners of the area that the drone can visit.
	 *  
	 *  First character stands for upper or lower.
	 *  Second character stands for left or right.
	 *  Final characters stand for longitude or
	 *  latitude.
	 *  
	 *  E.g. ULLON stands for upper left longitude.
	 */
	
	private static final double ULLON = -3.192473;
	private static final double ULLAT = 55.946233;
	private static final double LRLON = -3.184319;
	private static final double LRLAT = 55.942617;
	
	private static final String OUTPUT_FILEPATH = "heatmap.geojson";
	
	/**
	 * Parses predictions.txt, creates and writes geojson to file.
	 * 
	 * Assumes the first variable of args is the filePath to 
	 * the predictions file.
	 */
	
    public static void main( String[] args ) {
    	
		try {
			validateInput(args);
		} catch (IllegalArgumentException e) { // incorrect number of arguments
			e.printStackTrace();
			return;
		}
    	
    	var filePath = args[0];
        
    	List<List<Integer>> predictions;
		try {
			predictions = parsePredictions(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		var geoJson = new GeoJsonBuilder(predictions)
				.ullon(ULLON)
				.ullat(ULLAT)
				.lrlon(LRLON)
				.lrlat(LRLAT)
				.build();
		
		writeToFile(OUTPUT_FILEPATH, geoJson);
        
    }
    
    /**
	 * Validates the input arguments.
	 * 
	 * Throws exception if there is the wrong number 
	 * of arguments.
	 */
    
    private static void validateInput(String[] args) throws IllegalArgumentException {
    	if (args.length != 1) {
    		throw new IllegalArgumentException("input file parameter is required");
    	}
    }
    
    /**
	 * Writes the geojson (data) to the file found at 
	 * the filePath input.
	 */
    
    public static void writeToFile(String filePath, String data) {
    	
    	var file = new File(filePath);

		PrintWriter writer;
		try {
			writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		writer.println(data);
		writer.close();
		
	}
    
    /**
	 * Parses predictions from a file.
	 * 
	 * Assumes file is a text file.
	 */
    
    public static List<List<Integer>> parsePredictions(String filePath) throws FileNotFoundException {
    	
    	var predictionsParser = new PredictionsParser(new TextFileParser());
    	var predictions = predictionsParser.parseFile(filePath);

		return predictions;
    	
    }
    
}
