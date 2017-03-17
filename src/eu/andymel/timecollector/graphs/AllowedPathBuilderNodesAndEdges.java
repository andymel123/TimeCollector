package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedPathBuilderNodesAndEdges<ID_TYPE> {

	private static final boolean THROW_ERROR_IF_EDGE_IS_ADDED_MULTIPLE_TIMES = false;
	
	private final PermissionNode<ID_TYPE> startNode;
	private final Set<PermissionNode<ID_TYPE>> nodes;
	private final List<Edge<PermissionNode<ID_TYPE>>> edges;
	private final SimpleMutable simpleMutable = new SimpleMutable(true);
	 
	AllowedPathBuilderNodesAndEdges(PermissionNode<ID_TYPE> startNode, PermissionNode<ID_TYPE>... otherNodes) {
		
		// preconditions
		nn(startNode, "'startNode' is null!");
		if(otherNodes!=null){
			this.startNode = startNode;

			// HashSet should ensure that no node is in there more than once
			this.nodes = new HashSet<>(otherNodes.length+1);	// +1 for startNode
// commented out the copy because I copy alot of stuff only to be able to reuse nodes for different allowedGraphs
// I guess its ok, that I have to build new nodes per allowed graph...it's unlikely that I have a lot of different allowed graphs that need the same nodes
//			this.nodes.add(startNode.copy()); // first has to be the satrtNode!
//			for(PermissionNode<ID_TYPE> n:otherNodes){
//				this.nodes.add(n.copy());	
//			}
			this.nodes.add(startNode); // first has to be the satrtNode!
			this.nodes.addAll(Arrays.asList(otherNodes));
			this.nodes.forEach(n->n.setMutable(simpleMutable));
			this.edges = new LinkedList<>();
		}else{
			// TODO just one node was provided...exception? special case?
			throw new IllegalArgumentException("Just one node for a graph? I have to think about that...");
		}
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
		edges.add(newEdge);
		
		return this;
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

	public AllowedPathBuilderNodesAndEdges<ID_TYPE> path(PermissionNode<ID_TYPE>... nodes) {
		
		// preconditiions
		nn(nodes, "You need to provide nodes for this call!");
		
		Iterator<PermissionNode<ID_TYPE>> it = Arrays.asList(nodes).iterator();
		if(!it.hasNext())return this;
		PermissionNode<ID_TYPE> node1 = it.next();
		while(it.hasNext()){
			PermissionNode<ID_TYPE> node2 = it.next();
			edge(node1, node2);
			node1 = node2;
		}
		return this;
	}

}
