package phase2_core.trees;

import java.util.*;

/**
 * PHASE 2.2 — Binary Tree: DFS & BFS Traversals
 *
 * Cấu trúc cây nhị phân:
 *        1
 *       / \
 *      2   3
 *     / \   \
 *    4   5   6
 *
 * DFS (Depth First Search) — đi sâu trước:
 *   Inorder   (Left → Root → Right): 4 2 5 1 3 6   ← BST cho sorted order
 *   Preorder  (Root → Left → Right): 1 2 4 5 3 6   ← copy/serialize tree
 *   Postorder (Left → Right → Root): 4 5 2 6 3 1   ← delete tree, evaluate expr
 *
 * BFS (Breadth First Search) — duyệt từng tầng:
 *   Level order: [1] [2,3] [4,5,6]  ← tìm shortest path, print by level
 */
public class BinaryTree {

    static class Node {
        int val;
        Node left, right;
        Node(int val) { this.val = val; }
        Node(int val, Node left, Node right) { this.val = val; this.left = left; this.right = right; }
    }

    // =========================================================
    // DFS — Recursive (ngắn gọn, dễ đọc)
    // =========================================================
    static List<Integer> inorderRec(Node root) {
        List<Integer> res = new ArrayList<>();
        inorderHelper(root, res);
        return res;
    }
    static void inorderHelper(Node node, List<Integer> res) {
        if (node == null) return;
        inorderHelper(node.left, res);
        res.add(node.val);
        inorderHelper(node.right, res);
    }

    static List<Integer> preorderRec(Node root) {
        List<Integer> res = new ArrayList<>();
        preorderHelper(root, res);
        return res;
    }
    static void preorderHelper(Node node, List<Integer> res) {
        if (node == null) return;
        res.add(node.val);           // Root
        preorderHelper(node.left, res);  // Left
        preorderHelper(node.right, res); // Right
    }

    static List<Integer> postorderRec(Node root) {
        List<Integer> res = new ArrayList<>();
        postorderHelper(root, res);
        return res;
    }
    static void postorderHelper(Node node, List<Integer> res) {
        if (node == null) return;
        postorderHelper(node.left, res);
        postorderHelper(node.right, res);
        res.add(node.val);           // Root cuối cùng
    }

    // =========================================================
    // DFS — Iterative (dùng Stack — tránh stack overflow với cây sâu)
    // =========================================================

    /** Inorder Iterative — quan trọng, hay hỏi */
    static List<Integer> inorderIter(Node root) {
        List<Integer> res = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        Node curr = root;

        while (curr != null || !stack.isEmpty()) {
            // Đi hết sang trái
            while (curr != null) { stack.push(curr); curr = curr.left; }
            // Xử lý node hiện tại
            curr = stack.pop();
            res.add(curr.val);
            // Chuyển sang phải
            curr = curr.right;
        }
        return res;
    }

