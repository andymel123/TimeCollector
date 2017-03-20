package eu.andymel.timecollector;

import java.time.Instant;

/**
 * Collects timestamps of type {@link Instant} for milestones when 
 * {@link TimeCollector#saveTime(milestone)} is called. 
 * <p>
 * Afterwards you can retrieve the timestamp with {@link TimeCollector#getTime(milestone)} 
 * 
 * @author andymatic
 *
 * @param <MILESTONE_TYPE> the type of your milestone
 */
public interface TimeCollector<MILESTONE_TYPE> {

	/**
	 * Saves the current time for this milestone
	 * @param m the milestone to save the time for
	 */
	void saveTime(MILESTONE_TYPE m);

	/**
	 * @param m the milestone to get the time for 
	 * @return the {@link Instant} that was saved when {@link TimeCollector#saveTime(m)} was called for this milestone
	 */
//	Instant getTime(MILESTONE_TYPE m);

}