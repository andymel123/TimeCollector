package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.ne;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public class TextualPathAnalyzer<ID_TYPE> extends AbstractTextualAnalyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> {

	private TextualPathAnalyzer() {
		super();
	}

	public static<ID_TYPE> TextualPathAnalyzer<ID_TYPE> create(){
		return new TextualPathAnalyzer<>();
	}
	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		
		nn(tc, "TimeCollector is null!");
		
		List<List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>>> paths = tc.getRecordedPaths();

		ne(paths, "TimeCollector does not contain recorded paths!");
		
		List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> path = paths.get(0);
		
		Iterator<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> it = path.iterator();
		
		Instant lastInstant = null;
		ID_TYPE lastMilestone = null;
		while(it.hasNext()){
			SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant> node = it.next();
			GraphNode<ID_TYPE, NodePermissions> nodeFromAllowedGraph = node.getKey();
			Instant recordedInstant = node.getValue();
			ID_TYPE milestone = nodeFromAllowedGraph.getId();
			if(lastInstant!=null){
				addTimes(lastMilestone, milestone, lastInstant, recordedInstant);
			}
			lastInstant = recordedInstant;
			lastMilestone = milestone;
		}
		
	}
	
}
