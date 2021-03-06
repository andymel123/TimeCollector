package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.util.IdentitySet;

/**
 * @author andymatic
 *
 * @param <ID_TYPE> The type of the id object (milestone)
 */
public class Graph<ID_TYPE, PAYLOAD_TYPE> {

	private Logger LOG = LoggerFactory.getLogger(Graph.class); 
	
	private final GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode;
	
	// TODO I don't need this lastNodes list anymore after finished building...so maybe move to builder
	private List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> lastNodes = new LinkedList<>();

	/** To find all nodes fast by hash. */
	private final Set<GraphNode<ID_TYPE, PAYLOAD_TYPE>> allNodes;
	
	private final boolean allowMultipleEdges;
	
	private final Mutable mutable;
	
//	protected Graph(ID_TYPE id, PAYLOAD_TYPE payload, boolean allowMultipleEdges, Mutable mutable) {
//		this(new GraphNode<>(id, payload, mutable, allowMultipleEdges), allowMultipleEdges, mutable);
//	}
	protected Graph(Set<GraphNode<ID_TYPE, PAYLOAD_TYPE>> nodes, GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode, boolean allowMultipleEdges, Mutable mutable) {
		this.startNode = startNode;
		this.allowMultipleEdges = allowMultipleEdges;
		this.mutable = mutable;
//		addNodeToInternalCollections(startNode);
		this.lastNodes.clear();
		this.lastNodes.add(startNode);
		this.allNodes = Collections.unmodifiableSet(new IdentitySet<>(nodes));
	}

//	// !used in the constructor! (Don't simply change accessibility to override!)
//	private final void addNodeToInternalCollections(GraphNode<ID_TYPE, PAYLOAD_TYPE> node) {
//		ID_TYPE id = node.getId();
//		
//		boolean addedToSetOfAllNodes = allNodes.add(node);
//		
//		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> listOfNodesWithThisId = nodesOfName.get(id);
//		if(listOfNodesWithThisId==null){
//			listOfNodesWithThisId = new LinkedList<>();
//			nodesOfName.put(id, listOfNodesWithThisId);
//		}
//		if(listOfNodesWithThisId.contains(node)){
//			/* don't know if this is really a problem, I throw an error for now
//			 * ...remove this if it makes sense to call this mutliple time on the same node */
//			throw new IllegalStateException("You try to add the node '"+node+"' mutliple times!");
//		}
//		boolean addedToANamedList = listOfNodesWithThisId.add(node);
//		
//		if(addedToSetOfAllNodes != addedToANamedList){
//			throw new IllegalStateException("addedToSetOfAllNodes("+addedToSetOfAllNodes+") != addedToANamedList("+addedToANamedList+")");
//		}
//	}


	private List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getLastNodes() {
		return lastNodes;
	}

	public Mutable getMutable() {
		return mutable;
	}

//	public void addParallel(List<Graph<ID_TYPE, PAYLOAD_TYPE>> anyOfThoseSubPaths){
//		
//		// preconditions
//		checkMutable();
//		nn(anyOfThoseSubPaths, "addParallel(...) needs paths!");
//		if(anyOfThoseSubPaths.size()<2){
//			throw new IllegalArgumentException("addParallel() needs at least 2 paths!");
//		}
//		
//		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> newNodes = new ArrayList<>(anyOfThoseSubPaths.size());
//		
//		// add subpathes to last nodes of current path
//		for(Graph<ID_TYPE, PAYLOAD_TYPE> subPath: anyOfThoseSubPaths){
//			GraphNode<ID_TYPE, PAYLOAD_TYPE> firstOfSubPath = subPath.getStartNode();
//			List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> lastNodesOfSubPath = subPath.getLastNodes();
//			newNodes.addAll(lastNodesOfSubPath);
//			
//			for(GraphNode<ID_TYPE, PAYLOAD_TYPE> lastNode: lastNodes){
//				/* Put in the same instance of edge because the only mutable things
//				 * in the edge are the nodes itself, and those I need to be the same instance */
//				Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edge = Edge.create(lastNode, firstOfSubPath);
//				lastNode.addNextNode(edge);
//				firstOfSubPath.addPrevNode(edge);
//			}
//		}
//		
//		// add last nodes of those subpaths as current last nodes of total path
//		// set the lastAddedNodes reference on the new nodes
//		lastNodes.clear();
//		lastNodes.addAll(newNodes);
//
//	}
	
