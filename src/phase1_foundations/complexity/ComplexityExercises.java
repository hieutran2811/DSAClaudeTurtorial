package phase1_foundations.complexity;

/**
 * PHASE 1.1 — Bài tập phân tích Big-O
 *
 * Với mỗi hàm bên dưới, hãy tự phân tích trước khi xem đáp án.
 * Câu hỏi: Time complexity và Space complexity là gì?
 */
public class ComplexityExercises {

    // ---------------------------------------------------------
    // BÀI 1: Đây là O(?) — Time và Space?
    // ---------------------------------------------------------
    static boolean containsDuplicate(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) return true;
            }
        }
        return false;
    }
    // ĐÁP ÁN: Time O(n²) — 2 vòng lặp lồng nhau
    //          Space O(1)  — không dùng bộ nhớ thêm

    // ---------------------------------------------------------
    // BÀI 2: Đây là O(?) — Time và Space?
    // ---------------------------------------------------------
    static boolean containsDuplicateFast(int[] nums) {
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (int x : nums) {
            if (seen.contains(x)) return true;
            seen.add(x);
        }
        return false;
    }
    // ĐÁP ÁN: Time O(n)  — duyệt 1 lần, HashSet O(1) per op
    //          Space O(n) — HashSet tối đa n phần tử

    // ---------------------------------------------------------
    // BÀI 3: Đây là O(?) — Time và Space?
    // ---------------------------------------------------------
    static int[] twoSum(int[] nums, int target) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement))
                return new int[]{map.get(complement), i};
            map.put(nums[i], i);
        }
        return new int[]{};
    }
    // ĐÁP ÁN: Time O(n)  — 1 vòng lặp
    //          Space O(n) — HashMap lưu tối đa n cặp

    // ---------------------------------------------------------
    // BÀI 4: Đây là O(?) — Cẩn thận với vòng lặp trong!
    // ---------------------------------------------------------
    static void printPairs(int n) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                System.out.print("(" + i + "," + j + ") ");
            }
        }
    }
    // ĐÁP ÁN: Time O(n²) — n × n = n² lần in

    static void printUnorderedPairs(int n) {
        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) { // j bắt đầu từ i+1
                System.out.print("(" + i + "," + j + ") ");
            }
        }
    }
    // ĐÁP ÁN: Time O(n²) — vẫn là n²/2 → bỏ hằng số → O(n²)

    // ---------------------------------------------------------
    // BÀI 5: Đây là O(?) — có nhiều input!
    // ---------------------------------------------------------
    static void printAllElements(int[] arrA, int[] arrB) {
        for (int x : arrA) System.out.print(x + " "); // O(a)
        for (int x : arrB) System.out.print(x + " "); // O(b)
    }
    // ĐÁP ÁN: Time O(a + b) — KHÔNG phải O(n)!
    //          2 mảng khác nhau → dùng biến khác nhau

    static void printAllPairsCross(int[] arrA, int[] arrB) {
        for (int x : arrA)         // O(a)
            for (int y : arrB)     // O(b)
                System.out.print("(" + x + "," + y + ") ");
    }
    // ĐÁP ÁN: Time O(a × b) — KHÔNG phải O(n²)!

    // ---------------------------------------------------------
    // BÀI 6: Đây là O(?) — đệ quy!
    // ---------------------------------------------------------
    static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1); // gọi đệ quy n lần
    }
    // ĐÁP ÁN: Time O(n)  — call stack sâu n tầng
    //          Space O(n) — n frame trên call stack

    static int power(int base, int exp) {
        if (exp == 0) return 1;
        return base * power(base, exp / 2) * power(base, exp / 2); // 2 nhánh, exp/2
    }
    // ĐÁP ÁN: Time O(n) — exp bị chia đôi nhưng có 2 nhánh → O(exp)
    //          Tối ưu: tính power(base, exp/2) 1 lần → O(log n)

    // ---------------------------------------------------------
    // BÀI 7: Amortized Analysis — ArrayList.add()
    // Đôi khi O(n) nhưng trung bình vẫn là O(1)
    // ---------------------------------------------------------
    // ArrayList thường có capacity nhân đôi khi đầy:
    // Copy: 1, 2, 4, 8, 16... → tổng copy = 2n → mỗi add trung bình O(1)
    // Đây là AMORTIZED O(1)
    static void demonstrateAmortized() {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i); // amortized O(1) mỗi lần
        }
        // 100 phép add → tổng O(n), trung bình O(1) mỗi phép
    }

    // ---------------------------------------------------------
    // QUIZ — Phân tích nhanh
    // ---------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("=== Complexity Exercises ===\n");

        int[] nums = {1, 3, 5, 7, 3, 9};

        System.out.println("Bài 1 — containsDuplicate O(n²): "
                + containsDuplicate(nums));
        System.out.println("Bài 2 — containsDuplicate O(n):  "
                + containsDuplicateFast(nums));

        int[] twoSumArr = {2, 7, 11, 15};
        int[] result = twoSum(twoSumArr, 9);
        System.out.println("Bài 3 — twoSum O(n): [" + result[0] + ", " + result[1] + "]");

        System.out.println("Bài 6 — factorial(5) O(n): " + factorial(5));
        System.out.println("Bài 6 — power(2,10) O(n): " + power(2, 10));

        System.out.println("\n=== Quy tắc DROP ===");
        System.out.println("1. Bỏ hằng số:    O(2n)    → O(n)");
        System.out.println("2. Bỏ số hạng nhỏ: O(n²+n) → O(n²)");
        System.out.println("3. Giữ nguyên nếu input khác nhau: O(a+b) giữ nguyên");
    }
}
