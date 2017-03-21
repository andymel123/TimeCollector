package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import eu.andymel.timecollector.util.RecursionSavetyCounter;

public class Path<ID_TYPE, PAYLOAD_TYPE> extends Graph<ID_TYPE, PAYLOAD_TYPE> {

	public Path(GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode) {
		this(startNode, Mutable.ALWAYS_MUTABLE);
	}
	public Path(ID_TYPE id, PAYLOAD_TYPE payload) {
		this(id, payload, Mutable.ALWAYS_MUTABLE);
	}

	public Path(GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode, Mutable mutable) {
		super(startNode, false, mutable);
	}
	public Path(ID_TYPE id, PAYLOAD_TYPE payload, Mutable mutable) {
		super(
			id, 
			payload, 
			false, // no circles or multi edges allowed
			mutable
		);
	}
	
	
	public GraphNode<ID_TYPE, PAYLOAD_TYPE> addNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		
		// preconditions
		nn(id, "The given id is null!");
		checkMutable();
		
		//build new node for this milestone
		GraphNode<ID_TYPE, PAYLOAD_TYPE> newNode = new GraphNode<ID_TYPE, PAYLOAD_TYPE>(id, payload, getMutable(), false);
		
		// connect the new node with the last node
		GraphNode<ID_TYPE, PAYLOAD_TYPE> lastNode = getLastNode();
		
		/* Put in the same instance of edge because the only mutable things
		 * in the edge are the nodes itself, and those I need to be the same instance */
		Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edge = Edge.create(lastNode, newNode);
		lastNode.addNextNode(edge);
		newNode.addPrevNode(edge);
		
		return newNode;
	}
	
	public GraphNode<ID_TYPE, PAYLOAD_TYPE> getLastNode(){
		// TODO override add methods and remember
		GraphNode<ID_TYPE, PAYLOAD_TYPE> node = getStartNode();
		int max = 1000;
		RecursionSavetyCounter savetyCounter = RecursionSavetyCounter.create(max, "Seems like this path runs in circles. After going through "+max+" nodes I still found no end!");
		while(true){
			savetyCounter.inc();
			List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> edges = node.getEdgesToChildren();
			if(edges.size()==0){
				return node;
			}else if(edges.size()>1){
				throw new IllegalStateException("A node in a "+getClass().getSimpleName()
					+" can only have 1 child! But "+node+" has "+edges.size()
					+" edges to children! "+Arrays.toString(edges.toArray()));
			}else{
				// exactly one child
				node = edges.get(0).getChildNode();
			}
		}
		
	}
	
	public int getLength(){
		// TODO not nice
		int[] count = new int[1];
		count[0] = 0;
		forEach((n)->count[0]++);
		return count[0];
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName())
		.append('[').append(hashCode()).append(", ");
		
		forEach(n -> sb.append(n.getId()).append(' '));
		sb.append(']');
		return sb.toString();
	}
	public Iterator<GraphNode<ID_TYPE, PAYLOAD_TYPE>> iterator() {
		return new Iterator<GraphNode<ID_TYPE,PAYLOAD_TYPE>>() {

			GraphNode<ID_TYPE, PAYLOAD_TYPE> next = getStartNode();
			
			@Override
			public boolean hasNext() {
				return next!=null;
			}

			@Override
			public GraphNode<ID_TYPE, PAYLOAD_TYPE> next() {
				GraphNode<ID_TYPE, PAYLOAD_TYPE> n = next;
				List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> edgesToChildren = n.getEdgesToChildren();
				if(edgesToChildren == null || edgesToChildren.size()==0){
					next = null;
				}else if(edgesToChildren.size()>1){
					throw new IllegalArgumentException("A node in a Path may not have more than one child but "+n+" has "+ edgesToChildren.size()+" children!");
				}else{
					next = edgesToChildren.get(0).getChildNode();
				}
				return n;
			}
		};
	}
	
}
