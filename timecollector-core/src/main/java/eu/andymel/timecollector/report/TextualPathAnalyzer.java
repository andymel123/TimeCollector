package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.ne;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public class TextualPathAnalyzer<ID_TYPE> extends AbstractTextualAnalyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> {

	public TextualPathAnalyzer() {
		super();
	}

	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		
		nn(tc, "TimeCollector is null!");
		
		List<List<GraphNode<GraphNode<ID_TYPE, NodePermissions>, Instant>>> paths = tc.getRecordedPaths();

		ne(paths, "TimeCollector does not contain recorded paths!");
		
		List<GraphNode<GraphNode<ID_TYPE, NodePermissions>, Instant>> path = paths.get(0);
		
		Iterator<GraphNode<GraphNode<ID_TYPE, NodePermissions>, Instant>> it = path.iterator();
		
		Instant lastInstant = null;
		String lastName = null;
		while(it.hasNext()){
			GraphNode<GraphNode<ID_TYPE, NodePermissions>, Instant> node = it.next();
			GraphNode<ID_TYPE, NodePermissions> nodeFromAllowedGraph = node.getId();
			Instant recordedInstant = node.getPayload();
			String milestoneName = nodeFromAllowedGraph.getId().toString();
			if(lastInstant!=null){
				addTimes(lastName, milestoneName, lastInstant, recordedInstant);
			}
			lastInstant = recordedInstant;
			lastName = milestoneName;
		}
		
	}
	
}
