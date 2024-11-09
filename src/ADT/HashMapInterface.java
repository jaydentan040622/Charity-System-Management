package ADT;

public interface HashMapInterface<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    boolean containsKey(K key);
    boolean isEmpty();
    int size();
    V getOrDefault(K key, V defaultValue);
    K[] keySet();
    Entry<K, V>[] entrySet();

    interface Entry<K, V> {
        K getKey();
        V getValue();
        void setValue(V value);
    }
}
