package eu.andymel.timecollector;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TimeCollectorWithSerialPathFromEnumTest {

	TimeCollector<TestMilestones> tcRS;
	TimeCollector<TestMilestones> tcR;
	TimeCollector<TestMilestones> tcS;
	TimeCollector<TestMilestones> tc;
	
	private enum TestMilestones{
		MS1,MS2,MS3,MS4,MS5
	}
	
	@Before
	public void setup(){
		tcRS= TimeCollectorWithPath.createSerial(TestMilestones.class, true, true);
		tcR = TimeCollectorWithPath.createSerial(TestMilestones.class, false, false);
		tcS = TimeCollectorWithPath.createSerial(TestMilestones.class, false, true);
		tc 	= TimeCollectorWithPath.createSerial(TestMilestones.class, false, false);
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
	public void testNormalR() {
		tcR.saveTime(TestMilestones.MS1);
		tcR.saveTime(TestMilestones.MS2);
		tcR.saveTime(TestMilestones.MS3);
		tcR.saveTime(TestMilestones.MS4);
		tcR.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcR.getTime(TestMilestones.MS1));
		assertNotNull(tcR.getTime(TestMilestones.MS2));
		assertNotNull(tcR.getTime(TestMilestones.MS3));
		assertNotNull(tcR.getTime(TestMilestones.MS4));
		assertNotNull(tcR.getTime(TestMilestones.MS5));
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
	public void testNormalRS() {
		tcRS.saveTime(TestMilestones.MS1);
		tcRS.saveTime(TestMilestones.MS2);
		tcRS.saveTime(TestMilestones.MS3);
		tcRS.saveTime(TestMilestones.MS4);
		tcRS.saveTime(TestMilestones.MS5);
		
		assertNotNull(tcRS.getTime(TestMilestones.MS1));
		assertNotNull(tcRS.getTime(TestMilestones.MS2));
		assertNotNull(tcRS.getTime(TestMilestones.MS3));
		assertNotNull(tcRS.getTime(TestMilestones.MS4));
		assertNotNull(tcRS.getTime(TestMilestones.MS5));
	}


}
