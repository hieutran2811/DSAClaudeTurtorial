package leetcode_practice;

import java.util.*;

/**
 * LEETCODE PRACTICE -- SESSION 03
 * Pattern: STACK & TREES
 *
 * Stack mental model:
 *   - Monotonic stack: maintain increasing/decreasing order,
 *     pop when current element "breaks" the order => O(n) amortized.
 *   - "Next greater/smaller" problems => monotonic stack.
 *
 * Tree mental model:
 *   - DFS (recursion): think about what info each subtree returns UP.
 *   - BFS (queue): level-order, shortest path in unweighted tree.
 *   - "Return two values" trick: use int[] or long to pack info.
 *
 * Problems:
 *   Stack Easy:   #20  Valid Parentheses
 *   Stack Medium: #739 Daily Temperatures, #853 Car Fleet
 *   Stack Hard:   #84  Largest Rectangle in Histogram
 *                 #85  Maximal Rectangle
 *   Tree Easy:    #104 Maximum Depth, #572 Subtree of Another Tree
 *   Tree Medium:  #102 Binary Tree Level Order, #543 Diameter of Binary Tree
 *                 #236 Lowest Common Ancestor
 *   Tree Hard:    #124 Binary Tree Maximum Path Sum
 *                 #297 Serialize and Deserialize Binary Tree
 */
public class Session03_StackAndTrees {

    public static void main(String[] args) {
        System.out.println("=== SESSION 03: STACK & TREES ===\n");
        testStack();
        testTrees();
    }

    // =========================================================================
    // STACK PROBLEMS
    // =========================================================================

