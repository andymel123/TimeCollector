package eu.andymel.timecollector.graphs;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A single Node on the path
 * 
 * @author andymatic
 *
 */
public class GraphNode<ID_TYPE, PAYLOAD_TYPE> {

	private final ID_TYPE id;

	private PAYLOAD_TYPE payload;
	private final List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> prevNodes = new LinkedList<>();
	private final List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> nextNodes = new LinkedList<>();
	
	private final List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> immutableViewOnPrevNodes = Collections.unmodifiableList(prevNodes);
	private final List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> immutableViewOnNextNodes = Collections.unmodifiableList(nextNodes);
	
	private Mutable mutable;
	
	GraphNode(ID_TYPE id, PAYLOAD_TYPE payload, Mutable mutable) {
		
		// preconditions
		nn(id, "'id' is null!");
		nn(payload, "'payload' is null!");
		
		this.id = id;
		this.payload= payload;
		this.mutable = mutable;
		
	}

	GraphNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		this(id, payload, null);
	}
	
	void setMutable(Mutable mutable) {
		this.mutable = mutable;
	}
	
	public ID_TYPE getId() {
		return id;
	}
	
	void addNextNode(GraphNode<ID_TYPE, PAYLOAD_TYPE> n){
		checkMutable();
		nextNodes.add(n);
	}
	
	private void checkMutable() {
		if(mutable==null){
			throw new IllegalStateException("When you try to change a node a Mutable instance has to be in place!");
		}
		mutable.check();
	}

	void addPrevNode(GraphNode<ID_TYPE, PAYLOAD_TYPE> n){
		checkMutable();
		prevNodes.add(n);
	}

	List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getNextNodesEditable() {
		return nextNodes;
	}
	List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getPrevNodesEditable() {
		return prevNodes;
	}

	public List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getNextNodes() {
		return immutableViewOnNextNodes;
	}
	public List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getPrevNodes() {
		return immutableViewOnPrevNodes;
	}

	// ids have to be unique because otherwise it's not possible to simply say timeCollector.getTime(milestone)
	GraphNode<ID_TYPE, PAYLOAD_TYPE> getChildWithId(ID_TYPE idToSearch) {
		
		// preconditions
		nn(idToSearch, "Makes no sense to search for a child with id NULL!");
		
//		if(idToSearch.equals(milestone))return this; this is not a child
		
		if(nextNodes==null)return null;
		
		for(GraphNode<ID_TYPE, PAYLOAD_TYPE> childNode: nextNodes){
			if(idToSearch.equals(childNode.getId()))return childNode;
			GraphNode<ID_TYPE, PAYLOAD_TYPE> found = childNode.getChildWithId(idToSearch);
			if(found!=null)return found;
		}
		return null;
	}
	
	public PAYLOAD_TYPE getPayload() {
		return payload;
	}
	
	
	void setPayload(PAYLOAD_TYPE payload) {
		if(payload==null){
			throw new IllegalArgumentException("You can't set null as payload!");
		}
		this.payload = payload;
	}


	public Mutable getMutable() {
		return this.mutable;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+" [" + id + ", " + payload.getClass().getSimpleName() + ", " + mutable + "]";
	}
	
	
}
