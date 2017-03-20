package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllowedPathBuilderNodesAndEdges<ID_TYPE> {

	private static final Logger LOG = LoggerFactory.getLogger(AllowedPathBuilderNodesAndEdges.class);
	
	private static final boolean THROW_ERROR_IF_EDGE_IS_ADDED_MULTIPLE_TIMES = false;
	
	private final PermissionNode<ID_TYPE> startNode;
	private final Set<PermissionNode<ID_TYPE>> nodes;
	private final List<Edge<PermissionNode<ID_TYPE>>> edges;
	private final SimpleMutable simpleMutable = new SimpleMutable(true);
	 
	AllowedPathBuilderNodesAndEdges(PermissionNode<ID_TYPE> startNode, List<PermissionNode<ID_TYPE>> otherNodes) {
		
		// preconditions
		nn(startNode, "'startNode' is null!");
		this.startNode = startNode;

		if(otherNodes!=null){
			
			/* I allow equal nodes at the moment (same milestone instance leads 
			 * to equal nodes). I use an IdentityHashSet to ensure that same 
			 * instances are not in there more than once */
			this.nodes = Collections.newSetFromMap(
				new IdentityHashMap<PermissionNode<ID_TYPE>,Boolean>()
			);
			this.nodes.add(startNode); // first has to be the satrtNode!
			this.nodes.addAll(otherNodes);
		}else{
			// TODO just one node was provided...exception? special case?
			this.nodes = new HashSet<>(1);
			this.nodes.add(startNode);
		}
		int[] count = new int[1]; count[0]=0;
		this.nodes.forEach(n->{
			n.setMutable(simpleMutable);
			LOG.info("Set Mutable on "+n);
			count[0]++;
		});
		if(count[0]!=nodes.size()){
			throw new IllegalStateException("Nodes: "+nodes+" but count: "+count[0]);
		}
		this.edges = new LinkedList<>();
		
	}
	
	
	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edgeWithMax(int max, PermissionNode<ID_TYPE> node1, PermissionNode<ID_TYPE> node2) {
		return edge(node1, node2, EdgePermissions.max(max));
	}

	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edge(PermissionNode<ID_TYPE> node1, PermissionNode<ID_TYPE> node2){
		return edge(node1, node2, null);
	}
		

	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edge(PermissionNode<ID_TYPE> node1, PermissionNode<ID_TYPE> node2, EdgePermissions edgePermissions){
		
		// preconditions
		nn(node1, "'node1' is null!");
		nn(node2, "'node2' is null!");
		if(!nodes.contains(node1)){
			throw new IllegalStateException("At least the first node that you provided was not found! You have to use the "+PermissionNode.class.getSimpleName()+" instances that you provided in the "+AllowedPathsGraph.class.getSimpleName()+".nodes() method!");
		}
		if(!nodes.contains(node2)){
			throw new IllegalStateException("The second node you provided ws not found! You have to use the "+PermissionNode.class.getSimpleName()+" instances that you provided in the "+AllowedPathsGraph.class.getSimpleName()+".nodes() method!");
		}
		
		Edge<PermissionNode<ID_TYPE>> newEdge = Edge.create(node1, node2, edgePermissions);
		
		if(THROW_ERROR_IF_EDGE_IS_ADDED_MULTIPLE_TIMES){
			int idx = edges.indexOf(newEdge);
			if(idx!=-1){
				throw new IllegalStateException("There is already an edge like the one you want to add at index "+idx+" of your "+edges.size()+" edges! Edge: '"+newEdge+"'");
			}
		}
		checkEdge(newEdge);
		edges.add(newEdge);
		
		
		return this;
	}
	
	private static <ID_TYPE> void checkEdge(Edge<PermissionNode<ID_TYPE>> newEdge) {
		PermissionNode<ID_TYPE> child = newEdge.getChildNode();
		PermissionNode<ID_TYPE> parent = newEdge.getParentNode();
		
		nn(child, "childNode in edge is null!");
		nn(parent, "parentNode in edge is null!");
		nn(child.getMutable(), "childNode of edge has no mutable!");
		nn(parent.getMutable(), "parentNode of edge has no mutable!");
		
	}


	public AllowedPathsGraph<ID_TYPE> build(){
		if(!simpleMutable.isMutable()){
			throw new IllegalStateException("You have already build this path! Use the previously returned instance.");
		}
		
		List<PermissionNode<ID_TYPE>> copyOfNodes = new ArrayList<>(nodes);
		List<Edge<PermissionNode<ID_TYPE>>> copyOfEdges = new ArrayList<>(edges);
		
		Iterator<PermissionNode<ID_TYPE>> itNodes = copyOfNodes.iterator();
		while(itNodes.hasNext()){
			PermissionNode<ID_TYPE> node1 = itNodes.next();
			Iterator<Edge<PermissionNode<ID_TYPE>>> itEdges = copyOfEdges.iterator();
			boolean nodeRemoved = false;
			while(itEdges.hasNext()){
				Edge<PermissionNode<ID_TYPE>> edge = itEdges.next();
				PermissionNode<ID_TYPE> node2 = edge.getChildNode();
				if(edge.getParentNode()==node1){
					itEdges.remove();
					if(!nodeRemoved){
						/* multiple edges can be removed for this node1, the node
						 * itself can only be removed once, the next time it.remove()
						 * would throw an IllegalStateException() */
						itNodes.remove();
						nodeRemoved = true;
					}
					/* Put in the same instance of edge because the only mutable things
					 * in the edge are the nodes itself, and those I need to be the same instance */
					Edge<GraphNode<ID_TYPE, NodePermissions>> copyOfEdge = Edge.create(node1, node2, edge.getEdgePermissions());
					node1.addNextNode(copyOfEdge);
					node2.addPrevNode(copyOfEdge);
				}
			}
		}
		
		if(copyOfEdges.size()>0){
			throw new IllegalStateException(copyOfEdges.size()+" edges seem to be not used in the graph! "+Arrays.toString(copyOfEdges.toArray()));
		}
		if(copyOfNodes.size()>0){
			/* if a node was no node1 in an edge (has no outgoing edges) it is still in this list
			 * check if it has ingoing edges (it is used)*/
			itNodes = copyOfNodes.iterator();
			while(itNodes.hasNext()){
				if(itNodes.next().getEdgesToParents().size()>0)itNodes.remove();
			}
			
			if(copyOfNodes.size()>0){
				String s = copyOfNodes.stream().map(n -> n.getId().toString()).collect(Collectors.joining(", "));
				throw new IllegalStateException("the following nodes are not used in the graph: "+ s);
			}
		}

		simpleMutable.setImmutable();
		
//		throw new IllegalStateException("TODO I need an instance of AllowedPathsGraph");
		return new AllowedPathsGraph<>(startNode, simpleMutable);
	}

//	private static final <ID_TYPE> boolean checkForCircularConnections(Graph<ID_TYPE, NodePermissions> graph){
//		
//		nn(graph, "graph is null!");
//		
////		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = graph.getStartNode();
////		HashSet<GraphNode<ID_TYPE, PAYLOAD_TYPE>>
//
//		graph.forEachRootOfACircle((rootOfCircle) -> {
//			/* walk through the parents of this node and search 
//			 * for some edge permission that terminates the loop 
//			 * every loop (that should be recorded) has to end 
//			 * otherwise recording data would stack up endlessly */
//			
//			nn(rootOfCircle, "'rootOfCircle' is null!");
//			
//			/* as long as there is just one parent node I can iterate instead of something
//			 * recursive to prevent stack overflow in graphs that are very deep */
//			List<Edge<GraphNode<ID_TYPE, NodePermissions>>> parents = rootOfCircle.getEdgesToParents();
//			if(parents==null){
//				/* if the currentFirstNode is the startNode we should have handled that earlier
//				 * all other nodes need to have parents */
//				throw new IllegalStateException("No parents for node with id '"+rootOfCircle.getId()+"'");
//			}
//			
//			while(parents.size()==1){
//				GraphNode<ID_TYPE, NodePermissions> singleParent = parents.get(0).getParentNode();
//				baseReversedList.add(singleParent.copy());
//				
//				if(singleParent == getStartNode()){
//					// path finished
//					results.add(baseReversedList);
//					return;
//				} else {
//					parents = singleParent.getEdgesToParents();
//				}
//			}
//
//			// if mutliple parents...use recursive approach
//			for(Edge<GraphNode<ID_TYPE, NodePermissions>> edgeToParent: parents){
//				GraphNode<ID_TYPE, NodePermissions> parent = edgeToParent.getParentNode();
//				List<GraphNode<ID_TYPE, NodePermissions>> copyOfReversedList = copyNodeList(baseReversedList);
//				copyOfReversedList.add(parent.copy());
//				getAllReversedListsOfNodesToStartNode(copyOfReversedList, parent, results);
//			}
//			
//		});
//		
//		return false;
//	}
	
	
	
	public AllowedPathBuilderNodesAndEdges<ID_TYPE> path(PermissionNode<ID_TYPE>... nodes) {
		
		// preconditiions
		nn(nodes, "You need to provide nodes for this call!");
		
		Iterator<PermissionNode<ID_TYPE>> it = Arrays.asList(nodes).iterator();
		if(!it.hasNext())return this;
		PermissionNode<ID_TYPE> node1 = it.next();
		checkInNodes(node1);
		while(it.hasNext()){
			PermissionNode<ID_TYPE> node2 = it.next();
			try{
				checkInNodes(node2);
			}catch(IllegalStateException e){
				throw new IllegalStateException("A node does not hold a Mutable, the specified node before was "+node1, e);
			}
			edge(node1, node2);
			node1 = node2;
		}
		return this;
	}


	private void checkInNodes(PermissionNode<ID_TYPE> node) {
		if(!this.nodes.contains(node)){
			throw new IllegalStateException("You try to add a path with a node that "
				+ "you did not specify in nodes(...)! Node: "+node);
		}
		if(node.getMutable()==null){
			throw new IllegalStateException("The node "+node+" was specified in nodes(...) but it holds no instance of Mutable!" );
		}
	}

}
