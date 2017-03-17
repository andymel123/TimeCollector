package eu.andymel.timecollector.util;

public class RecursionSavetyCounter{
	
	private int max;
	private int count = 0;
	private String errorMsg;
	
	public RecursionSavetyCounter(int max, String errorMsg) {
		this.max = max;
		this.errorMsg = errorMsg;
	}
	
	public void inc(){
		if(++count > max){
			throw new RuntimeException(errorMsg);
		};
	}
}