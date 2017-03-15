package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.graphs.NodePermissions.REQUIRED_AND_SINGLESET;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Arrays;
import java.util.HashSet;
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
	
	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> start(ID_TYPE id){
		return start(id, REQUIRED_AND_SINGLESET);
	}
	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> start(ID_TYPE id, NodePermissions nodePermissions){
		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions, false);
	}
	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> subpath(ID_TYPE id){
		return subpath(id, REQUIRED_AND_SINGLESET);
	}
	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> subpath(ID_TYPE id, NodePermissions nodePermissions){
		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions, true);
	}

	
	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilderNodesAndEdges<ID_TYPE> nodes(PermissionNode<ID_TYPE> startNode, PermissionNode<ID_TYPE>... otherNodes){
		return new AllowedPathBuilderNodesAndEdges<ID_TYPE>(startNode, otherNodes);
	}
	
	
	
	
//	/**
//	 * Creats a serial graph from an enum, first to last enum entry
//	 * 
//	 * @param enumClazz the enum to create the path from
//	 * @param required if true the payload of a node has to be set before the payload of the next node can be set
//	 * @param singleSet if true the payload of a node can only be set once, the next time throws an exception
//	 * 
//	 * @return the serial {@link AllowedPathsGraph}
//	 */
//	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> AllowedPathsGraph<MILESTONE_TYPE> createSerial(Class<MILESTONE_TYPE> enumClazz, boolean required, boolean singleSet) {
//		nn(enumClazz, "You need to provide an enum to get the serial path from!");
//		
//		EnumSet<MILESTONE_TYPE> milestones = EnumSet.allOf(enumClazz);
//		Iterator<MILESTONE_TYPE> it = milestones.iterator();
//		if(!it.hasNext()){
//			throw new IllegalArgumentException("It makes no sense to build a TimeCollector with a serial path from an Enum thats empty!");
//		}
//		
//		NodePermissions permissions = NodePermissions.create(required, singleSet);
//		
//		MILESTONE_TYPE firstMilestone = it.next();
//		AllowedPathBuilder<MILESTONE_TYPE> pathBuilder = AllowedPathBuilder.start(firstMilestone, permissions);
//		
//		while(it.hasNext()){
//			pathBuilder.then(it.next(), permissions);
//		}
//		
//		return pathBuilder.build();
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
			GraphNode<ID_TYPE, NodePermissions> copyOfNode = copyNode(n);
			baseList.add(copyOfNode);

			// get all paths from startNode up until this node as lists of nodes (reversed from searched node to startnode)
			getAllReversedListsOfNodesToStartNode(baseList, copyOfNode, results);

		}
		
		// transform all those reversed lists to real paths
		for(List<GraphNode<ID_TYPE, NodePermissions>> revList: results){
			allPaths.add(createPathFromListOfReversedNodes(revList));
		}
		
		return allPaths;
	}

	
	
	private void getAllReversedListsOfNodesToStartNode(List<GraphNode<ID_TYPE, NodePermissions>> baseReversedList, GraphNode<ID_TYPE, NodePermissions> currentFirstNode, List<List<GraphNode<ID_TYPE, NodePermissions>>> results) {
		
		nn(currentFirstNode, "'currentFirstNode' is null!");
		
		GraphNode<ID_TYPE, NodePermissions> allowedGraphsStartNode = getStartNode();
		if(currentFirstNode==allowedGraphsStartNode){
			// do I need one path with just the startnode?
			return;
		}
		
		/* as long as there is just one paret node I can iterate instead of something
		 * recursive to prevent stack overflow in graphs that are very deep */
		List<GraphNode<ID_TYPE, NodePermissions>> parents = currentFirstNode.getPrevNodes();
		if(parents==null){
			throw new IllegalStateException("No parents for node with id '"+currentFirstNode.getId()+"'");
		}
		
		while(parents.size()==1){
			GraphNode<ID_TYPE, NodePermissions> singleParent = parents.get(0);
			baseReversedList.add(copyNode(singleParent));
			
			if(singleParent == getStartNode()){
				// path finished
				results.add(baseReversedList);
				return;
			} else {
				parents = singleParent.getPrevNodes();
			}
		}

		// if mutliple parents...use recursive approach
		for(GraphNode<ID_TYPE, NodePermissions> parent: parents){
			List<GraphNode<ID_TYPE, NodePermissions>> copyOfReversedList = copyNodeList(baseReversedList);
			copyOfReversedList.add(copyNode(parent));
			getAllReversedListsOfNodesToStartNode(copyOfReversedList, parent, results);
		}
		
	}

	private List<GraphNode<ID_TYPE, NodePermissions>> copyNodeList(List<GraphNode<ID_TYPE, NodePermissions>> baseReversedList) {
		List<GraphNode<ID_TYPE, NodePermissions>> copy = new LinkedList<>();
		for(GraphNode<ID_TYPE, NodePermissions> n: baseReversedList){
			copy.add(copyNode(n));
		}
		return copy;
	}

	private Path<ID_TYPE, NodePermissions> createPathFromListOfReversedNodes(List<GraphNode<ID_TYPE, NodePermissions>> reverseListOfNodes) {
		throw new RuntimeException("Not yet implemeted!");
//		return null;
	}

	private GraphNode<ID_TYPE, NodePermissions> copyNode(GraphNode<ID_TYPE, NodePermissions> startNode) {
		return new GraphNode<>(
			// TODO ensure both are immutable or copy them
			startNode.getId(), 
			startNode.getPayload(),
			startNode.getMutable()
		);
	}
	
	

