package eu.andymel.timecollector.graphs;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.Arrays;
import java.util.List;

/**
 *  
 * NOT THREADSAFE!
 * 
 * @author andymatic
 *
 * @param <ID_TYPE>
 */
public class AllowedPathBuilder<ID_TYPE> {

	/* > SINGLESET 
	 * means that the time may only be set once, trying to set the time on the same node again throws an exception
	 * > REQUIRED 
	 * means that the time of a node has to be set before a child not can be set */
	private static final NodePermissions NO_CHECKS = NodePermissions.create(false, false);
	private static final NodePermissions NOT_REQUIRED_BUT_SINGLESET = NodePermissions.create(false, true);
	private static final NodePermissions REQUIRED_AND_SINGLESET = NodePermissions.create(true, true);
	
	private final AllowedPathsGraph<ID_TYPE> allowedPath;
	
	/** to prevent changes on the path after it has been built */
	private boolean pathHasBeenBuilt = false;
	
	private final boolean isSubpath;
	
	public AllowedPathBuilder(ID_TYPE idOfStartNode, NodePermissions nodePermissions, boolean isSubPath) {
		nn(idOfStartNode, "'idOfStartNode' is null!");
		this.allowedPath = new AllowedPathsGraph<ID_TYPE>(idOfStartNode, nodePermissions);
		this.isSubpath = isSubPath;
	}

	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> start(ID_TYPE id){
		return start(id, REQUIRED_AND_SINGLESET);
	}
	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> start(ID_TYPE id, NodePermissions nodePermissions){
		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions, false);
	}

	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> startSubpath(ID_TYPE id){
		return startSubpath(id, REQUIRED_AND_SINGLESET);
	}
	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> startSubpath(ID_TYPE id, NodePermissions nodePermissions){
		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions, true);
	}

	public AllowedPathBuilder<ID_TYPE> then(ID_TYPE m){
		return then(m, REQUIRED_AND_SINGLESET);
	}
	public AllowedPathBuilder<ID_TYPE> thenMaybe(ID_TYPE m){
		return then(m, NOT_REQUIRED_BUT_SINGLESET);
	}
	public AllowedPathBuilder<ID_TYPE> then(ID_TYPE id, NodePermissions nodePermissions){
		
		// preconditions
		checkMutable();

		this.allowedPath.addNode(id, nodePermissions);
		
		return this;
	}

	public AllowedPathBuilder<ID_TYPE> thenEither(Graph<ID_TYPE, NodePermissions>... anyOfThoseSubPaths) {
		return thenEither(Arrays.asList(anyOfThoseSubPaths));
	}
	public AllowedPathBuilder<ID_TYPE> thenEither(List<Graph<ID_TYPE, NodePermissions>> anyOfThoseSubPaths) {
		
		// preconditions
		checkMutable();
		
		this.allowedPath.addParallel(anyOfThoseSubPaths);
		
		return this;
		
	}


	public AllowedPathsGraph<ID_TYPE> build() {
		if(pathHasBeenBuilt){
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
		
		pathHasBeenBuilt = true;
		if(!isSubpath){
			/* TODO this is not very nice...refactor
			 * a subpath is linked to the main path after build was called on it...
			 * so it can't be closed for linking earlier than the 
			 * build() call of the main path, for now I simply don't call finish 
			 * on subpaths ... at least not here in build()*/
			allowedPath.setFinishLinking();
		}
		return allowedPath;
	}

	private final void checkMutable(){
		if(pathHasBeenBuilt){
			throw new IllegalStateException("Path has already been built! You can not change it threw its builder anymore!");
		}
	}
	

}
