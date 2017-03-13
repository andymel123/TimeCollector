package eu.andymel.timecollector.graphs;

public class Path<ID_TYPE, PAYLOAD_TYPE> extends Graph<ID_TYPE, PAYLOAD_TYPE> {

	public Path(GraphNode<ID_TYPE, PAYLOAD_TYPE> startNode) {
		super(startNode, false);
	}
	public Path(ID_TYPE id, PAYLOAD_TYPE payload) {
		super(
			id, 
			payload, 
			false // no circles or multi edges allowed
		);
	}
	
	
}
