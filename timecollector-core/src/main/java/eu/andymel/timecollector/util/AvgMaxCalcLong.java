package eu.andymel.timecollector.util;

public class AvgMaxCalcLong {

	private long count;
	private double avg;
	private long max = Long.MIN_VALUE;
	private long min = Long.MAX_VALUE;
	private boolean valid = false;
	
	public static AvgMaxCalcLong create() {
		return new AvgMaxCalcLong(false);
	}
	public static AvgMaxCalcLong createInitializedWithZero() {
		return new AvgMaxCalcLong(true);
	}

	
	private AvgMaxCalcLong(boolean initializeWith0) {
		if(initializeWith0){
			// set everything to 0 without counting this as the first entry
			this.avg = 0;
			this.max = 0;
			this.min = 0;
			valid = true;
		}
	};
	
	public void add(long l){
		valid = true;
		count++;
		if(count==1){
			this.avg = l;
			this.max = l;
			this.min = l;
		} else{
			this.avg = avg*((count-1)/(double)count) + l/(double)count;
			if(l > this.max){
				this.max = l; 
			}else if(l< this.min){
				this.min = l;
			}
		}
	}
	
	public double getAvg() {
		if(!valid)throw new IllegalStateException("You have to add a value before you can retrieve with getAvg()!");
		return avg;
	}
	
	public long getMax() {
		if(!valid)throw new IllegalStateException("You have to add a value before you can retrieve with getMax()!");
		return max;
	}
	
	public long getMin() {
		if(!valid)throw new IllegalStateException("You have to add a value before you can retrieve with getMin()!");
		return min;
	}
	
	public long getCount() {
		return count;
	}

	
}
