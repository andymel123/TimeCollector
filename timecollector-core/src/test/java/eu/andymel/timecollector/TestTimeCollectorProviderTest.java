package eu.andymel.timecollector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.andymel.timecollector.util.NanoClock;
import eu.andymel.timecollector.util.teststuff.TestTimeCollectorProvider;
import eu.andymel.timecollector.util.teststuff.TestTimeCollectorProvider.TestMilestones;

public class TestTimeCollectorProviderTest {

	@Test
	public void test() {
		TimeCollectorWithPath<TestMilestones> tc = TestTimeCollectorProvider.getTC(new NanoClock());
		
		assertEquals(26, tc.getAllowedGraph().getAllNodes().size());
	}

}
