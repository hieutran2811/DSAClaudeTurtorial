package phase5_dp;

import java.util.*;

/**
 * PHASE 5.1 + 5.2 — DP FUNDAMENTALS & CLASSIC PATTERNS
 * =======================================================
 * Topics covered:
 *  73. Memoization (top-down)
 *  74. Tabulation (bottom-up)
 *  75. State definition & recurrence relation
 *  76. Space optimization
 *  77. 1D DP — Fibonacci, climbing stairs, house robber
 *  78. 0/1 Knapsack
 *  79. Unbounded Knapsack
 *  80. Longest Common Subsequence (LCS)
 *  81. Longest Increasing Subsequence (LIS)
 *  82. Edit Distance
 *
 * DP THINKING FRAMEWORK (4 steps):
 * ┌───────────────────────────────────────────────────────────┐
 * │ 1. STATE    : what does dp[i] (or dp[i][j]) represent?    │
 * │ 2. RECURRENCE: how does dp[i] relate to smaller states?   │
 * │ 3. BASE CASE : what are the trivial answers (dp[0], etc)? │
 * │ 4. ORDER     : which states to compute first?             │
 * └───────────────────────────────────────────────────────────┘
 *
 * Top-down (memo) vs Bottom-up (tabulation):
 *   Top-down  : natural recursion + cache; only computes needed states
 *   Bottom-up : iterative, better cache performance, easier space optimize
 */
public class DPFundamentals {

    // =========================================================
    // TOPIC 73 — MEMOIZATION (Top-down DP)
    // =========================================================

    /**
     * Fibonacci with memoization — the classic example.
     * Without memo: O(2^n) time (exponential, recomputes same values)
     * With memo:    O(n) time, O(n) space
     *
     * Every overlapping subproblem computed exactly once.
     */
    static long fibMemo(int n, long[] memo) {
        if (n <= 1) return n;
        if (memo[n] != -1) return memo[n];
        return memo[n] = fibMemo(n-1, memo) + fibMemo(n-2, memo);
    }

