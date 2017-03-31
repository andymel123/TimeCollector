package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.ne;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.util.AvgMaxCalcLong;

public abstract class AbstractPathAnalyzer<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> {

	private int countTimeCollectorsAdded = 0;

	/** HashMap in HashMap as outer key is the first milestone, inner key the second */
	private IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> timesPerSpan;

	
	public AbstractPathAnalyzer() {
		/* LinkedHashMap to get insertion order, has same performance as HashMap in my measurements */
		timesPerSpan = new IdentityHashMap<>();
	}

	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		// TODO make async!
		
		nn(tc, "TimeCollector is null!");
		
		List<List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>>> paths = tc.getRecordedPaths();

		ne(paths, "TimeCollector does not contain recorded paths!");
		
		List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> path = paths.get(0);
		
		Iterator<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> it = path.iterator();
		
		Instant lastInstant = null;
		GraphNode<ID_TYPE, NodePermissions> lastNode = null;
		while(it.hasNext()){
			SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant> entry = it.next();
			GraphNode<ID_TYPE, NodePermissions> node = entry.getKey();
			Instant instant = entry.getValue();
			
			ID_TYPE milestone = node.getId();
			if(lastInstant!=null){
				addTimes(lastNode, node, lastInstant, instant);
			}
			lastInstant = instant;
			lastNode = node;
		}
		
		countTimeCollectorsAdded++;

	}

	protected void addTimes(
			GraphNode<ID_TYPE, NodePermissions> node1, 
			GraphNode<ID_TYPE, NodePermissions> node2,
			Temporal t1, Temporal t2
	) {
		IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> inner = timesPerSpan.get(node1);
		AvgMaxCalcLong calc = null;
		if(inner==null){
			inner = new IdentityHashMap<>();
			timesPerSpan.put(node1, inner);
		} else {
			calc = inner.get(node2);
		}
		if(calc==null){
			calc = AvgMaxCalcLong.create();
			inner.put(node2, calc);
		}
		
		calc.add(Duration.between(t1, t2).toNanos());
	}


	public int getNumberOfAddedTimeCollectors() {
		return countTimeCollectorsAdded;
	}

	public IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> getTimesPerSpan() {
		return timesPerSpan;
	}
	
}
