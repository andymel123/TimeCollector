package eu.andymel.timecollector.report.analyzer;

import static eu.andymel.timecollector.util.Preconditions.ne;
import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.report.TimeSpanNameFormatter;
import eu.andymel.timecollector.util.AvgMaxCalcLong;
import eu.andymel.timecollector.util.StringTable;

/**
 * This analyzer saves the min/avg/max time between two milestones
 * 
 * @author andymatic
 *
 * @param <ID_TYPE> the milestone type
 */
public class AnalyzerAvgPath<ID_TYPE> implements Analyzer<ID_TYPE, TimeCollectorWithPath<ID_TYPE>> {

	private volatile long countTimeCollectorsAdded = 0;

	/** HashMap in HashMap as outer key is the first milestone, inner key the second */
	private IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> timesPerSpan;
	private AllowedPathsGraph<ID_TYPE> allowedGraph;

	private AnalyzerAvgPath() {
		/* LinkedHashMap to get insertion order, has same performance as HashMap in my measurements */
		timesPerSpan = new IdentityHashMap<>();
	}

	public static <ID_TYPE> AnalyzerAvgPath<ID_TYPE> create(){
		return new AnalyzerAvgPath<>();
	}
	
	@Override
	public synchronized void addCollector(TimeCollectorWithPath<ID_TYPE> tc) {
		// TODO make async!
		
		nn(tc, "TimeCollector is null!");
		
		AllowedPathsGraph<ID_TYPE> ag = tc.getAllowedGraph();
		if(allowedGraph==null){
			allowedGraph = ag;
		}else{
			if(allowedGraph!=ag){
				throw new IllegalStateException("One "+getClass().getSimpleName()+" may only collect paths for "
						+ "one allowedGraph! The current allowedGraph is "+allowedGraph+". "
						+ "You want to add a timecollector with allowedGraph "+ag);
			}
		}
		
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


	public long getNumberOfAddedTimeCollectors() {
		return countTimeCollectorsAdded;
	}

	public IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> getTimesPerSpan() {
		return timesPerSpan;
	}
	
	public double getAvgSummedUp(){
		return timesPerSpan.values().stream()// get all IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>
			.map(IdentityHashMap::values)	// map to Collections of AvgMaxCalcLong
			.flatMap(Collection::stream)	// combine to one stream of AvgMaxCalcLong
			.mapToDouble(c->c.getAvg())		// map from AvgMaxCalcLong to avg values
			.sum();							// sum all avg up
	}
	
	/**
	 * @param unit
	 * @return a {@link StringTable} either with two columns (timespan name and length of timespan), or 4 columns (name, min, avg, max).
	 * The 2 column version is returned if less than 2 timeCollectors were added, if multiple time collectors were added -> 4columns
	 */
	public StringTable getAsStringTable(TimeUnit unit, TimeSpanNameFormatter<ID_TYPE> timeSpanNameFormatter) {
		
		Objects.requireNonNull(unit, "'unit' is null!");
		Objects.requireNonNull(timeSpanNameFormatter, "'timeSpanNameFormatter' is null!");
		
		StringTable table = new StringTable();
		
		boolean justShowOneValueColumn = getNumberOfAddedTimeCollectors() < 2;
		
		if(justShowOneValueColumn){
			// just show one entry as "time"
			table.row("TimeSpan (Unit: "+unit+")", "Time");
		}else{
			// show 3 entries min/avg/max
			table.row("TimeSpan (Unit: "+unit+")", "min", "avg", "max");
		}
				
		for(Entry<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> outer: getTimesPerSpan().entrySet()){
			GraphNode<ID_TYPE, NodePermissions> node1 = outer.getKey();
			IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> inner = outer.getValue();
			for(Entry<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> e: inner.entrySet()){
				GraphNode<ID_TYPE, NodePermissions> node2 = e.getKey();
				AvgMaxCalcLong calc = e.getValue();

				// TODO replace by own implementation that does not round to full numbers (to get 0,001ms)
				String column1 = timeSpanNameFormatter.getTimeSpanName(node1, node2);
				String column2 = String.valueOf(unit.convert(calc.getMin(),TimeUnit.NANOSECONDS));
				
				if(justShowOneValueColumn){
					table.row(column1, column2);
				}else{
					String column3 = String.valueOf(unit.convert((long)calc.getAvg(),TimeUnit.NANOSECONDS));
					String column4 = String.valueOf(unit.convert(calc.getMax(),TimeUnit.NANOSECONDS));
					table.row(column1, column2, column3, column4);
				}
			}
		}
		return table;
	}
	
	public AllowedPathsGraph<ID_TYPE> getAllowedGraph() {
		return allowedGraph;
	}
	
	public String toString(TimeUnit unit, TimeSpanNameFormatter<ID_TYPE> timeSpanNameFormatter) {
		
		StringTable table = getAsStringTable(unit, timeSpanNameFormatter);
		if(table.getNumberOfRows()==0){
			return "";
		}
		return table.toString(3,2,2,2);
	}

}