    /**
     * LeetCode #120 — Triangle (top-down)
     * dp(row, col) = min path sum from (row,col) to bottom
     * dp(row,col) = triangle[row][col] + min(dp(row+1,col), dp(row+1,col+1))
     */
    static int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        Integer[][] memo = new Integer[n][n];
        return topDownTriangle(triangle, 0, 0, memo);
    }

    private static int topDownTriangle(List<List<Integer>> t, int row, int col, Integer[][] memo) {
        if (row == t.size()) return 0;
        if (memo[row][col] != null) return memo[row][col];
        int val = t.get(row).get(col);
        return memo[row][col] = val + Math.min(
            topDownTriangle(t, row+1, col,   memo),
            topDownTriangle(t, row+1, col+1, memo)
        );
    }

    // =========================================================
    // TOPIC 74 — TABULATION (Bottom-up DP)
    // =========================================================

    /**
     * Fibonacci tabulation — fill table from base cases up.
     * State:      dp[i] = i-th Fibonacci number
     * Recurrence: dp[i] = dp[i-1] + dp[i-2]
     * Base:       dp[0]=0, dp[1]=1
     * Order:      left to right
     */
    static long fibTab(int n) {
        if (n <= 1) return n;
        long[] dp = new long[n+1];
        dp[0] = 0; dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i-1] + dp[i-2];
        return dp[n];
    }

    // =========================================================
    // TOPIC 75 — STATE DEFINITION & RECURRENCE
    // =========================================================
    /*
     * The hardest part of DP is defining the right STATE.
     * Examples throughout this file show different state designs:
     *   dp[i]       = answer for first i elements
     *   dp[i][j]    = answer for subproblem on [i..j] or [i items, j capacity]
     *   dp[i][0/1]  = answer at position i with some boolean condition
     *
     * Recurrence examples:
     *   dp[i] = dp[i-1] + dp[i-2]          (Fibonacci, stairs)
     *   dp[i] = max(dp[i-1], dp[i-2]+nums[i]) (House Robber — skip or take)
     *   dp[i][j] = dp[i-1][j-1] + 1        (LCS match)
     *   dp[i][w] = max(dp[i-1][w], dp[i-1][w-wt]+val) (Knapsack)
     */

    // =========================================================
    // TOPIC 76 — SPACE OPTIMIZATION
    // =========================================================

    /**
     * Fibonacci O(1) space — only need last 2 values.
     * When dp[i] only depends on dp[i-1] and dp[i-2]:
     * replace array with two variables.
     */
    static long fibO1(int n) {
        if (n <= 1) return n;
        long prev2 = 0, prev1 = 1;
        for (int i = 2; i <= n; i++) {
            long cur = prev1 + prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    /**
     * 2D DP space optimization: when dp[i][j] only depends on dp[i-1][...],
     * we can use a single 1D array (rolling array).
     * IMPORTANT: traverse inner loop in correct direction to avoid overwriting.
     *   - 0/1 knapsack: traverse RIGHT TO LEFT (avoid using same item twice)
     *   - Unbounded knapsack: traverse LEFT TO RIGHT (allow reuse)
     */

    // =========================================================
    // TOPIC 77 — 1D DP PATTERNS
    // =========================================================

    /**
     * LeetCode #70 — Climbing Stairs
     * State:      dp[i] = number of ways to reach step i
     * Recurrence: dp[i] = dp[i-1] + dp[i-2]  (take 1 or 2 steps)
     * Base:       dp[0]=1, dp[1]=1
     */
    static int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int cur = prev1 + prev2;
            prev2 = prev1; prev1 = cur;
        }
        return prev1;
    }

    /**
     * LeetCode #198 — House Robber
     * State:      dp[i] = max money robbing houses 0..i
     * Recurrence: dp[i] = max(dp[i-1],          // skip house i
     *                         dp[i-2] + nums[i]) // rob house i
     * Key insight: can't rob adjacent houses.
     * Space optimized: O(1)
     */
    static int rob(int[] nums) {
        int prev2 = 0, prev1 = 0;
        for (int num : nums) {
            int cur = Math.max(prev1, prev2 + num);
            prev2 = prev1; prev1 = cur;
        }
        return prev1;
    }

    /**
     * LeetCode #213 — House Robber II (circular)
     * Houses arranged in circle → first and last are adjacent.
     * Solution: run House Robber on [0..n-2] and [1..n-1], take max.
     */
    static int robCircular(int[] nums) {
        if (nums.length == 1) return nums[0];
        return Math.max(
            robRange(nums, 0, nums.length-2),
            robRange(nums, 1, nums.length-1)
        );
    }

    private static int robRange(int[] nums, int l, int r) {
        int prev2 = 0, prev1 = 0;
        for (int i = l; i <= r; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1; prev1 = cur;
        }
        return prev1;
    }

    /**
     * LeetCode #300 preview — Max Alternating Subarray Sum
     * LeetCode #53 — Maximum Subarray (Kadane's)
     * State:      dp[i] = max subarray sum ending at i
     * Recurrence: dp[i] = max(nums[i], dp[i-1] + nums[i])
     *             = either start new subarray at i, or extend previous
     */
    static int maxSubArray(int[] nums) {
        int maxSum = nums[0], cur = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            maxSum = Math.max(maxSum, cur);
        }
        return maxSum;
    }

    /**
     * LeetCode #152 — Maximum Product Subarray
     * State: track both max AND min ending at i (negative × negative = positive)
     * dp_max[i] = max product subarray ending at i
     * dp_min[i] = min product subarray ending at i
     */
    static int maxProduct(int[] nums) {
        int maxProd = nums[0], minProd = nums[0], result = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int tmpMax = Math.max(nums[i], Math.max(maxProd * nums[i], minProd * nums[i]));
            minProd    = Math.min(nums[i], Math.min(maxProd * nums[i], minProd * nums[i]));
            maxProd    = tmpMax;
            result     = Math.max(result, maxProd);
        }
        return result;
    }

    /**
     * LeetCode #139 — Word Break
     * State:      dp[i] = can we segment s[0..i-1] using wordDict?
     * Recurrence: dp[i] = OR over all j < i where dp[j] && s[j..i-1] in dict
     */
    static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length()+1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && dict.contains(s.substring(j, i))) {
                    dp[i] = true; break;
                }
            }
        }
        return dp[s.length()];
    }

    // =========================================================
    // TOPIC 78 — 0/1 KNAPSACK
    // =========================================================

    /**
     * 0/1 Knapsack — each item used at most once.
     *
     * State:      dp[i][w] = max value using first i items with capacity w
     * Recurrence: dp[i][w] = max(dp[i-1][w],               // skip item i
     *                            dp[i-1][w-wt[i]] + val[i]) // take item i
     * Base:       dp[0][w] = 0 (no items)
     *
     * Time: O(n*W)   Space: O(n*W) → optimizable to O(W)
     */
    static int knapsack01(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n+1][capacity+1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i-1][w]; // skip item i
                if (weights[i-1] <= w)
                    dp[i][w] = Math.max(dp[i][w], dp[i-1][w-weights[i-1]] + values[i-1]);
            }
        }
        return dp[n][capacity];
    }

    /** Space-optimized 0/1 Knapsack — O(W) space */
    static int knapsack01Opt(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity+1];
        for (int i = 0; i < weights.length; i++) {
            // RIGHT TO LEFT — prevents using item i twice
            for (int w = capacity; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }

    /**
     * LeetCode #416 — Partition Equal Subset Sum
     * Can we split array into 2 subsets with equal sum?
     * = 0/1 Knapsack: can we select items summing to total/2?
     * dp[w] = can we reach sum w using some subset?
     */
    static boolean canPartition(int[] nums) {
        int total = Arrays.stream(nums).sum();
        if (total % 2 != 0) return false;
        int target = total / 2;

        boolean[] dp = new boolean[target+1];
        dp[0] = true;
        for (int num : nums) {
            for (int w = target; w >= num; w--) { // RIGHT TO LEFT (0/1)
                dp[w] = dp[w] || dp[w - num];
            }
        }
        return dp[target];
    }

    /**
     * LeetCode #494 — Target Sum
     * Assign +/- to each number, count ways to reach target.
     * Reframe: let P = sum of + items, N = sum of - items
     *   P - N = target, P + N = total → P = (total + target) / 2
     * = Count subsets summing to P (0/1 knapsack count variant)
     */
    static int findTargetSumWays(int[] nums, int target) {
        int total = Arrays.stream(nums).sum();
        if ((total + target) % 2 != 0 || Math.abs(target) > total) return 0;
        int goal = (total + target) / 2;

        int[] dp = new int[goal+1];
        dp[0] = 1; // one way to reach sum 0: take nothing
        for (int num : nums) {
            for (int w = goal; w >= num; w--) { // RIGHT TO LEFT
                dp[w] += dp[w - num];
            }
        }
        return dp[goal];
    }

    // =========================================================
    // TOPIC 79 — UNBOUNDED KNAPSACK
    // =========================================================

    /**
     * Unbounded Knapsack — each item can be used UNLIMITED times.
     *
     * Only change from 0/1: traverse LEFT TO RIGHT (allow reuse of same item)
     * Recurrence: dp[w] = max(dp[w], dp[w - wt[i]] + val[i])
     *             when using dp[w - wt[i]], item i may already be included
     */
    static int knapsackUnbounded(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity+1];
        for (int w = 0; w <= capacity; w++) {
            for (int i = 0; i < weights.length; i++) {
                if (weights[i] <= w)
                    dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }

    /**
     * LeetCode #322 — Coin Change (min coins, unbounded)
     * State:      dp[amount] = min coins to make this amount
     * Recurrence: dp[w] = min over all coins c: 1 + dp[w-c]
     * Base:       dp[0] = 0
     * LEFT TO RIGHT (unbounded — reuse same coin)
     */
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount+1];
        Arrays.fill(dp, amount+1); // "infinity"
        dp[0] = 0;
        for (int w = 1; w <= amount; w++) {
            for (int coin : coins) {
                if (coin <= w) dp[w] = Math.min(dp[w], 1 + dp[w - coin]);
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    /**
     * LeetCode #518 — Coin Change II (count combinations, unbounded)
     * State:      dp[w] = number of ways to make amount w
     * Recurrence: dp[w] += dp[w - coin]
     * OUTER loop = coins, INNER loop = amounts (prevents duplicate combos)
     */
    static int coinChangeII(int[] coins, int amount) {
        int[] dp = new int[amount+1];
        dp[0] = 1;
        for (int coin : coins) {                    // outer = items
            for (int w = coin; w <= amount; w++) {  // inner = capacity (L→R unbounded)
                dp[w] += dp[w - coin];
            }
        }
        return dp[amount];
    }

    /**
     * LeetCode #279 — Perfect Squares
     * Min number of perfect squares summing to n.
     * = Unbounded knapsack: items = {1,4,9,16,...}, each usable unlimited.
     */
    static int numSquares(int n) {
        int[] dp = new int[n+1];
        Arrays.fill(dp, n+1);
        dp[0] = 0;
        for (int w = 1; w <= n; w++) {
            for (int s = 1; s*s <= w; s++) {
                dp[w] = Math.min(dp[w], 1 + dp[w - s*s]);
            }
        }
        return dp[n];
    }

    // =========================================================
    // TOPIC 80 — LONGEST COMMON SUBSEQUENCE (LCS)
    // =========================================================

    /**
     * LCS — longest subsequence present in both strings.
     * (subsequence: not necessarily contiguous, but order preserved)
     *
     * State:      dp[i][j] = LCS length of s1[0..i-1] and s2[0..j-1]
     * Recurrence: if s1[i-1] == s2[j-1]: dp[i][j] = dp[i-1][j-1] + 1
     *             else:                   dp[i][j] = max(dp[i-1][j], dp[i][j-1])
     * Base:       dp[i][0] = dp[0][j] = 0
     *
     * Time: O(m*n)   Space: O(m*n) → O(n) with rolling array
     *
     * LeetCode #1143
     */
    static int lcs(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) dp[i][j] = dp[i-1][j-1] + 1;
                else                                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
        return dp[m][n];
    }

    /** Reconstruct the actual LCS string */
    static String lcsString(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) dp[i][j] = dp[i-1][j-1] + 1;
                else                                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        // Trace back
        StringBuilder sb = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (s1.charAt(i-1) == s2.charAt(j-1)) { sb.append(s1.charAt(i-1)); i--; j--; }
            else if (dp[i-1][j] > dp[i][j-1])     i--;
            else                                    j--;
        }
        return sb.reverse().toString();
    }

    /**
     * LeetCode #1143 — LCS (direct)
     * LeetCode #583 — Delete Operations for Two Strings
     *   Min deletions = m + n - 2*LCS
     * LeetCode #712 — Min ASCII Delete Sum
     *   dp[i][j] = min ASCII cost to make s1[0..i-1] == s2[0..j-1]
     */
    static int minimumDeleteSum(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 1; i <= m; i++) dp[i][0] = dp[i-1][0] + s1.charAt(i-1);
        for (int j = 1; j <= n; j++) dp[0][j] = dp[0][j-1] + s2.charAt(j-1);
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i-1) == s2.charAt(j-1)) dp[i][j] = dp[i-1][j-1];
                else dp[i][j] = Math.min(dp[i-1][j] + s1.charAt(i-1),
                                         dp[i][j-1] + s2.charAt(j-1));
            }
        return dp[m][n];
    }

    // =========================================================
    // TOPIC 81 — LONGEST INCREASING SUBSEQUENCE (LIS)
    // =========================================================

    /**
     * LIS — longest strictly increasing subsequence.
     *
     * Approach A — DP O(n²):
     * State:      dp[i] = LIS length ending at index i
     * Recurrence: dp[i] = max(dp[j] + 1) for all j < i where nums[j] < nums[i]
     * Base:       dp[i] = 1 (just nums[i] itself)
     *
     * LeetCode #300
     */
    static int lisDP(int[] nums) {
        int n = nums.length, maxLen = 1;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j]+1);
            }
            maxLen = Math.max(maxLen, dp[i]);
        }
        return maxLen;
    }

    /**
     * Approach B — Binary Search O(n log n):
     * Maintain a "tails" array: tails[i] = smallest tail element of all
     * increasing subsequences of length i+1.
     *
     * For each num:
     *   if num > tails.last → extend longest subsequence
     *   else → binary search: replace first tail >= num (patience sorting)
     *
     * "tails" is always sorted → binary search valid.
     * tails does NOT give you the actual LIS — only its length.
     */
    static int lisBinarySearch(int[] nums) {
        List<Integer> tails = new ArrayList<>();
        for (int num : nums) {
            int pos = Collections.binarySearch(tails, num);
            if (pos < 0) pos = -(pos + 1); // insertion point
            if (pos == tails.size()) tails.add(num);
            else                     tails.set(pos, num);
        }
        return tails.size();
    }

    /**
     * LeetCode #354 — Russian Doll Envelopes
     * Sort by width ASC, then height DESC (for same width).
     * LIS on heights only.
     * Reason: sorting height DESC for same width prevents using 2 envelopes of same width.
     */
    static int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a,b) -> a[0] != b[0] ? a[0]-b[0] : b[1]-a[1]);
        int[] heights = Arrays.stream(envelopes).mapToInt(e -> e[1]).toArray();
        return lisBinarySearch(heights);
    }

    // =========================================================
    // TOPIC 82 — EDIT DISTANCE
    // =========================================================

    /**
     * Edit Distance (Levenshtein) — LeetCode #72
     * Min operations (insert, delete, replace) to convert word1 → word2.
     *
     * State:      dp[i][j] = edit distance between word1[0..i-1] and word2[0..j-1]
     * Recurrence:
     *   if word1[i-1] == word2[j-1]: dp[i][j] = dp[i-1][j-1]  (no op needed)
     *   else: dp[i][j] = 1 + min(
     *             dp[i-1][j],    // delete from word1
     *             dp[i][j-1],    // insert into word1
     *             dp[i-1][j-1]   // replace in word1
     *         )
     * Base: dp[i][0] = i (delete all of word1)
     *       dp[0][j] = j (insert all of word2)
     *
     * Time: O(m*n)   Space: O(m*n) → O(n) with rolling
     */
    static int editDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m+1][n+1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i-1) == word2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],          // replace
                                   Math.min(dp[i-1][j],              // delete
                                            dp[i][j-1]));            // insert
                }
            }
        }
        return dp[m][n];
    }

    /** Space-optimized Edit Distance — O(n) space */
    static int editDistanceOpt(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] dp = new int[n+1];
        for (int j = 0; j <= n; j++) dp[j] = j;

        for (int i = 1; i <= m; i++) {
            int prev = dp[0]; // dp[i-1][j-1]
            dp[0] = i;
            for (int j = 1; j <= n; j++) {
                int tmp = dp[j];
                if (word1.charAt(i-1) == word2.charAt(j-1)) dp[j] = prev;
                else dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j-1]));
                prev = tmp;
            }
        }
        return dp[n];
    }

    /**
     * LeetCode #115 — Distinct Subsequences
     * Count ways s can produce t as a subsequence.
     * dp[i][j] = ways to form t[0..j-1] from s[0..i-1]
     * if s[i-1]==t[j-1]: dp[i][j] = dp[i-1][j-1] + dp[i-1][j]
     *                                (use s[i-1])   (skip s[i-1])
     * else:               dp[i][j] = dp[i-1][j]
     */
    static int numDistinct(String s, String t) {
        int m = s.length(), n = t.length();
        long[][] dp = new long[m+1][n+1];
        for (int i = 0; i <= m; i++) dp[i][0] = 1;
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i-1][j];
                if (s.charAt(i-1) == t.charAt(j-1)) dp[i][j] += dp[i-1][j-1];
            }
        return (int) dp[m][n];
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 73: Memoization ===");
        long[] memo = new long[51]; Arrays.fill(memo, -1);
        System.out.println("fib(50) memo:  " + fibMemo(50, memo));
        System.out.println("#120 triangle: " + minimumTotal(Arrays.asList(
            Arrays.asList(2), Arrays.asList(3,4),
            Arrays.asList(6,5,7), Arrays.asList(4,1,8,3)))); // 11

        System.out.println("\n=== TOPIC 74: Tabulation ===");
        System.out.println("fib(50) tab:   " + fibTab(50));

        System.out.println("\n=== TOPIC 76: Space Optimization ===");
        System.out.println("fib(50) O(1):  " + fibO1(50));

        System.out.println("\n=== TOPIC 77: 1D DP ===");
        System.out.println("#70  climbStairs(10): " + climbStairs(10));       // 89
        System.out.println("#198 rob [2,7,9,3,1]: " + rob(new int[]{2,7,9,3,1})); // 12
        System.out.println("#213 robCircular [2,3,2]: " + robCircular(new int[]{2,3,2})); // 3
        System.out.println("#53  maxSubArray [-2,1,-3,4,-1,2,1,-5,4]: "
                + maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4})); // 6
        System.out.println("#152 maxProduct [2,3,-2,4]: "
                + maxProduct(new int[]{2,3,-2,4}));                // 6
        System.out.println("#139 wordBreak 'leetcode': "
                + wordBreak("leetcode", Arrays.asList("leet","code"))); // true

        System.out.println("\n=== TOPIC 78: 0/1 Knapsack ===");
        int[] wt = {2,3,4,5}, val = {3,4,5,6};
        System.out.println("Knapsack W=8: " + knapsack01(wt, val, 8));      // 10
        System.out.println("Knapsack opt: " + knapsack01Opt(wt, val, 8));   // 10
        System.out.println("#416 canPartition [1,5,11,5]: "
                + canPartition(new int[]{1,5,11,5}));                       // true
        System.out.println("#494 targetSum [1,1,1,1,1] t=3: "
                + findTargetSumWays(new int[]{1,1,1,1,1}, 3));              // 5

        System.out.println("\n=== TOPIC 79: Unbounded Knapsack ===");
        System.out.println("Unbounded W=8: " + knapsackUnbounded(wt, val, 8)); // 12
        System.out.println("#322 coinChange [1,5,11] 15: "
                + coinChange(new int[]{1,5,11}, 15));                       // 3 (5+5+5)
        System.out.println("#518 coinChangeII [1,2,5] 5: "
                + coinChangeII(new int[]{1,2,5}, 5));                       // 4
        System.out.println("#279 numSquares(12): " + numSquares(12));       // 3 (4+4+4)

        System.out.println("\n=== TOPIC 80: LCS ===");
        System.out.println("#1143 LCS 'abcde','ace': " + lcs("abcde","ace")); // 3
        System.out.println("LCS string:             " + lcsString("abcde","ace")); // ace
        System.out.println("#712  minDeleteSum 'sea','eat': "
                + minimumDeleteSum("sea","eat")); // 231

        System.out.println("\n=== TOPIC 81: LIS ===");
        int[] nums = {10,9,2,5,3,7,101,18};
        System.out.println("#300 LIS O(n²):   " + lisDP(nums));          // 4
        System.out.println("#300 LIS O(nlogn):" + lisBinarySearch(nums)); // 4
        int[][] env = {{5,4},{6,4},{6,7},{2,3}};
        System.out.println("#354 maxEnvelopes: " + maxEnvelopes(env));   // 3

        System.out.println("\n=== TOPIC 82: Edit Distance ===");
        System.out.println("#72  editDist 'horse','ros': "
                + editDistance("horse","ros"));       // 3
        System.out.println("#72  editDist opt:          "
                + editDistanceOpt("horse","ros"));    // 3
        System.out.println("#115 numDistinct 'rabbbit','rabbit': "
                + numDistinct("rabbbit","rabbit"));   // 3
    }
}
