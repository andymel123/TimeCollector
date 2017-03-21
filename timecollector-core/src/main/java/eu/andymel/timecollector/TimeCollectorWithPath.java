package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.Path;
import eu.andymel.timecollector.graphs.PathRecorder;

public class TimeCollectorWithPath<MILESTONE_TYPE> implements TimeCollector<MILESTONE_TYPE> {

	/** The clock to use to save time stamps */
	private final Clock clock;
	
	private final PathRecorder<MILESTONE_TYPE, Instant> pathRecorder;
	
	private TimeCollectorWithPath(AllowedPathsGraph<MILESTONE_TYPE> allowedPath){
		this(Clock.systemDefaultZone(), allowedPath);
	}
	
	private TimeCollectorWithPath(Clock clock, AllowedPathsGraph<MILESTONE_TYPE> allowed) {
		nn(clock, "'clock' my not be null!");
		nn(allowed, "'path' my not be null!");
		this.clock = clock;
		this.pathRecorder = PathRecorder.create(allowed);
	}

	
	public static <MILESTONE_TYPE> TimeCollectorWithPath<MILESTONE_TYPE> createWithPath(AllowedPathsGraph<MILESTONE_TYPE> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(path);
	}
	public static <MILESTONE_TYPE> TimeCollectorWithPath<MILESTONE_TYPE> createWithPath(Clock clock, AllowedPathsGraph<MILESTONE_TYPE> path){
		return new TimeCollectorWithPath<MILESTONE_TYPE>(clock, path);
	}
	
	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public void saveTime(MILESTONE_TYPE m){
		// save current time for this milestone
		Instant now = clock.instant();
		pathRecorder.savePayload(m, now);
	}

	/**
	 * @return all possible paths (if there are multiple paths in the allowedGrap that fit)
	 */
	public List<Path<GraphNode<MILESTONE_TYPE, NodePermissions>, Instant>> getRecordedPaths() {
		return this.pathRecorder.getRecordedPaths();
	}
	
}
