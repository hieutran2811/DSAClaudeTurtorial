package phase2_core.hashtable;

/**
 * PHASE 2.1 — HashMap Internals & Custom Implementation
 *
 * Cách HashMap hoạt động bên trong:
 *   1. key → hashCode() → compress → bucket index
 *   2. Collision → chaining (LinkedList) hoặc open addressing
 *   3. Load factor > 0.75 → resize (gấp đôi) + rehash
 *
 * Java HashMap:
 *   - Bucket: array of Node (LinkedList)
 *   - Từ Java 8: bucket > 8 node → chuyển sang TreeNode (Red-Black Tree) → O(log n)
 *   - Default capacity: 16, load factor: 0.75
 *   - Thread-safe: dùng ConcurrentHashMap
 */
public class HashMapInternals {

    // =========================================================
    // Custom HashMap — Chaining (LinkedList per bucket)
    // =========================================================
    static class MyHashMap<K, V> {
        private static final int DEFAULT_CAPACITY = 16;
        private static final double LOAD_FACTOR   = 0.75;

        private Node<K, V>[] buckets;
        private int size;

        @SuppressWarnings("unchecked")
        MyHashMap() {
            buckets = new Node[DEFAULT_CAPACITY];
        }

        // ---- Hash function ----
        private int hash(K key) {
            if (key == null) return 0;
            int h = key.hashCode();
            // Spread bits — Java HashMap làm tương tự để giảm collision
            h = h ^ (h >>> 16);
            return Math.abs(h) % buckets.length;
        }

        // ---- PUT ----
        void put(K key, V value) {
            int idx = hash(key);
            Node<K, V> curr = buckets[idx];

            // Tìm key đã tồn tại chưa
            while (curr != null) {
                if (equals(curr.key, key)) { curr.value = value; return; }
                curr = curr.next;
            }

            // Thêm node mới vào đầu bucket (O(1))
            Node<K, V> node = new Node<>(key, value);
            node.next = buckets[idx];
            buckets[idx] = node;
            size++;

            // Resize nếu load factor vượt ngưỡng
            if ((double) size / buckets.length > LOAD_FACTOR) resize();
        }

        // ---- GET ----
        V get(K key) {
            int idx = hash(key);
            Node<K, V> curr = buckets[idx];
            while (curr != null) {
                if (equals(curr.key, key)) return curr.value;
                curr = curr.next;
            }
            return null;
        }

        // ---- REMOVE ----
        boolean remove(K key) {
            int idx = hash(key);
            Node<K, V> curr = buckets[idx], prev = null;
            while (curr != null) {
                if (equals(curr.key, key)) {
                    if (prev == null) buckets[idx] = curr.next;
                    else              prev.next = curr.next;
                    size--;
                    return true;
                }
                prev = curr;
                curr = curr.next;
            }
            return false;
        }

        boolean containsKey(K key) { return get(key) != null; }
        int size() { return size; }

