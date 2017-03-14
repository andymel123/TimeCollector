package eu.andymel.timecollector;

import static eu.andymel.timecollector.TestMilestones.*;
import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.AllowedPathBuilder;

public class TimeCollectorWithPathTest {

	private TimeCollector<TestMilestones> tc; 
	
	@Before
	public void setup(){
		
		/**
		 * A clock that increases it's "time" by 1 millisecond per call
		 * to instant(). I don't need to test with real time, I just want to test
		 * if the timecollector returns the right time in getTime (has to increase 
		 * by 1 with this clock)
		 */
		Clock testClock = new Clock(){

			long count = 0;
			
			@Override
			public ZoneId getZone() {
				return null;
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return null;
			}

			@Override
			public Instant instant() {
				return Instant.ofEpochMilli(count++);
			}
			
		};
		
		tc = TimeCollectorWithPath.createWithPath(
				testClock,
				AllowedPathBuilder.<TestMilestones>
				start(CREATION)
				.then(BEFORE_HANDLER_CONTEXT)
				.then(BEFORE_SEARCH_HANDLER)
				.then(AFTER_SEARCH_HANDLER)
				.then(BEFORE_HANDLER)
					.then(BEFORE_DAO_GETSTATE)
						.then(BEFORE_DBPOOL)
						.then(AFTER_DBPOOL)
						.then(BEFORE_DB_GETSTATE)
						.then(AFTER_DB_GETSTATE)
						.then(BEFORE_DB_GETSTATE_RESULTSET)
						.then(AFTER_DB_GETSTATE_RESULTSET)
					.then(AFTER_DAO_GETSTATE)
					
					.then(BEFORE_CALC1)
					.then(AFTER_CALC1)
					
					.then(BEFORE_DECIDER)
					.then(AFTER_DECIDER)
					
					.then(BEFORE_DAO_SAVE)
						.thenEither(
							AllowedPathBuilder.<TestMilestones>subpath(BEFORE_DB_SAVE_DESICION1)
								.then(AFTER_DB_SAVE_DESICION1)
								.then(BEFORE_DB_SAVE_DECISION1_RESULTSET)
								.then(AFTER_DB_SAVE_DECISION1_RESULTSET),
							AllowedPathBuilder.<TestMilestones>subpath(BEFORE_DB_SAVE_DESICION2)
								.then(AFTER_DB_SAVE_DESICION2)
								.then(BEFORE_DB_SAVE_DECISION2_RESULTSET)
								.then(AFTER_DB_SAVE_DECISION2_RESULTSET)
						)
					.then(AFTER_DAO_SAVE)
				.then(AFTER_HANDLER)
				.then(AFTER_HANDLER_CONTEXT)
				.build()
			);

	}
	
	@Test
	public void testFirstMileStone() {
		tc.saveTime(CREATION);
		assertNotNull(tc.getTime(CREATION));
	}

