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
		return false;
	}
	
	public int size() {
		return -1;
	}
	
	public int getIntervalCount(Interval interval) {
		return -1;
	}
	
	public Interval getInterval(int index) {
		return null;
	}
	
	public boolean add(int index) {
		return false;
	}
	
	public void remove(int index) {
	}
	
	public String toString() {
		return null;
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
		head = new Node(null, null, null, null);
		tail = new Node(null, null, head, null);
		head.setNext(tail);
		this.size = 0;
	}

	public void add(int index) {
		if (index > size || size < 0) {
			throw new IllegalArgumentException();
		} else {
			Node tempNode = head;
			for (int i = 0; i < index; i++) {
				tempNode = tempNode.getNext();
			}
			// curently working here, need to find a way to insert new node
		}
		size++;
	}

	@Override
	public Iterator<Integer> iterator() {
		return null;
	}
}

class Node {

	private Node next;
	private Node prev;
	private Integer lower;
	private Integer upper;

	public Node(Integer lower, Integer upper, Node next, Node prev) {
		this.lower = lower;
		this.upper = upper;
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
}
