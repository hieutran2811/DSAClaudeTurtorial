package phase6_advanced.trees;

import java.util.*;

/**
 * PHASE 6.1 — ADVANCED TREES
 *
 * Topics covered:
 *  #90  AVL Tree           — Self-balancing BST, rotations, O(log n) all ops
 *  #91  Red-Black Tree     — Concepts, properties, why Java TreeMap uses it
 *  #92  Segment Tree       — Range queries (sum/min/max), point updates O(log n)
 *  #93  Fenwick Tree / BIT — Prefix sum updates & queries O(log n), compact code
 *  #94  Sparse Table       — Static RMQ O(1) query, O(n log n) build
 */
public class AdvancedTrees {

    public static void main(String[] args) {
        System.out.println("=== PHASE 6.1: ADVANCED TREES ===\n");
        demoAVL();
        demoRedBlackConcepts();
        demoSegmentTree();
        demoFenwickTree();
        demoSparseTable();
    }

    // =========================================================================
    // TOPIC #90: AVL TREE
    // =========================================================================
    // AVL = Adelson-Velsky and Landis tree (1962)
    // KEY IDEA: BST where |height(left) - height(right)| <= 1 for EVERY node
    // => guarantees O(log n) for insert, delete, search
    //
    // Balance Factor (BF) = height(left) - height(right)
    //   BF in {-1, 0, 1} => balanced
    //   BF = 2 => left-heavy => need right rotation (or left-right)
    //   BF = -2 => right-heavy => need left rotation (or right-left)
    //
    // 4 ROTATION CASES:
    //   1. Left-Left   (BF=2, left child BF>=0)  => Right Rotate
    //   2. Right-Right (BF=-2, right child BF<=0) => Left Rotate
    //   3. Left-Right  (BF=2, left child BF<0)   => Left Rotate left child, then Right Rotate
    //   4. Right-Left  (BF=-2, right child BF>0) => Right Rotate right child, then Left Rotate

    static class AVLTree {
        private class Node {
            int key, height;
            Node left, right;
            Node(int key) { this.key = key; this.height = 1; }
        }

        private Node root;

        private int height(Node n) { return n == null ? 0 : n.height; }

        private int balanceFactor(Node n) {
            return n == null ? 0 : height(n.left) - height(n.right);
        }

        private void updateHeight(Node n) {
            n.height = 1 + Math.max(height(n.left), height(n.right));
        }

        // Right Rotation (fixes Left-Left case)
        //       y                x
        //      / \              / \
        //     x   T3   =>    T1   y
        //    / \                  / \
        //  T1   T2              T2   T3
        private Node rotateRight(Node y) {
            Node x = y.left;
            Node T2 = x.right;
            x.right = y;
            y.left = T2;
            updateHeight(y);
            updateHeight(x);
            return x; // new root
        }

        // Left Rotation (fixes Right-Right case)
        //     x                  y
        //    / \                / \
        //  T1   y     =>      x   T3
        //      / \           / \
        //    T2   T3       T1   T2
        private Node rotateLeft(Node x) {
            Node y = x.right;
            Node T2 = y.left;
            y.left = x;
            x.right = T2;
            updateHeight(x);
            updateHeight(y);
            return y; // new root
        }

        private Node balance(Node n) {
            updateHeight(n);
            int bf = balanceFactor(n);

            // Left-Left: right rotate
            if (bf == 2 && balanceFactor(n.left) >= 0)
                return rotateRight(n);

            // Left-Right: left rotate left child, then right rotate
            if (bf == 2 && balanceFactor(n.left) < 0) {
                n.left = rotateLeft(n.left);
                return rotateRight(n);
            }

            // Right-Right: left rotate
            if (bf == -2 && balanceFactor(n.right) <= 0)
                return rotateLeft(n);

            // Right-Left: right rotate right child, then left rotate
            if (bf == -2 && balanceFactor(n.right) > 0) {
                n.right = rotateRight(n.right);
                return rotateLeft(n);
            }

            return n; // already balanced
        }

