package eu.andymel.timecollector.graphs;

public class RecordedNode<ID_TYPE, PAYLOAD_TYPE> extends PathNode<GraphNode<ID_TYPE, NodePermissions>, PAYLOAD_TYPE> {

	public RecordedNode(GraphNode<ID_TYPE, NodePermissions> id, PAYLOAD_TYPE payload) {
		super(id, payload);
	}

	public RecordedNode(GraphNode<ID_TYPE, NodePermissions> id, PAYLOAD_TYPE payload, Mutable mutable) {
		super(id, payload, mutable);
	}

}
