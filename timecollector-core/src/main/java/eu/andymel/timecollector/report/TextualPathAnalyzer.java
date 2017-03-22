package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.Path;
import eu.andymel.timecollector.util.AvgMaxCalcLong;

public class TextualPathAnalyzer<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> {

	private LinkedHashMap<String, AvgMaxCalcLong> timesPerSpan;
	private int maxTimeSpanNameLength = 0;
	private int count = 0;
	
	public TextualPathAnalyzer() {
		timesPerSpan = new LinkedHashMap<>();
	}
	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		
		nn(tc, "TimeCollector is null!");
		
		List<Path<GraphNode<ID_TYPE, NodePermissions>, Instant>> paths = tc.getRecordedPaths();

		ne(paths, "TimeCollector does not contain recorded paths!");
		
		Path<GraphNode<ID_TYPE, NodePermissions>, Instant> path = paths.get(0);
		
		Iterator<GraphNode<GraphNode<ID_TYPE, NodePermissions>, Instant>> it = path.iterator();
		
		Instant lastInstant = null;
		String lastName = null;
		while(it.hasNext()){
			GraphNode<GraphNode<ID_TYPE, NodePermissions>, Instant> node = it.next();
			GraphNode<ID_TYPE, NodePermissions> nodeFromAllowedGraph = node.getId();
			Instant recordedInstant = node.getPayload();
			String milestoneName = nodeFromAllowedGraph.getId().toString();
			if(lastInstant!=null){
				String timeSpanName = getTimeSpanName(lastName, milestoneName);
				AvgMaxCalcLong calc = timesPerSpan.get(timeSpanName);
				if(calc==null){
					calc = AvgMaxCalcLong.create();
					timesPerSpan.put(timeSpanName, calc);
				}
				calc.add(Duration.between(lastInstant, recordedInstant).toNanos());
				int lenTimeSpanName = timeSpanName.length();
				if(lenTimeSpanName>maxTimeSpanNameLength){
					maxTimeSpanNameLength = lenTimeSpanName;
				}
			}
			lastInstant = recordedInstant;
			lastName = milestoneName;
		}
		 
		
	}

	private String getTimeSpanName(String lastName, String milestoneName) {
		return lastName+"->"+milestoneName;
	}

	public String toString(TimeUnit unit) {
		
		StringBuilder sb = new StringBuilder("");
		String headerTimeSpan = "TimeSpan (Unit: "+unit+")";
		maxTimeSpanNameLength = Math.max(maxTimeSpanNameLength, headerTimeSpan.length());

		String formatString = 	"%"+(maxTimeSpanNameLength+2)+"s %10d %10d %10d";
		sb.append(String.format("%"+(maxTimeSpanNameLength+2)+"s %10s %10s %10s", headerTimeSpan, "min", "avg", "max")).append('\n');
		timesPerSpan.forEach((timeSpanName, calc)->{
			sb.append(
				String.format(formatString, 
					timeSpanName, 
					// TODO replace by own implementation that does not round to full numbers (to get 0,001ms)
					unit.convert(calc.getMin(),TimeUnit.NANOSECONDS), 
					unit.convert((long)calc.getAvg(),TimeUnit.NANOSECONDS), 
					unit.convert(calc.getMax(),TimeUnit.NANOSECONDS)
				)
			).append('\n');
		});
		
		return sb.toString();
	}

	@Override
	public String toString() {
		return toString(TimeUnit.NANOSECONDS);
	}
	
}
