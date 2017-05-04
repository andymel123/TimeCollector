package eu.andymel.timecollector.server;

import java.time.Clock;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.report.analyzer.Analyzer;
import eu.andymel.timecollector.report.analyzer.AnalyzerEachPath;
import eu.andymel.timecollector.teststuff.TestClockIncrementRandom;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider;
import eu.andymel.timecollector.teststuff.TestTimeCollectorProvider.TestMilestones;



public class TCMonitorServerRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(TCMonitorServerRunner.class);
	
//	private final static Clock tcClock = new NanoClock();
	private final static Clock tcClock = new TestClockIncrementRandom(500, 1000);
	
	
	private final static TestMilestones[] milestones;
	static{
		milestones = new TestMilestones[]{
				TestMilestones.CREATION,
				TestMilestones.HANDLER_CTX_S,
				TestMilestones.SEARCH_HANDLER_S,
				TestMilestones.SEARCH_HANDLER_E,
				TestMilestones.HANDLER_S,

				TestMilestones.DAO_GET_S,
				TestMilestones.DBPOOL_S,
				TestMilestones.DBPOOL_E,
				TestMilestones.DB_GET_S,
				TestMilestones.DB_GET_E,
				TestMilestones.DAO_GET_E,	// idx 10

				TestMilestones.CALC1_S,
				TestMilestones.CALC1_E,

				TestMilestones.DECIDER_S,
				TestMilestones.DECIDER_E,

				TestMilestones.DAO_SAVE_S,
				TestMilestones.DBPOOL_S,
				TestMilestones.DBPOOL_E,
				TestMilestones.DB_SAVE1_S,
				TestMilestones.RETRY,		// idx 19
				
				TestMilestones.DAO_GET_S,
				TestMilestones.DBPOOL_S,
				TestMilestones.DBPOOL_E,
				TestMilestones.DB_GET_S,
				TestMilestones.DB_GET_E,
				TestMilestones.DAO_GET_E,

				TestMilestones.CALC1_S,
				TestMilestones.CALC1_E,

				TestMilestones.DECIDER_S,
				TestMilestones.DECIDER_E,

				TestMilestones.DAO_SAVE_S,	// idx 30
				TestMilestones.DBPOOL_S,
				TestMilestones.DBPOOL_E,
								
				TestMilestones.DB_SAVE2_S,
				TestMilestones.DB_SAVE2_E,
				
				TestMilestones.DAO_SAVE_E,

				TestMilestones.HANDLER_E,
				TestMilestones.HANDLER_CTX_E	// idx 37
				
		};
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
				// for example "/foo/*"
				return "/static/*";
			}

			@Override
			public double getUpdatesPerMinute() {
				return 60;
			}
			
		};
		
		TCMonitorServer s = new TCMonitorServer(
			cfg
		);
		
		AnalyzerEachPath<TestMilestones> analyzer = AnalyzerEachPath.create(
			Clock.systemUTC(),	// clock to get time for the request time from (x-axis in graph), thats not the clock used in the timeCollectors  
			100	// max saved number of requests (the monitoring graph will show just them, or has to change old request data on client side)
		);
		s.setTimeCollectorAnalyzer(analyzer);

		Random rng = new Random();
		int minMS = 5;
		int maxMS = 20;
		Supplier<TimeCollectorWithPath<TestMilestones>> tcSupplier = ()->{
			TimeCollectorWithPath<TestMilestones> tc = TestTimeCollectorProvider.getTC(tcClock);
//			walkPath(tc, minMS + rng.nextInt(maxMS-minMS));
//			walkPath(tc, 5); 
			walkPath(tc, 5 + rng.nextInt(2));
//			walkPath(tc, 4);
			return tc;
		};
					

		// fill up to have data in the GUI from the beginning
		for(int i=0; i<1000; i++){
			analyzer.addCollector(tcSupplier.get());
		}

		
		LOG.info("Starting monitoring server...");
		try {
			s.start(true);
		} catch (Exception e) {
			LOG.error("Can't start Jetty Server", e);
			return;
		}

		try{
			// add hook to monitor server, if it shutsdoen, stop the test as well
			AtomicBoolean stopTest = new AtomicBoolean(false);
			s.addServerStoppingHook(()->stopTest.set(true));
			
			LOG.info("Start to generate test data...");
			runTest(
				10, 		// the test adds this amount of timeCollectors per second
				analyzer,	// the analyzer to add the timeCollectors to
				stopTest,
				tcSupplier
			);
		}catch(Exception e){
			LOG.error("Exception while trying to test", e);
			s.stop();
		}
		
	}

	private static void runTest(double timeCollectorsPerSec, Analyzer<TestMilestones, TimeCollectorWithPath<TestMilestones>> analyzer, AtomicBoolean stop, Supplier<TimeCollectorWithPath<TestMilestones>> tcSupplier){
		
		Thread.currentThread().setName("TimeCollector-Producer");
		
		while(!stop.get()){
			
			analyzer.addCollector(tcSupplier.get());
			
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




	/**
	 * @param tc
	 * @param len THis wont work just until the first milestone name that is not on the allowed path
	 */
	private static void walkPath(TimeCollectorWithPath<TestMilestones> tc, int len) {

//		if(len<3){
//			switch(len){
//				case 0: walkPath0(tc); return;
//				case 1: walkPath1(tc); return;
//				case 2: walkPath2(tc); return;
//			}
//		}
		
		if(len==5){
			walkPathWithRepeat(tc);
			return;
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

	
	


	private static void walkPathWithRepeat(TimeCollectorWithPath<TestMilestones> tc) {
		// LOG.info("path2");
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
}
