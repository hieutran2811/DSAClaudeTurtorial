package phase3_algorithms.sorting;

import java.util.*;

/**
 * PHASE 3.1 — SORTING ALGORITHMS
 * =================================
 * Topics covered:
 *  38. Bubble, Selection, Insertion sort  — O(n²)
 *  39. Merge sort                         — O(n log n), stable
 *  40. Quick sort                         — O(n log n) avg, O(n²) worst
 *  41. Heap sort                          — O(n log n), in-place
 *  42. Counting sort, Radix sort          — O(n) for bounded integers
 *
 * WHEN TO USE WHICH:
 * ┌─────────────────┬──────────────┬──────────┬─────────┬──────────────────────────────┐
 * │ Algorithm       │ Time (avg)   │ Space    │ Stable? │ Best use case                │
 * ├─────────────────┼──────────────┼──────────┼─────────┼──────────────────────────────┤
 * │ Bubble          │ O(n²)        │ O(1)     │ Yes     │ Teaching only                │
 * │ Selection       │ O(n²)        │ O(1)     │ No      │ Minimize swaps               │
 * │ Insertion       │ O(n²)/O(n)*  │ O(1)     │ Yes     │ Nearly sorted / small n      │
 * │ Merge           │ O(n log n)   │ O(n)     │ Yes     │ Stable sort needed, linked   │
 * │ Quick           │ O(n log n)   │ O(log n) │ No      │ General purpose, cache-fast  │
 * │ Heap            │ O(n log n)   │ O(1)     │ No      │ Guaranteed O(n log n)        │
 * │ Counting        │ O(n + k)     │ O(k)     │ Yes     │ Small int range              │
 * │ Radix           │ O(d*(n+k))   │ O(n+k)   │ Yes     │ Large n, fixed-length ints   │
 * └─────────────────┴──────────────┴──────────┴─────────┴──────────────────────────────┘
 * * Insertion O(n) when array is nearly sorted
 */
public class Sorting {

    // =========================================================
    // TOPIC 38 — O(n²) SORTS
    // =========================================================

