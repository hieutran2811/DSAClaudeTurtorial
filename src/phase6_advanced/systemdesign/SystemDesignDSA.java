package phase6_advanced.systemdesign;

import java.util.*;

/**
 * PHASE 6.4 -- SYSTEM DESIGN DSA  (FINAL PHASE)
 *
 * Topics covered:
 *  #104 LRU Cache          -- O(1) get/put via HashMap + Doubly Linked List
 *  #105 LFU Cache          -- O(1) get/put via freq buckets + two HashMaps
 *  #106 Consistent Hashing -- Virtual nodes, ring, minimal redistribution
 *  #107 Skip List          -- Probabilistic O(log n) search/insert/delete
 *  #108 Bloom Filter       -- Space-efficient probabilistic set membership
 */
public class SystemDesignDSA {

    public static void main(String[] args) {
        System.out.println("=== PHASE 6.4: SYSTEM DESIGN DSA ===\n");
        demoLRU();
        demoLFU();
        demoConsistentHashing();
        demoSkipList();
        demoBloomFilter();
        printFinalSummary();
    }

    // =========================================================================
    // TOPIC #104: LRU CACHE
    // =========================================================================
    // LRU = Least Recently Used eviction policy.
    // When cache is full, evict the item that was LEAST RECENTLY USED.
    //
    // TARGET: O(1) get AND O(1) put.
    //
    // DATA STRUCTURES:
    //   HashMap<key, Node>  -- O(1) lookup by key
    //   Doubly Linked List  -- O(1) move-to-front and remove-from-tail
    //
    // DESIGN:
    //   - Most recently used => at HEAD of list
    //   - Least recently used => at TAIL of list
    //   - Use dummy head and tail nodes to avoid null checks
    //
    //   get(key):  found => move node to HEAD, return value
    //              not found => return -1
    //   put(key,val): exists => update val, move to HEAD
    //                 new    => add to HEAD; if over capacity, remove TAIL
    //
    // LeetCode: #146 LRU Cache

    static class LRUCache {
        private final int capacity;
        private final Map<Integer, Node> map;
        private final Node head, tail; // dummy sentinels

        private static class Node {
            int key, val;
            Node prev, next;
            Node(int k, int v) { key = k; val = v; }
        }

        LRUCache(int capacity) {
            this.capacity = capacity;
            map  = new HashMap<>();
            head = new Node(0, 0); // dummy head (most recent)
            tail = new Node(0, 0); // dummy tail (least recent)
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            if (!map.containsKey(key)) return -1;
            Node node = map.get(key);
            moveToHead(node);
            return node.val;
        }

        public void put(int key, int value) {
            if (map.containsKey(key)) {
                Node node = map.get(key);
                node.val = value;
                moveToHead(node);
            } else {
                Node node = new Node(key, value);
                map.put(key, node);
                addToHead(node);
                if (map.size() > capacity) {
                    Node lru = removeTail();
                    map.remove(lru.key);
                }
            }
        }

        private void addToHead(Node node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void moveToHead(Node node) {
            removeNode(node);
            addToHead(node);
        }

        private Node removeTail() {
            Node lru = tail.prev;
            removeNode(lru);
            return lru;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            Node cur = head.next;
            while (cur != tail) {
                sb.append(cur.key).append("=").append(cur.val);
                if (cur.next != tail) sb.append(", ");
                cur = cur.next;
            }
            return sb.append("]").toString();
        }
    }

    static void demoLRU() {
        System.out.println("--- TOPIC #104: LRU Cache ---");
        System.out.println("LeetCode #146 -- capacity=3");
        LRUCache lru = new LRUCache(3);
        lru.put(1, 1); System.out.println("put(1,1) => " + lru);
        lru.put(2, 2); System.out.println("put(2,2) => " + lru);
        lru.put(3, 3); System.out.println("put(3,3) => " + lru);
        System.out.println("get(1)=" + lru.get(1) + " => " + lru); // 1 moves to head
        lru.put(4, 4); // 2 is LRU, evict it
        System.out.println("put(4,4) evict LRU => " + lru);
        System.out.println("get(2)=" + lru.get(2)); // -1, evicted
        System.out.println("get(3)=" + lru.get(3) + " => " + lru);
        System.out.println();
    }

