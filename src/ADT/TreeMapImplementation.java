package ADT;

import java.util.Comparator;

public class TreeMapImplementation<K, V> implements TreeMapInterface<K, V> {

    private final Comparator<? super K> comparator;
    private Node<K, V> root;
    private int size;

    // Node class for the custom tree structure
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = this.right = null;
        }
    }

    // Default constructor
    public TreeMapImplementation() {
        this(null);
    }

    // Constructor with a comparator
    public TreeMapImplementation(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.root = null;
        this.size = 0;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private Node<K, V> put(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }

        int cmp = compare(key, node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public V remove(K key) {
        Node<K, V> removedNode = new Node<>(null, null);
        root = remove(root, key, removedNode);
        return removedNode.value;
    }

    private Node<K, V> remove(Node<K, V> node, K key, Node<K, V> removedNode) {
        if (node == null) return null;

        int cmp = compare(key, node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key, removedNode);
        } else if (cmp > 0) {
            node.right = remove(node.right, key, removedNode);
        } else {
            removedNode.key = node.key;
            removedNode.value = node.value;
            size--;

            if (node.right == null) return node.left;
            if (node.left == null) return node.right;

            Node<K, V> t = node;
            node = min(t.right);
            node.right = removeMin(t.right);
            node.left = t.left;
        }
        return node;
    }

    private Node<K, V> min(Node<K, V> node) {
        if (node.left == null) return node;
        return min(node.left);
    }

    private Node<K, V> removeMin(Node<K, V> node) {
        if (node.left == null) return node.right;
        node.left = removeMin(node.left);
        return node;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = get(root, key);
        return (node == null) ? null : node.value;
    }

    private Node<K, V> get(Node<K, V> node, K key) {
        if (node == null) return null;

        int cmp = compare(key, node.key);
        if (cmp < 0) return get(node.left, key);
        if (cmp > 0) return get(node.right, key);
        return node;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public K firstKey() {
        if (root == null) return null;
        return min(root).key;
    }

    @Override
    public K lastKey() {
        Node<K, V> node = root;
        if (node == null) return null;

        while (node.right != null) {
            node = node.right;
        }
        return node.key;
    }

    @Override
    public TreeMapInterface<K, V> subMap(K fromKey, K toKey) {
        TreeMapImplementation<K, V> subMap = new TreeMapImplementation<>(comparator);
        addRange(root, fromKey, toKey, subMap);
        return subMap;
    }

    private void addRange(Node<K, V> node, K fromKey, K toKey, TreeMapImplementation<K, V> subMap) {
        if (node == null) return;

        int cmpFrom = compare(fromKey, node.key);
        int cmpTo = compare(toKey, node.key);

        if (cmpFrom < 0) {
            addRange(node.left, fromKey, toKey, subMap);
        }

        if (cmpFrom <= 0 && cmpTo >= 0) {
            subMap.put(node.key, node.value);
        }

        if (cmpTo > 0) {
            addRange(node.right, fromKey, toKey, subMap);
        }
    }

    @Override
    public Iterable<TreeMapInterface.CustomEntry<K, V>> entries() {
        return () -> new TreeMapIterator(root);
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    private int compare(K k1, K k2) {
        if (comparator != null) {
            return comparator.compare(k1, k2);
        } else {
            return ((Comparable<K>) k1).compareTo(k2);
        }
    }

    // Iterator for entries
    private class TreeMapIterator implements java.util.Iterator<TreeMapInterface.CustomEntry<K, V>> {
        private final java.util.Stack<Node<K, V>> stack = new java.util.Stack<>();

        TreeMapIterator(Node<K, V> root) {
            pushLeft(root);
        }

        private void pushLeft(Node<K, V> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public TreeMapInterface.CustomEntry<K, V> next() {
            Node<K, V> node = stack.pop();
            pushLeft(node.right);
            return new TreeMapInterface.CustomEntry<>(node.key, node.value);
        }
    }
}