        public void insert(int key) { root = insert(root, key); }

        private Node insert(Node n, int key) {
            if (n == null) return new Node(key);
            if (key < n.key)      n.left  = insert(n.left,  key);
            else if (key > n.key) n.right = insert(n.right, key);
            else return n; // duplicate, ignore
            return balance(n);
        }

        public void delete(int key) { root = delete(root, key); }

        private Node delete(Node n, int key) {
            if (n == null) return null;
            if (key < n.key)      n.left  = delete(n.left,  key);
            else if (key > n.key) n.right = delete(n.right, key);
            else {
                // Found! Handle 3 cases:
                if (n.left == null)  return n.right; // 0 or 1 child
                if (n.right == null) return n.left;
                // 2 children: replace with in-order successor (min of right subtree)
                Node successor = findMin(n.right);
                n.key = successor.key;
                n.right = delete(n.right, successor.key);
            }
            return balance(n);
        }

        private Node findMin(Node n) {
            while (n.left != null) n = n.left;
            return n;
        }

        public boolean search(int key) { return search(root, key); }

        private boolean search(Node n, int key) {
            if (n == null) return false;
            if (key == n.key) return true;
            return key < n.key ? search(n.left, key) : search(n.right, key);
        }

        public int getHeight() { return height(root); }

        public void inorder() {
            inorder(root);
            System.out.println();
        }

        private void inorder(Node n) {
            if (n == null) return;
            inorder(n.left);
            System.out.print(n.key + " ");
            inorder(n.right);
        }

        // Visualize balance factors (useful for debugging)
        public void printBF() {
            printBF(root, "", false);
        }

        private void printBF(Node n, String prefix, boolean isLeft) {
            if (n == null) return;
            System.out.println(prefix + (isLeft ? "├── " : "└── ")
                    + n.key + " [BF=" + balanceFactor(n) + "]");
            printBF(n.left,  prefix + (isLeft ? "│   " : "    "), true);
            printBF(n.right, prefix + (isLeft ? "│   " : "    "), false);
        }
    }

    static void demoAVL() {
        System.out.println("--- TOPIC #90: AVL Tree ---");
        AVLTree avl = new AVLTree();

        // Insert sequence that would degenerate a normal BST to a linked list
        int[] keys = {10, 20, 30, 40, 50, 25};
        for (int k : keys) avl.insert(k);

        System.out.print("Inorder (sorted): ");
        avl.inorder();
        System.out.println("Height: " + avl.getHeight() + " (should be ~3, not 5)");

        avl.delete(40);
        System.out.print("After deleting 40: ");
        avl.inorder();

        System.out.println("Search 25: " + avl.search(25));
        System.out.println("Search 40: " + avl.search(40));
        System.out.println();
    }

    // =========================================================================
    // TOPIC #91: RED-BLACK TREE (Concepts)
    // =========================================================================
    // A Red-Black Tree is a self-balancing BST with 5 properties:
    //
    //  1. Every node is RED or BLACK
    //  2. Root is BLACK
    //  3. All NULL leaves (NIL) are BLACK
    //  4. If a node is RED, both children are BLACK (no 2 consecutive reds)
    //  5. All paths from any node to its descendant NIL nodes
    //     have the SAME number of BLACK nodes (black-height)
    //
    // WHY Red-Black over AVL?
    //   - AVL: stricter balance (height diff <= 1) => better READ performance
    //   - RB:  looser balance => fewer rotations on INSERT/DELETE => better WRITE
    //   - Java TreeMap, TreeSet, C++ std::map => all use Red-Black Tree
    //
    // Height guarantee: <= 2 * log2(n+1)  => still O(log n)
    //
    // Insertions fix violations with:
    //   - Recoloring (parent, uncle, grandparent)
    //   - Rotations (Left, Right)
    //
    // This is a CONCEPT demo — full RB implementation is complex (600+ lines).
    // In interviews, explain the properties and compare to AVL.

