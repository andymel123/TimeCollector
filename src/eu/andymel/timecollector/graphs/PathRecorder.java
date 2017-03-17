package eu.andymel.timecollector.graphs;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.util.RecursionSavetyCounter;

public class PathRecorder<MILESTONE_TYPE, PAYLOAD_TYPE>{

	private final static Logger LOG = LoggerFactory.getLogger(PathRecorder.class);
	
	/**This path models which milestones are allowed in whic order. The path
	 * that is taken at runtime can be different but has to be a "part" of this one
	 * at least if the {@link NodePermissions} are set so. */
	private final AllowedPathsGraph<MILESTONE_TYPE> allowedPath;
	
	private final List<Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>> possiblePaths;	
	private final List<Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>> immutableViewOnPossiblePaths;	

	private PathRecorder(AllowedPathsGraph<MILESTONE_TYPE> allowedPath) {
		this.allowedPath = allowedPath;
		this.possiblePaths = new LinkedList<>();
		this.immutableViewOnPossiblePaths = Collections.unmodifiableList(possiblePaths);
	}
	
	public static <MILESTONE_TYPE, PAYLOAD_TYPE> PathRecorder<MILESTONE_TYPE, PAYLOAD_TYPE> create(AllowedPathsGraph<MILESTONE_TYPE> allowedPath){
		return new PathRecorder<>(allowedPath);
	}
	
	/**
	 * Saves a payload at a milestone
	 * @param m the milestone to save the payload for
	 */
	public void savePayload(MILESTONE_TYPE m, PAYLOAD_TYPE p){
		
		if(possiblePaths.size()==0){
			// this is the first recording of a timestamp, see if it is the start of the allowed path
			
			GraphNode<MILESTONE_TYPE, NodePermissions> allowedStartNode = allowedPath.getStartNode();
			if(!m.equals(allowedStartNode.getId())){
				throw new MilestoneNotAllowedException("'"+m+"' is not allowed as first timestamp of this timeCollector! Allowed path: "+allowedPath);
			};
			
			// if the path is ok...
			possiblePaths.add(new Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>(allowedStartNode, p));
			
		}else{
			// we already have possible paths, this is not the start node
			
			List<Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>> newPossiblePaths = new LinkedList<>();
			GraphNode<MILESTONE_TYPE, NodePermissions> lastPermNode = null;
			
			// go through all former possible paths and see if it is allowed to extend them with the given milestone
			for(Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> possiblePath: possiblePaths){
				// the last node of the already built possiblePaths is the parent of my possible next node
				lastPermNode = possiblePath.getLastNode().getId();
				// the children could be the next possible nodes
				List<Edge<GraphNode<MILESTONE_TYPE, NodePermissions>>> edgesToNextAllowedNodes = lastPermNode.getEdgesToChildren();
				if(edgesToNextAllowedNodes.size()==0){
					// this path is over ... try with the next path if there is one
					continue;
				}
				// there are possible nodes in this possiblePath...iterate through and see if they fit
				for(Edge<GraphNode<MILESTONE_TYPE, NodePermissions>> edgeToPossibleNextNode: edgesToNextAllowedNodes){
					// this is one of the children...so one of the next possible nodes
					GraphNode<MILESTONE_TYPE, NodePermissions> possibleNextNode = edgeToPossibleNextNode.getChildNode();
					if(possibleNextNode.getId().equals(m)){
						// found a possible path with this milestone...copy to have a unique path
						Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> pathCopy = copyPath(possiblePath);
						pathCopy.addNode(possibleNextNode, p);
						newPossiblePaths.add(pathCopy);
					}
				}
			}
			
			if(lastPermNode==null){
				throw new IllegalStateException("The possible paths had no last node?!");
			}
			
			if(newPossiblePaths.size()==0){
				throw new MilestoneNotAllowedException("Found no way from '"+lastPermNode.getId()+"' to '"+m+"' that is (still) possible!");
			}
			
			possiblePaths.clear();
			possiblePaths.addAll(newPossiblePaths);
		}

		if(LOG.isDebugEnabled()){
			LOG.debug("PossiblePaths {}", Arrays.toString(possiblePaths.toArray()));	
		}
		
		
	}

	/**
	 * @return a Path with the Nodes of the given {@link AllowedPathsGraph} as ID and the recorded payload 
	 */
	public List<Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>> getRecordedPaths() {
		return immutableViewOnPossiblePaths;
	}

	
	private Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> copyPath(Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> pathToCopy) {
		
		GraphNode<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> nodeToCopy = pathToCopy.getStartNode();
		GraphNode<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> node = nodeToCopy.copy();
		Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> newPath = new Path<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>(node);
		
		int max = 1000;
		RecursionSavetyCounter savetyCounter = new RecursionSavetyCounter(max, "Seems like this path runs in circles. After going through "+max+" nodes I still found no end!");
		
		while(true){
			savetyCounter.inc();
			List<Edge<GraphNode<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>>> edges = nodeToCopy.getEdgesToChildren();
			if(edges.size()==0){
				// path end
				break;
			}else if(edges.size()>1){
				throw new IllegalStateException("A node in a "+getClass().getSimpleName()
					+" can only have 1 child! But "+nodeToCopy+" has "+edges.size()
					+" edges to children! "+Arrays.toString(edges.toArray()));
			}else{
				// exactly one child...make a copy and go on
				nodeToCopy = edges.get(0).getChildNode();
				GraphNode<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE> newNode = nodeToCopy.copy();
				Edge<GraphNode<GraphNode<MILESTONE_TYPE, NodePermissions>, PAYLOAD_TYPE>> e = Edge.create(node, newNode);
				node.addNextNode(e);
				newNode.addPrevNode(e);
				node = newNode;
			}
		}
		return newPath;
	}


	
	
}