//	/**
//	 * Checks if this {@link AllowedPathsGraph} allows the given node id to be added to the given path
//	 * 
//	 * @param pathToBeChecked the path that the node should be added to 
//	 * @param nodeId the id of the node that should be added to the path
//	 * 
//	 * @throws IllegalStateException if the given node id may not be added to the given path 
//	 */
//	public void isNodeAllowedToSetNext(Path<ID_TYPE, ?> pathToBeChecked, ID_TYPE nodeId) {
//		
//		// preconditions
//		nn(nodeId, "'nodeId' is null!");
//		
//		if(pathToBeChecked==null){
//			// check if the given node may be the first node in the path 
//			GraphNode<ID_TYPE, NodePermissions> startNode = getStartNode();
//			
//			// easy wins
//			if(nodeId.equals(startNode.getId())){
//				// it's the startNode, so it is allowed for sure as start
//				return;
//			} else if(startNode.getPayload().isRequired()){
//				// if the startNode is required but the given node is not the start node...fail
//				throw new IllegalStateException("You try to set '"+nodeId+"' as the first node on your recorded path but the required startNode would be '"+startNode.getId()+"'");
//			}
//			
//			/* 
//			 * get through children
//			 * if node is found...returned
//			 * if not 
//			 * 	repeat with the same with the children that are >NOT required< or >required AND set<
//			 * 	if there are no...throw exception as no valid path found
//			 */
//			
//		}else{
//
//			GraphNode<ID_TYPE, ?> checkedNode = pathToBeChecked.getStartNode();
//			GraphNode<ID_TYPE, NodePermissions> permissionNode = this.getStartNode();
//
//			while(true){
//
//				// preconditions
//				nn(checkedNode, "'checkedNode' is null!");
//				nn(permissionNode, "'permissionNode' is null!");
//				
//				if(!checkedNode.getId().equals(permissionNode.getId())){
//					
//				}
//
//				
//				// check required
//				if(permissionNode.getPayload().isRequired() && checkedNode.getPayload()==null){
//					return false;
//				}
//
//				
//				// get children for going on recursively
//				if(checkedNode.getNextNodes()==null){
//					break;
//					// normal for end of path?!
////					throw new IllegalStateException("No child!");
//				}else if(checkedNode.getNextNodes().size()!=1){
//					throw new IllegalStateException("A path node needs exactly one child but checkedNode '"+checkedNode.getId()+"' has "+checkedNode.getNextNodes().size()+"!");
//				}
//				List<GraphNode<ID_TYPE, NodePermissions>> permissionChildren = permissionNode.getNextNodes(); 
//				if(permissionChildren==null){
//					break;
//					// normal for end of path?!
////					throw new IllegalStateException("No child!");
//				}else if(permissionChildren.size()!=1){
//					throw new IllegalStateException("A path node needs exactly on child but permissionNode '"+permissionNode.getId()+"' has "+permissionChildren.size()+"!");
//				}
//				GraphNode<ID_TYPE, ?> child = checkedNode.getNextNodes().get(0);
//				GraphNode<ID_TYPE, NodePermissions> permissionChild = permissionChildren.get(0);
//
//				checkedNode = child;
//				permissionNode = permissionChild;
//				
//			}
//			
//			
////			// TODO
////			// check allowedPath with the pathToBeChecked...should be more perormant as getting all possible paths before!?
////		
////			// search an allowed path
////
////			/* 1) get all allowed paths from startNode to any of the nodes with this nodeId
////			 * 2) get through all those subpaths and check if all required nodes have already been set
////			 * 3) if one is found...return
////			 * 		otherwise exception */
////			
////			// 1 TODO add required... flag to only return paths were all required nodes are already set
////			List<Path<ID_TYPE, NodePermissions>> allPathsToThisNode = getAllPathsFromStartToAllNodesWithId(nodeId);
////			
////			for(Path<ID_TYPE, NodePermissions> allowedPath: allPathsToThisNode){
////				if(allRequiredNodesAreSet(pathToBeChecked, allowedPath)){
////					// finding one is enough
////					return;
////				}
////			}
////			
////			throw new IllegalStateException("Not allowed to set node '"+nodeId+"' in "+pathToBeChecked+" now!");
//			
//		}	
//		
//	}

