package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
import java.time.Instant;

import eu.andymel.timecollector.path.NodePermissions;
import eu.andymel.timecollector.path.Path;

public class TimeCollectorWithPath<MILESTONE_TYPE> implements TimeCollector<MILESTONE_TYPE> {

	/** The clock to use to save time stamps */
	private final Clock clock;
	
	/**This path models which milestones are allowed in whic order. The path
	 * that is taken at runtime can be different but has to be a "part" of this one
	 * at least if the {@link NodePermissions} are set so. */
	private final Path<MILESTONE_TYPE, NodePermissions> allowedPath;
	
	/** This saves the real path that this time collector went through */
	private Path<MILESTONE_TYPE, Instant> recordedPath;
	
	private TimeCollectorWithPath(Path<MILESTONE_TYPE, NodePermissions> allowedPath){
		this(Clock.systemDefaultZone(), allowedPath);
	}
	
	public TimeCollectorWithPath(Clock clock, Path<MILESTONE_TYPE, NodePermissions> allowed) {
		nn(clock, "'clock' my not be null!");
		nn(allowed, "'path' my not be null!");
		
		this.clock = clock;
		
		// TODO copy path!
		this.allowedPath = allowed;
	}

	
	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollector<MILESTONE_TYPE> createWithPath(Path<MILESTONE_TYPE, NodePermissions> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(path);
	}
	
	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public void saveTime(MILESTONE_TYPE m){

		
		// TODO check if this Milestone is allowed now
		

		Instant now = clock.instant();
		if(recordedPath==null){
			recordedPath = new Path<>(m, now);
		}else{
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
