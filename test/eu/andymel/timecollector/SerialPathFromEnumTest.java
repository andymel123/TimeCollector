package eu.andymel.timecollector;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import eu.andymel.timecollector.exceptions.MilestoneNotAllowedException;

public class SerialPathFromEnumTest {

	private TimeCollectorSerial<TestMilestones> tc;
	private TimeCollectorSerial<TestMilestones> tcS;
	private TimeCollectorSerial<TestMilestones> tcE;
	private TimeCollectorSerial<TestMilestones> tcEA;
	private TimeCollectorSerial<TestMilestones> tcES;
	private TimeCollectorSerial<TestMilestones> tcEAS;
	
	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5
	}
	
	@Before
	public void setup(){														//		EnsureOrder AllRequired SingleSet
		tc 		= TimeCollectorSerial.create(TestMilestones.class, new TestClock(), 	false, 		false, 		false);
		tcS 	= TimeCollectorSerial.create(TestMilestones.class, new TestClock(), 	false, 		false, 		true);
		tcE 	= TimeCollectorSerial.create(TestMilestones.class, new TestClock(), 	true, 		false, 		false);
		tcEA	= TimeCollectorSerial.create(TestMilestones.class, new TestClock(), 	true, 		true, 		false);
		tcES 	= TimeCollectorSerial.create(TestMilestones.class, new TestClock(), 	true, 		false, 		true);
		tcEAS	= TimeCollectorSerial.create(TestMilestones.class, new TestClock(), 	true, 		true, 		true);
		
		// The permutations tcA and tcAS would make no sense as allRequired is only possible if EnsureOrder is true as well

	}
	
	@Test
	public void testNormal() {
		tc.saveTime(TestMilestones.MS1);
		tc.saveTime(TestMilestones.MS2);
		tc.saveTime(TestMilestones.MS3);
		tc.saveTime(TestMilestones.MS4);
		tc.saveTime(TestMilestones.MS5);

		int count = 0;
		assertEquals(count++, tc.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tc.getTime(TestMilestones.MS5).toEpochMilli());
	}
	@Test
	public void testNormalS() {
		tcS.saveTime(TestMilestones.MS1);
		tcS.saveTime(TestMilestones.MS2);
		tcS.saveTime(TestMilestones.MS3);
		tcS.saveTime(TestMilestones.MS4);
		tcS.saveTime(TestMilestones.MS5);
		
		int count = 0;
		assertEquals(count++, tcS.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(count++, tcS.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(count++, tcS.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(count++, tcS.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(count++, tcS.getTime(TestMilestones.MS5).toEpochMilli());
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

	
	@Test
	public void testOtherOrder() {
		tc.saveTime(TestMilestones.MS2);
		tc.saveTime(TestMilestones.MS3);
		tc.saveTime(TestMilestones.MS1);
		tc.saveTime(TestMilestones.MS4);
		tc.saveTime(TestMilestones.MS5);

		assertEquals(2, tc.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(0, tc.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(1, tc.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(3, tc.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(4, tc.getTime(TestMilestones.MS5).toEpochMilli());
	}

	@Test
	public void testOtherOrderS() {
		tcS.saveTime(TestMilestones.MS2);
		tcS.saveTime(TestMilestones.MS3);
		tcS.saveTime(TestMilestones.MS1);
		tcS.saveTime(TestMilestones.MS4);
		tcS.saveTime(TestMilestones.MS5);

		assertEquals(2, tcS.getTime(TestMilestones.MS1).toEpochMilli());
		assertEquals(0, tcS.getTime(TestMilestones.MS2).toEpochMilli());
		assertEquals(1, tcS.getTime(TestMilestones.MS3).toEpochMilli());
		assertEquals(3, tcS.getTime(TestMilestones.MS4).toEpochMilli());
		assertEquals(4, tcS.getTime(TestMilestones.MS5).toEpochMilli());
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
		assertEquals(0, tcES.getTime(TestMilestones.MS2).toEpochMilli());
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
