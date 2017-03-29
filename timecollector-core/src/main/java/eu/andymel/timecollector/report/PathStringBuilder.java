package eu.andymel.timecollector.report;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.util.AvgMaxCalcLong;

public class PathStringBuilder<ID_TYPE, TC_TYPE extends TimeCollector<ID_TYPE>> {

	/** HashMap in HashMap as outer key is the first milestone, inner key the second */
	private HashMap<ID_TYPE, HashMap<ID_TYPE, AvgMaxCalcLong>> timesPerSpan;
	
	public PathStringBuilder() {
		/* LinkedHashMap to get insertion order, has same performance as HashMap in my measurements */
		timesPerSpan = new LinkedHashMap<>();
	}
	
	protected void addTimes(ID_TYPE m1, ID_TYPE m2, Temporal t1, Temporal t2) {
		HashMap<ID_TYPE, AvgMaxCalcLong> inner = timesPerSpan.get(m1);
		AvgMaxCalcLong calc = null;
		if(inner==null){
			inner = new LinkedHashMap<>();
			timesPerSpan.put(m1, inner);
		} else {
			calc = inner.get(m2);
		}
		if(calc==null){
			calc = AvgMaxCalcLong.create();
			inner.put(m2, calc);
		}
		
		calc.add(Duration.between(t1, t2).toNanos());
	}

	protected String getTimeSpanName(ID_TYPE from, ID_TYPE to) {
		return from+"->"+to;
	}

	public String toString(TimeUnit unit) {
		
		List<String[]> rows = new LinkedList<>(); 
		int[] columnWidth = new int[4];	// 4 columns
		
		for(Entry<ID_TYPE, HashMap<ID_TYPE, AvgMaxCalcLong>> outer: timesPerSpan.entrySet()){
			ID_TYPE m1 = outer.getKey();
			HashMap<ID_TYPE, AvgMaxCalcLong> inner = outer.getValue();
			for(Entry<ID_TYPE, AvgMaxCalcLong> e: inner.entrySet()){
				ID_TYPE m2 = e.getKey();
				AvgMaxCalcLong calc = e.getValue();
				
				String column1 = getTimeSpanName(m1, m2);
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
