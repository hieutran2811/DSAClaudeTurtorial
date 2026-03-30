package phase1_foundations.complexity;

/**
 * PHASE 1.1 — Complexity Analysis
 *
 * Big-O Notation: đo lường tốc độ tăng của thời gian / bộ nhớ
 * khi kích thước input (n) tăng lên.
 *
 * Thứ tự từ nhanh nhất đến chậm nhất:
 *   O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(2ⁿ) < O(n!)
 */
public class BigONotation {

    // =========================================================
    // O(1) — Constant Time
    // Không quan tâm n bao nhiêu, luôn thực hiện đúng 1 bước
    // =========================================================
    static int getFirst(int[] arr) {
        return arr[0]; // luôn 1 bước
    }

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i]; // 3 bước cố định
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // =========================================================
    // O(log n) — Logarithmic Time
    // Mỗi bước loại bỏ nửa input → n=1000 chỉ cần ~10 bước
    // Dấu hiệu: input bị chia đôi mỗi lần lặp
    // =========================================================
    static int binarySearch(int[] arr, int target) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {            // lặp log₂(n) lần
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return mid;
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    // =========================================================
    // O(n) — Linear Time
    // Duyệt qua toàn bộ input đúng 1 lần
    // =========================================================
    static int findMax(int[] arr) {
        int max = arr[0];
        for (int x : arr) { // n lần
            if (x > max) max = x;
        }
        return max;
    }

    static int sumArray(int[] arr) {
        int sum = 0;
        for (int x : arr) sum += x; // n lần
        return sum;
    }

    // =========================================================
    // O(n log n) — Linearithmic Time
    // Điển hình: Merge Sort, Quick Sort (average)
    // Dấu hiệu: chia nhỏ (log n tầng) + xử lý từng tầng (n)
    // =========================================================
    static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(arr, left, mid);      // T(n/2)
        mergeSort(arr, mid + 1, right); // T(n/2)
        merge(arr, left, mid, right);   // O(n)
        // Tổng: T(n) = 2T(n/2) + O(n) → O(n log n)
    }

    static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1, n2 = right - mid;
        int[] L = new int[n1], R = new int[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2)
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // =========================================================
    // O(n²) — Quadratic Time
    // Vòng lặp lồng nhau qua toàn bộ input
    // Dấu hiệu: 2 for lồng nhau cùng duyệt n phần tử
    // =========================================================
    static int[] bubbleSort(int[] arr) {
        int n = arr.length;
        int[] a = arr.clone();
        for (int i = 0; i < n - 1; i++) {      // n lần
            for (int j = 0; j < n - i - 1; j++) { // n lần
                if (a[j] > a[j + 1]) swap(a, j, j + 1);
            }
        }
        return a; // tổng: n × n = O(n²)
    }

    // Tìm tất cả cặp phần tử có tổng = target
    static void findPairs(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {      // n
            for (int j = i + 1; j < arr.length; j++) { // n
                if (arr[i] + arr[j] == target)
                    System.out.println("(" + arr[i] + ", " + arr[j] + ")");
            }
        }
    }

    // =========================================================
    // O(2ⁿ) — Exponential Time
    // Mỗi bước sinh ra 2 nhánh đệ quy → cây nhị phân đầy đủ
    // n=30 → hơn 1 tỷ bước! Cần tối ưu bằng DP
    // =========================================================
    static int fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2); // 2 nhánh mỗi lần
    }

    // Tối ưu O(2ⁿ) → O(n) bằng memoization
    static int fibMemo(int n, int[] memo) {
        if (n <= 1) return n;
        if (memo[n] != 0) return memo[n]; // cache hit
        memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
        return memo[n];
    }

    // =========================================================
    // Space Complexity — Bộ nhớ sử dụng
    // =========================================================

    // O(1) space — chỉ dùng vài biến cố định
    static int sumIterative(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) sum += i;
        return sum; // bộ nhớ không tăng theo n
    }

    // O(n) space — tạo mảng kích thước n
    static int[] buildArray(int n) {
        int[] arr = new int[n]; // n ô nhớ
        for (int i = 0; i < n; i++) arr[i] = i * 2;
        return arr;
    }

    // O(n) space — call stack của đệ quy sâu n tầng
    static int sumRecursive(int n) {
        if (n == 0) return 0;
        return n + sumRecursive(n - 1); // n frame trên stack
    }

    // =========================================================
    // DEMO — So sánh trực quan
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== Big-O Complexity Demo ===\n");

        int[] sortedArr = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        int[] unsortedArr = {64, 34, 25, 12, 22, 11, 90};

        // O(1)
        System.out.println("O(1) getFirst: " + getFirst(sortedArr));

        // O(log n)
        System.out.println("O(log n) binarySearch(13): index = "
                + binarySearch(sortedArr, 13));

        // O(n)
        System.out.println("O(n) findMax: " + findMax(unsortedArr));

        // O(n log n)
        int[] toSort = unsortedArr.clone();
        mergeSort(toSort, 0, toSort.length - 1);
        System.out.print("O(n log n) mergeSort: ");
        for (int x : toSort) System.out.print(x + " ");
        System.out.println();

        // O(n²)
        System.out.print("O(n²) bubbleSort: ");
        for (int x : bubbleSort(unsortedArr)) System.out.print(x + " ");
        System.out.println();

        // O(n²) find pairs
        System.out.println("O(n²) pairs summing to 35:");
        findPairs(sortedArr, 35);

        // O(2ⁿ) vs O(n)
        int n = 10;
        System.out.println("O(2ⁿ) fib(10) naive: " + fibNaive(n));
        System.out.println("O(n)  fib(10) memo:  " + fibMemo(n, new int[n + 1]));

        // Tổng kết
        System.out.println("\n=== Bảng So Sánh (n = 1000) ===");
        System.out.println("O(1)       →          1 bước");
        System.out.println("O(log n)   →         ~10 bước");
        System.out.println("O(n)       →      1,000 bước");
        System.out.println("O(n log n) →     10,000 bước");
        System.out.println("O(n²)      →  1,000,000 bước");
        System.out.println("O(2ⁿ)      → 10^301 bước  ← KHÔNG dùng được!");
    }
}
