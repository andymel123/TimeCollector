package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO copy and add immutable view on the path when finished to add nodes
 * 
 * @author andymatic
 *
 * @param <ID_TYPE> The type of the id object (milestone)
 */
public class Graph<ID_TYPE, PAYLOAD_TYPE> {

	Logger LOG = LoggerFactory.getLogger(Graph.class); 
	
	private final GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode;
	
	// TODO I don't need this lastNodes list anymore after finished building...so maybe move to builder
	private List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> lastNodes = new LinkedList<>();

	/** To find all nodes fast by hash. */
	protected final HashMap<ID_TYPE, List<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> nodes = new HashMap<>();
	
	private final boolean allowMultipleEdges;
	
	private final Mutable mutable;
	
//	private boolean finishedLinking = false;
	
	protected Graph(ID_TYPE id, PAYLOAD_TYPE payload, boolean allowMultipleEdges, Mutable mutable) {
		this(new GraphNode<>(id, payload, mutable), allowMultipleEdges, mutable);
	}
	protected Graph(GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode, boolean allowMultipleEdges, Mutable mutable) {
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
	private final void addNodeToHashMap(GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode) {
		ID_TYPE id = startNode.getId();
		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> listOfNodesWithThisId = nodes.get(id);
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


	private List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getLastNodes() {
		return lastNodes;
	}

	public GraphNode<ID_TYPE, PAYLOAD_TYPE> addNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		
		// preconditions
		nn(id, "The given id is null!");
		mutable.check();
		
		//build new node for this milestone
		GraphNode<ID_TYPE, PAYLOAD_TYPE> newNode = new GraphNode<ID_TYPE, PAYLOAD_TYPE>(id, payload, mutable);
		
		// connect the new node with the last nodes
		for(GraphNode<ID_TYPE, PAYLOAD_TYPE> lastNode: lastNodes){
			/* Put in the same instance of edge because the only mutable things
			 * in the edge are the nodes itself, and those I need to be the same instance */
			Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edge = Edge.create(lastNode, newNode);
			lastNode.addNextNode(edge);
			newNode.addPrevNode(edge);
		}
		
		// set the lastAddedNodes reference on the new node
		lastNodes.clear();
		lastNodes.add(newNode);
		
		// add to hashSet to find fast
		addNodeToHashMap(newNode);
		
		return newNode;
	}

	public void addParallel(List<Graph<ID_TYPE, PAYLOAD_TYPE>> anyOfThoseSubPaths){
//	public void addParallel(List<AllowedPathBuilder<NODE_ID_TYPE>> anyOfThoseSubPaths){
		
		// preconditions
		mutable.check();
		nn(anyOfThoseSubPaths, "addParallel(...) needs paths!");
		if(anyOfThoseSubPaths.size()<2){
			throw new IllegalArgumentException("addParallel() needs at least 2 paths!");
		}
		
		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> newNodes = new ArrayList<>(anyOfThoseSubPaths.size());
		
		// add subpathes to last nodes of current path
		for(Graph<ID_TYPE, PAYLOAD_TYPE> subPath: anyOfThoseSubPaths){
//		for(AllowedPathBuilder<NODE_ID_TYPE> builder: anyOfThoseSubPaths){
//			Graph<NODE_ID_TYPE, NODE_PAYLOAD_TYPE> subPath = builder.a
			GraphNode<ID_TYPE, PAYLOAD_TYPE> firstOfSubPath = subPath.getStartNode();
			List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> lastNodesOfSubPath = subPath.getLastNodes();
			newNodes.addAll(lastNodesOfSubPath);
			
			for(GraphNode<ID_TYPE, PAYLOAD_TYPE> lastNode: lastNodes){
				/* Put in the same instance of edge because the only mutable things
				 * in the edge are the nodes itself, and those I need to be the same instance */
				Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edge = Edge.create(lastNode, firstOfSubPath);
				lastNode.addNextNode(edge);
				firstOfSubPath.addPrevNode(edge);
			}
		}
		
		// add last nodes of those subpaths as current last nodes of total path
		// set the lastAddedNodes reference on the new nodes
		lastNodes.clear();
		lastNodes.addAll(newNodes);

	}
	
	protected GraphNode<ID_TYPE, PAYLOAD_TYPE> getStartNode() {
		return startNode;
	}

	public void setPayload(ID_TYPE id, PAYLOAD_TYPE p){
		
		/* first I just wanted to check mutablility for adding edge to other nodes...not for the payload.
		 * But for the allowedGraph the payload is set when the node is added I think.
		 * And the recorded path is always mutable, so I should be able to check here as well?! 
		 * So lets try */
		mutable.check();
		
		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> allNodesWithThisId = getAllNodesWIthId(id);
		if(allNodesWithThisId==null || allNodesWithThisId.size()!=1){
			throw new IllegalStateException("Not exactly one node found for id '"+id+"'!Think about what to do with the payload. "+allNodesWithThisId);
		}
		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = allNodesWithThisId.get(0);
		node.setPayload(p);
	}
	public PAYLOAD_TYPE getPayload(ID_TYPE id){
		
		// preconditions
		nn(id, "'id' is null!");
		
		// search for the node
		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> allNodesWithThisId = getAllNodesWIthId(id);
		if(allNodesWithThisId==null || allNodesWithThisId.size()==0){
			throw new IllegalStateException("Can't return payload for '"+id+"'! No node found with this id!");
		}
		if(allNodesWithThisId.size()>1){
			throw new IllegalStateException("Found "+allNodesWithThisId.size()+" nodes for id '"+id+"'! Don't know which payload to return!");
		}
		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = allNodesWithThisId.get(0);
		
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

	
	/**
	 * calls single threaded
	 * @param consumer
	 */
	void forEach(Consumer<GraphNode<ID_TYPE, PAYLOAD_TYPE>> consumer){
		forEachChild(getStartNode(), consumer, new HashSet<>());
	}
	
	private void forEachChild(GraphNode<ID_TYPE, PAYLOAD_TYPE> rootOfSearch, Consumer<GraphNode<ID_TYPE, PAYLOAD_TYPE>> consumer, HashSet<GraphNode<ID_TYPE, PAYLOAD_TYPE>> alreadyDone){
		
		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = rootOfSearch;

		while(true){

			// preconditions
			nn(node, "'node' is null!");
			
			if(alreadyDone.contains(node)){
				// this node was visited earlier
				LOG.trace(node+" was visited easlier.");
				return;
			}else{
				consumer.accept(node);
				alreadyDone.add(node);
			}
			
			List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> edgesToChildren = node.getEdgesToChildren(); 
			if(edgesToChildren==null){
				// end of path
				break;
			}
			
			if(edgesToChildren.size()==1){
				node = edgesToChildren.get(0).getChildNode();
				continue;
			}
			
			for(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edgeToChild: edgesToChildren){
				forEachChild(edgeToChild.getChildNode(), consumer, alreadyDone);
			}
			
			break;
		}
		
	};
	
	
	protected List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getAllNodesWIthId(ID_TYPE nodeId) {
		return nodes.get(nodeId);
	}
	
}
