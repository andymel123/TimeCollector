package eu.andymel.timecollector;

import static eu.andymel.timecollector.PathTestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.Path;
import eu.andymel.timecollector.graphs.PermissionNode;

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
				AFTER_DAO_GETSTATE,
				
				BEFORE_CALC1,
				AFTER_CALC1,
				
				BEFORE_DECIDER,
				AFTER_DECIDER,
				
				BEFORE_DAO_SAVE,
					// either
					BEFORE_DB_SAVE_DESICION1,
					AFTER_DB_SAVE_DESICION1,
					// or
					BEFORE_DB_SAVE_DESICION2,
					AFTER_DB_SAVE_DESICION2,
					
				AFTER_DAO_SAVE,
			AFTER_HANDLER,
		AFTER_HANDLER_CONTEXT;

	}
	
	private TimeCollectorWithPath<TestMilestones> tc; 
	
	@Before
	public void setup(){

		// TODO build this by reading a yaml or by doing any other simpler way 
		
		PermissionNode<TestMilestones> nCreation 							= PermissionNode.create(TestMilestones.CREATION);
		
		PermissionNode<TestMilestones> nBEFORE_HANDLER_CONTEXT 				= PermissionNode.create(TestMilestones.BEFORE_HANDLER_CONTEXT);
		PermissionNode<TestMilestones> nBEFORE_SEARCH_HANDLER 				= PermissionNode.create(TestMilestones.BEFORE_SEARCH_HANDLER);
		PermissionNode<TestMilestones> nAFTER_SEARCH_HANDLER 				= PermissionNode.create(TestMilestones.AFTER_SEARCH_HANDLER);

		PermissionNode<TestMilestones> nBEFORE_HANDLER 						= PermissionNode.create(TestMilestones.BEFORE_HANDLER);
			
		PermissionNode<TestMilestones> nBEFORE_DAO_GETSTATE 				= PermissionNode.create(TestMilestones.BEFORE_DAO_GETSTATE);
		PermissionNode<TestMilestones> nBEFORE_DBPOOL_GETSTATE				= PermissionNode.create(TestMilestones.BEFORE_DBPOOL);
		PermissionNode<TestMilestones> nAFTER_DBPOOL_GETSTATE				= PermissionNode.create(TestMilestones.AFTER_DBPOOL);
		PermissionNode<TestMilestones> nBEFORE_DB_GETSTATE 					= PermissionNode.create(TestMilestones.BEFORE_DB_GETSTATE);
		PermissionNode<TestMilestones> nAFTER_DB_GETSTATE 					= PermissionNode.create(TestMilestones.AFTER_DB_GETSTATE);
		PermissionNode<TestMilestones> nAFTER_DAO_GETSTATE 					= PermissionNode.create(TestMilestones.AFTER_DAO_GETSTATE);
		
		PermissionNode<TestMilestones> nBEFORE_CALC 						= PermissionNode.create(TestMilestones.BEFORE_CALC1);
		PermissionNode<TestMilestones> nAFTER_CALC 							= PermissionNode.create(TestMilestones.AFTER_CALC1);
		
		PermissionNode<TestMilestones> nBEFORE_DECIDER 						= PermissionNode.create(TestMilestones.BEFORE_DECIDER);
		PermissionNode<TestMilestones> nAFTER_DECIDER 						= PermissionNode.create(TestMilestones.AFTER_DECIDER);
		
		PermissionNode<TestMilestones> nBEFORE_DAO_SAVE 					= PermissionNode.create(TestMilestones.BEFORE_DAO_SAVE);
		PermissionNode<TestMilestones> nBEFORE_DBPOOL_SAVE 					= PermissionNode.create(TestMilestones.BEFORE_DBPOOL);
		PermissionNode<TestMilestones> nAFTER_DBPOOL_SAVE 					= PermissionNode.create(TestMilestones.AFTER_DBPOOL);
		PermissionNode<TestMilestones> nBEFORE_DB_SAVE_DESICION1 			= PermissionNode.create(TestMilestones.BEFORE_DB_SAVE_DESICION1);
		PermissionNode<TestMilestones> nAFTER_DB_SAVE_DESICION1 			= PermissionNode.create(TestMilestones.AFTER_DB_SAVE_DESICION1);
		PermissionNode<TestMilestones> nBEFORE_DB_SAVE_DESICION2 			= PermissionNode.create(TestMilestones.BEFORE_DB_SAVE_DESICION2);
		PermissionNode<TestMilestones> nAFTER_DB_SAVE_DESICION2 			= PermissionNode.create(TestMilestones.AFTER_DB_SAVE_DESICION2);
			
		PermissionNode<TestMilestones> nAFTER_DAO_SAVE 						= PermissionNode.create(TestMilestones.AFTER_DAO_SAVE);
		PermissionNode<TestMilestones> nAFTER_HANDLER 						= PermissionNode.create(TestMilestones.AFTER_HANDLER);
		PermissionNode<TestMilestones> nAFTER_HANDLER_CONTEXT				= PermissionNode.create(TestMilestones.AFTER_HANDLER_CONTEXT);
		
		
		
		
		// One more complex example of a path
		tc = TimeCollectorWithPath.createWithPath(
				new TestClock(),
				AllowedPathsGraph.
				<TestMilestones>
				nodes(
					nCreation,
					nBEFORE_HANDLER_CONTEXT, nBEFORE_SEARCH_HANDLER, nAFTER_SEARCH_HANDLER,
					nBEFORE_HANDLER,
					nBEFORE_DAO_GETSTATE,nBEFORE_DBPOOL_GETSTATE, nAFTER_DBPOOL_GETSTATE, 
					nBEFORE_DB_GETSTATE, nAFTER_DB_GETSTATE, nAFTER_DAO_GETSTATE,
					nBEFORE_CALC, nAFTER_CALC,
					nBEFORE_DECIDER, nAFTER_DECIDER,
					nBEFORE_DAO_SAVE,
					nBEFORE_DBPOOL_SAVE, nAFTER_DBPOOL_SAVE,
					nBEFORE_DB_SAVE_DESICION1, nAFTER_DB_SAVE_DESICION1,
					nBEFORE_DB_SAVE_DESICION2, nAFTER_DB_SAVE_DESICION2,
					nAFTER_DAO_SAVE, nAFTER_HANDLER, nAFTER_HANDLER_CONTEXT
				)
				.path(
						// first milestone in the life of each data object
						nCreation, 
						
							// depending on a request a handler is chosen in the code
							nBEFORE_HANDLER_CONTEXT,
							nBEFORE_SEARCH_HANDLER,
							nAFTER_SEARCH_HANDLER,
							
							// handler was found...the data object is given to the handler
							nBEFORE_HANDLER,
							    
								// some state is read from a database in a DataAccessObject
								nBEFORE_DAO_GETSTATE,
									// a db connection is retrieved from some connection pool
									nBEFORE_DBPOOL_GETSTATE,
									nAFTER_DBPOOL_GETSTATE,
									// db request
									nBEFORE_DB_GETSTATE,
									nAFTER_DB_GETSTATE,
								nAFTER_DAO_GETSTATE,

								// based on the data, something is calculated
								nBEFORE_CALC,
								nAFTER_CALC,
								
								// some decision is made
								nBEFORE_DECIDER,
								nAFTER_DECIDER,

								// depending on the decision different database requests could be done
								nBEFORE_DAO_SAVE, 

									// get a connection from the pool again
									nBEFORE_DBPOOL_SAVE, 
									nAFTER_DBPOOL_SAVE, 
									// save either 
									nBEFORE_DB_SAVE_DESICION1,
									nAFTER_DB_SAVE_DESICION1,

								nAFTER_DAO_SAVE,
							nAFTER_HANDLER,
						nAFTER_HANDLER_CONTEXT
				).path(
					// alternative path in the second DAO
					// another db request
					nAFTER_DBPOOL_SAVE, // getting the pool connection uses the same milestone
						/* but then we want to track this other request with own milestones
						 * to recognize which db request performs bad */
						nBEFORE_DB_SAVE_DESICION2,
						nAFTER_DB_SAVE_DESICION2,
					nAFTER_DAO_SAVE	
				)
				.path(
					/* if db request for saving descion1 leads to an db error
					 * we try again (jump back to reading in the new state from the db) */
					nBEFORE_DB_SAVE_DESICION1, nBEFORE_DAO_GETSTATE
				)					
				.build()
			);

	}
	
	@Test
	public void testFirstMileStone() {
		tc.saveTime(TestMilestones.CREATION);
		List<Path<GraphNode<TestMilestones, NodePermissions>, Instant>> recordedPaths =  tc.getRecordedPaths();
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
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
			tc.saveTime(TestMilestones.AFTER_DAO_SAVE);
		tc.saveTime(TestMilestones.AFTER_HANDLER);
		tc.saveTime(TestMilestones.AFTER_HANDLER_CONTEXT);

		checkRecPathLength(tc, 23);
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
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
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
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
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
			
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION1);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION1);
				
				// should not work as the above block and this next block are in different paths
				tc.saveTime(TestMilestones.BEFORE_DB_SAVE_DESICION2);
				tc.saveTime(TestMilestones.AFTER_DB_SAVE_DESICION2);
				
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
