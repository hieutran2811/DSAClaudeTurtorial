package phase1_foundations.linkedlist;

/**
 * PHASE 1.3 — Singly Linked List
 *
 * So sánh với Array:
 *   Array       : O(1) access by index, O(n) insert/delete giữa
 *   LinkedList  : O(n) access by index, O(1) insert/delete tại node đã biết
 *
 * Cấu trúc: [data | next] → [data | next] → [data | null]
 *            head                              tail
 */
public class SinglyLinkedList {

    // =========================================================
    // Node — đơn vị cơ bản
    // =========================================================
    static class Node {
        int val;
        Node next;

        Node(int val) { this.val = val; }
    }

    // =========================================================
    // Các thao tác cơ bản
    // =========================================================
    static class LinkedList {
        Node head;
        int size;

        // INSERT đầu — O(1)
        void addFirst(int val) {
            Node node = new Node(val);
            node.next = head;
            head = node;
            size++;
        }

        // INSERT cuối — O(n)
        void addLast(int val) {
            Node node = new Node(val);
            if (head == null) { head = node; size++; return; }
            Node curr = head;
            while (curr.next != null) curr = curr.next; // đi đến cuối
            curr.next = node;
            size++;
        }

        // INSERT tại index — O(n)
        void addAt(int index, int val) {
            if (index == 0) { addFirst(val); return; }
            Node curr = head;
            for (int i = 0; i < index - 1; i++) {
                if (curr == null) throw new IndexOutOfBoundsException();
                curr = curr.next;
            }
            Node node = new Node(val);
            node.next = curr.next;
            curr.next = node;
            size++;
        }

        // DELETE đầu — O(1)
        int removeFirst() {
            if (head == null) throw new RuntimeException("List is empty");
            int val = head.val;
            head = head.next;
            size--;
            return val;
        }

        // DELETE cuối — O(n)
        int removeLast() {
            if (head == null) throw new RuntimeException("List is empty");
            if (head.next == null) { int val = head.val; head = null; size--; return val; }
            Node curr = head;
            while (curr.next.next != null) curr = curr.next;
            int val = curr.next.val;
            curr.next = null;
            size--;
            return val;
        }

        // DELETE theo value — O(n)
        boolean remove(int val) {
            if (head == null) return false;
            if (head.val == val) { head = head.next; size--; return true; }
            Node curr = head;
            while (curr.next != null) {
                if (curr.next.val == val) {
                    curr.next = curr.next.next; // bỏ qua node cần xóa
                    size--;
                    return true;
                }
                curr = curr.next;
            }
            return false;
        }

        // SEARCH — O(n)
        boolean contains(int val) {
            Node curr = head;
            while (curr != null) {
                if (curr.val == val) return true;
                curr = curr.next;
            }
            return false;
        }

        // GET tại index — O(n)
        int get(int index) {
            Node curr = head;
            for (int i = 0; i < index; i++) {
                if (curr == null) throw new IndexOutOfBoundsException();
                curr = curr.next;
            }
            return curr.val;
        }

        // IN danh sách
        void print() {
            Node curr = head;
            StringBuilder sb = new StringBuilder("HEAD → ");
            while (curr != null) {
                sb.append(curr.val);
                if (curr.next != null) sb.append(" → ");
                curr = curr.next;
            }
            sb.append(" → NULL (size=").append(size).append(")");
            System.out.println(sb);
        }
    }

    // =========================================================
    // REVERSE Linked List (LeetCode #206) — Quan trọng!
    // =========================================================

    /**
     * Iterative Reverse — O(n) time, O(1) space
     *
     * Ý tưởng: dùng 3 con trỏ prev, curr, next
     *
     * Before: NULL ← ... prev  curr → next → ...
     * Step  : prev ← curr, rồi tiến cả 3 con trỏ
     * After : NULL ← 1 ← 2 ← 3 ← 4 ← 5 (head mới = prev)
     */
    static Node reverseIterative(Node head) {
        Node prev = null, curr = head;
        while (curr != null) {
            Node nextTemp = curr.next; // lưu node tiếp theo
            curr.next = prev;          // đảo chiều mũi tên
            prev = curr;               // tiến prev
            curr = nextTemp;           // tiến curr
        }
        return prev; // prev là head mới
    }

