package eu.andymel.timecollector;

import eu.andymel.timecollector.TimeCollector;

public interface TimeCollectorProvider<MILESTONE_TYPE> {

	TimeCollector<MILESTONE_TYPE> getNewTC();

}
