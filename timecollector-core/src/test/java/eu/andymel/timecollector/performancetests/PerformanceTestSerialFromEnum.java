package eu.andymel.timecollector.performancetests;

import java.io.IOException;
import java.time.Instant;

import eu.andymel.timecollector.TimeCollectorSerial;

public class PerformanceTestSerialFromEnum {

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

		waitForInput();
		
		int amount = 100_000_000;

		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorSerial.create(TestMilestones.class, true, true, true);
		}
		PerformanceTestsUtils.end("TimeCollectorSerial with "+TestMilestones.class.getSimpleName(), amount, start);
	
		
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
