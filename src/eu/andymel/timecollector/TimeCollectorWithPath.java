package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
import java.time.Instant;

import eu.andymel.timecollector.path.NodePermissions;
import eu.andymel.timecollector.path.Path;

public class TimeCollectorWithPath<MILESTONE_TYPE> implements TimeCollector<MILESTONE_TYPE> {

	private final Path<MILESTONE_TYPE, NodePermissions> allowedPath;
	private final Clock clock;
	
	private Path<MILESTONE_TYPE, Instant> recordedPath;
	
	public TimeCollectorWithPath(Clock clock, Path<MILESTONE_TYPE, NodePermissions> path) {
		nn(clock, ()->"'clock' my not be null!");
		nn(path, ()->"'path' my not be null!");
		
		this.clock = clock;
		
		// TODO copy path!
		this.allowedPath = path;
	}

	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollector<MILESTONE_TYPE> createWithPath(Clock clock, Path<MILESTONE_TYPE, NodePermissions> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(clock, path);
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
	public void getTime(MILESTONE_TYPE m){
		this.recordedPath.getPayload(m);
	}

}