	@Test
	public void testFullPath(){
		tc.saveTime(CREATION);
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
		tc.saveTime(BEFORE_SEARCH_HANDLER);
		tc.saveTime(AFTER_SEARCH_HANDLER);
		tc.saveTime(BEFORE_HANDLER);
			tc.saveTime(BEFORE_DAO_GETSTATE);
				tc.saveTime(BEFORE_DBPOOL);
				tc.saveTime(AFTER_DBPOOL);
				tc.saveTime(BEFORE_DB_GETSTATE);
				tc.saveTime(AFTER_DB_GETSTATE);
				tc.saveTime(BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(AFTER_DAO_GETSTATE);
			
			tc.saveTime(BEFORE_CALC1);
			tc.saveTime(AFTER_CALC1);
			
			tc.saveTime(BEFORE_DECIDER);
			tc.saveTime(AFTER_DECIDER);
			
			tc.saveTime(BEFORE_DAO_SAVE);
				tc.saveTime(BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(AFTER_DB_SAVE_DESICION1);
				tc.saveTime(BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(AFTER_DB_SAVE_DECISION1_RESULTSET);
			tc.saveTime(AFTER_DAO_SAVE);
		tc.saveTime(AFTER_HANDLER);
		tc.saveTime(AFTER_HANDLER_CONTEXT);

		int count = 0;
		assertEquals(count++, tc.getTime(CREATION).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_HANDLER_CONTEXT).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_SEARCH_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_SEARCH_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DAO_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DBPOOL).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DBPOOL).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DB_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DB_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DB_GETSTATE_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DB_GETSTATE_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DAO_GETSTATE).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_CALC1).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_CALC1).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DECIDER).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DECIDER).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DAO_SAVE).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DB_SAVE_DESICION1).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DB_SAVE_DESICION1).toEpochMilli());
		assertEquals(count++, tc.getTime(BEFORE_DB_SAVE_DECISION1_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DB_SAVE_DECISION1_RESULTSET).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_DAO_SAVE).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_HANDLER).toEpochMilli());
		assertEquals(count++, tc.getTime(AFTER_HANDLER_CONTEXT).toEpochMilli());
		
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testFullPathOneMissing(){
		tc.saveTime(CREATION);
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
		tc.saveTime(BEFORE_SEARCH_HANDLER);
		tc.saveTime(AFTER_SEARCH_HANDLER);
		tc.saveTime(BEFORE_HANDLER);
			tc.saveTime(BEFORE_DAO_GETSTATE);
				tc.saveTime(BEFORE_DBPOOL);
//				tc.saveTime(AFTER_DBPOOL);
				tc.saveTime(BEFORE_DB_GETSTATE);
				tc.saveTime(AFTER_DB_GETSTATE);
				tc.saveTime(BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(AFTER_DAO_GETSTATE);
			
			tc.saveTime(BEFORE_CALC1);
			tc.saveTime(AFTER_CALC1);
			
			tc.saveTime(BEFORE_DECIDER);
			tc.saveTime(AFTER_DECIDER);
			
			tc.saveTime(BEFORE_DAO_SAVE);
				tc.saveTime(BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(AFTER_DB_SAVE_DESICION1);
				tc.saveTime(BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(AFTER_DB_SAVE_DECISION1_RESULTSET);
			tc.saveTime(AFTER_DAO_SAVE);
		tc.saveTime(AFTER_HANDLER);
		tc.saveTime(AFTER_HANDLER_CONTEXT);

	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testFullPathOneMissingInSubPath(){
		tc.saveTime(CREATION);
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
		tc.saveTime(BEFORE_SEARCH_HANDLER);
		tc.saveTime(AFTER_SEARCH_HANDLER);
		tc.saveTime(BEFORE_HANDLER);
			tc.saveTime(BEFORE_DAO_GETSTATE);
				tc.saveTime(BEFORE_DBPOOL);
				tc.saveTime(AFTER_DBPOOL);
				tc.saveTime(BEFORE_DB_GETSTATE);
				tc.saveTime(AFTER_DB_GETSTATE);
				tc.saveTime(BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(AFTER_DAO_GETSTATE);
			
			tc.saveTime(BEFORE_CALC1);
			tc.saveTime(AFTER_CALC1);
			
			tc.saveTime(BEFORE_DECIDER);
			tc.saveTime(AFTER_DECIDER);
			
			tc.saveTime(BEFORE_DAO_SAVE);
//				tc.saveTime(BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(AFTER_DB_SAVE_DESICION1);
				tc.saveTime(BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(AFTER_DB_SAVE_DECISION1_RESULTSET);
			tc.saveTime(AFTER_DAO_SAVE);
		tc.saveTime(AFTER_HANDLER);
		tc.saveTime(AFTER_HANDLER_CONTEXT);

	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testBothSubPath(){
		tc.saveTime(CREATION);
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
		tc.saveTime(BEFORE_SEARCH_HANDLER);
		tc.saveTime(AFTER_SEARCH_HANDLER);
		tc.saveTime(BEFORE_HANDLER);
			tc.saveTime(BEFORE_DAO_GETSTATE);
				tc.saveTime(BEFORE_DBPOOL);
				tc.saveTime(AFTER_DBPOOL);
				tc.saveTime(BEFORE_DB_GETSTATE);
				tc.saveTime(AFTER_DB_GETSTATE);
				tc.saveTime(BEFORE_DB_GETSTATE_RESULTSET);
				tc.saveTime(AFTER_DB_GETSTATE_RESULTSET);
			tc.saveTime(AFTER_DAO_GETSTATE);
			
			tc.saveTime(BEFORE_CALC1);
			tc.saveTime(AFTER_CALC1);
			
			tc.saveTime(BEFORE_DECIDER);
			tc.saveTime(AFTER_DECIDER);
			
			tc.saveTime(BEFORE_DAO_SAVE);
			
				tc.saveTime(BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(AFTER_DB_SAVE_DESICION1);
				tc.saveTime(BEFORE_DB_SAVE_DECISION1_RESULTSET);
				tc.saveTime(AFTER_DB_SAVE_DECISION1_RESULTSET);
				
				// sould not work as the above block and this next block is added with "thenEither"
				tc.saveTime(BEFORE_DB_SAVE_DESICION2);
				tc.saveTime(AFTER_DB_SAVE_DESICION2);
				tc.saveTime(BEFORE_DB_SAVE_DECISION2_RESULTSET);
				tc.saveTime(AFTER_DB_SAVE_DECISION2_RESULTSET);
				
			tc.saveTime(AFTER_DAO_SAVE);
		tc.saveTime(AFTER_HANDLER);
		tc.saveTime(AFTER_HANDLER_CONTEXT);

	}

	
	@Test (expected=MilestoneNotAllowedException.class)
	public void testWrongFirstMileStone() {
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testSetMultipleTimesStartNode() {
		tc.saveTime(CREATION);	
		tc.saveTime(CREATION);
	}
	@Test (expected=MilestoneNotAllowedException.class)
	public void testSetMultipleTimesOtherNode() {
		tc.saveTime(CREATION);	
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
	}

	
	
	/*
	 * TODO write test with path including a loop of not required nodes...I guess 
	 * it would make problems with getNextPermissionNodes() at the moment 
	 */

	
}
