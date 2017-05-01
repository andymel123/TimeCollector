package eu.andymel.timecollector.report.analyzer;

import java.util.List;

import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public interface RecordedPathCollectorView<ID_TYPE> {
	
	AllowedPathsGraph<ID_TYPE> getAllowedGraph();

	Integer getHashOfRecPath();
	
	List<GraphNode<ID_TYPE, NodePermissions>> getRecPath();
	
	/**
	 * @return a long[] with the length of the number of timespans + 1. 
	 * At idx 0 is the time stamp of the recorded data. At idx 1 to n the times of the measured timespans. 
	 */
	List<long[]> getCollectedTimes();
	
	/**
	 * @return a {@code long} array with a length of the number of timespans * 3.
	 * You find the minimum value of the first timespan at idx 0, the avg of the first timespan on idx 1 and the maximum at idx 2. 
	 * Then it repeats with the min,avg,max of the second timespan at idx 3-5 and so on. Thats why the length is number of timespans * 3  
	 */
	long[] getTotalAvgTimes();
	
}