	protected void checkMutable() {
		mutable.check();
	}
	protected GraphNode<ID_TYPE, PAYLOAD_TYPE> getStartNode() {
		return startNode;
	}

//	public void setPayload(ID_TYPE id, PAYLOAD_TYPE p){
//		
//		/* first I just wanted to check mutablility for adding edge to other nodes...not for the payload.
//		 * But for the allowedGraph the payload is set when the node is added I think.
//		 * And the recorded path is always mutable, so I should be able to check here as well?! 
//		 * So lets try */
//		checkMutable();
//		
//		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> allNodesWithThisId = getAllNodesWIthId(id);
//		if(allNodesWithThisId==null || allNodesWithThisId.size()!=1){
//			throw new IllegalStateException("Not exactly one node found for id '"+id+"'!Think about what to do with the payload. "+allNodesWithThisId);
//		}
//		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = allNodesWithThisId.get(0);
//		node.setPayload(p);
//	}
//	public PAYLOAD_TYPE getPayload(ID_TYPE id){
//		
//		// preconditions
//		nn(id, "'id' is null!");
//		
//		// search for the node
//		List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> allNodesWithThisId = getAllNodesWIthId(id);
//		if(allNodesWithThisId==null || allNodesWithThisId.size()==0){
//			throw new IllegalStateException("Can't return payload for '"+id+"'! No node found with this id!");
//		}
//		if(allNodesWithThisId.size()>1){
//			throw new IllegalStateException("Found "+allNodesWithThisId.size()+" nodes for id '"+id+"'! Don't know which payload to return!");
//		}
//		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = allNodesWithThisId.get(0);
//		
//		if(node==null){
//			// no node found with this id
//			return null;
//		}
//		return node.getPayload();
//	}

	/**
	 * calls single threaded
	 * @param consumer
	 */
	void forEach(Consumer<GraphNode<ID_TYPE, PAYLOAD_TYPE>> consumer){
//		IdentityHashMap<GraphNode<ID_TYPE, PAYLOAD_TYPE>, Void> found = new IdentityHashMap<>();
		
		IdentitySet<GraphNode<ID_TYPE,PAYLOAD_TYPE>> found = new IdentitySet<>();
		forEachChild(getStartNode(), consumer, found, false);
		int i=0;
	}

//	void forEachRootOfACircle(Consumer<GraphNode<ID_TYPE, PAYLOAD_TYPE>> consumer){
//		forEachChild(getStartNode(), consumer, new IdentityHashMap<>().keySet(), true);
//	}

	private void forEachChild(GraphNode<ID_TYPE, PAYLOAD_TYPE> rootOfSearch, Consumer<GraphNode<ID_TYPE, PAYLOAD_TYPE>> consumer, Set<GraphNode<ID_TYPE,PAYLOAD_TYPE>> alreadyVisited, boolean onlyCallConsumerForRootOfCircles){
		
		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = rootOfSearch;

		while(true){

			// preconditions
			nn(node, "'node' is null!");
			
			if(alreadyVisited.contains(node)){
				// this node was visited earlier
				if(onlyCallConsumerForRootOfCircles){
					consumer.accept(node);
				}
				/* return, as the path to the children of this node has 
				 * also been done if this node has already been visited */
				return;
			}else{
				if(!onlyCallConsumerForRootOfCircles){
					consumer.accept(node);
				}
				alreadyVisited.add(node);
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
				forEachChild(edgeToChild.getChildNode(), consumer, alreadyVisited, onlyCallConsumerForRootOfCircles);
			}
			
			break;
		}
		
	};
	
	public Set<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getAllNodes() {
//		Set<GraphNode<ID_TYPE, PAYLOAD_TYPE>> result = new HashSet<>();
//		// TODO not nice to change the result state within a lambda!
//		forEach(n->result.add(n));
//		return result;
		
		return allNodes;
	}

	
//	protected List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getAllNodesWIthId(ID_TYPE nodeId) {
//		return nodesOfName.get(nodeId);
//	}
	
}