//	private boolean allRequiredNodesAreSet(Path<ID_TYPE, ?> pathToBeChecked, Path<ID_TYPE, NodePermissions> allowedPath) {
//		
//		GraphNode<ID_TYPE, ?> checkedNode = pathToBeChecked.getStartNode();
//		GraphNode<ID_TYPE, NodePermissions> permissionNode = allowedPath.getStartNode();
//
//		while(true){
//
//			// preconditions
//			nn(checkedNode, "'checkedNode' is null!");
//			nn(permissionNode, "'permissionNode' is null!");
//			if(!checkedNode.getId().equals(permissionNode.getId())){
//				throw new IllegalStateException("Can't get permissions for node with id '"+checkedNode.getId()+"' from a permission node with id '"+permissionNode.getId()+"'. Have to be equal!");
//			}
//
//			
//			// check required
//			if(permissionNode.getPayload().isRequired() && checkedNode.getPayload()==null){
//				return false;
//			}
//
//			
//			// get children for going on recursively
//			if(checkedNode.getNextNodes()==null){
//				break;
//				// normal for end of path?!
////				throw new IllegalStateException("No child!");
//			}else if(checkedNode.getNextNodes().size()!=1){
//				throw new IllegalStateException("A path node needs exactly one child but checkedNode '"+checkedNode.getId()+"' has "+checkedNode.getNextNodes().size()+"!");
//			}
//			List<GraphNode<ID_TYPE, NodePermissions>> permissionChildren = permissionNode.getNextNodes(); 
//			if(permissionChildren==null){
//				break;
//				// normal for end of path?!
////				throw new IllegalStateException("No child!");
//			}else if(permissionChildren.size()!=1){
//				throw new IllegalStateException("A path node needs exactly on child but permissionNode '"+permissionNode.getId()+"' has "+permissionChildren.size()+"!");
//			}
//			GraphNode<ID_TYPE, ?> child = checkedNode.getNextNodes().get(0);
//			GraphNode<ID_TYPE, NodePermissions> permissionChild = permissionChildren.get(0);
//
//			checkedNode = child;
//			permissionNode = permissionChild;
//			
//		}
//		
//		return true;
//	}


	
	
//	/**
//	 * @param nodeToStartSearch the given id is searched in the child nodes of this node 
//	 * @param idOfNodeToEndSearch the id of the node to search. All nodes between the start node and this node have to have the payload set
//	 * 
//	 * @return the node instance with the id where to end the search
//	 * @throws IllegalStateException if any node that needs to be set in between is not set yet
//	 */
//	public PathNode<ID_TYPE, Instant> checkIfAllRequiredNodesAreSetTill(PathNode<ID_TYPE, Instant> nodeToStartSearch, ID_TYPE idOfNodeToEndSearch) {
//		return null;
//	}
//
//	public PathNode<ID_TYPE, Instant> checkIfThisMilestoneCanBeFirst(ID_TYPE m) {
//		return null;
//	}


}
