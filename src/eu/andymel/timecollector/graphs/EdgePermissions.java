package eu.andymel.timecollector.graphs;

class EdgePermissions {

	/* I put it to -1 because maybe someone wants to use it to 
	 * prevent passing an edge totally with setting it to 0 */
	private int max = -1; 
	
	private EdgePermissions() {}
	
	public static EdgePermissions max(int max) {
		EdgePermissions ep = new EdgePermissions();
		ep.setMax(max);
		return ep;
	}

	private void setMax(int max) {
		this.max = max;
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
