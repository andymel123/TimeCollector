package eu.andymel.timecollector.performancetests;



import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.waitForInput;

import java.time.Clock;
import java.time.Instant;

import eu.andymel.timecollector.TestClock;
import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.AnalyzerEach;
import eu.andymel.timecollector.util.NanoClock;

/*
 * 20170409 
 * PerformanceTest: Create/save on pathTC, add to AnalyzerEach
 * Total time needed: 0.376 seconds for 50000 iterations
 * That's 7520.0nanos per iteration
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestAnalyzerEach {

	public static void main(String[] args) {
		
		int amount = 50000;
		
		Clock tcClock = new NanoClock();
		Clock analyzerClock = new TestClock();
		
		AnalyzerEach<eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones> analyzer = AnalyzerEach.create(analyzerClock);
		
//		waitForInput();
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorWithPath<eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones> tc = TestTimeCollectorProvider.getTC(tcClock);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.CREATION);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_HANDLER_CONTEXT);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_SEARCH_HANDLER);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_SEARCH_HANDLER);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_HANDLER);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DAO_GETSTATE);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DBPOOL);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DBPOOL);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DB_GETSTATE);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DB_GETSTATE);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DAO_GETSTATE);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_CALC1);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_CALC1);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DECIDER);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DECIDER);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DAO_SAVE);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DBPOOL);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DBPOOL);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.BEFORE_DB_SAVE_DECISION1);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DB_SAVE_DECISION1);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_DAO_SAVE);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_HANDLER);
			tc.saveTime(eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones.AFTER_HANDLER_CONTEXT);

			analyzer.addCollector(tc);	
		}
		PerformanceTestsUtils.end("Create/save on pathTC, add to AnalyzerEach", amount, start);
		
		int i=0;
		
	}
	
	
}
