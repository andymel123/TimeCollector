package eu.andymel.timecollector.performancetests;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.PermissionNode;

public class PerformanceTestContains {

	private enum e {a}
	
	public static void main(String[] args) {
		
		int amount = 50_000;
		
		// fill a list and an array
		List<GraphNode<e, NodePermissions>> list = new ArrayList<>(amount);
		for(int i=0;i <amount; i++){
			list.add(PermissionNode.create(e.a, NodePermissions.NO_CHECKS));
		}
		GraphNode<?,?>[] array = list.toArray(new PermissionNode[amount]);
		
		
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
