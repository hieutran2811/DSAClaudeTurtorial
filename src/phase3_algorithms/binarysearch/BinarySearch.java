package phase3_algorithms.binarysearch;

import java.util.*;

/**
 * PHASE 3.2 — BINARY SEARCH
 * ===========================
 * Topics covered:
 *  43. Classic binary search
 *  44. First / last position (lower bound / upper bound)
 *  45. Search in rotated sorted array
 *  46. Binary search on answer (min/max problems)
 *  47. Peak finding
 *
 * CORE TEMPLATE — memorize this, derive everything else from it:
 * ┌──────────────────────────────────────────────────────────────┐
 * │  int lo = 0, hi = n - 1;                                     │
 * │  while (lo <= hi) {                                           │
 * │      int mid = lo + (hi - lo) / 2;  // avoid overflow        │
 * │      if (condition) return mid;      // found                 │
 * │      else if (goLeft)  hi = mid - 1;                         │
 * │      else              lo = mid + 1;                         │
 * │  }                                                            │
 * │  return -1; // not found                                      │
 * └──────────────────────────────────────────────────────────────┘
 *
 * KEY INSIGHT: Binary search works on any MONOTONE predicate —
 * not just sorted arrays. If you can say "everything left of X
 * satisfies P, everything right doesn't" → binary search applies.
 */
public class BinarySearch {

    // =========================================================
    // TOPIC 43 — CLASSIC BINARY SEARCH
    // =========================================================

