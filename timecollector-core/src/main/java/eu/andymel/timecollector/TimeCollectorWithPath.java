package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.Edge;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public class TimeCollectorWithPath<MILESTONE_TYPE> implements TimeCollector<MILESTONE_TYPE> {

	public static enum ONPATHLOSE{
		EXCEPTION,
		LISTENER,
		ASSERT
	}
	
	private static final boolean ASSERT_CORRECTNESS_AT_EACH_SAVETIME = false;

	private static final int DEFAULT_INITIAL_CAPACITY = 30;
	
	/** The clock to use to save time stamps */
	private final Clock clock;
	
	private LinkedList<LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>>> possibleListsOfWalkedAllowedGraphNodes;	// outer LinkedList as I want to remove from in between fast, inner as I want to call getLast()
	private ArrayList<Instant> recordedTimes;				// ArrayList as I use get(i) in getRecordedPaths()
	private AllowedPathsGraph<MILESTONE_TYPE> allowedGraph;
	
	private int recordedMilestonesCount;
	
	private TimeCollectorWithPath(Clock clock, AllowedPathsGraph<MILESTONE_TYPE> allowed, int initialCapacity) {
		nn(clock, "'clock' my not be null!");
		nn(allowed, "'path' my not be null!");
		this.clock = clock;
		this.recordedTimes = new ArrayList<>(initialCapacity);			
		this.possibleListsOfWalkedAllowedGraphNodes = new LinkedList<>();	
		this.allowedGraph = allowed;
		this.recordedMilestonesCount = 0;
	}

	public AllowedPathsGraph<MILESTONE_TYPE> getAllowedGraph() {
		if(allowedGraph.getMutable().isMutable()){
			throw new IllegalStateException("An allowedGraph that is "
			+ "retrieved from a timecollector should not be mutable anymore!");
		}
		return allowedGraph;
	}
	
	public static <MILESTONE_TYPE> TimeCollectorWithPath<MILESTONE_TYPE> createWithPath(AllowedPathsGraph<MILESTONE_TYPE> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(Clock.systemDefaultZone(), path, DEFAULT_INITIAL_CAPACITY);
	}
	public static <MILESTONE_TYPE> TimeCollectorWithPath<MILESTONE_TYPE> createWithPath(Clock clock, AllowedPathsGraph<MILESTONE_TYPE> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(clock, path, DEFAULT_INITIAL_CAPACITY);
	}
	
	
	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public void saveTime(MILESTONE_TYPE m){

		checkPossible(m);
		
		recordedTimes.add(clock.instant());

		if(ASSERT_CORRECTNESS_AT_EACH_SAVETIME)assertions(m);
		
	}

	private void assertions(MILESTONE_TYPE m){
		
		recordedMilestonesCount++;
		
		// just some fast checks to find problems as early as possible
		if( recordedTimes.size() != recordedMilestonesCount){
			throw new IllegalStateException("Milestones recorded: "+recordedMilestonesCount+
					" but just "+recordedTimes.size()+" times recorded! You wanted to save '"+m+"'");
		}

		for(LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>> l:possibleListsOfWalkedAllowedGraphNodes){
			if(l.size() != recordedMilestonesCount){
				throw new IllegalStateException("Milestones recorded: "+recordedMilestonesCount+
					" but one of the possiblePaths has just a size of "+l.size()+"! You wanted to save '"+m+"'. "
					+ "The recorded path: "+getGraphNodeListAsString(l));
			}
		}
	}

	private void checkPossible(MILESTONE_TYPE newMilestone) {
		
		if(recordedTimes.size()==0){
			// shortcut this is the first milestone
			if(!newMilestone.equals(allowedGraph.getStartNode().getId())){
				throw newMilestoneNotAllowedException(newMilestone);
			}
			LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>> l = new LinkedList<>();
			l.add(allowedGraph.getStartNode());
			possibleListsOfWalkedAllowedGraphNodes.add(l);
			return;
		}

		
		List<LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>>> newPossiblePaths = null;

		boolean hasFoundPossiblePath = false;
		
		// iterate through all the former saved possible paths
		Iterator<LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>>> it = possibleListsOfWalkedAllowedGraphNodes.iterator();
		while(it.hasNext()){

			LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>> possiblePath = it.next();
			GraphNode<MILESTONE_TYPE, NodePermissions> lastNode = possiblePath.getLast();
			List<Edge<GraphNode<MILESTONE_TYPE, NodePermissions>>> edges = lastNode.getEdgesToChildren();
			LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>> possibleChildren = null;
			
			// iterate through all children of the last node of this former recorded path
			for(Edge<GraphNode<MILESTONE_TYPE, NodePermissions>> edge:edges){
				GraphNode<MILESTONE_TYPE, NodePermissions> childNode = edge.getChildNode();
				if(newMilestone.equals(childNode.getId())){
					// remember all children that are possible
					if(possibleChildren==null){
						possibleChildren = new LinkedList<>();
					}
					possibleChildren.add(childNode);
				} 
			}
			
			if(possibleChildren==null){
				// if no children for this path were found...delete the path
				it.remove();
			} else if(possibleChildren.size()==1){
				// if exactly one child for this path was found...simply add it to that path
				hasFoundPossiblePath = true;
				possiblePath.add(possibleChildren.getFirst());	
			} else{
				// if mutliple children are possible to go, copy the path for each child
				for(GraphNode<MILESTONE_TYPE, NodePermissions> childNode: possibleChildren){
					LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>> newPossiblePath = new LinkedList<>(possiblePath);
					newPossiblePath.add(childNode);
					if(newPossiblePaths==null){
						newPossiblePaths = new LinkedList<>();
					}
					newPossiblePaths.add(newPossiblePath);
				}
				hasFoundPossiblePath = true;
			}

		}
		
		if(!hasFoundPossiblePath){
			throw newMilestoneNotAllowedException(newMilestone);
		}
		
		if(newPossiblePaths!=null && newPossiblePaths.size()>0){
			possibleListsOfWalkedAllowedGraphNodes.addAll(newPossiblePaths);
		}
		
	}

	
	private MilestoneNotAllowedException newMilestoneNotAllowedException(MILESTONE_TYPE newMilestone) {
		
		String mileStones = null;
		if(possibleListsOfWalkedAllowedGraphNodes.size()>0){
			mileStones = getMileStoneListAsString(possibleListsOfWalkedAllowedGraphNodes.getFirst());
		}
		
		return new MilestoneNotAllowedException(
			"Milestone '"+newMilestone+"' is not allowed now! "
			+ "Former milestones were: ["+ mileStones +"]");
		
	}

	private String getMileStoneListAsString(List<GraphNode<MILESTONE_TYPE, NodePermissions>> list) {
		if(list==null || list.size()==0)return "";
		
		return list.stream()
				.map(m->m.getId().toString())
				.collect(Collectors.joining(" -> "));
	}
	
	private String getGraphNodeListAsString(List<GraphNode<MILESTONE_TYPE, NodePermissions>> list) {
		return list.stream()
				.map(n->n.getId().toString())
				.collect(Collectors.joining(" -> "));
	}


	/**
	 * @return all possible paths (if there are multiple paths in the allowedGrap that fit)
	 */
	public List<List<SimpleEntry<GraphNode<MILESTONE_TYPE, NodePermissions>, Instant>>> getRecordedPaths() {

		List<List<SimpleEntry<GraphNode<MILESTONE_TYPE, NodePermissions>, Instant>>> result = new LinkedList<>();
		
		for(LinkedList<GraphNode<MILESTONE_TYPE, NodePermissions>> possiblePath: possibleListsOfWalkedAllowedGraphNodes){
			List<SimpleEntry<GraphNode<MILESTONE_TYPE, NodePermissions>, Instant>> recPath = new ArrayList<>(possiblePath.size()); // Arraylist as nobody will remove or add entries in between but maybe jump to index?!
			int count=0;
			for(GraphNode<MILESTONE_TYPE, NodePermissions> allowedNode: possiblePath){
				recPath.add(
					new SimpleEntry<GraphNode<MILESTONE_TYPE, NodePermissions>, Instant>(allowedNode, recordedTimes.get(count++))
				);
			}
			result.add(Collections.unmodifiableList(recPath));
		}
		
		
		return Collections.unmodifiableList(result);
	
	}
	
}
