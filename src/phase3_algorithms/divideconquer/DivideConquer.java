package phase3_algorithms.divideconquer;

import java.util.*;

/**
 * PHASE 3.4 — DIVIDE & CONQUER
 * ==============================
 * Topics covered:
 *  53. Merge sort pattern (D&C on arrays)
 *  54. Quick select — Kth element O(n) average
 *  55. Closest pair of points O(n log n)
 *
 * DIVIDE & CONQUER PARADIGM:
 * ┌──────────────────────────────────────────────────────┐
 * │  solve(problem):                                      │
 * │    if problem is small → solve directly (base case)   │
 * │    left, right = divide(problem)                      │
 * │    leftAns  = solve(left)                             │
 * │    rightAns = solve(right)                            │
 * │    return combine(leftAns, rightAns)                  │
 * └──────────────────────────────────────────────────────┘
 *
 * Master Theorem for T(n) = aT(n/b) + O(n^d):
 *   if d > log_b(a)  → O(n^d)
 *   if d = log_b(a)  → O(n^d log n)
 *   if d < log_b(a)  → O(n^(log_b a))
 *
 * MergeSort:  a=2, b=2, d=1  → d = log_b(a) → O(n log n)
 * QuickSelect: a=1, b=2, d=1 → d > log_b(a) → O(n) average
 */
public class DivideConquer {

    static final Random RAND = new Random();

    // =========================================================
    // TOPIC 53 — MERGE SORT PATTERN (D&C on arrays)
    // =========================================================

    /**
     * Classic merge sort — already in Sorting.java, shown here to
     * highlight the D&C structure and real-world problem applications.
     *
     * D&C structure:
     *   Divide  : split array at mid          O(1)
     *   Conquer : sort left half, sort right  T(n/2) each
     *   Combine : merge two sorted halves      O(n)
     * → T(n) = 2T(n/2) + O(n) = O(n log n)
     */
    static void mergeSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int mid = l + (r - l) / 2;
        mergeSort(arr, l, mid);
        mergeSort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    private static void merge(int[] arr, int l, int mid, int r) {
        int[] tmp = Arrays.copyOfRange(arr, l, r + 1);
        int i = 0, j = mid - l + 1, k = l;
        int n1 = mid - l + 1;
        while (i < n1 && j < tmp.length) {
            arr[k++] = tmp[i] <= tmp[j] ? tmp[i++] : tmp[j++];
        }
        while (i < n1)          arr[k++] = tmp[i++];
        while (j < tmp.length)  arr[k++] = tmp[j++];
    }

    // ---------------------------------------------------------
    // D&C Pattern: Maximum Subarray — LeetCode #53
    // ---------------------------------------------------------
    /**
     * Kadane's is O(n) but D&C solution shows the pattern clearly.
     * Divide at mid:
     *   max subarray is entirely in left half, OR
     *   max subarray is entirely in right half, OR
     *   max subarray crosses the midpoint
     * For crossing case: expand left from mid, expand right from mid+1.
     *
     * T(n) = 2T(n/2) + O(n) = O(n log n)
     */
    static int maxSubarrayDC(int[] nums, int l, int r) {
        if (l == r) return nums[l];
        int mid = l + (r - l) / 2;
        int leftMax  = maxSubarrayDC(nums, l, mid);
        int rightMax = maxSubarrayDC(nums, mid + 1, r);
        int crossMax = maxCrossing(nums, l, mid, r);
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }

    private static int maxCrossing(int[] nums, int l, int mid, int r) {
        // Expand left from mid
        int leftSum = Integer.MIN_VALUE, sum = 0;
        for (int i = mid; i >= l; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }
        // Expand right from mid+1
        int rightSum = Integer.MIN_VALUE; sum = 0;
        for (int i = mid + 1; i <= r; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }
        return leftSum + rightSum;
    }

    // ---------------------------------------------------------
    // D&C Pattern: Count Inversions — LeetCode #493 variant
    // ---------------------------------------------------------
    /**
     * Count pairs (i,j) where i<j and nums[i]>nums[j].
     * Merge sort: when right element placed before left elements,
     *             all remaining left elements form inversions.
     * O(n log n)
     */
    static long countInversions(int[] arr) {
        return mergeCount(arr, 0, arr.length - 1);
    }