    /**
     * Standard binary search — find target in sorted array.
     * Time: O(log n)   Space: O(1)
     * Returns index of target, or -1 if not found.
     */
    static int binarySearch(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // (lo+hi)/2 can overflow for large ints
            if      (arr[mid] == target) return mid;
            else if (arr[mid] < target)  lo = mid + 1;
            else                         hi = mid - 1;
        }
        return -1;
    }

    /** Recursive version — same logic, O(log n) time O(log n) space */
    static int binarySearchRecursive(int[] arr, int target, int lo, int hi) {
        if (lo > hi) return -1;
        int mid = lo + (hi - lo) / 2;
        if      (arr[mid] == target) return mid;
        else if (arr[mid] < target)  return binarySearchRecursive(arr, target, mid + 1, hi);
        else                         return binarySearchRecursive(arr, target, lo, mid - 1);
    }

    // =========================================================
    // TOPIC 44 — FIRST / LAST POSITION  (Lower/Upper Bound)
    // =========================================================

    /**
     * Find FIRST occurrence of target — LeetCode #34 (left part)
     *
     * Key: when arr[mid] == target, don't stop — keep searching LEFT.
     *      hi = mid - 1, but record mid as a candidate.
     *
     * This is equivalent to "lower_bound" in C++ STL.
     */
    static int firstPosition(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1, result = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] == target) {
                result = mid;   // candidate found
                hi = mid - 1;  // keep searching left
            } else if (arr[mid] < target) lo = mid + 1;
            else                          hi = mid - 1;
        }
        return result;
    }

    /**
     * Find LAST occurrence of target — LeetCode #34 (right part)
     *
     * Key: when arr[mid] == target, keep searching RIGHT.
     *      lo = mid + 1, record mid as candidate.
     *
     * Equivalent to "upper_bound - 1" in C++ STL.
     */
    static int lastPosition(int[] arr, int target) {
        int lo = 0, hi = arr.length - 1, result = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] == target) {
                result = mid;   // candidate found
                lo = mid + 1;  // keep searching right
            } else if (arr[mid] < target) lo = mid + 1;
            else                          hi = mid - 1;
        }
        return result;
    }

    /** LeetCode #34 — Find First and Last Position */
    static int[] searchRange(int[] nums, int target) {
        return new int[]{firstPosition(nums, target), lastPosition(nums, target)};
    }

    /**
     * Count occurrences of target in sorted array.
     * Use first/last position: count = last - first + 1
     */
    static int countOccurrences(int[] arr, int target) {
        int first = firstPosition(arr, target);
        if (first == -1) return 0;
        return lastPosition(arr, target) - first + 1;
    }

    /**
     * Lower bound: index of first element >= target
     * (insertion point to keep array sorted)
     */
    static int lowerBound(int[] arr, int target) {
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] < target) lo = mid + 1;
            else                   hi = mid;
        }
        return lo; // lo == hi == insertion point
    }

    /**
     * Upper bound: index of first element > target
     */
    static int upperBound(int[] arr, int target) {
        int lo = 0, hi = arr.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] <= target) lo = mid + 1;
            else                    hi = mid;
        }
        return lo;
    }

    // =========================================================
    // TOPIC 45 — SEARCH IN ROTATED SORTED ARRAY
    // =========================================================

    /**
     * LeetCode #33 — Search in Rotated Sorted Array
     * e.g. [4,5,6,7,0,1,2], target = 0 → index 4
     *
     * Key insight: even after rotation, one half is always sorted.
     *   Check which half is sorted, then decide which half to search.
     *
     * Step 1: if arr[lo..mid] is sorted (arr[lo] <= arr[mid])
     *           → if target in [arr[lo], arr[mid]) → go left
     *           → else go right
     * Step 2: else arr[mid..hi] is sorted
     *           → if target in (arr[mid], arr[hi]] → go right
     *           → else go left
     *
     * Time: O(log n)
     */
    static int searchRotated(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;

            if (nums[lo] <= nums[mid]) { // left half is sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else                                          lo = mid + 1;
            } else {                     // right half is sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else                                          hi = mid - 1;
            }
        }
        return -1;
    }

    /**
     * LeetCode #81 — Search in Rotated Array WITH DUPLICATES
     * Duplicates break the "one half is always sorted" guarantee.
     * Fix: when arr[lo] == arr[mid] == arr[hi], shrink both ends.
     * Time: O(log n) avg, O(n) worst (all duplicates)
     */
    static boolean searchRotatedWithDups(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return true;

            // Can't determine sorted half → shrink
            if (nums[lo] == nums[mid] && nums[mid] == nums[hi]) {
                lo++; hi--;
            } else if (nums[lo] <= nums[mid]) {
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else                                          lo = mid + 1;
            } else {
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else                                          hi = mid - 1;
            }
        }
        return false;
    }

    /**
     * LeetCode #153 — Find Minimum in Rotated Sorted Array
     * Key: minimum is always in the unsorted half.
     *   if arr[mid] > arr[hi] → min is in right half (lo = mid+1)
     *   else                  → min is in left half  (hi = mid)
     */
    static int findMinInRotated(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) lo = mid + 1; // min in right
            else                      hi = mid;      // mid could be min
        }
        return nums[lo];
    }

    // =========================================================
    // TOPIC 46 — BINARY SEARCH ON ANSWER
    // =========================================================

    /**
     * PATTERN: "Binary search on the answer space"
     *
     * When: you need to find the minimum/maximum value X such that
     *       some condition f(X) is true/false.
     * If f is monotone (once true, always true for larger X),
     * binary search over [lo_answer, hi_answer].
     *
     * Template:
     *   lo = min_possible_answer
     *   hi = max_possible_answer
     *   while (lo < hi):
     *       mid = lo + (hi - lo) / 2
     *       if canAchieve(mid): hi = mid      // mid might be the answer
     *       else:               lo = mid + 1  // mid too small
     *   return lo
     */

    /**
     * LeetCode #875 — Koko Eating Bananas
     * Find minimum eating speed k such that Koko can eat all piles in h hours.
     * f(k) = can eat all piles with speed k?  → monotone (if k works, k+1 works)
     * Search space: [1, max(piles)]
     */
    static int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = Arrays.stream(piles).max().getAsInt();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canFinish(piles, mid, h)) hi = mid; // mid might be optimal
            else                         lo = mid + 1;
        }
        return lo;
    }

    private static boolean canFinish(int[] piles, int speed, int h) {
        int hours = 0;
        for (int p : piles) hours += (p + speed - 1) / speed; // ceil division
        return hours <= h;
    }

    /**
     * LeetCode #1011 — Capacity to Ship Packages Within D Days
     * Find minimum capacity so all packages shipped in d days.
     * Search space: [max(weights), sum(weights)]
     */
    static int shipWithinDays(int[] weights, int days) {
        int lo = Arrays.stream(weights).max().getAsInt();
        int hi = Arrays.stream(weights).sum();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canShip(weights, mid, days)) hi = mid;
            else                             lo = mid + 1;
        }
        return lo;
    }

    private static boolean canShip(int[] weights, int capacity, int days) {
        int daysNeeded = 1, current = 0;
        for (int w : weights) {
            if (current + w > capacity) { daysNeeded++; current = 0; }
            current += w;
        }
        return daysNeeded <= days;
    }

    /**
     * LeetCode #410 — Split Array Largest Sum
     * Split array into k subarrays minimizing the largest sum.
     * Same as shipWithinDays with k=days.
     */
    static int splitArray(int[] nums, int k) {
        int lo = Arrays.stream(nums).max().getAsInt();
        int hi = Arrays.stream(nums).sum();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canSplit(nums, mid, k)) hi = mid;
            else                        lo = mid + 1;
        }
        return lo;
    }

    private static boolean canSplit(int[] nums, int maxSum, int k) {
        int parts = 1, current = 0;
        for (int n : nums) {
            if (current + n > maxSum) { parts++; current = 0; }
            current += n;
        }
        return parts <= k;
    }

    /**
     * LeetCode #2187 — Minimum Time to Complete Trips
     * Find minimum time t such that all buses complete >= totalTrips trips.
     * Search space: [1, min(time) * totalTrips]
     */
    static long minimumTime(int[] time, int totalTrips) {
        long lo = 1, hi = (long) Arrays.stream(time).min().getAsInt() * totalTrips;
        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;
            long trips = 0;
            for (int t : time) trips += mid / t;
            if (trips >= totalTrips) hi = mid;
            else                     lo = mid + 1;
        }
        return lo;
    }

    /**
     * LeetCode #69 — Sqrt(x) integer part
     * Binary search: find largest k where k*k <= x
     */
    static int mySqrt(int x) {
        if (x < 2) return x;
        int lo = 1, hi = x / 2;
        while (lo < hi) {
            int mid = lo + (hi - lo + 1) / 2; // upper mid to avoid infinite loop
            if ((long) mid * mid <= x) lo = mid;
            else                       hi = mid - 1;
        }
        return lo;
    }

    // =========================================================
    // TOPIC 47 — PEAK FINDING
    // =========================================================

    /**
     * LeetCode #162 — Find Peak Element
     * Peak: element greater than its neighbors (boundary treated as -∞).
     * Array may have multiple peaks — return ANY peak index.
     *
     * Key insight: if arr[mid] < arr[mid+1], peak is in right half.
     *              if arr[mid] < arr[mid-1], peak is in left half.
     *              (we can always move toward the higher neighbor)
     *
     * Time: O(log n)
     */
    static int findPeakElement(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < nums[mid + 1]) lo = mid + 1; // ascending → peak on right
            else                           hi = mid;      // descending → peak on left (or at mid)
        }
        return lo; // lo == hi == peak
    }

    /**
     * LeetCode #852 — Peak Index in a Mountain Array
     * Find the peak of a strict mountain: arr increases then decreases.
     * Same logic as findPeakElement.
     */
    static int peakIndexInMountainArray(int[] arr) {
        int lo = 0, hi = arr.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] < arr[mid + 1]) lo = mid + 1;
            else                         hi = mid;
        }
        return lo;
    }

    /**
     * LeetCode #1095 — Find in Mountain Array (2 binary searches)
     * Step 1: find peak index
     * Step 2: binary search ascending part [0..peak]
     * Step 3: if not found, binary search descending part [peak..n-1]
     * Time: O(log n)
     */
    static int findInMountainArray(int target, int[] arr) {
        int peak = peakIndexInMountainArray(arr);

        // Search ascending part
        int lo = 0, hi = peak;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if      (arr[mid] == target) return mid;
            else if (arr[mid] < target)  lo = mid + 1;
            else                         hi = mid - 1;
        }

        // Search descending part
        lo = peak + 1; hi = arr.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if      (arr[mid] == target) return mid;
            else if (arr[mid] > target)  lo = mid + 1; // reversed: larger = earlier
            else                         hi = mid - 1;
        }

        return -1;
    }

    // =========================================================
    // BONUS — SEARCH IN 2D MATRIX
    // =========================================================

    /**
     * LeetCode #74 — Search a 2D Matrix
     * Matrix is sorted row by row, first of each row > last of previous.
     * Treat as flattened 1D sorted array.
     * index → (index/cols, index%cols)
     */
    static boolean searchMatrix(int[][] matrix, int target) {
        int rows = matrix.length, cols = matrix[0].length;
        int lo = 0, hi = rows * cols - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int val = matrix[mid / cols][mid % cols];
            if      (val == target) return true;
            else if (val < target)  lo = mid + 1;
            else                    hi = mid - 1;
        }
        return false;
    }

    /**
     * LeetCode #240 — Search a 2D Matrix II
     * Each row and column is sorted independently (NOT row-concatenated).
     * Approach: start from top-right corner.
     *   if val == target → found
     *   if val < target  → move down  (eliminate column)
     *   if val > target  → move left  (eliminate row)
     * Time: O(m + n)  — not binary search but related pattern
     */
    static boolean searchMatrixII(int[][] matrix, int target) {
        int r = 0, c = matrix[0].length - 1;
        while (r < matrix.length && c >= 0) {
            if      (matrix[r][c] == target) return true;
            else if (matrix[r][c] < target)  r++;
            else                             c--;
        }
        return false;
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 43: Classic Binary Search ===");
        int[] arr = {1, 3, 5, 7, 9, 11, 13};
        System.out.println("Search 7:  idx=" + binarySearch(arr, 7));   // 3
        System.out.println("Search 1:  idx=" + binarySearch(arr, 1));   // 0
        System.out.println("Search 13: idx=" + binarySearch(arr, 13));  // 6
        System.out.println("Search 4:  idx=" + binarySearch(arr, 4));   // -1

        System.out.println("\n=== TOPIC 44: First / Last Position ===");
        int[] dup = {2, 4, 4, 4, 6, 8, 8};
        System.out.println("Array: " + Arrays.toString(dup));
        System.out.println("searchRange(4):   " + Arrays.toString(searchRange(dup, 4)));  // [1,3]
        System.out.println("searchRange(8):   " + Arrays.toString(searchRange(dup, 8)));  // [5,6]
        System.out.println("searchRange(5):   " + Arrays.toString(searchRange(dup, 5)));  // [-1,-1]
        System.out.println("countOccurrences(4): " + countOccurrences(dup, 4)); // 3
        System.out.println("lowerBound(4): " + lowerBound(dup, 4)); // 1
        System.out.println("upperBound(4): " + upperBound(dup, 4)); // 4

        System.out.println("\n=== TOPIC 45: Rotated Array ===");
        int[] rot = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("Array: " + Arrays.toString(rot));
        System.out.println("search 0:  idx=" + searchRotated(rot, 0)); // 4
        System.out.println("search 3:  idx=" + searchRotated(rot, 3)); // -1
        System.out.println("findMin:   " + findMinInRotated(rot));      // 0

        int[] dups = {2, 5, 6, 0, 0, 1, 2};
        System.out.println("With dups, search 0: " + searchRotatedWithDups(dups, 0)); // true
        System.out.println("With dups, search 3: " + searchRotatedWithDups(dups, 3)); // false

        System.out.println("\n=== TOPIC 46: Binary Search on Answer ===");
        System.out.println("#875 Koko piles=[3,6,7,11] h=8: speed="
                + minEatingSpeed(new int[]{3,6,7,11}, 8));       // 4
        System.out.println("#1011 Ship [1,2,3,4,5,6,7,8,9,10] days=5: capacity="
                + shipWithinDays(new int[]{1,2,3,4,5,6,7,8,9,10}, 5)); // 15
        System.out.println("#410 Split [7,2,5,10,8] k=2: "
                + splitArray(new int[]{7,2,5,10,8}, 2));          // 18
        System.out.println("#69 sqrt(8)=" + mySqrt(8));           // 2
        System.out.println("#69 sqrt(9)=" + mySqrt(9));           // 3

        System.out.println("\n=== TOPIC 47: Peak Finding ===");
        System.out.println("#162 peak in [1,2,3,1]: idx=" + findPeakElement(new int[]{1,2,3,1})); // 2
        System.out.println("#162 peak in [1,2,1,3,5,6,4]: idx="
                + findPeakElement(new int[]{1,2,1,3,5,6,4}));     // 5
        System.out.println("#852 mountain peak [0,1,0]: idx="
                + peakIndexInMountainArray(new int[]{0,1,0}));     // 1
        System.out.println("#1095 find 3 in [1,2,3,4,5,3,1]: idx="
                + findInMountainArray(3, new int[]{1,2,3,4,5,3,1})); // 2

        System.out.println("\n=== BONUS: 2D Matrix ===");
        int[][] mat = {{1,3,5,7},{10,11,16,20},{23,30,34,60}};
        System.out.println("#74 search 3:  " + searchMatrix(mat, 3));   // true
        System.out.println("#74 search 13: " + searchMatrix(mat, 13));  // false

        int[][] mat2 = {{1,4,7,11},{2,5,8,12},{3,6,9,16},{10,13,14,17}};
        System.out.println("#240 search 5:  " + searchMatrixII(mat2, 5));  // true
        System.out.println("#240 search 20: " + searchMatrixII(mat2, 20)); // false
    }
}
