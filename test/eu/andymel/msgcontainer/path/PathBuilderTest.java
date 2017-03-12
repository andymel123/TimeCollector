package eu.andymel.msgcontainer.path;

import static eu.andymel.msgcontainer.path.TestMilestones.*;

import java.time.Clock;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.path.AllowedPathBuilder;

public class PathBuilderTest {

	private TimeCollector<TestMilestones> tc; 
	
	@Before
	public void setup(){
		tc = TimeCollectorWithPath.createWithPath(
				Clock.systemDefaultZone(),
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
							AllowedPathBuilder.<TestMilestones>start(BEFORE_DB_SAVE_DESICION1)
								.then(AFTER_DB_SAVE_DESICION1)
								.then(BEFORE_DB_SAVE_DECISION1_RESULTSET)
								.then(AFTER_DB_SAVE_DECISION1_RESULTSET)
								.build(),
							AllowedPathBuilder.<TestMilestones>start(BEFORE_DB_SAVE_DESICION2)
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

	@Test (expected=IllegalStateException.class)
	public void testSetMultipleTimes() {
		tc.saveTime(CREATION);	
		tc.saveTime(CREATION);
	}

	@Test (expected=IllegalStateException.class)
	public void testWrongFirstMileStone() {
		tc.saveTime(BEFORE_HANDLER_CONTEXT);
	}

	
}
