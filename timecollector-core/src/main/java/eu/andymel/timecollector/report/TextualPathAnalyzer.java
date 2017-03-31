package eu.andymel.timecollector.report;

import java.time.Instant;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.util.AvgMaxCalcLong;

public class TextualPathAnalyzer<ID_TYPE> extends AbstractPathAnalyzer<ID_TYPE> {

	private TextualPathAnalyzer() {
		super();
	}

	public static<ID_TYPE> TextualPathAnalyzer<ID_TYPE> create(){
		return new TextualPathAnalyzer<>();
	}
	
	protected String getTimeSpanName(GraphNode<ID_TYPE, NodePermissions> from, GraphNode<ID_TYPE, NodePermissions> to) {
		return from.getId()+"->"+to.getId();
	}

	public String toString(TimeUnit unit) {
		
		List<String[]> rows = new LinkedList<>(); 
		int[] columnWidth = new int[4];	// 4 columns
		
		for(Entry<GraphNode<ID_TYPE, NodePermissions>, IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong>> outer: getTimesPerSpan().entrySet()){
			GraphNode<ID_TYPE, NodePermissions> node1 = outer.getKey();
			IdentityHashMap<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> inner = outer.getValue();
			for(Entry<GraphNode<ID_TYPE, NodePermissions>, AvgMaxCalcLong> e: inner.entrySet()){
				GraphNode<ID_TYPE, NodePermissions> node2 = e.getKey();
				AvgMaxCalcLong calc = e.getValue();
				
				String column1 = getTimeSpanName(node1, node2);
				// TODO replace by own implementation that does not round to full numbers (to get 0,001ms)
				String column2 = String.valueOf(unit.convert(calc.getMin(),TimeUnit.NANOSECONDS)); 
				String column3 = String.valueOf(unit.convert((long)calc.getAvg(),TimeUnit.NANOSECONDS));
				String column4 = String.valueOf(unit.convert(calc.getMax(),TimeUnit.NANOSECONDS));
				
				columnWidth[0] = Math.max(columnWidth[0], column1.length());
				columnWidth[1] = Math.max(columnWidth[1], column2.length());
				columnWidth[2] = Math.max(columnWidth[2], column3.length());
				columnWidth[3] = Math.max(columnWidth[3], column4.length());
				
				rows.add(new String[]{column1, column2, column3, column4});
			}
		}
		
		if(rows.size()==0){
			return "";
		}

		StringBuilder sb = new StringBuilder("");
		String headerTimeSpan = "TimeSpan (Unit: "+unit+")";
		
		columnWidth[0] = Math.max(columnWidth[0], headerTimeSpan.length());

		String formatString = 	"%"+(columnWidth[0]+3)+"s %"+(columnWidth[1]+2)+"s %"+(columnWidth[2]+2)+"s %"+(columnWidth[3]+2)+"s";
		
		// header
		sb.append(String.format(formatString, headerTimeSpan, "min", "avg", "max")).append('\n');

		// other rows
		for(String[] row: rows){
			sb.append(String.format(formatString, row[0], row[1], row[2], row[3])).append('\n');
		}
		
		return sb.toString();
			
	}

	@Override
	public String toString() {
		return toString(TimeUnit.NANOSECONDS);
	}
	

}
