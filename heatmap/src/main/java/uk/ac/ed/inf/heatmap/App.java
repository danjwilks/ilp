package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class App {
	
	private static final double ULLON = -3.192473;
	private static final double ULLAT = 55.946233;
	private static final double LRLON = -3.184319;
	private static final double LRLAT = 55.942617;
	
    public static void main( String[] args ) {
    	
		try {
			validateInput(args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
    	
    	String filePath = args[0];
        
    	List<List<Integer>> predictions;
		try {
			predictions = parsePredictions(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		String geoJson = new GeoJsonBuilder(predictions)
				.ullon(ULLON)
				.ullat(ULLAT)
				.lrlon(LRLON)
				.lrlat(LRLAT)
				.build();
		
		writeToFile("heatmap.geojson", geoJson);
        
    }
    
    private static void validateInput(String[] args) throws IllegalAccessException {
    	if (args.length != 1) {
    		throw new IllegalArgumentException("input file parameter is required");
    	}
    }
    
    public static void writeToFile(String filePath, String data) {
    	
    	File file = new File(filePath);

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
    
    public static List<List<Integer>> parsePredictions(String filePath) throws FileNotFoundException {
    	
    	PredictionsParser predictionsParser = new PredictionsParser(new TextFileParser());
    	List<List<Integer>> predictions = predictionsParser.parseFile(filePath);

		return predictions;
    	
    }
    
}
