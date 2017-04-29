package eu.andymel.timecollector.util.teststuff;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Random;

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
public class TestClockIncrementRandom extends Clock {

	private Random rng = new Random();
	private long time = rng.nextInt(1_000_000);
	private int minIncrement;
	private int incrementSpan;
	
	public TestClockIncrementRandom(int minIncrement, int maxIncrement) {
		this.minIncrement = minIncrement;
		this.incrementSpan = maxIncrement-minIncrement;
	}
	
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
		time = time + rng.nextInt(incrementSpan)+minIncrement;
		return Instant.ofEpochMilli(time);
	}
	
}
