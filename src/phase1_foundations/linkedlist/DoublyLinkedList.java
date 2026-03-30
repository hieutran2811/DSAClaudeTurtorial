package phase1_foundations.linkedlist;

/**
 * PHASE 1.3 — Doubly Linked List
 *
 * Mỗi node có 2 con trỏ: prev và next
 * NULL ← [prev|data|next] ↔ [prev|data|next] → NULL
 *
 * Ưu điểm so với Singly:
 *   + Duyệt 2 chiều
 *   + Xóa node O(1) nếu đã có con trỏ đến node đó (không cần tìm prev)
 *   + Dễ implement LRU Cache, Browser history, ...
 *
 * Nhược điểm: tốn thêm 1 con trỏ (prev) mỗi node
 */
public class DoublyLinkedList {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
    }

    static class DLinkedList {
        Node head, tail;
        int size;

        // INSERT đầu — O(1)
        void addFirst(int val) {
            Node node = new Node(val);
            if (head == null) { head = tail = node; size++; return; }
            node.next = head;
            head.prev = node;
            head = node;
            size++;
        }

        // INSERT cuối — O(1) nhờ có tail
        void addLast(int val) {
            Node node = new Node(val);
            if (tail == null) { head = tail = node; size++; return; }
            tail.next = node;
            node.prev = tail;
            tail = node;
            size++;
        }

        // DELETE node bất kỳ — O(1) nếu đã có con trỏ đến node
        // Đây là lợi thế lớn nhất của Doubly so với Singly
        void deleteNode(Node node) {
            if (node.prev != null) node.prev.next = node.next;
            else head = node.next; // node là head

            if (node.next != null) node.next.prev = node.prev;
            else tail = node.prev; // node là tail

            node.prev = node.next = null; // GC
            size--;
        }

        // DELETE đầu — O(1)
        int removeFirst() {
            if (head == null) throw new RuntimeException("Empty");
            int val = head.val;
            deleteNode(head);
            return val;
        }

        // DELETE cuối — O(1) nhờ có tail
        int removeLast() {
            if (tail == null) throw new RuntimeException("Empty");
            int val = tail.val;
            deleteNode(tail);
            return val;
        }

        void printForward() {
            Node curr = head;
            System.out.print("HEAD ↔ ");
            while (curr != null) {
                System.out.print(curr.val + (curr.next != null ? " ↔ " : ""));
                curr = curr.next;
            }
            System.out.println(" ↔ NULL (size=" + size + ")");
        }

        void printBackward() {
            Node curr = tail;
            System.out.print("TAIL ↔ ");
            while (curr != null) {
                System.out.print(curr.val + (curr.prev != null ? " ↔ " : ""));
                curr = curr.prev;
            }
            System.out.println(" ↔ NULL");
        }
    }

    // =========================================================
    // Ứng dụng thực tế: Browser History
    // Dùng Doubly Linked List để navigate back/forward
    // =========================================================
    static class BrowserHistory {
        Node current;

        BrowserHistory(String homepage) {
            current = new Node(0); // dùng val làm id, url lưu riêng
            current.val = homepage.hashCode(); // simplified
            System.out.println("Homepage: " + homepage);
        }

        void visit(String url) {
            Node page = new Node(url.hashCode());
            page.prev = current;
            current.next = page; // xóa forward history
            current = page;
            System.out.println("Visited: " + url);
        }

        // back(steps) — đi lùi tối đa steps bước
        void back(int steps) {
            while (steps > 0 && current.prev != null) {
                current = current.prev;
                steps--;
            }
            System.out.println("Back → node hash: " + current.val);
        }

        // forward(steps) — đi tới tối đa steps bước
        void forward(int steps) {
            while (steps > 0 && current.next != null) {
                current = current.next;
                steps--;
            }
            System.out.println("Forward → node hash: " + current.val);
        }
    }

    // =========================================================
    // Ứng dụng: LRU Cache skeleton dùng Doubly LinkedList + HashMap
    // (Bài đầy đủ sẽ học ở Phase 6.4)
    // =========================================================
    /*
     * HashMap<key, Node>  → O(1) lookup
     * DoublyLinkedList    → O(1) move-to-front và evict từ tail
     *
     * get(key)  : tìm trong map, move node lên head → O(1)
     * put(key)  : thêm vào head, nếu full thì xóa tail → O(1)
     */

    public static void main(String[] args) {
        System.out.println("=== Doubly Linked List ===\n");

        DLinkedList dll = new DLinkedList();
        dll.addLast(1);
        dll.addLast(2);
        dll.addLast(3);
        dll.addFirst(0);
        dll.printForward();  // 0 ↔ 1 ↔ 2 ↔ 3
        dll.printBackward(); // 3 ↔ 2 ↔ 1 ↔ 0

        System.out.println("removeFirst: " + dll.removeFirst()); // 0
        System.out.println("removeLast:  " + dll.removeLast());  // 3
        dll.printForward(); // 1 ↔ 2

        System.out.println("\n--- Browser History ---");
        BrowserHistory browser = new BrowserHistory("google.com");
        browser.visit("youtube.com");
        browser.visit("github.com");
        browser.back(1);    // github → youtube
        browser.back(1);    // youtube → google
        browser.forward(1); // google → youtube
        browser.visit("stackoverflow.com"); // xóa forward history (github)
        browser.back(10);   // về đầu (google)
    }
}
