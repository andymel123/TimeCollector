package eu.andymel.timecollector.performancetests;

import static eu.andymel.timecollector.performancetests.PerformanceTestsUtils.*;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import eu.andymel.timecollector.TestTimeCollectorProvider;
import eu.andymel.timecollector.TimeCollectorSerial;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.report.TextualPathAnalyzer;
import eu.andymel.timecollector.util.NanoClock;

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
		
		int amount = 10_000_000;
		
		Instant start = Instant.now();
		for(int i=0; i<amount; i++){
			TimeCollectorSerial.create(TestMilestones.class, true, true, true);
		}
		PerformanceTestsUtils.end("TimeCollectorSerial with "+TestMilestones.class.getSimpleName(), amount, start);
	
		
	}

}