    // =========================================================================
    // TOPIC #105: LFU CACHE
    // =========================================================================
    // LFU = Least Frequently Used eviction policy.
    // Evict the item with the LOWEST USE FREQUENCY.
    // Tie-break: evict the LEAST RECENTLY USED among same-frequency items.
    //
    // TARGET: O(1) get AND O(1) put.
    //
    // DATA STRUCTURES:
    //   keyMap:  key => {value, freq}
    //   freqMap: freq => LinkedHashSet<key>  (insertion-order = LRU order)
    //   minFreq: track current minimum frequency
    //
    // get(key):
    //   1. Lookup in keyMap
    //   2. Increment freq, move key from freqMap[f] to freqMap[f+1]
    //   3. Update minFreq if freqMap[minFreq] is now empty
    //
    // put(key, val):
    //   exists  => update val, call get logic to bump freq
    //   new key => if full, evict last of freqMap[minFreq]
    //              add to keyMap with freq=1, freqMap[1]
    //              reset minFreq = 1
    //
    // LeetCode: #460 LFU Cache

    static class LFUCache {
        private final int capacity;
        private int minFreq;
        private final Map<Integer, int[]> keyMap;   // key -> {val, freq}
        private final Map<Integer, LinkedHashSet<Integer>> freqMap; // freq -> keys

        LFUCache(int capacity) {
            this.capacity = capacity;
            this.minFreq  = 0;
            keyMap  = new HashMap<>();
            freqMap = new HashMap<>();
        }

        public int get(int key) {
            if (!keyMap.containsKey(key)) return -1;
            incrementFreq(key);
            return keyMap.get(key)[0];
        }

        public void put(int key, int value) {
            if (capacity <= 0) return;
            if (keyMap.containsKey(key)) {
                keyMap.get(key)[0] = value;
                incrementFreq(key);
            } else {
                if (keyMap.size() >= capacity) {
                    // Evict LFU (and LRU among ties)
                    int evict = freqMap.get(minFreq).iterator().next();
                    freqMap.get(minFreq).remove(evict);
                    keyMap.remove(evict);
                }
                keyMap.put(key, new int[]{value, 1});
                freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
                minFreq = 1;
            }
        }

        private void incrementFreq(int key) {
            int freq = keyMap.get(key)[1];
            keyMap.get(key)[1]++;
            freqMap.get(freq).remove(key);
            if (freqMap.get(freq).isEmpty() && freq == minFreq) minFreq++;
            freqMap.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            keyMap.forEach((k, v) -> sb.append(k).append("(f=").append(v[1]).append(")=").append(v[0]).append(" "));
            return sb.append("}").toString();
        }
    }

    static void demoLFU() {
        System.out.println("--- TOPIC #105: LFU Cache ---");
        System.out.println("LeetCode #460 -- capacity=2");
        LFUCache lfu = new LFUCache(2);
        lfu.put(1, 1); System.out.println("put(1,1) => " + lfu);
        lfu.put(2, 2); System.out.println("put(2,2) => " + lfu);
        System.out.println("get(1)=" + lfu.get(1) + " => " + lfu); // freq(1)=2, freq(2)=1
        lfu.put(3, 3); // evict key=2 (lowest freq=1)
        System.out.println("put(3,3) evict LFU=2 => " + lfu);
        System.out.println("get(2)=" + lfu.get(2)); // -1
        System.out.println("get(3)=" + lfu.get(3) + " => " + lfu);
        lfu.put(4, 4); // evict key=3 (freq(3)=2 >= freq(1)=2, tie => LRU=3)
        System.out.println("put(4,4) evict LRU-among-LFU => " + lfu);
        System.out.println("get(1)=" + lfu.get(1));
        System.out.println("get(3)=" + lfu.get(3)); // -1
        System.out.println("get(4)=" + lfu.get(4));
        System.out.println();
    }

