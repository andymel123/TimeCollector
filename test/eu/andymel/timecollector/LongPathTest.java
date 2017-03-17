package eu.andymel.timecollector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;

public class LongPathTest {

	private enum TestMilestones{

		CREATION,
		BEFORE_HANDLER_CONTEXT,
			BEFORE_SEARCH_HANDLER,
			AFTER_SEARCH_HANDLER,
			BEFORE_HANDLER,
				BEFORE_DAO_GETSTATE,
					BEFORE_DBPOOL,
					AFTER_DBPOOL,
					BEFORE_DB_GETSTATE,
					AFTER_DB_GETSTATE,
					BEFORE_DB_GETSTATE_RESULTSET,
					AFTER_DB_GETSTATE_RESULTSET,
				AFTER_DAO_GETSTATE,
				
				BEFORE_CALC1,
				AFTER_CALC1,
				
				BEFORE_DECIDER,
				AFTER_DECIDER,
				
				BEFORE_DAO_SAVE,
					// either
					BEFORE_DB_SAVE_DESICION1,
					AFTER_DB_SAVE_DESICION1,
					BEFORE_DB_SAVE_DECISION1_RESULTSET,
					AFTER_DB_SAVE_DECISION1_RESULTSET,
					// or
					BEFORE_DB_SAVE_DESICION2,
					AFTER_DB_SAVE_DESICION2,
					BEFORE_DB_SAVE_DECISION2_RESULTSET,
					AFTER_DB_SAVE_DECISION2_RESULTSET,
					
				AFTER_DAO_SAVE,
			AFTER_HANDLER,
		AFTER_HANDLER_CONTEXT;

	}
	
	private TimeCollector<TestMilestones> tc; 
	
	@Before
	public void setup(){

		tc = TimeCollectorWithPath.createWithPath(
				new TestClock(),
				AllowedPathsGraph.
				<TestMilestones>
				start(TestMilestones.CREATION)
				.then(TestMilestones.BEFORE_HANDLER_CONTEXT)
				.then(TestMilestones.BEFORE_SEARCH_HANDLER)
				.then(TestMilestones.AFTER_SEARCH_HANDLER)
				.then(TestMilestones.BEFORE_HANDLER)
					.then(TestMilestones.BEFORE_DAO_GETSTATE)
						.then(TestMilestones.BEFORE_DBPOOL)
						.then(TestMilestones.AFTER_DBPOOL)
						.then(TestMilestones.BEFORE_DB_GETSTATE)
						.then(TestMilestones.AFTER_DB_GETSTATE)
						.then(TestMilestones.BEFORE_DB_GETSTATE_RESULTSET)
						.then(TestMilestones.AFTER_DB_GETSTATE_RESULTSET)
					.then(TestMilestones.AFTER_DAO_GETSTATE)
					
					.then(TestMilestones.BEFORE_CALC1)
					.then(TestMilestones.AFTER_CALC1)
					
					.then(TestMilestones.BEFORE_DECIDER)
					.then(TestMilestones.AFTER_DECIDER)
					
					.then(TestMilestones.BEFORE_DAO_SAVE)
						.thenEither(
							AllowedPathsGraph.<TestMilestones>subpath(TestMilestones.BEFORE_DB_SAVE_DESICION1)
								.then(TestMilestones.AFTER_DB_SAVE_DESICION1)
								.then(TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET)
								.then(TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET),
							AllowedPathsGraph.<TestMilestones>subpath(TestMilestones.BEFORE_DB_SAVE_DESICION2)
								.then(TestMilestones.AFTER_DB_SAVE_DESICION2)
								.then(TestMilestones.BEFORE_DB_SAVE_DECISION2_RESULTSET)
								.then(TestMilestones.AFTER_DB_SAVE_DECISION2_RESULTSET)
						)
					.then(TestMilestones.AFTER_DAO_SAVE)
				.then(TestMilestones.AFTER_HANDLER)
				.then(TestMilestones.AFTER_HANDLER_CONTEXT)
				.build()
			);

	}
	
	@Test
	public void testFirstMileStone() {
		tc.saveTime(TestMilestones.CREATION);
		assertNotNull(tc.getTime(TestMilestones.CREATION));
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
				tc.saveTime(TestMilestones.BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(TestMilestones.AFTER_DAO_GETSTATE);
			
			tc.saveTime(TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestMilestones.AFTER_CALC1);
			
			tc.saveTime(TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestMilestones.AFTER_DECIDER);
			
			tc.saveTime(TestMilestones.BEFORE_DAO_SAVE);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET);
			tc.saveTime(TestMilestones.AFTER_DAO_SAVE);
		tc.saveTime(TestMilestones.AFTER_HANDLER);
		tc.saveTime(TestMilestones.AFTER_HANDLER_CONTEXT);

		int count = 0;
		assertEquals(count++, tc.getTime(TestMilestones.CREATION).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_HANDLER_CONTEXT).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_SEARCH_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_SEARCH_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DAO_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DBPOOL).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DBPOOL).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DB_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DB_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DB_GETSTATE_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DB_GETSTATE_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DAO_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_CALC1).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_CALC1).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DECIDER).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DECIDER).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DAO_SAVE).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DB_SAVE_DESICION1).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DB_SAVE_DESICION1).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_DAO_SAVE).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.AFTER_HANDLER_CONTEXT).toEpochMilli());
		
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
				tc.saveTime(TestMilestones.BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(TestMilestones.AFTER_DAO_GETSTATE);
			
			tc.saveTime(TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestMilestones.AFTER_CALC1);
			
			tc.saveTime(TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestMilestones.AFTER_DECIDER);
			
			tc.saveTime(TestMilestones.BEFORE_DAO_SAVE);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET);
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
				tc.saveTime(TestMilestones.BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(TestMilestones.AFTER_DAO_GETSTATE);
			
			tc.saveTime(TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestMilestones.AFTER_CALC1);
			
			tc.saveTime(TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestMilestones.AFTER_DECIDER);
			
			tc.saveTime(TestMilestones.BEFORE_DAO_SAVE);
//				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET);
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
				tc.saveTime(TestMilestones.BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(TestMilestones.AFTER_DAO_GETSTATE);
			
			tc.saveTime(TestMilestones.BEFORE_CALC1);
			tc.saveTime(TestMilestones.AFTER_CALC1);
			
			tc.saveTime(TestMilestones.BEFORE_DECIDER);
			tc.saveTime(TestMilestones.AFTER_DECIDER);
			
			tc.saveTime(TestMilestones.BEFORE_DAO_SAVE);
			
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET);
				
				// sould not work as the above block and this next block is added with "thenEither"
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION2);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION2);
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DECISION2_RESULTSET);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DECISION2_RESULTSET);
				
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

	
	
	/*
	 * TODO write test with path including a loop of not required nodes...I guess 
	 * it would make problems with getNextPermissionNodes() at the moment 
	 */

	
}
