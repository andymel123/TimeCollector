package eu.andymel.timecollector.path;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.LinkedList;
import java.util.List;

/**
 * A single Node on the path
 * 
 * @author andymatic
 *
 */
class PathNode<ID_TYPE, PAYLOAD_TYPE> {

	private PAYLOAD_TYPE payload;
	private List<PathNode<ID_TYPE, PAYLOAD_TYPE>> possiblePrev;
	private List<PathNode<ID_TYPE, PAYLOAD_TYPE>> possibleNext;
	
	private final ID_TYPE id;
	
	
//	PathNode(ID_TYPE id) {
//		
//		// preconditions
//		nn(id, "'id' is null!");
//		this.id = id;
//
//	}

	PathNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		
		// preconditions
		nn(id, "'id' is null!");
		nn(payload, "'payload' is null!");
		
		this.id = id;
		this.payload= payload;
		
	}


	ID_TYPE getId() {
		return id;
	}
	
	void addNextNode(PathNode<ID_TYPE, PAYLOAD_TYPE> n){
		if(possibleNext==null)possibleNext = new LinkedList<>();
		possibleNext.add(n);
	}
	
	void addPrevNode(PathNode<ID_TYPE, PAYLOAD_TYPE> n){
		if(possiblePrev==null)possiblePrev = new LinkedList<>();
		possiblePrev.add(n);
	}
	
	PathNode<ID_TYPE, PAYLOAD_TYPE> getChildWithId(ID_TYPE idToSearch) {
		
		// preconditions
		nn(idToSearch, "Makes no sense to search for a child with id NULL!");
		
//		if(idToSearch.equals(milestone))return this; this is not a child
		
		if(possibleNext==null)return null;
		
		for(PathNode<ID_TYPE, PAYLOAD_TYPE> childNode: possibleNext){
			if(idToSearch.equals(childNode.getId()))return childNode;
			PathNode<ID_TYPE, PAYLOAD_TYPE> found = childNode.getChildWithId(idToSearch);
			if(found!=null)return found;
		}
		return null;
	}
	
	PAYLOAD_TYPE getPayload() {
		return payload;
	}
	
	
	void setPayload(PAYLOAD_TYPE payload) {
		if(payload==null){
			throw new IllegalArgumentException("You can't set null as payload!");
		}
		this.payload = payload;
	}
	


}
