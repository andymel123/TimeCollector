package eu.andymel.timecollector.graphs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.TestClock;
import eu.andymel.timecollector.TimeCollector;
import eu.andymel.timecollector.TimeCollectorWithPath;
import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;

public class AllowedPathBuilderNodesAndEdgesTest {

	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5,MS6
	}
	
	private TimeCollector<TestMilestones> tcEAS;
	
	@Before
	public void setup(){
		
		PermissionNode<TestMilestones> p1 = PermissionNode.create(TestMilestones.MS1, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p2 = PermissionNode.create(TestMilestones.MS2, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p3 = PermissionNode.create(TestMilestones.MS3, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p4 = PermissionNode.create(TestMilestones.MS4, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p5 = PermissionNode.create(TestMilestones.MS5, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p6 = PermissionNode.create(TestMilestones.MS6, NodePermissions.REQUIRED_AND_SINGLESET);
		
		tcEAS = TimeCollectorWithPath.<TestMilestones>createWithPath(
			new TestClock(),
			AllowedPathsGraph.
			<TestMilestones>
			nodes(p1,p2,p3,p4,p5,p6)
			.path(p1,p2,p3,p4,p6)
			.path(p4, p2) // jump back (because of a retry for example)
			.path(p3, p5, p6) // alternative path to the p3,p4,p5 path
			.build()
		);
	}

	@Test
	public void testNormalEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);
		tcEAS.saveTime(TestMilestones.MS6);
		
		int count = 0;
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS6).toEpochMilli());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetTimeFromWrongMilestoneEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.getTime(TestMilestones.MS2);
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
