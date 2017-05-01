package eu.andymel.timecollector.report.analyzer;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.util.AvgMaxCalcLong;

/**
 * This holds the timespans for all {@link TimeCollector}s that collected 
 * times on exactly the same path. This object also holds info about this path
 * (references to the nodes of the allowedPath ordered by their invocation)
 * and a hash over those nodes for efficient comparison with future paths.
 * 
 * @author andymatic
 *
 * @param <ID_TYPE> the milestone type
 */
class AnalyzerEachPathData<ID_TYPE> implements RecordedPathCollectorView<ID_TYPE>, RecordedPathCollector<ID_TYPE>{

	private static final Logger LOG = LoggerFactory.getLogger(AnalyzerEachPathData.class);
	
	private final List<GraphNode<ID_TYPE, NodePermissions>> recPath;
	private final AllowedPathsGraph<ID_TYPE> allowedGraph;
	private final Integer hashOfRecPath;
	
	/** at idx 0 in those arrays the time when this timecollector was added is saved. In the 
	 * other indexes the timespan duration in nanoseconds is saved. */
	private final LinkedList<long[]> collectedSpans;
	private final AvgMaxCalcLong[] totalAvgTimes;
	
	
	private final int numberOfTimespans;
	private final String toStringValue;
	private final int maxNumberOfCollectedPaths;
	private final Clock clock;
	
	/**
	 * 
	 * @param allowedGraph
	 * @param recPath
	 * @param hashOfRecPath
	 * @param maxNumberOfCollectedPaths
	 * @param clock
	 */
	AnalyzerEachPathData(AllowedPathsGraph<ID_TYPE> allowedGraph, List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> recPath, Integer hashOfRecPath, int maxNumberOfCollectedPaths, Clock clock) {
		Objects.requireNonNull(allowedGraph, "'allowedGraph' is null!");
		Objects.requireNonNull(recPath, "'recPath' is null!");
		Objects.requireNonNull(hashOfRecPath, "'hashOfRecPath' is null!");
		if(maxNumberOfCollectedPaths<0){
			throw new IllegalArgumentException("not sane to add a maximum of "+maxNumberOfCollectedPaths+" elements to a collection!");
		}
		
		List<GraphNode<ID_TYPE, NodePermissions>> listWithoutInstant = recPath.stream().map(e->e.getKey()).collect(Collectors.toList());
		
		this.allowedGraph = allowedGraph;
		this.recPath = Collections.unmodifiableList(listWithoutInstant);
		this.hashOfRecPath = hashOfRecPath;
		this.numberOfTimespans = recPath.size()-1; // -1 as there are 2 timespans between 3 milestones
		this.toStringValue = getClass().getSimpleName()+"["+hashOfRecPath+", "+numberOfTimespans+" timeSpans]";
		this.maxNumberOfCollectedPaths = maxNumberOfCollectedPaths;
		this.clock = clock;
		
		// this list collects single request times
		this.collectedSpans = new LinkedList<>();
		
		// this array collects the total averages since start
		this.totalAvgTimes = new AvgMaxCalcLong[numberOfTimespans];
		for(int i=0; i<totalAvgTimes.length; i++){
			totalAvgTimes[i] = AvgMaxCalcLong.create();
		}
	}
	

	/**
	 * @param recPath the path to add
	 * @return the long[] of timespans from a previous timeCollector if maxNumberOfCollectedPaths is reached. For
	 * example if you set maxNumberOfCollectedPaths to 100 and you add timeCollector 101 the first is removed and 
	 * returned while the new is added. The long at idx 0 is the time the timeCollector was added to the Analyzer
	 */
	@Override
	public long[] addRecordedPath(List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> recPath){
		Objects.requireNonNull(recPath, "'recPath' is null!");
		int size = recPath.size();
		if(size==0)return null;
		if(numberOfTimespans!=size-1){	// -1 as there is the current time on idx 0
			throw new IllegalStateException("Can't add a rec path of size "+recPath.size()+" into "+this);
		}
		long[] times = new long[numberOfTimespans+1]; // +1 as I save the current time to idx 0
		
		times[0] = clock.millis();
		
		Iterator<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> it = recPath.iterator();
		if(!it.hasNext()){
			throw new IllegalStateException("recPath size > 0 but iterator has no elements?");
		}
		SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant> firstEntry = it.next();
		Instant lastInstant = firstEntry.getValue();
		int idx = 1; // begin at idx 1 as the current time is saved at idx 0
		while(it.hasNext()){
			SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant> entry = it.next();
			Instant instant = entry.getValue();
			times[idx++] = Duration.between(lastInstant, instant).toNanos();
			lastInstant = instant;
		}
		if(idx-1!=numberOfTimespans){
			throw new IllegalStateException("idx should be "+numberOfTimespans+ " now but is "+idx);
		}
		
		long[] spansRemovedToFreeASlot = null;
		synchronized (this) {
			// to prevent concurrent modification while copying this 
			// list in getCollectedTimes()
			// TODO read/write lock instead?!
			if(collectedSpans.size()==maxNumberOfCollectedPaths){
				spansRemovedToFreeASlot = collectedSpans.removeFirst();
			}
			collectedSpans.add(times);	
		}
		// add the new times to the AverageCalcuator that belongs to the timespan
		synchronized (totalAvgTimes) {
			for(int i=0; i<totalAvgTimes.length; i++){
				totalAvgTimes[i].add(times[i+1]);	// +1 as there is the current time on idx 0
			}
		}
		return spansRemovedToFreeASlot;
	}
	
	@Override
	public Integer getHashOfRecPath() {
		return hashOfRecPath;
	}
	
	@Override
	public List<GraphNode<ID_TYPE, NodePermissions>> getRecPath() {
		return recPath;
	}
	
	/**
	 * @return a shallow copy of the internal list of collected times. 
	 * Shallow: the arrays inside the list are not copied.
	 */
	@Override
	public synchronized List<long[]> getCollectedTimes() {
		return new ArrayList<>(collectedSpans);
	}


	@Override
	public AllowedPathsGraph<ID_TYPE> getAllowedGraph() {
		return allowedGraph;
	}


	@Override
	public long[] getTotalAvgTimes() {
		int entries = 3;	// 3 = min,avg,max
		long[] times = new long[totalAvgTimes.length * entries];

		int idx = 0;
		synchronized (totalAvgTimes) {
			for(int i=0; i<totalAvgTimes.length; i++){
				AvgMaxCalcLong avgCalc = totalAvgTimes[i];
				
				times[idx++] = avgCalc.getMin();
				times[idx++] = (long)avgCalc.getAvg();
				times[idx++] = avgCalc.getMax();
				
			}
			return times;
		}
	}


	
}
