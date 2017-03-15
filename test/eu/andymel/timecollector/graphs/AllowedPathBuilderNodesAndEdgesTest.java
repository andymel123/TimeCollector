package eu.andymel.timecollector.graphs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AllowedPathBuilderNodesAndEdgesTest {

	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5
	}
	
	private AllowedPathsGraph<TestMilestones> path;
	
	@Before
	public void setup(){
		
		PermissionNode<TestMilestones> p1 = PermissionNode.create(TestMilestones.MS1, NodePermissions.NO_CHECKS);
		PermissionNode<TestMilestones> p2 = PermissionNode.create(TestMilestones.MS2, NodePermissions.NO_CHECKS);
		PermissionNode<TestMilestones> p3 = PermissionNode.create(TestMilestones.MS3, NodePermissions.NO_CHECKS);
		PermissionNode<TestMilestones> p4 = PermissionNode.create(TestMilestones.MS4, NodePermissions.NO_CHECKS);
		PermissionNode<TestMilestones> p5 = PermissionNode.create(TestMilestones.MS3, NodePermissions.NO_CHECKS);
		PermissionNode<TestMilestones> p6 = PermissionNode.create(TestMilestones.MS2, NodePermissions.NO_CHECKS);
		PermissionNode<TestMilestones> p7 = PermissionNode.create(TestMilestones.MS5, NodePermissions.NO_CHECKS);
		
		path = AllowedPathsGraph.
			<TestMilestones>
			nodes(p1,p2,p3,p4,p5,p6,p7)
			.edge(p1, p2)
			.edge(p2, p3)
			.edge(p3, p4)
			.edge(p4, p5)
			.edge(p5, p6)
			.edge(p6, p7)
			.build();
	}

	@Test
	public void test(){
		path.getAllNodesWIthId(TestMilestones.MS2);
	}
	
}
