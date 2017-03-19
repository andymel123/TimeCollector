package eu.andymel.timecollector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;
import eu.andymel.timecollector.graphs.GraphNode;
import eu.andymel.timecollector.graphs.NodePermissions;
import eu.andymel.timecollector.graphs.Path;
import eu.andymel.timecollector.graphs.PermissionNode;

public class NodesAndEdgesTest {

	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5,MS6
	}
	
	private TimeCollectorWithPath<TestMilestones> tcEAS;
	private TimeCollectorWithPath<TestMilestones> tcEASMax3;
	
	@Before
	public void setup(){
		
		PermissionNode<TestMilestones> p1 = PermissionNode.create(TestMilestones.MS1, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p2 = PermissionNode.create(TestMilestones.MS2, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p3 = PermissionNode.create(TestMilestones.MS3, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p4 = PermissionNode.create(TestMilestones.MS4, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p5 = PermissionNode.create(TestMilestones.MS5, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p6 = PermissionNode.create(TestMilestones.MS6, NodePermissions.REQUIRED_AND_SINGLESET);
		
		AllowedPathsGraph<TestMilestones> path = AllowedPathsGraph.
			<TestMilestones>
			nodes(p1,p2,p3,p4,p5,p6)
			.path(p1,p2,p3,p4,p6)
			.path(p3, p5, p6) // alternative path to the p3,p4,p5 path
			.path(p4, p2) // jump back (because of a retry for example)
			.build();

//		// the same graph but the jump back from p4 to p2 may only be done once
//		AllowedPathsGraph<TestMilestones> pathMax3 = AllowedPathsGraph.
//				<TestMilestones>
//				nodes(p1,p2,p3,p4,p5,p6)
//				.path(p1,p2,p3,p4,p6)
//				.path(p3, p5, p6) // alternative path to the p3,p4,p5 path
//				.edgeWithMax(3, p4, p2) // jump back (max retry 3)
//				.build();

//		path.forEach(System.out::println);
		
		tcEAS = TimeCollectorWithPath.<TestMilestones>createWithPath(new TestClock(), path);
//		tcEASMax3 = TimeCollectorWithPath.<TestMilestones>createWithPath(new TestClock(), pathMax3);
	}

	@Test
	public void testEASMax3_1() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);

		// first retry
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);

		// second retry
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);	// I tryed to check if saveTime on your milestone 'MS3' is allowed but I killed the search for allowed nodes in your path after not finding all possible next nodes in 100 recursions!
		tcEAS.saveTime(TestMilestones.MS4);

		// third retry
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);

		// forth retry
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);
		tcEAS.saveTime(TestMilestones.MS6);
		
		checkReCPathLength(tcEAS, 17);
	}
	
	@Test
	public void testNormalEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);
		tcEAS.saveTime(TestMilestones.MS6);
		
		checkReCPathLength(tcEAS, 5);	
	}
	
	@Test
	public void testRecPathLength1() {
		tcEAS.saveTime(TestMilestones.MS1);
		checkReCPathLength(tcEAS, 1);
		
		tcEAS.saveTime(TestMilestones.MS2);
		checkReCPathLength(tcEAS, 2);

		tcEAS.saveTime(TestMilestones.MS3);
		checkReCPathLength(tcEAS, 3);

	}


	
	private static void checkReCPathLength(TimeCollectorWithPath<TestMilestones> tc, int expectedLength) {
		List<Path<GraphNode<TestMilestones, NodePermissions>, Instant>> recordedPaths =  tc.getRecordedPaths();
		assertNotNull(recordedPaths);
		assertEquals(1, recordedPaths.size());
		Path<GraphNode<TestMilestones, NodePermissions>, Instant> recPath = recordedPaths.get(0);
		assertEquals(expectedLength, recPath.getLength());
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testWrongPathEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);
		tcEAS.saveTime(TestMilestones.MS5); //not on path
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testRequiredNodeMissingEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS3); // next should be MS2
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testOtherOrderEAS() {
		tcEAS.saveTime(TestMilestones.MS2);
	}

	
	
}
