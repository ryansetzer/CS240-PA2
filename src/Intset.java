import java.util.Iterator;

/**
 * Intset Class for PA 2.
 * @author RYAN SETZER
 * @version 1.0
 *
 */
public class Intset implements Iterable<Integer> {

	private LinkedIntSet integerSet;
	
	public boolean isEmpty() {
		return integerSet.size() == 0;
	}
	
	public int size() {
		int totalSize = 0;
		for (int i = 0; i < integerSet.size(); i++) {
			totalSize += integerSet.get(i).getInterval().size();
		}
		return totalSize;
	}
	
	public int getIntervalCount(Interval interval) {
		return -1;
	}
	
	public Interval getInterval(int index) {
		return integerSet.get(index).getInterval();
	}
	
	public boolean add(int index) {
		return false;
	}
	
	public void remove(int index) {
	}
	
	public String toString() {
		String result = "{";
		for (int i = 0; i < integerSet.size(); i++) {
			result += integerSet.get(i).getInterval().toString() + ",";
		}
		// remove extra ',' later
		return result += "}";
	}
	
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		return null;
	}
}

class LinkedIntSet implements Iterable<Integer> {

	public Node head;
	public Node tail;
	private int size;

	public LinkedIntSet() {
		head = new Node(null, null, null);
		tail = new Node(null, head, null);
		head.setNext(tail);
		this.size = 0;
	}

	public void add(int index) {
		if (index > size || size < 0) {
			throw new IllegalArgumentException();
		} else {
			Node tempNode = get(index);
			// curently working here, need to find a way to insert new node
		}
		size++;
	}

	public Node get(int index) {
		Node tempNode = head;
		for (int i = 0; i < index; i++) {
			tempNode = tempNode.getNext();
		}
		return tempNode;
	}

	public int size() {
		return size;
	}

	@Override
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
