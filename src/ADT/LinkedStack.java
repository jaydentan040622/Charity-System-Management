package ADT;


import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedStack<T> implements StackInterface<T>, Iterable<T> {

    private Node topNode;  // Reference to the top of the stack
    private int size;  // Track the number of elements in the stack

    public LinkedStack() {
        topNode = null;
        size = 0;
    }

    @Override
    public void push(T newEntry) {
        Node newNode = new Node(newEntry, topNode);
        topNode = newNode;
        size++;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            return null;  // Handle empty stack condition
        } else {
            T data = topNode.data;
            topNode = topNode.next;  // Remove the top element
            size--;
            return data;
        }
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();  // Handle empty stack condition
        } else {
            return topNode.data;
        }
    }

    @Override
    public boolean isEmpty() {
        return topNode == null;
    }

    @Override
    public void clear() {
        topNode = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedStackIterator();
    }

    private class LinkedStackIterator implements Iterator<T> {

        private Node currentNode = topNode;

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T data = currentNode.data;
            currentNode = currentNode.next;
            return data;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

    private class Node {

        private T data;  // Stack entry
        private Node next;  // Link to the next node

        private Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }


}
