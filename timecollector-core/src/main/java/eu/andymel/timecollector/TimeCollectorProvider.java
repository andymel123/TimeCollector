package eu.andymel.timecollector;

public interface TimeCollectorProvider<MILESTONE_TYPE> {

	TimeCollector<MILESTONE_TYPE> getNewTC();

}
