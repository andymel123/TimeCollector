package eu.andymel.timecollector.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TestClockIncrementBy1;
import eu.andymel.timecollector.TestClockIncrementRandom;
import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.util.NanoClock;

public class TCMonitorServerRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(TCMonitorServerRunner.class);
	
	public static void main(String[] args) {

		LOG.error("error");
		LOG.warn("warn");
		LOG.info("info");
		LOG.debug("debug");
		LOG.trace("trace");
		
		TCMonitorServerConfig cfg = new TCMonitorServerConfig() {

			@Override
			public int getPort() {
				return 1234;
			}

			@Override
			public String getContextPath() {
				// those two work: /foo/ or /foo
				return "/timecollector";
			}

			@Override
			public String getStaticWebContentDir() {
				return "WebContent";
			}
			@Override
			public String getSubPathStaticWebContent() {
				// for example "/foo/*"
				return "/static/*";
			}
			
		};
		
		TCMonitorServer s = new TCMonitorServer(cfg);
		AnalyzerEachPath<TestMilestones> analyzer = AnalyzerEachPath.create(
			Clock.systemUTC(),	// clock to get time for the request time from (x-axis in graph), thats not the clock used in the timeCollectors  
			100	// max saved number of requests (the monitoring graph will show just them, or has to change old request data on client side)
		);
		s.setTimeCollectorAnalyzer(analyzer);

		LOG.info("Starting monitoring server...");
		try {
			s.start(true);
		} catch (Exception e) {
			LOG.error("Can't start Jetty Server", e);
		}

		try{
			// add hook to monitor server, if it shutsdoen, stop the test as well
			AtomicBoolean stopTest = new AtomicBoolean(false);
			s.addServerStoppingHook(()->stopTest.set(true));
			
			LOG.info("Start to generate test data...");
			runTest(
				10, 			// the test adds this amount of timeCollectors per second
				analyzer,	// the analyzer to add the timeCollectors to
				stopTest
			);
		}catch(Exception e){
			LOG.error("Exception while trying to test", e);
			s.stop();
		}
		
	}

	private static void runTest(double timeCollectorsPerSec, Analyzer<TestMilestones, TimeCollectorWithPath<TestMilestones>> analyzer, AtomicBoolean stop){
		
		Thread.currentThread().setName("TimeCollector-Producer");
		
		Clock tcClock = new NanoClock();
		
		while(!stop.get()){
//			LOG.info("new tc...");
			
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
			analyzer.addCollector(tc);
			
			double sleepTime = 1000/timeCollectorsPerSec;
			
			if(sleepTime>1){
				try {
					Thread.sleep((long)sleepTime);
				} catch (InterruptedException e) {
					LOG.info("Break monitor loop because of interupt");
					break;
				}	
			}else{
				LOG.warn("Not yet implemented!");
				break;
			}
			
			
		}
	}

//	static String readFile(String path, Charset encoding) throws IOException {
//		byte[] encoded = Files.readAllBytes(Paths.get(path));
//		return new String(encoded, encoding);
//	}

}
