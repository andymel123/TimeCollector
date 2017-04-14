package eu.andymel.timecollector.performancetests;

import java.time.Duration;
import java.time.Instant;

import eu.andymel.timecollector.util.NanoClock;

public class PerformanceTestLongVsDur {

	public static void main(String[] args) {
		
		NanoClock nanoClock = new NanoClock();
		
		// prepare
		int amount = 100_000;
		Instant[] array = new Instant[amount];
		for(int i=0; i<amount; i++)array[i] = nanoClock.instant();
		
		
		long l=0;
		Instant start = Instant.now();
		for(int i=1; i<amount; i++){
			Duration d = Duration.between(array[i-1], array[i]);
			l = d.toNanos();
		}
		PerformanceTestsUtils.end("Instant and Duration", amount, start);
		o("");
		
		
		long[] array2 = new long[amount];
		for(int i=0; i<amount; i++)array2[i] = System.nanoTime();
		
		
		l=0;
		start = Instant.now();
		for(int i=1; i<amount; i++){
			l = array2[i]-array2[i-1];
		}
		PerformanceTestsUtils.end("direct System.nano longs", amount, start);

	}
	
	private static final void o(Object o){
		System.out.println(o);
	}
}
