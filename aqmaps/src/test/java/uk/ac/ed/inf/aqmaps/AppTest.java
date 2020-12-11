package uk.ac.ed.inf.aqmaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppTest {
	
	private static List<List<String>> generateValidDates() {
		
		var dates = new ArrayList<List<String>>();
		var numberOfDaysInMonth = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		var years = new String[] {"2020", "2021"};
		var months = new String[] 
				{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
		
		for (String year : years) {
			for (int monthIndex = 0; monthIndex < 12; monthIndex++) {
				String month = months[monthIndex];
				int numberOfDays = numberOfDaysInMonth[monthIndex];
				
				for (int i = 1; i <= numberOfDays; i++) {
					String day = String.valueOf(i);
					day = ("00" + day).substring(day.length());
				
					dates.add(Arrays.asList(day, month, year));
				}
			}
		}
		
		return dates;
	}

}
