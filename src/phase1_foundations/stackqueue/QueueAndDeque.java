package phase1_foundations.stackqueue;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * PHASE 1.4 — Queue, Deque & Priority Queue
 *
 * Queue  : FIFO — First In First Out (hàng đợi)
 * Deque  : Double-Ended Queue — thêm/xóa được cả 2 đầu
 * PQ     : Luôn lấy ra phần tử nhỏ nhất (min-heap) hoặc lớn nhất (max-heap)
 *
 * Java APIs:
 *   Queue<Integer> q   = new ArrayDeque<>()
 *   Deque<Integer> dq  = new ArrayDeque<>()
 *   PriorityQueue<Integer> pq = new PriorityQueue<>()     // min-heap
 *   PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()) // max-heap
 */
public class QueueAndDeque {

    // =========================================================
    // Queue tự cài bằng mảng vòng (Circular Array)
    // =========================================================
    static class MyQueue {
        private int[] data;
        private int head = 0, tail = 0, size = 0;

        MyQueue(int capacity) { data = new int[capacity]; }

        void enqueue(int val) {
            if (size == data.length) throw new RuntimeException("Queue full");
            data[tail] = val;
            tail = (tail + 1) % data.length; // vòng tròn
            size++;
        }

        int dequeue() {
            if (isEmpty()) throw new RuntimeException("Queue empty");
            int val = data[head];
            head = (head + 1) % data.length; // vòng tròn
            size--;
            return val;
        }

        int peek()      { return data[head]; }
        boolean isEmpty() { return size == 0; }
        int size()        { return size; }
    }

    // =========================================================
    // Queue dùng 2 Stack (bài phỏng vấn phổ biến)
    // =========================================================
    /**
     * Implement Queue using Stacks (LeetCode #232)
     *
     * Ý tưởng: stack1 nhận push, stack2 phục vụ pop/peek
     * Khi stack2 rỗng → đổ toàn bộ stack1 sang stack2 (đảo ngược thứ tự)
     *
     * Amortized O(1) mỗi thao tác (mỗi phần tử chuyển tối đa 1 lần)
     */
    static class MyQueueWith2Stacks {
        private Deque<Integer> pushStack = new ArrayDeque<>();
        private Deque<Integer> popStack  = new ArrayDeque<>();

        void push(int x) {
            pushStack.push(x);
        }

        int pop() {
            refill();
            return popStack.pop();
        }

        int peek() {
            refill();
            return popStack.peek();
        }

        boolean empty() { return pushStack.isEmpty() && popStack.isEmpty(); }

        private void refill() {
            if (popStack.isEmpty()) {
                while (!pushStack.isEmpty()) {
                    popStack.push(pushStack.pop()); // đảo ngược
                }
            }
        }
    }

    // =========================================================
    // Stack dùng 2 Queue (ít gặp hơn, nhưng hay hỏi)
    // =========================================================
    /**
     * Implement Stack using Queues (LeetCode #225)
     * push O(n) — pop O(1)
     *
     * Mỗi lần push: thêm vào q2, chuyển hết q1 sang q2, swap q1 q2
     * → q1 luôn có top ở đầu queue
     */
    static class MyStackWith2Queues {
        private Deque<Integer> q1 = new ArrayDeque<>();
        private Deque<Integer> q2 = new ArrayDeque<>();

        void push(int x) {
            q2.offer(x);
            while (!q1.isEmpty()) q2.offer(q1.poll());
            Deque<Integer> temp = q1; q1 = q2; q2 = temp; // swap
        }

        int pop()       { return q1.poll(); }
        int top()       { return q1.peek(); }
        boolean empty() { return q1.isEmpty(); }
    }

    // =========================================================
    // DEQUE — Double-Ended Queue
    // =========================================================

    /**
     * Sliding Window Maximum (LeetCode #239) — Hard
     * Tìm max trong mỗi cửa sổ kích thước k
     * [1,3,-1,-3,5,3,6,7], k=3 → [3,3,5,5,6,7]
     *
     * Dùng Monotonic Deque (giảm dần):
     *   - Thêm vào deque từ phải, pop phần tử nhỏ hơn phần tử mới
     *   - Pop từ trái nếu index đã ra ngoài cửa sổ
     *   - Max luôn là deque.peekFirst()
     *
     * Time: O(n) — mỗi phần tử vào/ra deque đúng 1 lần
     */
    static int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // lưu INDEX

