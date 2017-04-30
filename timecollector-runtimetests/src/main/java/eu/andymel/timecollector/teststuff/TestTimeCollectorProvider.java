package eu.andymel.timecollector.teststuff;

import java.time.Clock;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.PermissionNode;

public class TestTimeCollectorProvider {

	public enum TestMilestones{

		CREATION,
		HANDLER_CTX_S,
		SEARCH_HANDLER_S,
		SEARCH_HANDLER_E,
		HANDLER_S,
		
		DAO_GET_S,
		DBPOOL_S,
		DBPOOL_E,
		DB_GET_S,
		DB_GET_E,
		DAO_GET_E,
		
		CALC1_S,
		CALC1_E,
		
		DECIDER_S,
		DECIDER_E,
		
		DAO_SAVE_S,
		DB_SAVE1_S,
		DB_SAVE1_E,
		DAO_SAVE_E,

		HANDLER_E,
		HANDLER_CTX_E,

		DB_SAVE2_S,
		DB_SAVE2_E,
		RETRY
		
	}
	
	private final static PermissionNode<TestMilestones> nCreation 					= PermissionNode.create(TestMilestones.CREATION);
	
	private final static PermissionNode<TestMilestones> nBEFORE_HANDLER_CONTEXT 	= PermissionNode.create(TestMilestones.HANDLER_CTX_S);
	private final static PermissionNode<TestMilestones> nBEFORE_SEARCH_HANDLER 		= PermissionNode.create(TestMilestones.SEARCH_HANDLER_S);
	private final static PermissionNode<TestMilestones> nAFTER_SEARCH_HANDLER 		= PermissionNode.create(TestMilestones.SEARCH_HANDLER_E);
     
	private final static PermissionNode<TestMilestones> nBEFORE_HANDLER 			= PermissionNode.create(TestMilestones.HANDLER_S);
	// 5 	
	private final static PermissionNode<TestMilestones> nBEFORE_DAO_GETSTATE 		= PermissionNode.create(TestMilestones.DAO_GET_S);
	private final static PermissionNode<TestMilestones> nBEFORE_DBPOOL_GETSTATE		= PermissionNode.create(TestMilestones.DBPOOL_S);
	private final static PermissionNode<TestMilestones> nAFTER_DBPOOL_GETSTATE		= PermissionNode.create(TestMilestones.DBPOOL_E);
	private final static PermissionNode<TestMilestones> nBEFORE_DB_GETSTATE 		= PermissionNode.create(TestMilestones.DB_GET_S);
	private final static PermissionNode<TestMilestones> nAFTER_DB_GETSTATE 			= PermissionNode.create(TestMilestones.DB_GET_E);
	private final static PermissionNode<TestMilestones> nAFTER_DAO_GETSTATE 		= PermissionNode.create(TestMilestones.DAO_GET_E);
	//11
	private final static PermissionNode<TestMilestones> nBEFORE_CALC 				= PermissionNode.create(TestMilestones.CALC1_S);
	private final static PermissionNode<TestMilestones> nAFTER_CALC 				= PermissionNode.create(TestMilestones.CALC1_E);
	 
	private final static PermissionNode<TestMilestones> nBEFORE_DECIDER 			= PermissionNode.create(TestMilestones.DECIDER_S);
	private final static PermissionNode<TestMilestones> nAFTER_DECIDER 				= PermissionNode.create(TestMilestones.DECIDER_E);
	//15 
	private final static PermissionNode<TestMilestones> nBEFORE_DAO_SAVE 			= PermissionNode.create(TestMilestones.DAO_SAVE_S);
	private final static PermissionNode<TestMilestones> nBEFORE_DBPOOL_SAVE 		= PermissionNode.create(TestMilestones.DBPOOL_S);
	private final static PermissionNode<TestMilestones> nAFTER_DBPOOL_SAVE 			= PermissionNode.create(TestMilestones.DBPOOL_E);
	private final static PermissionNode<TestMilestones> nBEFORE_DB_SAVE_DESICION1 	= PermissionNode.create(TestMilestones.DB_SAVE1_S);
	private final static PermissionNode<TestMilestones> nRETRY 						= PermissionNode.create(TestMilestones.RETRY);
	private final static PermissionNode<TestMilestones> nAFTER_DB_SAVE_DESICION1 	= PermissionNode.create(TestMilestones.DB_SAVE1_E);
	private final static PermissionNode<TestMilestones> nBEFORE_DB_SAVE_DESICION2 	= PermissionNode.create(TestMilestones.DB_SAVE2_S);
	private final static PermissionNode<TestMilestones> nAFTER_DB_SAVE_DESICION2 	= PermissionNode.create(TestMilestones.DB_SAVE2_E);
	// 23
	private final static PermissionNode<TestMilestones> nAFTER_DAO_SAVE 			= PermissionNode.create(TestMilestones.DAO_SAVE_E);
	private final static PermissionNode<TestMilestones> nAFTER_HANDLER 				= PermissionNode.create(TestMilestones.HANDLER_E);
	private final static PermissionNode<TestMilestones> nAFTER_HANDLER_CONTEXT		= PermissionNode.create(TestMilestones.HANDLER_CTX_E);

	
	private static AllowedPathsGraph<TestMilestones> allowedGraph = AllowedPathsGraph.
			<TestMilestones>
			nodes(
				nCreation,
				nBEFORE_HANDLER_CONTEXT, nBEFORE_SEARCH_HANDLER, nAFTER_SEARCH_HANDLER,
				nBEFORE_HANDLER,	// 5
				nBEFORE_DAO_GETSTATE,nBEFORE_DBPOOL_GETSTATE, nAFTER_DBPOOL_GETSTATE, // 8 
				nBEFORE_DB_GETSTATE, nAFTER_DB_GETSTATE, nAFTER_DAO_GETSTATE,	// 11
				nBEFORE_CALC, nAFTER_CALC,	// 13
				nBEFORE_DECIDER, nAFTER_DECIDER,	// 15
				nBEFORE_DAO_SAVE,
				nBEFORE_DBPOOL_SAVE, nAFTER_DBPOOL_SAVE,	// 18
				nBEFORE_DB_SAVE_DESICION1, 
				nRETRY, 
				nAFTER_DB_SAVE_DESICION1,	// 21
				nBEFORE_DB_SAVE_DESICION2, nAFTER_DB_SAVE_DESICION2,	// 23
				nAFTER_DAO_SAVE, nAFTER_HANDLER, nAFTER_HANDLER_CONTEXT	//26
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
			)
			.path(
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
				nBEFORE_DB_SAVE_DESICION1, 
				nRETRY, 
				nAFTER_DB_SAVE_DESICION1, 
				nAFTER_DAO_SAVE
				, nBEFORE_DAO_GETSTATE
			)					
			.build();
		
	
		
	public static AllowedPathsGraph<TestMilestones> getAllowedGraph() {
		return allowedGraph;
	}
	
	
	public static TimeCollectorWithPath<TestMilestones> getTC(Clock clock){

		return TimeCollectorWithPath.createWithPath(clock, allowedGraph);

	}
	
}
