package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.graphs.NodePermissions.REQUIRED_AND_SINGLESET;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.andymel.timecollector.util.RecursionSavetyCounter;

public class AllowedPathsGraph<ID_TYPE> extends Graph<ID_TYPE, NodePermissions> {
	
	AllowedPathsGraph(ID_TYPE idOfStartNode, NodePermissions nodePermissionsOfStartNode, Mutable mutable) {
		super(
			idOfStartNode, 
			nodePermissionsOfStartNode, 
			true, // allow multiedges, circles,...
			mutable
		);
	}
	AllowedPathsGraph(PermissionNode<ID_TYPE> startNode, Mutable mutable) {
		super(
			startNode,
			true, // allow multiedges, circles,...
			mutable
		);
	}
	
//	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> start(ID_TYPE id){
//		return start(id, REQUIRED_AND_SINGLESET);
//	}
//	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> start(ID_TYPE id, NodePermissions nodePermissions){
//		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions);
//	}

//	// I removed the only difference for subpaths but I leave this separate factory methods in my API, maybe there will be differences again in the future
//	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> subpath(ID_TYPE id){
//		return subpath(id, REQUIRED_AND_SINGLESET);
//	}
//	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> subpath(ID_TYPE id, NodePermissions nodePermissions){
//		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions);
//	}

	
	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilderNodesAndEdges<ID_TYPE> nodes(PermissionNode<ID_TYPE> startNode, PermissionNode<ID_TYPE>... otherNodes){
		return new AllowedPathBuilderNodesAndEdges<ID_TYPE>(startNode, Arrays.asList(otherNodes));
	}
//	commented out: I have to know the nodes outside because I need the instances to build the edges with .path(...) 
//	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilderNodesAndEdges<ID_TYPE> nodes(ID_TYPE startMilestone, ID_TYPE... otherMilestones){
//		PermissionNode<ID_TYPE> startNode = PermissionNode.create(startMilestone, NodePermissions.REQUIRED_AND_SINGLESET);
//		List<PermissionNode<ID_TYPE>> otherNodes = new LinkedList<>();
//		for(ID_TYPE m: otherMilestones){
//			otherNodes.add(PermissionNode.create(m, NodePermissions.REQUIRED_AND_SINGLESET));
//		}
//		return new AllowedPathBuilderNodesAndEdges<ID_TYPE>(startNode, otherNodes);
//	}
	
	
	@Override
	public GraphNode<ID_TYPE, NodePermissions> getStartNode() {
		return super.getStartNode();
	}
	
	public List<Path<ID_TYPE, NodePermissions>> getAllPathsFromStartToAllNodesWithId(ID_TYPE id) {

		List<Path<ID_TYPE, NodePermissions>> allPaths = new LinkedList<>();
		
		List<GraphNode<ID_TYPE, NodePermissions>> allNodesWithId = getAllNodesWIthId(id);
		
		GraphNode<ID_TYPE, NodePermissions> startNode = getStartNode();

		List<List<GraphNode<ID_TYPE, NodePermissions>>> results = new LinkedList<>();

		for(GraphNode<ID_TYPE, NodePermissions> n: allNodesWithId){
			
			// start with the node we search
			List<GraphNode<ID_TYPE, NodePermissions>> baseList = new LinkedList<>();
			GraphNode<ID_TYPE, NodePermissions> copyOfNode = n.copy();
			baseList.add(copyOfNode);

			if(n==startNode){
				// do I need one path with just the startnode?
				results.add(baseList);
				continue;
			}
			
			// get all paths from startNode up until this node as lists of nodes (reversed from searched node to startnode)
			getAllReversedListsOfNodesToStartNode(baseList, copyOfNode, results);

		}
		
		// transform all those reversed lists to real paths
		for(List<GraphNode<ID_TYPE, NodePermissions>> revList: results){
			allPaths.add(createPathFromListOfReversedNodes(revList));
		}
		
		return allPaths;
	}

	
	boolean checkForCircularConnections(){
		
		forEachRootOfACircle((rootOfCircle) -> {
			/* 
			 * 1.) get all paths to itself
			 * 2.) heck paths for edge permissions that ensure termination
			 */
		});
		
		return false;
	}

	
	private void getAllReversedListsOfNodesToStartNode(List<GraphNode<ID_TYPE, NodePermissions>> baseReversedList, GraphNode<ID_TYPE, NodePermissions> currentFirstNode, List<List<GraphNode<ID_TYPE, NodePermissions>>> results) {
		
		nn(currentFirstNode, "'currentFirstNode' is null!");
		
		/* as long as there is just one paret node I can iterate instead of something
		 * recursive to prevent stack overflow in graphs that are very deep */
		List<Edge<GraphNode<ID_TYPE, NodePermissions>>> parents = currentFirstNode.getEdgesToParents();
		if(parents==null){
			/* if the currentFirstNode is the startNode we should have handled that earlier
			 * all other nodes need to have parents */
			throw new IllegalStateException("No parents for node with id '"+currentFirstNode.getId()+"'");
		}
		
		while(parents.size()==1){
			GraphNode<ID_TYPE, NodePermissions> singleParent = parents.get(0).getParentNode();
			baseReversedList.add(singleParent.copy());
			
			if(singleParent == getStartNode()){
				// path finished
				results.add(baseReversedList);
				return;
			} else {
				parents = singleParent.getEdgesToParents();
			}
		}

		// if mutliple parents...use recursive approach
		for(Edge<GraphNode<ID_TYPE, NodePermissions>> edgeToParent: parents){
			GraphNode<ID_TYPE, NodePermissions> parent = edgeToParent.getParentNode();
			List<GraphNode<ID_TYPE, NodePermissions>> copyOfReversedList = copyNodeList(baseReversedList);
			copyOfReversedList.add(parent.copy());
			getAllReversedListsOfNodesToStartNode(copyOfReversedList, parent, results);
		}
		
	}

	private List<GraphNode<ID_TYPE, NodePermissions>> copyNodeList(List<GraphNode<ID_TYPE, NodePermissions>> baseReversedList) {
		List<GraphNode<ID_TYPE, NodePermissions>> copy = new LinkedList<>();
		for(GraphNode<ID_TYPE, NodePermissions> n: baseReversedList){
			copy.add(n.copy());
		}
		return copy;
	}

	private Path<ID_TYPE, NodePermissions> createPathFromListOfReversedNodes(List<GraphNode<ID_TYPE, NodePermissions>> reverseListOfNodes) {
		throw new RuntimeException("Not yet implemeted!");
//		return null;
	}

	
	
	


	
}
