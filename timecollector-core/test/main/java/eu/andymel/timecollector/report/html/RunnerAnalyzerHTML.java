package eu.andymel.timecollector.report.html;


import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TestClockIncrementRandom;
import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.performancetests.PerformanceTestsUtils;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;

/*
 * PerformanceTest: Create TimeCollectorWithPath
 * Total time needed: 0.397 seconds for 50000 iterations
 * That's 7940.0nanos per iteration
 */

 
/**
 * 
 * @author andymatic
 *
 */
public class RunnerAnalyzerHTML {

	private static final Logger LOG = LoggerFactory.getLogger(RunnerAnalyzerHTML.class);
	
	public static void main(String[] args) {
		
		int amount = 50_000;
		
//		Clock clock = new NanoClock();
		Clock clock = new TestClockIncrementRandom(1, 1000);
//		Analyzer<TestMilestones, TimeCollectorWithPath<TestMilestones>>  analyzer = AnalyzerAvgPath.create(); 
		Analyzer<TestMilestones, TimeCollectorWithPath<TestMilestones>>  analyzer = AnalyzerEachPath.create(clock); 
		
//		waitForInput();
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorWithPath<TestTimeCollectorProvider.TestMilestones> tc = TestTimeCollectorProvider.getTC(clock);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.CREATION);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_HANDLER_CONTEXT);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_SEARCH_HANDLER);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_SEARCH_HANDLER);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_HANDLER);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DAO_GETSTATE);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DBPOOL);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DBPOOL);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DB_GETSTATE);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DB_GETSTATE);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DAO_GETSTATE);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_CALC1);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DECIDER);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DAO_SAVE);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DBPOOL);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DBPOOL);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.BEFORE_DB_SAVE_DECISION1);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DB_SAVE_DECISION1);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_DAO_SAVE);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_HANDLER);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.AFTER_HANDLER_CONTEXT);

			analyzer.addCollector(tc);	
		}
		PerformanceTestsUtils.end("Create TimeCollectorWithPath", amount, start);
		
		
		AbstractHTMLFormatter<TestTimeCollectorProvider.TestMilestones> htmlFormatter;
		
//		o(analyzer.getHTMLString(TimeUnit.NANOSECONDS));
		
//		htmlFormatter = HTMLFormatterPath.create(analyzer);
//		htmlFormatter = HTMLFormatterCandlestick.create(analyzer);
//		htmlFormatter = HTMLFormatterBars.create(analyzer);
//		htmlFormatter = HTMLFormatterRangeBars.create(analyzer);
		htmlFormatter = HTMLFormatterStackedBars.create(analyzer);
		
		try {
			File f = new File("output.html");
			htmlFormatter.writeToFile(f, TimeUnit.MICROSECONDS, false);
			LOG.info("HTML written to file '"+f.getAbsolutePath()+"'");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
