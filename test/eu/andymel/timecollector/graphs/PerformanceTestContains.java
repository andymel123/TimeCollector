package eu.andymel.timecollector.graphs;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.andymel.timecollector.graphs.PermissionNode;
import eu.andymel.timecollector.performancetests.PerformanceTestsUtils;

public class PerformanceTestContains {

	private enum e {a}
	
	public static void main(String[] args) {
		
		int amount = 50_000;
		
		// fill a list and an array
		List<PermissionNode<e>> list = new ArrayList<>(amount);
		for(int i=0;i <amount; i++){
			list.add(new PermissionNode(e.a, NodePermissions.NO_CHECKS));
		}
		PermissionNode<?>[] array = list.toArray(new PermissionNode[amount]);
		
		
		// ask for every added PermissionNode if it's contained in the list  
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			if(!list.contains(array[i]))throw new RuntimeException("wtf?");
		}
		PerformanceTestsUtils.end("list.contains", amount, start);
		
		
		// ask for every added PermissionNode if it's contained in the array
		start = Instant.now();
		for(int i=0; i<amount; i++){
			// classic way of searching p in an array
			for(int x=0; x<amount; x++){
				if(array[x] == array[i])break;
				if(x==amount-1)throw new RuntimeException("wtf?");
			}
		}
		PerformanceTestsUtils.end("array.contains", amount, start);
		
		
	}
	
}
