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
    	
    	if (!isValidInput(args)) {
    		throw new IllegalArgumentException();
    	}
        
    	List<List<Integer>> predictions;
		try {
			predictions = parsePredictions(args);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		HeatMap heatMap = new HeatMap.HeatMapBuilder(predictions)
				.ullon(ULLON)
				.ullat(ULLAT)
				.lrlon(LRLON)
				.lrlat(LRLAT)
				.build();
		
		writeToFile("heatmap.geojson", heatMap.getGeoJSON());
        
    }
    
    public static void writeToFile(String filePath, String geoJSON) {
    	
    	File file = new File(filePath);

		PrintWriter writer;
		try {
			writer = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		writer.println(geoJSON);
		writer.close();
		
	}
    
    public static List<List<Integer>> parsePredictions(String[] args) throws FileNotFoundException {
    	
    	String filePath = args[0];
    	PredictionsParser predictionsParser = new PredictionsParser(new TextFileParser());
    	List<List<Integer>> predictions = predictionsParser.parseFile(filePath);
		return predictions;
    	
    }
    
    private static boolean isValidInput(String[] args) {
    	if (args.length != 1) {
    		return false;
    	}
    	return true;
    }
    
}
