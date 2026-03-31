package leetcode_practice;

import java.util.*;

/**
 * LEETCODE PRACTICE -- SESSION 02
 * Pattern: BINARY SEARCH
 *
 * Mental model:
 *   - Binary search is NOT just for sorted arrays.
 *   - Use whenever you can define a monotonic predicate f(x):
 *     f(x) = false, false, ..., false, TRUE, TRUE, ..., TRUE
 *   - Find the FIRST TRUE (lower_bound) or LAST FALSE (upper_bound).
 *
 * Template (find first position where predicate is true):
 *   lo = 0, hi = n (or max answer)
 *   while (lo < hi):
 *       mid = lo + (hi - lo) / 2   // avoid overflow
 *       if predicate(mid): hi = mid
 *       else:              lo = mid + 1
 *   return lo
 *
 * Problems:
 *   Easy:   #704 Binary Search, #35 Search Insert Position
 *   Medium: #33  Search in Rotated Sorted Array
 *           #81  Search in Rotated Array II (with duplicates)
 *           #153 Find Minimum in Rotated Sorted Array
 *           #875 Koko Eating Bananas
 *           #1011 Capacity to Ship Packages
 *           #74  Search a 2D Matrix
 *   Hard:   #410 Split Array Largest Sum
 *           #4   Median of Two Sorted Arrays
 */
public class Session02_BinarySearch {

    public static void main(String[] args) {
        System.out.println("=== SESSION 02: BINARY SEARCH ===\n");
        testEasy();
        testMedium();
        testHard();
    }

    // -------------------------------------------------------------------------
    // EASY
    // -------------------------------------------------------------------------

