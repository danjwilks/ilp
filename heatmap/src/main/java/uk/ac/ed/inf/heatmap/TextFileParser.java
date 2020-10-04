package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFileParser implements FileParser{

	@Override
	public List<List<Integer>> parseFile(String filePath) throws FileNotFoundException {
		
		List<String> fileLines = readFile(filePath);
		List<List<Integer>> predictions = parsePredictions(fileLines);
		
		return predictions;
	}
	
	private static List<String> readFile(String predictionsPath) throws FileNotFoundException {
    	
	    File predictionsFile = new File(predictionsPath); 
	    Scanner fileScanner = new Scanner(predictionsFile); 
	    ArrayList<String> fileLines = new ArrayList<>();
	    
	    while (fileScanner.hasNextLine()) { 
	    	fileLines.add(fileScanner.nextLine());
	  	} 
	    
	    fileScanner.close();
	    
	    return fileLines;
    	
    }
	
	private static List<List<Integer>> parsePredictions(List<String> fileLines) {
    	
    	ArrayList<List<Integer>> predictions = new ArrayList<>();
    	
    	for (String line: fileLines) {
    		List<Integer> rowOfPredictions = parseIntegers(line);
    		predictions.add(rowOfPredictions);
    	}
    	
    	return predictions;
    	
    }
	
	private static List<Integer> parseIntegers(String fileLine) {
    	
    	List<Integer> integers = new ArrayList<>();
    	
    	Pattern pattern = Pattern.compile("\\d+");
    	Matcher matcher = pattern.matcher(fileLine); 
    	while (matcher.find()) {
    		try {
    			int i = Integer.parseInt(matcher.group());
    			integers.add(i);
    		} catch (NumberFormatException e){
    			e.printStackTrace();
    		}
    	}
    	
    	return integers;
    	
    }

}
