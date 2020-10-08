package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/** 
 *  Parser for text files.
 */

public class TextFileParser implements FileParser{
	
	/**
	 * Parses file given a filePath. 
	 * 
	 * It returns a list of list of integers that represents
	 * the number matrix given in the input file. 
	 * 
	 * If the file is not at the inputed filePath then
	 * an exception is thrown.
	 * 
	 * First the lines of the file are read and then the
	 * predictions from the file are parsed.
	 */

	@Override
	public List<List<Integer>> parseFile(String filePath) throws FileNotFoundException {
		
		var fileLines = readFile(filePath);
		var predictions = parsePredictions(fileLines);
		
		return predictions;
	}
	
	/**
	 * Reads the lines from a file.
	 * 
	 * A list of the lines from the read file are returned.
	 */
	
	private static List<String> readFile(String predictionsPath) throws FileNotFoundException {
    	
	    var predictionsFile = new File(predictionsPath); 
	    var fileScanner = new Scanner(predictionsFile); 
	    var fileLines = new ArrayList<String>();
	    
	    while (fileScanner.hasNextLine()) { 
	    	fileLines.add(fileScanner.nextLine());
	  	} 
	    
	    fileScanner.close();
	    
	    return fileLines;
    	
    }
	
	/**
	 * This method parses predictions from a list of file lines. 
	 */
	
	private static List<List<Integer>> parsePredictions(List<String> fileLines) {
    	
    	var predictions = new ArrayList<List<Integer>>();
    	
    	for (var line: fileLines) {
    		var rowOfPredictions = parseIntegers(line);
    		predictions.add(rowOfPredictions);
    	}
    	
    	return predictions;
    	
    }
	
	/**
	 * Parses integers from string of a line from a file.
	 * 
	 * Assumes all integers are positive.
	 */
	
	private static List<Integer> parseIntegers(String fileLine) {
    	
    	var integers = new ArrayList<Integer>();
    	
    	var pattern = Pattern.compile("\\d+");
    	var matcher = pattern.matcher(fileLine); 
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
