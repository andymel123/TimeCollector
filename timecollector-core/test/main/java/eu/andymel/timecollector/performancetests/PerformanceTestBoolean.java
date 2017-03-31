package eu.andymel.timecollector.performancetests;

import java.time.Instant;

public class PerformanceTestBoolean {

	public static void main(String[] args) {
		
		long amount = 1_000_000_000L;
		boolean b = false;
		
		Instant start = Instant.now();
		for(long i=0; i<amount; i++){
			if(b){
				b = 5<6;
			}
		}
		PerformanceTestsUtils.end("boolean just read", amount, start);
		
		start = Instant.now();
		for(long i=0; i<amount; i++){
			b = 5<6;
		}
		PerformanceTestsUtils.end("boolean just write", amount, start);
		
	}

}
