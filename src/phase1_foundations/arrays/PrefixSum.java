package phase1_foundations.arrays;

import java.util.HashMap;
import java.util.Map;

/**
 * PHASE 1.2 — Prefix Sum & String Manipulation
 *
 * PREFIX SUM: Tính trước tổng tích lũy
 * → Query range sum O(n²) → O(1) sau O(n) preprocessing
 *
 * prefix[i] = arr[0] + arr[1] + ... + arr[i-1]
 * sum(left, right) = prefix[right+1] - prefix[left]
 */
public class PrefixSum {

    // =========================================================
    // 1. Prefix Sum cơ bản
    // =========================================================

    static int[] buildPrefix(int[] arr) {
        int[] prefix = new int[arr.length + 1]; // prefix[0] = 0
        for (int i = 0; i < arr.length; i++) {
            prefix[i + 1] = prefix[i] + arr[i];
        }
        return prefix;
        // prefix = [0, arr[0], arr[0]+arr[1], ...]
    }

    // Query: tổng từ index left đến right (inclusive) — O(1)
    static int rangeSum(int[] prefix, int left, int right) {
        return prefix[right + 1] - prefix[left];
    }

    /**
     * Bài: Range Sum Query (LeetCode #303)
     * Cho nhiều query (left, right), trả về tổng nhanh
     */
    static class NumArray {
        private int[] prefix;

        NumArray(int[] nums) {
            prefix = buildPrefix(nums);
        }

        int sumRange(int left, int right) {
            return rangeSum(prefix, left, right); // O(1)
        }
    }

    // =========================================================
    // 2. Subarray Sum Equals K (LeetCode #560) — Quan trọng!
    // =========================================================
    /**
     * Đếm số subarray có tổng = k
     * [1,1,1], k=2 → 2 ([1,1] bắt đầu từ 0 và từ 1)
     *
     * Kỹ thuật: prefix sum + HashMap
     * prefix[j] - prefix[i] = k  →  prefix[i] = prefix[j] - k
     * Với mỗi j, đếm số prefix[i] đã gặp bằng prefix[j] - k
     *
     * Time: O(n) | Space: O(n)
     */
    static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1); // tổng từ đầu = k là 1 cách
        int count = 0, sum = 0;

        for (int x : nums) {
            sum += x;
            // Tìm số prefix đã gặp mà = sum - k
            count += prefixCount.getOrDefault(sum - k, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // =========================================================
    // 3. Product Array Except Self (LeetCode #238)
    // =========================================================
    /**
     * output[i] = tích tất cả phần tử NGOẠI TRỪ nums[i]
     * [1,2,3,4] → [24,12,8,6]
     * KHÔNG dùng phép chia! Time O(n) Space O(1) (không tính output)
     *
     * Kỹ thuật: prefix product từ trái + suffix product từ phải
     */
    static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] output = new int[n];

        // Pass 1: output[i] = tích tất cả phần tử BÊN TRÁI i
        output[0] = 1;
        for (int i = 1; i < n; i++) {
            output[i] = output[i - 1] * nums[i - 1];
        }

        // Pass 2: nhân thêm tích phần tử BÊN PHẢI i (dùng biến suffix)
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            output[i] *= suffix;
            suffix *= nums[i];
        }
        return output;
    }

    // =========================================================
    // 4. Prefix Sum 2D — cho matrix
    // =========================================================
    static int[][] build2DPrefix(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] prefix = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = matrix[i-1][j-1]
                        + prefix[i-1][j]
                        + prefix[i][j-1]
                        - prefix[i-1][j-1]; // tránh tính 2 lần
            }
        }
        return prefix;
    }

    // Query: tổng hình chữ nhật từ (r1,c1) đến (r2,c2) — O(1)
    static int queryMatrix(int[][] prefix, int r1, int c1, int r2, int c2) {
        return prefix[r2+1][c2+1]
             - prefix[r1][c2+1]
             - prefix[r2+1][c1]
             + prefix[r1][c1];
    }

    // =========================================================
    // 5. String Manipulation — các thao tác thường dùng trong Java
    // =========================================================
    static void stringTechniques() {
        System.out.println("\n=== String Techniques ===");

        // StringBuilder — O(n) thay vì O(n²) khi nối chuỗi trong vòng lặp
        // Sai: String s = ""; for(...) s += c;  → O(n²) do tạo object mới mỗi lần
        // Đúng:
        StringBuilder sb = new StringBuilder();
        for (char c : "hello world".toCharArray()) {
            if (c != ' ') sb.append(c);
        }
        System.out.println("Remove spaces: " + sb); // "helloworld"

        // Reverse string — Two pointer trên char[]
        char[] chars = "abcdef".toCharArray();
        int l = 0, r = chars.length - 1;
        while (l < r) {
            char tmp = chars[l]; chars[l] = chars[r]; chars[r] = tmp;
            l++; r--;
        }
        System.out.println("Reverse: " + new String(chars)); // "fedcba"

        // Anagram check — frequency array
        System.out.println("isAnagram('anagram','nagaram'): " + isAnagram("anagram", "nagaram"));

        // Pangram check
        System.out.println("isPangram('The quick brown fox...'): "
                + isPangram("The quick brown fox jumps over the lazy dog"));
    }

    static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;
        for (char c : t.toCharArray()) {
            if (--freq[c - 'a'] < 0) return false;
        }
        return true;
    }

    static boolean isPangram(String s) {
        boolean[] seen = new boolean[26];
        for (char c : s.toLowerCase().toCharArray()) {
            if (c >= 'a' && c <= 'z') seen[c - 'a'] = true;
        }
        for (boolean b : seen) if (!b) return false;
        return true;
    }

    public static void main(String[] args) {
        System.out.println("=== Prefix Sum ===\n");

        int[] arr = {1, 2, 3, 4, 5};
        int[] prefix = buildPrefix(arr);
        System.out.print("Prefix array: ");
        for (int x : prefix) System.out.print(x + " ");
        System.out.println();
        System.out.println("rangeSum(1,3) = " + rangeSum(prefix, 1, 3)); // 2+3+4=9
        System.out.println("rangeSum(0,4) = " + rangeSum(prefix, 0, 4)); // 15

        // NumArray
        NumArray na = new NumArray(new int[]{-2, 0, 3, -5, 2, -1});
        System.out.println("\nNumArray queries:");
        System.out.println("  sumRange(0,2) = " + na.sumRange(0, 2)); // 1
        System.out.println("  sumRange(2,5) = " + na.sumRange(2, 5)); // -1

        // Subarray Sum
        System.out.println("\nsubarraySum([1,1,1], k=2) = "
                + subarraySum(new int[]{1, 1, 1}, 2)); // 2
        System.out.println("subarraySum([1,2,3], k=3)  = "
                + subarraySum(new int[]{1, 2, 3}, 3)); // 2 ([1,2] và [3])

        // Product Except Self
        int[] product = productExceptSelf(new int[]{1, 2, 3, 4});
        System.out.print("\nproductExceptSelf([1,2,3,4]): ");
        for (int x : product) System.out.print(x + " "); // 24 12 8 6
        System.out.println();

        // 2D Prefix
        int[][] matrix = {{3, 0, 1, 4}, {5, 6, 3, 2}, {1, 2, 0, 1}};
        int[][] p2d = build2DPrefix(matrix);
        System.out.println("\n2D prefix query (1,1)→(2,2) = "
                + queryMatrix(p2d, 1, 1, 2, 2)); // 6+3+2+0=11

        stringTechniques();
    }
}
