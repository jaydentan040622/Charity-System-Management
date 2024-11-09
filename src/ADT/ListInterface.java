package ADT;

import java.util.stream.Stream;

public interface ListInterface<T> extends Iterable<T> {

    boolean add(T newEntry);
    boolean add(int newPosition, T newEntry);
    boolean addAll(ListInterface<? extends T> items);
    boolean remove(T element);
    T remove(int index);
    void clear();
    boolean replace(int givenPosition, T newEntry);
    T getEntry(int givenPosition);
    boolean contains(T anEntry);
    int getNumberOfEntries();
    boolean isEmpty();
    boolean isFull();
    T get(int index);
    int size();
    Stream<T> stream();
    T linearSearch(T target);
    
}