package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.ne;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public abstract class AbstractPathAnalyzer<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> {

	private int countTimeCollectorsAdded = 0;
	

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

	protected abstract void addTimes(
		GraphNode<ID_TYPE, NodePermissions> node1, 
		GraphNode<ID_TYPE, NodePermissions> node2, 
		Instant lastInstant, Instant instant
	);

	public int getNumberOfAddedTimeCollectors() {
		return countTimeCollectorsAdded;
	}


}
