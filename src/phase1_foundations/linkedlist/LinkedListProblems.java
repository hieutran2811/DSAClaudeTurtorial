package phase1_foundations.linkedlist;

/**
 * PHASE 1.3 — Classic Linked List Problems
 *
 * Bao gồm:
 *   1. Cycle Detection — Floyd's Algorithm
 *   2. Find Middle Node (đã học trong SinglyLinkedList)
 *   3. Các bài LeetCode quan trọng
 */
public class LinkedListProblems {

    static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    // =========================================================
    // 1. CYCLE DETECTION — Floyd's Tortoise & Hare
    // =========================================================

    /**
     * Bài: Linked List Cycle (LeetCode #141)
     * Kiểm tra danh sách có vòng lặp không?
     *
     * Ý tưởng: slow đi 1 bước, fast đi 2 bước
     *   - Không có cycle: fast đến null trước
     *   - Có cycle: fast và slow sẽ gặp nhau trong cycle
     *
     * Tại sao chúng gặp nhau?
     *   Mỗi bước, khoảng cách giữa fast và slow tăng 1
     *   Trong vòng tròn, khoảng cách cuối cùng = 0 (mod cycle_length)
     *
     * Time: O(n) | Space: O(1)
     */
    static boolean hasCycle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true; // gặp nhau → có cycle
        }
        return false;
    }

    /**
     * Bài: Linked List Cycle II (LeetCode #142)
     * Tìm NODE bắt đầu của cycle (entry point)
     *
     * Chứng minh toán học:
     *   Gọi F = khoảng từ head đến entry
     *        C = chu vi cycle
     *        h = khoảng từ entry đến điểm gặp
     *
     *   Khi gặp nhau: slow đi F+h, fast đi F+h+n*C
     *   fast = 2*slow → F+h+n*C = 2(F+h) → F = n*C - h
     *
     *   → Reset 1 pointer về head, giữ 1 tại điểm gặp
     *     Cả 2 đi 1 bước/lần → gặp nhau tại entry!
     */
    static Node detectCycleStart(Node head) {
        Node slow = head, fast = head;

        // Bước 1: Tìm điểm gặp nhau
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) break;
        }
        if (fast == null || fast.next == null) return null; // không có cycle

        // Bước 2: Reset slow về head, tiến cả 2 với tốc độ = 1
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        return slow; // entry point của cycle
    }

    // =========================================================
    // 2. REMOVE Nth NODE FROM END (LeetCode #19)
    // =========================================================

    /**
     * Xóa node thứ n tính từ cuối — 1 lần duyệt (O(n))
     *
     * Kỹ thuật: 2 con trỏ cách nhau n bước
     *   fast đi trước n bước → khi fast đến cuối,
     *   slow đang ở vị trí cần xóa
     *
     * [dummy → 1 → 2 → 3 → 4 → 5], n=2
     *  slow=dummy, fast đi 2 bước → fast tại node 2
     *  Cả 2 cùng đi đến fast.next = null
     *  slow dừng tại node 3 → slow.next = node 4 (xóa node 4)
     */
    static Node removeNthFromEnd(Node head, int n) {
        Node dummy = new Node(0);
        dummy.next = head;
        Node slow = dummy, fast = dummy;

        // fast đi trước n+1 bước (để slow dừng tại node TRƯỚC node cần xóa)
        for (int i = 0; i <= n; i++) fast = fast.next;

        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }
        slow.next = slow.next.next; // xóa node
        return dummy.next;
    }

    // =========================================================
    // 3. REORDER LIST (LeetCode #143)
    // =========================================================

    /**
     * L0 → L1 → L2 → ... → Ln
     * thành: L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → ...
     *
     * Kỹ thuật: 3 bước
     *   1. Tìm middle (slow/fast pointer)
     *   2. Reverse nửa sau
     *   3. Merge 2 nửa xen kẽ
     */
    static void reorderList(Node head) {
        if (head == null || head.next == null) return;

        // Bước 1: Tìm middle
        Node slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        Node secondHalf = slow.next;
        slow.next = null; // cắt đôi list

        // Bước 2: Reverse nửa sau
        Node prev = null, curr = secondHalf;
        while (curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        secondHalf = prev;

        // Bước 3: Merge xen kẽ
        Node first = head, second = secondHalf;
        while (second != null) {
            Node nextFirst  = first.next;
            Node nextSecond = second.next;
            first.next  = second;
            second.next = nextFirst;
            first  = nextFirst;
            second = nextSecond;
        }
    }

    // =========================================================
    // 4. PALINDROME LINKED LIST (LeetCode #234)
    // =========================================================

    /**
     * Kiểm tra linked list có phải palindrome — O(n) time O(1) space
     *
     * Kỹ thuật:
     *   1. Tìm middle
     *   2. Reverse nửa sau
     *   3. So sánh nửa trước và nửa sau đã reverse
     *   4. (Optional) Restore list về trạng thái ban đầu
     */
    static boolean isPalindrome(Node head) {
        // Tìm middle
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reverse nửa sau
        Node prev = null, curr = slow;
        while (curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // So sánh
        Node left = head, right = prev;
        while (right != null) {
            if (left.val != right.val) return false;
            left  = left.next;
            right = right.next;
        }
        return true;
    }

    // =========================================================
    // 5. ADD TWO NUMBERS (LeetCode #2)
    // =========================================================

    /**
     * Hai số biểu diễn bằng linked list (chữ số ngược)
     * 2 → 4 → 3  (342) + 5 → 6 → 4  (465) = 7 → 0 → 8  (807)
     *
     * Giống phép cộng tay: xử lý carry
     */
    static Node addTwoNumbers(Node l1, Node l2) {
        Node dummy = new Node(0);
        Node curr = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) { sum += l1.val; l1 = l1.next; }
            if (l2 != null) { sum += l2.val; l2 = l2.next; }
            carry = sum / 10;
            curr.next = new Node(sum % 10);
            curr = curr.next;
        }
        return dummy.next;
    }

    // =========================================================
    // HELPERS
    // =========================================================
    static Node createList(int... vals) {
        Node dummy = new Node(0), curr = dummy;
        for (int v : vals) { curr.next = new Node(v); curr = curr.next; }
        return dummy.next;
    }

    static void printList(Node head) {
        StringBuilder sb = new StringBuilder();
        while (head != null) {
            sb.append(head.val);
            if (head.next != null) sb.append(" → ");
            head = head.next;
        }
        sb.append(" → NULL");
        System.out.println(sb);
    }

    // Tạo cycle tại vị trí pos để test
    static Node createCycleList(int[] vals, int pos) {
        Node dummy = new Node(0), curr = dummy;
        Node[] nodes = new Node[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new Node(vals[i]);
            curr.next = nodes[i];
            curr = curr.next;
        }
        if (pos >= 0) curr.next = nodes[pos]; // tạo cycle
        return dummy.next;
    }

    public static void main(String[] args) {
        System.out.println("=== Linked List Problems ===\n");

        // Cycle Detection
        System.out.println("--- Floyd's Cycle Detection ---");
        Node cycleList  = createCycleList(new int[]{3, 2, 0, -4}, 1);
        Node noCycle    = createList(1, 2, 3, 4, 5);
        System.out.println("hasCycle (pos=1): " + hasCycle(cycleList));    // true
        System.out.println("hasCycle (none):  " + hasCycle(noCycle));      // false

        Node entry = detectCycleStart(createCycleList(new int[]{3, 2, 0, -4}, 1));
        System.out.println("cycleStart val:   " + (entry != null ? entry.val : "null")); // 2

        // Remove Nth from End
        System.out.println("\n--- Remove Nth From End ---");
        Node list = createList(1, 2, 3, 4, 5);
        System.out.print("Before: "); printList(list);
        System.out.print("After removeNth(2): ");
        printList(removeNthFromEnd(list, 2)); // 1→2→3→5

        // Reorder List
        System.out.println("\n--- Reorder List ---");
        Node reorder = createList(1, 2, 3, 4, 5);
        System.out.print("Before: "); printList(reorder);
        reorderList(reorder);
        System.out.print("After:  "); printList(reorder); // 1→5→2→4→3

        // Palindrome
        System.out.println("\n--- Palindrome ---");
        System.out.println("isPalindrome [1,2,2,1]: " + isPalindrome(createList(1, 2, 2, 1))); // true
        System.out.println("isPalindrome [1,2,3]:   " + isPalindrome(createList(1, 2, 3)));     // false
        System.out.println("isPalindrome [1,2,1]:   " + isPalindrome(createList(1, 2, 1)));     // true

        // Add Two Numbers
        System.out.println("\n--- Add Two Numbers (342 + 465 = 807) ---");
        Node l1 = createList(2, 4, 3); // 342
        Node l2 = createList(5, 6, 4); // 465
        System.out.print("Result: "); printList(addTwoNumbers(l1, l2)); // 7→0→8
    }
}
