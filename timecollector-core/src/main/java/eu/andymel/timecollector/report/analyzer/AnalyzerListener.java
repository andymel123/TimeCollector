package eu.andymel.timecollector.report.analyzer;

import eu.andymel.timecollector.TimeCollector;

public interface AnalyzerListener {

	void timeCollectorAddedToAnalyzer(TimeCollector<?> tc, Analyzer<?, ?> analyzer);
	
}
