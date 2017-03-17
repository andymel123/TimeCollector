package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import eu.andymel.timecollector.exceptions.EdgePermissionViolatedException;

/**
 * This class holds state to check if EdgePermissions are violated  
 * 
 * @author andymatic
 *
 */
public class EdgeState {

	private int count;
	private final Edge<GraphNode<?, ?>> edge;
	
	private EdgeState(Edge<GraphNode<?, ?>> edge) {

		// preconditions
		nn(edge.getEdgePermissions(), "It makes no sense to build an EdgeState for '"+edge+"' as it contains no EdgePermissions!");

		this.edge = edge;
	}
	
	public static EdgeState create(Edge<GraphNode<?, ?>> edge){
		return new EdgeState(edge);
	}
	
	/**
	 * Counts the visits internally and checks permissions.
	 * @param throwException if true, this method will throw an exception if a permission is violated
	 * @return 
	 */
	public boolean visit(boolean throwException){
		this.count++;
		return check(throwException);
	}

	public boolean check(boolean throwException) {

		int max = edge.getEdgePermissions().getMax();
		if(count>max){
			if(throwException){
				throw new EdgePermissionViolatedException("You wanted to go from "+edge.getParentNode()+" to "+edge.getChildNode()
				+" but this path can only be walked "+max+" times. You already did that!");
			}else{
				return false; 
			}
		}
		
		return true;
	}
	
}
