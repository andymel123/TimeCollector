package eu.andymel.timecollector.performancetests;

import java.time.Instant;

/*
 * PerformanceTest: boolean just write
 * Total time needed: 0.347 seconds for 1000000000 iterations
 * That's 0.347nanos per iteration
 * 
 * PerformanceTest: boolean just read
 * Total time needed: 0.454 seconds for 1000000000 iterations
 * That's 0.454nanos per iteration
 * 
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestBoolean {

	public static void main(String[] args) {
		
		long amount = 1_000_000_000L;
		boolean b = false;
		
		Instant start = null;
		
		
		start = Instant.now();
		for(long i=0; i<amount; i++){
			b = 5<6;
		}
		PerformanceTestsUtils.end("boolean just write", amount, start);
		
		start = Instant.now();
		for(long i=0; i<amount; i++){
			if(b){
				b = 5<6;
			}
		}
		PerformanceTestsUtils.end("boolean just read", amount, start);
		
	}

}
