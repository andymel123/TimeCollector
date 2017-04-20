package eu.andymel.timecollector.server;

import java.time.Clock;
import java.util.EnumSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.util.NanoClock;

public class TCMonitorServerRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(TCMonitorServerRunner.class);
	
	private final static TestMilestones[] milestones;
	static{
		EnumSet<TestMilestones> e = EnumSet.allOf(TestMilestones.class);
		milestones = e.toArray(new TestMilestones[e.size()]);
	}
	
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
				// if its not simply "/" it has to be added in the javascript
				// as well (function getWebSocketPath())
				return "/";
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
		
		TCMonitorServer s = new TCMonitorServer(
			cfg,
			120 // updates per minute
		);
		
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
				10, 		// the test adds this amount of timeCollectors per second
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
		
		Random rng = new Random();
		
		while(!stop.get()){
//			LOG.info("new tc...");
			
			TimeCollectorWithPath<TestMilestones> tc = TestTimeCollectorProvider.getTC(tcClock);
			
			walkPath(tc, rng.nextInt(5));
			
//			switch(rng.nextInt(3)){
//				case 0: walkPath0(tc); break;
//				case 1: walkPath1(tc); break;
//				case 2: walkPath2(tc); break;
//			}
			
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


	private static void walkPath0(TimeCollectorWithPath<TestMilestones> tc) {
//		LOG.info("path0");
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
	}

	private static void walkPath1(TimeCollectorWithPath<TestMilestones> tc) {
//		LOG.info("path1");
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
		tc.saveTime(TestMilestones.DB_SAVE2_S);
		tc.saveTime(TestMilestones.DB_SAVE2_E);
		tc.saveTime(TestMilestones.DAO_SAVE_E);
		tc.saveTime(TestMilestones.HANDLER_E);
		tc.saveTime(TestMilestones.HANDLER_CTX_E);

	}
	
	private static void walkPath2(TimeCollectorWithPath<TestMilestones> tc) {
//		LOG.info("path2");
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
		// retry
		tc.saveTime(TestMilestones.RETRY);
		tc.saveTime(TestMilestones.DB_SAVE1_E);
		tc.saveTime(TestMilestones.DAO_SAVE_E);
		
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
	}

	/**
	 * @param tc
	 * @param len THis wont work just until the first milestone name that is not on the allowed path
	 */
	private static void walkPath(TimeCollectorWithPath<TestMilestones> tc, int len) {

		if(len<3){
			switch(len){
				case 0: walkPath0(tc); return;
				case 1: walkPath1(tc); return;
				case 2: walkPath2(tc); return;
			}
		}
		
		if(len>milestones.length)return;
		
		for(int i=0; i<len;i++){
			tc.saveTime(milestones[i]);
		}

	}
	
//	static String readFile(String path, Charset encoding) throws IOException {
//		byte[] encoded = Files.readAllBytes(Paths.get(path));
//		return new String(encoded, encoding);
//	}

}
