package leetcode_practice;

import java.util.*;

/**
 * LEETCODE PRACTICE -- SESSION 06 (FINAL)
 * Pattern: BACKTRACKING & GREEDY
 *
 * Backtracking Mental Model:
 *   choose -> explore -> unchoose  (3-step template)
 *   Prune early: skip invalid states before recursing.
 *   Key questions:
 *     1. What is a "choice" at each step?
 *     2. When is a "state" complete? (base case)
 *     3. What makes a state invalid? (pruning)
 *
 * Greedy Mental Model:
 *   Make the locally optimal choice at each step.
 *   Must PROVE greedy is correct (exchange argument):
 *     "If I swap greedy choice with any other, result doesn't improve."
 *   Clue words: minimum/maximum, intervals, jumps, partitions.
 *
 * Problems:
 *   Backtracking Medium: #39  Combination Sum
 *                        #40  Combination Sum II (with duplicates)
 *                        #46  Permutations
 *                        #79  Word Search
 *   Backtracking Hard:   #51  N-Queens
 *   Greedy Easy:         #455 Assign Cookies
 *   Greedy Medium:       #45  Jump Game II
 *                        #55  Jump Game
 *                        #435 Non-overlapping Intervals
 *                        #452 Minimum Arrows to Burst Balloons
 *                        #763 Partition Labels
 */
public class Session06_BacktrackingGreedy {

    public static void main(String[] args) {
        System.out.println("=== SESSION 06: BACKTRACKING & GREEDY ===\n");
        testBacktracking();
        testGreedy();
        printFinalStats();
    }

    // =========================================================================
    // BACKTRACKING
    // =========================================================================

