package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
import java.time.Instant;

import eu.andymel.timecollector.path.AllowedPathsGraph;
import eu.andymel.timecollector.path.NodePermissions;
import eu.andymel.timecollector.path.Path;
import eu.andymel.timecollector.path.Graph;
import eu.andymel.timecollector.path.GraphNode;

public class TimeCollectorWithPath<MILESTONE_TYPE> implements TimeCollector<MILESTONE_TYPE> {

	/** The clock to use to save time stamps */
	private final Clock clock;
	
	/**This path models which milestones are allowed in whic order. The path
	 * that is taken at runtime can be different but has to be a "part" of this one
	 * at least if the {@link NodePermissions} are set so. */
	private final AllowedPathsGraph<MILESTONE_TYPE> allowedPath;
	
	/** This saves the real path that this time collector went through */
	private Path<MILESTONE_TYPE, Instant> recordedPath;
	
	private GraphNode<MILESTONE_TYPE, Instant> lastRecordedNode;
	
	private TimeCollectorWithPath(AllowedPathsGraph<MILESTONE_TYPE> allowedPath){
		this(Clock.systemDefaultZone(), allowedPath);
	}
	
	private TimeCollectorWithPath(Clock clock, AllowedPathsGraph<MILESTONE_TYPE> allowed) {
		nn(clock, "'clock' my not be null!");
		nn(allowed, "'path' my not be null!");
		
		this.clock = clock;
		
		// TODO copy path!
		this.allowedPath = allowed;
	}

	
//	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollector<MILESTONE_TYPE> createWithPath(Path<MILESTONE_TYPE, NodePermissions> path){
	public static <MILESTONE_TYPE> TimeCollector<MILESTONE_TYPE> createWithPath(AllowedPathsGraph<MILESTONE_TYPE> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(path);
	}
	
	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public void saveTime(MILESTONE_TYPE m){

//		// check if it's currently allowed to save a time for the given milestone
//		if(lastRecordedNode!=null){
//			lastRecordedNode = allowedPath.checkIfAllRequiredNodesAreSetTill(lastRecordedNode, m);	
//		} else {
//			lastRecordedNode = allowedPath.checkIfThisMilestoneCanBeFirst(m);
//		}

		// save current time for this milestone
		Instant now = clock.instant();
		if(recordedPath==null){
			
			// throws exception if this milestone is not allowed as first milestone
			allowedPath.checkPath(null, m); 

			// if the path is ok...
			recordedPath = new Path<>(m, now);
			
		}else{
			allowedPath.checkPath(recordedPath, m);
			recordedPath.addNode(m, now);
		}
		
	}


	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#getTime(MILESTONE_TYPE)
	 */
	@Override
	public Instant getTime(MILESTONE_TYPE m){
		return this.recordedPath.getPayload(m);
	}

}
