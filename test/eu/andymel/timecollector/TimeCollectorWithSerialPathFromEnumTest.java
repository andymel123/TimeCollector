package eu.andymel.timecollector;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;

public class TimeCollectorWithSerialPathFromEnumTest {

	TimeCollector<TestMilestones> tc;
	TimeCollector<TestMilestones> tcS;
	TimeCollector<TestMilestones> tcE;
	TimeCollector<TestMilestones> tcEA;
	TimeCollector<TestMilestones> tcES;
	TimeCollector<TestMilestones> tcEAS;
	
	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5
	}
	
	@Before
	public void setup(){									// 	   EnsureOrder 	AllRequired SingleSet
		tc 		= TimeCollectorSerial.create(TestMilestones.class, false, 		false, 		false);
		tcS 	= TimeCollectorSerial.create(TestMilestones.class, false, 		false, 		true);
		tcE 	= TimeCollectorSerial.create(TestMilestones.class, true, 		false, 		false);
		tcEA	= TimeCollectorSerial.create(TestMilestones.class, true, 		true, 		false);
		tcES 	= TimeCollectorSerial.create(TestMilestones.class, true, 		false, 		true);
		tcEAS	= TimeCollectorSerial.create(TestMilestones.class, true, 		true, 		true);
		
		// The permutations tcA and tcAS would make no sense as allRequired is only possible if EnsureOrder is true as well

	}
	
	@Test
	public void testNormal() {
		tc.saveTime(TestMilestones.MS1);
		tc.saveTime(TestMilestones.MS2);
		tc.saveTime(TestMilestones.MS3);
		tc.saveTime(TestMilestones.MS4);
		tc.saveTime(TestMilestones.MS5);

		assertNotNull(tc.getTime(TestMilestones.MS1));
		assertNotNull(tc.getTime(TestMilestones.MS2));
		assertNotNull(tc.getTime(TestMilestones.MS3));
		assertNotNull(tc.getTime(TestMilestones.MS4));
		assertNotNull(tc.getTime(TestMilestones.MS5));
	}
	@Test
	public void testNormalS() {
		tcS.saveTime(TestMilestones.MS1);
		tcS.saveTime(TestMilestones.MS2);
		tcS.saveTime(TestMilestones.MS3);
		tcS.saveTime(TestMilestones.MS4);
		tcS.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcS.getTime(TestMilestones.MS1));
		assertNotNull(tcS.getTime(TestMilestones.MS2));
		assertNotNull(tcS.getTime(TestMilestones.MS3));
		assertNotNull(tcS.getTime(TestMilestones.MS4));
		assertNotNull(tcS.getTime(TestMilestones.MS5));
	}

	@Test
	public void testNormalE() {
		tcE.saveTime(TestMilestones.MS1);
		tcE.saveTime(TestMilestones.MS2);
		tcE.saveTime(TestMilestones.MS3);
		tcE.saveTime(TestMilestones.MS4);
		tcE.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcE.getTime(TestMilestones.MS1));
		assertNotNull(tcE.getTime(TestMilestones.MS2));
		assertNotNull(tcE.getTime(TestMilestones.MS3));
		assertNotNull(tcE.getTime(TestMilestones.MS4));
		assertNotNull(tcE.getTime(TestMilestones.MS5));
	}


	@Test
	public void testNormalEA() {
		tcEA.saveTime(TestMilestones.MS1);
		tcEA.saveTime(TestMilestones.MS2);
		tcEA.saveTime(TestMilestones.MS3);
		tcEA.saveTime(TestMilestones.MS4);
		tcEA.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcEA.getTime(TestMilestones.MS1));
		assertNotNull(tcEA.getTime(TestMilestones.MS2));
		assertNotNull(tcEA.getTime(TestMilestones.MS3));
		assertNotNull(tcEA.getTime(TestMilestones.MS4));
		assertNotNull(tcEA.getTime(TestMilestones.MS5));
	}

	@Test
	public void testNormalES() {
		tcES.saveTime(TestMilestones.MS1);
		tcES.saveTime(TestMilestones.MS2);
		tcES.saveTime(TestMilestones.MS3);
		tcES.saveTime(TestMilestones.MS4);
		tcES.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcES.getTime(TestMilestones.MS1));
		assertNotNull(tcES.getTime(TestMilestones.MS2));
		assertNotNull(tcES.getTime(TestMilestones.MS3));
		assertNotNull(tcES.getTime(TestMilestones.MS4));
		assertNotNull(tcES.getTime(TestMilestones.MS5));
	}

	@Test
	public void testNormalEAS() {
		tcEAS.saveTime(TestMilestones.MS1);
		tcEAS.saveTime(TestMilestones.MS2);
		tcEAS.saveTime(TestMilestones.MS3);
		tcEAS.saveTime(TestMilestones.MS4);
		tcEAS.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcEAS.getTime(TestMilestones.MS1));
		assertNotNull(tcEAS.getTime(TestMilestones.MS2));
		assertNotNull(tcEAS.getTime(TestMilestones.MS3));
		assertNotNull(tcEAS.getTime(TestMilestones.MS4));
		assertNotNull(tcEAS.getTime(TestMilestones.MS5));
	}
	
	
//	tc;
//	tcS;
//	tcA;
//	tcE;
//	tcEA;
//	tcAS;
//	tcES;
//	tcEAS;

	
	@Test
	public void testOtherOrder() {
		tc.saveTime(TestMilestones.MS2);
		tc.saveTime(TestMilestones.MS3);
		tc.saveTime(TestMilestones.MS1);
		tc.saveTime(TestMilestones.MS4);
		tc.saveTime(TestMilestones.MS5);

		assertNotNull(tc.getTime(TestMilestones.MS1));
		assertNotNull(tc.getTime(TestMilestones.MS2));
		assertNotNull(tc.getTime(TestMilestones.MS3));
		assertNotNull(tc.getTime(TestMilestones.MS4));
		assertNotNull(tc.getTime(TestMilestones.MS5));
	}

	@Test
	public void testOtherOrderS() {
		tcS.saveTime(TestMilestones.MS2);
		tcS.saveTime(TestMilestones.MS3);
		tcS.saveTime(TestMilestones.MS1);
		tcS.saveTime(TestMilestones.MS4);
		tcS.saveTime(TestMilestones.MS5);

		assertNotNull(tcS.getTime(TestMilestones.MS1));
		assertNotNull(tcS.getTime(TestMilestones.MS2));
		assertNotNull(tcS.getTime(TestMilestones.MS3));
		assertNotNull(tcS.getTime(TestMilestones.MS4));
		assertNotNull(tcS.getTime(TestMilestones.MS5));
	}

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

	@Test
	public void testOtherOrderESFirstMilestone() {
		tcES.saveTime(TestMilestones.MS2);
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
