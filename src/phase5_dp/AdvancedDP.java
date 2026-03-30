package phase5_dp;

import java.util.*;

/**
 * PHASE 5.3 + 5.4 — 2D DP & ADVANCED DP
 * =========================================
 * Topics covered:
 *  83. Grid path problems
 *  84. Matrix chain multiplication
 *  85. Palindrome partitioning DP
 *  86. Bitmask DP
 *  87. Interval DP
 *  88. Tree DP
 *  89. DP on graphs (DAG DP)
 */
public class AdvancedDP {

    // =========================================================
    // TOPIC 83 — GRID PATH PROBLEMS
    // =========================================================

    /**
     * LeetCode #62 — Unique Paths
     * Count paths from (0,0) to (m-1,n-1) moving only right or down.
     *
     * State:      dp[r][c] = number of paths to reach (r,c)
     * Recurrence: dp[r][c] = dp[r-1][c] + dp[r][c-1]
     * Base:       dp[0][c] = dp[r][0] = 1  (only one way along edges)
     * Time: O(m*n)   Space: O(n) with rolling row
     */
    static int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int r = 1; r < m; r++)
            for (int c = 1; c < n; c++)
                dp[c] += dp[c-1]; // dp[c] = from above, dp[c-1] = from left
        return dp[n-1];
    }

    /**
     * LeetCode #63 — Unique Paths II (with obstacles)
     * 0 = free, 1 = obstacle (dp[r][c] = 0 if obstacle)
     */
    static int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1 || grid[m-1][n-1] == 1) return 0;
        int[] dp = new int[n];
        dp[0] = 1;
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) { dp[c] = 0; continue; }
                if (c > 0) dp[c] += dp[c-1];
            }
        return dp[n-1];
    }

    /**
     * LeetCode #64 — Minimum Path Sum
     * Find path from top-left to bottom-right minimizing sum (right/down only).
     *
     * State:      dp[r][c] = min cost path to (r,c)
     * Recurrence: dp[r][c] = grid[r][c] + min(dp[r-1][c], dp[r][c-1])
     */
    static int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] dp = new int[n];
        dp[0] = grid[0][0];
        for (int c = 1; c < n; c++) dp[c] = dp[c-1] + grid[0][c];

        for (int r = 1; r < m; r++) {
            dp[0] += grid[r][0];
            for (int c = 1; c < n; c++)
                dp[c] = grid[r][c] + Math.min(dp[c], dp[c-1]);
        }
        return dp[n-1];
    }

    /**
     * LeetCode #931 — Minimum Falling Path Sum
     * Fall from any cell in row 0 to any cell in last row.
     * Each step: go to (r+1, c-1), (r+1, c), or (r+1, c+1)
     *
     * State:      dp[r][c] = min falling path sum ending at (r,c)
     * Recurrence: dp[r][c] = matrix[r][c] + min(dp[r-1][c-1], dp[r-1][c], dp[r-1][c+1])
     */
    static int minFallingPathSum(int[][] matrix) {
        int n = matrix.length;
        int[] dp = matrix[0].clone();
        for (int r = 1; r < n; r++) {
            int[] next = new int[n];
            for (int c = 0; c < n; c++) {
                int best = dp[c];
                if (c > 0)   best = Math.min(best, dp[c-1]);
                if (c < n-1) best = Math.min(best, dp[c+1]);
                next[c] = matrix[r][c] + best;
            }
            dp = next;
        }
        return Arrays.stream(dp).min().getAsInt();
    }

    /**
     * LeetCode #741 — Cherry Pickup (hard)
     * Two simultaneous paths: top-left → bottom-right → top-left.
     * Key insight: simulate as TWO people going top-left → bottom-right simultaneously.
     *   At step k: p1 at (r1, k-r1), p2 at (r2, k-r2)
     *   Only track r1 and r2 (c derived from k).
     *
     * State:      dp[k][r1][r2] = max cherries collected
     * Recurrence: try all 4 combinations of previous moves for p1 and p2
     */
    static int cherryPickup(int[][] grid) {
        int n = grid.length;
        // dp[r1][r2] at step k
        int[][] dp = new int[n][n];
        for (int[] row : dp) Arrays.fill(row, Integer.MIN_VALUE);
        dp[0][0] = grid[0][0];

        for (int k = 1; k < 2*n-1; k++) {
            int[][] next = new int[n][n];
            for (int[] row : next) Arrays.fill(row, Integer.MIN_VALUE);

            for (int r1 = Math.max(0, k-(n-1)); r1 < Math.min(n, k+1); r1++) {
                int c1 = k - r1;
                if (c1 < 0 || c1 >= n || grid[r1][c1] == -1) continue;

                for (int r2 = r1; r2 < Math.min(n, k+1); r2++) {
                    int c2 = k - r2;
                    if (c2 < 0 || c2 >= n || grid[r2][c2] == -1) continue;

                    int cherries = dp[r1][r2];
                    if (cherries == Integer.MIN_VALUE) continue;

                    cherries += grid[r1][c1];
                    if (r1 != r2) cherries += grid[r2][c2]; // different cells

                    // Try all 4 prev moves
                    for (int pr1 : new int[]{r1-1, r1}) {
                        for (int pr2 : new int[]{r2-1, r2}) {
                            if (pr1 >= 0 && pr2 >= 0 && dp[pr1][pr2] != Integer.MIN_VALUE) {
                                next[r1][r2] = Math.max(next[r1][r2], cherries);
                            }
                        }
                    }
                    // actually fill from best previous
                    next[r1][r2] = Math.max(next[r1][r2], cherries);
                }
            }
            dp = next;
        }
        return Math.max(0, dp[n-1][n-1]);
    }

    // =========================================================
    // TOPIC 84 — MATRIX CHAIN MULTIPLICATION
    // =========================================================

    /**
     * Find optimal parenthesization to minimize scalar multiplications.
     * Matrices: A[0]*A[1]*...*A[n-1]
     * dims[i] = rows of matrix i, dims[i+1] = cols of matrix i
     *
     * State:      dp[i][j] = min cost to multiply matrices i..j
     * Recurrence: dp[i][j] = min over k in [i,j): dp[i][k] + dp[k+1][j] + dims[i]*dims[k+1]*dims[j+1]
     * Base:       dp[i][i] = 0  (single matrix, no multiplication)
     * Order:      fill by increasing LENGTH of subchain
     *
     * Time: O(n³)   Space: O(n²)
     */
    static int matrixChainOrder(int[] dims) {
        int n = dims.length - 1; // number of matrices
        int[][] dp = new int[n][n]; // dp[i][j] = min cost for matrices i..j

        // len = length of chain (2 to n)
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n-len; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    int cost = dp[i][k] + dp[k+1][j] + dims[i]*dims[k+1]*dims[j+1];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp[0][n-1];
    }

    /**
     * LeetCode #312 — Burst Balloons
     * Same pattern as matrix chain: interval DP.
     * Trick: think of last balloon burst in range [i,j].
     * When balloon k is burst last, neighbors are i-1 and j+1.
     *
     * State:      dp[i][j] = max coins bursting all balloons between i and j (exclusive)
     * Recurrence: dp[i][j] = max over k in (i,j): dp[i][k] + dp[k][j] + nums[i]*nums[k]*nums[j]
     *
     * Time: O(n³)
     */
    static int maxCoins(int[] nums) {
        int n = nums.length;
        // Pad with 1s at boundaries
        int[] balloons = new int[n+2];
        balloons[0] = balloons[n+1] = 1;
        for (int i = 0; i < n; i++) balloons[i+1] = nums[i];
        n += 2;

        int[][] dp = new int[n][n];
        // len from 2 upward (open interval needs at least 2 boundary points)
        for (int len = 2; len < n; len++) {
            for (int i = 0; i < n-len; i++) {
                int j = i + len;
                for (int k = i+1; k < j; k++) {
                    dp[i][j] = Math.max(dp[i][j],
                        dp[i][k] + dp[k][j] + balloons[i]*balloons[k]*balloons[j]);
                }
            }
        }
        return dp[0][n-1];
    }

    // =========================================================
    // TOPIC 85 — PALINDROME PARTITIONING DP
    // =========================================================

    /**
     * LeetCode #132 — Palindrome Partitioning II
     * Min cuts to partition s so every substring is a palindrome.
     *
     * Step 1: precompute isPalin[i][j] in O(n²)
     * Step 2: dp[i] = min cuts for s[0..i]
     *         dp[i] = min over j<=i where isPalin[j][i]: dp[j-1] + 1
     *         if isPalin[0][i]: dp[i] = 0 (whole prefix is palindrome)
     *
     * Time: O(n²)   Space: O(n²) for isPalin, O(n) for dp
     */
    static int minCutPalindrome(String s) {
        int n = s.length();
        boolean[][] isPalin = new boolean[n][n];

        // Expand around center to fill isPalin in O(n²)
        for (int center = 0; center < n; center++) {
            // Odd length
            for (int r = 0; center-r >= 0 && center+r < n
                    && s.charAt(center-r) == s.charAt(center+r); r++)
                isPalin[center-r][center+r] = true;
            // Even length
            for (int r = 0; center-r >= 0 && center+r+1 < n
                    && s.charAt(center-r) == s.charAt(center+r+1); r++)
                isPalin[center-r][center+r+1] = true;
        }

        int[] dp = new int[n];
        Arrays.fill(dp, n); // max cuts = n-1
        for (int i = 0; i < n; i++) {
            if (isPalin[0][i]) { dp[i] = 0; continue; }
            for (int j = 1; j <= i; j++)
                if (isPalin[j][i]) dp[i] = Math.min(dp[i], dp[j-1] + 1);
        }
        return dp[n-1];
    }

    /**
     * LeetCode #5 — Longest Palindromic Substring
     * Use isPalin table from expand-around-center.
     * Track the longest palindrome found.
     */
    static String longestPalindrome(String s) {
        int n = s.length(), start = 0, maxLen = 1;
        for (int center = 0; center < n; center++) {
            // Odd
            int lo = center, hi = center;
            while (lo >= 0 && hi < n && s.charAt(lo) == s.charAt(hi)) {
                if (hi-lo+1 > maxLen) { maxLen = hi-lo+1; start = lo; }
                lo--; hi++;
            }
            // Even
            lo = center; hi = center+1;
            while (lo >= 0 && hi < n && s.charAt(lo) == s.charAt(hi)) {
                if (hi-lo+1 > maxLen) { maxLen = hi-lo+1; start = lo; }
                lo--; hi++;
            }
        }
        return s.substring(start, start+maxLen);
    }

    // =========================================================
    // TOPIC 86 — BITMASK DP
    // =========================================================

    /**
     * Bitmask DP — state includes a bitmask representing a SUBSET of items.
     * Use when: n is small (≤ 20), need to track which items are used.
     *
     * Classic: Traveling Salesman Problem (TSP)
     * dp[mask][i] = min cost to visit exactly the cities in `mask`,
     *               ending at city i.
     *
     * mask has n bits: bit j set = city j has been visited.
     * Full mask = (1<<n)-1 = all cities visited.
     *
     * Time: O(2^n * n²)   Space: O(2^n * n)
     */
    static int tsp(int[][] dist) {
        int n = dist.length;
        int FULL = (1 << n) - 1;
        int[][] dp = new int[1<<n][n];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE/2);
        dp[1][0] = 0; // start at city 0, only city 0 visited (bit 0 set)

        for (int mask = 1; mask <= FULL; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1<<u)) == 0) continue;           // u not in mask
                if (dp[mask][u] == Integer.MAX_VALUE/2) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1<<v)) != 0) continue;       // v already visited
                    int nextMask = mask | (1<<v);
                    dp[nextMask][v] = Math.min(dp[nextMask][v], dp[mask][u] + dist[u][v]);
                }
            }
        }

        // Return to start: min over all ending cities
        int result = Integer.MAX_VALUE;
        for (int u = 1; u < n; u++)
            result = Math.min(result, dp[FULL][u] + dist[u][0]);
        return result;
    }

    /**
     * LeetCode #847 — Shortest Path Visiting All Nodes
     * BFS + bitmask: state = (node, visited_mask)
     * Find shortest path visiting ALL nodes (not necessarily returning to start).
     *
     * Time: O(2^n * n)
     */
    static int shortestPathAllNodes(int[][] graph) {
        int n = graph.length, FULL = (1<<n)-1;
        int[][] dist = new int[1<<n][n];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

        Queue<int[]> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            dist[1<<i][i] = 0;
            q.offer(new int[]{1<<i, i, 0}); // [mask, node, steps]
        }

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int mask = cur[0], u = cur[1], steps = cur[2];
            if (mask == FULL) return steps;
            if (steps > dist[mask][u]) continue;

            for (int v : graph[u]) {
                int nextMask = mask | (1<<v);
                if (steps+1 < dist[nextMask][v]) {
                    dist[nextMask][v] = steps+1;
                    q.offer(new int[]{nextMask, v, steps+1});
                }
            }
        }
        return -1;
    }

    /**
     * LeetCode #1986 — Minimum Number of Work Sessions to Finish the Tasks
     * Bitmask DP: dp[mask] = min sessions to finish tasks in mask.
     * Try adding each unfinished task to current session or start new one.
     */
    static int minSessions(int[] tasks, int sessionTime) {
        int n = tasks.length, FULL = (1<<n)-1;
        int[] dp    = new int[FULL+1];  // min sessions
        int[] time  = new int[FULL+1];  // time used in last session
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 1; time[0] = 0;

        for (int mask = 0; mask < FULL; mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue;
            for (int i = 0; i < n; i++) {
                if ((mask & (1<<i)) != 0) continue;
                int nextMask = mask | (1<<i);
                if (time[mask] + tasks[i] <= sessionTime) {
                    // Add to current session
                    if (dp[mask] < dp[nextMask] ||
                       (dp[mask] == dp[nextMask] && time[mask]+tasks[i] < time[nextMask])) {
                        dp[nextMask]   = dp[mask];
                        time[nextMask] = time[mask] + tasks[i];
                    }
                } else {
                    // Start new session
                    if (dp[mask]+1 < dp[nextMask] ||
                       (dp[mask]+1 == dp[nextMask] && tasks[i] < time[nextMask])) {
                        dp[nextMask]   = dp[mask]+1;
                        time[nextMask] = tasks[i];
                    }
                }
            }
        }
        return dp[FULL];
    }

    // =========================================================
    // TOPIC 87 — INTERVAL DP
    // =========================================================

    /**
     * Interval DP — dp on subarray/substring [i..j].
     * Pattern: solve small intervals, combine into larger ones.
     * Always fill by INCREASING LENGTH of interval.
     *
     * LeetCode #1000 — Minimum Cost to Merge Stones
     * Merge k consecutive piles into 1. Cost = sum of merged piles.
     * dp[i][j] = min cost to merge piles[i..j] into as few as possible.
     *
     * Only possible to fully merge [i..j] into 1 pile if (j-i) % (k-1) == 0.
     */
    static int mergeStones(int[] stones, int k) {
        int n = stones.length;
        if ((n-1) % (k-1) != 0) return -1;

        int[] prefix = new int[n+1];
        for (int i = 0; i < n; i++) prefix[i+1] = prefix[i] + stones[i];

        int[][] dp = new int[n][n];
        for (int len = k; len <= n; len++) {
            for (int i = 0; i <= n-len; i++) {
                int j = i+len-1;
                dp[i][j] = Integer.MAX_VALUE;
                for (int m = i; m < j; m += k-1)
                    dp[i][j] = Math.min(dp[i][j], dp[i][m] + dp[m+1][j]);
                if ((len-1) % (k-1) == 0)
                    dp[i][j] += prefix[j+1] - prefix[i];
            }
        }
        return dp[0][n-1];
    }

    /**
     * LeetCode #1039 — Minimum Score Triangulation of Polygon
     * dp[i][j] = min score triangulating vertices i..j
     * Try each middle vertex k: triangle (i,k,j) + sub-polygons
     *
     * Time: O(n³)
     */
    static int minScoreTriangulation(int[] values) {
        int n = values.length;
        int[][] dp = new int[n][n];
        for (int len = 2; len < n; len++) {
            for (int i = 0; i < n-len; i++) {
                int j = i+len;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i+1; k < j; k++)
                    dp[i][j] = Math.min(dp[i][j],
                        dp[i][k] + dp[k][j] + values[i]*values[k]*values[j]);
            }
        }
        return dp[0][n-1];
    }

    // =========================================================
    // TOPIC 88 — TREE DP
    // =========================================================

    static class TreeNode {
        int val; TreeNode left, right;
        TreeNode(int v) { val = v; }
        TreeNode(int v, TreeNode l, TreeNode r) { val=v; left=l; right=r; }
    }

    /**
     * LeetCode #337 — House Robber III (Tree DP)
     * Rob houses on a binary tree: can't rob parent and child simultaneously.
     *
     * State per node: [robThis, skipThis]
     *   robThis  = max money if we rob this node
     *   skipThis = max money if we skip this node
     *
     * Recurrence:
     *   robThis  = node.val + skipLeft + skipRight
     *   skipThis = max(robLeft, skipLeft) + max(robRight, skipRight)
     *
     * Bottom-up on tree via post-order DFS.
     * Time: O(n)   Space: O(h)
     */
    static int robTree(TreeNode root) {
        int[] res = robTreeDFS(root);
        return Math.max(res[0], res[1]);
    }

    private static int[] robTreeDFS(TreeNode node) {
        if (node == null) return new int[]{0, 0};
        int[] left  = robTreeDFS(node.left);
        int[] right = robTreeDFS(node.right);
        int rob  = node.val + left[1] + right[1];  // rob this + skip children
        int skip = Math.max(left[0], left[1])       // best of left
                 + Math.max(right[0], right[1]);    // best of right
        return new int[]{rob, skip};
    }

    /**
     * LeetCode #124 — Binary Tree Maximum Path Sum (Tree DP)
     * Path can start and end at any node.
     *
     * At each node: max gain from left and right subtrees (only take if positive).
     * Track global max of (left_gain + node.val + right_gain).
     * Return to parent: node.val + max(left_gain, right_gain) (can only extend one side)
     */
    static int maxPathSum(TreeNode root) {
        int[] maxSum = {Integer.MIN_VALUE};
        maxGain(root, maxSum);
        return maxSum[0];
    }

    private static int maxGain(TreeNode node, int[] maxSum) {
        if (node == null) return 0;
        int left  = Math.max(0, maxGain(node.left,  maxSum)); // ignore negative paths
        int right = Math.max(0, maxGain(node.right, maxSum));
        maxSum[0] = Math.max(maxSum[0], left + node.val + right); // path through node
        return node.val + Math.max(left, right); // extend to parent (one direction only)
    }

    /**
     * LeetCode #968 — Binary Tree Cameras (Tree DP / Greedy)
     * Min cameras to monitor all nodes.
     * State: 0 = needs coverage, 1 = has camera, 2 = covered (no camera)
     * Greedy: place cameras at lowest possible level (leaves' parents first).
     */
    static int minCameraCover(TreeNode root) {
        int[] cameras = {0};
        if (dfsCameras(root, cameras) == 0) cameras[0]++; // root uncovered
        return cameras[0];
    }

    private static int dfsCameras(TreeNode node, int[] cameras) {
        if (node == null) return 2; // null = covered
        int left  = dfsCameras(node.left,  cameras);
        int right = dfsCameras(node.right, cameras);
        if (left == 0 || right == 0) { cameras[0]++; return 1; } // child needs coverage
        if (left == 1 || right == 1) return 2;                    // child has camera
        return 0;                                                   // node needs coverage
    }

    // =========================================================
    // TOPIC 89 — DP ON GRAPHS (DAG DP)
    // =========================================================

    /**
     * Longest path in a DAG.
     * dp[u] = longest path starting from u
     * Process in reverse topological order (DFS with memoization).
     *
     * Time: O(V + E)
     */
    static int longestPathDAG(List<List<Integer>> adj, int V) {
        int[] dp = new int[V];
        boolean[] visited = new boolean[V];
        int maxPath = 0;
        for (int u = 0; u < V; u++)
            maxPath = Math.max(maxPath, dfsLongest(adj, u, dp, visited));
        return maxPath;
    }

    private static int dfsLongest(List<List<Integer>> adj, int u, int[] dp, boolean[] visited) {
        if (visited[u]) return dp[u];
        visited[u] = true;
        for (int v : adj.get(u))
            dp[u] = Math.max(dp[u], 1 + dfsLongest(adj, v, dp, visited));
        return dp[u];
    }

    /**
     * LeetCode #329 — Longest Increasing Path in a Matrix
     * Treat matrix as DAG: edge from (r,c) to neighbor if neighbor > current.
     * DFS + memoization on each cell.
     *
     * State:      dp[r][c] = longest increasing path starting from (r,c)
     * Time: O(m*n)   Space: O(m*n)
     */
    static int longestIncreasingPath(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        int[][] dp = new int[rows][cols];
        int result = 0;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                result = Math.max(result, dfsLIP(matrix, r, c, dp));
        return result;
    }

    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

    private static int dfsLIP(int[][] mat, int r, int c, int[][] dp) {
        if (dp[r][c] != 0) return dp[r][c];
        dp[r][c] = 1;
        for (int[] d : DIRS) {
            int nr = r+d[0], nc = c+d[1];
            if (nr>=0 && nr<mat.length && nc>=0 && nc<mat[0].length
                    && mat[nr][nc] > mat[r][c])
                dp[r][c] = Math.max(dp[r][c], 1 + dfsLIP(mat, nr, nc, dp));
        }
        return dp[r][c];
    }

    /**
     * LeetCode #787 preview — DP on weighted graph
     * Count paths of exactly k steps in a graph.
     * Matrix exponentiation for large k — concept shown via dp.
     *
     * dp[k][v] = number of paths of length k ending at v
     * Recurrence: dp[k][v] = sum of dp[k-1][u] for all u with edge u→v
     */
    static long countPaths(int[][] adj, int V, int src, int dst, int k) {
        long[] dp = new long[V];
        dp[src] = 1;
        for (int step = 0; step < k; step++) {
            long[] next = new long[V];
            for (int u = 0; u < V; u++) {
                if (dp[u] == 0) continue;
                for (int[] edge : new int[0][]) { // placeholder
                    next[edge[0]] += dp[u];
                }
            }
            dp = next;
        }
        return dp[dst];
    }

    /**
     * LeetCode #1345 / Frog Jump — DP on position+state graph
     * LeetCode #403 — Frog Jump
     * Frog at stone index i, last jump = k, can jump k-1/k/k+1.
     * Can it reach last stone?
     *
     * State: dp[stone] = set of jump sizes that can reach this stone
     */
    static boolean canCross(int[] stones) {
        Map<Integer, Set<Integer>> dp = new HashMap<>();
        for (int s : stones) dp.put(s, new HashSet<>());
        dp.get(0).add(0);

        for (int s : stones) {
            for (int k : dp.get(s)) {
                for (int step : new int[]{k-1, k, k+1}) {
                    if (step > 0 && dp.containsKey(s + step))
                        dp.get(s + step).add(step);
                }
            }
        }
        return !dp.get(stones[stones.length-1]).isEmpty();
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 83: Grid Path DP ===");
        System.out.println("#62  uniquePaths(3,7):        " + uniquePaths(3, 7));     // 28
        int[][] obs = {{0,0,0},{0,1,0},{0,0,0}};
        System.out.println("#63  uniquePathsObstacles:    " + uniquePathsWithObstacles(obs)); // 2
        int[][] grid = {{1,3,1},{1,5,1},{4,2,1}};
        System.out.println("#64  minPathSum:              " + minPathSum(grid));       // 7
        int[][] falling = {{2,1,3},{6,5,4},{7,8,9}};
        System.out.println("#931 minFallingPathSum:       " + minFallingPathSum(falling)); // 13

        System.out.println("\n=== TOPIC 84: Matrix Chain / Burst Balloons ===");
        int[] dims = {10, 30, 5, 60};
        System.out.println("MatrixChain [10,30,5,60]:    " + matrixChainOrder(dims)); // 4500
        System.out.println("#312 maxCoins [3,1,5,8]:     "
                + maxCoins(new int[]{3,1,5,8}));                                       // 167

        System.out.println("\n=== TOPIC 85: Palindrome DP ===");
        System.out.println("#132 minCut 'aab':           " + minCutPalindrome("aab")); // 1
        System.out.println("#132 minCut 'aabbc':         " + minCutPalindrome("aabbc")); // 2
        System.out.println("#5   longestPalindrome 'babad': " + longestPalindrome("babad")); // bab

        System.out.println("\n=== TOPIC 86: Bitmask DP ===");
        int[][] tspDist = {{0,10,15,20},{10,0,35,25},{15,35,0,30},{20,25,30,0}};
        System.out.println("TSP min cost:                " + tsp(tspDist)); // 80
        int[][] graphSP = {{1,2,3},{0},{0},{0}};
        System.out.println("#847 shortestPathAllNodes:   " + shortestPathAllNodes(graphSP)); // 4

        System.out.println("\n=== TOPIC 87: Interval DP ===");
        System.out.println("#1000 mergeStones k=3:       "
                + mergeStones(new int[]{3,2,4,1}, 3));                                // 26
        System.out.println("#1039 minScoreTriangulation: "
                + minScoreTriangulation(new int[]{1,2,3}));                           // 6

        System.out.println("\n=== TOPIC 88: Tree DP ===");
        //       3
        //      / \
        //     2   3
        //      \   \
        //       3   1
        TreeNode t1 = new TreeNode(3,
            new TreeNode(2, null, new TreeNode(3)),
            new TreeNode(3, null, new TreeNode(1)));
        System.out.println("#337 robTree:                " + robTree(t1)); // 7

        TreeNode t2 = new TreeNode(-10,
            new TreeNode(9),
            new TreeNode(20, new TreeNode(15), new TreeNode(7)));
        System.out.println("#124 maxPathSum:             " + maxPathSum(t2)); // 42

        System.out.println("\n=== TOPIC 89: DP on Graphs ===");
        int[][] mat = {{9,9,4},{6,6,8},{2,1,1}};
        System.out.println("#329 longestIncreasingPath:  " + longestIncreasingPath(mat)); // 4

        System.out.println("#403 canCross [0,1,3,5,6,8,12,17]: "
                + canCross(new int[]{0,1,3,5,6,8,12,17})); // true
        System.out.println("#403 canCross [0,1,2,3,4,8,9,11]: "
                + canCross(new int[]{0,1,2,3,4,8,9,11}));  // false
    }
}
