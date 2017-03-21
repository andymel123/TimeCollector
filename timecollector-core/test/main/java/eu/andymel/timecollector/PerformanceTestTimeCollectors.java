package eu.andymel.timecollector;


import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.report.TextualPathAnalyzer;
import eu.andymel.timecollector.util.NanoClock;

public class PerformanceTestTimeCollectors {

	public static void main(String[] args) {
		
		int amount = 10_000;
		
		NanoClock clock = new NanoClock();
		
		TextualPathAnalyzer<eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones> analyzer = new TextualPathAnalyzer<>();
		
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
		
		o(analyzer.toString(TimeUnit.NANOSECONDS));
	}
	
	private static final void o(Object o){
		System.out.println(o);
	}
}
