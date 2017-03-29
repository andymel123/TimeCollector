package eu.andymel.timecollector.report;

import static eu.andymel.timecollector.util.Preconditions.nn;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TimeCollectorSerial;

public class TextualSerialAnalyzer<ID_TYPE extends Enum<ID_TYPE>> implements Analyzer<ID_TYPE, TimeCollectorSerial<ID_TYPE>> {

	private EnumSet<ID_TYPE> enumSet;

	private final PathStringBuilder<ID_TYPE, TimeCollectorSerial<ID_TYPE>> pathStringBuilder;
	
	public TextualSerialAnalyzer(Class<ID_TYPE> enumClazz) {
		nn(enumClazz,"The given enum class is null!");
		enumSet = EnumSet.allOf(enumClazz);
		pathStringBuilder = new PathStringBuilder<>();
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
			pathStringBuilder.addTimes(lastMS, nextMS, lastTime, nextTime);
			lastTime = nextTime;
			lastMS = nextMS;
		}
		
	}

	@Override
	public String toString() {
		return pathStringBuilder.toString();
	}

	public String toString(TimeUnit unit) {
		return pathStringBuilder.toString(unit);
	}

}
