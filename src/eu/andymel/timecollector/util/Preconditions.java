package eu.andymel.timecollector.util;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Normally I do a lot of validity checks. So I reduce work by using this short utility methods. 
 * @author andymatic
 */
public class Preconditions {

	private Preconditions(){throw new IllegalStateException("May not be initialized");}

	
	/**
	 * check if not null
	 * @param o the object that may not be null
	 * @param s the msg if the object is null
	 * 
	 * @throws NullPointerException if object is null
	 */
	public final static void nn(Object o, String s) throws NullPointerException{
		if(o==null){
			throw new NullPointerException(s);
		}
	}

	
	/**
	 * check if not null
	 * @param o the object that may not be null
	 * @param s a {@link Supplier} to get the string from if the object is null. 
	 * 			It's not directly a string to have string concanation only if 
	 * 			the string is really needed
	 * 
	 * @throws NullPointerException if object is null
	 */
	public final static void nn(Object o, Supplier<String> s) throws NullPointerException{
		if(o==null){
			throw new NullPointerException(s.get());
		}
	}
	
	/**
	 * check if not empty
	 * @param c a collection to be checked
	 * @param s the msg if the object is null
	 * 
	 * @throws IllegalStateException if collection is empty
	 * @throws NullPointerException if collection is null
	 */
	public void ne(Collection<?> c, String s) throws IllegalStateException, NullPointerException{
		nn(c, s);
		if(c.size()==0){
			throw new IllegalStateException(s);
		}
	}

	
	/**
	 * check if not empty
	 * @param c a collection to be checked
	 * @param s {@link Supplier} to get the string from if the collection is empty. 
	 * 			It's not directly a string to have string concanation only if 
	 * 			the string is really needed
	 * 
	 * @throws IllegalStateException if collection is empty
	 * @throws NullPointerException if collection is null
	 */
	public void ne(Collection<?> c, Supplier<String> s) throws IllegalStateException, NullPointerException{
		nn(c, s);
		if(c.size()==0){
			throw new IllegalStateException(s.get());
		}
	}


}
