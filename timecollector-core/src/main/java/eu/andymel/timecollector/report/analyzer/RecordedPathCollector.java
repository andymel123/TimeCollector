package eu.andymel.timecollector.report.analyzer;

import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public interface RecordedPathCollector<ID_TYPE> extends RecordedPathCollectorView<ID_TYPE>{

	long[] addRecordedPath(List<SimpleEntry<GraphNode<ID_TYPE, NodePermissions>, Instant>> recPath);
	
}
