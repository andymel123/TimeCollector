package eu.andymel.timecollector.path;

import static eu.andymel.timecollector.path.TestMilestones.AFTER_CALC1;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DAO_GETSTATE;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DAO_SAVE;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DBPOOL;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DB_GETSTATE;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DB_GETSTATE_RESULTSET;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DB_SAVE_DECISION1_RESULTSET;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DB_SAVE_DECISION2_RESULTSET;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DB_SAVE_DESICION1;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DB_SAVE_DESICION2;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_DECIDER;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_HANDLER;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_HANDLER_CONTEXT;
import static eu.andymel.timecollector.path.TestMilestones.AFTER_SEARCH_HANDLER;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_CALC1;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DAO_GETSTATE;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DAO_SAVE;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DBPOOL;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DB_GETSTATE;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DB_GETSTATE_RESULTSET;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DB_SAVE_DECISION1_RESULTSET;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DB_SAVE_DECISION2_RESULTSET;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DB_SAVE_DESICION1;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DB_SAVE_DESICION2;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_DECIDER;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_HANDLER;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_HANDLER_CONTEXT;
import static eu.andymel.timecollector.path.TestMilestones.BEFORE_SEARCH_HANDLER;
import static eu.andymel.timecollector.path.TestMilestones.CREATION;

import java.time.Clock;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;

public class PathBuilderTest {

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
