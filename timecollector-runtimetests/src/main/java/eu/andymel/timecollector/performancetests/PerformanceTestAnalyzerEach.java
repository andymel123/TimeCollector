package eu.andymel.timecollector.performancetests;



import java.time.Clock;
import java.time.Instant;
import java.util.Collection;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath.AnalyzerEachEntry;
import eu.andymel.timecollector.teststuff.TestClockIncrementBy1;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider.TestMilestones;
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
			tc.saveTime(TestMilestones.HANDLER_CTX_S);
			tc.saveTime(TestMilestones.SEARCH_HANDLER_S);
			tc.saveTime(TestMilestones.SEARCH_HANDLER_E);
			tc.saveTime(TestMilestones.HANDLER_S);
			tc.saveTime(TestMilestones.DAO_GET_S);
			tc.saveTime(TestMilestones.DBPOOL_S);
			tc.saveTime(TestMilestones.DBPOOL_E);
			tc.saveTime(TestMilestones.DB_GET_S);
			tc.saveTime(TestMilestones.DB_GET_E);
			tc.saveTime(TestMilestones.DAO_GET_E);
			tc.saveTime(TestMilestones.CALC1_S);
			tc.saveTime(TestMilestones.CALC1_E);
			tc.saveTime(TestMilestones.DECIDER_S);
			tc.saveTime(TestMilestones.DECIDER_E);
			tc.saveTime(TestMilestones.DAO_SAVE_S);
			tc.saveTime(TestMilestones.DBPOOL_S);
			tc.saveTime(TestMilestones.DBPOOL_E);
			tc.saveTime(TestMilestones.DB_SAVE1_S);
			tc.saveTime(TestMilestones.DB_SAVE1_E);
			tc.saveTime(TestMilestones.DAO_SAVE_E);
			tc.saveTime(TestMilestones.HANDLER_E);
			tc.saveTime(TestMilestones.HANDLER_CTX_E);

			analyzerEach.addCollector(tc);	
		}
		PerformanceTestsUtils.end("Create/save on pathTC, add to AnalyzerEach", amount, start);
		
		Collection<AnalyzerEachEntry<TestMilestones>> all = analyzerEach.getAll();
		
		
		
	}
	
	
}