        // ---- RESIZE — rehash toàn bộ ----
        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] old = buckets;
            buckets = new Node[old.length * 2]; // gấp đôi capacity
            size = 0;
            for (Node<K, V> head : old) {
                Node<K, V> curr = head;
                while (curr != null) {
                    put(curr.key, curr.value); // rehash vào bucket mới
                    curr = curr.next;
                }
            }
            System.out.println("  [Resized to capacity: " + buckets.length + "]");
        }

        private boolean equals(K a, K b) {
            if (a == null) return b == null;
            return a.equals(b);
        }

        void printBuckets() {
            System.out.println("Buckets (capacity=" + buckets.length + ", size=" + size + "):");
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    System.out.print("  [" + i + "] → ");
                    Node<K, V> curr = buckets[i];
                    while (curr != null) {
                        System.out.print("(" + curr.key + "=" + curr.value + ")");
                        if (curr.next != null) System.out.print(" → ");
                        curr = curr.next;
                    }
                    System.out.println();
                }
            }
        }

        static class Node<K, V> {
            K key; V value; Node<K, V> next;
            Node(K k, V v) { key = k; value = v; }
        }
    }

    // =========================================================
    // Open Addressing — Linear Probing (alternative collision strategy)
    // =========================================================
    /**
     * Thay vì chain, tìm bucket trống tiếp theo khi collision
     * Ưu: cache-friendly (liên tục trong bộ nhớ)
     * Nhược: clustering — nhiều phần tử dồn về 1 vùng
     *
     * Java không dùng cái này (dùng chaining), nhưng cần biết khái niệm
     */
    static class OpenAddressMap {
        private int[] keys, values;
        private boolean[] deleted;
        private int capacity, size;
        private static final int EMPTY = Integer.MIN_VALUE;

        OpenAddressMap(int capacity) {
            this.capacity = capacity;
            keys     = new int[capacity];
            values   = new int[capacity];
            deleted  = new boolean[capacity];
            java.util.Arrays.fill(keys, EMPTY);
        }

        private int hash(int key) { return Math.abs(key) % capacity; }

        void put(int key, int value) {
            int idx = hash(key);
            while (keys[idx] != EMPTY && keys[idx] != key && !deleted[idx]) {
                idx = (idx + 1) % capacity; // linear probe
            }
            if (keys[idx] == EMPTY || deleted[idx]) size++;
            keys[idx]    = key;
            values[idx]  = value;
            deleted[idx] = false;
        }

        int get(int key) {
            int idx = hash(key);
            while (keys[idx] != EMPTY) {
                if (keys[idx] == key && !deleted[idx]) return values[idx];
                idx = (idx + 1) % capacity;
            }
            return -1;
        }

        void remove(int key) {
            int idx = hash(key);
            while (keys[idx] != EMPTY) {
                if (keys[idx] == key && !deleted[idx]) { deleted[idx] = true; size--; return; }
                idx = (idx + 1) % capacity;
            }
        }
    }

    // =========================================================
    // Design HashMap (LeetCode #706) — không dùng built-in
    // =========================================================
    /**
     * Phiên bản đơn giản hóa — bucket cố định 1009 (số nguyên tố)
     * Dùng số nguyên tố làm capacity → phân tán đều hơn
     */
    static class LeetCodeHashMap {
        private static final int SIZE = 1009;
        private java.util.LinkedList<int[]>[] map;

        @SuppressWarnings("unchecked")
        LeetCodeHashMap() { map = new java.util.LinkedList[SIZE]; }

        private int hash(int key) { return key % SIZE; }

        void put(int key, int value) {
            int idx = hash(key);
            if (map[idx] == null) map[idx] = new java.util.LinkedList<>();
            for (int[] pair : map[idx]) {
                if (pair[0] == key) { pair[1] = value; return; }
            }
            map[idx].add(new int[]{key, value});
        }

        int get(int key) {
            int idx = hash(key);
            if (map[idx] == null) return -1;
            for (int[] pair : map[idx]) if (pair[0] == key) return pair[1];
            return -1;
        }

        void remove(int key) {
            int idx = hash(key);
            if (map[idx] == null) return;
            map[idx].removeIf(pair -> pair[0] == key);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== HashMap Internals ===\n");

        // Custom HashMap với Chaining
        System.out.println("--- Custom HashMap (Chaining) ---");
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("alice", 90);
        map.put("bob", 85);
        map.put("charlie", 92);
        map.put("david", 78);
        map.printBuckets();

        System.out.println("get(alice)   = " + map.get("alice"));   // 90
        System.out.println("get(bob)     = " + map.get("bob"));     // 85
        System.out.println("get(unknown) = " + map.get("unknown")); // null

        map.put("alice", 95); // update
        System.out.println("after update alice = " + map.get("alice")); // 95

        map.remove("bob");
        System.out.println("after remove bob, contains: " + map.containsKey("bob")); // false

        // Test resize
        System.out.println("\n--- Resize Test ---");
        MyHashMap<Integer, Integer> growing = new MyHashMap<>();
        for (int i = 0; i < 15; i++) growing.put(i, i * 10); // resize tại 12+
        System.out.println("size after 15 puts: " + growing.size());

        // Open Addressing
        System.out.println("\n--- Open Addressing (Linear Probing) ---");
        OpenAddressMap oam = new OpenAddressMap(7);
        oam.put(10, 100); oam.put(17, 170); // 10%7=3, 17%7=3 → collision!
        oam.put(24, 240); // 24%7=3 → collision lần 2
        System.out.println("get(10) = " + oam.get(10)); // 100
        System.out.println("get(17) = " + oam.get(17)); // 170
        System.out.println("get(24) = " + oam.get(24)); // 240

        // LeetCode #706
        System.out.println("\n--- LeetCode HashMap (#706) ---");
        LeetCodeHashMap lc = new LeetCodeHashMap();
        lc.put(1, 1); lc.put(2, 2);
        System.out.println("get(1)="+lc.get(1)+" get(3)="+lc.get(3)); // 1, -1
        lc.put(2, 1);
        System.out.println("get(2) after update = " + lc.get(2)); // 1
        lc.remove(2);
        System.out.println("get(2) after remove = " + lc.get(2)); // -1

        // Java HashMap API recap
        System.out.println("\n--- Java HashMap API Patterns ---");
        java.util.Map<String, Integer> jMap = new java.util.HashMap<>();
        jMap.put("a", 1);
        System.out.println("getOrDefault: " + jMap.getOrDefault("b", 0)); // 0
        jMap.merge("a", 1, Integer::sum);   // a = 1+1 = 2
        jMap.putIfAbsent("a", 99);          // không thay đổi vì a đã tồn tại
        jMap.computeIfAbsent("c", k -> k.length()); // c = 1
        System.out.println("map: " + jMap); // {a=2, c=1}
    }
}
