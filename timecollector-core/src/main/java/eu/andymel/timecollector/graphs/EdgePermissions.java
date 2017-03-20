package eu.andymel.timecollector.graphs;

class EdgePermissions {

	private final int max; 
	
	private EdgePermissions(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max needs to be >= 0! You gave me "+max+"!");
		}
		this.max = max;
	}
	
	public static EdgePermissions max(int max) {
		EdgePermissions ep = new EdgePermissions(max);
		return ep;
	}

	/**
	 * @return max passes on this edge. -1 means no maximum
	 */
	public int getMax() {
		return max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + max;
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
		EdgePermissions other = (EdgePermissions) obj;
		if (max != other.max)
			return false;
		return true;
	}
	
	
	
}
