package phase1_foundations.arrays;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * PHASE 1.2 — Static Array vs Dynamic Array (ArrayList)
 *
 * Static Array  : kích thước cố định khi khai báo, lưu trên stack/heap liên tục.
 * Dynamic Array : tự động mở rộng khi đầy (ArrayList trong Java).
 *
 * So sánh nhanh:
 * ┌─────────────────┬──────────────┬──────────────────┐
 * │ Operation       │ Static Array │ Dynamic Array    │
 * ├─────────────────┼──────────────┼──────────────────┤
 * │ Access  arr[i]  │ O(1)         │ O(1)             │
 * │ Search  (unsort)│ O(n)         │ O(n)             │
 * │ Insert at end   │ O(1)*        │ O(1) amortized   │
 * │ Insert at i     │ O(n)         │ O(n)             │
 * │ Delete at end   │ O(1)         │ O(1)             │
 * │ Delete at i     │ O(n)         │ O(n)             │
 * │ Resize          │ ✗ không thể  │ O(n) — tự động   │
 * └─────────────────┴──────────────┴──────────────────┘
 * * Static array không thực sự "insert" — chỉ ghi vào ô trống.
 */
public class StaticVsDynamicArray {

    // =========================================================
    // 1. STATIC ARRAY — int[], fixed capacity
    // =========================================================

    /**
     * Mảng tĩnh thủ công: quản lý size bằng tay.
     * Minh họa cách ArrayList hoạt động bên dưới.
     */
    static class StaticArray {
        private int[] data;
        private int size;       // số phần tử thực tế
        private int capacity;   // kích thước mảng thô

        StaticArray(int capacity) {
            this.capacity = capacity;
            this.data = new int[capacity];
            this.size = 0;
        }

        // O(1) — ghi trực tiếp theo index
        void set(int index, int value) {
            if (index < 0 || index >= capacity)
                throw new IndexOutOfBoundsException("index: " + index);
            data[index] = value;
            if (index >= size) size = index + 1;
        }

        // O(1) — đọc trực tiếp theo index
        int get(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("index: " + index);
            return data[index];
        }

        // O(n) — dịch chuyển các phần tử sau index sang phải
        void insertAt(int index, int value) {
            if (size >= capacity) throw new RuntimeException("Array is full");
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("index: " + index);
            // Dịch phải từ cuối về index
            for (int i = size; i > index; i--)
                data[i] = data[i - 1];
            data[index] = value;
            size++;
        }

        // O(n) — dịch chuyển các phần tử sau index sang trái
        void deleteAt(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("index: " + index);
            for (int i = index; i < size - 1; i++)
                data[i] = data[i + 1];
            size--;
        }

