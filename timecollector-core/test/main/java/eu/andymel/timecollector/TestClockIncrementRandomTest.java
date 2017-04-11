package eu.andymel.timecollector;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestClockIncrementRandomTest {

	@Test
	public void testTimeDifference() {
		int amount = 500_000;
		int min = 1000;
		int max = 2000;
		TestClockIncrementRandom clock = new TestClockIncrementRandom(min, max);
		
		long lastTime = clock.instant().toEpochMilli();
		for(int i=0; i<amount; i++){
			long millis = clock.instant().toEpochMilli();
			long diff = millis-lastTime; 
			assertTrue("Wrong: "+min+" =< " +diff+" =< "+max, diff>=min && diff<=max);
			lastTime = millis;
//			System.out.println(millis);
		}
		
	}

}
