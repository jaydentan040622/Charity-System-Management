package ADT;

public interface TreeMapInterface<K, V> {

    void put(K key, V value);

    V remove(K key);

    V get(K key);

    boolean containsKey(K key);

    int size();

    boolean isEmpty();

    K firstKey();

    K lastKey();

    TreeMapInterface<K, V> subMap(K fromKey, K toKey);

    Iterable<CustomEntry<K, V>> entries();

    void clear();

    class CustomEntry<S, T> {
        private S key;
        private T value;

        public CustomEntry(S key, T value) {
            this.key = key;
            this.value = value;
        }

        public S getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