    // =========================================================================
    // TOPIC #106: CONSISTENT HASHING
    // =========================================================================
    // PROBLEM: Distributing data across N servers such that adding/removing
    //   a server causes minimal data movement (not a full reshuffle).
    //
    // NAIVE APPROACH: key % N => adding/removing server remaps almost ALL keys.
    //
    // CONSISTENT HASHING:
    //   1. Map both SERVERS and KEYS onto a RING (circular hash space 0..2^32-1)
    //   2. A key is assigned to the FIRST SERVER clockwise on the ring.
    //   3. Add server: only keys between new server and its predecessor move.
    //   4. Remove server: only keys on that server move to next server.
    //      => Expected O(K/N) keys move, not O(K).
    //
    // VIRTUAL NODES: Each physical server gets V virtual positions on the ring.
    //   => More uniform distribution even with heterogeneous servers.
    //   => V=100-200 is typical in production.
    //
    // Used in: Amazon DynamoDB, Apache Cassandra, Memcached (ketama hashing)

    static class ConsistentHashing {
        private final TreeMap<Long, String> ring = new TreeMap<>();
        private final int virtualNodes;
        private final Map<String, List<Long>> serverTokens = new HashMap<>();

        ConsistentHashing(int virtualNodes) {
            this.virtualNodes = virtualNodes;
        }

        private long hash(String key) {
            // Simple polynomial hash (use MurmurHash/MD5 in production)
            long h = 0;
            for (char c : key.toCharArray()) h = h * 31 + c;
            return Math.abs(h) % (1L << 32);
        }

        public void addServer(String server) {
            List<Long> tokens = new ArrayList<>();
            for (int i = 0; i < virtualNodes; i++) {
                long pos = hash(server + "#" + i);
                ring.put(pos, server);
                tokens.add(pos);
            }
            serverTokens.put(server, tokens);
        }

        public void removeServer(String server) {
            for (long pos : serverTokens.getOrDefault(server, List.of()))
                ring.remove(pos);
            serverTokens.remove(server);
        }

        public String getServer(String key) {
            if (ring.isEmpty()) return null;
            long h = hash(key);
            // Find first server clockwise (ceiling key on ring, wrap around)
            Map.Entry<Long, String> entry = ring.ceilingEntry(h);
            if (entry == null) entry = ring.firstEntry(); // wrap around
            return entry.getValue();
        }

        public Map<String, Integer> distribution(List<String> keys) {
            Map<String, Integer> dist = new TreeMap<>();
            for (String key : keys)
                dist.merge(getServer(key), 1, Integer::sum);
            return dist;
        }
    }

    static void demoConsistentHashing() {
        System.out.println("--- TOPIC #106: Consistent Hashing ---");
        ConsistentHashing ch = new ConsistentHashing(50); // 50 virtual nodes each

        ch.addServer("Server-A");
        ch.addServer("Server-B");
        ch.addServer("Server-C");

        // Generate test keys
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 100; i++) keys.add("user:" + i);

        System.out.println("Distribution with 3 servers (100 keys, 50 virtual nodes each):");
        System.out.println("  " + ch.distribution(keys));

        // Specific lookups
        System.out.println("\nKey routing:");
        for (String k : new String[]{"user:1", "user:42", "user:99"})
            System.out.println("  " + k + " => " + ch.getServer(k));

        // Add a new server -- measure redistribution
        Map<String, String> before = new HashMap<>();
        for (String k : keys) before.put(k, ch.getServer(k));

