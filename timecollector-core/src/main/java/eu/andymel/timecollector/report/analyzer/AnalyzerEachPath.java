package eu.andymel.timecollector.report.analyzer;

import java.time.Clock;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

/**
 * This holds data about every single {@link TimeCollector} added.
 * 
 * @author andymatic
 */
public class AnalyzerEachPath<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>>{

	private static final Logger LOG = LoggerFactory.getLogger(AnalyzerEachPath.class);
	
	private static final int MAX_NUMBER_OF_COLLECTED_PATHS = 100;			
	
	private final HashMap<Integer, AnalyzerEachPathData<ID_TYPE>> dataOfDifferentPaths;
	private final Clock clock;
	
	private volatile int countTimeCollectorsAdded = 0;

	private List<AnalyzerListener> listeners;
	private final int maxNumberCollectors;
	
	/**
	 * @param clock the clock to use to retrieve the time a timecollector was added to this analyzer
	 */
	private AnalyzerEachPath(Clock clock, int maxNumberCollectors) {
		this.dataOfDifferentPaths = new HashMap<>();
		this.clock = clock;
		this.maxNumberCollectors = maxNumberCollectors;
	}

	/**
	 * @param clock to get the time when the {@link TimeCollector} is added to the analyzer
	 * @return
	 */
	public static <ID_TYPE> AnalyzerEachPath<ID_TYPE> create(Clock clock){
		return new AnalyzerEachPath<>(clock, MAX_NUMBER_OF_COLLECTED_PATHS);
	}
	public static <ID_TYPE> AnalyzerEachPath<ID_TYPE> create(Clock clock, int maxNumberCollectors){
		return new AnalyzerEachPath<>(clock, maxNumberCollectors);
	}
	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		List<List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>>> recordedPaths = tc.getRecordedPaths();
		if(recordedPaths.size()==0)return;
		if(recordedPaths.size()>1){
			LOG.warn("Not able to add multiple possible paths of a timecollector to "+this+". Using the first path.");
		}
		
		List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> path = recordedPaths.get(0);
		Integer hashOfPath = Integer.valueOf(hashOfPath(path));

		// get data collector for this kind of path or generate a new such container
		AnalyzerEachPathData<ID_TYPE> pathData = dataOfDifferentPaths.computeIfAbsent(hashOfPath, hash->new AnalyzerEachPathData<ID_TYPE>(tc.getAllowedGraph(), path, hash, maxNumberCollectors));
		pathData.addTimes(path, this.clock);
		countTimeCollectorsAdded++;
		informListeners(tc);
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

	public Collection<AnalyzerEachEntry<ID_TYPE>> getAll(){
//		return Collections.unmodifiableCollection(dataOfDifferentPaths.values());
		/*
		 * I return a copy as the list can change very frequently
		 */
		return new ArrayList<>(dataOfDifferentPaths.values());
	}
	
	public interface AnalyzerEachEntry<ID_TYPE> {
		
		Integer getHashOfRecPath();
		
		List<GraphNode<ID_TYPE, NodePermissions>> getRecPath();
		
		List<long[]> getCollectedTimes();
		
		AllowedPathsGraph<ID_TYPE> getAllowedGraph();
		
	}

	@Override
	public long getNumberOfAddedTimeCollectors() {
		return countTimeCollectorsAdded;
	}

	@Override
	public synchronized void addListener(AnalyzerListener listener) {
		if(listeners == null){
			listeners = new ArrayList<>();
		}
		listeners.add(listener);
	}
	
	private void informListeners(TimeCollector<?> tc){
		if(listeners==null)return;
		listeners.forEach(l->l.timeCollectorAddedToAnalyzer(tc, this));
	}
}