    // #704 Binary Search -- classic
    // Return index of target, or -1.
    // Key: use lo + (hi-lo)/2 to avoid integer overflow.
    static int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if      (nums[mid] == target) return mid;
            else if (nums[mid] <  target) lo = mid + 1;
            else                          hi = mid - 1;
        }
        return -1;
    }

    // #35 Search Insert Position
    // Return index where target is, or where it WOULD be inserted.
    // = lower_bound: first index where nums[i] >= target
    static int searchInsert(int[] nums, int target) {
        int lo = 0, hi = nums.length; // hi = n (target may go after all)
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < target) lo = mid + 1;
            else                    hi = mid;      // first >= target
        }
        return lo;
    }

    static void testEasy() {
        System.out.println("--- EASY ---");
        System.out.println("#704 search([-1,0,3,5,9,12], 9) = "
            + search(new int[]{-1,0,3,5,9,12}, 9));   // 4
        System.out.println("#704 search([-1,0,3,5,9,12], 2) = "
            + search(new int[]{-1,0,3,5,9,12}, 2));   // -1

        System.out.println("#35  searchInsert([1,3,5,6], 5) = "
            + searchInsert(new int[]{1,3,5,6}, 5));   // 2
        System.out.println("#35  searchInsert([1,3,5,6], 2) = "
            + searchInsert(new int[]{1,3,5,6}, 2));   // 1
        System.out.println("#35  searchInsert([1,3,5,6], 7) = "
            + searchInsert(new int[]{1,3,5,6}, 7));   // 4
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MEDIUM
    // -------------------------------------------------------------------------

    // #33 Search in Rotated Sorted Array (no duplicates)
    // Key insight: after splitting at mid, ONE half is always sorted.
    //   Identify which half is sorted, then check if target falls in it.
    // Brute: O(n) linear scan
    // Optimal: O(log n)
    static int searchRotated(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return mid;

            if (nums[lo] <= nums[mid]) {        // LEFT half is sorted
                if (nums[lo] <= target && target < nums[mid])
                    hi = mid - 1;               // target in left
                else
                    lo = mid + 1;               // target in right
            } else {                            // RIGHT half is sorted
                if (nums[mid] < target && target <= nums[hi])
                    lo = mid + 1;               // target in right
                else
                    hi = mid - 1;               // target in left
            }
        }
        return -1;
    }

    // #81 Search in Rotated Array II (WITH duplicates)
    // Problem: nums[lo] == nums[mid] => can't determine which half is sorted.
    // Fix: when nums[lo] == nums[mid] == nums[hi], just shrink both ends by 1.
    // Worst case O(n), average O(log n).
    static boolean searchRotatedII(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) return true;

            // Can't determine sorted half -- shrink both sides
            if (nums[lo] == nums[mid] && nums[mid] == nums[hi]) {
                lo++; hi--;
            } else if (nums[lo] <= nums[mid]) { // left sorted
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else                                          lo = mid + 1;
            } else {                            // right sorted
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else                                          hi = mid - 1;
            }
        }
        return false;
    }

    // #153 Find Minimum in Rotated Sorted Array (no duplicates)
    // Key: minimum is the only point where nums[mid] > nums[right].
    //   If nums[mid] < nums[hi]: min is in left half (including mid).
    //   If nums[mid] > nums[hi]: min is in right half (excluding mid).
    static int findMin(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < nums[hi]) hi = mid;      // min in [lo, mid]
            else                      lo = mid + 1;  // min in [mid+1, hi]
        }
        return nums[lo];
    }

    // #875 Koko Eating Bananas
    // Binary search on ANSWER (speed k).
    // Predicate: canFinish(k) = can eat all piles in h hours at speed k?
    // Monotonic: if canFinish(k) then canFinish(k+1).
    // Find MINIMUM k where canFinish(k) is true.
    static int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = Arrays.stream(piles).max().getAsInt();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canFinish(piles, h, mid)) hi = mid; // try smaller
            else                          lo = mid + 1;
        }
        return lo;
    }

    static boolean canFinish(int[] piles, int h, int speed) {
        int hours = 0;
        for (int pile : piles) hours += (pile + speed - 1) / speed; // ceil division
        return hours <= h;
    }

    // #1011 Capacity to Ship Packages Within D Days
    // Binary search on answer (ship capacity).
    // lo = max(weights) -- must carry heaviest single package
    // hi = sum(weights) -- carry everything in one day
    // Predicate: canShip(cap) = can ship all packages in d days at capacity cap?
    static int shipWithinDays(int[] weights, int days) {
        int lo = Arrays.stream(weights).max().getAsInt();
        int hi = Arrays.stream(weights).sum();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canShip(weights, days, mid)) hi = mid;
            else                             lo = mid + 1;
        }
        return lo;
    }

    static boolean canShip(int[] weights, int days, int cap) {
        int daysNeeded = 1, cur = 0;
        for (int w : weights) {
            if (cur + w > cap) { daysNeeded++; cur = 0; }
            cur += w;
        }
        return daysNeeded <= days;
    }

    // #74 Search a 2D Matrix
    // Matrix is sorted: each row sorted, first element of row > last of prev row.
    // Key: treat as 1D sorted array of size m*n.
    //   row = index / n,  col = index % n
    static boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int lo = 0, hi = m * n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int val = matrix[mid / n][mid % n];
            if      (val == target) return true;
            else if (val <  target) lo = mid + 1;
            else                    hi = mid - 1;
        }
        return false;
    }

    static void testMedium() {
        System.out.println("--- MEDIUM ---");

        System.out.println("#33  searchRotated([4,5,6,7,0,1,2], 0) = "
            + searchRotated(new int[]{4,5,6,7,0,1,2}, 0));  // 4
        System.out.println("#33  searchRotated([4,5,6,7,0,1,2], 3) = "
            + searchRotated(new int[]{4,5,6,7,0,1,2}, 3));  // -1
        System.out.println("#33  searchRotated([1], 0)             = "
            + searchRotated(new int[]{1}, 0));               // -1

        System.out.println("#81  searchRotatedII([2,5,6,0,0,1,2], 0) = "
            + searchRotatedII(new int[]{2,5,6,0,0,1,2}, 0));  // true
        System.out.println("#81  searchRotatedII([2,5,6,0,0,1,2], 3) = "
            + searchRotatedII(new int[]{2,5,6,0,0,1,2}, 3));  // false

        System.out.println("#153 findMin([3,4,5,1,2])   = "
            + findMin(new int[]{3,4,5,1,2}));    // 1
        System.out.println("#153 findMin([4,5,6,7,0,1,2])= "
            + findMin(new int[]{4,5,6,7,0,1,2})); // 0
        System.out.println("#153 findMin([11,13,15,17]) = "
            + findMin(new int[]{11,13,15,17}));   // 11

        System.out.println("#875 minEatingSpeed([3,6,7,11], 8) = "
            + minEatingSpeed(new int[]{3,6,7,11}, 8));  // 4
        System.out.println("#875 minEatingSpeed([30,11,23,4,20], 5) = "
            + minEatingSpeed(new int[]{30,11,23,4,20}, 5)); // 30
        System.out.println("#875 minEatingSpeed([30,11,23,4,20], 6) = "
            + minEatingSpeed(new int[]{30,11,23,4,20}, 6)); // 23

        System.out.println("#1011 shipWithinDays([1,2,3,4,5,6,7,8,9,10], 5) = "
            + shipWithinDays(new int[]{1,2,3,4,5,6,7,8,9,10}, 5)); // 15
        System.out.println("#1011 shipWithinDays([3,2,2,4,1,4], 3)          = "
            + shipWithinDays(new int[]{3,2,2,4,1,4}, 3));           // 6

        System.out.println("#74  searchMatrix([[1,3,5,7],[10,11,16,20],[23,30,34,60]], 3)  = "
            + searchMatrix(new int[][]{{1,3,5,7},{10,11,16,20},{23,30,34,60}}, 3));  // true
        System.out.println("#74  searchMatrix([[1,3,5,7],[10,11,16,20],[23,30,34,60]], 13) = "
            + searchMatrix(new int[][]{{1,3,5,7},{10,11,16,20},{23,30,34,60}}, 13)); // false
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // HARD
    // -------------------------------------------------------------------------

    // #410 Split Array Largest Sum
    // Split nums into k subarrays, minimize the largest subarray sum.
    // This is the SAME structure as #1011 (capacity = largest sum, days = k).
    // Binary search on answer: lo = max(nums), hi = sum(nums)
    // Predicate: canSplit(maxSum) = can split into <= k parts all <= maxSum?
    static int splitArray(int[] nums, int k) {
        int lo = Arrays.stream(nums).max().getAsInt();
        int hi = Arrays.stream(nums).sum();
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canSplit(nums, k, mid)) hi = mid;
            else                        lo = mid + 1;
        }
        return lo;
    }

    static boolean canSplit(int[] nums, int k, int maxSum) {
        int parts = 1, cur = 0;
        for (int n : nums) {
            if (cur + n > maxSum) { parts++; cur = 0; }
            cur += n;
        }
        return parts <= k;
    }

    // #4 Median of Two Sorted Arrays -- O(log(min(m,n)))
    // Key idea: binary search on the partition of the SMALLER array.
    //   partition: split A into left A[0..i-1] and right A[i..m-1]
    //              split B into left B[0..j-1] and right B[j..n-1]
    //   such that left half has exactly (m+n+1)/2 elements total.
    //   Valid partition: A[i-1] <= B[j] AND B[j-1] <= A[i]
    //   Then median = average of max(left) and min(right).
    static double findMedianSortedArrays(int[] A, int[] B) {
        // Ensure A is the smaller array
        if (A.length > B.length) return findMedianSortedArrays(B, A);
        int m = A.length, n = B.length;
        int lo = 0, hi = m;
        while (lo <= hi) {
            int i = lo + (hi - lo) / 2;   // partition A: i elements on left
            int j = (m + n + 1) / 2 - i;  // partition B: j elements on left

            int maxLeftA  = (i == 0) ? Integer.MIN_VALUE : A[i - 1];
            int minRightA = (i == m) ? Integer.MAX_VALUE : A[i];
            int maxLeftB  = (j == 0) ? Integer.MIN_VALUE : B[j - 1];
            int minRightB = (j == n) ? Integer.MAX_VALUE : B[j];

            if (maxLeftA <= minRightB && maxLeftB <= minRightA) {
                // Valid partition found
                if ((m + n) % 2 == 1)
                    return Math.max(maxLeftA, maxLeftB);
                else
                    return (Math.max(maxLeftA, maxLeftB) + Math.min(minRightA, minRightB)) / 2.0;
            } else if (maxLeftA > minRightB) {
                hi = i - 1; // move partition left in A
            } else {
                lo = i + 1; // move partition right in A
            }
        }
        return 0.0;
    }

    static void testHard() {
        System.out.println("--- HARD ---");

        System.out.println("#410 splitArray([7,2,5,10,8], 2)    = "
            + splitArray(new int[]{7,2,5,10,8}, 2));    // 18
        System.out.println("#410 splitArray([1,2,3,4,5], 2)     = "
            + splitArray(new int[]{1,2,3,4,5}, 2));     // 9
        System.out.println("#410 splitArray([1,4,4], 3)         = "
            + splitArray(new int[]{1,4,4}, 3));          // 4

        System.out.println("#4   findMedianSortedArrays([1,3],[2])       = "
            + findMedianSortedArrays(new int[]{1,3}, new int[]{2}));         // 2.0
        System.out.println("#4   findMedianSortedArrays([1,2],[3,4])     = "
            + findMedianSortedArrays(new int[]{1,2}, new int[]{3,4}));       // 2.5
        System.out.println("#4   findMedianSortedArrays([0,0],[0,0])     = "
            + findMedianSortedArrays(new int[]{0,0}, new int[]{0,0}));       // 0.0
        System.out.println("#4   findMedianSortedArrays([],[1])          = "
            + findMedianSortedArrays(new int[]{}, new int[]{1}));            // 1.0
        System.out.println("#4   findMedianSortedArrays([2],[])          = "
            + findMedianSortedArrays(new int[]{2}, new int[]{}));            // 2.0

        System.out.println();
        System.out.println("PATTERN SUMMARY -- Binary Search:");
        System.out.println("  Classic          : lo<=hi, return index of exact match");
        System.out.println("  Lower bound      : lo<hi, hi=mid, return first TRUE position");
        System.out.println("  Rotated array    : identify sorted half, check if target in it");
        System.out.println("  BS on answer     : lo=min_possible, hi=max_possible, minimize/maximize");
        System.out.println("  2D matrix        : index = mid/n row, mid%n col");
        System.out.println("  Partition (hard) : binary search on partition point, balance two arrays");
        System.out.println();
        System.out.println("Ceil division trick:  (x + divisor - 1) / divisor");
        System.out.println("Overflow-safe mid:    lo + (hi - lo) / 2");
        System.out.println("Duplicate bs:         when nums[lo]==nums[mid]==nums[hi] => lo++, hi--");
    }
}
