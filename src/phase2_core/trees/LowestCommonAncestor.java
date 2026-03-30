package phase2_core.trees;

import java.util.*;

/**
 * PHASE 2.2 — Lowest Common Ancestor (LCA)
 *
 * LCA(u, v) = node sâu nhất là tổ tiên chung của cả u và v
 *
 *        3
 *       / \
 *      5   1
 *     / \ / \
 *    6  2 0  8
 *      / \
 *     7   4
 *
 * LCA(5, 1) = 3
 * LCA(5, 4) = 5  (node là tổ tiên của chính nó)
 * LCA(6, 4) = 5
 */
public class LowestCommonAncestor {

    static class Node {
        int val;
        Node left, right, parent; // parent dùng cho variant
        Node(int val) { this.val = val; }
    }

    // =========================================================
    // LCA — Binary Tree (LeetCode #236)
    // =========================================================

    /**
     * Approach 1: Recursive DFS — O(n) time, O(h) space
     *
     * Logic:
     *   - Nếu root là null → return null
     *   - Nếu root == p hoặc root == q → return root (tìm thấy)
     *   - Đệ quy sang left và right
     *   - Nếu cả 2 đều non-null → root là LCA (p và q ở 2 phía)
     *   - Nếu chỉ 1 bên non-null → LCA ở phía đó
     */
    static Node lcaRecursive(Node root, Node p, Node q) {
        if (root == null || root == p || root == q) return root;

        Node left  = lcaRecursive(root.left,  p, q);
        Node right = lcaRecursive(root.right, p, q);

        if (left != null && right != null) return root; // p và q ở 2 phía
        return left != null ? left : right;             // cả 2 ở cùng 1 phía
    }

    /**
     * Approach 2: Iterative với Parent Map — O(n) time, O(n) space
     *
     * Bước 1: BFS/DFS để build map<node, parent>
     * Bước 2: Lưu tất cả ancestors của p vào Set
     * Bước 3: Duyệt từ q lên theo parent, gặp ancestor của p → LCA
     */
    static Node lcaIterative(Node root, Node p, Node q) {
        Map<Node, Node> parentMap = new HashMap<>();
        parentMap.put(root, null);

        // DFS để điền parent map
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!parentMap.containsKey(p) || !parentMap.containsKey(q)) {
            Node node = stack.pop();
            if (node.left  != null) { parentMap.put(node.left,  node); stack.push(node.left);  }
            if (node.right != null) { parentMap.put(node.right, node); stack.push(node.right); }
        }

        // Tập tổ tiên của p
        Set<Node> ancestors = new HashSet<>();
        while (p != null) { ancestors.add(p); p = parentMap.get(p); }

