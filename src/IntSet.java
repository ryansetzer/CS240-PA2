import java.util.Iterator;
import java.util.NoSuchElementException;

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

/**
 * Linked Integer Set class for CS240 PA 2.
 */
class LinkedIntSet implements IntSet {

	public Node head;
	public Node tail;
	private int size;

	/**
	 * Constructor for Linked Integer Set Class.
	 */
	public LinkedIntSet() {
		head = new Node(null, null, null);
		tail = new Node(null, null, head);
		head.setNext(tail);
		this.size = 0;
	}

	/**
	 * Calculates if Integer Set is empty.
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Calculates the number of values stored in Integer Set.
	 * @return the number of values stored in Integer Set.
	 */
	public int size() {
		int totalSize = 0;
		for (int i = 0; i < size; i++) {
			totalSize += get(i).getInterval().size();
		}
		return totalSize;
	}

	/**
	 * Retrieves number of intervals used to store all values.
	 * @return number of intervals used to store all values.
	 */
	public int getIntervalCount() {
		return size;
	}

	/**
	 * Retrieves the interval given the index provided in param.
	 * @param index - Index of interval retrieving.
	 * @return The interval of index provided.
	 */
	public Interval getInterval(int index) {
		return get(index).getInterval();
	}

	/**
	 * Calculates and Adds the value to the Integer Set provided in param.
	 * @param num - value being added to Integer Set.
	 * @return true if value is added, false otherwise.
	 */
	public boolean add(int num) {
		// "false if it was already a member"
		if (contains(num)) {
			return false;
		}

		/**
		 * 4 Possible Adding Outcomes:
		 * 		1. Added IN FRONT of Index with NO combination
		 * 		2. Added BEHIND Index with NO combination
		 * 		3. Added to Index WITH combination
		 * 		4. Added INDEPENDENTLY
		 */
		if (contains(num + 1) && !contains(num - 1)) { //			1
			Node tempNode = findNode(num + 1);
			Interval replacementInterval = new Interval(num, tempNode.getInterval().getUpperBound());
			tempNode.setInterval(replacementInterval);
		} else if (!contains(num + 1) && contains(num - 1)) { //	2
			Node tempNode = findNode(num - 1);
			Interval replacementInterval = new Interval(tempNode.getInterval().getLowerBound(), num);
			tempNode.setInterval(replacementInterval);
		} else if (contains(num - 1) && contains(num + 1)) { // 	3
			Node lowerNode = findNode(num - 1);
			Node upperNode = lowerNode.getNext();
			Interval replacementInterval = new Interval(lowerNode.getInterval().getLowerBound(), upperNode.getInterval().getUpperBound());
			lowerNode.setInterval(replacementInterval);
			lowerNode.setNext(upperNode.getNext());
			upperNode.getNext().setPrev(lowerNode);
			size--;
		} else { //													4
			Node tempNode = head.getNext();
			while (tempNode != tail && tempNode.getInterval().getLowerBound() < num) {
				tempNode = tempNode.getNext();
			}
			Node node = new Node(new Interval(num, num), tempNode, tempNode.getPrev());
			Node previousNode = tempNode.getPrev();
			node.getNext().setPrev(node);
			previousNode.setNext(node);
			// Have the Node above needing to be placed
			size++;
		}
		return true;
	}

	/**
	 * Calculates and Removes the value in Integer Set provided in param.
	 * @param num - Value being removed from Integer Set.
	 */
	public void remove(int num) {
		if (findNode(num) == null) {
			throw new NoSuchElementException();
		}

		// Node that value (num) is found within
		Node tempNode = findNode(num);

		/**
		 * 4 Possible Removing Outcomes:
		 * 		1. Removing INDIVIDUAL Element
		 * 		2. Removing Element in BEGINNING of Interval
		 * 		3. Removing Element on the END of Interval
		 * 		4. Removing Element WITHIN Interval
		 */

		if (tempNode.getInterval().size() == 1) { //						1
			tempNode.getPrev().setNext(tempNode.getNext());
			tempNode.getNext().setPrev(tempNode.getPrev());
			size--;
		} else if (num == tempNode.getInterval().getLowerBound()) { // 		2
			tempNode.getInterval().setLowerBound(num + 1);
		} else if (num == tempNode.getInterval().getUpperBound()) {	//		3
			tempNode.getInterval().setUpperBound(num - 1);
		} else { //															4
			Interval interval = new Interval(num + 1, tempNode.getInterval().getUpperBound());
			Interval replacementInterval = new Interval(tempNode.getInterval().getLowerBound(), num - 1);
			Node node = new Node(interval, tempNode.getNext(), tempNode);
			tempNode.setInterval(replacementInterval);
			tempNode.setNext(node);
			node.getNext().setPrev(node);
			size++;
		}

	}

