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
}
