package eu.andymel.timecollector.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Set that compares for identity instead of comparing with the equals method
 * 
 * @author andymatic
 *
 * @param <E> Type of the objects in this {@link Set}
 */
public class IdentitySet<E> implements Set<E> {

	private Map<E, Object> map;
	private static final Object DUMMY_MAP_CONTENT = new Object();

	public IdentitySet() {
		this(10);
	}

	public IdentitySet(int size) {
		this.map = new IdentityHashMap<E, Object>(size);
	}

	public IdentitySet(Set<E> setToCopy) {
		this.map = new IdentityHashMap<E, Object>(setToCopy.size());
		for(E e:setToCopy){
			this.map.putIfAbsent(e, DUMMY_MAP_CONTENT);
		}
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return map.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return map.keySet().toArray(a);
	}

	@Override
	public boolean add(E o) {
		return map.put(o, DUMMY_MAP_CONTENT) == null;
	}
	
	@Override
	public boolean remove(Object o) {
		return map.remove(o) == DUMMY_MAP_CONTENT;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean thisWasChanged = false;
		for (E o : c) {
			thisWasChanged = thisWasChanged || add(o);
		}
		return thisWasChanged;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean thisWasChanged = false;
		Iterator<E> it = iterator();
		while(it.hasNext()){
			E e = it.next();
			if(c.contains(e)){
				continue;
			}else{
				it.remove();
				thisWasChanged = true;
			}
		}
		return thisWasChanged;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean remove = false;
		for (Object o : c) {
			remove = remove || remove(o);
		}
		return remove;
	}


	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"[" + map.keySet() +"]";
	}

}