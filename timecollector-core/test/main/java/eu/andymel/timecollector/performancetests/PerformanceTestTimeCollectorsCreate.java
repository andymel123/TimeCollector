package eu.andymel.timecollector.performancetests;

import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.waitForInput;

import java.time.Instant;

import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.util.NanoClock;

/*
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 3.579 seconds for 100000000 iterations
 * That's 35.79nanos per iteration
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestTimeCollectorsCreate {

	public static void main(String[] args) {
		
		int amount = 100_000_000;
		
		NanoClock clock = new NanoClock();

		waitForInput();
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorWithPath<eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones> tc = TestTimeCollectorProvider.getTC(clock);
		}
		PerformanceTestsUtils.end("Create TimeCollectorWithPath", amount, start);
		
	}
	
	private static final void o(Object o){
		System.out.println(o);
	}
}
