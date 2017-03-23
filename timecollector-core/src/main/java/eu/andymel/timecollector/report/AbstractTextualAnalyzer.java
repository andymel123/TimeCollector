package eu.andymel.timecollector.report;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.util.AvgMaxCalcLong;

public abstract class AbstractTextualAnalyzer<ID_TYPE, TC_TYPE extends TimeCollector<ID_TYPE>> implements Analyzer<ID_TYPE, TC_TYPE> {

	private LinkedHashMap<String, AvgMaxCalcLong> timesPerSpan;
	private int maxTimeSpanNameLength = 0;
	private int count = 0;
	
	public AbstractTextualAnalyzer() {
		timesPerSpan = new LinkedHashMap<>();
	}
	
	protected void addTimes(String m1, String m2, Temporal t1, Temporal t2) {
		String timeSpanName = getTimeSpanName(m1, m2);
		AvgMaxCalcLong calc = timesPerSpan.get(timeSpanName);
		if(calc==null){
			calc = AvgMaxCalcLong.create();
			timesPerSpan.put(timeSpanName, calc);
		}
		calc.add(Duration.between(t1, t2).toNanos());
		int lenTimeSpanName = timeSpanName.length();
		if(lenTimeSpanName>maxTimeSpanNameLength){
			maxTimeSpanNameLength = lenTimeSpanName;
		}
	}

	protected String getTimeSpanName(String lastName, String milestoneName) {
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
