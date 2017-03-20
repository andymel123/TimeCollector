package eu.andymel.timecollector.graphs;

public interface Mutable {

	public static final Mutable ALWAYS_MUTABLE = new Mutable() {
		@Override
		public boolean isMutable() {
			return true;
		}
		
		@Override
		public String toString(){
			return "mutable";
		};
	};
	
	boolean isMutable();
	
	default void check(){
		if(!isMutable()){
			throw new IllegalStateException("It's not allowed to change this graph anymore!");
		}
	}
	
}
