package eu.andymel.timecollector.graphs;

public class PermissionNode<ID_TYPE> extends GraphNode<ID_TYPE, NodePermissions> {

	private PermissionNode(ID_TYPE id, NodePermissions payload, Mutable mutable) {
		super(id, payload, mutable, true);
	}

	public static <ID_TYPE> PermissionNode<ID_TYPE> create(ID_TYPE id, NodePermissions payload, Mutable mutable){
		return new PermissionNode<ID_TYPE>(id, payload, mutable);
	}
	public static <ID_TYPE> PermissionNode<ID_TYPE> create(ID_TYPE id, NodePermissions payload){
		return create(id, payload, null);
	}
	public static <ID_TYPE> PermissionNode<ID_TYPE> create(ID_TYPE id){
		return create(id, NodePermissions.REQUIRED_AND_SINGLESET);
	}
	
	@Override
	public String toString() {
		return "PNode@"+System.identityHashCode(this)
		+"["
			+getId()+", "
			+hashCode()
		+"]";
	}
	
	@Override
	public PermissionNode<ID_TYPE> copy() {
		return new PermissionNode<ID_TYPE>(getId(), getPayload(), getMutable());
	}

}