    static void demoRedBlackConcepts() {
        System.out.println("--- TOPIC #91: Red-Black Tree Concepts ---");
        System.out.println("Properties:");
        System.out.println("  1. Each node is RED or BLACK");
        System.out.println("  2. Root is always BLACK");
        System.out.println("  3. NULL leaves (NIL) are BLACK");
        System.out.println("  4. RED node => both children are BLACK");
        System.out.println("  5. Equal black-height on all root-to-NIL paths");
        System.out.println();
        System.out.println("Java uses RB Tree in: TreeMap, TreeSet, LinkedHashMap buckets");
        System.out.println("C++ uses RB Tree in:  std::map, std::set, std::multimap");
        System.out.println();
        System.out.println("AVL vs Red-Black:");
        System.out.println("  AVL  => stricter balance, faster reads, more rotations on write");
        System.out.println("  RB   => looser balance, faster writes, preferred for general use");

        // Demo: Java's built-in TreeMap is backed by a Red-Black Tree
        TreeMap<Integer, String> rbTree = new TreeMap<>();
        rbTree.put(5, "five"); rbTree.put(3, "three"); rbTree.put(7, "seven");
        rbTree.put(1, "one");  rbTree.put(9, "nine");
        System.out.println("\nJava TreeMap (RB-backed) in order: " + rbTree.keySet());
        System.out.println("First: " + rbTree.firstKey() + ", Last: " + rbTree.lastKey());
        System.out.println("Floor(6): " + rbTree.floorKey(6) + ", Ceiling(6): " + rbTree.ceilingKey(6));
        System.out.println();
    }

    // =========================================================================
    // TOPIC #92: SEGMENT TREE
    // =========================================================================
    // PROBLEM: Given array, support:
    //   - Range Query: sum/min/max of arr[l..r] in O(log n)
    //   - Point Update: arr[i] = val in O(log n)
    //
    // IDEA: Binary tree where each node stores aggregate (sum/min/max) of a range
    //   - Leaf nodes = individual elements
    //   - Internal node = aggregate of left + right children
    //   - Store in array: node i => children 2i and 2i+1 (1-indexed)
    //
    // Size: 4 * n (safe upper bound for the segment tree array)
    //
    // BUILD:  O(n)
    // QUERY:  O(log n)
    // UPDATE: O(log n)
    //
    // LeetCode:
    //   #307 Range Sum Query - Mutable
    //   #315 Count of Smaller Numbers After Self
    //   #493 Reverse Pairs

    static class SegmentTree {
        private final int[] tree;
        private final int n;

        SegmentTree(int[] arr) {
            n = arr.length;
            tree = new int[4 * n];
            build(arr, 1, 0, n - 1);
        }

        // Build tree bottom-up
        private void build(int[] arr, int node, int start, int end) {
            if (start == end) {
                tree[node] = arr[start]; // leaf
            } else {
                int mid = (start + end) / 2;
                build(arr, 2 * node, start, mid);
                build(arr, 2 * node + 1, mid + 1, end);
                tree[node] = tree[2 * node] + tree[2 * node + 1]; // merge
            }
        }

        // Range sum query [l, r]
        public int query(int l, int r) { return query(1, 0, n - 1, l, r); }

        private int query(int node, int start, int end, int l, int r) {
            if (r < start || end < l) return 0; // out of range
            if (l <= start && end <= r) return tree[node]; // fully inside
            int mid = (start + end) / 2;
            int leftSum  = query(2 * node, start, mid, l, r);
            int rightSum = query(2 * node + 1, mid + 1, end, l, r);
            return leftSum + rightSum;
        }

        // Point update: arr[idx] = val
        public void update(int idx, int val) { update(1, 0, n - 1, idx, val); }

        private void update(int node, int start, int end, int idx, int val) {
            if (start == end) {
                tree[node] = val; // update leaf
            } else {
                int mid = (start + end) / 2;
                if (idx <= mid) update(2 * node, start, mid, idx, val);
                else            update(2 * node + 1, mid + 1, end, idx, val);
                tree[node] = tree[2 * node] + tree[2 * node + 1]; // re-merge
            }
        }
    }

