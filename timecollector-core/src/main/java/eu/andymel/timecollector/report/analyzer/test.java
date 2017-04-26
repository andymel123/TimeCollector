package eu.andymel.timecollector.report.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class test {

	public static void main(String[] args) {
		
		Object[] keys = new Object[]{new Object(), new Object(), new Object()};
		
		HashMap<Object, List<String>> map = new HashMap<>();
		for(int i=0; i<keys.length; i++){
			List<String> l = new ArrayList<>();
			l.add("eins");
			l.add("zwei");
			l.add("drei");
			map.put(keys[i], l);
		}
		
		HashMap<Object, List<String>> newMap = new HashMap<>(map);
		
		
		int i= 0;
	}

}
