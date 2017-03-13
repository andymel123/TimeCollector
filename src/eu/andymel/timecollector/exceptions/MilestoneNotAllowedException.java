package eu.andymel.timecollector.exceptions;

public class MilestoneNotAllowedException extends RuntimeException{
	
	public MilestoneNotAllowedException(String msg) {
		super(msg);
	}
	
	public MilestoneNotAllowedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
