package eu.andymel.timecollector.path;
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
	private List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> prevNodes;
	private List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> nextNodes;

	/**
	 * if true no prev or next nodes may be added anymore. Only after linking is finished
	 * getPrevNodes and getNextNodes may be called. Those return an unmodifiable view on
	 * those lists of neighbor nodes
	 */
	private boolean finishedLinking = false;
	
	/** after you have called this no other nodes can be added as 
	 * previous or next nodes */
	void setLinkingFinished(){
		this.finishedLinking = true;
		
		/* I throw away the modifiable view on the data to ensure I don't 
		 * change them anymore */
		if(prevNodes!=null){
			prevNodes = Collections.unmodifiableList(prevNodes);	
		}
		if(nextNodes!=null){
			nextNodes = Collections.unmodifiableList(nextNodes);	
		}
	}

	GraphNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		
		// preconditions
		nn(id, "'id' is null!");
		nn(payload, "'payload' is null!");
		
		this.id = id;
		this.payload= payload;
		
	}


	ID_TYPE getId() {
		return id;
	}
	
	void addNextNode(GraphNode<ID_TYPE, PAYLOAD_TYPE> n){
		if(finishedLinking){
			throw new IllegalStateException("This node has already been finished linking!");
		}
		if(nextNodes==null)nextNodes = new LinkedList<>();
		nextNodes.add(n);
	}
	
	void addPrevNode(GraphNode<ID_TYPE, PAYLOAD_TYPE> n){
		if(finishedLinking){
			throw new IllegalStateException("This node has already been finished linking!");
		}
		if(prevNodes==null)prevNodes = new LinkedList<>();
		prevNodes.add(n);
	}
	
	List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getNextNodes() {
		if(!finishedLinking){
			throw new IllegalStateException("You have to call setLinkingFinished() before you can retrieve the nodes!");
		}
		return nextNodes;
	}
	List<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getPrevNodes() {
		if(!finishedLinking){
			throw new IllegalStateException("You have to call setLinkingFinished() before you can retrieve the nodes!");
		}
		return prevNodes;
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
