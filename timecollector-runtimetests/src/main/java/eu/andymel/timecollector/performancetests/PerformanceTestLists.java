package eu.andymel.timecollector.performancetests;

import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.end;
import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.o;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class PerformanceTestLists {

	public static void main(String[] args) {
		
		
		long amount = 10_000_000;
		int[] amountOfObjectAdds = {5, 10, 30, 100, 1000, 10000};
		int sum = IntStream.of(amountOfObjectAdds).sum();
		
//		waitForInput();
		
		for(int listSize:amountOfObjectAdds){
			
			Instant start = Instant.now();
			for(int i=0; i<amount/listSize; i++){
				List<Instant> l = new ArrayList<>();
				for(int s=0; s<listSize; s++){
					l.add(Instant.now());
				}
			}
			end("Adding "+listSize+" objects to ArrayList "+amount/listSize+" times", amount*sum, start);
			o("");
		}

		o("##############################################################\n");
		
		for(int listSize:amountOfObjectAdds){
			
			Instant start = Instant.now();
			for(int i=0; i<amount/listSize; i++){
				List<Instant> l = new LinkedList<>();
				for(int s=0; s<listSize; s++){
					l.add(Instant.now());
				}
			}
			end("Adding "+listSize+" objects to LinkedList "+amount/listSize+" times", amount*sum, start);
			o("");
		}

		
	}

}
