package phase1_foundations.stackqueue;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * PHASE 1.4 — Monotonic Stack (Chuyên đề)
 *
 * Stack đơn điệu: luôn duy trì thứ tự tăng hoặc giảm
 *
 * Dùng khi cần tìm:
 *   "Next Greater Element"  → stack giảm dần
 *   "Next Smaller Element"  → stack tăng dần
 *   "Previous Greater"      → duyệt từ phải sang trái
 *
 * TEMPLATE:
 *   for i in 0..n:
 *     while stack không rỗng && stack.peek() [</>] arr[i]:
 *       pop → xử lý kết quả cho phần tử vừa pop
 *     push(i)
 */
public class MonotonicStack {

    // =========================================================
    // 1. Next Greater Element I (LeetCode #496)
    // =========================================================
    /**
     * nums1=[4,1,2], nums2=[1,3,4,2]
     * → [-1,3,-1]  (next greater của 4 trong nums2 không có → -1, của 1 là 3, của 2 không có)
     *
     * Bước 1: Tính next greater cho TẤT CẢ phần tử nums2 bằng mono stack
     * Bước 2: Tra cứu bằng HashMap
     */
    static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        java.util.Map<Integer, Integer> nextGreater = new java.util.HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>(); // lưu VALUE (giảm dần)

