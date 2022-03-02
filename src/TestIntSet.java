import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Map.entry;

/**
 * A test suite for LinkedIntSet.
 */
public class TestIntSet {
  public IntSet create() {
    return new LinkedIntSet();
  }

  private static final Map<String, String> stories = Map.ofEntries(
      entry("five oscillating", "add 5 {5}\nremove 5 {}\nadd 5 {5}\nremove 5 {}\nadd 5 {5}\nremove 5 {}"),
      entry("zero through five", "add 0 {0}\nadd 1 {0-1}\nadd 2 {0-2}\nadd 3 {0-3}\nadd 4 {0-4}\nadd 5 {0-5}"),
      entry("odds", "add 1 {1}\nadd 3 {1,3}\nadd 5 {1,3,5}\nadd 7 {1,3,5,7}\nadd 9 {1,3,5,7,9}\nadd 11 {1,3,5,7,9,11}\nadd 13 {1,3,5,7,9,11,13}"),
      entry("multiples of 10", "add 20 {20}\nadd 90 {20,90}\nadd 70 {20,70,90}\nadd 60 {20,60,70,90}\nadd 30 {20,30,60,70,90}\nadd 50 {20,30,50,60,70,90}\nadd 50 {20,30,50,60,70,90}\nadd 80 {20,30,50,60,70,80,90}\nadd 40 {20,30,40,50,60,70,80,90}\nadd 70 {20,30,40,50,60,70,80,90}\nadd 20 {20,30,40,50,60,70,80,90}\nadd 90 {20,30,40,50,60,70,80,90}\nadd 10 {10,20,30,40,50,60,70,80,90}\nremove 90 {10,20,30,40,50,60,70,80}\nremove 70 {10,20,30,40,50,60,80}\nremove 10 {20,30,40,50,60,80}\nremove 30 {20,40,50,60,80}\nremove 50 {20,40,60,80}\nremove 80 {20,40,60}\nremove 40 {20,60}\nremove 20 {60}\nremove 60 {}"),
      entry("outside-in-outside", "add 1 {1}\nadd 10 {1,10}\nadd 2 {1-2,10}\nadd 9 {1-2,9-10}\nadd 3 {1-3,9-10}\nadd 8 {1-3,8-10}\nadd 4 {1-4,8-10}\nadd 7 {1-4,7-10}\nadd 5 {1-5,7-10}\nadd 6 {1-10}\nremove 6 {1-5,7-10}\nremove 5 {1-4,7-10}\nremove 7 {1-4,8-10}\nremove 4 {1-3,8-10}\nremove 8 {1-3,9-10}\nremove 3 {1-2,9-10}\nremove 9 {1-2,10}\nremove 2 {1,10}\nremove 10 {1}\nremove 1 {}")
  );

  private static int[][] parseIntervals(String s) {
    String inside = s.substring(1, s.length() - 1);
    if (inside.isEmpty()) {
      return new int[0][];
    }
    String[] fields = inside.split(",");
    return Arrays.stream(fields).map((field) -> {
      int hyphenIndex = field.indexOf('-');
      if (hyphenIndex >= 0) {
        int lo = Integer.parseInt(field.substring(0, hyphenIndex));
        int hi = Integer.parseInt(field.substring(hyphenIndex + 1));
        return new int[]{lo, hi};
      } else {
        int x = Integer.parseInt(field);
        return new int[]{x, x};
      }
    }).toArray(int[][]::new);
  }