        ch.addServer("Server-D");
        int moved = 0;
        for (String k : keys) if (!ch.getServer(k).equals(before.get(k))) moved++;
        System.out.printf("\nAfter adding Server-D: %d/%d keys moved (%.1f%%)%n",
            moved, keys.size(), moved * 100.0 / keys.size());
        System.out.println("Distribution with 4 servers:");
        System.out.println("  " + ch.distribution(keys));
        System.out.println();
    }

    // =========================================================================
    // TOPIC #107: SKIP LIST
    // =========================================================================
    // A Skip List is a PROBABILISTIC data structure that provides O(log n)
    // average time for search, insert, and delete -- same as a balanced BST,
    // but simpler to implement and supports concurrent operations better.
    //
    // STRUCTURE: Multiple layers of sorted linked lists.
    //   Level 0: complete list (all elements)
    //   Level 1: ~half the elements (express lane)
    //   Level 2: ~quarter of elements (faster express)
    //   ...
    //   Each element is promoted to higher levels with probability p=0.5
    //
    // SEARCH: Start at highest level, move right until overshoot, drop down.
    //   Average O(log n), worst case O(n) (unlikely with random promotion).
    //
    // INSERT: Find position at each level, insert with random height.
    // DELETE: Remove node from all levels it appears in.
    //
    // Used in: Redis ZADD (Sorted Sets), LevelDB, Java ConcurrentSkipListMap
    //
    // LeetCode: #1206 Design Skiplist

    static class SkipList {
        private static final int MAX_LEVEL = 16;
        private static final double P = 0.5;
        private final Random rand = new Random();

        private static class Node {
            int val;
            Node[] next;
            @SuppressWarnings("unchecked")
            Node(int val, int level) {
                this.val  = val;
                this.next = new Node[level + 1];
            }
        }

        private final Node head;   // sentinel head with -INF value
        private int level;         // current max level in use

        SkipList() {
            head  = new Node(Integer.MIN_VALUE, MAX_LEVEL);
            level = 0;
        }

        private int randomLevel() {
            int lvl = 0;
            while (lvl < MAX_LEVEL && rand.nextDouble() < P) lvl++;
            return lvl;
        }

        // Search: O(log n) average
        public boolean search(int target) {
            Node cur = head;
            for (int i = level; i >= 0; i--) {
                while (cur.next[i] != null && cur.next[i].val < target)
                    cur = cur.next[i];
            }
            cur = cur.next[0];
            return cur != null && cur.val == target;
        }

        // Insert: O(log n) average
        public void add(int num) {
            Node[] update = new Node[MAX_LEVEL + 1];
            Node cur = head;
            for (int i = level; i >= 0; i--) {
                while (cur.next[i] != null && cur.next[i].val < num)
                    cur = cur.next[i];
                update[i] = cur;
            }
            int newLevel = randomLevel();
            if (newLevel > level) {
                for (int i = level + 1; i <= newLevel; i++) update[i] = head;
                level = newLevel;
            }
            Node node = new Node(num, newLevel);
            for (int i = 0; i <= newLevel; i++) {
                node.next[i]   = update[i].next[i];
                update[i].next[i] = node;
            }
        }

        // Delete: O(log n) average
        public boolean erase(int num) {
            Node[] update = new Node[MAX_LEVEL + 1];
            Node cur = head;
            for (int i = level; i >= 0; i--) {
                while (cur.next[i] != null && cur.next[i].val < num)
                    cur = cur.next[i];
                update[i] = cur;
            }
            Node target = cur.next[0];
            if (target == null || target.val != num) return false;
            for (int i = 0; i <= level; i++) {
                if (update[i].next[i] != target) break;
                update[i].next[i] = target.next[i];
            }
            while (level > 0 && head.next[level] == null) level--;
            return true;
        }

        public List<Integer> toList() {
            List<Integer> res = new ArrayList<>();
            Node cur = head.next[0];
            while (cur != null) { res.add(cur.val); cur = cur.next[0]; }
            return res;
        }

        public int getLevel() { return level; }
    }

