package phase2_core.trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * PHASE 2.2 — Binary Search Tree (BST)
 *
 * BST Property: left.val < root.val < right.val (với mọi node)
 *
 * Operations:
 *   Search  : O(h) — h = height
 *   Insert  : O(h)
 *   Delete  : O(h)
 *   h = O(log n) nếu cân bằng, O(n) nếu skewed (worst case)
 *
 * Inorder traversal của BST → sorted array
 */
public class BinarySearchTree {

    static class Node {
        int val;
        Node left, right;
        Node(int val) { this.val = val; }
    }

    // =========================================================
    // BST Operations
    // =========================================================

    /** Search — O(log n) avg */
    static Node search(Node root, int target) {
        if (root == null || root.val == target) return root;
        if (target < root.val) return search(root.left, target);
        return search(root.right, target);
    }

    /** Search Iterative — O(1) space */
    static Node searchIter(Node root, int target) {
        while (root != null && root.val != target) {
            root = target < root.val ? root.left : root.right;
        }
        return root;
    }

    /** Insert — luôn insert vào leaf */
    static Node insert(Node root, int val) {
        if (root == null) return new Node(val);
        if (val < root.val) root.left  = insert(root.left, val);
        else if (val > root.val) root.right = insert(root.right, val);
        // val == root.val → BST không có duplicate, bỏ qua
        return root;
    }

    /**
     * Delete — 3 trường hợp:
     *   1. Node là lá → xóa trực tiếp
     *   2. Node có 1 con → thay bằng con đó
     *   3. Node có 2 con → thay bằng inorder successor (node nhỏ nhất bên phải)
     */
    static Node delete(Node root, int val) {
        if (root == null) return null;
        if (val < root.val) {
            root.left  = delete(root.left, val);
        } else if (val > root.val) {
            root.right = delete(root.right, val);
        } else {
            // Tìm thấy node cần xóa
            if (root.left  == null) return root.right; // case 1 & 2
            if (root.right == null) return root.left;  // case 2

            // Case 3: tìm inorder successor (min của subtree phải)
            Node successor = findMin(root.right);
            root.val   = successor.val;                // copy giá trị
            root.right = delete(root.right, successor.val); // xóa successor
        }
        return root;
    }

    static Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    static Node findMax(Node node) {
        while (node.right != null) node = node.right;
        return node;
    }

    // =========================================================
    // BST Validation & Properties
    // =========================================================

