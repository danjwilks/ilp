package uk.ac.ed.inf.heatmap;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

public class AppTest {
	
	private static final double ULLON = -3.192473;
	private static final double ULLAT = 55.946233;
	private static final double LRLON = -3.184319;
	private static final double LRLAT = 55.942617;

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue( true );
    }
    
    @Test
    public void runApp() {
    	
    	String[] args = new String[] {"predictions.txt"};
    	App.main(args);
    	
    }
    
//    @Test
    public void buildLats() throws FileNotFoundException {
    	String filePath = "predictions.txt";
    	List<List<Integer>> predictions = App.parsePredictions(filePath);
    	
    	GeoJsonBuilder builder = new GeoJsonBuilder(predictions)
				.ullon(ULLON)
				.ullat(ULLAT)
				.lrlon(LRLON)
				.lrlat(LRLAT);
    	
    	builder.build();
    	
    	
    }
    
}
