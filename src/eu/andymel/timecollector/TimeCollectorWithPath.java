package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.path.AllowedPathsGraph;
import eu.andymel.timecollector.path.GraphNode;
import eu.andymel.timecollector.path.NodePermissions;
import eu.andymel.timecollector.path.Path;

public class TimeCollectorWithPath<MILESTONE_TYPE> implements TimeCollector<MILESTONE_TYPE> {

	/** The clock to use to save time stamps */
	private final Clock clock;
	
	/**This path models which milestones are allowed in whic order. The path
	 * that is taken at runtime can be different but has to be a "part" of this one
	 * at least if the {@link NodePermissions} are set so. */
	private final AllowedPathsGraph<MILESTONE_TYPE> allowedPath;
	
	/** This saves the real path that this time collector went through */
	private Path<MILESTONE_TYPE, Instant> recordedPath;
	
//	private GraphNode<MILESTONE_TYPE, Instant> lastRecordedNode;
	
	private List<GraphNode<MILESTONE_TYPE, NodePermissions>> possiblePermissionNodesOfLastRecordedTimeStamp;
	
	private TimeCollectorWithPath(AllowedPathsGraph<MILESTONE_TYPE> allowedPath){
		this(Clock.systemDefaultZone(), allowedPath);
	}
	
	private TimeCollectorWithPath(Clock clock, AllowedPathsGraph<MILESTONE_TYPE> allowed) {
		nn(clock, "'clock' my not be null!");
		nn(allowed, "'path' my not be null!");
		
		this.clock = clock;
		this.possiblePermissionNodesOfLastRecordedTimeStamp = new LinkedList<>();
		
		// TODO copy path!
		this.allowedPath = allowed;
	}

	
//	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollector<MILESTONE_TYPE> createWithPath(Path<MILESTONE_TYPE, NodePermissions> path){
	public static <MILESTONE_TYPE> TimeCollector<MILESTONE_TYPE> createWithPath(AllowedPathsGraph<MILESTONE_TYPE> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(path);
	}
	
	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public void saveTime(MILESTONE_TYPE m){

		// save current time for this milestone
		Instant now = clock.instant();

		if(recordedPath==null){
			// this is the first recording of a timestamp, see if it is the start of the allowed path
			
			GraphNode<MILESTONE_TYPE, NodePermissions> allowedStartNode = allowedPath.getStartNode();
			if(!m.equals(allowedStartNode.getId())){
				throw new MilestoneNotAllowedException("'"+m+"' is not allowed as first timestamp of this timeCollector! Allowed path: "+allowedPath);
			};

			if(possiblePermissionNodesOfLastRecordedTimeStamp.size()>0){
				throw new IllegalStateException("This is the first timestamp to save but I already "
					+ "have saved "+possiblePermissionNodesOfLastRecordedTimeStamp.size()+" possible permission nodes!");
			}
			possiblePermissionNodesOfLastRecordedTimeStamp.add(allowedStartNode); 
			
			// if the path is ok...
			recordedPath = new Path<>(m, now);
			
		}else{
			if(possiblePermissionNodesOfLastRecordedTimeStamp.isEmpty()){
				throw new IllegalStateException("This is not the first timestamp to save but I don't "
					+ "have saved any possible permission nodes yet!");
			}
			List<GraphNode<MILESTONE_TYPE, NodePermissions>> newPermissionNodes = getNextPermissionNodes(m);
			if(newPermissionNodes == null || newPermissionNodes.size()==0){
				String lastSetMilestone = null;
				try{
					lastSetMilestone = possiblePermissionNodesOfLastRecordedTimeStamp.stream().findFirst().get().getId().toString();
				}catch(Exception e){
					lastSetMilestone = ">ErrorWhileRetrievingMilestone!<";
				}
				throw new MilestoneNotAllowedException("There is no next milestone in the allowed path of "
						+ "this TimeCollector with the id '"+m+"'. The previosuly set Milestone was '"+lastSetMilestone+"'");
			}else{
				/* clear just to recognize if I mistakenly have a reference on 
				 * this list somewhere else and to prevent MemoryLeaks in that case */
				possiblePermissionNodesOfLastRecordedTimeStamp.clear(); 
				possiblePermissionNodesOfLastRecordedTimeStamp = newPermissionNodes;
				recordedPath.addNode(m, now);
			}
			
		}
		
	}


	private List<GraphNode<MILESTONE_TYPE, NodePermissions>> getNextPermissionNodes(MILESTONE_TYPE m) {
		
		List<GraphNode<MILESTONE_TYPE, NodePermissions>> newPossiblePermissionNodes = new LinkedList<>();
		int maxRecursions = 100;
		RecursionSavetyCounter savetyCounter = new RecursionSavetyCounter(maxRecursions, "I tryed to check if saveTime on your milestone '"+m+"' is allowed but I killed the search for allowed nodes in your path after not finding all possible next nodes in "+maxRecursions+" recursions!");
		
		for(GraphNode<MILESTONE_TYPE, NodePermissions> possiblePermissionNode : possiblePermissionNodesOfLastRecordedTimeStamp){
			collectNextPermissionNodes(m, possiblePermissionNode, newPossiblePermissionNodes, savetyCounter);
		}
		
		return newPossiblePermissionNodes;
	}

	private void collectNextPermissionNodes(MILESTONE_TYPE m, GraphNode<MILESTONE_TYPE,NodePermissions> root, List<GraphNode<MILESTONE_TYPE, NodePermissions>> result, RecursionSavetyCounter recursionSavetyCounter) {
		
		recursionSavetyCounter.inc();
		
		/* TODO Attention with recursion through cyclic path of not required nodes! */
		
		List<GraphNode<MILESTONE_TYPE, NodePermissions>> childrenOfPossiblePermissionNode = root.getNextNodes();
		
		if(childrenOfPossiblePermissionNode==null || childrenOfPossiblePermissionNode.isEmpty()){
			return;
		}
		
		List<GraphNode<MILESTONE_TYPE, NodePermissions>> notRequiredChildren = new LinkedList<>();
		for(GraphNode<MILESTONE_TYPE, NodePermissions> child: childrenOfPossiblePermissionNode){
			if(child.getId().equals(m)){
				result.add(child);
			}else{
				if(!child.getPayload().isRequired()){
					notRequiredChildren.add(child);
				}
			}
		}

		for(GraphNode<MILESTONE_TYPE, NodePermissions> notRequiredChild: notRequiredChildren){
			collectNextPermissionNodes(m, notRequiredChild, result, recursionSavetyCounter);
		}
		
	}

	
	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#getTime(MILESTONE_TYPE)
	 */
	@Override
	public Instant getTime(MILESTONE_TYPE m){
		return this.recordedPath.getPayload(m);
	}

	private static class RecursionSavetyCounter{
		private int max;
		private int count = 0;
		private String errorMsg;
		
		private RecursionSavetyCounter(int max, String errorMsg) {
			this.max = max;
			this.errorMsg = errorMsg;
		}
		
		private void inc(){
			if(++count > max){
				throw new RuntimeException(errorMsg);
			};
		}
	}
	
}
