package eu.andymel.timecollector.performancetests;

import java.time.Duration;
import java.time.Instant;

public class PerformanceTestsUtils {

	public static Duration end(String msg, long amount, Instant start){

		Duration d = Duration.between(start, Instant.now());
		System.out.println("PerformanceTest: "+msg);
		System.out.println("Total time needed: "+d); 
		System.out.println("That's "+d.toNanos()/(double)amount+"nanos per iteration");

		return d;
	}
	
}