    // LeetCode #307 — Range Sum Query Mutable
    static class NumArray {
        private final SegmentTree st;
        NumArray(int[] nums) { st = new SegmentTree(nums); }
        public void update(int index, int val) { st.update(index, val); }
        public int sumRange(int left, int right) { return st.query(left, right); }
    }

    // Segment Tree for Range Minimum Query (RMQ)
    static class RMQSegmentTree {
        private final int[] tree;
        private final int n;

        RMQSegmentTree(int[] arr) {
            n = arr.length;
            tree = new int[4 * n];
            build(arr, 1, 0, n - 1);
        }

        private void build(int[] arr, int node, int start, int end) {
            if (start == end) {
                tree[node] = arr[start];
            } else {
                int mid = (start + end) / 2;
                build(arr, 2 * node, start, mid);
                build(arr, 2 * node + 1, mid + 1, end);
                tree[node] = Math.min(tree[2 * node], tree[2 * node + 1]);
            }
        }

        public int queryMin(int l, int r) { return queryMin(1, 0, n - 1, l, r); }

        private int queryMin(int node, int start, int end, int l, int r) {
            if (r < start || end < l) return Integer.MAX_VALUE;
            if (l <= start && end <= r) return tree[node];
            int mid = (start + end) / 2;
            return Math.min(
                queryMin(2 * node, start, mid, l, r),
                queryMin(2 * node + 1, mid + 1, end, l, r)
            );
        }
    }

    static void demoSegmentTree() {
        System.out.println("--- TOPIC #92: Segment Tree ---");
        int[] arr = {1, 3, 5, 7, 9, 11};
        System.out.println("Array: " + Arrays.toString(arr));

        SegmentTree st = new SegmentTree(arr);
        System.out.println("Sum [1,3]   = " + st.query(1, 3)); // 3+5+7 = 15
        System.out.println("Sum [0,5]   = " + st.query(0, 5)); // 36
        System.out.println("Sum [2,4]   = " + st.query(2, 4)); // 5+7+9 = 21

        st.update(1, 10); // arr[1] = 10
        System.out.println("After update arr[1]=10:");
        System.out.println("Sum [1,3]   = " + st.query(1, 3)); // 10+5+7 = 22

        // LeetCode #307
        NumArray na = new NumArray(new int[]{1, 3, 5});
        System.out.println("\nLeetCode #307:");
        System.out.println("sumRange(0,2) = " + na.sumRange(0, 2)); // 9
        na.update(1, 2);
        System.out.println("After update(1,2): sumRange(0,2) = " + na.sumRange(0, 2)); // 8

        // RMQ
        RMQSegmentTree rmq = new RMQSegmentTree(arr);
        System.out.println("\nRMQ min[1,4] = " + rmq.queryMin(1, 4)); // min(3,5,7,9)=3
        System.out.println();
    }

    // =========================================================================
    // TOPIC #93: FENWICK TREE / BINARY INDEXED TREE (BIT)
    // =========================================================================
    // SAME GOAL as Segment Tree for prefix sums, but SIMPLER code, less memory
    //
    // KEY TRICK: each index i stores sum of a specific range determined by
    //   the lowest set bit of i: i & (-i)  [also written as i & ~(i-1)]
    //
    //   i = 6 = 110  => lowest set bit = 010 = 2
    //   bit[6] stores sum of arr[5] + arr[6] (last 2 elements, 1-indexed)
    //
    // UPDATE index i (1-indexed): propagate up by adding lowest set bit
    //   i, i + (i & -i), i + 2*(i & -i), ...
    //
    // PREFIX SUM [1..i]: accumulate down by removing lowest set bit
    //   i, i - (i & -i), i - 2*(i & -i), ...
    //
    // Range query [l, r] = prefixSum(r) - prefixSum(l-1)
    //
    // BUILD:  O(n log n) or O(n) with special trick
    // QUERY:  O(log n)
    // UPDATE: O(log n)
    // SPACE:  O(n)  (simpler than segment tree's 4n)
    //
    // LeetCode:
    //   #307 Range Sum Query - Mutable (can use BIT)
    //   #315 Count of Smaller Numbers After Self
    //   #327 Count of Range Sum

