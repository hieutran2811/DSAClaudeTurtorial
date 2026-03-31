package leetcode_practice;

import java.util.*;

/**
 * LEETCODE PRACTICE -- SESSION 01
 * Pattern: TWO POINTERS & SLIDING WINDOW
 *
 * Strategy for each problem:
 *   1. Read problem, identify pattern
 *   2. Write brute force approach + complexity
 *   3. Optimize with the pattern
 *   4. Code clean solution
 *   5. Test edge cases
 *
 * Problems:
 *   Easy:   #167 Two Sum II, #283 Move Zeroes, #344 Reverse String
 *   Medium: #3   Longest Substring Without Repeating, #11 Container With Most Water
 *           #15  3Sum, #438 Find All Anagrams, #567 Permutation in String
 *   Hard:   #76  Minimum Window Substring, #42 Trapping Rain Water
 */
public class Session01_TwoPointerSliding {

    public static void main(String[] args) {
        System.out.println("=== SESSION 01: TWO POINTERS & SLIDING WINDOW ===\n");
        testEasy();
        testMedium();
        testHard();
    }

    // -------------------------------------------------------------------------
    // EASY
    // -------------------------------------------------------------------------

    // #167 Two Sum II -- Input array is sorted
    // Pattern: Opposite-end two pointers
    // Brute: O(n^2) -- try all pairs
    // Optimal: O(n) -- left=0, right=n-1, move inward based on sum vs target
    static int[] twoSumII(int[] numbers, int target) {
        int l = 0, r = numbers.length - 1;
        while (l < r) {
            int sum = numbers[l] + numbers[r];
            if (sum == target) return new int[]{l + 1, r + 1}; // 1-indexed
            if (sum < target) l++;
            else              r--;
        }
        return new int[]{-1, -1};
    }

