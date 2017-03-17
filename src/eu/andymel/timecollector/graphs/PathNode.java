package eu.andymel.timecollector.graphs;

public class PathNode<ID_TYPE, PAYLOAD_TYPE> extends AbstractNode<ID_TYPE, PAYLOAD_TYPE>{

	private Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edgeToChild;
	private Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> edgeToParent;
	
	protected PathNode(ID_TYPE id, PAYLOAD_TYPE payload, Mutable mutable) {
		super(id, payload, mutable);
	}
	public PathNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		super(id, payload);
	}
	
	
	@Override
	void addNextNode(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> e) {
		if(edgeToChild!=null){
			throw new IllegalStateException(this+" already has an edge to a child! "+getClass().getSimpleName()+" only have max one child/parent node! The former added edge was "+edgeToChild+" you want to add "+e);
		}
		this.edgeToChild = e;
	}

	@Override
	void addPrevNode(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> e) {
		if(edgeToParent!=null){
			throw new IllegalStateException(this+" already has an edge to a parent! "+getClass().getSimpleName()+" only have max one child/parent node! The former added edge was "+edgeToParent+" you want to add "+e);
		}
		this.edgeToParent = e;
	}
	
	public Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getEdgeToChild() {
		return edgeToChild;
	}
	
	public Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> getEdgeToParent() {
		return edgeToParent;
	}
	
}
