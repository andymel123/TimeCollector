package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	
	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilderNodesAndEdges<ID_TYPE> nodes(PermissionNode<ID_TYPE> startNode, PermissionNode<ID_TYPE>... otherNodes){
		return new AllowedPathBuilderNodesAndEdges<ID_TYPE>(startNode, Arrays.asList(otherNodes));
	}
	
	
	@Override
	public GraphNode<ID_TYPE, NodePermissions> getStartNode() {
		return super.getStartNode();
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
	
}
