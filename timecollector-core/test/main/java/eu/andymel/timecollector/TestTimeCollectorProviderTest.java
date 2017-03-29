package eu.andymel.timecollector;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.andymel.timecollector.TestTimeCollectorProvider.TestMilestones;
import eu.andymel.timecollector.util.NanoClock;

public class TestTimeCollectorProviderTest {

	@Test
	public void test() {
		TimeCollectorWithPath<TestMilestones> tc = TestTimeCollectorProvider.getTC(new NanoClock());
		
		assertEquals(26, tc.getAllowedGraph().getAllNodes().size());
	}

}
