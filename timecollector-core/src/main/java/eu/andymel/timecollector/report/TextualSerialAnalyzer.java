package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Iterator;

import eu.andymel.timecollector.TimeCollectorSerial;

public class TextualSerialAnalyzer<ID_TYPE extends Enum<ID_TYPE>> extends AbstractTextualAnalyzer<ID_TYPE, TimeCollectorSerial<ID_TYPE>> {

	private EnumSet<ID_TYPE> enumSet;
	
	public TextualSerialAnalyzer(Class<ID_TYPE> enumClazz) {
		nn(enumClazz,"The given enum class is null!");
		enumSet = EnumSet.allOf(enumClazz);
	}
	
	@Override
	public void addCollector(TimeCollectorSerial<ID_TYPE> tc) {
		
		Iterator<ID_TYPE> it = enumSet.iterator();
		if(!it.hasNext()){
			// timecollector is empty (no times saved)
			return;
		}
		
		
		ID_TYPE lastMS = it.next();
		Instant lastTime = tc.getTime(lastMS);
		while(it.hasNext()){
			ID_TYPE nextMS = it.next();
			Instant nextTime = tc.getTime(nextMS);
			addTimes(lastMS, nextMS, lastTime, nextTime);
			lastTime = nextTime;
			lastMS = nextMS;
		}
		
	}


}
