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
	
	/** It should be ok to simply skip edges that the user adds multiple times (between the same instances of nodes)
	 * to find problems this can be activated */
	private static final boolean THROW_ERROR_IF_EDGE_IS_ADDED_MULTIPLE_TIMES = false;
	
	private final GraphNode<ID_TYPE, NodePermissions> startNode;
	private final Set<GraphNode<ID_TYPE, NodePermissions>> nodes;
	private final List<Edge<GraphNode<ID_TYPE, NodePermissions>>> edges;
	private final SimpleMutable simpleMutable = new SimpleMutable(true);
	private final AllowedPathsLayoutHelpData layoutHelpInfo;
	
	AllowedPathBuilderNodesAndEdges(GraphNode<ID_TYPE, NodePermissions> startNode, List<GraphNode<ID_TYPE, NodePermissions>> otherNodes) {
		
		// preconditions
		nn(startNode, "'startNode' is null!");
		this.startNode = startNode;

		if(otherNodes!=null){
			
			/* I allow equal nodes at the moment (same milestone instance leads 
			 * to equal nodes). I use an IdentityHashSet to ensure that same 
			 * instances are not in there more than once */
			this.nodes = Collections.newSetFromMap(
				new IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>,Boolean>()
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
			count[0]++;
		});
		if(count[0]!=nodes.size()){
			throw new IllegalStateException("Nodes: "+nodes+" but count: "+count[0]);
		}
		this.edges = new LinkedList<>();
		
		
		layoutHelpInfo = new AllowedPathsLayoutHelpData(
			nodes.stream()
			.map(n->""+System.identityHashCode(n))
			.collect(Collectors.toList())
		);
		
	}
	
	
	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edgeWithMax(int max, GraphNode<ID_TYPE, NodePermissions> node1, GraphNode<ID_TYPE, NodePermissions> node2) {
		return edge(node1, node2, EdgePermissions.max(max));
	}

	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edge(GraphNode<ID_TYPE, NodePermissions> node1, GraphNode<ID_TYPE, NodePermissions> node2){
		return edge(node1, node2, null);
	}
		

	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edge(GraphNode<ID_TYPE, NodePermissions> node1, GraphNode<ID_TYPE, NodePermissions> node2, EdgePermissions edgePermissions){
		
		// preconditions
		nn(node1, "'node1' is null!");
		nn(node2, "'node2' is null!");
		if(!nodes.contains(node1)){
			throw new IllegalStateException("At least the first node that you provided was not found! You have to use the "+PermissionNode.class.getSimpleName()+" instances that you provided in the "+AllowedPathsGraph.class.getSimpleName()+".nodes() method!");
		}
		if(!nodes.contains(node2)){
			throw new IllegalStateException("The second node you provided ws not found! You have to use the "+PermissionNode.class.getSimpleName()+" instances that you provided in the "+AllowedPathsGraph.class.getSimpleName()+".nodes() method!");
		}
		
		Edge<GraphNode<ID_TYPE, NodePermissions>> newEdge = Edge.create(node1, node2, edgePermissions);
		checkEdge(newEdge);
		
		int idx = edges.indexOf(newEdge);
		if(idx!=-1){
			// don't add equal edges (identity!!, so equal means connecting the same INSTANCES of nodes, not different nodes with same values)
			String msg = "This edge was saved earlier! '"+newEdge+"'";
			if(THROW_ERROR_IF_EDGE_IS_ADDED_MULTIPLE_TIMES){
				throw new IllegalStateException(msg);
			} else {
				LOG.warn(msg);
				return this;
			}
		}
	
		edges.add(newEdge);
		return this;
	}
	
	private static <ID_TYPE> void checkEdge(Edge<GraphNode<ID_TYPE, NodePermissions>> newEdge) {
		GraphNode<ID_TYPE, NodePermissions> child = newEdge.getChildNode();
		GraphNode<ID_TYPE, NodePermissions> parent = newEdge.getParentNode();
		
		nn(child, "childNode in edge is null!");
		nn(parent, "parentNode in edge is null!");
		nn(child.getMutable(), "childNode of edge has no mutable!");
		nn(parent.getMutable(), "parentNode of edge has no mutable!");
		
	}


	public AllowedPathsGraph<ID_TYPE> build(){
		if(!simpleMutable.isMutable()){
			throw new IllegalStateException("You have already build this path! Use the previously returned instance.");
		}
		
		// copy to be able to remove what I added, to see if something is left after adding everything I get
		List<GraphNode<ID_TYPE, NodePermissions>> copyOfNodes = new ArrayList<>(nodes);
		List<Edge<GraphNode<ID_TYPE, NodePermissions>>> copyOfEdges = new ArrayList<>(edges);
		
		Iterator<GraphNode<ID_TYPE, NodePermissions>> itNodes = copyOfNodes.iterator();
		while(itNodes.hasNext()){
			GraphNode<ID_TYPE, NodePermissions> node1 = itNodes.next();
			Iterator<Edge<GraphNode<ID_TYPE, NodePermissions>>> itEdges = copyOfEdges.iterator();
			boolean nodeRemoved = false;
			while(itEdges.hasNext()){
				Edge<GraphNode<ID_TYPE, NodePermissions>> edge = itEdges.next();
				GraphNode<ID_TYPE, NodePermissions> node2 = edge.getChildNode();
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
		
		return new AllowedPathsGraph<ID_TYPE>(nodes, startNode, simpleMutable, layoutHelpInfo);
	}
	
	public AllowedPathBuilderNodesAndEdges<ID_TYPE> path(GraphNode<ID_TYPE, NodePermissions>... nodes) {
		
		// preconditiions
		nn(nodes, "You need to provide nodes for this call!");
		
		List<GraphNode<ID_TYPE, NodePermissions>> nodeList = Arrays.asList(nodes);
		
		Iterator<GraphNode<ID_TYPE, NodePermissions>> it = nodeList.iterator();
		if(!it.hasNext())return this;
		GraphNode<ID_TYPE, NodePermissions> node1 = it.next();
		checkInNodes(node1);
		while(it.hasNext()){
			GraphNode<ID_TYPE, NodePermissions> node2 = it.next();
			try{
				checkInNodes(node2);
			}catch(IllegalStateException e){
				throw new IllegalStateException("A node does not hold a Mutable, the specified node before was "+node1, e);
			}
			edge(node1, node2);
			node1 = node2;
		}

		// I save some data to now how to visualize this allowedGraph(in the way the user used the path method)
		if(this.layoutHelpInfo!=null){
			this.layoutHelpInfo.addPath(
				nodeList.stream()
				.map(n->""+System.identityHashCode(n))
				.collect(Collectors.toList())
			);
		}
		
		return this;
	}


	private void checkInNodes(GraphNode<ID_TYPE, NodePermissions> node) {
		if(!this.nodes.contains(node)){
			throw new IllegalStateException("You try to add a path with a node that "
				+ "you did not specify in nodes(...)! Node: "+node);
		}
		if(node.getMutable()==null){
			throw new IllegalStateException("The node "+node+" was specified in nodes(...) but it holds no instance of Mutable!" );
		}
	}

}
