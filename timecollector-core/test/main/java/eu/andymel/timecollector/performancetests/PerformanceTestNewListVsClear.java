package eu.andymel.timecollector.performancetests;

import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/*
 * 
 * PerformanceTest: new LinkedList()
 * Total time needed: 1.702 seconds for 10000000 iterations
 * That's 170.2nanos per iteration
 * 
 * PerformanceTest: LinkedList.clear()
 * Total time needed: 1.787 seconds for 10000000 iterations
 * That's 178.7nanos per iteration
 * 
 * 
 * PerformanceTest: new LinkedList()
 * Total time needed: 15.256 seconds for 100000000 iterations
 * That's 152.56nanos per iteration
 * 
 * PerformanceTest: LinkedList.clear()
 * Total time needed: 17.083 seconds for 100000000 iterations
 * That's 170.83nanos per iteration
 */

/**
 * 
 * @author andymatic
 *
 */
public class PerformanceTestNewListVsClear {

	public static void main(String[] args) {
		
		long amount = 10_000_000;
		long amountOfObjectAdds = 10;
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			List<Instant> l = new LinkedList<>();
			for(int o=0; o<amountOfObjectAdds; o++){
				l.add(Instant.now());
			}
		}
		end("new LinkedList()", amount, start);
		o("");
		
		start = Instant.now();
		List<Instant> l = new LinkedList<>();
		for(int i=0; i<amount; i++){
			for(int o=0; o<amountOfObjectAdds; o++){
				l.add(Instant.now());
			}
			l.clear();
		}
		end("LinkedList.clear()", amount, start);


	}

}
