package eu.andymel.timecollector.report.analyzer;

import java.time.Clock;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

/**
 * This holds data about the last x {@link TimeCollector}s added. Not only average data but data
 * about every single {@link TimeCollector}.
 * 
 * @author andymatic
 */
public class AnalyzerEachPath<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>>{

	private static final Logger LOG = LoggerFactory.getLogger(AnalyzerEachPath.class);
	
	private static final int MAX_NUMBER_OF_COLLECTED_PATHS = 100;			
	
	/** 
	 * A {@link HashMap} that hols {@link HashMap}s
	 * The outer Hashmap maps one HashMap to each {@link AllowedPathsGraph}
	 * In the inner Hashmap a {@link AnalyzerEachPathData} object is mapped to the hash of recorded paths.
	 * All findings of the timestamps that went the exact same way through the code are saved in that
	 * {@link AnalyzerEachPathData} 
	 */
	private final HashMap<AllowedPathsGraph<ID_TYPE>, HashMap<Integer, RecordedPathCollector<ID_TYPE>>> data;
	private final Clock clock;
	
	private volatile int countTimeCollectorsAdded = 0;

	private List<AnalyzerListener> listeners;
	private final int maxNumberCollectors;
	
	/**
	 * @param clock the clock to use to retrieve the time a timecollector was added to this analyzer
	 */
	private AnalyzerEachPath(Clock clock, int maxNumberCollectors) {
//		this.dataOfDifferentPaths = new HashMap<>();
		data = new HashMap<>();
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
			// think about how to display such information if it's not sure which was the real path
			LOG.warn("Not able to add multiple possible paths of "+tc+" to "+this+". Using the first path.");
		}
		
		List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> path = recordedPaths.get(0);
		Integer hashOfPath = Integer.valueOf(hashOfPath(path));

		// get the HashMap to collect data for this allowedGraph or generate a new hashmap
		HashMap<Integer, RecordedPathCollector<ID_TYPE>> dataOfDifferentPathsOfSameAllowedGraph = data.computeIfAbsent(
			tc.getAllowedGraph(), 
			allowedGraph -> new HashMap<>()
		);
		
		// get data collector for this kind of path or generate a new such container
		RecordedPathCollector<ID_TYPE> pathData = dataOfDifferentPathsOfSameAllowedGraph.computeIfAbsent(
			hashOfPath, 
			hash -> new AnalyzerEachPathData<ID_TYPE>(
				tc.getAllowedGraph(), 
				path, 
				hash, 
				maxNumberCollectors,
				this.clock
			)
		);
		pathData.addRecordedPath(path);
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

	public synchronized List<SimpleEntry<AllowedPathsGraph<ID_TYPE>, List<RecordedPathCollectorView<ID_TYPE>>>> getCopyOFData(){
		/* I copy the actual state of the data synchronized and return it so no 
		 * concurrent modifications can happen while the caller of this method 
		 * reads the returned data. I return it as a List, not as 
		 * HashMap as (at least for me at the moment) fast copy is more important 
		 * than finding some data for one specific entry */
		
		ArrayList<SimpleEntry<AllowedPathsGraph<ID_TYPE>, List<RecordedPathCollectorView<ID_TYPE>>>> result = new ArrayList<>(data.size());
		
		for(Entry<AllowedPathsGraph<ID_TYPE>, HashMap<Integer, RecordedPathCollector<ID_TYPE>>> e: data.entrySet()){
			result.add(new SimpleEntry<>(
				e.getKey(),	// no copy but the real graph (should be immutable) 
				new ArrayList<>(e.getValue().values()))	// copy if the inner list as well as it changes frequently
			);
		}
		return result;
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
