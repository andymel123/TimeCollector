package eu.andymel.timecollector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.List;

import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.Path;

public class PathTestUtils {

	public static void checkRecPathLength(TimeCollectorWithPath<?> tc, int expectedLength) {
		List<?> recordedPaths =  tc.getRecordedPaths();
		assertNotNull(recordedPaths);
		assertEquals(1, recordedPaths.size());
		Path<GraphNode<?, NodePermissions>, Instant> recPath = (Path<GraphNode<?, NodePermissions>, Instant>)recordedPaths.get(0);
		assertEquals(expectedLength, recPath.getLength());
	}

}
