package eu.andymel.timecollector.performancetests;

import java.time.Instant;

public class PerformanceTestTimeProviders {

	public static void main(String[] args) {
		
		long amount = 100_000_000;
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++)System.nanoTime();
		PerformanceTestsUtils.end("System.nanoTime()", amount, start);
		
		
		start = Instant.now();
		for(int i=0; i<amount; i++)System.currentTimeMillis();
		PerformanceTestsUtils.end("System.currentTimeMillis()", amount, start);

		
		start = Instant.now();
		for(int i=0; i<amount; i++)Instant.now();
		PerformanceTestsUtils.end("Instant.now()", amount, start);
		
	}
	
}
