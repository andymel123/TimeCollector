package eu.andymel.timecollector.report;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;

public class TextualPathAnalyzer<ID_TYPE> extends AbstractPathAnalyzer<ID_TYPE> {

	private PathStringBuilder<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> pathStringBuilder;
	
	private TextualPathAnalyzer() {
		super();
		pathStringBuilder = new PathStringBuilder<ID_TYPE, TimeCollectorWithPath<ID_TYPE>>();
	}

	public static<ID_TYPE> TextualPathAnalyzer<ID_TYPE> create(){
		return new TextualPathAnalyzer<>();
	}
	
	@Override
	protected void addTimes(
			GraphNode<ID_TYPE, NodePermissions> node1, 
			GraphNode<ID_TYPE, NodePermissions> node2,
			Instant lastInstant, Instant instant) {
		pathStringBuilder.addTimes(node1.getId(), node2.getId(), lastInstant, instant);
	}
	
	@Override
	public String toString() {
		return pathStringBuilder.toString();
	}
	
	public String toString(TimeUnit unit) {
		return pathStringBuilder.toString(unit);
	}

}