        // Tìm tổ tiên đầu tiên của q cũng là tổ tiên của p
        while (!ancestors.contains(q)) q = parentMap.get(q);
        return q;
    }

    // =========================================================
    // LCA với node có pointer lên parent (LeetCode #1650)
    // =========================================================

    /**
     * Giống bài "Intersection of Two Linked Lists":
     * 2 con trỏ cùng tốc độ, khi 1 đến null thì chuyển sang đầu của chuỗi kia
     * → gặp nhau tại LCA
     */
    static Node lcaWithParent(Node p, Node q) {
        Node a = p, b = q;
        while (a != b) {
            a = (a == null) ? q : a.parent;
            b = (b == null) ? p : b.parent;
        }
        return a;
    }

    // =========================================================
    // LCA — Multiple Nodes (LeetCode #1676)
    // =========================================================

    /**
     * LCA của nhiều node trong Set
     * Tương tự LCA 2 node nhưng kiểm tra với Set
     */
    static Node lcaMultiple(Node root, Set<Integer> targets) {
        if (root == null) return null;
        if (targets.contains(root.val)) return root; // tìm thấy 1 target

        Node left  = lcaMultiple(root.left,  targets);
        Node right = lcaMultiple(root.right, targets);

        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    // =========================================================
    // APPLICATIONS: Khoảng cách giữa 2 node
    // =========================================================

    /**
     * Distance Between Two Nodes
     * dist(p, q) = depth(p) + depth(q) - 2 * depth(LCA(p, q))
     */
    static int distBetweenNodes(Node root, int p, int q) {
        int[] result = {0};
        findLCAAndDepth(root, p, q, result);
        return result[0];
    }

    // Trả về depth của node nếu tìm thấy p hoặc q, -1 nếu không
    static int findLCAAndDepth(Node node, int p, int q, int[] result) {
        if (node == null) return -1;

        int left  = findLCAAndDepth(node.left,  p, q, result);
        int right = findLCAAndDepth(node.right, p, q, result);

        if (node.val == p || node.val == q) {
            // Nếu bên kia cũng tìm thấy → tính khoảng cách
            int otherDepth = Math.max(left, right);
            if (otherDepth != -1) result[0] = otherDepth + 1;
            return 0;
        }

        if (left != -1 && right != -1) {
            // LCA tìm thấy ở đây
            result[0] = left + right + 2;
            return -1; // đã tính xong
        }

        if (left != -1)  return left  + 1;
        if (right != -1) return right + 1;
        return -1;
    }

    // =========================================================
    // HELPERS
    // =========================================================
    static Node buildTree() {
        //        3
        //       / \
        //      5   1
        //     / \ / \
        //    6  2 0  8
        //      / \
        //     7   4
        Node root = new Node(3);
        root.left  = new Node(5);
        root.right = new Node(1);
        root.left.left   = new Node(6);
        root.left.right  = new Node(2);
        root.right.left  = new Node(0);
        root.right.right = new Node(8);
        root.left.right.left  = new Node(7);
        root.left.right.right = new Node(4);
        return root;
    }

    static Node findNode(Node root, int val) {
        if (root == null) return null;
        if (root.val == val) return root;
        Node l = findNode(root.left, val);
        return l != null ? l : findNode(root.right, val);
    }

    public static void main(String[] args) {
        System.out.println("=== Lowest Common Ancestor ===\n");

        Node root = buildTree();

        // LCA Recursive
        System.out.println("--- LCA Recursive ---");
        Node p = findNode(root, 5), q = findNode(root, 1);
        System.out.println("LCA(5,1) = " + lcaRecursive(root, p, q).val); // 3

        p = findNode(root, 5); q = findNode(root, 4);
        System.out.println("LCA(5,4) = " + lcaRecursive(root, p, q).val); // 5

        p = findNode(root, 6); q = findNode(root, 4);
        System.out.println("LCA(6,4) = " + lcaRecursive(root, p, q).val); // 5

        p = findNode(root, 7); q = findNode(root, 0);
        System.out.println("LCA(7,0) = " + lcaRecursive(root, p, q).val); // 3

        // LCA Iterative
        System.out.println("\n--- LCA Iterative (Parent Map) ---");
        root = buildTree();
        p = findNode(root, 5); q = findNode(root, 4);
        System.out.println("LCA(5,4) iterative = " + lcaIterative(root, p, q).val); // 5

        // LCA Multiple Nodes
        System.out.println("\n--- LCA Multiple Nodes ---");
        root = buildTree();
        Set<Integer> targets = new HashSet<>(Arrays.asList(4, 7));
        System.out.println("LCA({4,7}) = " + lcaMultiple(root, targets).val); // 2
        targets = new HashSet<>(Arrays.asList(6, 4, 0, 8));
        System.out.println("LCA({6,4,0,8}) = " + lcaMultiple(root, targets).val); // 3

        // Distance between nodes
        System.out.println("\n--- Distance Between Nodes ---");
        root = buildTree();
        // dist(6,4): LCA=5, depth(6)=2, depth(4)=3, dist=2+3-2*1=3
        System.out.println("dist(6,4) = " + distBetweenNodes(root, 6, 4)); // 3
        System.out.println("dist(7,8) = " + distBetweenNodes(root, 7, 8)); // 5

        // Tóm tắt pattern
        System.out.println("\n=== LCA Patterns Summary ===");
        System.out.println("1. Binary Tree:      DFS trả về node khi tìm thấy p hoặc q");
        System.out.println("2. BST:              So sánh val để rẽ nhánh — O(h) thay vì O(n)");
        System.out.println("3. Có parent ptr:    Two-pointer (giống Intersection LinkedList)");
        System.out.println("4. Multiple targets: Thay điều kiện check bằng Set.contains()");
        System.out.println("5. Distance:         depth(p) + depth(q) - 2*depth(LCA)");
    }
}