  @Test
  public void testStories() {
    for (Map.Entry<String, String> entry : stories.entrySet()) {
      IntSet set = create();
      Assertions.assertEquals(0, set.size(), "Set {} has the wrong size.");
      Assertions.assertEquals(0, set.getIntervalCount(), "Set {} has the wrong number of intervals.");
      Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
        set.getInterval(0);
      }, "Calling getInterval(0) on set {} is supposed to throw an exception.");
      runStory(set, entry.getValue());
    }
  }

  private void runStory(IntSet set, String story) {
    String[] lines = story.split("\\r?\\n", 0);
    String previousSet = set.toString();

    for (String line : lines) {
      String[] fields = line.split(" ");
      String action = fields[0];
      int x = Integer.parseInt(fields[1]);
      int[][] expectedIntervals = parseIntervals(fields[2]);

      if (action.equals("add")) {
        set.add(x);
      } else if (action.equals("remove")) {
        set.remove(x);
      }

      // Test toString.
      Assertions.assertEquals(fields[2], set.toString(), String.format("After calling %s(%d) on set %s, toString gives the wrong result.", action, x, previousSet));

      // Test size.
      int expectedSize = 0;
      for (int[] interval : expectedIntervals) {
        expectedSize += interval[1] - interval[0] + 1;
      }

      Assertions.assertEquals(expectedSize, set.size(), String.format("After calling %s(%d) on set %s, size gives the wrong result.", action, x, previousSet));
      Assertions.assertEquals(expectedSize == 0, set.isEmpty(), String.format("After calling %s(%d) on set %s, isEmpty gives the wrong result.", action, x, previousSet));

      // Test intervals.
      Assertions.assertEquals(expectedIntervals.length, set.getIntervalCount(), String.format("After calling %s(%d) on set %s, getIntervalCount gives the wrong result.", action, x, previousSet));
      boolean[] hundred = new boolean[100];
      for (int intervalIndex = 0; intervalIndex < expectedIntervals.length; ++intervalIndex) {
        int[] expectedInterval = expectedIntervals[intervalIndex];
        Interval actualInterval = set.getInterval(intervalIndex);
        Assertions.assertEquals(expectedInterval[0], actualInterval.getLowerBound(), String.format("After calling %s(%d) on set %s, getInterval(%d) has the wrong lower bound.", action, x, previousSet, expectedInterval[0]));
        Assertions.assertEquals(expectedInterval[1], actualInterval.getUpperBound(), String.format("After calling %s(%d) on set %s, getInterval(%d) has the wrong upper bound.", action, x, previousSet, expectedInterval[1]));
        for (int j = expectedInterval[0]; j <= expectedInterval[1]; ++j) {
          hundred[j] = true;
        }
      }

      // Test contains.
      ArrayList<Integer> sequence = new ArrayList<>();
      for (int j = 0; j < hundred.length; ++j) {
        Assertions.assertEquals(hundred[j], set.contains(j), String.format("Calling contains(%d) on set %s gives the wrong result.", j, set.toString()));
        if (hundred[j]) {
          sequence.add(j);
        }
      }

      // Test iterator.
      Iterator<Integer> it = set.iterator();
      for (Integer integer : sequence) {
        Assertions.assertTrue(it.hasNext(), String.format("Calling hasNext on an iterator for set %s gives the wrong result.", set.toString()));
        Assertions.assertTrue(it.hasNext(), "Calling hasNext twice in succession leads to a different result.");
        Integer actualNext = it.next();
        Assertions.assertEquals(integer, actualNext, String.format("Calling next on an iterator for set %s gives the wrong result.", set.toString()));
      }

      Assertions.assertFalse(it.hasNext(), "Calling hasNext on a depleted iterator gives the wrong result.");
      Assertions.assertThrows(NoSuchElementException.class, it::next, "Calling next on a depleted iterator is supposed to throw an exception.");

      previousSet = set.toString();
    }
  }

  @Test
  public void testSize() {
    IntSet set = create();
    Assertions.assertEquals(0, set.size(), "Set {} has the wrong size.");

    set.add(5);
    Assertions.assertEquals(1, set.size(), "Set {5} has the wrong size.");

    set.add(6);
    Assertions.assertEquals(2, set.size(), "Set {5-6} has the wrong size.");

    set.add(8);
    Assertions.assertEquals(3, set.size(), "Set {5-6,8} has the wrong size.");

    set.add(-100);
    Assertions.assertEquals(4, set.size(), "Set {-100,5-6,8} has the wrong size.");
  }

  @Test
  public void testContains() {
    IntSet set = create();

    Assertions.assertFalse(set.contains(0), "Set {} does not contain 0.");
    set.add(0);
    Assertions.assertTrue(set.contains(0), "Set {0} does contain 0.");

    set.add(1);
    Assertions.assertTrue(set.contains(1), "Set {0-1} does contain 1.");

    set.add(2);
    Assertions.assertTrue(set.contains(2), "Set {0-2} does contain 2.");

    set.add(10);
    Assertions.assertTrue(set.contains(10), "Set {0-2,10} does contain 10.");

    set.add(20);
    Assertions.assertTrue(set.contains(20), "Set {0-2,10,20} does contain 20.");

    set.add(9);
    Assertions.assertTrue(set.contains(9), "Set {0-2,9-10,20} does contain 9.");

    set.add(11);
    Assertions.assertTrue(set.contains(11), "Set {0-2,9-11,20} does contain 11.");
  }

  @Test
  public void testIsEmpty() {
    IntSet set = create();
    Assertions.assertTrue(set.isEmpty(), "Set {} is empty.");

    set.add(9999999);
    Assertions.assertFalse(set.isEmpty(), "Set {9999999} is not empty.");
  }

  @Test
  public void testIsEmptyAfterRemove() {
    IntSet set = create();
    Assertions.assertTrue(set.isEmpty(), "Set {} is empty.");

    set.add(10);
    Assertions.assertFalse(set.isEmpty(), "Set {10} is not empty.");

    set.add(30);
    Assertions.assertFalse(set.isEmpty(), "Set {10,30} is not empty.");

    set.add(31);
    Assertions.assertFalse(set.isEmpty(), "Set {10,30-31} is not empty.");

    set.remove(30);
    Assertions.assertFalse(set.isEmpty(), "Set {10,31} is not empty.");

    set.remove(10);
    Assertions.assertFalse(set.isEmpty(), "Set {31} is not empty.");

    set.remove(31);
    Assertions.assertTrue(set.isEmpty(), "Set {} is empty.");
  }

  @Test
  public void testSizeAfterRemove() {
    IntSet set = create();
    Assertions.assertEquals(0, set.size(), "Set {} has size 0.");

    set.add(3);
    Assertions.assertEquals(1, set.size(), "Set {3} has size 1.");

    set.remove(3);
    Assertions.assertEquals(0, set.size(), "Set {} has size 0.");

    set.add(30);
    set.add(32);
    set.add(31);
    Assertions.assertEquals(3, set.size(), "Set {30-32} has size 3.");

    set.remove(31);
    Assertions.assertEquals(2, set.size(), "Set {30,32} has size 2.");

    set.remove(30);
    Assertions.assertEquals(1, set.size(), "Set {32} has size 1.");

    set.remove(32);
    Assertions.assertEquals(0, set.size(), "Set {} has size 0.");
  }

  @Test
  public void testToStringAfterRemove() {
    IntSet set = create();

    Assertions.assertEquals("{}", set.toString(), "Calling toString on set {} doesn't yield the correct string.");

    set.add(5);
    set.add(7);
    Assertions.assertEquals("{5,7}", set.toString(), "Calling toString on set {5,7} doesn't yield the correct string.");

    set.add(6);
    Assertions.assertEquals("{5-7}", set.toString(), "Calling toString on set {5-7} doesn't yield the correct string.");

    set.add(100);
    Assertions.assertEquals("{5-7,100}", set.toString(), "Calling toString on set {5-7,100} doesn't yield the correct string.");

    set.add(101);
    Assertions.assertEquals("{5-7,100-101}", set.toString(), "Calling toString on set {5-7,100-101} doesn't yield the correct string.");

    set.add(-20);
    Assertions.assertEquals("{-20,5-7,100-101}", set.toString(), "Calling toString on set {-20,5-7,100-101} doesn't yield the correct string.");

    set.add(250);
    Assertions.assertEquals("{-20,5-7,100-101,250}", set.toString(), "Calling toString on set {-20,5-7,100-101,250} doesn't yield the correct string.");

    set.add(249);
    Assertions.assertEquals("{-20,5-7,100-101,249-250}", set.toString(), "Calling toString on set {-20,5-7,100-101,249-250} doesn't yield the correct string.");

    set.remove(-20);
    Assertions.assertEquals("{5-7,100-101,249-250}", set.toString(), "Calling toString on set {-20,5-7,100-101,249-250} after removing -20 doesn't yield the correct string.");
    set.add(-20);

    set.remove(101);
    Assertions.assertEquals("{-20,5-7,100,249-250}", set.toString(), "Calling toString on set {-20,5-7,100-101,249-250} after removing 101 doesn't yield the correct string.");
    set.add(101);

    set.remove(6);
    Assertions.assertEquals("{-20,5,7,100-101,249-250}", set.toString(), "Calling toString on set {-20,5,7,100-101,249-250} after removing 6 doesn't yield the correct string.");
    set.add(6);

    set.remove(249);
    Assertions.assertEquals("{-20,5-7,100-101,250}", set.toString(), "Calling toString on set {-20,5,7,100-101,249-250} after removing 249 doesn't yield the correct string.");
    set.add(249);
  }

  @Test
  public void testRemoveNonmember() {
    Assertions.assertThrows(NoSuchElementException.class, () -> {
      IntSet set = create();
      set.add(5);
      set.add(7);
      set.remove(6);
    });
  }

  @Test
  public void testRemoveEmpty() {
    Assertions.assertThrows(NoSuchElementException.class, () -> {
      IntSet set = create();
      set.remove(10);
    });
  }

  @Test
  public void testContainsAfterRemove() {
    IntSet set = create();
    set.add(13);
    set.remove(13);
    Assertions.assertFalse(set.contains(13), "After removing 13 from set {13}, it must not contain 13.");

    set.add(6);
    set.add(5);
    set.add(7);
    set.remove(6);
    Assertions.assertFalse(set.contains(6), "After removing 6 from set {5-7}, it must not contain 6.");

    set.add(10);
    set.add(11);
    set.add(12);
    set.remove(12);
    Assertions.assertFalse(set.contains(12), "After removing 12 from set {5-6,10-12}, it must not contain 12.");

    set.remove(10);
    Assertions.assertFalse(set.contains(10), "After removing 10 from set {5-6,10-11}, it must not contain 10.");

    set.add(1);
    set.remove(1);
    Assertions.assertFalse(set.contains(1), "After removing 1 from set {1,5-6,10-11}, it must not contain 10.");
  }

  @Test
  public void testIterator() {
    IntSet set = create();

    {
      final Iterator<Integer> it = set.iterator();
      Assertions.assertFalse(it.hasNext(), "Calling hasNext on a brand new iterator for {} must yield false.");
      Assertions.assertFalse(it.hasNext(), "Calling hasNext on a brand new iterator for {} must yield false.");
      Assertions.assertThrows(NoSuchElementException.class, () -> it.next(), "Calling next on a depleted iterator must throw an exception.");
    }

    set.add(3);

    {
      final Iterator<Integer> it = set.iterator();
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on a brand new iterator for {3} must yield true.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on a brand new iterator for {3} must yield true.");
      Assertions.assertEquals(3, it.next(), "Calling next on an iterator for {3} must yield 3.");
      Assertions.assertFalse(it.hasNext(), "Calling hasNext after iterating an iterator for {3} must yield false.");
      Assertions.assertThrows(NoSuchElementException.class, () -> it.next(), "Calling next on a depleted iterator must throw an exception.");
    }

    set.add(10);

    {
      final Iterator<Integer> it = set.iterator();
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on a brand new iterator for {3,10} must yield true.");
      Assertions.assertEquals(3, it.next(), "Calling next on an iterator for {3,10} must yield 3.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on an undepleted iterator for {3,10} must yield true.");
      Assertions.assertEquals(10, it.next(), "Calling next on an iterator for {3,10} must yield 10.");
      Assertions.assertFalse(it.hasNext(), "Calling hasNext after iterating an iterator for {3,10} must yield false.");
      Assertions.assertThrows(NoSuchElementException.class, () -> it.next(), "Calling next on a depleted iterator must throw an exception.");
    }

    set.add(5);

    {
      final Iterator<Integer> it = set.iterator();
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on a brand new iterator for {3,5,10} must yield true.");
      Assertions.assertEquals(3, it.next(), "Calling next on an iterator for {3,5,10} must yield 3.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on an undepleted iterator for {3,5,10} must yield true.");
      Assertions.assertEquals(5, it.next(), "Calling next on an iterator for {3,5,10} must yield 5.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on an undepleted iterator for {3,5,10} must yield true.");
      Assertions.assertEquals(10, it.next(), "Calling next on an iterator for {3,5,10} must yield 10.");
      Assertions.assertFalse(it.hasNext(), "Calling hasNext after iterating an iterator for {3,5,10} must yield false.");
      Assertions.assertThrows(NoSuchElementException.class, () -> it.next(), "Calling next on a depleted iterator must throw an exception.");
    }

    set.add(4);

    {
      Iterator<Integer> it = set.iterator();
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on a brand new iterator for {3-5,10} must yield true.");
      Assertions.assertEquals(3, it.next(), "Calling next on an iterator for {3-5,10} must yield 3.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on an undepleted iterator for {3-5,10} must yield true.");
      Assertions.assertEquals(4, it.next(), "Calling next on an iterator for {3-5,10} must yield 4.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on an undepleted iterator for {3-5,10} must yield true.");
      Assertions.assertEquals(5, it.next(), "Calling next on an iterator for {3-5,10} must yield 5.");
      Assertions.assertTrue(it.hasNext(), "Calling hasNext on an undepleted iterator for {3-5,10} must yield true.");
      Assertions.assertEquals(10, it.next(), "Calling next on an iterator for {3-5,10} must yield 10.");
      Assertions.assertFalse(it.hasNext(), "Calling hasNext after iterating an iterator for {3-5,10} must yield false.");
      Assertions.assertThrows(NoSuchElementException.class, () -> it.next(), "Calling next on a depleted iterator must throw an exception.");
    }
  }
}