	/**
	 * Retrieves the Node in the index provided by param.
	 * @param index - Index of node being retrieved.
	 * @return - Node in the index provided by param.
	 */
	public Node get(int index) {
		if (isEmpty()) {
			throw new IndexOutOfBoundsException();
		}
		Node tempNode = head.getNext();
		for (int i = 0; i < index; i++) {
			tempNode = tempNode.getNext();
		}
		return tempNode;
	}

	/**
	 * Determines if value provided in param is within Integer Set.
	 * @param num - Value being determined if within Integer Set.
	 * @return - true if within Integer Set, false otherwise.
	 */
	public boolean contains(int num) {
		if (isEmpty()) {
			return false;
		}

		for (int i = 0; i < size; i++) {
			if (get(i).getInterval().contains(num)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * To String method for Linked Integer Set Class.
	 * @return - String representation of Integer Set Class.
	 */
	public String toString() {
		String result = "{";
		for (int i = 0; i < size; i++) {
			result += get(i).getInterval().toString() + ',';
		}
		result = result.substring(0, Math.max(result.length() - 1, 1));
		return result += "}";
	}

	/**
	 * Determines if Object provided by param is equivalent to Integer Set.
	 * @param obj - Object being compared with Integer Set.
	 * @return true if equivalent, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof IntSet) {
			return true;
		}
		return false;
	}

	/**
	 * Determines the Node in with value provided in param is within.
	 * @param num - Integer being checked.
	 * @return - The Node that num is found in.
	 */
	private Node findNode(int num) {
		for (int i = 0; i < getIntervalCount(); i++) {
			if (get(i).getInterval().contains(num)) {
				return get(i);
			}
		}
		return null;
	}

	/**
	 * Iterator for Linked Integer Set Class.
	 * @return Iterator for Linked Integer Set Class.
	 */
	public Iterator<Integer> iterator() {
		return new IntSetIterator();
	}

	private class IntSetIterator implements Iterator<Integer> {

		int currentPosition = -1;

		@Override
		public boolean hasNext() {
			return currentPosition + 1 < size();
		}

		@Override
		public Integer next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			currentPosition++;
			int count = 0;
			Node tempNode = head.getNext();
			while (count + tempNode.getInterval().size() <=  currentPosition) {
				count += tempNode.getInterval().size();
				tempNode = tempNode.getNext();
			}
			return tempNode.getInterval().getLowerBound() + (currentPosition - count);
		}
	}

	/**
	 * Node Class for CS240 PA 2.
	 */
	class Node {

		private Node next;
		private Node prev;
		private Interval interval;

		/**
		 * Constructor for Node Class.
		 * @param interval - Interval stored in Node.
		 * @param next - Node following current Node in Linked List.
		 * @param prev - Node preceding current Node in Linked List.
		 */
		public Node(Interval interval, Node next, Node prev) {
			this.interval = interval;
			this.next = next;
			this.prev = prev;
		}

		/**
		 * Getter for the Node following current Node in Linked List.
		 * @return Node following current Node in Linked List.
		 */
		public Node getNext() {
			return next;
		}

		/**
		 * Getter for the Node preceding current Node in Linked List.
		 * @return - Node preceding current Node in Linked List.
		 */
		public Node getPrev() {
			return prev;
		}

		/**
		 * Setter for the Node following current Node in Linked List.
		 * @param next - Node being set following current Node in Linked List.
		 */
		public void setNext(Node next) {
			this.next = next;
		}

		/**
		 * Setter for the Node preceding current Node in Linked List.
		 * @param prev - Node being set preceding current Node in Linked List.
		 */
		public void setPrev(Node prev) {
			this.prev = prev;
		}

		/**
		 * Getter for the Interval stored in Node.
		 * @return Interval stored in Node.
		 */
		public Interval getInterval() {
			return interval;
		}

		/**
		 * Setter for the Interval stored in Node.
		 * @param interval - Interval being set for the Interval stored in Node.
		 */
		public void setInterval(Interval interval) {
			this.interval = interval;
		}

	}
}