    private static long mergeCount(int[] arr, int l, int r) {
        if (l >= r) return 0;
        int mid = l + (r - l) / 2;
        long count = mergeCount(arr, l, mid) + mergeCount(arr, mid + 1, r);
        // merge and count
        int[] tmp = Arrays.copyOfRange(arr, l, r + 1);
        int i = 0, j = mid - l + 1, k = l;
        int n1 = mid - l + 1;
        while (i < n1 && j < tmp.length) {
            if (tmp[i] <= tmp[j]) arr[k++] = tmp[i++];
            else {
                count += (n1 - i); // all remaining in left > tmp[j]
                arr[k++] = tmp[j++];
            }
        }
        while (i < n1)         arr[k++] = tmp[i++];
        while (j < tmp.length) arr[k++] = tmp[j++];
        return count;
    }

    // ---------------------------------------------------------
    // D&C Pattern: Majority Element — LeetCode #169
    // ---------------------------------------------------------
    /**
     * Divide: find majority in left half and right half.
     * Combine: count each candidate in full range; winner wins.
     * T(n) = 2T(n/2) + O(n) = O(n log n)
     * (Boyer-Moore is O(n) but D&C solution illustrates the pattern)
     */
    static int majorityElement(int[] nums) {
        return majorityDC(nums, 0, nums.length - 1);
    }

    private static int majorityDC(int[] nums, int l, int r) {
        if (l == r) return nums[l];
        int mid = l + (r - l) / 2;
        int left  = majorityDC(nums, l, mid);
        int right = majorityDC(nums, mid + 1, r);
        if (left == right) return left;
        // count both in [l..r], return the one with more occurrences
        int lCount = 0, rCount = 0;
        for (int i = l; i <= r; i++) {
            if (nums[i] == left)  lCount++;
            if (nums[i] == right) rCount++;
        }
        return lCount > rCount ? left : right;
    }

    // =========================================================
    // TOPIC 54 — QUICK SELECT: Kth SMALLEST/LARGEST — O(n) avg
    // =========================================================

    /**
     * QuickSelect — find Kth smallest element in unsorted array.
     *
     * Idea: like QuickSort but only recurse into ONE side.
     *   1. Pick random pivot, partition array.
     *   2. If pivot index == k → found!
     *      If pivot index > k  → search left side
     *      If pivot index < k  → search right side
     *
     * Time: O(n) average, O(n²) worst (bad pivot every time)
     *       With random pivot: expected O(n) — standard in interviews.
     * Space: O(1) in-place (O(log n) stack with recursion)
     *
     * LeetCode #215 — Kth Largest Element in an Array
     */
    static int quickSelect(int[] nums, int k) {
        // kth largest = (n-k)th smallest (0-indexed)
        return quickSelectHelper(nums, 0, nums.length - 1, nums.length - k);
    }

    private static int quickSelectHelper(int[] nums, int lo, int hi, int k) {
        if (lo == hi) return nums[lo];

        int pivotIdx = partition(nums, lo, hi);
        if      (pivotIdx == k) return nums[pivotIdx];
        else if (pivotIdx < k)  return quickSelectHelper(nums, pivotIdx + 1, hi, k);
        else                    return quickSelectHelper(nums, lo, pivotIdx - 1, k);
    }

