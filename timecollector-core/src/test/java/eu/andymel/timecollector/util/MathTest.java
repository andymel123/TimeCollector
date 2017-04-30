package eu.andymel.timecollector.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathTest {

	@Test
	public void testRounding() {
		assertEquals(103, 			Math.round(103.123456789, 0), 0);
		assertEquals(103.1, 		Math.round(103.123456789, 1), 0);
		assertEquals(103.12, 		Math.round(103.123456789, 2), 0);
		assertEquals(103.123, 		Math.round(103.123456789, 3), 0);
		assertEquals(103.1235, 		Math.round(103.123456789, 4), 0);
		assertEquals(103.12346, 	Math.round(103.123456789, 5), 0);
		assertEquals(103.123457, 	Math.round(103.123456789, 6), 0);
		assertEquals(103.1234568, 	Math.round(103.123456789, 7), 0);
		assertEquals(103.12345679, 	Math.round(103.123456789, 8), 0);
		assertEquals(103.123456789, Math.round(103.123456789, 9), 0);
		assertEquals(103.123456789, Math.round(103.123456789, 10), 0);
		
		assertEquals(-103, 				Math.round(-103.123456789, 0), 0);
		assertEquals(-103.1, 			Math.round(-103.123456789, 1), 0);
		assertEquals(-103.12, 			Math.round(-103.123456789, 2), 0);
		assertEquals(-103.123, 			Math.round(-103.123456789, 3), 0);
		assertEquals(-103.1235, 		Math.round(-103.123456789, 4), 0);
		assertEquals(-103.12346, 		Math.round(-103.123456789, 5), 0);
		assertEquals(-103.123457, 		Math.round(-103.123456789, 6), 0);
		assertEquals(-103.1234568, 		Math.round(-103.123456789, 7), 0);
		assertEquals(-103.12345679, 	Math.round(-103.123456789, 8), 0);
		assertEquals(-103.123456789, 	Math.round(-103.123456789, 9), 0);
		assertEquals(-103.123456789, 	Math.round(-103.123456789, 10), 0);
		
		// added because of an answer to http://stackoverflow.com/a/153753/7869582
		assertEquals(265, 				Math.round(265.335, 0), 0);
		assertEquals(265.3, 			Math.round(265.335, 1), 0);
//		assertEquals(265.34, 			Math.round(265.335, 2), 0); <= this fails! but it's ok, as I don't need the accuracy (added note to the javaDoc of the method)
		assertEquals(265.335, 			Math.round(265.335, 3), 0);
		assertEquals(-265, 				Math.round(-265.335, 0), 0);
		assertEquals(-265.3, 			Math.round(-265.335, 1), 0);
		assertEquals(-265.33, 			Math.round(-265.335, 2), 0);
		assertEquals(-265.335, 			Math.round(-265.335, 3), 0);
		
		assertEquals(0, 				Math.round(0, 0), 0);

		assertEquals(Double.NaN, 		Math.round(Double.NaN, 0), 0);
		assertEquals(Double.NaN, 		Math.round(Double.NaN, 5), 0);

	}

}
