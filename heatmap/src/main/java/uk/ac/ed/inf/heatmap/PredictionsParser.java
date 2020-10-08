package uk.ac.ed.inf.heatmap;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class for parsing prediction files.
 */

public class PredictionsParser {
	
	private FileParser fileParser;
	
	public PredictionsParser(FileParser fileParser) {
		this.fileParser = fileParser;
	}
	
	public List<List<Integer>> parseFile(String filePath) throws FileNotFoundException {
		return fileParser.parseFile(filePath);
	}

}
