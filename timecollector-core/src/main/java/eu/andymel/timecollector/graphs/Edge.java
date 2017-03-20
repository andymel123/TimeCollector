package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

public class Edge<Node_Type> {

	private final Node_Type from;
	private final Node_Type to;
	private final EdgePermissions edgePermissions;
	
	private Edge(Node_Type node1, Node_Type node2, EdgePermissions edgePermissions) {
		nn(node1, "'node1' is null");
		nn(node2, "'node2' is null");
		this.from = node1;
		this.to = node2;
		this.edgePermissions = edgePermissions;
	}

	static <Node_Type> Edge<Node_Type> create(Node_Type node1, Node_Type node2){
		return create(node1, node2, null);
	}
	static <Node_Type> Edge<Node_Type> create(Node_Type node1, Node_Type node2, EdgePermissions edgePermissions){
		return new Edge<Node_Type>(node1, node2, edgePermissions);
	}
	
	public Node_Type getParentNode() {
		return from;
	}
	
	public Node_Type getChildNode() {
		return to;
	}

	/** @return the {@link EdgePermissions} for this {@link Edge} (can be <code>null</code> if there are no) */
	public EdgePermissions getEdgePermissions() {
		return edgePermissions;
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edgePermissions == null) ? 0 : edgePermissions.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		Edge other = (Edge) obj;
		if (edgePermissions == null) {
			if (other.edgePermissions != null)
				return false;
		} else if (!edgePermissions.equals(other.edgePermissions))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+" [node1=" + from + ", node2=" + to + "]";
	}
	
	
}
