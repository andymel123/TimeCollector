package eu.andymel.timecollector;

public interface TimeCollector<MILESTONE_TYPE> {

	void saveTime(MILESTONE_TYPE m);

	void getTime(MILESTONE_TYPE m);

}