    static class FenwickTree {
        private final int[] bit;
        private final int n;

        FenwickTree(int n) {
            this.n = n;
            this.bit = new int[n + 1]; // 1-indexed
        }

        FenwickTree(int[] arr) {
            this(arr.length);
            for (int i = 0; i < arr.length; i++)
                update(i + 1, arr[i]); // build via n updates
        }

        // Add delta to position i (1-indexed)
        public void update(int i, int delta) {
            for (; i <= n; i += i & (-i))
                bit[i] += delta;
        }

        // Prefix sum [1..i] (1-indexed)
        public int prefixSum(int i) {
            int sum = 0;
            for (; i > 0; i -= i & (-i))
                sum += bit[i];
            return sum;
        }

        // Range sum [l..r] (1-indexed)
        public int rangeSum(int l, int r) {
            return prefixSum(r) - prefixSum(l - 1);
        }

        // Point query (get value at position i)
        // Works only if we track individual elements OR update with exact values
        public int pointQuery(int i) {
            return rangeSum(i, i);
        }
    }

    // 2D Fenwick Tree — for 2D prefix sums (e.g., matrix range sum queries)
    static class FenwickTree2D {
        private final int[][] bit;
        private final int rows, cols;

        FenwickTree2D(int rows, int cols) {
            this.rows = rows; this.cols = cols;
            bit = new int[rows + 1][cols + 1];
        }

        public void update(int r, int c, int delta) {
            for (int i = r; i <= rows; i += i & (-i))
                for (int j = c; j <= cols; j += j & (-j))
                    bit[i][j] += delta;
        }

        public int prefixSum(int r, int c) {
            int sum = 0;
            for (int i = r; i > 0; i -= i & (-i))
                for (int j = c; j > 0; j -= j & (-j))
                    sum += bit[i][j];
            return sum;
        }

        // Sum of rectangle [r1,c1] to [r2,c2] (1-indexed)
        public int rangeSum(int r1, int c1, int r2, int c2) {
            return prefixSum(r2, c2)
                 - prefixSum(r1 - 1, c2)
                 - prefixSum(r2, c1 - 1)
                 + prefixSum(r1 - 1, c1 - 1);
        }
    }

    static void demoFenwickTree() {
        System.out.println("--- TOPIC #93: Fenwick Tree / BIT ---");
        int[] arr = {1, 3, 5, 7, 9, 11};
        System.out.println("Array: " + Arrays.toString(arr));

        FenwickTree bit = new FenwickTree(arr);
        System.out.println("Prefix sum [1..3] = " + bit.prefixSum(3));   // 1+3+5=9
        System.out.println("Range sum  [2..5] = " + bit.rangeSum(2, 5)); // 3+5+7+9=24
        System.out.println("Range sum  [1..6] = " + bit.rangeSum(1, 6)); // 36

        // Update arr[2] (0-indexed) => position 3 (1-indexed): 5 -> 10, delta = +5
        bit.update(3, 5);
        System.out.println("After arr[2] += 5 (5->10):");
        System.out.println("Range sum  [2..5] = " + bit.rangeSum(2, 5)); // 3+10+7+9=29

        System.out.println("\nBIT i & (-i) trick:");
        for (int i = 1; i <= 8; i++) {
            System.out.printf("  i=%d (%4s), i&(-i)=%d => stores %d element(s)%n",
                i, Integer.toBinaryString(i), i & (-i), i & (-i));
        }
        System.out.println();
    }

