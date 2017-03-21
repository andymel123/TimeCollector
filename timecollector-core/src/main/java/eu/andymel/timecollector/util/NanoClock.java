package eu.andymel.timecollector.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A {@link Clock} pimping the normal clock by adding nanos from System.nanoTime
 * to get more precision between calls of instant() 
 * 
 * @author andymatic
 */
public class NanoClock extends Clock {
	
	private final static Clock DEFAULT_CLOCK = Clock.systemUTC(); 
	
    private final Instant startInstant; 
	private final long startNanos;
	private final ZoneId zoneId;

    public NanoClock()
    {
        this(DEFAULT_CLOCK);
    }

    public NanoClock(final Clock clock)
    {
        this(
        	clock.instant(), 
        	System.nanoTime(), 
        	clock.getZone()
        );
    }

    public NanoClock(Instant startInstant, long startNanos, ZoneId zone)
    {
        this.startInstant = startInstant;
        this.startNanos = startNanos;
        this.zoneId = zone;
    }
    
    
	@Override
	public ZoneId getZone() {
        return zoneId;
	}

	@Override
	public Clock withZone(ZoneId zone) {
        return new NanoClock(startInstant, startNanos, zone);
	}

	@Override
	public Instant instant() {
    	long nanosSinceStart = System.nanoTime() - startNanos;
        return startInstant.plusNanos(nanosSinceStart);
	}

}

	
	
	
