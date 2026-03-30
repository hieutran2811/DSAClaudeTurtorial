package phase1_foundations.arrays;

import java.util.Arrays;

/**
 * PHASE 1.2 — Two Pointer Technique
 *
 * Ý tưởng: Dùng 2 con trỏ (index) thay vì lồng vòng lặp
 *          → Giảm O(n²) xuống O(n)
 *
 * 3 dạng phổ biến:
 *   1. Opposite ends  : left=0, right=n-1, tiến vào giữa
 *   2. Same direction : slow & fast (cùng chiều, tốc độ khác nhau)
 *   3. Two arrays     : mỗi con trỏ duyệt 1 mảng
 */
public class TwoPointer {

    // =========================================================
    // DẠNG 1: Opposite Ends (hai đầu tiến vào giữa)
    // =========================================================

    /**
     * Bài: Two Sum (mảng đã sort) — tìm 2 phần tử có tổng = target
     * Brute force: O(n²) | Two pointer: O(n)
     *
     * Logic: nếu sum < target → tăng left (cần số lớn hơn)
     *        nếu sum > target → giảm right (cần số nhỏ hơn)
     */
    static int[] twoSumSorted(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left < right) {
            int sum = arr[left] + arr[right];
            if (sum == target) return new int[]{left, right};
            if (sum < target) left++;   // cần tổng lớn hơn
            else              right--;  // cần tổng nhỏ hơn
        }
        return new int[]{-1, -1}; // không tìm thấy
    }

    /**
     * Bài: Valid Palindrome — chuỗi có phải palindrome không?
     * "racecar" → true | "hello" → false
     */
    static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }

    /**
     * Bài: Container With Most Water (LeetCode #11)
     * Cho mảng chiều cao cột, tìm 2 cột chứa nhiều nước nhất
     * Diện tích = min(height[left], height[right]) × (right - left)
     *
     * Logic: luôn dịch chuyển cột thấp hơn (vì giữ cột cao không giúp gì)
     */
    static int maxWater(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;
        while (left < right) {
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);
            if (height[left] < height[right]) left++;  // dịch cột thấp
            else                              right--;
        }
        return maxArea;
    }

    /**
     * Bài: 3Sum — tìm tất cả bộ ba có tổng = 0
     * Kỹ thuật: sort + fix 1 phần tử + two pointer cho 2 phần còn lại
     * Time: O(n²) | Space: O(1)
     */
    static java.util.List<java.util.List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums); // phải sort trước
        java.util.List<java.util.List<Integer>> result = new java.util.ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // bỏ duplicate
            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++; right--;
                } else if (sum < 0) left++;
                else                right--;
            }
        }
        return result;
    }

    // =========================================================
    // DẠNG 2: Same Direction — Slow & Fast Pointer
    // =========================================================

    /**
     * Bài: Remove Duplicates from Sorted Array (LeetCode #26)
     * slow: vị trí ghi tiếp theo | fast: duyệt toàn mảng
     * Không dùng extra space: O(1) space
     */
    static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int slow = 0;
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast]; // ghi phần tử mới vào vị trí slow
            }
        }
        return slow + 1; // độ dài mảng sau khi xóa duplicate
    }

    /**
     * Bài: Move Zeroes (LeetCode #283)
     * Đẩy tất cả số 0 về cuối, giữ thứ tự các số khác 0
     * slow: vị trí số 0 đầu tiên | fast: tìm số khác 0
     */
    static void moveZeroes(int[] nums) {
        int slow = 0; // slow luôn trỏ vào vị trí đặt số tiếp theo
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                nums[slow++] = nums[fast];
            }
        }
        while (slow < nums.length) nums[slow++] = 0; // điền 0 vào cuối
    }

    // =========================================================
    // DẠNG 3: Two Arrays
    // =========================================================

    /**
     * Bài: Merge Two Sorted Arrays
     * Dùng 2 con trỏ, mỗi cái duyệt 1 mảng
     * Time: O(m+n) | Space: O(m+n)
     */
    static int[] mergeSortedArrays(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) result[k++] = a[i++];
            else               result[k++] = b[j++];
        }
        while (i < a.length) result[k++] = a[i++];
        while (j < b.length) result[k++] = b[j++];
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== Two Pointer Technique ===\n");

        // Dạng 1 — Opposite ends
        int[] sorted = {1, 2, 3, 4, 6};
        int[] idx = twoSumSorted(sorted, 6);
        System.out.println("twoSumSorted(target=6): [" + idx[0] + ", " + idx[1] + "]"
                + " → " + sorted[idx[0]] + " + " + sorted[idx[1]]);

        System.out.println("isPalindrome(racecar): " + isPalindrome("racecar"));
        System.out.println("isPalindrome(hello):   " + isPalindrome("hello"));

        int[] heights = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        System.out.println("maxWater: " + maxWater(heights)); // 49

        int[] nums3 = {-1, 0, 1, 2, -1, -4};
        System.out.println("3Sum: " + threeSum(nums3)); // [[-1,-1,2],[-1,0,1]]

        // Dạng 2 — Slow & Fast
        int[] dupArr = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int len = removeDuplicates(dupArr);
        System.out.print("removeDuplicates: ");
        for (int i = 0; i < len; i++) System.out.print(dupArr[i] + " ");
        System.out.println();

        int[] zeros = {0, 1, 0, 3, 12};
        moveZeroes(zeros);
        System.out.println("moveZeroes: " + Arrays.toString(zeros)); // [1,3,12,0,0]

        // Dạng 3 — Two arrays
        int[] merged = mergeSortedArrays(new int[]{1, 3, 5}, new int[]{2, 4, 6});
        System.out.println("mergeSortedArrays: " + Arrays.toString(merged));
    }
}
