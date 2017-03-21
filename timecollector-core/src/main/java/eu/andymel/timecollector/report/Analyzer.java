package eu.andymel.timecollector.report;

import eu.andymel.timecollector.TimeCollector;

public interface Analyzer <ID_TYPE, TC_TYPE extends TimeCollector<ID_TYPE>>{

	/**
	 * To recognize problems with your time measurement code I throw exceptions when the given timecollector is null or its list of 
	 * recorded paths is empty or null. So check this before you call this method if you think it makes sense in your code. 
	 * 
	 * @param tc the {@link TimeCollector} with the data to analyze
	 * 
	 * @throws NullPointerException if the given {@link TimeCollector} or its recorded paths are null.
	 * @throws IllegalStateException if getRecordedPaths on the given {@link TimeCollector} returns an empty list of paths 
	 * 
	 */
	void addCollector(TC_TYPE tc);
	
}
