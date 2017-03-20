package eu.andymel.timecollector;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * 
 * A {@link Clock} that increases it's "time" by 1 millisecond per call
 * to instant(). I don't need to test with real time, I just want to test
 * if the timecollector returns the right time in getTime 
 * (has to increase by 1 with this clock with each call to saveTime as
 * saveTime calls instant() internally on the clock of the timeCollector)
 * 
 * @author andymatic
 *
 */
public class TestClock extends Clock {

	long count = 0;
	
	@Override
	public ZoneId getZone() {
		return null;
	}

	@Override
	public Clock withZone(ZoneId zone) {
		return null;
	}

	@Override
	public Instant instant() {
		return Instant.ofEpochMilli(count++);
	}
	
}
