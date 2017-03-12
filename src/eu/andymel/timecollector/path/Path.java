package eu.andymel.timecollector.path;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO copy and add immutable view on the path when finished to add nodes
 * 
 * @author andymatic
 *
 * @param <NODE_ID_TYPE>
 */
public class Path<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> {

	private final PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> startNode;
	private final List<PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> lastNodes = new LinkedList<>();
	
	private final HashSet<NODE_ID_TYPE> usedIds = new HashSet<>();
	
	
	private Path(PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> startNode) {
		this.startNode = startNode;
		usedIds.add(startNode.getId());
		this.lastNodes.clear();
		this.lastNodes.add(startNode);
	}

	public Path(NODE_ID_TYPE id, NODE_PAYLOAD_TYPE payload) {
		this(new PathNode<>(id, payload));
	}

	private List<PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> getLastNodes() {
		return lastNodes;
	}

	public PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> addNode(NODE_ID_TYPE id, NODE_PAYLOAD_TYPE nodePermissions) {
		
		// preconditions
		nn(id, "The given id is null!");
		checkIdNotUsedYet(id);
		
		//build new node for this milestone
		PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> newNode = new PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>(id, nodePermissions);
		
		// connect the new node with the last nodes
		for(PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> lastNode: lastNodes){
			lastNode.addNextNode(newNode);
			newNode.addPrevNode(lastNode);
		}
		
		// set the lastAddedNodes reference on the new node
		lastNodes.clear();
		lastNodes.add(newNode);
		
		return newNode;
	}

	private void checkIdNotUsedYet(NODE_ID_TYPE id) {
		if(usedIds.contains(id)){
			throw new IllegalStateException("You try to add the node id '"+id+"' again. Ids have to be unique!");
		}
	}

	public void addParallel(List<Path<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> anyOfThoseSubPaths){
		
		// preconditions
		nn(anyOfThoseSubPaths, "addParallel(...) needs paths!");
		if(anyOfThoseSubPaths.size()<2){
			throw new IllegalArgumentException("addParallel() needs at least 2 paths!");
		}
		
		List<PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> newNodes = new ArrayList<>(anyOfThoseSubPaths.size());
		
		// add subpathes to last nodes of current path
		for(Path<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> subPath: anyOfThoseSubPaths){
			PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> firstOfSubPath = subPath.getStartNode();
			List<PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> lastNodesOfSubPath = subPath.getLastNodes();
			newNodes.addAll(lastNodesOfSubPath);
			
			for(PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> lastNode: lastNodes){
				lastNode.addNextNode(firstOfSubPath);
				firstOfSubPath.addPrevNode(lastNode);
			}
		}
		
		// add last nodes of those subpaths as current last nodes of total path
		// set the lastAddedNodes reference on the new nodes
		lastNodes.clear();
		lastNodes.addAll(newNodes);

	}
	
	protected PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> getStartNode() {
		return startNode;
	}

	public void setPayload(NODE_ID_TYPE id, NODE_PAYLOAD_TYPE p){
		PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> node = getChildWithId(startNode, id);
		node.setPayload(p);
	}
	public NODE_PAYLOAD_TYPE getPayload(NODE_ID_TYPE id){
		
		// preconditions
		nn(id, "'id' is null!");
		
		// shortcut
		if(id.equals(startNode.getId()))return startNode.getPayload();
		
		// otherwise search for the node
		PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> node = getChildWithId(startNode, id);
		
		if(node==null){
			// no node found with this id
			return null;
		}
		return node.getPayload();
	}

	private PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> getNode(NODE_ID_TYPE idToSearch){
		// TODO add hashmap in addition to linked nodes to speed up in big paths
		if(idToSearch.equals(startNode.getId()))return startNode;
		return getChildWithId(startNode, idToSearch);
	}
	
	private PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> getChildWithId(PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> root, NODE_ID_TYPE idToSearch) {

		// preconditions
		nn(root, "'root' is null!");
		nn(idToSearch, "'idToSearch' is null!");
		
		// if the startnode itself is the searched node...return it
		if(idToSearch.equals(root.getId())){
			/* root is not a child of itself, returning null would be enough here,
			 * but I throw an error as I should check that earlier and I want to 
			 * recognize this as a problem. */
			throw new IllegalStateException("'"+idToSearch+"' can't be the id of "
				+ "a child as it's already the id of your given root note!");
		};
		
		// otherwise search recursivly in its childs
		return root.getChildWithId(idToSearch);
		
	}

}
