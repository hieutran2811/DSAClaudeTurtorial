package phase2_core.heaps;

import java.util.*;

/**
 * PHASE 2.3 — HEAPS
 * ==================
 * Topics covered:
 *  29. Min-heap / Max-heap
 *  30. Heap operations: insert O(log n), extractMin/Max O(log n), peek O(1)
 *  31. Build heap O(n) — heapify from array
 *  32. K largest / K smallest elements
 *  33. Merge K sorted lists
 *
 * KEY CONCEPTS:
 *  - Heap = complete binary tree stored in array
 *  - Parent of i  : (i-1)/2
 *  - Left child   : 2*i + 1
 *  - Right child  : 2*i + 2
 *  - Min-heap: parent <= children  → root = minimum
 *  - Max-heap: parent >= children  → root = maximum
 */
public class Heap {

    // =========================================================
    // TOPIC 29 & 30 — MIN-HEAP implementation from scratch
    // =========================================================
    static class MinHeap {
        private int[] data;
        private int size;

        MinHeap(int capacity) {
            data = new int[capacity];
            size = 0;
        }

        // ---- helpers ----
        private int parent(int i) { return (i - 1) / 2; }
        private int left(int i)   { return 2 * i + 1; }
        private int right(int i)  { return 2 * i + 2; }

        private void swap(int i, int j) {
            int tmp = data[i]; data[i] = data[j]; data[j] = tmp;
        }

        // Bubble UP after insert — O(log n)
        private void siftUp(int i) {
            while (i > 0 && data[parent(i)] > data[i]) {
                swap(i, parent(i));
                i = parent(i);
            }
        }

        // Bubble DOWN after extract — O(log n)
        private void siftDown(int i) {
            while (true) {
                int smallest = i;
                int l = left(i), r = right(i);
                if (l < size && data[l] < data[smallest]) smallest = l;
                if (r < size && data[r] < data[smallest]) smallest = r;
                if (smallest == i) break;
                swap(i, smallest);
                i = smallest;
            }
        }

        /** Insert value — O(log n) */
        void insert(int val) {
            if (size == data.length) throw new RuntimeException("Heap full");
            data[size++] = val;
            siftUp(size - 1);
        }

        /** Peek minimum — O(1) */
        int peek() {
            if (size == 0) throw new NoSuchElementException();
            return data[0];
        }

        /** Extract minimum — O(log n) */
        int extractMin() {
            if (size == 0) throw new NoSuchElementException();
            int min = data[0];
            data[0] = data[--size];   // move last to root
            siftDown(0);
            return min;
        }