    // #20 Valid Parentheses -- Easy
    // Pattern: classic stack match
    // Push open brackets; on close, check top matches.
    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == ']' && top != '[') return false;
                if (c == '}' && top != '{') return false;
            }
        }
        return stack.isEmpty();
    }

    // #739 Daily Temperatures -- Medium
    // Pattern: Monotonic decreasing stack (stores indices)
    // For each day, pop all days that are COOLER (answer found for them).
    // Brute: O(n^2) -- for each day scan forward
    // Optimal: O(n) -- each index pushed and popped at most once
    static int[] dailyTemperatures(int[] temps) {
        int n = temps.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // indices, decreasing temps
        for (int i = 0; i < n; i++) {
            // Current temp is warmer than stack top => answer found for stack top
            while (!stack.isEmpty() && temps[i] > temps[stack.peek()])
                result[stack.pop()] = i - stack.peek() - 1 + 1;
                // Simpler: result[idx] = i - idx
            stack.push(i);
        }
        return result;
    }

    // Fix the subtle off-by-one above -- cleaner version
    static int[] dailyTemperaturesClean(int[] temps) {
        int n = temps.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temps[i] > temps[stack.peek()]) {
                int idx = stack.pop();
                result[idx] = i - idx;
            }
            stack.push(i);
        }
        return result;
    }

    // #853 Car Fleet -- Medium
    // Pattern: monotonic stack thinking (process from right)
    // Cars closer to target that are slower create a "fleet" bottleneck.
    // Sort by position desc. Stack tracks fleet arrival times.
    // If current car arrives BEFORE the car ahead => merges into that fleet (pop-like).
    static int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        // Sort by position descending (closest to target first)
        Arrays.sort(idx, (a, b) -> position[b] - position[a]);

        int fleets = 0;
        double topTime = 0; // arrival time of current leading fleet
        for (int i : idx) {
            double time = (double)(target - position[i]) / speed[i];
            if (time > topTime) { // this car arrives AFTER leader => new fleet
                fleets++;
                topTime = time;
            }
            // else: merges into leader fleet (arrives same time or earlier)
        }
        return fleets;
    }

    // #84 Largest Rectangle in Histogram -- Hard
    // Pattern: Monotonic increasing stack
    // For each bar, find the farthest left it can extend (using stack of indices).
    // When we pop index i (because current bar is shorter), the rectangle
    //   height = heights[i], width = current_index - stack.peek() - 1
    // Append sentinel 0 at end to flush remaining bars.
    static int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int[] h = Arrays.copyOf(heights, n + 1); // sentinel 0 at end
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1); // sentinel for left boundary
        int maxArea = 0;
        for (int i = 0; i <= n; i++) {
            while (stack.peek() != -1 && h[i] < h[stack.peek()]) {
                int height = h[stack.pop()];
                int width  = i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    // #85 Maximal Rectangle -- Hard
    // Build histogram row by row, apply #84 on each row's histogram.
    // heights[j] = consecutive 1s ending at current row in column j
    static int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) return 0;
        int n = matrix[0].length;
        int[] heights = new int[n];
        int maxArea = 0;
        for (char[] row : matrix) {
            for (int j = 0; j < n; j++)
                heights[j] = (row[j] == '1') ? heights[j] + 1 : 0;
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }
        return maxArea;
    }

    static void testStack() {
        System.out.println("--- STACK ---");

        System.out.println("#20  isValid(\"()\")      = " + isValid("()"));       // true
        System.out.println("#20  isValid(\"()[]{}\")  = " + isValid("()[]{}"));   // true
        System.out.println("#20  isValid(\"(]\")      = " + isValid("(]"));       // false
        System.out.println("#20  isValid(\"{[]}\")    = " + isValid("{[]}"));     // true

        System.out.println("#739 dailyTemperatures([73,74,75,71,69,72,76,73]) = "
            + Arrays.toString(dailyTemperaturesClean(new int[]{73,74,75,71,69,72,76,73})));
        // [1,1,4,2,1,1,0,0]

        System.out.println("#853 carFleet(12,[10,8,0,5,3],[2,4,1,1,3]) = "
            + carFleet(12, new int[]{10,8,0,5,3}, new int[]{2,4,1,1,3})); // 3
        System.out.println("#853 carFleet(10,[3],[3])                   = "
            + carFleet(10, new int[]{3}, new int[]{3}));                   // 1

        System.out.println("#84  largestRectangleArea([2,1,5,6,2,3]) = "
            + largestRectangleArea(new int[]{2,1,5,6,2,3})); // 10
        System.out.println("#84  largestRectangleArea([2,4])          = "
            + largestRectangleArea(new int[]{2,4}));          // 4

        char[][] matrix = {
            {'1','0','1','0','0'},
            {'1','0','1','1','1'},
            {'1','1','1','1','1'},
            {'1','0','0','1','0'}
        };
        System.out.println("#85  maximalRectangle(matrix)             = "
            + maximalRectangle(matrix)); // 6
        System.out.println();
    }

    // =========================================================================
    // TREE PROBLEMS
    // =========================================================================

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val; this.left = left; this.right = right;
        }
    }

    // Helper: build tree from level-order array (null = null node)
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode node = q.poll();
            if (i < vals.length && vals[i] != null) {
                node.left = new TreeNode(vals[i]); q.add(node.left);
            }
            i++;
            if (i < vals.length && vals[i] != null) {
                node.right = new TreeNode(vals[i]); q.add(node.right);
            }
            i++;
        }
        return root;
    }

    // #104 Maximum Depth of Binary Tree -- Easy
    // DFS: depth = 1 + max(depth(left), depth(right)), base = 0 for null
    static int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    // #572 Subtree of Another Tree -- Easy
    // Check if subRoot is a subtree of root.
    // Brute: O(m*n) -- for each node in root, check isSameTree
    // (Optimal O(m+n) via hashing/KMP on serialized strings, but O(m*n) is fine)
    static boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;
        if (isSameTree(root, subRoot)) return true;
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    // #102 Binary Tree Level Order Traversal -- Medium
    // BFS with size snapshot: process exactly one level per while-iteration.
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            int size = q.size(); // snapshot: nodes in current level
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                level.add(node.val);
                if (node.left  != null) q.add(node.left);
                if (node.right != null) q.add(node.right);
            }
            result.add(level);
        }
        return result;
    }

    // #543 Diameter of Binary Tree -- Medium
    // Diameter = longest path between ANY two nodes (may not pass through root).
    // Key: for each node, candidate = depth(left) + depth(right).
    //   Return depth upward, update global max at each node.
    static int diameterResult;

    static int diameter(TreeNode root) {
        diameterResult = 0;
        depthForDiameter(root);
        return diameterResult;
    }

    static int depthForDiameter(TreeNode node) {
        if (node == null) return 0;
        int left  = depthForDiameter(node.left);
        int right = depthForDiameter(node.right);
        diameterResult = Math.max(diameterResult, left + right); // path through node
        return 1 + Math.max(left, right);                        // depth returned up
    }

    // #236 Lowest Common Ancestor of Binary Tree -- Medium
    // Key insight: if we find p in left and q in right (or vice versa),
    //   current node IS the LCA. If only one side has both, return that side.
    // Return: the LCA node if found below, or p/q if one of them is found.
    static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left  = lowestCommonAncestor(root.left,  p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) return root; // p in one side, q in other
        return left != null ? left : right;             // both in same side
    }

    // #124 Binary Tree Maximum Path Sum -- Hard
    // Path can start and end at ANY node. Path must go DOWNWARD (no U-turn).
    // Key: at each node, we compute:
    //   1. maxGain: max sum from this node going DOWN one direction (returned up)
    //   2. localMax: left gain + node.val + right gain (full path through node)
    //   Use negative gain? Take 0 instead (don't include that side).
    static int maxPathSumResult;

    static int maxPathSum(TreeNode root) {
        maxPathSumResult = Integer.MIN_VALUE;
        gainFromNode(root);
        return maxPathSumResult;
    }

    static int gainFromNode(TreeNode node) {
        if (node == null) return 0;
        int leftGain  = Math.max(gainFromNode(node.left),  0); // ignore negative
        int rightGain = Math.max(gainFromNode(node.right), 0);
        // Best path through this node
        maxPathSumResult = Math.max(maxPathSumResult, node.val + leftGain + rightGain);
        // Return the best single-direction gain upward
        return node.val + Math.max(leftGain, rightGain);
    }

    // #297 Serialize and Deserialize Binary Tree -- Hard
    // Use preorder DFS with "#" for nulls and "," as delimiter.
    // Serialize: preorder traversal, null -> "#"
    // Deserialize: use a Queue to consume tokens in preorder
    static String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    static void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) { sb.append("#,"); return; }
        sb.append(node.val).append(",");
        serializeHelper(node.left,  sb);
        serializeHelper(node.right, sb);
    }

    static TreeNode deserialize(String data) {
        Queue<String> tokens = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeHelper(tokens);
    }

    static TreeNode deserializeHelper(Queue<String> tokens) {
        String token = tokens.poll();
        if ("#".equals(token)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left  = deserializeHelper(tokens);
        node.right = deserializeHelper(tokens);
        return node;
    }

    static void testTrees() {
        System.out.println("--- TREES ---");

        // [3,9,20,null,null,15,7]
        TreeNode t1 = build(3, 9, 20, null, null, 15, 7);
        System.out.println("#104 maxDepth([3,9,20,null,null,15,7]) = " + maxDepth(t1)); // 3
        System.out.println("#104 maxDepth([1,null,2])              = " + maxDepth(build(1, null, 2))); // 2

        // Subtree
        TreeNode s = build(3, 4, 5, 1, 2);
        TreeNode sub = build(4, 1, 2);
        System.out.println("#572 isSubtree([3,4,5,1,2], [4,1,2])  = " + isSubtree(s, sub)); // true
        TreeNode sub2 = build(4, 1, null, null, 2);
        System.out.println("#572 isSubtree([3,4,5,1,2], [4,1,,2]) = " + isSubtree(s, sub2)); // false

        // Level order
        System.out.println("#102 levelOrder([3,9,20,null,null,15,7]) = " + levelOrder(t1));
        // [[3],[9,20],[15,7]]

        // Diameter
        TreeNode d = build(1, 2, 3, 4, 5);
        System.out.println("#543 diameter([1,2,3,4,5]) = " + diameter(d)); // 3
        System.out.println("#543 diameter([1,2])       = " + diameter(build(1, 2))); // 1

        // LCA
        TreeNode lca = build(3, 5, 1, 6, 2, 0, 8, null, null, 7, 4);
        TreeNode p = lca.left;          // node 5
        TreeNode q = lca.left.right;    // node 2
        System.out.println("#236 LCA(3,p=5,q=2) = " + lowestCommonAncestor(lca, p, q).val); // 5
        TreeNode q2 = lca.right;        // node 1
        System.out.println("#236 LCA(3,p=5,q=1) = " + lowestCommonAncestor(lca, p, q2).val); // 3

        // Max Path Sum
        System.out.println("#124 maxPathSum([1,2,3])          = "
            + maxPathSum(build(1, 2, 3)));          // 6
        System.out.println("#124 maxPathSum([-10,9,20,,15,7]) = "
            + maxPathSum(build(-10, 9, 20, null, null, 15, 7))); // 42
        System.out.println("#124 maxPathSum([-3])             = "
            + maxPathSum(build(-3)));               // -3

        // Serialize / Deserialize
        TreeNode orig = build(1, 2, 3, null, null, 4, 5);
        String serialized = serialize(orig);
        System.out.println("#297 serialize([1,2,3,,4,5]) = " + serialized);
        TreeNode restored = deserialize(serialized);
        System.out.println("#297 re-serialized           = " + serialize(restored));
        System.out.println("#297 levelOrder(restored)    = " + levelOrder(restored));

        System.out.println();
        System.out.println("PATTERN SUMMARY -- Stack & Trees:");
        System.out.println("  Mono stack       : pop when current breaks order => O(n) NGE/NSE");
        System.out.println("  Histogram rect   : stack with sentinel -1, width = i - stack.peek - 1");
        System.out.println("  DFS return up    : each subtree returns info (depth/gain/bool)");
        System.out.println("  Global + local   : update global max at each node, return local best up");
        System.out.println("  LCA pattern      : if both sides return non-null => current is LCA");
        System.out.println("  Serialize        : preorder + '#' for null, deserialize via Queue");
    }
}
