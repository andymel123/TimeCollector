package eu.andymel.timecollector.report;

import java.util.concurrent.TimeUnit;

public class TextualPathAnalyzer<ID_TYPE> extends AbstractPathAnalyzerAvg<ID_TYPE> {

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
		return table.toString(3,2,2,2);
	}

	@Override
	public String toString() {
		return toString(TimeUnit.NANOSECONDS);
	}
	

}
