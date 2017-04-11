package eu.andymel.timecollector.performancetests;



import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.o;
import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.waitForInput;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.analyzer.AnalyzerAvgPath;
import eu.andymel.timecollector.report.html.TimeSpanNameFormatter;
import eu.andymel.timecollector.util.NanoClock;

/*
 * 20170322
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 2.771 seconds for 50000 iterations 
 * That's 55420.0nanos per iteration
 * 
 * rebuilt saveTime
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 3.35 seconds for 50000 iterations
 * That's 67000.0nanos per iteration
 * 
 * new saveTime and getRecordedPaths
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 0.761 seconds for 50000 iterations
 * That's 15220.0nanos per iteration
 * 
 * 20170327
 * using HashMap<ID1, HashMap<ID2, time>> instead of Hashmap<String, time>
 * using AbstractMap.SimpleEntry instead of GraphNode for getting RecPath
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 0.511 seconds for 50000 iterations
 * That's 10220.0nanos per iteration
 * 
 * 20170328 
 * Removed redundant list from Analyzer
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 0.374 seconds for 50000 iterations
 * That's 7480.0nanos per iteration
 * 
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestSaveTimePath {

	public static void main(String[] args) {
		
		int amount = 50_000;
		
		NanoClock clock = new NanoClock();
		
		AnalyzerAvgPath<TestMilestones> analyzer = AnalyzerAvgPath.create();
		
		waitForInput();
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorWithPath<TestMilestones> tc = TestTimeCollectorProvider.getTC(clock);
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

			analyzer.addCollector(tc);	
		}
		PerformanceTestsUtils.end("Create TimeCollectorWithPath", amount, start);
		
		o(analyzer.toString(TimeUnit.NANOSECONDS, TimeSpanNameFormatter.DEFAULT_TIMESPAN_NAME_FORMATTER));
		
	}
	
	
}
