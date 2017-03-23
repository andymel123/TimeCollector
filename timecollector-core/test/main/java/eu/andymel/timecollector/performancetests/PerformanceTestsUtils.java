package eu.andymel.timecollector.performancetests;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class PerformanceTestsUtils {

	public static Duration end(String msg, long amount, Instant start){

		Duration d = Duration.between(start, Instant.now());
		System.out.println("PerformanceTest: "+msg);
		System.out.println("Total time needed: "+d.toMillis()/1000d+" seconds for "+amount+" iterations"); 
		System.out.println("That's "+d.toNanos()/(double)amount+"nanos per iteration");

		return d;
	}
	
	public static void waitForInput() {
		o("Press Enter");
		
		try {
			System.in.read();
			o("...");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static final void o(Object o){
		System.out.println(o);
	}

}
