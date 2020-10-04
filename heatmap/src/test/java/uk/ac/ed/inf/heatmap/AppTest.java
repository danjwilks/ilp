package uk.ac.ed.inf.heatmap;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

public class AppTest 
{

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue( true );
    }
    
    @Test
    public void runApp() {
    	
    	String[] args = new String[] {"predictions.txt"};
    	App.main(args);
    	
    }
    
}
