package eu.andymel.timecollector.report;

import java.time.Clock;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

/**
 * This holds data about every single {@link TimeCollector} added.
 * 
 * @author andymatic
 */
public class AnalyzerEach<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>>{

	private static final Logger LOG = LoggerFactory.getLogger(AnalyzerEach.class);
	
	private static final int MAX_NUMBER_OF_COLLECTED_PATHS = 1000;			
	
	private final HashMap<Integer, AnalyzerEachRecPathData<ID_TYPE>> dataOfDifferentPaths;
	private final Clock clock;
	
	/**
	 * @param clock the clock to use to retrieve the time a timecollector was added to this analyzer
	 */
	private AnalyzerEach(Clock clock) {
		this.dataOfDifferentPaths = new HashMap<>();
		this.clock = clock;
	}

	public static <ID_TYPE> AnalyzerEach<ID_TYPE> create(Clock clock){
		return new AnalyzerEach<>(clock);
	}
	
	@Override
	public void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		List<List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>>> recordedPaths = tc.getRecordedPaths();
		if(recordedPaths.size()==0)return;
		if(recordedPaths.size()>1){
			LOG.warn("Not able to add multiple possible paths of a timecollector to "+this+". Using the first path.");
		}
		
		List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> path = recordedPaths.get(0);
		Integer hashOfPath = Integer.valueOf(hashOfPath(path));

		// get data collector for this kind of path or generate a new such container
		AnalyzerEachRecPathData<ID_TYPE> pathData = dataOfDifferentPaths.computeIfAbsent(hashOfPath, hash->new AnalyzerEachRecPathData<ID_TYPE>(tc.getAllowedGraph(), path, hash, MAX_NUMBER_OF_COLLECTED_PATHS));
		pathData.addTimes(path, this.clock);
	}
	
	/**
	 * Calculates a hashCode simply by using the System.identityHashCode of the nodes in this path.
	 * So the returned hashcode should only be equal for exactly a list of the same node instances 
	 * in the same order
	 * @param recPath the list of {@link GraphNode}/{@link Instant} pairs
	 * @return
	 */
	private int hashOfPath(List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> recPath){
		int hashCode = 1;
        for (SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant> entry : recPath){
        	GraphNode<ID_TYPE, NodePermissions> node = entry.getKey();
            hashCode = 31*hashCode + (node==null ? 0 : System.identityHashCode(node));
        }
        return hashCode;
	}

	Collection<AnalyzerEachEntry> getAll(){
		return Collections.unmodifiableCollection(dataOfDifferentPaths.values());
	}
	
}