    /**
     * BUBBLE SORT
     * Idea: repeatedly swap adjacent elements if out of order.
     *       After pass i, the i-th largest is in its final place.
     * Optimization: if no swap in a pass → already sorted → break early.
     * Time: O(n²) worst/avg, O(n) best (already sorted with flag)
     * Space: O(1)  Stable: YES
     */
    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // early exit if sorted
        }
    }

    /**
     * SELECTION SORT
     * Idea: find minimum in [i..n-1], swap to position i.
     * Always does exactly n*(n-1)/2 comparisons regardless of input.
     * Advantage: minimizes the number of swaps — useful when write cost is high.
     * Time: O(n²) always   Space: O(1)   Stable: NO (can skip equal elements)
     */
    static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            if (minIdx != i) swap(arr, i, minIdx);
        }
    }

    /**
     * INSERTION SORT
     * Idea: maintain sorted prefix; insert arr[i] into correct position.
     * Like sorting a hand of cards.
     * Time: O(n²) worst, O(n) best (sorted input) — fast for small/nearly-sorted
     * Space: O(1)   Stable: YES
     *
     * Used internally by Java's Arrays.sort() for small subarrays (< 32 elements).
     */
    static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            // shift elements greater than key one position right
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // =========================================================
    // TOPIC 39 — MERGE SORT  O(n log n), stable
    // =========================================================

    /**
     * MERGE SORT — Divide & Conquer
     * Idea:
     *   1. Divide array in half recursively until size = 1
     *   2. Merge two sorted halves into one sorted array
     *
     * Time: O(n log n) — always (not affected by input order)
     * Space: O(n) auxiliary array
     * Stable: YES — equal elements keep original order
     *
     * Best for:
     *   - Stable sort required
     *   - Sorting linked lists (no random access needed)
     *   - External sort (data too large for RAM)
     *   - Count inversions (bonus problem)
     */
    static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        // Copy both halves to temp arrays
        int n1 = mid - left + 1, n2 = right - mid;
        int[] L = Arrays.copyOfRange(arr, left, mid + 1);
        int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            // <= keeps it stable (left side wins on tie)
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else               arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    /**
     * BONUS: Count Inversions using Merge Sort — O(n log n)
     * Inversion: pair (i,j) where i < j but arr[i] > arr[j]
     * Key insight: during merge, when R[j] < L[i],
     *              all remaining L[i..n1-1] are > R[j] → add (n1 - i) inversions.
     * LeetCode #493 — Reverse Pairs (variant)
     */
    static long countInversions(int[] arr, int left, int right) {
        if (left >= right) return 0;
        int mid = left + (right - left) / 2;
        long count = countInversions(arr, left, mid)
                   + countInversions(arr, mid + 1, right);

        // merge and count
        int[] L = Arrays.copyOfRange(arr, left, mid + 1);
        int[] R = Arrays.copyOfRange(arr, mid + 1, right + 1);
        int i = 0, j = 0, k = left;
        while (i < L.length && j < R.length) {
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else {
                count += (L.length - i); // all remaining in L are > R[j]
                arr[k++] = R[j++];
            }
        }
        while (i < L.length) arr[k++] = L[i++];
        while (j < R.length) arr[k++] = R[j++];
        return count;
    }

    // =========================================================
    // TOPIC 40 — QUICK SORT  O(n log n) avg, O(n²) worst
    // =========================================================

    /**
     * QUICK SORT — Divide & Conquer (in-place)
     * Idea:
     *   1. Pick a pivot
     *   2. Partition: elements < pivot go left, > pivot go right
     *   3. Recurse on both sides
     *
     * Time: O(n log n) avg, O(n²) worst (sorted array + bad pivot choice)
     * Space: O(log n) stack  (O(n) worst)
     * Stable: NO
     *
     * Pivot strategies:
     *   - Last element    : simple but O(n²) on sorted input
     *   - Random element  : expected O(n log n), avoids adversarial input
     *   - Median-of-three : pick median of first/mid/last → practical best
     *
     * Java's Arrays.sort() uses dual-pivot quicksort (Yaroslavskiy, 2009).
     */

    static final Random RAND = new Random();

    static void quickSort(int[] arr, int low, int high) {
        if (low >= high) return;
        int pivotIdx = partition(arr, low, high);
        quickSort(arr, low, pivotIdx - 1);
        quickSort(arr, pivotIdx + 1, high);
    }

    /** Lomuto partition scheme — pivot = last element */
    private static int partition(int[] arr, int low, int high) {
        // Randomize pivot to avoid O(n²) on sorted input
        int randIdx = low + RAND.nextInt(high - low + 1);
        swap(arr, randIdx, high);

        int pivot = arr[high];
        int i = low - 1; // boundary of elements < pivot

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high); // place pivot in correct position
        return i + 1;
    }

    /**
     * THREE-WAY QUICK SORT (Dutch National Flag partition)
     * Handles duplicate elements efficiently — O(n log n) even with many dupes.
     * Partitions into: [< pivot | == pivot | > pivot]
     * LeetCode #75 — Sort Colors uses this exact idea.
     */
    static void quickSort3Way(int[] arr, int low, int high) {
        if (low >= high) return;
        int lt = low, gt = high, i = low;
        int pivot = arr[low + RAND.nextInt(high - low + 1)];

        while (i <= gt) {
            if      (arr[i] < pivot) swap(arr, lt++, i++);
            else if (arr[i] > pivot) swap(arr, i, gt--);
            else                     i++;
        }
        // arr[low..lt-1] < pivot, arr[lt..gt] == pivot, arr[gt+1..high] > pivot
        quickSort3Way(arr, low, lt - 1);
        quickSort3Way(arr, gt + 1, high);
    }

    // =========================================================
    // TOPIC 41 — HEAP SORT  O(n log n), in-place
    // =========================================================

    /**
     * HEAP SORT
     * Phase 1: Build max-heap from array — O(n)
     * Phase 2: Swap root (max) with last, shrink heap, siftDown — O(n log n)
     *
     * Time: O(n log n) guaranteed (unlike quicksort, no O(n²) case)
     * Space: O(1) — in-place!
     * Stable: NO
     *
     * Drawback: poor cache locality vs quicksort (random access pattern)
     * Use when: guaranteed O(n log n) needed with O(1) space.
     */
    static void heapSort(int[] arr) {
        int n = arr.length;
        // Phase 1: build max-heap
        for (int i = n / 2 - 1; i >= 0; i--) maxSiftDown(arr, n, i);
        // Phase 2: extract max to end repeatedly
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);        // move current max to end
            maxSiftDown(arr, i, 0); // restore heap property
        }
    }

    private static void maxSiftDown(int[] arr, int n, int i) {
        while (true) {
            int largest = i, l = 2*i+1, r = 2*i+2;
            if (l < n && arr[l] > arr[largest]) largest = l;
            if (r < n && arr[r] > arr[largest]) largest = r;
            if (largest == i) break;
            swap(arr, i, largest);
            i = largest;
        }
    }

    // =========================================================
    // TOPIC 42 — LINEAR SORTS: COUNTING & RADIX  O(n)
    // =========================================================

    /**
     * COUNTING SORT
     * Idea: count frequency of each value, then reconstruct.
     * Constraint: values must be non-negative integers in range [0, k].
     *
     * Time: O(n + k)  Space: O(k)  Stable: YES (if done carefully)
     *
     * Best when k = O(n) — range is proportional to input size.
     * Used internally in Radix sort for each digit pass.
     */
    static int[] countingSort(int[] arr, int maxVal) {
        int[] count = new int[maxVal + 1];
        for (int x : arr) count[x]++;

        // Convert count[] to prefix sums (cumulative) for stable placement
        for (int i = 1; i <= maxVal; i++) count[i] += count[i - 1];

        int[] output = new int[arr.length];
        // Traverse BACKWARDS for stability
        for (int i = arr.length - 1; i >= 0; i--) {
            output[--count[arr[i]]] = arr[i];
        }
        return output;
    }

    /**
     * RADIX SORT (LSD — Least Significant Digit first)
     * Idea: sort digit by digit from LSD to MSD using stable counting sort.
     *
     * Time: O(d * (n + k))  where d = digits, k = base (10)
     *       For 32-bit ints: d ≈ 10, so effectively O(n)
     * Space: O(n + k)  Stable: YES
     *
     * Best when: large n, values have fixed digit count.
     * Example: sort 1 million phone numbers.
     */
    static void radixSort(int[] arr) {
        int max = Arrays.stream(arr).max().getAsInt();

        // Process each digit position (ones, tens, hundreds, ...)
        for (int exp = 1; max / exp > 0; exp *= 10) {
            countingSortByDigit(arr, exp);
        }
    }

    private static void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10]; // digits 0-9

        for (int x : arr) count[(x / exp) % 10]++;
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];

        // Backwards for stability
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[--count[digit]] = arr[i];
        }
        System.arraycopy(output, 0, arr, 0, n);
    }

    // =========================================================
    // LEETCODE PROBLEMS USING SORTING PATTERNS
    // =========================================================

    /**
     * LeetCode #75 — Sort Colors (Dutch National Flag)
     * Sort array of 0s, 1s, 2s in one pass, O(n) time O(1) space.
     * Same as 3-way partition with pivot = 1.
     */
    static void sortColors(int[] nums) {
        int lo = 0, mid = 0, hi = nums.length - 1;
        while (mid <= hi) {
            if      (nums[mid] == 0) swap(nums, lo++, mid++);
            else if (nums[mid] == 2) swap(nums, mid, hi--);
            else                     mid++;
        }
    }

    /**
     * LeetCode #56 — Merge Intervals
     * Sort by start time, then greedily merge overlapping intervals.
     * Time: O(n log n)
     */
    static int[][] mergeIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> res = new ArrayList<>();
        res.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] last = res.get(res.size() - 1);
            if (intervals[i][0] <= last[1]) {
                last[1] = Math.max(last[1], intervals[i][1]); // extend
            } else {
                res.add(intervals[i]);
            }
        }
        return res.toArray(new int[0][]);
    }

    /**
     * LeetCode #179 — Largest Number
     * Sort strings by custom comparator: prefer (a+b) over (b+a).
     * E.g. "9" vs "34": "934" > "349" so "9" comes first.
     */
    static String largestNumber(int[] nums) {
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) strs[i] = String.valueOf(nums[i]);
        Arrays.sort(strs, (a, b) -> (b + a).compareTo(a + b));
        if (strs[0].equals("0")) return "0";
        StringBuilder sb = new StringBuilder();
        for (String s : strs) sb.append(s);
        return sb.toString();
    }

    /**
     * LeetCode #315 — Count of Smaller Numbers After Self
     * Uses modified merge sort to count inversions per element.
     * Time: O(n log n)
     */
    static List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int[] indices = new int[n]; // track original positions through sorting
        for (int i = 0; i < n; i++) indices[i] = i;

        mergeSortCount(nums, indices, result, 0, n - 1);

        List<Integer> ans = new ArrayList<>();
        for (int r : result) ans.add(r);
        return ans;
    }

    private static void mergeSortCount(int[] nums, int[] indices, int[] result, int l, int r) {
        if (l >= r) return;
        int mid = l + (r - l) / 2;
        mergeSortCount(nums, indices, result, l, mid);
        mergeSortCount(nums, indices, result, mid + 1, r);

        int[] tmp = new int[r - l + 1];
        int i = l, j = mid + 1, k = 0;
        while (i <= mid && j <= r) {
            if (nums[indices[i]] <= nums[indices[j]]) {
                result[indices[i]] += (j - mid - 1); // count elements from right already placed
                tmp[k++] = indices[i++];
            } else {
                tmp[k++] = indices[j++];
            }
        }
        while (i <= mid) {
            result[indices[i]] += (j - mid - 1);
            tmp[k++] = indices[i++];
        }
        while (j <= r) tmp[k++] = indices[j++];
        System.arraycopy(tmp, 0, indices, l, tmp.length);
    }

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
        System.out.println("=== TOPIC 38: O(n²) Sorts ===");
        int[] arr;

        arr = new int[]{64, 34, 25, 12, 22, 11, 90};
        bubbleSort(arr);
        System.out.println("Bubble Sort:    " + Arrays.toString(arr));

        arr = new int[]{64, 25, 12, 22, 11};
        selectionSort(arr);
        System.out.println("Selection Sort: " + Arrays.toString(arr));

        arr = new int[]{12, 11, 13, 5, 6};
        insertionSort(arr);
        System.out.println("Insertion Sort: " + Arrays.toString(arr));

        // Nearly sorted — insertion sort shines
        arr = new int[]{1, 2, 3, 5, 4};
        insertionSort(arr);
        System.out.println("Insertion (nearly sorted): " + Arrays.toString(arr));

        System.out.println("\n=== TOPIC 39: Merge Sort ===");
        arr = new int[]{38, 27, 43, 3, 9, 82, 10};
        System.out.println("Before: " + Arrays.toString(arr));
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("After:  " + Arrays.toString(arr));

        int[] inv = {2, 4, 1, 3, 5};
        System.out.println("Inversions in " + Arrays.toString(inv)
                + ": " + countInversions(inv.clone(), 0, inv.length - 1));

        System.out.println("\n=== TOPIC 40: Quick Sort ===");
        arr = new int[]{10, 7, 8, 9, 1, 5};
        System.out.println("Before:   " + Arrays.toString(arr));
        quickSort(arr, 0, arr.length - 1);
        System.out.println("QuickSort: " + Arrays.toString(arr));

        arr = new int[]{4, 2, 4, 1, 4, 3, 2};
        System.out.println("3-Way Before: " + Arrays.toString(arr));
        quickSort3Way(arr, 0, arr.length - 1);
        System.out.println("3-Way After:  " + Arrays.toString(arr));

        System.out.println("\n=== TOPIC 41: Heap Sort ===");
        arr = new int[]{12, 11, 13, 5, 6, 7};
        heapSort(arr);
        System.out.println("Heap Sort: " + Arrays.toString(arr));

        System.out.println("\n=== TOPIC 42: Counting Sort & Radix Sort ===");
        int[] csArr = {4, 2, 2, 8, 3, 3, 1};
        System.out.println("Counting Sort: " + Arrays.toString(countingSort(csArr, 8)));

        int[] rdArr = {170, 45, 75, 90, 802, 24, 2, 66};
        radixSort(rdArr);
        System.out.println("Radix Sort:    " + Arrays.toString(rdArr));

        System.out.println("\n=== LeetCode Problems ===");

        // #75 Sort Colors
        int[] colors = {2, 0, 2, 1, 1, 0};
        sortColors(colors);
        System.out.println("#75 Sort Colors: " + Arrays.toString(colors));

        // #56 Merge Intervals
        int[][] intervals = {{1,3},{2,6},{8,10},{15,18}};
        System.out.println("#56 Merge Intervals: " + Arrays.deepToString(mergeIntervals(intervals)));

        // #179 Largest Number
        System.out.println("#179 Largest Number [3,30,34,5,9]: "
                + largestNumber(new int[]{3, 30, 34, 5, 9}));

        // #315 Count of Smaller Numbers After Self
        System.out.println("#315 Count Smaller [5,2,6,1]: "
                + countSmaller(new int[]{5, 2, 6, 1}));
    }
}
