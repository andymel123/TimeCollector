package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedPathBuilderNodesAndEdges<ID_TYPE> {

	private PermissionNode<ID_TYPE> startNode;
	private Set<PermissionNode<ID_TYPE>> nodes;
	private List<Edge<PermissionNode<ID_TYPE>>> edges;
	private SimpleMutable simpleMutable = new SimpleMutable(true);
	
	AllowedPathBuilderNodesAndEdges(PermissionNode<ID_TYPE> startNode, PermissionNode<ID_TYPE>... otherNodes) {
		
		// preconditions
		nn(startNode, "'startNode' is null!");
		if(otherNodes!=null){
			this.startNode = startNode;
			this.nodes = new HashSet<>(otherNodes.length+1);	// +1 for startNode
			this.nodes.add(startNode); // first has to be the satrtNode!
			this.nodes.addAll(Arrays.asList(otherNodes));
			
			this.nodes.forEach(n->n.setMutable(simpleMutable));
			
			this.edges = new LinkedList<>();
		}else{
			// TODO just one node was provided...exception? special case?
			throw new IllegalArgumentException("Just one node for a graph? I have to think about that...");
		}
	}
	
	public AllowedPathBuilderNodesAndEdges<ID_TYPE> edge(PermissionNode<ID_TYPE> node1, PermissionNode<ID_TYPE> node2){
		
		// preconditions
		nn(node1, "'node1' is null!");
		nn(node2, "'node2' is null!");
		if(!nodes.contains(node1)){
			throw new IllegalStateException("At least the first node that you provided was not found! You have to use the "+PermissionNode.class.getSimpleName()+" instances that you provided in the "+AllowedPathsGraph.class.getSimpleName()+".nodes() method!");
		}
		if(!nodes.contains(node2)){
			throw new IllegalStateException("The second node you provided ws not found! You have to use the "+PermissionNode.class.getSimpleName()+" instances that you provided in the "+AllowedPathsGraph.class.getSimpleName()+".nodes() method!");
		}
		
		edges.add(new Edge<PermissionNode<ID_TYPE>>(node1, node2));
		
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
			while(itEdges.hasNext()){
				Edge<PermissionNode<ID_TYPE>> edge = itEdges.next();
				PermissionNode<ID_TYPE> node2 = edge.getNode2();
				if(edge.getNode1()==node1){
					itEdges.remove();
					itNodes.remove();
					node1.addNextNode(node2);
					node2.addPrevNode(node1);
				}
			}
		}
		
		if(copyOfEdges.size()>0){
			throw new IllegalStateException(copyOfEdges.size()+" edges seem to be not used in the graph!");
		}
		if(copyOfNodes.size()>0){
			/* if a node was no node1 in an edge (has no outgoing edges) it is still in this list
			 * check if it has ingoing edges (it is used)*/
			itNodes = copyOfNodes.iterator();
			while(itNodes.hasNext()){
				if(itNodes.next().getPrevNodes().size()>0)itNodes.remove();
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

}
