package eu.andymel.timecollector.graphs;

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
	
	
}