    // #283 Move Zeroes -- move all 0s to end, keep relative order of non-zeros
    // Pattern: slow/fast pointer (partition)
    // Key: slow = next position for non-zero, fast scans ahead
    static void moveZeroes(int[] nums) {
        int slow = 0; // next write position for non-zero
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) nums[slow++] = nums[fast];
        }
        while (slow < nums.length) nums[slow++] = 0; // fill rest with 0
    }

    // #344 Reverse String -- in-place
    // Pattern: Opposite-end two pointers, swap until they meet
    static void reverseString(char[] s) {
        int l = 0, r = s.length - 1;
        while (l < r) { char tmp = s[l]; s[l++] = s[r]; s[r--] = tmp; }
    }

    static void testEasy() {
        System.out.println("--- EASY ---");

        // #167
        System.out.println("#167 twoSumII([2,7,11,15], 9) = "
            + Arrays.toString(twoSumII(new int[]{2,7,11,15}, 9)));   // [1,2]
        System.out.println("#167 twoSumII([2,3,4], 6)      = "
            + Arrays.toString(twoSumII(new int[]{2,3,4}, 6)));        // [1,3]

        // #283
        int[] arr = {0,1,0,3,12};
        moveZeroes(arr);
        System.out.println("#283 moveZeroes([0,1,0,3,12])  = " + Arrays.toString(arr)); // [1,3,12,0,0]

        // #344
        char[] s = {'h','e','l','l','o'};
        reverseString(s);
        System.out.println("#344 reverseString(\"hello\")  = " + new String(s)); // olleh
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MEDIUM
    // -------------------------------------------------------------------------

    // #3 Longest Substring Without Repeating Characters
    // Pattern: Dynamic sliding window + HashMap/Set
    // Brute: O(n^2) -- check all substrings
    // Optimal: O(n) -- expand right, when duplicate found shrink left
    //   Key: store last seen index of each char to jump left pointer directly
    static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastIndex = new HashMap<>();
        int max = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastIndex.containsKey(c) && lastIndex.get(c) >= left)
                left = lastIndex.get(c) + 1; // jump past duplicate
            lastIndex.put(c, right);
            max = Math.max(max, right - left + 1);
        }
        return max;
    }

    // #11 Container With Most Water
    // Pattern: Opposite-end two pointers
    // Brute: O(n^2) -- all pairs
    // Optimal: O(n) -- always move the SHORTER side inward
    //   WHY: moving the taller side can only decrease or keep width same,
    //        the shorter side limits height, so moving it is the only hope.
    static int maxArea(int[] height) {
        int l = 0, r = height.length - 1, max = 0;
        while (l < r) {
            max = Math.max(max, Math.min(height[l], height[r]) * (r - l));
            if (height[l] < height[r]) l++;
            else                        r--;
        }
        return max;
    }

    // #15 3Sum -- find all unique triplets that sum to 0
    // Pattern: Sort + fix one element + two-pointer on remainder
    // Brute: O(n^3) -- three nested loops
    // Optimal: O(n^2) -- sort, for each i use two pointers on i+1..n-1
    //   Key: skip duplicates carefully at all three levels
    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i-1]) continue; // skip dup i
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l+1]) l++; // skip dup l
                    while (l < r && nums[r] == nums[r-1]) r--; // skip dup r
                    l++; r--;
                } else if (sum < 0) l++;
                else                r--;
            }
        }
        return result;
    }

    // #438 Find All Anagrams in a String
    // Pattern: Fixed sliding window + frequency count
    // Brute: O(n*m*logm) -- check each window
    // Optimal: O(n) -- maintain char freq count, track how many chars match
    static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;
        int[] pCount = new int[26], wCount = new int[26];
        for (char c : p.toCharArray()) pCount[c - 'a']++;

        int m = p.length();
        for (int i = 0; i < s.length(); i++) {
            wCount[s.charAt(i) - 'a']++;          // add right char
            if (i >= m) wCount[s.charAt(i - m) - 'a']--; // remove left char
            if (Arrays.equals(pCount, wCount)) result.add(i - m + 1);
        }
        return result;
    }

    // #567 Permutation in String -- does s2 contain a permutation of s1?
    // Same pattern as #438 but return boolean
    // Optimize: track a `matches` counter instead of Arrays.equals each step
    static boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;
        int[] count = new int[26];
        for (char c : s1.toCharArray()) count[c - 'a']++;
        // need = number of distinct chars in s1 that must match
        int need = 0;
        for (int f : count) if (f > 0) need++;
        int[] window = new int[26];
        int matched = 0, m = s1.length();
        for (int i = 0; i < s2.length(); i++) {
            int r = s2.charAt(i) - 'a';
            window[r]++;
            if (window[r] == count[r]) matched++; // exactly satisfied
            if (i >= m) {
                int l = s2.charAt(i - m) - 'a';
                if (window[l] == count[l]) matched--; // about to break match
                window[l]--;
            }
            if (matched == need) return true;
        }
        return false;
    }

    static void testMedium() {
        System.out.println("--- MEDIUM ---");

        System.out.println("#3  lengthOfLongestSubstring(\"abcabcbb\") = "
            + lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println("#3  lengthOfLongestSubstring(\"pwwkew\")   = "
            + lengthOfLongestSubstring("pwwkew"));   // 3
        System.out.println("#3  lengthOfLongestSubstring(\"bbbbb\")    = "
            + lengthOfLongestSubstring("bbbbb"));    // 1

        System.out.println("#11 maxArea([1,8,6,2,5,4,8,3,7]) = "
            + maxArea(new int[]{1,8,6,2,5,4,8,3,7})); // 49

        System.out.println("#15 threeSum([-1,0,1,2,-1,-4]) = "
            + threeSum(new int[]{-1,0,1,2,-1,-4})); // [[-1,-1,2],[-1,0,1]]

        System.out.println("#438 findAnagrams(\"cbaebabacd\",\"abc\") = "
            + findAnagrams("cbaebabacd", "abc")); // [0, 6]

        System.out.println("#567 checkInclusion(\"ab\",\"eidbaooo\") = "
            + checkInclusion("ab", "eidbaooo"));  // true
        System.out.println("#567 checkInclusion(\"ab\",\"eidboaoo\") = "
            + checkInclusion("ab", "eidboaoo"));  // false
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // HARD
    // -------------------------------------------------------------------------

    // #76 Minimum Window Substring
    // Find smallest window in s containing all chars of t.
    // Pattern: Dynamic sliding window + freq count + `have/need` counter
    // Brute: O(n^2) -- all substrings, check each
    // Optimal: O(n+m) -- expand right until valid, shrink left to minimize
    //   Key: track `have` (chars matching required freq) vs `need` (distinct chars in t)
    static String minWindow(String s, String t) {
        if (s.isEmpty() || t.isEmpty()) return "";
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

        int have = 0, required = need.size();
        Map<Character, Integer> window = new HashMap<>();
        int[] best = {-1, 0, 0}; // [length, left, right]
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window.merge(c, 1, Integer::sum);
            if (need.containsKey(c) && window.get(c).equals(need.get(c))) have++;

            while (have == required) { // valid window -- try to shrink
                if (best[0] == -1 || right - left + 1 < best[0]) {
                    best[0] = right - left + 1;
                    best[1] = left; best[2] = right;
                }
                char lc = s.charAt(left++);
                window.merge(lc, -1, Integer::sum);
                if (need.containsKey(lc) && window.get(lc) < need.get(lc)) have--;
            }
        }
        return best[0] == -1 ? "" : s.substring(best[1], best[2] + 1);
    }

    // #42 Trapping Rain Water
    // Pattern: Opposite-end two pointers (space O(1))
    // Brute: O(n^2) -- for each position, find max left and max right
    // Better: O(n) time O(n) space with prefix max arrays
    // Optimal: O(n) time O(1) space -- two pointers
    //   Key insight: water at i = min(maxLeft, maxRight) - height[i]
    //   We process from whichever side has the SMALLER max, because
    //   we already know the constraint on that side.
    static int trap(int[] height) {
        int l = 0, r = height.length - 1;
        int maxL = 0, maxR = 0, water = 0;
        while (l < r) {
            if (height[l] <= height[r]) {
                // Left side is the bottleneck
                maxL = Math.max(maxL, height[l]);
                water += maxL - height[l]; // maxL >= height[l] always
                l++;
            } else {
                // Right side is the bottleneck
                maxR = Math.max(maxR, height[r]);
                water += maxR - height[r];
                r--;
            }
        }
        return water;
    }

    static void testHard() {
        System.out.println("--- HARD ---");

        System.out.println("#76 minWindow(\"ADOBECODEBANC\", \"ABC\") = \""
            + minWindow("ADOBECODEBANC", "ABC") + "\""); // "BANC"
        System.out.println("#76 minWindow(\"a\", \"a\")               = \""
            + minWindow("a", "a") + "\"");               // "a"
        System.out.println("#76 minWindow(\"a\", \"aa\")              = \""
            + minWindow("a", "aa") + "\"");              // ""

        System.out.println("#42 trap([0,1,0,2,1,0,1,3,2,1,2,1]) = "
            + trap(new int[]{0,1,0,2,1,0,1,3,2,1,2,1})); // 6
        System.out.println("#42 trap([4,2,0,3,2,5])              = "
            + trap(new int[]{4,2,0,3,2,5}));              // 9

        System.out.println();
        System.out.println("PATTERN SUMMARY -- Two Pointers & Sliding Window:");
        System.out.println("  Opposite ends   : sorted array, minimize/maximize, container");
        System.out.println("  Slow/fast       : partition, remove duplicates, cycle detect");
        System.out.println("  Fixed window    : anagram, substring of length k");
        System.out.println("  Dynamic window  : longest/shortest with constraint, min window");
        System.out.println("  have/need trick : count matched freq chars to avoid O(26) check");
    }
}