        for (int x : nums2) {
            while (!stack.isEmpty() && stack.peek() < x) {
                nextGreater.put(stack.pop(), x); // x là next greater của stack.peek()
            }
            stack.push(x);
        }
        // Còn lại trong stack → không có next greater → -1

        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreater.getOrDefault(nums1[i], -1);
        }
        return result;
    }

    // =========================================================
    // 2. Next Greater Element II — Circular Array (LeetCode #503)
    // =========================================================
    /**
     * [1,2,1] → [2,-1,2]  (circular: sau 1 cuối là 1 đầu → next greater là 2)
     *
     * Trick: duyệt 2 lần (i từ 0 đến 2n-1), dùng i % n
     */
    static int[] nextGreaterCircular(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>(); // lưu INDEX

        for (int i = 0; i < 2 * n; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i % n]) {
                result[stack.pop()] = nums[i % n];
            }
            if (i < n) stack.push(i); // chỉ push trong lần duyệt đầu
        }
        return result;
    }

    // =========================================================
    // 3. Trapping Rain Water (LeetCode #42) — Very Important
    // =========================================================
    /**
     * [0,1,0,2,1,0,1,3,2,1,2,1] → 6
     *
     * Cách 1: Mono Stack — O(n) time O(n) space
     *   Khi gặp cột cao hơn cột top → có thể chứa nước giữa 2 cột cao
     *   width  = i - stack.peek() - 1
     *   height = min(heights[i], heights[stack.peek()]) - heights[top]
     */
    static int trapRainWater(int[] height) {
        Deque<Integer> stack = new ArrayDeque<>(); // lưu INDEX (tăng dần)
        int totalWater = 0;

        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = stack.pop(); // đáy của "hố nước"
                if (stack.isEmpty()) break;

                int left  = stack.peek();
                int width  = i - left - 1;
                int h      = Math.min(height[i], height[left]) - height[bottom];
                totalWater += width * h;
            }
            stack.push(i);
        }
        return totalWater;
    }

    /**
     * Cách 2: Two Pointer — O(n) time O(1) space (tối ưu hơn)
     * Với mỗi vị trí i: nước = min(maxLeft[i], maxRight[i]) - height[i]
     */
    static int trapTwoPointer(int[] height) {
        int left = 0, right = height.length - 1;
        int maxLeft = 0, maxRight = 0, water = 0;

        while (left < right) {
            if (height[left] <= height[right]) {
                if (height[left] >= maxLeft) maxLeft = height[left];
                else water += maxLeft - height[left]; // chứa nước
                left++;
            } else {
                if (height[right] >= maxRight) maxRight = height[right];
                else water += maxRight - height[right];
                right--;
            }
        }
        return water;
    }

    // =========================================================
    // 4. Sum of Subarray Minimums (LeetCode #907)
    // =========================================================
    /**
     * arr=[3,1,2,4] → 17
     * Tất cả subarray: [3]=3, [1]=1, [2]=2, [4]=4,
     *                  [3,1]=1, [1,2]=1, [2,4]=2,
     *                  [3,1,2]=1, [1,2,4]=1, [3,1,2,4]=1 → tổng=17
     *
     * Kỹ thuật: với mỗi arr[i], đóng góp = arr[i] × (# subarray có min = arr[i])
     *   left[i]  = # phần tử liên tiếp bên trái > arr[i] (dùng mono stack)
     *   right[i] = # phần tử liên tiếp bên phải >= arr[i]
     *   đóng góp = arr[i] × (left[i]+1) × (right[i]+1)
     *
     * Time: O(n) | Space: O(n)
     */
    static int sumSubarrayMins(int[] arr) {
        int n = arr.length;
        int MOD = 1_000_000_007;
        int[] left = new int[n], right = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        // Tính left[i] — khoảng cách đến previous smaller (strictly)
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) stack.pop();
            left[i] = stack.isEmpty() ? i + 1 : i - stack.peek();
            stack.push(i);
        }

        stack.clear();
        // Tính right[i] — khoảng cách đến next smaller or equal
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) stack.pop();
            right[i] = stack.isEmpty() ? n - i : stack.peek() - i;
            stack.push(i);
        }

        long result = 0;
        for (int i = 0; i < n; i++) {
            result = (result + (long) arr[i] * left[i] % MOD * right[i]) % MOD;
        }
        return (int) result;
    }

    // =========================================================
    // 5. Remove K Digits (LeetCode #402)
    // =========================================================
    /**
     * num="1432219", k=3 → "1219"  (xóa 3 chữ số để được số nhỏ nhất)
     *
     * Dùng mono stack tăng dần:
     *   Khi gặp chữ số nhỏ hơn đỉnh stack → pop (xóa chữ số lớn hơn)
     *   → Đảm bảo chữ số nhỏ luôn ở trước
     */
    static String removeKDigits(String num, int k) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : num.toCharArray()) {
            while (k > 0 && !stack.isEmpty() && stack.peek() > c) {
                stack.pop();
                k--;
            }
            stack.push(c);
        }
        // Nếu còn k → xóa k chữ số từ cuối (stack tăng dần → cuối là lớn nhất)
        while (k-- > 0) stack.pop();

        // Build kết quả, bỏ leading zeros
        StringBuilder sb = new StringBuilder();
        boolean leadingZero = true;
        while (!stack.isEmpty()) sb.append(stack.pollLast()); // pollLast lấy từ bottom
        String result = sb.toString().replaceAll("^0+", "");
        return result.isEmpty() ? "0" : result;
    }

    public static void main(String[] args) {
        System.out.println("=== Monotonic Stack ===\n");

        // Next Greater Element
        System.out.println("--- Next Greater Element ---");
        int[] res = nextGreaterElement(new int[]{4,1,2}, new int[]{1,3,4,2});
        System.out.println("nums1=[4,1,2], nums2=[1,3,4,2] → " + Arrays.toString(res)); // [-1,3,-1]

        // Circular
        System.out.println("\n--- Next Greater Circular ---");
        System.out.println("[1,2,1] → " + Arrays.toString(nextGreaterCircular(new int[]{1,2,1}))); // [2,-1,2]
        System.out.println("[1,2,3,4,3] → " + Arrays.toString(nextGreaterCircular(new int[]{1,2,3,4,3}))); // [2,3,4,-1,4]

        // Trapping Rain Water
        System.out.println("\n--- Trapping Rain Water ---");
        int[] h1 = {0,1,0,2,1,0,1,3,2,1,2,1};
        int[] h2 = {4,2,0,3,2,5};
        System.out.println("[0,1,0,2,1,0,1,3,2,1,2,1] Stack:  " + trapRainWater(h1)); // 6
        System.out.println("[0,1,0,2,1,0,1,3,2,1,2,1] 2Ptr:   " + trapTwoPointer(h1)); // 6
        System.out.println("[4,2,0,3,2,5]             Stack:  " + trapRainWater(h2)); // 9

        // Sum Subarray Mins
        System.out.println("\n--- Sum of Subarray Minimums ---");
        System.out.println("[3,1,2,4] → " + sumSubarrayMins(new int[]{3,1,2,4})); // 17
        System.out.println("[11,81,94,43,3] → " + sumSubarrayMins(new int[]{11,81,94,43,3})); // 444

        // Remove K Digits
        System.out.println("\n--- Remove K Digits ---");
        System.out.println("'1432219' k=3 → " + removeKDigits("1432219", 3)); // "1219"
        System.out.println("'10200'   k=1 → " + removeKDigits("10200", 1));   // "200"
        System.out.println("'10'      k=2 → " + removeKDigits("10", 2));      // "0"
    }
}
