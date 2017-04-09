package eu.andymel.timecollector.report;

import java.util.List;

import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public interface AnalyzerEachEntry<ID_TYPE> {
	
	Integer getHashOfRecPath();
	
	List<GraphNode<ID_TYPE, NodePermissions>> getRecPath();
	
	List<long[]> getCollectedTimes();
	
	AllowedPathsGraph<ID_TYPE> getAllowedGraph();
	
}
