package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO copy and add immutable view on the path when finished to add nodes
 * 
 * @author andymatic
 *
 * @param <NODE_ID_TYPE>
 */
public class Graph<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> {

	private final GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> startNode;
	
	// TODO I don't need this lastNodes list anymore after finished building...so maybe move to builder
	private List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> lastNodes = new LinkedList<>();

	/** To find all nodes fast by hash. */
	protected final HashMap<NODE_ID_TYPE, List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>>> nodes = new HashMap<>();
	
	private final boolean allowMultipleEdges;
	
	private final Mutable mutable;
	
//	private boolean finishedLinking = false;
	
	protected Graph(NODE_ID_TYPE id, NODE_PAYLOAD_TYPE payload, boolean allowMultipleEdges, Mutable mutable) {
		this(new GraphNode<>(id, payload, mutable), allowMultipleEdges, mutable);
	}
	protected Graph(GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> startNode, boolean allowMultipleEdges, Mutable mutable) {
		this.startNode = startNode;
		this.allowMultipleEdges = allowMultipleEdges;
		this.mutable = mutable;
		addNodeToHashMap(startNode);
		this.lastNodes.clear();
		this.lastNodes.add(startNode);
	}

//	void setFinishLinking(){
//		lastNodes = Collections.unmodifiableList(lastNodes);
//		// set all nodes finished (links between nodes may not be changed anymore)
////		nodes.values().forEach(
////			list -> list.forEach(
////				node->node.setLinkingFinished()
////			)
////		);
//	}
	
	// !used in the constructor! (Don't simply change accessibility to override!)
	private final void addNodeToHashMap(GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> startNode) {
		NODE_ID_TYPE id = startNode.getId();
		List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> listOfNodesWithThisId = nodes.get(id);
		if(listOfNodesWithThisId==null){
			listOfNodesWithThisId = new LinkedList<>();
			nodes.put(id, listOfNodesWithThisId);
		}
		if(listOfNodesWithThisId.contains(startNode)){
			/* don't know if this is really a problem, I throw an error for now
			 * ...remove this if it makes sense to call this mutliple time on the same node */
			throw new IllegalStateException("You try to add the node '"+startNode+"' mutliple times!");
		}
		listOfNodesWithThisId.add(startNode);
	}


	private List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> getLastNodes() {
		return lastNodes;
	}

	public GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> addNode(NODE_ID_TYPE id, NODE_PAYLOAD_TYPE nodePermissions) {
		
		// preconditions
		nn(id, "The given id is null!");
		mutable.check();
		
		//build new node for this milestone
		GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> newNode = new GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>(id, nodePermissions, mutable);
		
		// connect the new node with the last nodes
		for(GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> lastNode: lastNodes){
			lastNode.addNextNode(newNode);
			newNode.addPrevNode(lastNode);
		}
		
		// set the lastAddedNodes reference on the new node
		lastNodes.clear();
		lastNodes.add(newNode);
		
		// add to hashSet to find fast
		addNodeToHashMap(newNode);
		
		return newNode;
	}

	public void addParallel(List<Graph<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> anyOfThoseSubPaths){
//	public void addParallel(List<AllowedPathBuilder<NODE_ID_TYPE>> anyOfThoseSubPaths){
		
		// preconditions
		mutable.check();
		nn(anyOfThoseSubPaths, "addParallel(...) needs paths!");
		if(anyOfThoseSubPaths.size()<2){
			throw new IllegalArgumentException("addParallel() needs at least 2 paths!");
		}
		
		List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> newNodes = new ArrayList<>(anyOfThoseSubPaths.size());
		
		// add subpathes to last nodes of current path
		for(Graph<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> subPath: anyOfThoseSubPaths){
//		for(AllowedPathBuilder<NODE_ID_TYPE> builder: anyOfThoseSubPaths){
//			Graph<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> subPath = builder.a
			GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> firstOfSubPath = subPath.getStartNode();
			List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> lastNodesOfSubPath = subPath.getLastNodes();
			newNodes.addAll(lastNodesOfSubPath);
			
			for(GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> lastNode: lastNodes){
				lastNode.addNextNode(firstOfSubPath);
				firstOfSubPath.addPrevNode(lastNode);
			}
		}
		
		// add last nodes of those subpaths as current last nodes of total path
		// set the lastAddedNodes reference on the new nodes
		lastNodes.clear();
		lastNodes.addAll(newNodes);

	}
	
	protected GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> getStartNode() {
		return startNode;
	}

	public void setPayload(NODE_ID_TYPE id, NODE_PAYLOAD_TYPE p){
		
		/* first I just wanted to check mutablility for adding edge to other nodes...not for the payload.
		 * But for the allowedGraph the payload is set when the node is added I think.
		 * And the recorded path is always mutable, so I should be able to check here as well?! 
		 * So lets try */
		mutable.check();
		
		List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> allNodesWithThisId = getAllNodesWIthId(id);
		if(allNodesWithThisId==null || allNodesWithThisId.size()!=1){
			throw new IllegalStateException("Not exactly one node found for id '"+id+"'!Think about what to do with the payload. "+allNodesWithThisId);
		}
		GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> node = allNodesWithThisId.get(0);
		node.setPayload(p);
	}
	public NODE_PAYLOAD_TYPE getPayload(NODE_ID_TYPE id){
		
		// preconditions
		nn(id, "'id' is null!");
		
		// search for the node
		List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> allNodesWithThisId = getAllNodesWIthId(id);
		if(allNodesWithThisId==null || allNodesWithThisId.size()==0){
			throw new IllegalStateException("Can't return payload for '"+id+"'! No node found with this id!");
		}
		if(allNodesWithThisId.size()>1){
			throw new IllegalStateException("Found "+allNodesWithThisId.size()+" nodes for id '"+id+"'! Don't know which payload to return!");
		}
		GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> node = allNodesWithThisId.get(0);
		
		if(node==null){
			// no node found with this id
			return null;
		}
		return node.getPayload();
	}

//	private PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> getChildWithId(PathNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> root, NODE_ID_TYPE idToSearch) {
//
//		// preconditions
//		nn(root, "'root' is null!");
//		nn(idToSearch, "'idToSearch' is null!");
//		
//		// if the startnode itself is the searched node...return it
//		if(idToSearch.equals(root.getId())){
//			/* root is not a child of itself, returning null would be enough here,
//			 * but I throw an error as I should check that earlier and I want to 
//			 * recognize this as a problem. */
//			throw new IllegalStateException("'"+idToSearch+"' can't be the id of "
//				+ "a child as it's already the id of your given root note!");
//		};
//		
//		// otherwise search recursivly in its childs
//		return root.getChildWithId(idToSearch);
//		
//	}

	protected List<GraphNode<NODE_ID_TYPE, NODE_PAYLOAD_TYPE>> getAllNodesWIthId(NODE_ID_TYPE nodeId) {
//		return Collections.unmodifiableList(nodes.get(nodeId)); its not public so save some performance
		return nodes.get(nodeId);
	}
	
}
