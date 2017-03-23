package eu.andymel.timecollector;

import static eu.andymel.timecollector.PathTestUtils.checkRecPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.Path;
import eu.andymel.timecollector.report.TextualPathAnalyzer;

public class LongPathTest {

	private TimeCollectorWithPath<TestMilestones> tc; 
	
	@Before
	public void setup(){
		tc = TestTimeCollectorProvider.getTC(new TestClock());
	}
	
	@Test
	public void testFirstMileStone() {
		tc.saveTime(TestMilestones.CREATION);
		List<List<GraphNode<GraphNode<TestMilestones, NodePermissions>, Instant>>> recordedPaths =  tc.getRecordedPaths();
		assertNotNull(recordedPaths);
		assertEquals(1, recordedPaths.size());
	}

	@Test
	public void testFullPath(){
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

		checkRecPath(tc, 23);
		
		TextualPathAnalyzer<TestMilestones> analyzer = new TextualPathAnalyzer();
		analyzer.addCollector(tc);
		o(analyzer.toString());
		
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testFullPathOneMissing(){
		tc.saveTime(TestMilestones.CREATION);
		tc.saveTime(TestMilestones.BEFORE_HANDLER_CONTEXT);
		tc.saveTime(TestMilestones.BEFORE_SEARCH_HANDLER);
		tc.saveTime(TestMilestones.AFTER_SEARCH_HANDLER);
		tc.saveTime(TestMilestones.BEFORE_HANDLER);
			tc.saveTime(TestMilestones.BEFORE_DAO_GETSTATE);
				tc.saveTime(TestMilestones.BEFORE_DBPOOL);
//				tc.saveTime(TestMilestones.AFTER_DBPOOL);
				tc.saveTime(TestMilestones.BEFORE_DB_GETSTATE);
				tc.saveTime(TestMilestones.AFTER_DB_GETSTATE);
			tc.saveTime(TestMilestones.AFTER_DAO_GETSTATE);
			
			tc.saveTime(TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestMilestones.AFTER_CALC1);
			
			tc.saveTime(TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestMilestones.AFTER_DECIDER);
			
			tc.saveTime(TestMilestones.BEFORE_DAO_SAVE);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1);
			tc.saveTime(TestMilestones.AFTER_DAO_SAVE);
		tc.saveTime(TestMilestones.AFTER_HANDLER);
		tc.saveTime(TestMilestones.AFTER_HANDLER_CONTEXT);

	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testFullPathOneMissingInSubPath(){
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
//				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1);
			tc.saveTime(TestMilestones.AFTER_DAO_SAVE);
		tc.saveTime(TestMilestones.AFTER_HANDLER);
		tc.saveTime(TestMilestones.AFTER_HANDLER_CONTEXT);

	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testBothSubPath(){
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
			
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1);
				
				// should not work as the above block and this next block are in different paths
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION2);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION2);
				
			tc.saveTime(TestMilestones.AFTER_DAO_SAVE);
		tc.saveTime(TestMilestones.AFTER_HANDLER);
		tc.saveTime(TestMilestones.AFTER_HANDLER_CONTEXT);

	}

	
	@Test (expected=MilestoneNotAllowedException.class)
	public void testWrongFirstMileStone() {
		tc.saveTime(TestMilestones.BEFORE_HANDLER_CONTEXT);
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testSetMultipleTimesStartNode() {
		tc.saveTime(TestMilestones.CREATION);	
		tc.saveTime(TestMilestones.CREATION);
	}
	@Test (expected=MilestoneNotAllowedException.class)
	public void testSetMultipleTimesOtherNode() {
		tc.saveTime(TestMilestones.CREATION);	
		tc.saveTime(TestMilestones.BEFORE_HANDLER_CONTEXT);
		tc.saveTime(TestMilestones.BEFORE_HANDLER_CONTEXT);
	}

	

	private static final void o(Object o){
		System.out.println(o);
	}
}
