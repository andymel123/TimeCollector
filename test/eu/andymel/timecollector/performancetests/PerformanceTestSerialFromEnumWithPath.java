package eu.andymel.timecollector.performancetests;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;

public class PerformanceTestSerialFromEnumWithPath {

	private enum TestMilestones{
		MS1,
		MS2,
		MS3,
		MS4,
		MS5,
		MS6,
		MS7,
		MS8
	}
	
	public static void main(String[] args) {

		int amount = 100_000;

// 		wait to start profiler
//		waitForInput();
		
		System.out.println("Building "+amount+" TimeCollectors from enum...");
		
		TimeCollector<?>[] result = new TimeCollector[amount];

		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			result[i] = TimeCollectorWithPath.createSerial(TestMilestones.class, true, true);
		}
		
		PerformanceTestsUtils.end("Build TimeCollector with serial path from enum "+TestMilestones.class, amount, start);
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
