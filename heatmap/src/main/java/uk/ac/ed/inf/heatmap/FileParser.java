package uk.ac.ed.inf.heatmap;

import java.io.FileNotFoundException;
import java.util.List;

public interface FileParser {
	
	public List<List<Integer>> parseFile(String filePath) throws FileNotFoundException;
	
}