    // =========================================================================
    // TOPIC #94: SPARSE TABLE
    // =========================================================================
    // PURPOSE: Answer STATIC Range Minimum (or Maximum) Queries in O(1)
    //   "Static" means the array DOES NOT change after preprocessing.
    //
    // IDEA: Precompute answers for all ranges of length 2^k
    //   st[i][k] = min/max of arr[i .. i + 2^k - 1]
    //
    // KEY INSIGHT for O(1) query:
    //   For a query [l, r] of length len = r - l + 1:
    //   Find k = floor(log2(len))
    //   Answer = min(st[l][k], st[r - 2^k + 1][k])
    //   (Two overlapping ranges of length 2^k cover [l, r] exactly for min/max)
    //   This works because min is IDEMPOTENT (min(x, x) = x)
    //   *** NOT usable for sum queries (sum not idempotent) ***
    //
    // BUILD:  O(n log n)
    // QUERY:  O(1)  <-- the killer feature
    // SPACE:  O(n log n)
    //
    // When to use:
    //   - Many queries, no updates => Sparse Table
    //   - Updates needed           => Segment Tree or Fenwick Tree

    static class SparseTable {
        private final int[][] table; // table[i][k] = min of [i, i+2^k-1]
        private final int[] log2;    // precomputed floor(log2(i))
        private final int n;

        SparseTable(int[] arr) {
            n = arr.length;
            int maxLog = 1;
            while ((1 << maxLog) <= n) maxLog++;

            table = new int[n][maxLog];
            log2 = new int[n + 1];

            // Precompute log2 values
            log2[1] = 0;
            for (int i = 2; i <= n; i++)
                log2[i] = log2[i / 2] + 1;

            // Base case: ranges of length 1
            for (int i = 0; i < n; i++) table[i][0] = arr[i];

            // Fill table[i][k] = min(table[i][k-1], table[i + 2^(k-1)][k-1])
            for (int k = 1; k < maxLog; k++)
                for (int i = 0; i + (1 << k) - 1 < n; i++)
                    table[i][k] = Math.min(table[i][k - 1],
                                           table[i + (1 << (k - 1))][k - 1]);
        }

        // Range Minimum Query [l, r] in O(1)
        public int queryMin(int l, int r) {
            int k = log2[r - l + 1];
            return Math.min(table[l][k], table[r - (1 << k) + 1][k]);
        }
    }

    // LeetCode-style: given queries, return min for each range
    static int[] answerQueries(int[] arr, int[][] queries) {
        SparseTable st = new SparseTable(arr);
        int[] result = new int[queries.length];
        for (int i = 0; i < queries.length; i++)
            result[i] = st.queryMin(queries[i][0], queries[i][1]);
        return result;
    }

    static void demoSparseTable() {
        System.out.println("--- TOPIC #94: Sparse Table (Static RMQ) ---");
        int[] arr = {7, 2, 3, 0, 5, 10, 3, 12, 18};
        System.out.println("Array: " + Arrays.toString(arr));

        SparseTable st = new SparseTable(arr);
        System.out.println("Min [0, 4] = " + st.queryMin(0, 4)); // min(7,2,3,0,5) = 0
        System.out.println("Min [3, 7] = " + st.queryMin(3, 7)); // min(0,5,10,3,12) = 0
        System.out.println("Min [5, 8] = " + st.queryMin(5, 8)); // min(10,3,12,18) = 3
        System.out.println("Min [1, 1] = " + st.queryMin(1, 1)); // 2
        System.out.println("Min [0, 8] = " + st.queryMin(0, 8)); // 0

        // Multiple queries answered in O(1) each
        int[][] queries = {{0, 8}, {2, 6}, {4, 7}};
        System.out.println("\nBatch queries: " + Arrays.toString(answerQueries(arr, queries)));

        System.out.println();
        System.out.println("SUMMARY — When to use which:");
        System.out.println("  Sparse Table  : static data, O(1) RMQ, O(n log n) build");
        System.out.println("  Fenwick Tree  : prefix sums + point updates, simple code");
        System.out.println("  Segment Tree  : range queries + range updates, most flexible");
        System.out.println("  AVL / RB Tree : sorted dynamic set, O(log n) all ops");
    }
}
