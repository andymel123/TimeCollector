package eu.andymel.timecollector.graphs;

public class SimpleMutable implements Mutable{

	private boolean mutable;
	
	public SimpleMutable(boolean mutableAtStart) {
		this.mutable = mutableAtStart;
	}
	
	@Override
	public boolean isMutable() {
		return this.mutable;
	}

	void setImmutable(){
		this.mutable = false;
	}
	
	@Override
	public String toString() {
		return mutable?"mutable":"immutable";
	}
}
