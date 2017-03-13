package eu.andymel.timecollector;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class PerformanceTest {

	private enum TestMilestones{
		MS1,
		MS2,
		MS3
	}
	
	public static void main(String[] args) {

		int amount = 100_000;

//		waitForInput();
		System.out.println("Building "+amount+" TimeCollectors from enum...");
		
		TimeCollector<?>[] result = new TimeCollector[amount];

		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			result[i] = TimeCollectorWithPath.createSerial(TestMilestones.class, true, true);
		}
		
		Duration d = Duration.between(start, Instant.now());
		System.out.println("Built "+amount+" timeCollectors with serial path from enum "+TestMilestones.class+" in "+d+".");
		System.out.println("That's "+d.toMillis()/(double)amount+"ms per TimeCollector");
	}

	private static void waitForInput() {
		System.out.print("Press Enter");
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

}
