package phase1_foundations.arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * PHASE 1.2 — Sliding Window Technique
 *
 * Ý tưởng: Duy trì một "cửa sổ" [left, right] trượt qua mảng
 *          → Tránh tính lại toàn bộ từ đầu mỗi lần
 *          → Giảm O(n²) hoặc O(n³) xuống O(n)
 *
 * 2 dạng:
 *   Fixed window  : kích thước cửa sổ cố định (k)
 *   Dynamic window: kích thước thay đổi theo điều kiện
 */
public class SlidingWindow {

    // =========================================================
    // DẠNG 1: Fixed Window (kích thước k cố định)
    // =========================================================

    /**
     * Bài: Maximum Sum Subarray of Size K
     * Tìm tổng lớn nhất của k phần tử liên tiếp
     *
     * Brute force O(n×k): tính tổng mỗi cửa sổ từ đầu
     * Sliding window O(n): trượt cửa sổ, cộng phần mới - trừ phần cũ
     */
    static int maxSumFixedWindow(int[] arr, int k) {
        // Tính tổng cửa sổ đầu tiên
        int windowSum = 0;
        for (int i = 0; i < k; i++) windowSum += arr[i];

        int maxSum = windowSum;
        // Trượt cửa sổ: thêm arr[right], bỏ arr[right-k]
        for (int right = k; right < arr.length; right++) {
            windowSum += arr[right] - arr[right - k]; // O(1) mỗi bước!
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum;
    }

    /**
     * Bài: Average of Subarrays of Size K
     */
    static double[] avgOfSubarrays(int[] arr, int k) {
        double[] result = new double[arr.length - k + 1];
        double windowSum = 0;
        for (int i = 0; i < k; i++) windowSum += arr[i];
        result[0] = windowSum / k;
        for (int i = k; i < arr.length; i++) {
            windowSum += arr[i] - arr[i - k];
            result[i - k + 1] = windowSum / k;
        }
        return result;
    }

    // =========================================================
    // DẠNG 2: Dynamic Window (kích thước thay đổi)
    // Template:
    //   right duyệt từ trái sang phải (mở rộng cửa sổ)
    //   khi vi phạm điều kiện → tăng left (thu hẹp cửa sổ)
    // =========================================================

    /**
     * Bài: Longest Substring Without Repeating Characters (LeetCode #3)
     * Tìm chuỗi con dài nhất không có ký tự lặp
     * "abcabcbb" → 3 ("abc")
     *
     * Dùng Set để track ký tự trong cửa sổ hiện tại
     * Time: O(n) | Space: O(k) với k là số ký tự unique
     */
    static int lengthOfLongestSubstring(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            // Thu hẹp cửa sổ cho đến khi không còn duplicate
            while (window.contains(c)) {
                window.remove(s.charAt(left));
                left++;
            }
            window.add(c);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    /**
     * Bài: Minimum Size Subarray Sum (LeetCode #209)
     * Tìm subarray ngắn nhất có tổng >= target
     * [2,3,1,2,4,3], target=7 → 2 (subarray [4,3])
     *
     * Time: O(n) — mỗi phần tử vào/ra cửa sổ đúng 1 lần
     */
    static int minSubarrayLen(int target, int[] nums) {
        int left = 0, sum = 0, minLen = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right]; // mở rộng cửa sổ
            while (sum >= target) { // điều kiện thỏa → thu hẹp để tìm min
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left++];
            }
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    /**
     * Bài: Longest Substring with At Most K Distinct Characters
     * "eceba", k=2 → 3 ("ece")
     *
     * Dùng Map để đếm tần suất ký tự trong cửa sổ
     */
    static int longestSubstringKDistinct(String s, int k) {
        Map<Character, Integer> freq = new HashMap<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            freq.merge(c, 1, Integer::sum); // thêm ký tự mới

            // Vi phạm: hơn k ký tự distinct → thu hẹp
            while (freq.size() > k) {
                char leftChar = s.charAt(left++);
                freq.merge(leftChar, -1, Integer::sum);
                if (freq.get(leftChar) == 0) freq.remove(leftChar);
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    /**
     * Bài: Permutation in String (LeetCode #567)
     * Kiểm tra s2 có chứa permutation của s1 không
     * s1="ab", s2="eidbaooo" → true ("ba" là permutation của "ab")
     *
     * Kỹ thuật: so sánh frequency array, window size = s1.length()
     */
    static boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        int[] freq1 = new int[26], freqWindow = new int[26];
        int k = s1.length();

        // Build frequency của s1 và cửa sổ đầu tiên
        for (int i = 0; i < k; i++) {
            freq1[s1.charAt(i) - 'a']++;
            freqWindow[s2.charAt(i) - 'a']++;
        }
        if (java.util.Arrays.equals(freq1, freqWindow)) return true;

        // Trượt cửa sổ
        for (int right = k; right < s2.length(); right++) {
            freqWindow[s2.charAt(right) - 'a']++;          // thêm phải
            freqWindow[s2.charAt(right - k) - 'a']--;      // bỏ trái
            if (java.util.Arrays.equals(freq1, freqWindow)) return true;
        }
        return false;
    }

    // =========================================================
    // TEMPLATE tổng quát — ghi nhớ để áp dụng
    // =========================================================
    /*
     * int left = 0;
     * for (int right = 0; right < n; right++) {
     *     // 1. Thêm arr[right] vào cửa sổ
     *
     *     // 2. Thu hẹp khi vi phạm điều kiện
     *     while (điều kiện vi phạm) {
     *         // Bỏ arr[left] ra khỏi cửa sổ
     *         left++;
     *     }
     *
     *     // 3. Cập nhật kết quả
     *     result = Math.max/min(result, right - left + 1);
     * }
     */

    public static void main(String[] args) {
        System.out.println("=== Sliding Window ===\n");

        // Fixed window
        int[] arr = {2, 1, 5, 1, 3, 2};
        System.out.println("maxSumFixedWindow(k=3): " + maxSumFixedWindow(arr, 3)); // 9

        double[] avgs = avgOfSubarrays(new int[]{1, 3, 2, 6, -1, 4, 1, 8, 2}, 5);
        System.out.print("avgOfSubarrays(k=5): ");
        for (double v : avgs) System.out.printf("%.1f ", v);
        System.out.println();

        // Dynamic window
        System.out.println("\nlengthOfLongestSubstring:");
        System.out.println("  'abcabcbb' → " + lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println("  'bbbbb'    → " + lengthOfLongestSubstring("bbbbb"));    // 1
        System.out.println("  'pwwkew'   → " + lengthOfLongestSubstring("pwwkew"));   // 3

        System.out.println("\nminSubarrayLen(target=7):");
        System.out.println("  [2,3,1,2,4,3] → " + minSubarrayLen(7, new int[]{2,3,1,2,4,3})); // 2

        System.out.println("\nlongestSubstringKDistinct(k=2):");
        System.out.println("  'eceba' → " + longestSubstringKDistinct("eceba", 2));   // 3
        System.out.println("  'aa'    → " + longestSubstringKDistinct("aa", 1));      // 2

        System.out.println("\ncheckInclusion:");
        System.out.println("  s1='ab', s2='eidbaooo' → " + checkInclusion("ab", "eidbaooo")); // true
        System.out.println("  s1='ab', s2='eidboaoo' → " + checkInclusion("ab", "eidboaoo")); // false
    }
}
