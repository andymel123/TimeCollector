package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.EnumSet;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;

/**
 * For serial paths through the code use this more performant implementation of a {@link TimeCollector}
 * 
 * @author andymatic
 *
 * @param <MILESTONE_TYPE>
 */
public class TimeCollectorSerial<MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> implements TimeCollector<MILESTONE_TYPE> {

	private final Instant[] savedMileStonesTimes;
	private final boolean ensureOrder;
	private final boolean singleSet;
	private final EnumSet<MILESTONE_TYPE> milestoneSet;

	private MILESTONE_TYPE lastMilestone = null;
	
	private TimeCollectorSerial(boolean ensureOrder, boolean singleSet, Class<MILESTONE_TYPE> clazz) {
		this.ensureOrder = ensureOrder;
		this.singleSet = singleSet;
		this.milestoneSet = EnumSet.allOf(clazz);
		this.savedMileStonesTimes = new Instant[milestoneSet.size()];
	}
	
	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollectorSerial<MILESTONE_TYPE> create(Class<MILESTONE_TYPE> enumClazz, boolean ensureOrder, boolean singleSet){
		return new TimeCollectorSerial<MILESTONE_TYPE>(ensureOrder, singleSet, enumClazz);
	}

	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public synchronized void saveTime(MILESTONE_TYPE m) {

		// ***** preconditions *****
		nn(m, "'milestone' is null!");
		int idx = m.ordinal();
		
		if(ensureOrder && lastMilestone!=null && idx < lastMilestone.ordinal()){
			throw new MilestoneNotAllowedException("The later milestone '"+lastMilestone+"' has already been set at "+getTime(lastMilestone)+". You can't set '"+m+"' anymore!");
		}
		
		if(singleSet && savedMileStonesTimes[idx] != null){
			throw new MilestoneNotAllowedException("The milestone '"+m+"' has already been set! Old time was "+savedMileStonesTimes[idx]+".");
		}else{
			savedMileStonesTimes[idx] = Instant.now();
		}
		
	}

	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#getTime(MILESTONE_TYPE)
	 */
	@Override
	public synchronized Instant getTime(MILESTONE_TYPE milestone) {
		return savedMileStonesTimes[milestone.ordinal()];
	}
	
}
