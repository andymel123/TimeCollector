package eu.andymel.timecollector.performancetests;

import java.time.Instant;

import eu.andymel.timecollector.util.AvgMaxCalcLong;
import eu.andymel.timecollector.util.NanoClock;

public class PerformanceTestTimeProviders {

	public static void main(String[] args) {
		
		long amount = 100_000_000;
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++)System.nanoTime();
		PerformanceTestsUtils.end("System.nanoTime()", amount, start);
		o("");
		
		start = Instant.now();
		for(int i=0; i<amount; i++)System.currentTimeMillis();
		PerformanceTestsUtils.end("System.currentTimeMillis()", amount, start);
		o("");

		
		start = Instant.now();
		for(int i=0; i<amount; i++)Instant.now();
		PerformanceTestsUtils.end("Instant.now()", amount, start);
		o("");

		NanoClock nanoClock = new NanoClock();
		start = Instant.now();
		for(int i=0; i<amount; i++)nanoClock.instant();
		PerformanceTestsUtils.end("nanoClock.instant()", amount, start);
		o("");


		AvgMaxCalcLong calc = AvgMaxCalcLong.create();
		start = Instant.now();
		Instant last = null;
		int count=0;
		for(int i=0; i<amount; i++){
			Instant now = Instant.now();
			if(now.equals(last)){
				count++;
			}else if(last!=null){
				calc.add(count);
				count=0;
			}
			last = now;
		}
		PerformanceTestsUtils.end("Instant.now() Precision", amount, start);
		o("Avg equal Instants by clock: "+calc.getAvg());
		o("Max equal Instants by clock: "+calc.getMax());
		o("Min equal Instants by clock: "+calc.getMin());
		o("");

		
		calc = AvgMaxCalcLong.create();
		start = Instant.now();
		last = null;
		count=0;
		for(int i=0; i<amount; i++){
			Instant now = nanoClock.instant();
			if(now.equals(last)){
				count++;
			}else if(last!=null){
				calc.add(count);
				count=0;
			}
			last = now;
		}
		PerformanceTestsUtils.end("nanoClock.instant() Precision", amount, start);
		o("Avg equal Instants by clock: "+calc.getAvg());
		o("Max equal Instants by clock: "+calc.getMax());
		o("Min equal Instants by clock: "+calc.getMin());

		
	}
	
	private static final void o(Object o){
		System.out.println(o);
	}
}
