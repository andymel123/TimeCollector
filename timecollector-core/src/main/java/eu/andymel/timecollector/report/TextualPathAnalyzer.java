package eu.andymel.timecollector.report;

import java.util.concurrent.TimeUnit;

public class TextualPathAnalyzer<ID_TYPE> extends AbstractPathAnalyzerAvg<ID_TYPE> {

	private final int[] OFFSETS_2Columns = {3,2};
	private final int[] OFFSETS_4Columns = {3,2,2,2};
	
	private TextualPathAnalyzer() {
		super();
	}

	public static<ID_TYPE> TextualPathAnalyzer<ID_TYPE> create(){
		return new TextualPathAnalyzer<>();
	}
	
	public String toString(TimeUnit unit) {
		
		StringTable table = getAsStringTable(unit);
		if(table.getNumberOfRows()==0){
			return "";
		}
		
		int columns = table.getNumberOfColumns();
		
		switch(columns){
			case 0: return "no data";
			case 2: return table.toString(OFFSETS_2Columns); 
			case 4: return table.toString(OFFSETS_4Columns);
			default: throw new IllegalStateException("No offset layout for "+columns+" columns!");
		}
		
	}

	@Override
	public String toString() {
		return toString(TimeUnit.NANOSECONDS);
	}
	

}
