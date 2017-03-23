package eu.andymel.timecollector;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Clock;
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
	private final boolean allRequired;
	private final boolean singleSet;
	private final EnumSet<MILESTONE_TYPE> milestoneSet;
	private final Clock clock;
	
	private MILESTONE_TYPE lastMilestone = null;

	private TimeCollectorSerial(Clock clock, boolean ensureOrder, boolean allRequired, boolean singleSet, Class<MILESTONE_TYPE> clazz) {
		
		// preconditions
		nn(clock, "'clock' is null!");
		
		this.clock = clock;
		this.ensureOrder = ensureOrder;
		this.allRequired = allRequired;
		
		if(allRequired && !ensureOrder){
			throw new IllegalArgumentException("AllRequired can only be set if ensureOrder is set as well. Because I can't test at which time all has to be set without knowing an order.");
		}
		
		this.singleSet = singleSet;
		this.milestoneSet = EnumSet.allOf(clazz);
		this.savedMileStonesTimes = new Instant[milestoneSet.size()];
	}
	
	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollectorSerial<MILESTONE_TYPE> create(Class<MILESTONE_TYPE> enumClazz, boolean ensureOrder, boolean allRequired, boolean singleSet){
		return new TimeCollectorSerial<MILESTONE_TYPE>(Clock.systemDefaultZone(), ensureOrder, allRequired, singleSet, enumClazz);
	}
	public static <MILESTONE_TYPE extends Enum<MILESTONE_TYPE>> TimeCollectorSerial<MILESTONE_TYPE> create(Class<MILESTONE_TYPE> enumClazz, Clock clock, boolean ensureOrder, boolean allRequired, boolean singleSet){
		return new TimeCollectorSerial<MILESTONE_TYPE>(clock, ensureOrder, allRequired, singleSet, enumClazz);
	}

	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#saveTime(MILESTONE_TYPE)
	 */
	@Override
	public synchronized void saveTime(MILESTONE_TYPE m) {

		// ***** preconditions *****
		nn(m, "'milestone' is null!");
		
		int idx = m.ordinal();
		if(ensureOrder){
			
			if(lastMilestone!=null && lastMilestone.ordinal()==milestoneSet.size()-1){
				throw new MilestoneNotAllowedException("The last milestone in your enum '"+lastMilestone+"' has already been set in "+this+". There is no other. So '"+m+"' can't be a milestone after '"+lastMilestone+"'");
			}
			
			if(lastMilestone!=null && idx < lastMilestone.ordinal()){
				throw new MilestoneNotAllowedException("The later milestone '"+lastMilestone+"' has already been set at "+getTime(lastMilestone)+". You can't set '"+m+"' anymore!");
			}
			
			if(allRequired){
				if(lastMilestone==null && idx != 0){
					throw new MilestoneNotAllowedException("This is the first time you call saveTime on "+this+" but '"+m+"' is not the first Milestone in your given enum "+m.getClass().getSimpleName());
				} else if(lastMilestone!=null && idx != lastMilestone.ordinal()+1) {
					throw new MilestoneNotAllowedException("Last milestone saved was '"+lastMilestone+"'! '"+m+"' is not the next milestone!");
				}
			}
		}
		
		if(singleSet && savedMileStonesTimes[idx] != null){
			throw new MilestoneNotAllowedException("The milestone '"+m+"' has already been set! Old time was "+savedMileStonesTimes[idx]+".");
		}else{
			savedMileStonesTimes[idx] = clock.instant();
			lastMilestone = m;
		}
		
	}

	/* (non-Javadoc)
	 * @see eu.andymel.timecollector.TimeCollector#getTime(MILESTONE_TYPE)
	 */
//	@Override
	public synchronized Instant getTime(MILESTONE_TYPE milestone) {
		return savedMileStonesTimes[milestone.ordinal()];
	}
	
}
