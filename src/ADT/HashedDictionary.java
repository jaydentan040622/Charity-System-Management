package ADT;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
    private TableEntry<K, V>[] hashTable; // dictionary entries
    private int numberOfEntries;
    private static final int DEFAULT_SIZE = 101; // must be prime
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public HashedDictionary() {
        this(DEFAULT_SIZE);
    }

    @SuppressWarnings("unchecked")
    public HashedDictionary(int tableSize) {
        hashTable = new TableEntry[tableSize];
        numberOfEntries = 0;
    }

    public String toString() {
        StringBuilder outputStr = new StringBuilder();
        for (int index = 0; index < hashTable.length; index++) {
            outputStr.append(String.format("%4d. ", index));
            if (hashTable[index] == null) {
                outputStr.append("null\n");
            } else if (hashTable[index].isRemoved()) {
                outputStr.append("notIn\n");
            } else {
                outputStr.append(hashTable[index].getKey()).append(" ").append(hashTable[index].getValue())
                        .append("\n");
            }
        }
        return outputStr.toString();
    }

    @Override
    public V add(K key, V value) {
        if (isFull()) {
            rehash();
        }

        int index = getHashIndex(key);

        if (hashTable[index] == null || hashTable[index].isRemoved()) {
            hashTable[index] = new TableEntry<>(key, value);
            numberOfEntries++;
            return null;
        } else {
            V oldValue = hashTable[index].getValue();
            hashTable[index].setValue(value);
            return oldValue;
        }
    }

    @Override
    public K[] getKeys() {
        K[] keys = (K[]) new Object[numberOfEntries];
        int keyIndex = 0;
        for (TableEntry<K, V> entry : hashTable) {
            if (entry != null && entry.isIn()) {
                keys[keyIndex++] = entry.getKey();
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        int index = getHashIndex(key);
        index = locate(index, key);

        if (index != -1) {
            V removedValue = hashTable[index].getValue();
            hashTable[index].setToRemoved();
            numberOfEntries--;
            return removedValue;
        }
        return null;
    }

    @Override
    public V getValue(K key) {
        int index = getHashIndex(key);
        index = locate(index, key);

        if (index != -1) {
            return hashTable[index].getValue();
        }
        return null;
    }

    private int locate(int index, K key) {
        if (hashTable[index] == null || !key.equals(hashTable[index].getKey())) {
            return -1;
        }
        return index;
    }

    @Override
    public boolean contains(K key) {
        return getValue(key) != null;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public boolean isFull() {
        return (double) numberOfEntries / hashTable.length >= LOAD_FACTOR_THRESHOLD;
    }

    @Override
    public int getSize() {
        return numberOfEntries;
    }

    @Override
    public final void clear() {
        for (int index = 0; index < hashTable.length; index++) {
            hashTable[index] = null;
        }
        numberOfEntries = 0;
    }

    private int getHashIndex(K key) {
        int hashIndex = key.hashCode() % hashTable.length;
        if (hashIndex < 0) {
            hashIndex += hashTable.length;
        }
        return hashIndex;
    }

    private void rehash() {
        TableEntry<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = 2 * oldSize;
        hashTable = new TableEntry[newSize];
        numberOfEntries = 0;

        for (int index = 0; index < oldSize; index++) {
            if (oldTable[index] != null && oldTable[index].isIn()) {
                add(oldTable[index].getKey(), oldTable[index].getValue());
            }
        }
    }

    private class TableEntry<S, T> {
        private S key;
        private T value;
        private boolean inTable;

        private TableEntry(S searchKey, T dataValue) {
            key = searchKey;
            value = dataValue;
            inTable = true;
        }

        private S getKey() {
            return key;
        }

        private T getValue() {
            return value;
        }

        private void setValue(T newValue) {
            value = newValue;
        }

        private boolean isIn() {
            return inTable;
        }

        private boolean isRemoved() {
            return !inTable;
        }

        private void setToRemoved() {
            key = null;
            value = null;
            inTable = false;
        }
    }
}