    /**
     * Validate BST (LeetCode #98)
     * KHÔNG chỉ so sánh root với left/right — phải kiểm tra toàn bộ range
     *
     * Sai lầm phổ biến:
     *       5
     *      / \
     *     1   4      ← 4 < 5 đúng với node 5
     *        / \
     *       3   6    ← 3 < 5 nhưng 3 > 4? → không hợp lệ!
     *
     * Đúng: truyền min, max bound qua mọi node
     */
    static boolean isValidBST(Node root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    static boolean validate(Node node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return validate(node.left,  min, node.val)   // left: val < node.val
            && validate(node.right, node.val, max);  // right: val > node.val
    }

    /**
     * Validate bằng Inorder — BST hợp lệ → inorder phải tăng dần
     */
    static boolean isValidBSTInorder(Node root) {
        long prev = Long.MIN_VALUE;
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = root;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) { stack.push(curr); curr = curr.left; }
            curr = stack.pop();
            if (curr.val <= prev) return false; // không tăng → invalid
            prev = curr.val;
            curr = curr.right;
        }
        return true;
    }

    // =========================================================
    // BST Problems
    // =========================================================

    /**
     * Kth Smallest Element in BST (LeetCode #230)
     * Inorder traversal → dừng tại phần tử thứ k
     */
    static int kthSmallest(Node root, int k) {
        int[] count = {0}, result = {0};
        inorderKth(root, k, count, result);
        return result[0];
    }

    static void inorderKth(Node node, int k, int[] count, int[] result) {
        if (node == null) return;
        inorderKth(node.left, k, count, result);
        if (++count[0] == k) { result[0] = node.val; return; }
        inorderKth(node.right, k, count, result);
    }

    /**
     * Lowest Common Ancestor of BST (LeetCode #235)
     * Lợi dụng BST property: không cần duyệt toàn cây
     *   - Cả 2 node bên trái → LCA ở left subtree
     *   - Cả 2 node bên phải → LCA ở right subtree
     *   - Khác phía → root là LCA
     */
    static Node lcaBST(Node root, Node p, Node q) {
        if (p.val < root.val && q.val < root.val) return lcaBST(root.left, p, q);
        if (p.val > root.val && q.val > root.val) return lcaBST(root.right, p, q);
        return root; // root nằm giữa p và q
    }

    /**
     * Convert Sorted Array to BST (LeetCode #108)
     * → tạo BST cân bằng từ mảng đã sort
     * Luôn chọn phần tử giữa làm root → cân bằng
     */
    static Node sortedArrayToBST(int[] nums) {
        return buildBST(nums, 0, nums.length - 1);
    }

    static Node buildBST(int[] nums, int left, int right) {
        if (left > right) return null;
        int mid = left + (right - left) / 2;
        Node node = new Node(nums[mid]);
        node.left  = buildBST(nums, left,    mid - 1);
        node.right = buildBST(nums, mid + 1, right);
        return node;
    }

    /**
     * BST Iterator (LeetCode #173)
     * next() và hasNext() trong O(1) avg, O(h) space
     * Dùng stack lưu trạng thái inorder traversal
     */
    static class BSTIterator {
        private Deque<Node> stack = new ArrayDeque<>();

        BSTIterator(Node root) { pushLeft(root); }

        /** O(1) amortized */
        int next() {
            Node node = stack.pop();
            pushLeft(node.right); // chuẩn bị nhánh phải
            return node.val;
        }

        boolean hasNext() { return !stack.isEmpty(); }

        private void pushLeft(Node node) {
            while (node != null) { stack.push(node); node = node.left; }
        }
    }

    /**
     * Recover BST (LeetCode #99) — Medium/Hard
     * Đúng 2 node bị swap → tìm và khôi phục
     * Inorder: tìm 2 vị trí mà thứ tự bị đảo ngược
     */
    static void recoverTree(Node root) {
        Node[] first = {null}, second = {null}, prev = {null};

        // Inorder tìm 2 node sai
        inorderRecover(root, first, second, prev);

        // Swap giá trị
        int tmp = first[0].val;
        first[0].val  = second[0].val;
        second[0].val = tmp;
    }

    static void inorderRecover(Node node, Node[] first, Node[] second, Node[] prev) {
        if (node == null) return;
        inorderRecover(node.left, first, second, prev);

        if (prev[0] != null && prev[0].val > node.val) {
            if (first[0] == null) first[0] = prev[0]; // lần vi phạm đầu tiên
            second[0] = node;                          // luôn cập nhật second
        }
        prev[0] = node;
        inorderRecover(node.right, first, second, prev);
    }

    // =========================================================
    // HELPERS
    // =========================================================
    static void printInorder(Node root) {
        List<Integer> res = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = root;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) { stack.push(curr); curr = curr.left; }
            curr = stack.pop();
            res.add(curr.val);
            curr = curr.right;
        }
        System.out.println("Inorder: " + res);
    }

    static Node buildBST() {
        //       5
        //      / \
        //     3   7
        //    / \ / \
        //   2  4 6  8
        Node root = new Node(5);
        root.left  = new Node(3);
        root.right = new Node(7);
        root.left.left   = new Node(2);
        root.left.right  = new Node(4);
        root.right.left  = new Node(6);
        root.right.right = new Node(8);
        return root;
    }

    public static void main(String[] args) {
        System.out.println("=== Binary Search Tree ===\n");

        Node bst = buildBST();
        printInorder(bst); // [2,3,4,5,6,7,8]

        // Search
        System.out.println("\n--- Search ---");
        System.out.println("search(6): " + (search(bst, 6) != null ? "found" : "not found")); // found
        System.out.println("search(9): " + (search(bst, 9) != null ? "found" : "not found")); // not found

        // Insert
        System.out.println("\n--- Insert ---");
        bst = insert(bst, 1);
        bst = insert(bst, 9);
        printInorder(bst); // [1,2,3,4,5,6,7,8,9]

        // Delete
        System.out.println("\n--- Delete ---");
        bst = delete(bst, 3); // node có 2 con → successor = 4
        printInorder(bst); // [1,2,4,5,6,7,8,9]
        bst = delete(bst, 1); // lá
        printInorder(bst); // [2,4,5,6,7,8,9]

        // Validate
        System.out.println("\n--- Validate BST ---");
        Node valid = buildBST();
        Node invalid = new Node(5);
        invalid.left = new Node(1);
        invalid.right = new Node(4);
        invalid.right.left  = new Node(3);
        invalid.right.right = new Node(6);
        System.out.println("valid BST:   " + isValidBST(valid));    // true
        System.out.println("invalid BST: " + isValidBST(invalid));  // false
        System.out.println("valid (inorder): " + isValidBSTInorder(buildBST())); // true

        // Kth Smallest
        System.out.println("\n--- Kth Smallest ---");
        System.out.println("kthSmallest(k=3): " + kthSmallest(buildBST(), 3)); // 4

        // LCA of BST
        System.out.println("\n--- LCA of BST ---");
        Node b = buildBST();
        System.out.println("LCA(2,4): " + lcaBST(b, new Node(2), new Node(4)).val); // 3
        System.out.println("LCA(2,8): " + lcaBST(b, new Node(2), new Node(8)).val); // 5

        // Sorted Array to BST
        System.out.println("\n--- Sorted Array to Balanced BST ---");
        Node balanced = sortedArrayToBST(new int[]{-10,-3,0,5,9});
        printInorder(balanced); // [-10,-3,0,5,9]
        System.out.println("isBalanced: " + BinaryTree.isBalanced(
                new BinaryTree.Node(balanced.val))); // simplified check

        // BST Iterator
        System.out.println("\n--- BST Iterator ---");
        BSTIterator it = new BSTIterator(buildBST());
        System.out.print("Iterator: ");
        while (it.hasNext()) System.out.print(it.next() + " "); // 2 3 4 5 6 7 8
        System.out.println();
    }
}