        int size() { return size; }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(data, size));
        }
    }

    // =========================================================
    // 2. DYNAMIC ARRAY — tự resize khi đầy
    // =========================================================

    /**
     * Tự xây Dynamic Array đơn giản để hiểu cơ chế resize.
     *
     * Chiến lược resize: nhân đôi capacity khi đầy.
     *   → n lần add: tổng công resize = 1+2+4+...+n = 2n → O(1) amortized mỗi add.
     *
     * Java ArrayList dùng: newCapacity = oldCapacity + (oldCapacity >> 1) ≈ × 1.5
     */
    static class DynamicArray {
        private int[] data;
        private int size;
        private int capacity;
        private int resizeCount = 0; // chỉ để demo

        DynamicArray() {
            capacity = 4; // capacity ban đầu nhỏ để dễ thấy resize
            data = new int[capacity];
            size = 0;
        }

        // O(1) amortized — thỉnh thoảng O(n) khi resize
        void add(int value) {
            if (size == capacity) resize();
            data[size++] = value;
        }

        // O(n) — dịch phải + có thể resize
        void addAt(int index, int value) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException("index: " + index);
            if (size == capacity) resize();
            for (int i = size; i > index; i--)
                data[i] = data[i - 1];
            data[index] = value;
            size++;
        }

        // O(1)
        int get(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("index: " + index);
            return data[index];
        }

        // O(1)
        void set(int index, int value) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("index: " + index);
            data[index] = value;
        }

        // O(n) — dịch trái
        void removeAt(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException("index: " + index);
            for (int i = index; i < size - 1; i++)
                data[i] = data[i + 1];
            size--;
            // Tùy chọn: shrink nếu size < capacity/4 để tránh lãng phí bộ nhớ
        }

        // O(n) — cấp phát mảng mới, copy toàn bộ
        private void resize() {
            capacity = capacity * 2; // nhân đôi
            int[] newData = new int[capacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
            resizeCount++;
            System.out.println("  [resize] capacity → " + capacity
                    + "  (resize #" + resizeCount + ")");
        }

        int size()     { return size; }
        int capacity() { return capacity; }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOf(data, size))
                    + " (size=" + size + ", capacity=" + capacity + ")";
        }
    }

    // =========================================================
    // 3. ArrayList của Java — API thực tế
    // =========================================================

    static void javaArrayListDemo() {
        System.out.println("\n--- Java ArrayList API ---");
        ArrayList<Integer> list = new ArrayList<>(4); // initialCapacity hint

        // add O(1) amortized
        for (int i = 1; i <= 6; i++) list.add(i * 10);
        System.out.println("after 6 adds   : " + list);

        // insert tại index O(n)
        list.add(2, 99);
        System.out.println("add(2, 99)     : " + list);

        // remove theo index O(n)
        list.remove(2);         // remove index 2
        System.out.println("remove(idx=2)  : " + list);

        // remove theo value O(n)
        list.remove(Integer.valueOf(40)); // autoboxing → remove value
        System.out.println("remove(val=40) : " + list);

        // get / set O(1)
        System.out.println("get(0)         : " + list.get(0));
        list.set(0, 5);
        System.out.println("set(0, 5)      : " + list);

        // contains O(n)
        System.out.println("contains(50)   : " + list.contains(50));

        // size
        System.out.println("size           : " + list.size());

        // convert to array O(n)
        Integer[] arr = list.toArray(new Integer[0]);
        System.out.println("toArray        : " + Arrays.toString(arr));
    }

    // =========================================================
    // 4. Khi nào dùng Static Array vs Dynamic Array?
    // =========================================================
    /*
     * Dùng Static Array (int[]) khi:
     *   - Biết trước kích thước (fixed-size buffers, DP tables, sliding window)
     *   - Cần hiệu năng tối đa (không có overhead boxing/unboxing)
     *   - Làm việc với primitive types: int, char, byte…
     *   - Ví dụ: int[] dp = new int[n+1]; char[] chars = s.toCharArray();
     *
     * Dùng Dynamic Array (ArrayList) khi:
     *   - Không biết trước số lượng phần tử
     *   - Cần thêm/xóa linh hoạt ở cuối mảng
     *   - Cần trả về collection từ method (List<Integer>)
     *   - Ví dụ: kết quả backtracking, BFS queue output…
     *
     * Tránh:
     *   - ArrayList<Integer> cho primitive — boxing overhead O(1) nhưng tốn bộ nhớ 4×
     *   - Static array khi cần resize thường xuyên → phải tự copy thủ công
     */

    // =========================================================
    // DEMO
    // =========================================================
    public static void main(String[] args) {

        // --- Static Array ---
        System.out.println("=== Static Array ===");
        StaticArray sa = new StaticArray(6);
        sa.set(0, 10); sa.set(1, 20); sa.set(2, 30);
        System.out.println("after set 0,1,2 : " + sa);

        sa.insertAt(1, 15);
        System.out.println("insertAt(1,15)  : " + sa);

        sa.deleteAt(2);
        System.out.println("deleteAt(2)     : " + sa);

        System.out.println("get(1)          : " + sa.get(1));

        // --- Dynamic Array ---
        System.out.println("\n=== Dynamic Array (custom) — watch resize ===");
        DynamicArray da = new DynamicArray();
        System.out.println("initial         : " + da);

        for (int v : new int[]{10, 20, 30, 40, 50, 60, 70}) {
            da.add(v);
            System.out.println("add(" + v + ")           : " + da);
        }

        da.addAt(3, 99);
        System.out.println("addAt(3, 99)    : " + da);

        da.removeAt(3);
        System.out.println("removeAt(3)     : " + da);

        // --- Java ArrayList ---
        javaArrayListDemo();

        // --- Bảng tóm tắt ---
        System.out.println("\n=== Bảng So Sánh ===");
        System.out.println("Operation       Static int[]   ArrayList<T>");
        System.out.println("Access  arr[i]  O(1)           O(1)");
        System.out.println("Append  (end)   O(1)*          O(1) amortized");
        System.out.println("Insert  (mid)   O(n)           O(n)");
        System.out.println("Delete  (mid)   O(n)           O(n)");
        System.out.println("Resize          ✗ manual       ✓ auto (×1.5)");
        System.out.println("Memory          compact        boxing overhead");
        System.out.println("Type            primitive only generics only");
    }
}
