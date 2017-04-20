package eu.andymel.timecollector.report.html;


import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.performancetests.PerformanceTestsUtils;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.teststuff.TestClockIncrementRandom;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider.TestMilestones;

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
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.HANDLER_CTX_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.SEARCH_HANDLER_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.SEARCH_HANDLER_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.HANDLER_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DAO_GET_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DBPOOL_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DBPOOL_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DB_GET_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DB_GET_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DAO_GET_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.CALC1_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.CALC1_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DECIDER_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DECIDER_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DAO_SAVE_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DBPOOL_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DBPOOL_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DB_SAVE1_S);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DB_SAVE1_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.DAO_SAVE_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.HANDLER_E);
			tc.saveTime(TestTimeCollectorProvider.TestMilestones.HANDLER_CTX_E);

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
