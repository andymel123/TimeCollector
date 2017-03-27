package eu.andymel.timecollector.performancetests;

import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.o;
import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.waitForInput;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollectorSerial;
import eu.andymel.timecollector.report.TextualSerialAnalyzer;
import eu.andymel.timecollector.util.NanoClock;

/*
 * PerformanceTest: TimeCollectorSerial with TestMilestones
 * Total time needed: 1.884 seconds for 1000000 iterations
 * That's 1884.0nanos per iteration
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestSerialWithSaveTime {

	private enum TestMilestones{
		MS1,
		MS2,
		MS3,
		MS4,
		MS5,
		MS6,
		MS7,
		MS8
	}
	
	public static void main(String[] args) {

		waitForInput();
		
		int amount = 100_000;
		
		NanoClock clock = new NanoClock();
		
		TextualSerialAnalyzer<TestMilestones> analyzer = new TextualSerialAnalyzer<>(TestMilestones.class);
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorSerial<TestMilestones> tc = TimeCollectorSerial.create(TestMilestones.class, clock, true, true, true);
			tc.saveTime(TestMilestones.MS1);
			tc.saveTime(TestMilestones.MS2);
			tc.saveTime(TestMilestones.MS3);
			tc.saveTime(TestMilestones.MS4);
			tc.saveTime(TestMilestones.MS5);
			tc.saveTime(TestMilestones.MS6);
			tc.saveTime(TestMilestones.MS7);
			tc.saveTime(TestMilestones.MS8);
			
			analyzer.addCollector(tc);
		}
		PerformanceTestsUtils.end("TimeCollectorSerial with "+TestMilestones.class.getSimpleName(), amount, start);
	
		o(analyzer.toString(TimeUnit.NANOSECONDS));
		
	}

}
