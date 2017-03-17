package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

public abstract class AbstractNode<ID_TYPE, PAYLOAD_TYPE> {

	private final ID_TYPE id;
	private PAYLOAD_TYPE payload;
	private Mutable mutable;
	
	protected AbstractNode(ID_TYPE id, PAYLOAD_TYPE payload, Mutable mutable) {
		// preconditions
		nn(id, "'id' is null!");
		nn(payload, "'payload' is null!");
		
		this.id = id;
		this.payload= payload;
		this.mutable = mutable;
	}
	public AbstractNode(ID_TYPE id, PAYLOAD_TYPE payload) {
		this(id, payload, null);
	}
	
	abstract void addNextNode(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> e);
	abstract void addPrevNode(Edge<GraphNode<ID_TYPE, PAYLOAD_TYPE>> e);
	

	
	void setPayload(PAYLOAD_TYPE payload) {
		if(payload==null){
			throw new IllegalArgumentException("You can't set null as payload!");
		}
		this.payload = payload;
	}
	void setMutable(Mutable mutable) {
		this.mutable = mutable;
	}
	
	
	public ID_TYPE getId() {
		return id;
	}
	public PAYLOAD_TYPE getPayload() {
		return payload;
	}
	public Mutable getMutable() {
		return this.mutable;
	}
	
	
	protected void checkMutable() {
		if(mutable==null){
			throw new IllegalStateException("When you try to change a node a Mutable instance has to be in place!");
		}
		mutable.check();
	}

	
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mutable == null) ? 0 : mutable.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractNode))
			return false;
		AbstractNode other = (AbstractNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mutable == null) {
			if (other.mutable != null)
				return false;
		} else if (!mutable.equals(other.mutable))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		return true;
	}




}
