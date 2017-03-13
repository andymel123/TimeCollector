package eu.andymel.timecollector;

import static eu.andymel.timecollector.path.TestMilestones.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.path.AllowedPathBuilder;
import eu.andymel.timecollector.path.TestMilestones;

public class TimeCollectorWithPathTest {

	private TimeCollector<TestMilestones> tc; 
	
	@Before
	public void setup(){
		tc = TimeCollectorWithPath.createWithPath(
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
							AllowedPathBuilder.<TestMilestones>startSubpath(BEFORE_DB_SAVE_DESICION1)
								.then(AFTER_DB_SAVE_DESICION1)
								.then(BEFORE_DB_SAVE_DECISION1_RESULTSET)
								.then(AFTER_DB_SAVE_DECISION1_RESULTSET)
								.build(),
							AllowedPathBuilder.<TestMilestones>startSubpath(BEFORE_DB_SAVE_DESICION2)
								.then(AFTER_DB_SAVE_DESICION2)
								.then(BEFORE_DB_SAVE_DECISION2_RESULTSET)
								.then(AFTER_DB_SAVE_DECISION2_RESULTSET)
								.build()
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
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testWrongFirstMileStone() {
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testSetMultipleTimes() {
		tc.saveTime(CREATION);	
		tc.saveTime(CREATION);
	}

	
	/*
	 * TODO write test with path including a loop of not required nodes...I guess 
	 * it would make problems with getNextPermissionNodes() at the moment 
	 */

	
}