        for (int i = 0; i < n; i++) {
            // Loại index đã ra khỏi cửa sổ
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }
            // Loại phần tử nhỏ hơn nums[i] từ phía sau (vô dụng)
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);

            // Bắt đầu ghi kết quả từ cửa sổ đầy đủ đầu tiên
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    /**
     * Design Circular Deque (LeetCode #641)
     * Thêm/xóa cả 2 đầu, kiểm tra full/empty
     */
    static class CircularDeque {
        private int[] data;
        private int front, rear, size, capacity;

        CircularDeque(int k) {
            capacity = k;
            data = new int[k];
            front = 0;
            rear = k - 1; // rear trỏ vào vị trí sẽ ghi tiếp theo
            size = 0;
        }

        boolean insertFront(int val) {
            if (isFull()) return false;
            front = (front - 1 + capacity) % capacity;
            data[front] = val;
            size++;
            return true;
        }

        boolean insertLast(int val) {
            if (isFull()) return false;
            rear = (rear + 1) % capacity;
            data[rear] = val;
            size++;
            return true;
        }

        boolean deleteFront() {
            if (isEmpty()) return false;
            front = (front + 1) % capacity;
            size--;
            return true;
        }

        boolean deleteLast() {
            if (isEmpty()) return false;
            rear = (rear - 1 + capacity) % capacity;
            size--;
            return true;
        }

        int getFront()   { return isEmpty() ? -1 : data[front]; }
        int getRear()    { return isEmpty() ? -1 : data[rear]; }
        boolean isEmpty(){ return size == 0; }
        boolean isFull() { return size == capacity; }
    }

    // =========================================================
    // PRIORITY QUEUE (Min-Heap / Max-Heap)
    // =========================================================

    /**
     * Kth Largest Element in Array (LeetCode #215)
     *
     * Dùng min-heap kích thước k:
     *   Duyệt mảng, giữ heap luôn có k phần tử lớn nhất
     *   Khi heap > k → poll() phần tử nhỏ nhất
     *   Kết quả: heap.peek() = phần tử nhỏ nhất trong top-k = kth largest
     *
     * Time: O(n log k) | Space: O(k)
     * Tốt hơn sort O(n log n) khi k << n
     */
    static int findKthLargest(int[] nums, int k) {
        java.util.PriorityQueue<Integer> minHeap = new java.util.PriorityQueue<>();
        for (int x : nums) {
            minHeap.offer(x);
            if (minHeap.size() > k) minHeap.poll(); // loại min
        }
        return minHeap.peek(); // top của heap = kth largest
    }

    /**
     * Top K Frequent Elements (LeetCode #347)
     * [1,1,1,2,2,3], k=2 → [1,2]
     *
     * Bước 1: Đếm tần suất bằng HashMap
     * Bước 2: Dùng min-heap theo tần suất kích thước k
     */
    static int[] topKFrequent(int[] nums, int k) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);

        // Min-heap theo tần suất
        java.util.PriorityQueue<int[]> minHeap =
                new java.util.PriorityQueue<>((a, b) -> a[1] - b[1]);

        for (var entry : freq.entrySet()) {
            minHeap.offer(new int[]{entry.getKey(), entry.getValue()});
            if (minHeap.size() > k) minHeap.poll();
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = minHeap.poll()[0];
        return result;
    }

    /**
     * Merge K Sorted Lists (LeetCode #23)
     * Dùng min-heap để luôn lấy node nhỏ nhất trong k list
     * Time: O(n log k) với n = tổng số node
     */
    static class ListNode { int val; ListNode next; ListNode(int v) { val = v; } }

    static ListNode mergeKLists(ListNode[] lists) {
        java.util.PriorityQueue<ListNode> minHeap =
                new java.util.PriorityQueue<>((a, b) -> a.val - b.val);

        for (ListNode node : lists) {
            if (node != null) minHeap.offer(node);
        }

        ListNode dummy = new ListNode(0), curr = dummy;
        while (!minHeap.isEmpty()) {
            ListNode node = minHeap.poll();
            curr.next = node;
            curr = curr.next;
            if (node.next != null) minHeap.offer(node.next);
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        System.out.println("=== Queue, Deque & Priority Queue ===\n");

        // MyQueue (circular)
        MyQueue q = new MyQueue(5);
        q.enqueue(1); q.enqueue(2); q.enqueue(3);
        System.out.println("MyQueue: peek=" + q.peek() + " dequeue=" + q.dequeue() + " size=" + q.size());

        // Queue with 2 Stacks
        System.out.println("\n--- Queue with 2 Stacks ---");
        MyQueueWith2Stacks mq = new MyQueueWith2Stacks();
        mq.push(1); mq.push(2); mq.push(3);
        System.out.println("peek: " + mq.peek() + " | pop: " + mq.pop() + " | peek: " + mq.peek());

        // Stack with 2 Queues
        System.out.println("\n--- Stack with 2 Queues ---");
        MyStackWith2Queues sq = new MyStackWith2Queues();
        sq.push(1); sq.push(2); sq.push(3);
        System.out.println("top: " + sq.top() + " | pop: " + sq.pop() + " | top: " + sq.top());

        // Sliding Window Maximum
        System.out.println("\n--- Sliding Window Maximum (Deque) ---");
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int[] maxes = maxSlidingWindow(nums, 3);
        System.out.print("[1,3,-1,-3,5,3,6,7] k=3 → ");
        for (int m : maxes) System.out.print(m + " "); // 3 3 5 5 6 7
        System.out.println();

        // Priority Queue
        System.out.println("\n--- Priority Queue ---");
        System.out.println("findKthLargest([3,2,1,5,6,4], k=2) = "
                + findKthLargest(new int[]{3,2,1,5,6,4}, 2)); // 5

        int[] topK = topKFrequent(new int[]{1,1,1,2,2,3}, 2);
        System.out.print("topKFrequent([1,1,1,2,2,3], k=2) = ");
        for (int x : topK) System.out.print(x + " "); // 1 2
        System.out.println();

        // PQ so sánh Java API
        System.out.println("\n--- PriorityQueue Java API ---");
        java.util.PriorityQueue<Integer> minHeap = new java.util.PriorityQueue<>();
        java.util.PriorityQueue<Integer> maxHeap =
                new java.util.PriorityQueue<>(java.util.Collections.reverseOrder());
        int[] arr = {5, 1, 3, 2, 4};
        for (int x : arr) { minHeap.offer(x); maxHeap.offer(x); }
        System.out.println("minHeap.peek() = " + minHeap.peek()); // 1
        System.out.println("maxHeap.peek() = " + maxHeap.peek()); // 5
    }
}
