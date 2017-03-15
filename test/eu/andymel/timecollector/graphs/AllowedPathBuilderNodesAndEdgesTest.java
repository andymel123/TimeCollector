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
		MS1,MS2,MS3,MS4,MS5
	}
	
//	private AllowedPathsGraph<TestMilestones> path;
	private TimeCollector<TestMilestones> tcEAS;
	
	@Before
	public void setup(){
		
		PermissionNode<TestMilestones> p1 = PermissionNode.create(TestMilestones.MS1, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p2 = PermissionNode.create(TestMilestones.MS2, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p3 = PermissionNode.create(TestMilestones.MS3, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p4 = PermissionNode.create(TestMilestones.MS4, NodePermissions.REQUIRED_AND_SINGLESET);
		PermissionNode<TestMilestones> p5 = PermissionNode.create(TestMilestones.MS5, NodePermissions.REQUIRED_AND_SINGLESET);
		
		tcEAS = TimeCollectorWithPath.<TestMilestones>createWithPath(
			new TestClock(),
			AllowedPathsGraph.
			<TestMilestones>
			nodes(p1,p2,p3,p4,p5)
			.edge(p1, p2)
			.edge(p2, p3)
			.edge(p3, p4)
			.edge(p4, p5)
			.build()
		);
	}

	@Test
	public void testNormalEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);
		tcEAS.saveTime(TestMilestones.MS5);
		
		int count = 0;
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tcEAS.getTime(TestMilestones.MS5).toEpochMilli());
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testOtherOrderEAS() {
		tcEAS.saveTime(TestMilestones.MS2);
	}

	
	
}