        int size() { return size; }
        boolean isEmpty() { return size == 0; }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(data, size));
        }
    }

    // =========================================================
    // TOPIC 29 & 30 — MAX-HEAP implementation from scratch
    // =========================================================
    static class MaxHeap {
        private int[] data;
        private int size;

        MaxHeap(int capacity) {
            data = new int[capacity];
            size = 0;
        }

        private int parent(int i) { return (i - 1) / 2; }
        private int left(int i)   { return 2 * i + 1; }
        private int right(int i)  { return 2 * i + 2; }

        private void swap(int i, int j) {
            int tmp = data[i]; data[i] = data[j]; data[j] = tmp;
        }

        private void siftUp(int i) {
            while (i > 0 && data[parent(i)] < data[i]) {
                swap(i, parent(i));
                i = parent(i);
            }
        }

        private void siftDown(int i) {
            while (true) {
                int largest = i;
                int l = left(i), r = right(i);
                if (l < size && data[l] > data[largest]) largest = l;
                if (r < size && data[r] > data[largest]) largest = r;
                if (largest == i) break;
                swap(i, largest);
                i = largest;
            }
        }

        void insert(int val) {
            if (size == data.length) throw new RuntimeException("Heap full");
            data[size++] = val;
            siftUp(size - 1);
        }

        int peek()       { return data[0]; }
        int extractMax() {
            int max = data[0];
            data[0] = data[--size];
            siftDown(0);
            return max;
        }

        int size() { return size; }
        boolean isEmpty() { return size == 0; }
    }

    // =========================================================
    // TOPIC 31 — BUILD HEAP O(n) from an existing array
    // =========================================================
    /**
     * Naive approach: insert one by one → O(n log n)
     * Smart approach: start from last non-leaf, siftDown each → O(n)
     *
     * Why O(n)?  Most nodes are near leaves and sift down very little.
     * Mathematical proof: sum of heights = O(n).
     */
    static int[] buildMinHeap(int[] arr) {
        int[] heap = arr.clone();
        int n = heap.length;
        // last non-leaf index = n/2 - 1
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDownArr(heap, n, i);
        }
        return heap;
    }

    private static void siftDownArr(int[] heap, int n, int i) {
        while (true) {
            int smallest = i;
            int l = 2 * i + 1, r = 2 * i + 2;
            if (l < n && heap[l] < heap[smallest]) smallest = l;
            if (r < n && heap[r] < heap[smallest]) smallest = r;
            if (smallest == i) break;
            int tmp = heap[i]; heap[i] = heap[smallest]; heap[smallest] = tmp;
            i = smallest;
        }
    }

    // =========================================================
    // TOPIC 32 — K LARGEST / K SMALLEST ELEMENTS
    // =========================================================

    /**
     * K Largest elements — LeetCode #215 variant
     *
     * Approach A (MinHeap of size k): O(n log k)
     *   - Maintain a min-heap of size k
     *   - For each element: if heap.size < k → push
     *                       else if elem > heap.top → pop + push
     *   - Result: everything in the heap
     *
     * Approach B (sort desc): O(n log n) — simpler but slower
     * Approach C (QuickSelect): O(n) average — covered in Phase 3
     */
    static int[] kLargest(int[] nums, int k) {
        // Min-heap: keeps the k largest seen so far
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) minHeap.poll(); // remove smallest
        }
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = minHeap.poll();
        return result;
    }

    /**
     * Kth Largest element — LeetCode #215
     * Same approach: keep min-heap of size k → top = kth largest
     * Time: O(n log k)   Space: O(k)
     */
    static int kthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) minHeap.poll();
        }
        return minHeap.peek();
    }

    /**
     * K Smallest elements
     * Approach: Max-heap of size k — O(n log k)
     *   Keep max-heap; if elem < heap.top → pop + push
     *   Result: everything in the heap
     */
    static int[] kSmallest(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int num : nums) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) maxHeap.poll(); // remove largest
        }
        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = maxHeap.poll();
        return result;
    }

    /**
     * Kth Smallest — LeetCode #703 (stream variant)
     * Pattern: "Kth" problems → use opposite-type heap of size k
     *   kth Largest → Min-heap size k  (top = kth largest)
     *   kth Smallest → Max-heap size k (top = kth smallest)
     */
    static int kthSmallest(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int num : nums) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) maxHeap.poll();
        }
        return maxHeap.peek();
    }

    /**
     * Top K Frequent Elements — LeetCode #347
     * Approach: HashMap frequency count + Min-heap of size k
     * Time: O(n log k)
     */
    static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        // Min-heap sorted by frequency
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            minHeap.offer(new int[]{e.getKey(), e.getValue()});
            if (minHeap.size() > k) minHeap.poll();
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = minHeap.poll()[0];
        return result;
    }

    // =========================================================
    // TOPIC 33 — MERGE K SORTED LISTS — LeetCode #23
    // =========================================================

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int v) { val = v; }
    }

    /**
     * Merge K sorted linked lists
     *
     * Approach: Min-heap with (value, listIndex, node)
     *   1. Push head of each list into heap
     *   2. Poll min → append to result → push next of that node
     *   Time: O(N log k)  where N = total nodes, k = number of lists
     *   Space: O(k)
     */
    static ListNode mergeKLists(ListNode[] lists) {
        // heap: sorted by node.val
        PriorityQueue<ListNode> heap = new PriorityQueue<>((a, b) -> a.val - b.val);

        // seed heap with heads
        for (ListNode head : lists) {
            if (head != null) heap.offer(head);
        }

        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;

        while (!heap.isEmpty()) {
            ListNode node = heap.poll();
            cur.next = node;
            cur = cur.next;
            if (node.next != null) heap.offer(node.next);
        }

        return dummy.next;
    }

    /**
     * Merge K sorted ARRAYS (same pattern)
     * Time: O(N log k)
     */
    static int[] mergeKSortedArrays(int[][] arrays) {
        // heap entry: [value, arrayIndex, elementIndex]
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        int total = 0;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i].length > 0) {
                heap.offer(new int[]{arrays[i][0], i, 0});
                total += arrays[i].length;
            }
        }

        int[] result = new int[total];
        int idx = 0;
        while (!heap.isEmpty()) {
            int[] entry = heap.poll();
            result[idx++] = entry[0];
            int ai = entry[1], ei = entry[2];
            if (ei + 1 < arrays[ai].length) {
                heap.offer(new int[]{arrays[ai][ei + 1], ai, ei + 1});
            }
        }
        return result;
    }

    // =========================================================
    // BONUS — FIND MEDIAN FROM DATA STREAM — LeetCode #295
    // =========================================================
    /**
     * MedianFinder: Two-heap approach
     *   - maxHeap (lo): stores the smaller half
     *   - minHeap (hi): stores the larger half
     *
     * Invariant: maxHeap.size() == minHeap.size()
     *            OR maxHeap.size() == minHeap.size() + 1
     *
     * addNum: O(log n)   findMedian: O(1)
     */
    static class MedianFinder {
        private PriorityQueue<Integer> lo; // max-heap — smaller half
        private PriorityQueue<Integer> hi; // min-heap — larger half

        MedianFinder() {
            lo = new PriorityQueue<>(Collections.reverseOrder());
            hi = new PriorityQueue<>();
        }

        void addNum(int num) {
            lo.offer(num);          // always push to lo first
            hi.offer(lo.poll());    // balance: move max of lo → hi
            if (hi.size() > lo.size()) {
                lo.offer(hi.poll()); // keep lo >= hi in size
            }
        }

        double findMedian() {
            if (lo.size() > hi.size()) return lo.peek();
            return (lo.peek() + hi.peek()) / 2.0;
        }
    }

    // =========================================================
    // BONUS — HEAP SORT — O(n log n) in-place
    // =========================================================
    /**
     * Heap sort:
     *   Phase 1: Build max-heap from array — O(n)
     *   Phase 2: Repeatedly extract max to end — O(n log n)
     * Total: O(n log n) time, O(1) space
     */
    static void heapSort(int[] arr) {
        int n = arr.length;

        // Build max-heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            maxSiftDown(arr, n, i);
        }

        // Extract elements one by one
        for (int i = n - 1; i > 0; i--) {
            // Swap root (max) with last element
            int tmp = arr[0]; arr[0] = arr[i]; arr[i] = tmp;
            // Sift down on reduced heap
            maxSiftDown(arr, i, 0);
        }
    }

    private static void maxSiftDown(int[] arr, int n, int i) {
        while (true) {
            int largest = i;
            int l = 2 * i + 1, r = 2 * i + 2;
            if (l < n && arr[l] > arr[largest]) largest = l;
            if (r < n && arr[r] > arr[largest]) largest = r;
            if (largest == i) break;
            int tmp = arr[i]; arr[i] = arr[largest]; arr[largest] = tmp;
            i = largest;
        }
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 29 & 30: Min-Heap / Max-Heap ===");
        MinHeap minH = new MinHeap(10);
        for (int v : new int[]{5, 3, 8, 1, 4}) minH.insert(v);
        System.out.println("MinHeap: " + minH);
        System.out.println("Peek: " + minH.peek());
        System.out.println("Extract: " + minH.extractMin() + " → " + minH);

        MaxHeap maxH = new MaxHeap(10);
        for (int v : new int[]{5, 3, 8, 1, 4}) maxH.insert(v);
        System.out.println("\nMaxHeap extractMax: " + maxH.extractMax());

        System.out.println("\n=== TOPIC 31: Build Heap O(n) ===");
        int[] arr = {4, 10, 3, 5, 1, 8};
        int[] heap = buildMinHeap(arr);
        System.out.println("Input:    " + Arrays.toString(arr));
        System.out.println("MinHeap:  " + Arrays.toString(heap));

        System.out.println("\n=== TOPIC 32: K Largest / K Smallest ===");
        int[] nums = {3, 2, 1, 5, 6, 4};
        System.out.println("Array: " + Arrays.toString(nums));
        System.out.println("3 Largest:  " + Arrays.toString(kLargest(nums, 3)));
        System.out.println("3 Smallest: " + Arrays.toString(kSmallest(nums, 3)));
        System.out.println("Kth Largest (k=2): " + kthLargest(nums, 2));
        System.out.println("Kth Smallest (k=2): " + kthSmallest(nums, 2));

        int[] nums2 = {1, 1, 1, 2, 2, 3};
        System.out.println("Top 2 Frequent in " + Arrays.toString(nums2) + ": "
                + Arrays.toString(topKFrequent(nums2, 2)));

        System.out.println("\n=== TOPIC 33: Merge K Sorted Lists ===");
        // Build: [1->4->5], [1->3->4], [2->6]
        ListNode l1 = new ListNode(1); l1.next = new ListNode(4); l1.next.next = new ListNode(5);
        ListNode l2 = new ListNode(1); l2.next = new ListNode(3); l2.next.next = new ListNode(4);
        ListNode l3 = new ListNode(2); l3.next = new ListNode(6);
        ListNode merged = mergeKLists(new ListNode[]{l1, l2, l3});
        StringBuilder sb = new StringBuilder("Merged: ");
        for (ListNode n = merged; n != null; n = n.next) sb.append(n.val).append(n.next != null ? "->" : "");
        System.out.println(sb);

        int[][] arrays = {{1, 4, 7}, {2, 5, 8}, {3, 6, 9}};
        System.out.println("Merge K Arrays: " + Arrays.toString(mergeKSortedArrays(arrays)));

        System.out.println("\n=== BONUS: Median Finder ===");
        MedianFinder mf = new MedianFinder();
        for (int v : new int[]{1, 2, 3, 4, 5}) {
            mf.addNum(v);
            System.out.println("Add " + v + " → median = " + mf.findMedian());
        }

        System.out.println("\n=== BONUS: Heap Sort ===");
        int[] sortArr = {12, 11, 13, 5, 6, 7};
        System.out.println("Before: " + Arrays.toString(sortArr));
        heapSort(sortArr);
        System.out.println("After:  " + Arrays.toString(sortArr));
    }
}
