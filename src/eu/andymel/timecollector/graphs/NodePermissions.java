package eu.andymel.timecollector.graphs;

import eu.andymel.timecollector.TimeCollector;

/**
 * Used to set permissions for {@link GraphNode}s on the allowedPath of my {@link TimeCollector} 
 * @author andymatic
 */
public class NodePermissions {

	/* > SINGLESET 
	 * means that the time may only be set once, trying to set the time on the same node again throws an exception
	 * > REQUIRED 
	 * means that the time of a node has to be set before a child not can be set */
	public static final NodePermissions NO_CHECKS = NodePermissions.create(false, false);
	public static final NodePermissions NOT_REQUIRED_BUT_SINGLESET = NodePermissions.create(false, true);
	public static final NodePermissions REQUIRED_AND_SINGLESET = NodePermissions.create(true, true);
	public static final NodePermissions REQUIRED_MULTISET = NodePermissions.create(true, true);

	
	/** A payload has to be set on this node before 
	 * something can be set on a later node on the path */
	private final boolean required;
	
	/** if true, an exception is thrown if setPayload is called more than once */
	private final boolean singleSet;


	/**
	 * @param required A payload has to be set on this node before something can be set on a later node on the path
	 * @param singleSet if true, an exception is thrown if setPayload is called more than once
	 * 
	 * @return a new instance of PathModelNodePayload holding the given flags
	 */
	public static NodePermissions create(boolean required, boolean singleSet) {
		return new NodePermissions(required, singleSet);
	}
	
	/**
	 * @param required A payload has to be set on this node before something can be set on a later node on the path
	 * @param singleSet if true, an exception is thrown if setPayload is called more than once
	 */
	private NodePermissions(boolean required, boolean singleSet) {
		this.required = required;
		this.singleSet = singleSet;
	}
	
	/**
	 * @return true if a payload has to be set on this node before something can be set on a later node on the path
	 */
	public boolean isRequired() {
		return required;
	}
	
	/**
	 * @return true if an exception should be thrown if setPayload is called more than once
	 */
	public boolean isSingleSet() {
		return singleSet;
	}

}
