import java.util.Iterator;

/**
 * Intset Class for PA 2.
 * @author RYAN SETZER
 * @version 1.0
 *
 */
public interface IntSet extends Iterable<Integer> {
	public boolean isEmpty();
	
	public int size();
	
	public int getIntervalCount();
	
	public Interval getInterval(int index);
	
	public boolean add(int index);
	
	public void remove(int index);
	
	public String toString();
	
	public boolean equals(Object obj);

	boolean contains(int num);

	@Override
	public Iterator<Integer> iterator();
}

class LinkedIntSet implements IntSet {

	public Node head;
	public Node tail;
	private int size;

	public LinkedIntSet() {
		head = new Node(null, null, null);
		tail = new Node(null, head, null);
		head.setNext(tail);
		this.size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		int totalSize = 0;
		for (int i = 0; i < size; i++) {
			totalSize += get(i).getInterval().size();
		}
		return totalSize;
	}

	public int getIntervalCount() {
		return size;
	}

	public Interval getInterval(int index) {
		return get(index).getInterval();
	}

	public boolean add(int num) {
		if (contains(num)) {
			return false;
		}

		/**
		 * 5 Possible Outcomes
		 * 		1. Added IN FRONT of Index with NO combination
		 * 		2. Added BEHIND Index with NO combination
		 * 		3. Added BEHIND Index WITH combination
		 * 		4. Added INDEPENDENTLY
		 */


		size++;
		return false;
	}

	public void remove(int index) {
		//TODO
		size--;
	}

	public Node get(int index) {
		Node tempNode = head;
		for (int i = 0; i < index; i++) {
			tempNode = tempNode.getNext();
		}
		return tempNode;
	}

	public boolean contains(int num) {
		for (int i = 0; i < size; i++) {
			if (get(i).getInterval().contains(num)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String result = "{";
		for (int i = 0; i < size; i++) {
			result += get(i).getInterval().toString() + ",";
		}
		// remove extra ',' later
		return result += "}";
	}

	public boolean equals(Object obj) {
		if (obj instanceof IntSet) {
			return true;
		}
		return false;
	}

	public Iterator<Integer> iterator() {
		return null;
	}
}

class Node {

	private Node next;
	private Node prev;
	private Interval interval;

	public Node(Interval interval, Node next, Node prev) {
		this.interval = interval;
		this.next = next;
		this.prev = prev;
	}

	public Node getNext() {
		return next;
	}

	public Node getPrev() {
		return prev;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public void setPrev(Node prev) {
		this.prev = prev;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}
}