    /** Preorder Iterative */
    static List<Integer> preorderIter(Node root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            res.add(node.val);                          // Root
            if (node.right != null) stack.push(node.right); // right trước (LIFO)
            if (node.left  != null) stack.push(node.left);  // left sau → xử lý trước
        }
        return res;
    }

    /** Postorder Iterative — trick: reverse của modified preorder (Root→Right→Left) */
    static List<Integer> postorderIter(Node root) {
        LinkedList<Integer> res = new LinkedList<>();
        if (root == null) return res;
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            res.addFirst(node.val);                     // thêm vào ĐẦU → đảo ngược
            if (node.left  != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }
        return res;
    }

    // =========================================================
    // BFS — Level Order Traversal
    // =========================================================

    /**
     * Level Order (LeetCode #102)
     * → [[1],[2,3],[4,5,6]]
     * Dùng Queue — xử lý từng tầng
     */
    static List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size(); // số node trong tầng hiện tại
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < levelSize; i++) {
                Node node = queue.poll();
                level.add(node.val);
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            res.add(level);
        }
        return res;
    }

    /**
     * Zigzag Level Order (LeetCode #103)
     * → [[1],[3,2],[4,5,6]]  (xen kẽ trái-phải)
     */
    static List<List<Integer>> zigzagLevelOrder(Node root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int size = queue.size();
            LinkedList<Integer> level = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                if (leftToRight) level.addLast(node.val);
                else             level.addFirst(node.val); // đảo chiều
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            res.add(level);
            leftToRight = !leftToRight;
        }
        return res;
    }

    /**
     * Right Side View (LeetCode #199)
     * → [1, 3, 6]  (node ngoài cùng bên phải mỗi tầng)
     */
    static List<Integer> rightSideView(Node root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;
        Queue<Node> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                if (i == size - 1) res.add(node.val); // node cuối của mỗi tầng
                if (node.left  != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return res;
    }

    // =========================================================
    // HEIGHT, DEPTH, DIAMETER
    // =========================================================

    /**
     * Maximum Depth / Height (LeetCode #104)
     * Postorder: tính height từ lá lên gốc
     */
    static int maxDepth(Node root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    /**
     * Minimum Depth (LeetCode #111)
     * Chú ý: min depth = đường đến LÁ, không phải node null
     * Node chỉ có 1 con → không phải lá → không tính
     */
    static int minDepth(Node root) {
        if (root == null) return 0;
        if (root.left == null && root.right == null) return 1; // lá
        if (root.left  == null) return 1 + minDepth(root.right); // chỉ có right
        if (root.right == null) return 1 + minDepth(root.left);  // chỉ có left
        return 1 + Math.min(minDepth(root.left), minDepth(root.right));
    }

    /**
     * Diameter of Binary Tree (LeetCode #543)
     * Đường kính = đường dài nhất giữa 2 node bất kỳ
     * = max(leftHeight + rightHeight) qua mọi node
     *
     * Trick: dùng int[] diameter để cập nhật từ bên trong đệ quy
     */
    static int diameterOfBinaryTree(Node root) {
        int[] diameter = {0};
        heightForDiameter(root, diameter);
        return diameter[0];
    }

    static int heightForDiameter(Node node, int[] diameter) {
        if (node == null) return 0;
        int left  = heightForDiameter(node.left,  diameter);
        int right = heightForDiameter(node.right, diameter);
        diameter[0] = Math.max(diameter[0], left + right); // đường qua node này
        return 1 + Math.max(left, right);
    }

    /**
     * Balanced Binary Tree (LeetCode #110)
     * Cây cân bằng: |height(left) - height(right)| <= 1 với mọi node
     * Dùng -1 làm sentinel "không cân bằng"
     */
    static boolean isBalanced(Node root) {
        return checkHeight(root) != -1;
    }

    static int checkHeight(Node node) {
        if (node == null) return 0;
        int left  = checkHeight(node.left);
        int right = checkHeight(node.right);
        if (left == -1 || right == -1) return -1;          // con không cân bằng
        if (Math.abs(left - right) > 1) return -1;         // node này không cân bằng
        return 1 + Math.max(left, right);
    }

    // =========================================================
    // PATH SUM
    // =========================================================

    /**
     * Path Sum (LeetCode #112) — có đường từ root → lá với tổng = targetSum?
     */
    static boolean hasPathSum(Node root, int targetSum) {
        if (root == null) return false;
        if (root.left == null && root.right == null) return root.val == targetSum;
        return hasPathSum(root.left,  targetSum - root.val)
            || hasPathSum(root.right, targetSum - root.val);
    }

    /**
     * Path Sum II (LeetCode #113) — liệt kê tất cả đường root→lá có tổng = target
     */
    static List<List<Integer>> pathSumII(Node root, int target) {
        List<List<Integer>> res = new ArrayList<>();
        dfsPath(root, target, new ArrayList<>(), res);
        return res;
    }

    static void dfsPath(Node node, int remain, List<Integer> path, List<List<Integer>> res) {
        if (node == null) return;
        path.add(node.val);
        if (node.left == null && node.right == null && remain == node.val)
            res.add(new ArrayList<>(path)); // tìm thấy → copy path
        dfsPath(node.left,  remain - node.val, path, res);
        dfsPath(node.right, remain - node.val, path, res);
        path.remove(path.size() - 1); // backtrack
    }

    /**
     * Binary Tree Maximum Path Sum (LeetCode #124) — Hard
     * Path có thể bắt đầu và kết thúc ở bất kỳ node nào
     * → tại mỗi node: gain = val + max(leftGain, 0) + max(rightGain, 0)
     */
    static int maxPathSum(Node root) {
        int[] maxSum = {Integer.MIN_VALUE};
        maxGain(root, maxSum);
        return maxSum[0];
    }

    static int maxGain(Node node, int[] maxSum) {
        if (node == null) return 0;
        int leftGain  = Math.max(maxGain(node.left,  maxSum), 0); // bỏ nếu âm
        int rightGain = Math.max(maxGain(node.right, maxSum), 0);
        maxSum[0] = Math.max(maxSum[0], node.val + leftGain + rightGain);
        return node.val + Math.max(leftGain, rightGain); // chỉ chọn 1 nhánh để trả về
    }

    // =========================================================
    // HELPER — build tree
    // =========================================================
    static Node build() {
        //        1
        //       / \
        //      2   3
        //     / \   \
        //    4   5   6
        return new Node(1,
                new Node(2, new Node(4), new Node(5)),
                new Node(3, null, new Node(6)));
    }

    public static void main(String[] args) {
        System.out.println("=== Binary Tree Traversals ===\n");
        Node root = build();

        // DFS
        System.out.println("--- DFS Recursive ---");
        System.out.println("Inorder   (L→Root→R): " + inorderRec(root));   // 4 2 5 1 3 6
        System.out.println("Preorder  (Root→L→R): " + preorderRec(root));  // 1 2 4 5 3 6
        System.out.println("Postorder (L→R→Root): " + postorderRec(root)); // 4 5 2 6 3 1

        System.out.println("\n--- DFS Iterative ---");
        System.out.println("Inorder   iter: " + inorderIter(root));   // 4 2 5 1 3 6
        System.out.println("Preorder  iter: " + preorderIter(root));  // 1 2 4 5 3 6
        System.out.println("Postorder iter: " + postorderIter(root)); // 4 5 2 6 3 1

        // BFS
        System.out.println("\n--- BFS ---");
        System.out.println("levelOrder:    " + levelOrder(root));     // [[1],[2,3],[4,5,6]]
        System.out.println("zigzagOrder:   " + zigzagLevelOrder(root));// [[1],[3,2],[4,5,6]]
        System.out.println("rightSideView: " + rightSideView(root));  // [1,3,6]

        // Height / Depth / Diameter
        System.out.println("\n--- Height / Depth / Diameter ---");
        System.out.println("maxDepth:  " + maxDepth(root));  // 3
        System.out.println("minDepth:  " + minDepth(root));  // 2 (1→3→6? no, 1→2→4=3, 1→3→6=3... wait)
        System.out.println("diameter:  " + diameterOfBinaryTree(root)); // 4 (4→2→1→3→6)
        System.out.println("isBalanced:" + isBalanced(root)); // true

        // Path Sum
        System.out.println("\n--- Path Sum ---");
        System.out.println("hasPathSum(root,7):  " + hasPathSum(root, 7));  // true (1→2→4)
        System.out.println("hasPathSum(root,10): " + hasPathSum(root, 10)); // false

        Node ps = new Node(5,
                new Node(4, new Node(11, new Node(7), new Node(2)), null),
                new Node(8, new Node(13), new Node(4, new Node(5), new Node(1))));
        System.out.println("pathSumII(root,22): " + pathSumII(ps, 22)); // [[5,4,11,2],[5,8,4,5]]

        // Max Path Sum
        Node mps = new Node(-10, new Node(9), new Node(20, new Node(15), new Node(7)));
        System.out.println("maxPathSum: " + maxPathSum(mps)); // 42 (15+20+7)
    }
}
