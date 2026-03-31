package leetcode_practice;

import java.util.*;

/**
 * LEETCODE PRACTICE -- SESSION 04
 * Pattern: DYNAMIC PROGRAMMING
 *
 * DP Mental Model (4 steps):
 *   1. DEFINE state: dp[i] means "..."
 *   2. RECURRENCE: dp[i] = f(dp[i-1], dp[i-2], ...)
 *   3. BASE CASE: dp[0] = ?, dp[1] = ?
 *   4. ORDER: fill left-to-right, or right-to-left?
 *
 * Optimization checklist:
 *   - 1D DP rolling array: if dp[i] only depends on dp[i-1], use two variables
 *   - 2D DP row compress: if dp[i][j] depends on dp[i-1][...], use 1D array
 *
 * Problems:
 *   Easy:   #70  Climbing Stairs, #198 House Robber
 *   Medium: #300 Longest Increasing Subsequence
 *           #322 Coin Change
 *           #416 Partition Equal Subset Sum
 *           #1143 Longest Common Subsequence
 *           #647 Palindromic Substrings
 *   Hard:   #72  Edit Distance
 *           #312 Burst Balloons
 *           #10  Regular Expression Matching
 *           #32  Longest Valid Parentheses
 */
public class Session04_DynamicProgramming {

    public static void main(String[] args) {
        System.out.println("=== SESSION 04: DYNAMIC PROGRAMMING ===\n");
        testEasy();
        testMedium();
        testHard();
    }

    // -------------------------------------------------------------------------
    // EASY
    // -------------------------------------------------------------------------

    // #70 Climbing Stairs
    // dp[i] = ways to reach step i = dp[i-1] + dp[i-2]  (Fibonacci pattern)
    // Space O(n) -> O(1) with two variables
    static int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int cur = prev1 + prev2;
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    // #198 House Robber
    // dp[i] = max money robbing houses 0..i
    // dp[i] = max(dp[i-1],  dp[i-2] + nums[i])
    //            skip i      rob i
    // Space O(n) -> O(1)
    static int rob(int[] nums) {
        int prev2 = 0, prev1 = 0;
        for (int num : nums) {
            int cur = Math.max(prev1, prev2 + num);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    static void testEasy() {
        System.out.println("--- EASY ---");
        System.out.println("#70  climbStairs(2) = " + climbStairs(2)); // 2
        System.out.println("#70  climbStairs(3) = " + climbStairs(3)); // 3
        System.out.println("#70  climbStairs(5) = " + climbStairs(5)); // 8

        System.out.println("#198 rob([1,2,3,1])     = " + rob(new int[]{1,2,3,1}));     // 4
        System.out.println("#198 rob([2,7,9,3,1])   = " + rob(new int[]{2,7,9,3,1}));   // 12
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // MEDIUM
    // -------------------------------------------------------------------------

    // #300 Longest Increasing Subsequence (LIS)
    // dp[i] = LIS ending at index i
    // dp[i] = 1 + max(dp[j]) for all j < i where nums[j] < nums[i]
    // O(n^2) DP -- then O(n log n) with patience sort / binary search
    static int lengthOfLIS(int[] nums) {
        int n = nums.length, max = 1;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++)
                if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
            max = Math.max(max, dp[i]);
        }
        return max;
    }

    // O(n log n) version: maintain 'tails' array (patience sorting)
    // tails[k] = smallest tail element of all increasing subsequences of length k+1
    // For each num: binary search in tails for first element >= num, replace it.
    static int lengthOfLISFast(int[] nums) {
        List<Integer> tails = new ArrayList<>();
        for (int num : nums) {
            int lo = 0, hi = tails.size();
            while (lo < hi) {               // find first tail >= num (lower bound)
                int mid = lo + (hi - lo) / 2;
                if (tails.get(mid) < num) lo = mid + 1;
                else                      hi = mid;
            }
            if (lo == tails.size()) tails.add(num); // extend LIS
            else                    tails.set(lo, num); // replace to keep tails small
        }
        return tails.size();
    }

    // #322 Coin Change
    // dp[i] = min coins to make amount i
    // dp[i] = 1 + min(dp[i - coin]) for each coin <= i
    // Base: dp[0] = 0, dp[i] = INF initially
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // sentinel "infinity"
        dp[0] = 0;
        for (int i = 1; i <= amount; i++)
            for (int coin : coins)
                if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // #416 Partition Equal Subset Sum
    // Can we split array into two subsets with equal sum?
    // If total sum is odd => impossible.
    // Reduce to: can we find a subset summing to total/2?
    // dp[j] = true if subset sum j is achievable
    // KEY: iterate j BACKWARDS to avoid using same element twice (0/1 knapsack)
    static boolean canPartition(int[] nums) {
        int total = Arrays.stream(nums).sum();
        if (total % 2 != 0) return false;
        int target = total / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : nums)
            for (int j = target; j >= num; j--) // backwards!
                dp[j] = dp[j] || dp[j - num];
        return dp[target];
    }