    /**
     * Recursive Reverse — O(n) time, O(n) space (call stack)
     *
     * reverseRec(1→2→3→4→5):
     *   reverseRec(2→3→4→5) trả về head mới = 5
     *   Sau đó: 1.next.next = 1  (tức 2.next = 1)
     *            1.next = null
     */
    static Node reverseRecursive(Node head) {
        if (head == null || head.next == null) return head;
        Node newHead = reverseRecursive(head.next);
        head.next.next = head; // node sau trỏ lại node hiện tại
        head.next = null;
        return newHead;
    }

    // =========================================================
    // MERGE TWO SORTED LISTS (LeetCode #21)
    // =========================================================

    /**
     * Iterative — dùng dummy node để đơn giản code
     * Dummy node: tránh xử lý edge case khi head = null
     */
    static Node mergeSortedIterative(Node l1, Node l2) {
        Node dummy = new Node(0); // sentinel node
        Node curr = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) { curr.next = l1; l1 = l1.next; }
            else                  { curr.next = l2; l2 = l2.next; }
            curr = curr.next;
        }
        curr.next = (l1 != null) ? l1 : l2; // gắn phần còn lại
        return dummy.next;
    }

    /** Recursive — code ngắn gọn hơn */
    static Node mergeSortedRecursive(Node l1, Node l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        if (l1.val <= l2.val) {
            l1.next = mergeSortedRecursive(l1.next, l2);
            return l1;
        } else {
            l2.next = mergeSortedRecursive(l1, l2.next);
            return l2;
        }
    }

    // =========================================================
    // FIND MIDDLE NODE (LeetCode #876)
    // =========================================================

    /**
     * Fast & Slow pointer:
     * fast đi 2 bước, slow đi 1 bước
     * Khi fast đến cuối → slow đang ở giữa
     *
     * 1 → 2 → 3 → 4 → 5
     *                 F        (fast)
     *         S               (slow = middle)
     */
    static Node findMiddle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // =========================================================
    // HELPER — tạo danh sách từ array
    // =========================================================
    static Node createList(int... vals) {
        Node dummy = new Node(0);
        Node curr = dummy;
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

    public static void main(String[] args) {
        System.out.println("=== Singly Linked List ===\n");

        // Thao tác cơ bản
        LinkedList list = new LinkedList();
        list.addLast(1); list.addLast(2); list.addLast(3);
        list.addFirst(0);
        list.addAt(2, 99);
        list.print(); // 0 → 1 → 99 → 2 → 3

        list.removeFirst();
        list.removeLast();
        list.remove(99);
        list.print(); // 1 → 2

        System.out.println("contains(2): " + list.contains(2));
        System.out.println("get(1): " + list.get(1));

        // Reverse
        System.out.println("\n--- Reverse ---");
        Node n1 = createList(1, 2, 3, 4, 5);
        System.out.print("Original:          "); printList(n1);
        System.out.print("reverseIterative:  "); printList(reverseIterative(n1));

        Node n2 = createList(1, 2, 3, 4, 5);
        System.out.print("reverseRecursive:  "); printList(reverseRecursive(n2));

        // Merge
        System.out.println("\n--- Merge Sorted ---");
        Node l1 = createList(1, 3, 5, 7);
        Node l2 = createList(2, 4, 6);
        System.out.print("L1: "); printList(l1);
        System.out.print("L2: "); printList(l2);
        System.out.print("Merged: "); printList(mergeSortedIterative(
                createList(1, 3, 5, 7), createList(2, 4, 6)));

        // Find Middle
        System.out.println("\n--- Find Middle ---");
        Node odd  = createList(1, 2, 3, 4, 5);
        Node even = createList(1, 2, 3, 4);
        System.out.println("Middle of [1,2,3,4,5]: " + findMiddle(odd).val);  // 3
        System.out.println("Middle of [1,2,3,4]:   " + findMiddle(even).val); // 3 (second middle)
    }
}
