package eu.andymel.timecollector.performancetests;



import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.HTMLFileAnalyzer;
import eu.andymel.timecollector.util.NanoClock;

/*
 * 
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestWithHTMLFileAnalyzer {

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceTestWithHTMLFileAnalyzer.class);
	
	public static void main(String[] args) {
		
		int amount = 50_000;
		
		NanoClock clock = new NanoClock();
		
		HTMLFileAnalyzer<eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones> analyzer = HTMLFileAnalyzer.create();
		
//		waitForInput();
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorWithPath<eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones> tc = TestTimeCollectorProvider.getTC(clock);
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
		PerformanceTestsUtils.end("Create TimeCollectorWithPath", amount, start);
		
//		o(analyzer.getHTMLString(TimeUnit.NANOSECONDS));
		try {
			File f = new File("output.html");
			analyzer.writeToFile(f, TimeUnit.NANOSECONDS, false);
			LOG.info("HTML written to file '"+f.getAbsolutePath()+"'");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
