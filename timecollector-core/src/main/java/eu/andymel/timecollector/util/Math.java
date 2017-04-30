package eu.andymel.timecollector.util;

import java.util.concurrent.TimeUnit;

/**
 * @author andymatic
 */
public class Math {

	/**
	 * Don't use double if you need totally accurate numbers!
	 * 
	 * For example this method returns 265.33 instead of 265.34 for Math.round(265.335, 2)
	 * Why? See http://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java/7593617#7593617 
	 * and http://stackoverflow.com/a/19431463/7869582
	 * 
	 * If speed is more important than accuracy..use it. 
	 * 
	 * @param d the number to round
	 * @param decimalPoints the decimal points to round to
	 * @return the rounded value
	 * 
	 * @throws ArithmeticException if the distance between the given number and the resulting rounded number is >0.5 (because of an overrun) 
	 */
	public static double round(double d, int decimalPoints) throws ArithmeticException{
		if(Double.isNaN(d) || Double.isInfinite(d))return d;
		if(decimalPoints==0){
			return java.lang.Math.round(d);
		}
		double f = java.lang.Math.pow(10, decimalPoints);
		double result = java.lang.Math.round(d*f)/f;
		
		if(java.lang.Math.abs(result-d)>0.5){
			throw new ArithmeticException("Seems like my mini rounding method is not capable of rounding "
					+d+" with "+decimalPoints+ "decimal points! It results in "+result+". Maybe an overrun?");
		}
		return result;
	}
	
	
	/**
	 * This is not always exact as it returns {@code double}! See javaDoc of {@link #round(double, int)}
	 * 
	 * @param nanos
	 * @param unit
	 * @param decimalPoints
	 * @return
	 * @throws ArithmeticException on overflow
	 */
	public static double convertNanos(long nanos, TimeUnit unit, int decimalPoints) {
		long f=1;
		switch(unit){
			case DAYS: {
				f=24*3600_000_000_000L; break;
			}
			case HOURS: {
				f=3600_000_000_000L; break;
			}
			case MINUTES:{
				f=60_000_000_000L; break;
			}
			case SECONDS:{
				f=1_000_000_000; break;
			}
			case MILLISECONDS:{
				f=1_000_000; break;
			}
			case MICROSECONDS:{
				f=1000; break;
			}
			case NANOSECONDS:{
				f=1; break;
			}
			default: {
				throw new IllegalArgumentException("Don't know TimeUnit '"+unit+"'");
			}
		}
		return Math.round(
			nanos/(double)f, 
			decimalPoints
		);
	}
}
