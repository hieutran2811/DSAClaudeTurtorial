package phase3_algorithms.backtracking;

import java.util.*;

/**
 * PHASE 3.3 — RECURSION & BACKTRACKING
 * =======================================
 * Topics covered:
 *  48. Recursion fundamentals & call stack
 *  49. Memoization (top-down DP)
 *  50. Permutations & combinations (subsets, combinations, permutations)
 *  51. N-Queens
 *  52. Sudoku solver
 *
 * BACKTRACKING TEMPLATE — memorize this:
 * ┌──────────────────────────────────────────────────────────┐
 * │  void backtrack(state, choices) {                         │
 * │      if (isSolution(state)) { collect(state); return; }  │
 * │      for (choice : choices) {                             │
 * │          if (!isValid(state, choice)) continue;           │
 * │          makeChoice(state, choice);   // choose           │
 * │          backtrack(state, nextChoices);                   │
 * │          undoChoice(state, choice);   // un-choose        │
 * │      }                                                    │
 * │  }                                                        │
 * └──────────────────────────────────────────────────────────┘
 *
 * KEY INSIGHT: Backtracking = DFS on a decision tree.
 *   At each node: make a choice → recurse → undo choice.
 *   Pruning eliminates branches early → much faster than brute force.
 */
public class Backtracking {

    // =========================================================
    // TOPIC 48 — RECURSION FUNDAMENTALS
    // =========================================================

    /**
     * Classic recursion examples demonstrating call stack behavior.
     * Every recursive call = new stack frame with its own local variables.
     * Base case stops the recursion.
     */

    // Factorial: O(n) time, O(n) space (call stack)
    static long factorial(int n) {
        if (n <= 1) return 1;           // base case
        return n * factorial(n - 1);   // recursive case
    }

    // Fibonacci: naive O(2^n) — shows why memoization matters
    static int fibNaive(int n) {
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    // Power: O(log n) — divide & conquer recursion
    static long power(long base, int exp) {
        if (exp == 0) return 1;
        long half = power(base, exp / 2);
        return exp % 2 == 0 ? half * half : half * half * base;
    }

    // Flatten nested list recursively
    static void flatten(int[] arr, int i, List<Integer> result) {
        if (i == arr.length) return;
        result.add(arr[i]);
        flatten(arr, i + 1, result);
    }

    // =========================================================
    // TOPIC 49 — MEMOIZATION (top-down DP)
    // =========================================================

    /**
     * Memoization = recursion + cache.
     * If subproblem already solved, return cached answer.
     * Converts exponential → polynomial time.
     *
     * fib naive:  O(2^n)
     * fib memo:   O(n) time, O(n) space
     */
    static Map<Integer, Long> memo = new HashMap<>();

    static long fibMemo(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long result = fibMemo(n - 1) + fibMemo(n - 2);
        memo.put(n, result);
        return result;
    }

    /**
     * LeetCode #70 — Climbing Stairs
     * Ways to climb n stairs taking 1 or 2 steps at a time.
     * dp[n] = dp[n-1] + dp[n-2]  (Fibonacci-like)
     */
    static int climbStairs(int n) {
        int[] dp = new int[n + 1];
        dp[0] = 1; dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i-1] + dp[i-2];
        return dp[n];
    }

