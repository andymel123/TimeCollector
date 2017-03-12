package eu.andymel.timecollector.path;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NOT THREADSAFE!
 * 
 * @author andymatic
 *
 * @param <ID_TYPE>
 */
public class AllowedPathBuilder<ID_TYPE> {

	private static final NodePermissions REQUIRED_AND_NO_MULTISET = NodePermissions.create(true, true);
	private static final NodePermissions NOT_REQUIRED_BUT_NO_MULTISET = NodePermissions.create(true, true);
	
	private final Path<ID_TYPE, NodePermissions> path;
	
	/** to prevent changes on the path after it has been built */
	private boolean pathHasBeenBuilt = false;
	
	public AllowedPathBuilder(ID_TYPE idOfStartNode, NodePermissions nodePermissions) {
		nn(idOfStartNode, ()->"'idOfStartNode' is null!");
		this.path = new Path<>(idOfStartNode, nodePermissions);
	}

	public final static <ID_TYPE extends Enum<ID_TYPE>> AllowedPathBuilder<ID_TYPE> start(ID_TYPE id){
		return start(id, REQUIRED_AND_NO_MULTISET);
	}
	
	public final static <ID_TYPE extends Enum<ID_TYPE>>AllowedPathBuilder<ID_TYPE> start(ID_TYPE id, NodePermissions nodePermissions){
		return new AllowedPathBuilder<ID_TYPE>(id, nodePermissions);
	}

	public AllowedPathBuilder<ID_TYPE> then(ID_TYPE m){
		return then(m, REQUIRED_AND_NO_MULTISET);
	}
	public AllowedPathBuilder<ID_TYPE> thenMaybe(ID_TYPE m){
		return then(m, NOT_REQUIRED_BUT_NO_MULTISET);
	}
	public AllowedPathBuilder<ID_TYPE> then(ID_TYPE id, NodePermissions nodePermissions){
		
		// preconditions
		checkMutable();

		this.path.addNode(id, nodePermissions);
		
		return this;
	}

//	public PathBuilder<ID_TYPE, PAYLOAD_TYPE> thenEither(ID_TYPE... anyOfThose) {
//		return thenEither(Arrays.asList(anyOfThose));
//	}
//
//	public PathBuilder<ID_TYPE, PAYLOAD_TYPE> thenEither(List<ID_TYPE> anyOfThose) {
//		
//		// preconditions
//		checkMutable();
//		ne(anyOfThose, ()->"thenEither needs milestiones!");
//		
//		// add all new milestones as next nodes to all last nodes
//		List<PathNode<ID_TYPE, PathModelNodePayload>> lastNodesOfBuiltPath = path.getLastNodes();
//		List<PathNode<ID_TYPE, PathModelNodePayload>> newNodes = new ArrayList<>(anyOfThose.size());
//		for(ID_TYPE m: anyOfThose){
//			PathNode<ID_TYPE, PathModelNodePayload> newNode = new PathNode<ID_TYPE, PathModelNodePayload>(m, false);
//			newNodes.add(newNode);
//			for(PathNode<ID_TYPE, PathModelNodePayload> lastnode: lastNodesOfBuiltPath){
//				lastnode.addNextNode(newNode);
//			}
//		}
//
//		// set the lastAddedNodes reference on the new nodes
//		lastNodesOfBuiltPath.clear();
//		lastNodesOfBuiltPath.addAll(newNodes);
//		
//		return this;
//	}

	
	@SuppressWarnings("varargs")
	public AllowedPathBuilder<ID_TYPE> thenEither(Path<ID_TYPE, NodePermissions>... anyOfThoseSubPaths) {
		return thenEither(Arrays.asList(anyOfThoseSubPaths));
	}
	public AllowedPathBuilder<ID_TYPE> thenEither(List<Path<ID_TYPE, NodePermissions>> anyOfThoseSubPaths) {
		
		// preconditions
		checkMutable();
		
		this.path.addParallel(anyOfThoseSubPaths);
		
		return this;
		
	}


	public Path<ID_TYPE, NodePermissions> build() {
		if(pathHasBeenBuilt){
			throw new IllegalStateException("You have already build this path! Use the previously returned instance.");
		}
		
		/* 
		 * Normally the builer only really instantiates the object it builds in the build() method.
		 * As this would be more difficult here I already instantiate the path object when the builder is constructed.
		 * 
		 * Problem is someone could build a path object
		 * then call methods on the builder and he would change the previously built object
		 * 
		 * For now I added a flag to prevent changes after calling build() but this is error-prone
		 * 
		 * TODO return a copy of the path! That way someone could change tha path in the builder and
		 * retrieve an instance of the path with build(), then add something to the path and retrieve a new
		 * path instance, both could work. THe problem, if something in the path wuld be mutable or not copied
		 * the two path instances would be connected! Thats Error-prone again so I maybe stay with the flag.
		 * Or do both, copy and flag  
		 */
		
		pathHasBeenBuilt = true;
		return path;
	}

	private final void checkMutable(){
		if(pathHasBeenBuilt){
			throw new IllegalStateException("Path has already been built! You can not change it threw its builder anymore!");
		}
	}
	

}
