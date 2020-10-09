package uk.ac.ed.inf.heatmap;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Interface for parsing prediction files.
 * 
 * Allows for other file parsers to be created
 * for different file types.
 * 
 * This follows the strategy creational design pattern.
 * 
 * Allows code to be more extendable and managable for 
 * future researcher use.
 */

public interface FileParser {
	
	/**
	 * This method parses file given a filePath. 
	 * 
	 * It returns a list of list of integers that represents
	 * the number matrix given in the input file. 
	 * 
	 * If the file is not at the inputed filePath then
	 * an exception is thrown.
	 */
	
	public List<List<Integer>> parseFile(String filePath) throws FileNotFoundException;
	
}