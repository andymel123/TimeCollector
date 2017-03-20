package eu.andymel.timecollector.graphs;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A single Node on the path
 * 
 * @author andymatic
 *
 */
public class GraphNode<ID_TYPE, PAYLOAD_TYPE> extends AbstractNode<ID_TYPE, PAYLOAD_TYPE>{

	private final List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> prevNodes = new LinkedList<>();
	private final List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> nextNodes = new LinkedList<>();
	
	private final List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> immutableViewOnPrevNodes = Collections.unmodifiableList(prevNodes);
	private final List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> immutableViewOnNextNodes = Collections.unmodifiableList(nextNodes);
	
	
	GraphNode(ID_TYPE id, PAYLOAD_TYPE payload, Mutable mutable, boolean mutltiEdges) {
		super(id, payload, mutable, mutltiEdges);
	}

	GraphNode(ID_TYPE id, PAYLOAD_TYPE payload, boolean mutltiEdges) {
		super(id, payload, mutltiEdges);
	}
	
	void addNextNode(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> e){
		checkMutable();
		if(!isAllowMutltipleEdges() && nextNodes.size()>0){
			throw new IllegalStateException("This Node does not allow to add more than one child! Current child: "+Arrays.toString(nextNodes.toArray())+". You want to add "+e);
		}
		nextNodes.add(e);
	}
	

	void addPrevNode(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> e){
		checkMutable();
		if(!isAllowMutltipleEdges() && prevNodes.size()>0){
			throw new IllegalStateException("This Node does not allow to add more than one parent! Current parent: "+Arrays.toString(prevNodes.toArray())+". You want to add "+e);
		}
		prevNodes.add(e);
	}

	List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> getNextNodesEditable() {
		return nextNodes;
	}
	List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> getPrevNodesEditable() {
		return prevNodes;
	}

	public List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> getEdgesToChildren() {
		return immutableViewOnNextNodes;
	}
	public List<Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>>> getEdgesToParents() {
		return immutableViewOnPrevNodes;
	}

	// ids have to be unique because otherwise it's not possible to simply say timeCollector.getTime(milestone)
	GraphNode<ID_TYPE, PAYLOAD_TYPE> getChildWithId(ID_TYPE idToSearch) {
		
		// preconditions
		nn(idToSearch, "Makes no sense to search for a child with id NULL!");
		
//		if(idToSearch.equals(milestone))return this; this is not a child
		
		if(nextNodes==null)return null;
		
		for(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edgeToChildNode: nextNodes){
			GraphNode<ID_TYPE, PAYLOAD_TYPE> childNode = edgeToChildNode.getChildNode();
			if(idToSearch.equals(childNode.getId()))return childNode;
			GraphNode<ID_TYPE, PAYLOAD_TYPE> found = childNode.getChildWithId(idToSearch);
			if(found!=null)return found;
		}
		return null;
	}
	
	/**
	 * Does not copy the edges but only the node itself (id, payload, {@link EdgePermissions})!
	 * @return the new instance with the copied data
	 */
	GraphNode<ID_TYPE, PAYLOAD_TYPE> copy() {
		GraphNode<ID_TYPE, PAYLOAD_TYPE> n = new GraphNode<>(
				// TODO ensure immutable or copy them
				this.getId(), 
				this.getPayload(),
				this.getMutable(),
				this.isAllowMutltipleEdges()
			);
		if(!n.equals(this)){
			throw new IllegalStateException("?");
		}
		if(n==this){
			throw new IllegalStateException("????");
		}
		return n; 
	}
	

	@Override
	public String toString() {
		return getClass().getSimpleName()+" [" + getId() + ", " + getPayload().getClass().getSimpleName() + ", " + getMutable() + "]";
	}
	
	
}
