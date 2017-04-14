package eu.andymel.timecollector.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AvgMaxCalcLongTest {

	private AvgMaxCalcLong calc;
	
	@Before
	public void setup() {
		calc = AvgMaxCalcLong.create();
	}

	
	@Test
	public void testAdd0() {
		calc.add(0);
		calc.add(0);
		calc.add(0);
		calc.add(0);
		
		assertEquals(0, calc.getAvg(), 0);
		assertEquals(0, calc.getMax());
		assertEquals(0, calc.getMin());
		assertEquals(4, calc.getCount());
	}

	@Test
	public void testAvg1() {
		calc.add(0);
		calc.add(0);
		calc.add(0);
		calc.add(1);
		
		assertEquals(0.25, calc.getAvg(), 0);
		assertEquals(1, calc.getMax());
		assertEquals(0, calc.getMin());
		assertEquals(4, calc.getCount());
	}

	@Test
	public void testAvg2() {
		calc.add(1);
		calc.add(1);
		calc.add(3);
		calc.add(3);
		
		assertEquals(2, calc.getAvg(), 0);
		assertEquals(3, calc.getMax());
		assertEquals(1, calc.getMin());
		assertEquals(4, calc.getCount());
	}

	@Test
	public void testAvgMaxValue() {
		calc.add(Long.MAX_VALUE);
		calc.add(Long.MAX_VALUE);
		calc.add(Long.MAX_VALUE);
		calc.add(Long.MAX_VALUE);
		
		assertEquals(Long.MAX_VALUE, calc.getAvg(), 0);
		assertEquals(Long.MAX_VALUE, calc.getMax());
		assertEquals(Long.MAX_VALUE, calc.getMin());
		assertEquals(4, calc.getCount());
	}

	@Test
	public void testAvg4() {
		calc.add(0);
		calc.add(0);
		calc.add(0);
		calc.add(4);
		
		assertEquals(1, calc.getAvg(), 0);
		assertEquals(4, calc.getMax());
		assertEquals(0, calc.getMin());
		assertEquals(4, calc.getCount());
	}

	@Test
	public void testALot() {
		int amount = 10_000_000;
		
		for(int i=1; i<=amount; i++){
			calc.add(i);
		}
		
		assertEquals(amount/2d, calc.getAvg(), 0.5);
		assertEquals(amount, calc.getMax());
		assertEquals(1, calc.getMin());
		assertEquals(amount, calc.getCount());
	}

	

}
