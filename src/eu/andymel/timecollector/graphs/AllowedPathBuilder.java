package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  
 * NOT THREADSAFE!
 * 
 * @author andymatic
 *
 * @param <ID_TYPE>
 */
public class AllowedPathBuilder<ID_TYPE> {

	private final AllowedPathsGraph<ID_TYPE> allowedGraph;
	
	/** to prevent changes on the path after it has been built */
	private boolean pathIsFinished = false;
	
	private final boolean isSubpath;
	
	private Mutable mutable = new Mutable() {
		@Override
		public boolean isMutable() {
			// all nodes of the graph and the graph itself can ask this method if changing the graph is allowed.
			// if this method returns false all nodes and the graph should throw exceptions if something tries to change any edges
			return !pathIsFinished; 
		}
	};
	
	AllowedPathBuilder(ID_TYPE idOfStartNode, NodePermissions nodePermissions, boolean isSubPath) {
		nn(idOfStartNode, "'idOfStartNode' is null!");

		this.allowedGraph = new AllowedPathsGraph<ID_TYPE>(
			idOfStartNode, 
			nodePermissions,
			mutable
		);
		this.isSubpath = isSubPath;
	}


	public AllowedPathBuilder<ID_TYPE> then(ID_TYPE m){
		return then(m, AllowedPathsGraph.REQUIRED_AND_SINGLESET);
	}
	public AllowedPathBuilder<ID_TYPE> thenMaybe(ID_TYPE m){
		return then(m, AllowedPathsGraph.NOT_REQUIRED_BUT_SINGLESET);
	}
	public AllowedPathBuilder<ID_TYPE> then(ID_TYPE id, NodePermissions nodePermissions){
		
		// preconditions
		checkMutable();

		this.allowedGraph.addNode(id, nodePermissions);
		
		return this;
	}

//	public AllowedPathBuilder<ID_TYPE> thenEither(Graph<ID_TYPE, NodePermissions>... anyOfThoseSubPaths) {
//		return thenEither(Arrays.asList(anyOfThoseSubPaths));
//	}
//	public AllowedPathBuilder<ID_TYPE> thenEither(List<Graph<ID_TYPE, NodePermissions>> anyOfThoseSubPaths) {
//	
//	// preconditions
//	checkMutable();
//	
//	this.allowedPath.addParallel(anyOfThoseSubPaths);
//	
//	return this;
//	
//}
	
	public AllowedPathBuilder<ID_TYPE> thenEither(AllowedPathBuilder<ID_TYPE>... anyOfThoseSubPaths) {
		return thenEither(Arrays.asList(anyOfThoseSubPaths));
	}
	public AllowedPathBuilder<ID_TYPE> thenEither(List<AllowedPathBuilder<ID_TYPE>> builders) {
		
		// preconditions
		checkMutable();
		
		/* highcheck the "mutable" of all subpath builders, so all
		 * subpaths listen to my own "mutable" */
		builders.stream().forEach(b->b.mutable = this.mutable);
		
		// get list of sub graphs from builders and add parallel to my main graph
		this.allowedGraph.addParallel(
			builders.stream()
			.map(b->b.allowedGraph)
			.collect(Collectors.toList())
		);
		
		return this;
		
	}
	


	public AllowedPathsGraph<ID_TYPE> build() {
		if(pathIsFinished){
			throw new IllegalStateException("You have already build this path! Use the previously returned instance.");
		}
		
		/* 
		 * Normally a builer only really instantiates the object it builds in the build() method.
		 * As this would be more difficult here I already instantiate the path object when the builder is constructed.
		 * 
		 * Problem is someone could build a path object
		 * then call methods on the builder and he would change the previously built object
		 * 
		 * For now I added a flag to prevent changes after calling build() but this is error-prone as I could forget to check it
		 * At the moment I check it in checkMutable()
		 * 
		 * TODO return a copy of the path! That way someone could change tha path in the builder and
		 * retrieve an instance of the path with build(), then add something to the path and retrieve a new
		 * path instance, both could work. THe problem, if something in the path wuld be mutable or not copied
		 * the two path instances would be connected! Thats Error-prone again so I maybe stay with the flag.
		 * Or do both, copy and flag  
		 */
		
		
		
		pathIsFinished = true;
		
//		commented out...instead of this I highcheck the "mutable" of all subpaths I'm adding
//		if(!isSubpath){
//			/* TODO this is not very nice...refactor!
//			 * A subpath is linked to the main path after build was called on it...
//			 * so it can't be closed for linking earlier than the 
//			 * build() call of the main path, for now I simply don't call finish 
//			 * on subpaths ... at least not here in build()*/
//			allowedGraph.setFinishLinking();
//		}
		return allowedGraph;
	}

	private final void checkMutable(){
		if(!mutable.isMutable()){
			throw new IllegalStateException("Path has already been built! You can not change it with its builder anymore!");
		}
	}
	

}