    // #39 Combination Sum
    // Find all unique combinations that sum to target. Each number can be reused.
    // Template: choose candidate[i], recurse with i (allow reuse), unchoose.
    // Prune: if remaining < 0 stop; sort first to enable pruning.
    static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrackCombSum(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    static void backtrackCombSum(int[] cands, int remain, int start,
                                  List<Integer> path, List<List<Integer>> result) {
        if (remain == 0) { result.add(new ArrayList<>(path)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > remain) break; // sorted => no point continuing
            path.add(cands[i]);
            backtrackCombSum(cands, remain - cands[i], i, path, result); // i not i+1 (reuse)
            path.remove(path.size() - 1);
        }
    }

    // #40 Combination Sum II (each number used ONCE, no duplicate combinations)
    // Duplicate handling: sort + skip nums[i] == nums[i-1] at same recursion level.
    // "Same level" means: i > start (not the first pick at this depth).
    static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrackCombSum2(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    static void backtrackCombSum2(int[] cands, int remain, int start,
                                   List<Integer> path, List<List<Integer>> result) {
        if (remain == 0) { result.add(new ArrayList<>(path)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > remain) break;
            if (i > start && cands[i] == cands[i - 1]) continue; // skip dup at same level
            path.add(cands[i]);
            backtrackCombSum2(cands, remain - cands[i], i + 1, path, result); // i+1: no reuse
            path.remove(path.size() - 1);
        }
    }

    // #46 Permutations (no duplicates)
    // At each step: pick any unused number.
    // Track used with boolean[] or swap-in-place.
    // Swap approach: swap nums[i] with nums[start], recurse, swap back.
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackPerm(nums, 0, result);
        return result;
    }

    static void backtrackPerm(int[] nums, int start, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int n : nums) perm.add(n);
            result.add(perm);
            return;
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);           // choose
            backtrackPerm(nums, start + 1, result); // explore
            swap(nums, start, i);           // unchoose
        }
    }

    static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    // #79 Word Search
    // DFS on grid: mark cell visited by replacing with '#', restore after.
    // Prune: if grid[r][c] != word[idx] return false immediately.
    static boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                if (dfsWord(board, word, r, c, 0)) return true;
        return false;
    }

    static boolean dfsWord(char[][] board, String word, int r, int c, int idx) {
        if (idx == word.length()) return true; // all chars matched
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
        if (board[r][c] != word.charAt(idx)) return false;
        char tmp = board[r][c];
        board[r][c] = '#';  // mark visited
        boolean found = dfsWord(board, word, r+1, c, idx+1)
                     || dfsWord(board, word, r-1, c, idx+1)
                     || dfsWord(board, word, r, c+1, idx+1)
                     || dfsWord(board, word, r, c-1, idx+1);
        board[r][c] = tmp;  // restore
        return found;
    }

    // #51 N-Queens (Hard)
    // Place N queens on N×N board so none attack each other.
    // Track: cols, diag1 (r-c), diag2 (r+c) as boolean/HashSet.
    // At each row, try each column. If safe, place queen and recurse to next row.
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        boolean[] cols = new boolean[n], diag1 = new boolean[2*n], diag2 = new boolean[2*n];
        backtrackQueens(n, 0, new int[n], cols, diag1, diag2, result);
        return result;
    }

    static void backtrackQueens(int n, int row, int[] queens,
                                 boolean[] cols, boolean[] diag1, boolean[] diag2,
                                 List<List<String>> result) {
        if (row == n) {
            List<String> board = new ArrayList<>();
            for (int q : queens) {
                char[] line = new char[n];
                Arrays.fill(line, '.');
                line[q] = 'Q';
                board.add(new String(line));
            }
            result.add(board);
            return;
        }
        for (int col = 0; col < n; col++) {
            int d1 = row - col + n, d2 = row + col;
            if (cols[col] || diag1[d1] || diag2[d2]) continue; // attacked
            // Place queen
            queens[row] = col; cols[col] = true; diag1[d1] = true; diag2[d2] = true;
            backtrackQueens(n, row + 1, queens, cols, diag1, diag2, result);
            // Remove queen
            cols[col] = false; diag1[d1] = false; diag2[d2] = false;
        }
    }

    static void testBacktracking() {
        System.out.println("--- BACKTRACKING ---");

        System.out.println("#39  combinationSum([2,3,6,7], 7)     = "
            + combinationSum(new int[]{2,3,6,7}, 7));   // [[2,2,3],[7]]
        System.out.println("#39  combinationSum([2,3,5], 8)       = "
            + combinationSum(new int[]{2,3,5}, 8));     // [[2,2,2,2],[2,3,3],[3,5]]

        System.out.println("#40  combinationSum2([10,1,2,7,6,1,5],8) = "
            + combinationSum2(new int[]{10,1,2,7,6,1,5}, 8)); // [[1,1,6],[1,2,5],[1,7],[2,6]]
        System.out.println("#40  combinationSum2([2,5,2,1,2],5)      = "
            + combinationSum2(new int[]{2,5,2,1,2}, 5));      // [[1,2,2],[5]]

        System.out.println("#46  permute([1,2,3]) count = "
            + permute(new int[]{1,2,3}).size());  // 6
        System.out.println("#46  permute([1,2,3])       = "
            + permute(new int[]{1,2,3}));

        char[][] board = {
            {'A','B','C','E'},
            {'S','F','C','S'},
            {'A','D','E','E'}
        };
        System.out.println("#79  exist(board, \"ABCCED\") = " + exist(board, "ABCCED")); // true
        System.out.println("#79  exist(board, \"SEE\")    = " + exist(board, "SEE"));    // true
        System.out.println("#79  exist(board, \"ABCB\")   = " + exist(board, "ABCB"));   // false

        List<List<String>> queens4 = solveNQueens(4);
        System.out.println("#51  solveNQueens(4) count = " + queens4.size());  // 2
        System.out.println("     Solution 1: " + queens4.get(0));
        System.out.println();
    }

    // =========================================================================
    // GREEDY
    // =========================================================================

    // #455 Assign Cookies -- Easy
    // Greedily match smallest sufficient cookie to least greedy child.
    // Sort both, use two pointers.
    static int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g); Arrays.sort(s);
        int child = 0, cookie = 0;
        while (child < g.length && cookie < s.length) {
            if (s[cookie] >= g[child]) child++; // cookie satisfies child
            cookie++; // always move to next cookie
        }
        return child;
    }

    // #55 Jump Game
    // Can you reach the last index?
    // Greedy: track maxReach. If current index > maxReach => stuck.
    static boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) return false;      // can't reach index i
            maxReach = Math.max(maxReach, i + nums[i]);
        }
        return true;
    }

    // #45 Jump Game II -- minimum jumps to reach last index
    // Greedy BFS layers: treat each jump as expanding a "current reach" window.
    // When we exhaust current window, we MUST jump => jumps++, expand to maxReach.
    static int jump(int[] nums) {
        int jumps = 0, curEnd = 0, maxReach = 0;
        for (int i = 0; i < nums.length - 1; i++) { // stop before last
            maxReach = Math.max(maxReach, i + nums[i]);
            if (i == curEnd) { // exhausted current jump range
                jumps++;
                curEnd = maxReach;
            }
        }
        return jumps;
    }

    // #435 Non-overlapping Intervals
    // Find minimum intervals to REMOVE so none overlap.
    // Greedy: sort by END time. Keep intervals with earliest end (more room for rest).
    // Count removals = n - kept.
    static int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[1])); // sort by end
        int kept = 1, prevEnd = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= prevEnd) { // no overlap
                kept++;
                prevEnd = intervals[i][1];
            }
            // else: overlap -> skip (remove) this interval
        }
        return intervals.length - kept;
    }

    // #452 Minimum Arrows to Burst Balloons
    // Balloons = intervals [xstart, xend]. Arrow at x bursts all balloons covering x.
    // SAME greedy as #435: sort by end, shoot at end of first balloon.
    // An arrow at prevEnd bursts all balloons that start <= prevEnd.
    static int findMinArrowShots(int[][] points) {
        if (points.length == 0) return 0;
        Arrays.sort(points, Comparator.comparingInt(a -> a[1]));
        int arrows = 1, prevEnd = points[0][1];
        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > prevEnd) { // balloon not burst by current arrow
                arrows++;
                prevEnd = points[i][1];
            }
        }
        return arrows;
    }

    // #763 Partition Labels
    // Partition string so each letter appears in at most one part. Maximize parts.
    // Greedy:
    //   1. Record last occurrence of each character.
    //   2. Scan left-to-right; extend current partition end to max(end, last[c]).
    //   3. When i == end, current partition is complete.
    static List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;
        List<Integer> result = new ArrayList<>();
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']); // extend partition
            if (i == end) { // partition complete
                result.add(end - start + 1);
                start = i + 1;
            }
        }
        return result;
    }

    static void testGreedy() {
        System.out.println("--- GREEDY ---");

        System.out.println("#455 findContentChildren([1,2,3],[1,1])   = "
            + findContentChildren(new int[]{1,2,3}, new int[]{1,1})); // 1
        System.out.println("#455 findContentChildren([1,2],[1,2,3])   = "
            + findContentChildren(new int[]{1,2}, new int[]{1,2,3})); // 2

        System.out.println("#55  canJump([2,3,1,1,4]) = " + canJump(new int[]{2,3,1,1,4})); // true
        System.out.println("#55  canJump([3,2,1,0,4]) = " + canJump(new int[]{3,2,1,0,4})); // false

        System.out.println("#45  jump([2,3,1,1,4])    = " + jump(new int[]{2,3,1,1,4})); // 2
        System.out.println("#45  jump([2,3,0,1,4])    = " + jump(new int[]{2,3,0,1,4})); // 2
        System.out.println("#45  jump([1,2,3])         = " + jump(new int[]{1,2,3}));    // 2

        System.out.println("#435 eraseOverlapIntervals([[1,2],[2,3],[3,4],[1,3]]) = "
            + eraseOverlapIntervals(new int[][]{{1,2},{2,3},{3,4},{1,3}})); // 1
        System.out.println("#435 eraseOverlapIntervals([[1,2],[1,2],[1,2]])       = "
            + eraseOverlapIntervals(new int[][]{{1,2},{1,2},{1,2}}));       // 2

        System.out.println("#452 findMinArrowShots([[10,16],[2,8],[1,6],[7,12]]) = "
            + findMinArrowShots(new int[][]{{10,16},{2,8},{1,6},{7,12}})); // 2
        System.out.println("#452 findMinArrowShots([[1,2],[3,4],[5,6],[7,8]])    = "
            + findMinArrowShots(new int[][]{{1,2},{3,4},{5,6},{7,8}}));    // 4

        System.out.println("#763 partitionLabels(\"ababcbacadefegdehijhklij\") = "
            + partitionLabels("ababcbacadefegdehijhklij")); // [9,7,8]
        System.out.println("#763 partitionLabels(\"eccbbbbdec\")               = "
            + partitionLabels("eccbbbbdec"));               // [10]

        System.out.println();
        System.out.println("PATTERN SUMMARY -- Backtracking & Greedy:");
        System.out.println("  Backtracking template: choose -> recurse -> unchoose");
        System.out.println("  Avoid dup combinations: sort + skip nums[i]==nums[i-1] at same level");
        System.out.println("  Permutation swap:       swap(start,i) -> recurse(start+1) -> swap back");
        System.out.println("  Grid DFS:               mark '#', recurse 4 dirs, restore");
        System.out.println("  N-Queens:               track cols + 2 diagonals (r-c) and (r+c)");
        System.out.println("  Greedy intervals:       sort by END -> keep earliest-ending = most room");
        System.out.println("  Jump game greedy:       BFS layers, jump when window exhausted");
        System.out.println("  Partition labels:       last[] index array + extend end greedily");
    }

    // =========================================================================
    // FINAL STATS
    // =========================================================================
    static void printFinalStats() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  LEETCODE PRACTICE SESSIONS COMPLETE!");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("Session 01  Two Pointers & Sliding Window   10 problems");
        System.out.println("Session 02  Binary Search                   10 problems");
        System.out.println("Session 03  Stack & Trees                   12 problems");
        System.out.println("Session 04  Dynamic Programming             11 problems");
        System.out.println("Session 05  Graphs                          10 problems");
        System.out.println("Session 06  Backtracking & Greedy           11 problems");
        System.out.println("─".repeat(60));
        System.out.println("Total practice problems solved:             64 problems");
        System.out.println("Theory topics (Phase 1-6):                 108 topics");
        System.out.println();
        System.out.println("Interview Pattern Quick Reference:");
        System.out.println("  Sorted array / two targets   -> Two Pointers");
        System.out.println("  Substring / subarray bounds  -> Sliding Window");
        System.out.println("  O(log n) search / minimize   -> Binary Search on Answer");
        System.out.println("  Next greater/smaller element -> Monotonic Stack");
        System.out.println("  Subtree info / path sum      -> DFS return-up pattern");
        System.out.println("  Enumerate all combinations   -> Backtracking + prune");
        System.out.println("  Optimal local = optimal glob -> Greedy + sort by end");
        System.out.println("  Overlapping subproblems      -> DP (define state first!)");
        System.out.println("  Shortest path unweighted     -> BFS");
        System.out.println("  Shortest path weighted       -> Dijkstra (PQ)");
        System.out.println("  Cycle / connectivity         -> Union-Find");
        System.out.println("  Dependency ordering          -> Topological Sort (Kahn's)");
    }
}