    // #1143 Longest Common Subsequence (LCS)
    // dp[i][j] = LCS of text1[0..i-1] and text2[0..j-1]
    // if text1[i-1] == text2[j-1]: dp[i][j] = dp[i-1][j-1] + 1
    // else:                        dp[i][j] = max(dp[i-1][j], dp[i][j-1])
    // Space: O(m*n) -> O(n) with rolling 1D array
    static int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[] dp = new int[n + 1]; // rolling 1D
        for (int i = 1; i <= m; i++) {
            int prev = 0; // dp[i-1][j-1]
            for (int j = 1; j <= n; j++) {
                int temp = dp[j]; // save before overwrite (will be dp[i-1][j] for next j)
                if (text1.charAt(i - 1) == text2.charAt(j - 1))
                    dp[j] = prev + 1;
                else
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                prev = temp;
            }
        }
        return dp[n];
    }

    // #647 Palindromic Substrings
    // Count all palindromic substrings.
    // Approach: expand from each center (O(n^2) time, O(1) space)
    // Two centers per char: odd-length (center = char) and even-length (between chars)
    static int countSubstrings(String s) {
        int count = 0;
        for (int center = 0; center < 2 * s.length() - 1; center++) {
            int lo = center / 2;
            int hi = lo + center % 2; // odd center: lo==hi; even: lo+1==hi
            while (lo >= 0 && hi < s.length() && s.charAt(lo) == s.charAt(hi)) {
                count++;
                lo--; hi++;
            }
        }
        return count;
    }

    static void testMedium() {
        System.out.println("--- MEDIUM ---");

        System.out.println("#300 lengthOfLIS([10,9,2,5,3,7,101,18]) = "
            + lengthOfLIS(new int[]{10,9,2,5,3,7,101,18}));    // 4
        System.out.println("#300 O(nlogn) fast version             = "
            + lengthOfLISFast(new int[]{10,9,2,5,3,7,101,18})); // 4
        System.out.println("#300 lengthOfLIS([0,1,0,3,2,3])       = "
            + lengthOfLIS(new int[]{0,1,0,3,2,3}));             // 4

        System.out.println("#322 coinChange([1,5,11],11) = "
            + coinChange(new int[]{1,5,11}, 11));   // 1
        System.out.println("#322 coinChange([1,5,6,9],11)= "
            + coinChange(new int[]{1,5,6,9}, 11));  // 2 (5+6)
        System.out.println("#322 coinChange([2],3)      = "
            + coinChange(new int[]{2}, 3));          // -1

        System.out.println("#416 canPartition([1,5,11,5]) = "
            + canPartition(new int[]{1,5,11,5})); // true (1+5+5=11)
        System.out.println("#416 canPartition([1,2,3,5]) = "
            + canPartition(new int[]{1,2,3,5}));  // false

        System.out.println("#1143 LCS(\"abcde\",\"ace\")      = "
            + longestCommonSubsequence("abcde", "ace"));   // 3
        System.out.println("#1143 LCS(\"abc\",\"abc\")        = "
            + longestCommonSubsequence("abc", "abc"));     // 3
        System.out.println("#1143 LCS(\"abc\",\"def\")        = "
            + longestCommonSubsequence("abc", "def"));     // 0

        System.out.println("#647 countSubstrings(\"abc\")    = "
            + countSubstrings("abc"));   // 3
        System.out.println("#647 countSubstrings(\"aaa\")    = "
            + countSubstrings("aaa"));   // 6
        System.out.println();
    }

    // -------------------------------------------------------------------------
    // HARD
    // -------------------------------------------------------------------------

    // #72 Edit Distance
    // dp[i][j] = min ops to convert word1[0..i-1] to word2[0..j-1]
    // if chars match:   dp[i][j] = dp[i-1][j-1]
    // else min of:
    //   dp[i-1][j]   + 1  (delete from word1)
    //   dp[i][j-1]   + 1  (insert into word1)
    //   dp[i-1][j-1] + 1  (replace)
    // Space O(m*n) -> O(n) rolling
    static int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[] dp = new int[n + 1];
        for (int j = 0; j <= n; j++) dp[j] = j; // base: convert "" to word2[0..j]
        for (int i = 1; i <= m; i++) {
            int prev = dp[0]; // dp[i-1][j-1]
            dp[0] = i;        // base: convert word1[0..i] to ""
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (word1.charAt(i - 1) == word2.charAt(j - 1))
                    dp[j] = prev;
                else
                    dp[j] = 1 + Math.min(prev, Math.min(dp[j], dp[j - 1]));
                    //               replace       delete    insert
                prev = temp;
            }
        }
        return dp[n];
    }

    // #312 Burst Balloons
    // Interval DP: dp[l][r] = max coins from bursting all balloons in (l, r) exclusive
    // KEY insight: think of k as the LAST balloon burst in [l..r], not the first.
    //   When k is last, its neighbors are nums[l] and nums[r] (already burst = boundaries).
    //   dp[l][r] = max over k in (l,r): nums[l]*nums[k]*nums[r] + dp[l][k] + dp[k][r]
    // Add boundary sentinels 1 on both sides.
    static int maxCoins(int[] nums) {
        int n = nums.length;
        int[] balls = new int[n + 2];
        balls[0] = balls[n + 1] = 1;
        for (int i = 0; i < n; i++) balls[i + 1] = nums[i];
        int N = n + 2;
        int[][] dp = new int[N][N];
        // Fill by increasing interval length
        for (int len = 2; len < N; len++) {
            for (int l = 0; l < N - len; l++) {
                int r = l + len;
                for (int k = l + 1; k < r; k++) {
                    dp[l][r] = Math.max(dp[l][r],
                        balls[l] * balls[k] * balls[r] + dp[l][k] + dp[k][r]);
                }
            }
        }
        return dp[0][N - 1];
    }

    // #10 Regular Expression Matching
    // dp[i][j] = true if s[0..i-1] matches p[0..j-1]
    // Cases:
    //   p[j-1] == s[i-1] or p[j-1] == '.': dp[i][j] = dp[i-1][j-1]
    //   p[j-1] == '*':
    //     zero occurrence: dp[i][j] = dp[i][j-2]  (ignore x*)
    //     one+ occurrence: dp[i][j] = dp[i-1][j]  if p[j-2] matches s[i-1]
    static boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        // Base: empty string can match patterns like a*, a*b*, a*b*c*
        for (int j = 2; j <= n; j++)
            if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1), pc = p.charAt(j - 1);
                if (pc == '*') {
                    dp[i][j] = dp[i][j - 2]; // zero occurrence of x*
                    if (p.charAt(j - 2) == '.' || p.charAt(j - 2) == sc)
                        dp[i][j] = dp[i][j] || dp[i - 1][j]; // one+ occurrence
                } else if (pc == '.' || pc == sc) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }
        return dp[m][n];
    }

    // #32 Longest Valid Parentheses
    // dp[i] = length of longest valid parens ending at index i
    // s[i] == '(': dp[i] = 0 (can't end a valid sequence with open)
    // s[i] == ')':
    //   Case 1: s[i-1] == '(': dp[i] = dp[i-2] + 2
    //   Case 2: s[i-1] == ')' and s[i - dp[i-1] - 1] == '(':
    //           dp[i] = dp[i-1] + 2 + dp[i - dp[i-1] - 2]
    //                                   ^prev valid block before the matched '('
    static int longestValidParentheses(String s) {
        int n = s.length(), max = 0;
        int[] dp = new int[n];
        for (int i = 1; i < n; i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    // ...() case
                    dp[i] = (i >= 2 ? dp[i - 2] : 0) + 2;
                } else {
                    // ...)) case: find the matching '(' for s[i]
                    int matchIdx = i - dp[i - 1] - 1;
                    if (matchIdx >= 0 && s.charAt(matchIdx) == '(') {
                        dp[i] = dp[i - 1] + 2;
                        if (matchIdx > 0) dp[i] += dp[matchIdx - 1]; // glue prev block
                    }
                }
                max = Math.max(max, dp[i]);
            }
        }
        return max;
    }

    static void testHard() {
        System.out.println("--- HARD ---");

        System.out.println("#72  minDistance(\"horse\",\"ros\")    = "
            + minDistance("horse", "ros"));    // 3
        System.out.println("#72  minDistance(\"intention\",\"execution\") = "
            + minDistance("intention", "execution")); // 5
        System.out.println("#72  minDistance(\"\",\"a\")            = "
            + minDistance("", "a"));            // 1

        System.out.println("#312 maxCoins([3,1,5,8]) = "
            + maxCoins(new int[]{3,1,5,8})); // 167
        System.out.println("#312 maxCoins([1,5])     = "
            + maxCoins(new int[]{1,5}));     // 10

        System.out.println("#10  isMatch(\"aa\",\"a\")    = " + isMatch("aa","a"));     // false
        System.out.println("#10  isMatch(\"aa\",\"a*\")   = " + isMatch("aa","a*"));    // true
        System.out.println("#10  isMatch(\"ab\",\".*\")   = " + isMatch("ab",".*"));    // true
        System.out.println("#10  isMatch(\"aab\",\"c*a*b\")= " + isMatch("aab","c*a*b")); // true
        System.out.println("#10  isMatch(\"mississippi\",\"mis*is*p*.\") = "
            + isMatch("mississippi","mis*is*p*.")); // false

        System.out.println("#32  longestValidParentheses(\"(()\")   = "
            + longestValidParentheses("(()")); // 2
        System.out.println("#32  longestValidParentheses(\")()())\") = "
            + longestValidParentheses(")()())")); // 4
        System.out.println("#32  longestValidParentheses(\"\")       = "
            + longestValidParentheses("")); // 0

        System.out.println();
        System.out.println("PATTERN SUMMARY -- Dynamic Programming:");
        System.out.println("  1D rolling       : only need prev 1-2 values => O(1) space");
        System.out.println("  0/1 Knapsack     : iterate j BACKWARDS to avoid reuse");
        System.out.println("  Unbounded knap.  : iterate j FORWARDS (reuse allowed)");
        System.out.println("  LCS/Edit dist.   : 2D dp -> 1D rolling with 'prev' variable");
        System.out.println("  Interval DP      : think of LAST op, fill by length increasing");
        System.out.println("  Regex matching   : '*' has two cases: zero-use and one+-use");
        System.out.println("  Expand center    : O(n^2) palindrome without 2D array");
    }
}