    static void demoSkipList() {
        System.out.println("--- TOPIC #107: Skip List ---");
        System.out.println("LeetCode #1206 -- Design Skiplist");
        SkipList sl = new SkipList();

        int[] vals = {3, 6, 7, 9, 12, 17, 19, 21, 25};
        for (int v : vals) sl.add(v);
        System.out.println("After inserts: " + sl.toList());
        System.out.println("Active levels: " + sl.getLevel());

        System.out.println("search(7)  = " + sl.search(7));  // true
        System.out.println("search(15) = " + sl.search(15)); // false
        System.out.println("erase(7)   = " + sl.erase(7));   // true
        System.out.println("erase(15)  = " + sl.erase(15));  // false
        System.out.println("search(7)  = " + sl.search(7));  // false
        System.out.println("After erase(7): " + sl.toList());

        sl.add(7); sl.add(7); // duplicates allowed
        System.out.println("After add(7) twice: " + sl.toList());

        System.out.println("\nUsed in: Redis Sorted Sets (ZADD/ZRANK), Java ConcurrentSkipListMap");
        System.out.println();
    }

    // =========================================================================
    // TOPIC #108: BLOOM FILTER
    // =========================================================================
    // A Bloom Filter answers: "Is element X in the set?" with:
    //   - NO  => 100% certain (no false negatives)
    //   - YES => PROBABLY yes (false positives possible)
    //
    // USE CASES: When you can tolerate false positives but NOT false negatives,
    //   and you need O(1) space-efficient membership check.
    //   - Cache: "Is this URL cached?" (false positive => unnecessary DB lookup)
    //   - Spam filter, weak password check, crawl dedup
    //   - Google BigTable, Apache Cassandra, LevelDB use Bloom Filters
    //
    // STRUCTURE: A bit array of size m, with k independent hash functions.
    //
    // INSERT(x):
    //   Compute h1(x), h2(x), ..., hk(x)
    //   Set bits[hi(x)] = 1 for all i
    //
    // QUERY(x):
    //   If ALL bits[hi(x)] == 1 => PROBABLY in set
    //   If ANY bits[hi(x)] == 0 => DEFINITELY NOT in set
    //
    // FALSE POSITIVE RATE: p = (1 - e^(-kn/m))^k
    //   n = elements inserted, m = bit array size, k = number of hash functions
    //   Optimal k = (m/n) * ln(2)
    //   For p=1%: need m = n * 9.6 bits per element
    //
    // CANNOT DELETE (bits shared among elements). Use Counting Bloom Filter for deletes.

    static class BloomFilter {
        private final long[] bits;
        private final int m;     // bit array size
        private final int k;     // number of hash functions
        private int count;       // elements inserted

