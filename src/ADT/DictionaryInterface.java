package ADT;

public interface DictionaryInterface<K, V> {

    V add(K key, V value);

    V remove(K key);

    V getValue(K key);

    boolean contains(K key);

    boolean isEmpty();

    boolean isFull();

    int getSize();

    K[] getKeys(); // Replace Set<K> with K[] to return an array of keys

    void clear();
}
