package eu.andymel.timecollector.util;

/**
 * Small utility calls to prevent loops from running endlessly 
 * because of buggy code.
 * 
 * @author andymatic
 *
 */
public class RecursionSavetyCounter{
	
	private int max;
	private int count = 0;
	private String errorMsg;
	
	private RecursionSavetyCounter(int max, String errorMsg) {
		this.max = max;
		this.errorMsg = errorMsg;
	}
	
	public static RecursionSavetyCounter create(int max, String errorMsg){
		return new RecursionSavetyCounter(max, errorMsg);
	}
	
	public void inc(){
		if(++count > max){
			throw new RuntimeException(errorMsg);
		};
	}
}