package eu.andymel.timecollector.performancetests;



import java.time.Clock;
import java.time.Instant;
import java.util.Collection;

import eu.andymel.timecollector.TestClockIncrementBy1;
import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath.AnalyzerEachEntry;
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
		Clock analyzerClock = new TestClockIncrementBy1();
		
		AnalyzerEachPath<TestMilestones> analyzerEach = AnalyzerEachPath.create(analyzerClock);
		
//		waitForInput();
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorWithPath<TestMilestones> tc = TestTimeCollectorProvider.getTC(tcClock);
			tc.saveTime(TestMilestones.CREATION);
			tc.saveTime(TestMilestones.BEFORE_HANDLER_CONTEXT);
			tc.saveTime(TestMilestones.BEFORE_SEARCH_HANDLER);
			tc.saveTime(TestMilestones.AFTER_SEARCH_HANDLER);
			tc.saveTime(TestMilestones.BEFORE_HANDLER);
			tc.saveTime(TestMilestones.BEFORE_DAO_GETSTATE);
			tc.saveTime(TestMilestones.BEFORE_DBPOOL);
			tc.saveTime(TestMilestones.AFTER_DBPOOL);
			tc.saveTime(TestMilestones.BEFORE_DB_GETSTATE);
			tc.saveTime(TestMilestones.AFTER_DB_GETSTATE);
			tc.saveTime(TestMilestones.AFTER_DAO_GETSTATE);
			tc.saveTime(TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestMilestones.AFTER_CALC1);
			tc.saveTime(TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestMilestones.AFTER_DECIDER);
			tc.saveTime(TestMilestones.BEFORE_DAO_SAVE);
			tc.saveTime(TestMilestones.BEFORE_DBPOOL);
			tc.saveTime(TestMilestones.AFTER_DBPOOL);
			tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1);
			tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1);
			tc.saveTime(TestMilestones.AFTER_DAO_SAVE);
			tc.saveTime(TestMilestones.AFTER_HANDLER);
			tc.saveTime(TestMilestones.AFTER_HANDLER_CONTEXT);

			analyzerEach.addCollector(tc);	
		}
		PerformanceTestsUtils.end("Create/save on pathTC, add to AnalyzerEach", amount, start);
		
		Collection<AnalyzerEachEntry<TestMilestones>> all = analyzerEach.getAll();
		
		
		
	}
	
	
}
