package eu.andymel.timecollector.path;

import java.time.Instant;

import static eu.andymel.timecollector.util.Preconditions.*;

public class AllowedPath<ID_TYPE> extends Path<ID_TYPE, NodePermissions> {

	
	public AllowedPath(ID_TYPE idOfStartNode, NodePermissions nodePermissionsOfStartNode) {
		super(idOfStartNode, nodePermissionsOfStartNode);
	}

	/**
	 * Checks if this {@link AllowedPath} allows the given node id to be added to the given path
	 * 
	 * @param pathToBeChecked the path that the node should be added to 
	 * @param nodeId the id of the node that should be added to the path
	 * 
	 * @throws IllegalStateException if the given node id may not be added to the given path 
	 */
	public void checkPath(Path<ID_TYPE, ?> pathToBeChecked, ID_TYPE nodeId) {
		
		// preconditions
		nn(nodeId, "'nodeId' is null!");
		
		if(pathToBeChecked==null){
			// check if the given node may be the first node in the path 
			PathNode<ID_TYPE, NodePermissions> startNode = getStartNode();
			
			// easy wins
			if(nodeId.equals(startNode.getId())){
				// it's the startNode, so it is allowed for sure as start
				return;
			} else if(startNode.getPayload().isRequired()){
				// if the startNode is required but the given node is not the start node...fail
				throw new IllegalStateException("You try to set '"+nodeId+"' as the first node on your recorded path but the required startNode would be '"+startNode.getId()+"'");
			}
			
			// search recursively
			
			
		}
	}

	
	
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
