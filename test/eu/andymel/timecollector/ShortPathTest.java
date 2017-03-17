package eu.andymel.timecollector;

import static eu.andymel.timecollector.graphs.NodePermissions.NOT_REQUIRED_BUT_SINGLESET;
import static eu.andymel.timecollector.graphs.NodePermissions.NO_CHECKS;
import static eu.andymel.timecollector.graphs.NodePermissions.REQUIRED_AND_SINGLESET;
import static eu.andymel.timecollector.graphs.NodePermissions.REQUIRED_MULTISET;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;
import eu.andymel.timecollector.graphs.AllowedPathsGraph;


public class ShortPathTest {

	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5
	}
	
	private TimeCollector<TestMilestones> tcE;
	private TimeCollector<TestMilestones> tcEA;
	private TimeCollector<TestMilestones> tcES;
	private TimeCollector<TestMilestones> tcEAS;

	@Before
	public void setup(){
		
		tcE = TimeCollectorWithPath.createWithPath(
			new TestClock(), 
			AllowedPathsGraph
				.start(TestMilestones.MS1,NO_CHECKS)
				.then(TestMilestones.MS2, NO_CHECKS)
				.then(TestMilestones.MS3, NO_CHECKS)
				.then(TestMilestones.MS4, NO_CHECKS)
				.then(TestMilestones.MS5, NO_CHECKS)
				.build()
		);

		tcEA = TimeCollectorWithPath.createWithPath(
				new TestClock(), 
				AllowedPathsGraph
					.start(TestMilestones.MS1,REQUIRED_MULTISET)
					.then(TestMilestones.MS2, REQUIRED_MULTISET)
					.then(TestMilestones.MS3, REQUIRED_MULTISET)
					.then(TestMilestones.MS4, REQUIRED_MULTISET)
					.then(TestMilestones.MS5, REQUIRED_MULTISET)
					.build()
			);

		tcES = TimeCollectorWithPath.createWithPath(
				new TestClock(), 
				AllowedPathsGraph
					.start(TestMilestones.MS1,NOT_REQUIRED_BUT_SINGLESET)
					.then(TestMilestones.MS2, NOT_REQUIRED_BUT_SINGLESET)
					.then(TestMilestones.MS3, NOT_REQUIRED_BUT_SINGLESET)
					.then(TestMilestones.MS4, NOT_REQUIRED_BUT_SINGLESET)
					.then(TestMilestones.MS5, NOT_REQUIRED_BUT_SINGLESET)
					.build()
			);
		
		tcEAS = TimeCollectorWithPath.createWithPath(
			new TestClock(), 
			AllowedPathsGraph
				.start(TestMilestones.MS1,REQUIRED_AND_SINGLESET)
				.then(TestMilestones.MS2, REQUIRED_AND_SINGLESET)
				.then(TestMilestones.MS3, REQUIRED_AND_SINGLESET)
				.then(TestMilestones.MS4, REQUIRED_AND_SINGLESET)
				.then(TestMilestones.MS5, REQUIRED_AND_SINGLESET)
				.build()
		);
	}


	@Test
	public void testNormalE() {
		tcE.saveTime(TestMilestones.MS1);
		tcE.saveTime(TestMilestones.MS2);
		tcE.saveTime(TestMilestones.MS3);
		tcE.saveTime(TestMilestones.MS4);
		tcE.saveTime(TestMilestones.MS5);
		
		int count = 0;
		assertEquals(count++, tcE.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tcE.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tcE.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tcE.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tcE.getTime(TestMilestones.MS5).toEpochMilli());
	}


	@Test
	public void testNormalEA() {
		tcEA.saveTime(TestMilestones.MS1);
		tcEA.saveTime(TestMilestones.MS2);
		tcEA.saveTime(TestMilestones.MS3);
		tcEA.saveTime(TestMilestones.MS4);
		tcEA.saveTime(TestMilestones.MS5);
		
		int count = 0;
		assertEquals(count++, tcEA.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tcEA.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tcEA.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tcEA.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tcEA.getTime(TestMilestones.MS5).toEpochMilli());
	}

	@Test
	public void testNormalES() {
		tcES.saveTime(TestMilestones.MS1);
		tcES.saveTime(TestMilestones.MS2);
		tcES.saveTime(TestMilestones.MS3);
		tcES.saveTime(TestMilestones.MS4);
		tcES.saveTime(TestMilestones.MS5);
		
		int count = 0;
		assertEquals(count++, tcES.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tcES.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tcES.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tcES.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tcES.getTime(TestMilestones.MS5).toEpochMilli());
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
	
	
//	tc;
//	tcS;
//	tcA;
//	tcE;
//	tcEA;
//	tcAS;
//	tcES;
//	tcEAS;

	@Test (expected=MilestoneNotAllowedException.class)
	public void testOtherOrderE() {
		tcE.saveTime(TestMilestones.MS2);
		tcE.saveTime(TestMilestones.MS3);
		tcE.saveTime(TestMilestones.MS1);
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testOtherOrderEA() {
		tcEA.saveTime(TestMilestones.MS2);
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testOtherOrderES() {
		tcES.saveTime(TestMilestones.MS2);
		tcES.saveTime(TestMilestones.MS3);
		tcES.saveTime(TestMilestones.MS1);
	}

	@Test (expected=MilestoneNotAllowedException.class)
	public void testOtherOrderEAS() {
		tcEAS.saveTime(TestMilestones.MS2);
	}


	


}
