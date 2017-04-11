package eu.andymel.timecollector.report;

import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public interface TimeSpanNameFormatter<ID_TYPE>{

	public static TimeSpanNameFormatter DEFAULT_TIMESPAN_NAME_FORMATTER = new TimeSpanNameFormatter() {

		@Override
		public String getTimeSpanName(GraphNode from, GraphNode to) {
			return from.getId()+" => "+to.getId();
		}
		
	}; 
	
	String getTimeSpanName(GraphNode<ID_TYPE, NodePermissions> from, GraphNode<ID_TYPE, NodePermissions> to);
	
}
