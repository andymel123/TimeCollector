package eu.andymel.timecollector.path;

public class NodePermissions {

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
