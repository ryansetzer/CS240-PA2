import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Simple test driver for the Interval class.
 * 
 * @author RYAN SETZER
 * @version 1.0
 *
 */
class TestInterval {

	@Test
	public void testConstructor() {
		Interval interval = new Interval(2, 3);
		assertEquals(2, interval.size());
		assertEquals("2-3", interval.toString());
		assertThrows(IllegalArgumentException.class, () -> new Interval(3, 2));
		
	}
	
	@Test
	public void testSetBounds() {
		Interval interval = new Interval(2, 10);
		interval.setLowerBound(1);
		assertEquals(10, interval.size());
		interval.setUpperBound(20);
		assertEquals(20, interval.size());
	}
	
	@Test
	public void TestContains() {
		Interval interval = new Interval(1, 100);
		for (int i = 1; i <= 100; i++) {
			assertTrue(interval.contains(i));
		}
		assertFalse(interval.contains(0));
		assertFalse(interval.contains(101));

	}
	
	@Test
	public void TestEnclose() {
		Interval interval = new Interval(1, 1);
		assertEquals("1", interval.toString());
		// test upper
		interval.enclose(110);
		assertEquals("1-110", interval.toString());
		assertEquals(110, interval.size());
		// test lower
		interval.enclose(-10);
		assertEquals("-10-110", interval.toString());
		assertEquals(121, interval.size());
	}

}