    /** Lomuto partition with random pivot — returns final pivot index */
    private static int partition(int[] arr, int lo, int hi) {
        int randIdx = lo + RAND.nextInt(hi - lo + 1);
        swap(arr, randIdx, hi); // move pivot to end
        int pivot = arr[hi], i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (arr[j] <= pivot) swap(arr, ++i, j);
        }
        swap(arr, i + 1, hi);
        return i + 1;
    }

    /**
     * Kth Smallest in Sorted Matrix — LeetCode #378
     * Matrix: each row and column sorted.
     * Approach: binary search on value range + count ≤ mid.
     * Time: O(n log(max-min))
     */
    static int kthSmallestMatrix(int[][] matrix, int k) {
        int n = matrix.length;
        int lo = matrix[0][0], hi = matrix[n-1][n-1];
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            // Count elements <= mid using staircase traversal
            int count = 0, row = n - 1, col = 0;
            while (row >= 0 && col < n) {
                if (matrix[row][col] <= mid) { count += row + 1; col++; }
                else                         row--;
            }
            if (count >= k) hi = mid;
            else            lo = mid + 1;
        }
        return lo;
    }

    /**
     * Kth Largest in Stream — LeetCode #703
     * Maintain a min-heap of size k: top = kth largest.
     */
    static class KthLargest {
        private final PriorityQueue<Integer> minHeap;
        private final int k;

        KthLargest(int k, int[] nums) {
            this.k = k;
            minHeap = new PriorityQueue<>();
            for (int n : nums) add(n);
        }

        int add(int val) {
            minHeap.offer(val);
            if (minHeap.size() > k) minHeap.poll();
            return minHeap.peek();
        }
    }

    // =========================================================
    // TOPIC 55 — CLOSEST PAIR OF POINTS — O(n log n)
    // =========================================================

    /**
     * Given n points, find the pair with minimum Euclidean distance.
     *
     * Brute force: O(n²) — check all pairs.
     *
     * D&C approach: O(n log n)
     *   1. Sort points by x-coordinate.
     *   2. Divide: split into left and right halves.
     *   3. Conquer: find closest pair in each half → d = min(dL, dR)
     *   4. Combine: check strip of width 2d around midpoint.
     *      Key insight: strip can have at most O(1) points within d of
     *      any strip point → strip check is O(n), not O(n²).
     *
     * T(n) = 2T(n/2) + O(n log n) = O(n log²n)
     * With pre-sorting by y in strip: T(n) = O(n log n)
     */

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        @Override public String toString() { return "(" + x + "," + y + ")"; }
    }

    static double closestPair(Point[] points) {
        // Sort by x once
        Point[] sorted = points.clone();
        Arrays.sort(sorted, Comparator.comparingDouble(p -> p.x));
        return closestPairRec(sorted, 0, sorted.length - 1);
    }

    private static double closestPairRec(Point[] pts, int l, int r) {
        if (r - l < 3) return bruteForce(pts, l, r);

        int mid = l + (r - l) / 2;
        double midX = pts[mid].x;

        double dL = closestPairRec(pts, l, mid);
        double dR = closestPairRec(pts, mid + 1, r);
        double d  = Math.min(dL, dR);

        // Collect strip: points within d of midX
        List<Point> strip = new ArrayList<>();
        for (int i = l; i <= r; i++) {
            if (Math.abs(pts[i].x - midX) < d) strip.add(pts[i]);
        }

        // Sort strip by y (for O(n) strip check)
        strip.sort(Comparator.comparingDouble(p -> p.y));

        // Check strip: each point compared to at most 7 neighbors
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && strip.get(j).y - strip.get(i).y < d; j++) {
                d = Math.min(d, dist(strip.get(i), strip.get(j)));
            }
        }
        return d;
    }

    private static double bruteForce(Point[] pts, int l, int r) {
        double min = Double.MAX_VALUE;
        for (int i = l; i <= r; i++)
            for (int j = i + 1; j <= r; j++)
                min = Math.min(min, dist(pts[i], pts[j]));
        return min;
    }

    private static double dist(Point a, Point b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    // =========================================================
    // BONUS — ADDITIONAL D&C PROBLEMS
    // =========================================================

    /**
     * LeetCode #4 — Median of Two Sorted Arrays — O(log(m+n))
     *
     * Key insight: binary search on partition of the smaller array.
     * Find i in A and j in B such that:
     *   A[i-1] <= B[j] AND B[j-1] <= A[i]
     * where i + j = (m + n + 1) / 2
     *
     * This is classic D&C: each step eliminates half the search space.
     */
    static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // Ensure nums1 is the shorter array
        if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);
        int m = nums1.length, n = nums2.length;
        int lo = 0, hi = m;
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;       // partition nums1
            int j = (m + n + 1) / 2 - i;      // partition nums2
            int maxL1 = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
            int minR1 = (i == m) ? Integer.MAX_VALUE : nums1[i];
            int maxL2 = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
            int minR2 = (j == n) ? Integer.MAX_VALUE : nums2[j];

            if (maxL1 <= minR2 && maxL2 <= minR1) {
                // Found correct partition
                int maxLeft  = Math.max(maxL1, maxL2);
                int minRight = Math.min(minR1, minR2);
                if ((m + n) % 2 == 1) return maxLeft;
                return (maxLeft + minRight) / 2.0;
            } else if (maxL1 > minR2) hi = i - 1; // move left in nums1
            else                      lo = i + 1; // move right in nums1
        }
        return 0.0;
    }

    /**
     * LeetCode #23 — Merge K Sorted Lists using D&C
     * Instead of merging one by one O(kN), use D&C:
     *   Pair up lists → merge pairs → repeat
     * T(n) = O(N log k) where N = total nodes
     */
    static class ListNode {
        int val; ListNode next;
        ListNode(int v) { val = v; }
    }

    static ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) return null;
        return mergeRange(lists, 0, lists.length - 1);
    }

    private static ListNode mergeRange(ListNode[] lists, int l, int r) {
        if (l == r) return lists[l];
        int mid = l + (r - l) / 2;
        ListNode left  = mergeRange(lists, l, mid);
        ListNode right = mergeRange(lists, mid + 1, r);
        return mergeTwoLists(left, right);
    }

    private static ListNode mergeTwoLists(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), cur = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) { cur.next = a; a = a.next; }
            else                { cur.next = b; b = b.next; }
            cur = cur.next;
        }
        cur.next = a != null ? a : b;
        return dummy.next;
    }

    /**
     * LeetCode #240 / Matrix Search using D&C
     * Staircase search — already in BinarySearch.java.
     * Shown here as example of D&C on 2D space.
     */

    // =========================================================
    // HELPER
    // =========================================================
    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 53: Merge Sort Pattern (D&C) ===");

        int[] arr = {5, 2, 8, 1, 9, 3};
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("MergeSort:  " + Arrays.toString(arr));

        int[] inv = {8, 4, 2, 1};
        System.out.println("Inversions in [8,4,2,1]: " + countInversions(inv)); // 6

        int[] sub = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("#53 MaxSubarray D&C: "
                + maxSubarrayDC(sub, 0, sub.length - 1)); // 6

        int[] maj = {2, 2, 1, 1, 1, 2, 2};
        System.out.println("#169 MajorityElement: " + majorityElement(maj)); // 2

        System.out.println("\n=== TOPIC 54: Quick Select ===");
        int[] nums = {3, 2, 1, 5, 6, 4};
        System.out.println("Array: " + Arrays.toString(nums.clone()));
        System.out.println("#215 Kth Largest (k=2): "
                + quickSelect(nums.clone(), 2)); // 5
        System.out.println("#215 Kth Largest (k=1): "
                + quickSelect(nums.clone(), 1)); // 6

        int[][] matrix = {{1,5,9},{10,11,13},{12,13,15}};
        System.out.println("#378 Kth Smallest Matrix k=8: "
                + kthSmallestMatrix(matrix, 8)); // 13

        KthLargest kl = new KthLargest(3, new int[]{4, 5, 8, 2});
        System.out.println("#703 KthLargest stream add(3): " + kl.add(3)); // 4
        System.out.println("#703 KthLargest stream add(5): " + kl.add(5)); // 5
        System.out.println("#703 KthLargest stream add(10): " + kl.add(10)); // 8

        System.out.println("\n=== TOPIC 55: Closest Pair of Points ===");
        Point[] points = {
            new Point(2, 3), new Point(12, 30), new Point(40, 50),
            new Point(5, 1), new Point(12, 10), new Point(3, 4)
        };
        System.out.printf("Closest distance: %.4f%n", closestPair(points)); // ~1.414 (between (2,3)&(3,4))

        Point[] simple = {
            new Point(0, 0), new Point(3, 4), new Point(1, 1), new Point(6, 8)
        };
        System.out.printf("Closest distance: %.4f%n", closestPair(simple)); // ~1.414

        System.out.println("\n=== BONUS: Median of Two Sorted Arrays ===");
        System.out.println("#4 median [1,3] & [2]:     "
                + findMedianSortedArrays(new int[]{1,3}, new int[]{2}));       // 2.0
        System.out.println("#4 median [1,2] & [3,4]:   "
                + findMedianSortedArrays(new int[]{1,2}, new int[]{3,4}));     // 2.5
        System.out.println("#4 median [0,0] & [0,0]:   "
                + findMedianSortedArrays(new int[]{0,0}, new int[]{0,0}));     // 0.0

        System.out.println("\n=== BONUS: Merge K Lists (D&C) ===");
        // [1->4->5], [1->3->4], [2->6]
        ListNode l1 = new ListNode(1); l1.next = new ListNode(4); l1.next.next = new ListNode(5);
        ListNode l2 = new ListNode(1); l2.next = new ListNode(3); l2.next.next = new ListNode(4);
        ListNode l3 = new ListNode(2); l3.next = new ListNode(6);
        StringBuilder sb = new StringBuilder("#23 Merged: ");
        for (ListNode n = mergeKLists(new ListNode[]{l1, l2, l3}); n != null; n = n.next)
            sb.append(n.val).append(n.next != null ? "->" : "");
        System.out.println(sb);
    }
}
