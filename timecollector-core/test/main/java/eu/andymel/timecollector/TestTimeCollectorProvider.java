package eu.andymel.timecollector;

import java.time.Clock;

import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.PermissionNode;

public class TestTimeCollectorProvider {

	public enum TestMilestones{

		CREATION,
		BEFORE_HANDLER_CONTEXT,
		BEFORE_SEARCH_HANDLER,
		AFTER_SEARCH_HANDLER,
		BEFORE_HANDLER,
		
		BEFORE_DAO_GETSTATE,
		BEFORE_DB_GETSTATE,
		AFTER_DB_GETSTATE,
		AFTER_DAO_GETSTATE,
		
		BEFORE_CALC1,
		AFTER_CALC1,
		
		BEFORE_DECIDER,
		AFTER_DECIDER,
		
		BEFORE_DAO_SAVE,
		BEFORE_DB_SAVE_DECISION1,
		AFTER_DB_SAVE_DECISION1,
		BEFORE_DB_SAVE_DECISION2,
		AFTER_DB_SAVE_DECISION2,
		AFTER_DAO_SAVE,

		AFTER_HANDLER,
		AFTER_HANDLER_CONTEXT,

		
		BEFORE_DBPOOL,
		AFTER_DBPOOL,
		RETRY

		
	}
	
	private final static PermissionNode<TestMilestones> nCreation 					= PermissionNode.create(TestMilestones.CREATION);
	
	private final static PermissionNode<TestMilestones> nBEFORE_HANDLER_CONTEXT 	= PermissionNode.create(TestMilestones.BEFORE_HANDLER_CONTEXT);
	private final static PermissionNode<TestMilestones> nBEFORE_SEARCH_HANDLER 		= PermissionNode.create(TestMilestones.BEFORE_SEARCH_HANDLER);
	private final static PermissionNode<TestMilestones> nAFTER_SEARCH_HANDLER 		= PermissionNode.create(TestMilestones.AFTER_SEARCH_HANDLER);
     
	private final static PermissionNode<TestMilestones> nBEFORE_HANDLER 			= PermissionNode.create(TestMilestones.BEFORE_HANDLER);
	 	
	private final static PermissionNode<TestMilestones> nBEFORE_DAO_GETSTATE 		= PermissionNode.create(TestMilestones.BEFORE_DAO_GETSTATE);
	private final static PermissionNode<TestMilestones> nBEFORE_DBPOOL_GETSTATE		= PermissionNode.create(TestMilestones.BEFORE_DBPOOL);
	private final static PermissionNode<TestMilestones> nAFTER_DBPOOL_GETSTATE		= PermissionNode.create(TestMilestones.AFTER_DBPOOL);
	private final static PermissionNode<TestMilestones> nBEFORE_DB_GETSTATE 		= PermissionNode.create(TestMilestones.BEFORE_DB_GETSTATE);
	private final static PermissionNode<TestMilestones> nAFTER_DB_GETSTATE 			= PermissionNode.create(TestMilestones.AFTER_DB_GETSTATE);
	private final static PermissionNode<TestMilestones> nAFTER_DAO_GETSTATE 		= PermissionNode.create(TestMilestones.AFTER_DAO_GETSTATE);
	 
	private final static PermissionNode<TestMilestones> nBEFORE_CALC 				= PermissionNode.create(TestMilestones.BEFORE_CALC1);
	private final static PermissionNode<TestMilestones> nAFTER_CALC 				= PermissionNode.create(TestMilestones.AFTER_CALC1);
	 
	private final static PermissionNode<TestMilestones> nBEFORE_DECIDER 			= PermissionNode.create(TestMilestones.BEFORE_DECIDER);
	private final static PermissionNode<TestMilestones> nAFTER_DECIDER 				= PermissionNode.create(TestMilestones.AFTER_DECIDER);
	 
	private final static PermissionNode<TestMilestones> nBEFORE_DAO_SAVE 			= PermissionNode.create(TestMilestones.BEFORE_DAO_SAVE);
	private final static PermissionNode<TestMilestones> nBEFORE_DBPOOL_SAVE 		= PermissionNode.create(TestMilestones.BEFORE_DBPOOL);
	private final static PermissionNode<TestMilestones> nAFTER_DBPOOL_SAVE 			= PermissionNode.create(TestMilestones.AFTER_DBPOOL);
	private final static PermissionNode<TestMilestones> nBEFORE_DB_SAVE_DESICION1 	= PermissionNode.create(TestMilestones.BEFORE_DB_SAVE_DECISION1);
	private final static PermissionNode<TestMilestones> nRETRY 						= PermissionNode.create(TestMilestones.RETRY);
	private final static PermissionNode<TestMilestones> nAFTER_DB_SAVE_DESICION1 	= PermissionNode.create(TestMilestones.AFTER_DB_SAVE_DECISION1);
	private final static PermissionNode<TestMilestones> nBEFORE_DB_SAVE_DESICION2 	= PermissionNode.create(TestMilestones.BEFORE_DB_SAVE_DECISION2);
	private final static PermissionNode<TestMilestones> nAFTER_DB_SAVE_DESICION2 	= PermissionNode.create(TestMilestones.AFTER_DB_SAVE_DECISION2);
	 	
	private final static PermissionNode<TestMilestones> nAFTER_DAO_SAVE 			= PermissionNode.create(TestMilestones.AFTER_DAO_SAVE);
	private final static PermissionNode<TestMilestones> nAFTER_HANDLER 				= PermissionNode.create(TestMilestones.AFTER_HANDLER);
	private final static PermissionNode<TestMilestones> nAFTER_HANDLER_CONTEXT		= PermissionNode.create(TestMilestones.AFTER_HANDLER_CONTEXT);

	
	private static AllowedPathsGraph<TestMilestones> allowedGraph = AllowedPathsGraph.
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
				nBEFORE_DB_SAVE_DESICION1, nRETRY, nAFTER_DB_SAVE_DESICION1,
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
				nBEFORE_DB_SAVE_DESICION1, nRETRY, nAFTER_DB_SAVE_DESICION1, nAFTER_DAO_SAVE, nBEFORE_DAO_GETSTATE
			)					
			.build();
		
	
		

	
	
	public static TimeCollectorWithPath<TestMilestones> getTC(Clock clock){

		return TimeCollectorWithPath.createWithPath(clock, allowedGraph);

	}
	
}
