/**
 * Interval Class for PA 2.
 * @author RYAN SETZER
 * @version 1.0
 */
public class Interval {
	
	private int lower;
	private int upper;

	public Interval(int lower, int upper) {
		if (lower > upper) {
			throw new IllegalArgumentException();
		} else {
			this.lower = lower;
			this.upper = upper;
		}
	}
	
	public int size() {
		return upper - lower + 1;
	}
	
	public void enclose(int i) {
		if (i > upper) {
			this.upper = i;
		}
		if (i < lower) {
			this.lower = i;
		}
	}
	
	public void setLowerBound(int i) {
		this.lower = i;
	}
	
	public void setUpperBound(int i) {
		this.upper = i;
	}
	
	public boolean contains(int i) {
		return i >= lower && i <= upper;
	}
	
	public String toString() {
		String result = String.valueOf(lower);
		if (upper != lower) {
			result += "-" + upper;
		}
		return result;
	}
}