    /**
     * LeetCode #322 — Coin Change (memoization)
     * Minimum coins to make amount.
     * dp(amount) = 1 + min(dp(amount - coin)) for each coin
     */
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // "infinity"
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) dp[i] = Math.min(dp[i], 1 + dp[i - coin]);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // =========================================================
    // TOPIC 50 — PERMUTATIONS & COMBINATIONS
    // =========================================================

    // ---------------------------------------------------------
    // SUBSETS — LeetCode #78
    // ---------------------------------------------------------
    /**
     * Generate all 2^n subsets of nums (no duplicates).
     *
     * Decision tree: for each element, choose INCLUDE or EXCLUDE.
     * At each level i: branch left (skip nums[i]), branch right (add nums[i]).
     *
     * Time: O(2^n * n)   Space: O(n) recursion depth
     */
    static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSubsets(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackSubsets(int[] nums, int start,
                                          List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current)); // every node is a valid subset
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);                           // choose
            backtrackSubsets(nums, i + 1, current, result); // recurse
            current.remove(current.size() - 1);             // un-choose
        }
    }

    /**
     * Subsets II — LeetCode #90
     * Input has duplicates; return unique subsets only.
     * Fix: sort first, then skip duplicate elements at same recursion level.
     */
    static List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        backtrackSubsetsDup(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackSubsetsDup(int[] nums, int start,
                                             List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue; // skip duplicate
            current.add(nums[i]);
            backtrackSubsetsDup(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // ---------------------------------------------------------
    // COMBINATIONS — LeetCode #77
    // ---------------------------------------------------------
    /**
     * Generate all C(n,k) combinations of numbers 1..n choosing k.
     *
     * Pruning: if remaining elements < needed, stop early.
     * Time: O(C(n,k) * k)
     */
    static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombine(n, k, 1, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackCombine(int n, int k, int start,
                                          List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) { result.add(new ArrayList<>(current)); return; }

        // Pruning: need (k - current.size()) more elements, only (n - i + 1) remain
        for (int i = start; i <= n - (k - current.size()) + 1; i++) {
            current.add(i);
            backtrackCombine(n, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Combination Sum — LeetCode #39
     * Find all combinations that sum to target (reuse allowed).
     */
    static List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombSum(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackCombSum(int[] cands, int remaining, int start,
                                          List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) { result.add(new ArrayList<>(current)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > remaining) break; // sorted → no point continuing
            current.add(cands[i]);
            backtrackCombSum(cands, remaining - cands[i], i, current, result); // i not i+1 (reuse)
            current.remove(current.size() - 1);
        }
    }

    /**
     * Combination Sum II — LeetCode #40
     * Each number used once; no duplicate combinations.
     */
    static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombSum2(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackCombSum2(int[] cands, int remaining, int start,
                                           List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) { result.add(new ArrayList<>(current)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > remaining) break;
            if (i > start && cands[i] == cands[i - 1]) continue; // skip dup at same level
            current.add(cands[i]);
            backtrackCombSum2(cands, remaining - cands[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // ---------------------------------------------------------
    // PERMUTATIONS — LeetCode #46
    // ---------------------------------------------------------
    /**
     * Generate all n! permutations of distinct numbers.
     *
     * Approach: swap-based (modify array in-place, backtrack by swapping back).
     * At position i, try placing each nums[i..n-1] at position i.
     *
     * Time: O(n! * n)   Space: O(n)
     */
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackPermute(nums, 0, result);
        return result;
    }

    private static void backtrackPermute(int[] nums, int start, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> perm = new ArrayList<>();
            for (int n : nums) perm.add(n);
            result.add(perm);
            return;
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);                       // choose
            backtrackPermute(nums, start + 1, result);  // recurse
            swap(nums, start, i);                       // un-choose
        }
    }

    /**
     * Permutations II — LeetCode #47
     * Input has duplicates; return unique permutations.
     * Fix: sort + use `used[]` array + skip duplicate at same level.
     */
    static List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        backtrackPermuteUnique(nums, new boolean[nums.length], new ArrayList<>(), result);
        return result;
    }

    private static void backtrackPermuteUnique(int[] nums, boolean[] used,
                                                List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) { result.add(new ArrayList<>(current)); return; }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            // Skip duplicate: same value, previous copy not used (would create same branch)
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
            used[i] = true;
            current.add(nums[i]);
            backtrackPermuteUnique(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    // ---------------------------------------------------------
    // LETTER COMBINATIONS — LeetCode #17
    // ---------------------------------------------------------
    static List<String> letterCombinations(String digits) {
        if (digits.isEmpty()) return new ArrayList<>();
        String[] map = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        List<String> result = new ArrayList<>();
        backtrackPhone(digits, map, 0, new StringBuilder(), result);
        return result;
    }

    private static void backtrackPhone(String digits, String[] map, int i,
                                        StringBuilder sb, List<String> result) {
        if (i == digits.length()) { result.add(sb.toString()); return; }
        for (char c : map[digits.charAt(i) - '0'].toCharArray()) {
            sb.append(c);
            backtrackPhone(digits, map, i + 1, sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    // =========================================================
    // TOPIC 51 — N-QUEENS
    // =========================================================

    /**
     * LeetCode #51 — N-Queens
     * Place N queens on NxN board so no two queens attack each other.
     * (No same row, column, or diagonal)
     *
     * Approach: place one queen per row, track attacked columns & diagonals.
     *   cols:    column i is occupied
     *   diag1:   "/" diagonal  → row - col is constant
     *   diag2:   "\" diagonal  → row + col is constant
     *
     * Time: O(N!)   Space: O(N)
     */
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n]; // queens[row] = col where queen is placed
        Arrays.fill(queens, -1);
        Set<Integer> cols  = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // row - col
        Set<Integer> diag2 = new HashSet<>(); // row + col
        backtrackQueens(n, 0, queens, cols, diag1, diag2, result);
        return result;
    }

    private static void backtrackQueens(int n, int row, int[] queens,
                                         Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2,
                                         List<List<String>> result) {
        if (row == n) {
            result.add(buildBoard(queens, n));
            return;
        }
        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col))
                continue; // attacked → skip (pruning)

            queens[row] = col;
            cols.add(col); diag1.add(row - col); diag2.add(row + col);

            backtrackQueens(n, row + 1, queens, cols, diag1, diag2, result);

            queens[row] = -1;
            cols.remove(col); diag1.remove(row - col); diag2.remove(row + col);
        }
    }

    private static List<String> buildBoard(int[] queens, int n) {
        List<String> board = new ArrayList<>();
        for (int row = 0; row < n; row++) {
            char[] line = new char[n];
            Arrays.fill(line, '.');
            line[queens[row]] = 'Q';
            board.add(new String(line));
        }
        return board;
    }

    /** LeetCode #52 — N-Queens II: just count solutions */
    static int totalNQueens(int n) {
        return countQueens(n, 0, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    private static int countQueens(int n, int row,
                                    Set<Integer> cols, Set<Integer> d1, Set<Integer> d2) {
        if (row == n) return 1;
        int count = 0;
        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || d1.contains(row - col) || d2.contains(row + col)) continue;
            cols.add(col); d1.add(row - col); d2.add(row + col);
            count += countQueens(n, row + 1, cols, d1, d2);
            cols.remove(col); d1.remove(row - col); d2.remove(row + col);
        }
        return count;
    }

    // =========================================================
    // TOPIC 52 — SUDOKU SOLVER — LeetCode #37
    // =========================================================

    /**
     * Fill a 9x9 Sudoku board (empty cells = '.').
     * Rules: each row, column, and 3x3 box contains digits 1-9 exactly once.
     *
     * Approach:
     *   Find next empty cell → try digits 1-9 → check validity → recurse.
     *   If no digit works → backtrack.
     *
     * Optimization: track row/col/box sets for O(1) validity check.
     *
     * Time: O(9^(empty cells))  worst case — but pruning makes it very fast.
     */
    static void solveSudoku(char[][] board) {
        // Pre-fill constraint sets
        Set<Character>[] rows  = new HashSet[9];
        Set<Character>[] cols  = new HashSet[9];
        Set<Character>[] boxes = new HashSet[9];
        for (int i = 0; i < 9; i++) {
            rows[i]  = new HashSet<>();
            cols[i]  = new HashSet<>();
            boxes[i] = new HashSet<>();
        }
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board[r][c] != '.') {
                    char ch = board[r][c];
                    rows[r].add(ch);
                    cols[c].add(ch);
                    boxes[boxIndex(r, c)].add(ch);
                }
            }
        }
        backtrackSudoku(board, rows, cols, boxes, 0, 0);
    }

    private static boolean backtrackSudoku(char[][] board,
                                            Set<Character>[] rows, Set<Character>[] cols,
                                            Set<Character>[] boxes, int row, int col) {
        // Advance to next empty cell
        while (row < 9 && board[row][col] != '.') {
            col++;
            if (col == 9) { col = 0; row++; }
        }
        if (row == 9) return true; // all cells filled → solved!

        int box = boxIndex(row, col);
        for (char d = '1'; d <= '9'; d++) {
            if (rows[row].contains(d) || cols[col].contains(d) || boxes[box].contains(d))
                continue; // invalid placement

            // Place digit
            board[row][col] = d;
            rows[row].add(d); cols[col].add(d); boxes[box].add(d);

            // Recurse to next cell
            int nextCol = col + 1, nextRow = row;
            if (nextCol == 9) { nextCol = 0; nextRow++; }

            if (backtrackSudoku(board, rows, cols, boxes, nextRow, nextCol)) return true;

            // Undo
            board[row][col] = '.';
            rows[row].remove(d); cols[col].remove(d); boxes[box].remove(d);
        }
        return false; // trigger backtrack
    }

    private static int boxIndex(int r, int c) {
        return (r / 3) * 3 + (c / 3);
    }

    // =========================================================
    // BONUS — WORD SEARCH — LeetCode #79
    // =========================================================

    /**
     * Find if word exists in 2D grid (move up/down/left/right, no reuse).
     * DFS + backtracking: mark cell as visited, recurse, unmark.
     */
    static boolean wordSearch(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (dfWord(board, word, r, c, 0)) return true;
        return false;
    }

    private static boolean dfWord(char[][] board, String word, int r, int c, int i) {
        if (i == word.length()) return true;
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
        if (board[r][c] != word.charAt(i)) return false;

        char tmp = board[r][c];
        board[r][c] = '#'; // mark visited
        boolean found = dfWord(board, word, r+1, c, i+1)
                     || dfWord(board, word, r-1, c, i+1)
                     || dfWord(board, word, r, c+1, i+1)
                     || dfWord(board, word, r, c-1, i+1);
        board[r][c] = tmp; // unmark (backtrack)
        return found;
    }

    // =========================================================
    // BONUS — PALINDROME PARTITIONING — LeetCode #131
    // =========================================================

    /**
     * Partition string s so every substring is a palindrome.
     * At each index, try all valid palindrome prefixes, recurse on remainder.
     */
    static List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrackPalin(s, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackPalin(String s, int start,
                                        List<String> current, List<List<String>> result) {
        if (start == s.length()) { result.add(new ArrayList<>(current)); return; }
        for (int end = start + 1; end <= s.length(); end++) {
            String sub = s.substring(start, end);
            if (isPalindrome(sub)) {
                current.add(sub);
                backtrackPalin(s, end, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    private static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
        return true;
    }

    // =========================================================
    // HELPER
    // =========================================================
    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 48: Recursion Fundamentals ===");
        System.out.println("factorial(10): " + factorial(10));
        System.out.println("power(2, 10):  " + power(2, 10));

        System.out.println("\n=== TOPIC 49: Memoization ===");
        memo.clear();
        System.out.println("fib(10) naive: " + fibNaive(10));
        System.out.println("fib(10) memo:  " + fibMemo(10));
        System.out.println("fib(50) memo:  " + fibMemo(50));
        System.out.println("#70 climbStairs(5): " + climbStairs(5));  // 8
        System.out.println("#322 coinChange([1,5,11], 15): "
                + coinChange(new int[]{1, 5, 11}, 15));  // 3 (5+5+5 or 11+... wait: 11+4? no. 5+5+5=15 → 3 coins)

        System.out.println("\n=== TOPIC 50: Subsets / Combinations / Permutations ===");
        System.out.println("#78  subsets([1,2,3]):       " + subsets(new int[]{1,2,3}));
        System.out.println("#90  subsetsWithDup([1,2,2]):" + subsetsWithDup(new int[]{1,2,2}));
        System.out.println("#77  combine(4,2):           " + combine(4, 2));
        System.out.println("#39  combSum([2,3,6,7] t=7): " + combinationSum(new int[]{2,3,6,7}, 7));
        System.out.println("#40  combSum2([2,5,2,1,2] t=5):" + combinationSum2(new int[]{2,5,2,1,2}, 5));
        System.out.println("#46  permute([1,2,3]):       " + permute(new int[]{1,2,3}));
        System.out.println("#47  permuteUnique([1,1,2]): " + permuteUnique(new int[]{1,1,2}));
        System.out.println("#17  letterComb(\"23\"):       " + letterCombinations("23"));

        System.out.println("\n=== TOPIC 51: N-Queens ===");
        System.out.println("#51  N=4 solutions: " + solveNQueens(4).size()); // 2
        for (List<String> board : solveNQueens(4)) {
            System.out.println("  --- solution ---");
            for (String row : board) System.out.println("  " + row);
        }
        System.out.println("#52  N=8 total solutions: " + totalNQueens(8)); // 92

        System.out.println("\n=== TOPIC 52: Sudoku Solver ===");
        char[][] board = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        solveSudoku(board);
        System.out.println("Solved board:");
        for (char[] row : board) System.out.println("  " + Arrays.toString(row));

        System.out.println("\n=== BONUS: Word Search ===");
        char[][] grid = {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}};
        System.out.println("#79 'ABCCED': " + wordSearch(grid, "ABCCED")); // true
        System.out.println("#79 'SEE':    " + wordSearch(grid, "SEE"));    // true
        System.out.println("#79 'ABCB':   " + wordSearch(grid, "ABCB"));   // false

        System.out.println("\n=== BONUS: Palindrome Partitioning ===");
        System.out.println("#131 partition('aab'): " + partition("aab"));
    }
}
