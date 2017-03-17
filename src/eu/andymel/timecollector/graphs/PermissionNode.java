package eu.andymel.timecollector.graphs;

public class PermissionNode<ID_TYPE> extends GraphNode<ID_TYPE, NodePermissions> {

	PermissionNode(ID_TYPE id, NodePermissions payload) {
		super(id, payload, true);
	}

	public static <ID_TYPE> PermissionNode<ID_TYPE> create(ID_TYPE id, NodePermissions payload){
		return new PermissionNode<ID_TYPE>(id, payload);
	}
	
	@Override
	public String toString() {
		return "PNode["+getId()+", "+hashCode()+"]";
	}
	
	@Override
	public PermissionNode<ID_TYPE> copy() {
		return new PermissionNode<ID_TYPE>(getId(), getPayload());
	}
}
