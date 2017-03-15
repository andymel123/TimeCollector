package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.*;

class Edge<Node_Type> {

	private final Node_Type node1;
	private final Node_Type node2;
	
	public Edge(Node_Type node1, Node_Type node2) {
		nn(node1, "'node1' is null");
		nn(node2, "'node2' is null");
		this.node1 = node1;
		this.node2 = node2;
	}

	Node_Type getNode1() {
		return node1;
	}
	
	Node_Type getNode2() {
		return node2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node1 == null) ? 0 : node1.hashCode());
		result = prime * result + ((node2 == null) ? 0 : node2.hashCode());
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
		if (node1 == null) {
			if (other.node1 != null)
				return false;
		} else if (!node1.equals(other.node1))
			return false;
		if (node2 == null) {
			if (other.node2 != null)
				return false;
		} else if (!node2.equals(other.node2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+" [node1=" + node1 + ", node2=" + node2 + "]";
	}
	
	
}