        BloomFilter(int expectedElements, double falsePositiveRate) {
            // Optimal size: m = -n * ln(p) / (ln2)^2
            m = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate)
                                / (Math.log(2) * Math.log(2)));
            // Optimal k: k = (m/n) * ln(2)
            k = (int) Math.ceil((double) m / expectedElements * Math.log(2));
            bits = new long[(m + 63) / 64]; // pack into longs
            System.out.printf("BloomFilter: m=%d bits (%.1fKB), k=%d hash functions%n",
                m, m / 8.0 / 1024, k);
        }

        private void setBit(int pos) { bits[pos / 64] |= (1L << (pos % 64)); }

        private boolean getBit(int pos) { return (bits[pos / 64] >> (pos % 64) & 1) == 1; }

        // Simulate k independent hash functions via double hashing:
        // h_i(x) = (h1(x) + i * h2(x)) % m
        private int hash(String key, int seed) {
            int h = 0;
            for (char c : key.toCharArray()) h = h * (31 + seed * 17) + c;
            return Math.abs(h) % m;
        }

        public void add(String key) {
            for (int i = 0; i < k; i++) setBit(hash(key, i));
            count++;
        }

        public boolean mightContain(String key) {
            for (int i = 0; i < k; i++) if (!getBit(hash(key, i))) return false;
            return true; // probably yes
        }

        // Estimated false positive rate given current insertions
        public double estimatedFPR() {
            return Math.pow(1 - Math.exp(-(double) k * count / m), k);
        }

        public int getCount() { return count; }
    }

    static void demoBloomFilter() {
        System.out.println("--- TOPIC #108: Bloom Filter ---");

        // 1000 expected elements, 1% false positive rate target
        BloomFilter bf = new BloomFilter(1000, 0.01);

        // Insert 1000 elements
        for (int i = 0; i < 1000; i++) bf.add("user:" + i);
        System.out.printf("Inserted %d elements, estimated FPR=%.3f%%%n",
            bf.getCount(), bf.estimatedFPR() * 100);

        // Test true positives (must all return true -- no false negatives)
        int falseMiss = 0;
        for (int i = 0; i < 1000; i++) if (!bf.mightContain("user:" + i)) falseMiss++;
        System.out.println("False negatives (must be 0): " + falseMiss);

        // Test false positives on unseen elements
        int falseHits = 0;
        int testSize = 10000;
        for (int i = 1000; i < 1000 + testSize; i++)
            if (bf.mightContain("user:" + i)) falseHits++;
        System.out.printf("False positives: %d/%d = %.2f%% (target ~1%%)%n",
            falseHits, testSize, falseHits * 100.0 / testSize);

        // Show specific lookups
        System.out.println("\nLookup examples:");
        System.out.println("  mightContain(\"user:42\")   = " + bf.mightContain("user:42"));     // true
        System.out.println("  mightContain(\"user:999\")  = " + bf.mightContain("user:999"));    // true
        System.out.println("  mightContain(\"user:9999\") = " + bf.mightContain("user:9999"));   // prob false
        System.out.println("  mightContain(\"unknown\")   = " + bf.mightContain("unknown"));     // prob false

        System.out.println("\nWhere Bloom Filters are used:");
        System.out.println("  Google BigTable  -- avoid disk lookups for non-existent rows");
        System.out.println("  Apache Cassandra -- reduce SSTable reads");
        System.out.println("  Web crawlers     -- dedup visited URLs");
        System.out.println("  Chrome browser   -- malicious URL check (before full DB query)");
        System.out.println();
    }

    // =========================================================================
    // FINAL SUMMARY
    // =========================================================================

    static void printFinalSummary() {
        System.out.println("=".repeat(60));
        System.out.println("  ALL 6 PHASES COMPLETE -- DSA ROADMAP FINISHED!");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("PHASE 1  Foundations       -- Arrays, LinkedList, Stack, Queue");
        System.out.println("PHASE 2  Core Data Struct  -- HashMap, Trees, Heaps, Trie");
        System.out.println("PHASE 3  Algorithms        -- Sorting, BinSearch, Backtracking, D&C");
        System.out.println("PHASE 4  Graph Algorithms  -- BFS/DFS, Shortest Path, MST, SCC");
        System.out.println("PHASE 5  Dynamic Prog.     -- Memo/Tab, Knapsack, Bitmask, Tree DP");
        System.out.println("PHASE 6  Advanced Topics   -- AVL/SegTree/BIT, KMP/SA, Math/Bits, System Design");
        System.out.println();
        System.out.println("System Design DSA Cheat Sheet:");
        System.out.println("  LRU Cache         O(1) get/put   HashMap + DLL");
        System.out.println("  LFU Cache         O(1) get/put   2x HashMap + LinkedHashSet freq buckets");
        System.out.println("  Consistent Hash   O(log n) route  TreeMap ring + virtual nodes");
        System.out.println("  Skip List         O(log n) all    Probabilistic multi-level list");
        System.out.println("  Bloom Filter      O(k) add/query  Bit array + k hash functions, no FN");
        System.out.println();
        System.out.println("108 / 108 topics completed. Ready for interviews!");
    }
}
