package phase6_advanced.strings;

import java.util.*;

/**
 * PHASE 6.2 -- STRING ALGORITHMS
 *
 * Topics covered:
 *  #95  KMP (Knuth-Morris-Pratt)  -- Pattern search O(n+m), failure function
 *  #96  Rabin-Karp                -- Rolling hash, multi-pattern search
 *  #97  Z-Algorithm               -- Z-array, linear pattern matching
 *  #98  Suffix Array (concept)    -- SA + LCP array, powerful string queries
 */
public class StringAlgorithms {

    public static void main(String[] args) {
        System.out.println("=== PHASE 6.2: STRING ALGORITHMS ===\n");
        demoKMP();
        demoRabinKarp();
        demoZAlgorithm();
        demoSuffixArray();
    }

    // =========================================================================
    // TOPIC #95: KMP -- KNUTH-MORRIS-PRATT
    // =========================================================================
    // PROBLEM: Find all occurrences of pattern P in text T.
    //
    // NAIVE: O(n*m) -- for each position in T, compare P character by character.
    // KMP:   O(n+m) -- never go back in T, use info from P itself.
    //
    // KEY: The FAILURE FUNCTION (LPS = Longest Proper Prefix which is also Suffix)
    //   lps[i] = length of longest proper prefix of P[0..i] that is also a suffix
    //
    //   P = "AAACAAAA"
    //       i: 0 1 2 3 4 5 6 7
    //     lps: 0 1 2 0 1 2 3 3
    //
    //   When mismatch at P[j], don't restart from j=0.
    //   Instead set j = lps[j-1] (reuse the matched prefix).
    //
    // LeetCode:
    //   #28  Find the Index of the First Occurrence in a String
    //   #459 Repeated Substring Pattern
    //   #214 Shortest Palindrome

    // Build the LPS (failure) array -- O(m)
    static int[] buildLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        lps[0] = 0;
        int len = 0; // length of previous longest prefix suffix
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1]; // fall back (don't increment i)
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    // KMP search: returns list of all start indices where pattern occurs in text
    static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) return result;

        int[] lps = buildLPS(pattern);
        int i = 0; // index in text
        int j = 0; // index in pattern
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
            }
            if (j == m) { // full match found
                result.add(i - j);
                j = lps[j - 1]; // look for next match
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j > 0) j = lps[j - 1]; // mismatch after j > 0
                else        i++;             // mismatch at j == 0
            }
        }
        return result;
    }

    // LeetCode #28 -- strStr (first occurrence)
    static int strStr(String haystack, String needle) {
        List<Integer> matches = kmpSearch(haystack, needle);
        return matches.isEmpty() ? -1 : matches.get(0);
    }

    // LeetCode #459 -- Repeated Substring Pattern
    // "abcabc" => true (abc repeated 2x)
    // KEY TRICK: if s is formed by repeating a substring,
    //   then lps[m-1] > 0 AND m % (m - lps[m-1]) == 0
    static boolean repeatedSubstringPattern(String s) {
        int m = s.length();
        int[] lps = buildLPS(s);
        int longest = lps[m - 1];
        int period = m - longest;
        return longest > 0 && m % period == 0;
    }

    // LeetCode #214 -- Shortest Palindrome
    // Find shortest palindrome by adding chars in FRONT of s.
    // KEY: Find longest palindromic prefix of s.
    //   Build string: s + "#" + reverse(s), compute LPS.
    //   lps[last] = length of longest palindromic prefix.
    static String shortestPalindrome(String s) {
        String rev = new StringBuilder(s).reverse().toString();
        String combined = s + "#" + rev;
        int[] lps = buildLPS(combined);
        int palinLen = lps[combined.length() - 1];
        // Prepend the non-palindromic suffix (reversed)
        return rev.substring(0, s.length() - palinLen) + s;
    }

    static void demoKMP() {
        System.out.println("--- TOPIC #95: KMP ---");
        String text    = "AABAACAADAABAABA";
        String pattern = "AABA";
        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);
        System.out.println("LPS:     " + Arrays.toString(buildLPS(pattern)));
        System.out.println("Matches at indices: " + kmpSearch(text, pattern));

        System.out.println("\nLeetCode #28 strStr:");
        System.out.println("  strStr(\"hello\", \"ll\") = " + strStr("hello", "ll")); // 2
        System.out.println("  strStr(\"aaaaa\", \"bba\") = " + strStr("aaaaa", "bba")); // -1

        System.out.println("\nLeetCode #459 repeatedSubstringPattern:");
        System.out.println("  \"abcabc\" => " + repeatedSubstringPattern("abcabc")); // true
        System.out.println("  \"aba\"    => " + repeatedSubstringPattern("aba"));    // false
        System.out.println("  \"abab\"   => " + repeatedSubstringPattern("abab"));   // true

        System.out.println("\nLeetCode #214 shortestPalindrome:");
        System.out.println("  \"aacecaaa\" => " + shortestPalindrome("aacecaaa")); // "aaacecaaa"
        System.out.println("  \"abcd\"     => " + shortestPalindrome("abcd"));     // "dcbabcd"
        System.out.println();
    }

    // =========================================================================
    // TOPIC #96: RABIN-KARP
    // =========================================================================
    // IDEA: Use a ROLLING HASH to compare pattern with each window of text.
    //   - Hash(window) == Hash(pattern) => check character by character (rare)
    //   - Rolling: remove leftmost char, add rightmost char in O(1)
    //
    // HASH formula (polynomial rolling hash):
    //   H = (c0*BASE^(m-1) + c1*BASE^(m-2) + ... + c(m-1)) % MOD
    //
    // Rolling: H_new = (H_old - c_left * BASE^(m-1)) * BASE + c_right) % MOD
    //
    // Worst case: O(n*m) if many hash collisions
    // Average:    O(n+m)
    //
    // ADVANTAGE over KMP: easy to extend to MULTIPLE PATTERN search
    //   (store patterns as a HashSet of hashes -- match all at once)
    //
    // LeetCode:
    //   #28  strStr
    //   #187 Repeated DNA Sequences
    //   #1044 Longest Duplicate Substring (binary search + rolling hash)

    static final long BASE = 31;
    static final long MOD  = 1_000_000_007L;

    // Compute hash of s[l..r]
    static long computeHash(String s, int l, int r, long[] pow) {
        // We'll use precomputed prefix hash array
        // This demo computes inline for clarity
        long h = 0;
        for (int i = l; i <= r; i++)
            h = (h * BASE + (s.charAt(i) - 'a' + 1)) % MOD;
        return h;
    }

    // Rabin-Karp using prefix hashes for O(1) window hash
    static class RollingHash {
        private final long[] prefHash, pow;
        private final int n;

        RollingHash(String s) {
            n = s.length();
            prefHash = new long[n + 1];
            pow = new long[n + 1];
            pow[0] = 1;
            for (int i = 0; i < n; i++) {
                prefHash[i + 1] = (prefHash[i] * BASE + (s.charAt(i) - 'a' + 1)) % MOD;
                pow[i + 1] = pow[i] * BASE % MOD;
            }
        }

        // Hash of s[l..r] (0-indexed, inclusive)
        long getHash(int l, int r) {
            return (prefHash[r + 1] - prefHash[l] * pow[r - l + 1] % MOD + MOD * MOD) % MOD;
        }
    }

    // Rabin-Karp search -- returns all match positions
    static List<Integer> rabinKarpSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m > n) return result;

        RollingHash th = new RollingHash(text);
        RollingHash ph = new RollingHash(pattern);
        long patHash = ph.getHash(0, m - 1);

        for (int i = 0; i <= n - m; i++) {
            if (th.getHash(i, i + m - 1) == patHash) {
                // Hash match -- verify (avoid false positives)
                if (text.substring(i, i + m).equals(pattern))
                    result.add(i);
            }
        }
        return result;
    }

    // LeetCode #187 -- Repeated DNA Sequences
    // Find all 10-letter-long substrings that appear more than once
    static List<String> findRepeatedDnaSequences(String s) {
        if (s.length() <= 10) return new ArrayList<>();
        Map<Long, Integer> seen = new HashMap<>();
        List<String> result = new ArrayList<>();
        RollingHash rh = new RollingHash(s);
        for (int i = 0; i <= s.length() - 10; i++) {
            long h = rh.getHash(i, i + 9);
            seen.merge(h, 1, Integer::sum);
            if (seen.get(h) == 2) result.add(s.substring(i, i + 10));
        }
        return result;
    }

    // LeetCode #1044 -- Longest Duplicate Substring (Binary Search + Rolling Hash)
    static String longestDupSubstring(String s) {
        int lo = 1, hi = s.length() - 1;
        String result = "";
        RollingHash rh = new RollingHash(s);
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            String dup = findDuplicate(s, rh, mid);
            if (dup != null) { result = dup; lo = mid + 1; }
            else             { hi = mid - 1; }
        }
        return result;
    }

    static String findDuplicate(String s, RollingHash rh, int len) {
        Map<Long, Integer> seen = new HashMap<>();
        for (int i = 0; i <= s.length() - len; i++) {
            long h = rh.getHash(i, i + len - 1);
            if (seen.containsKey(h)) {
                // Verify (handle collision)
                int prev = seen.get(h);
                if (s.substring(prev, prev + len).equals(s.substring(i, i + len)))
                    return s.substring(i, i + len);
            }
            seen.putIfAbsent(h, i);
        }
        return null;
    }

    static void demoRabinKarp() {
        System.out.println("--- TOPIC #96: Rabin-Karp ---");
        String text    = "abcababcabc";
        String pattern = "abc";
        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);
        System.out.println("Matches: " + rabinKarpSearch(text, pattern));

        System.out.println("\nLeetCode #187 Repeated DNA Sequences:");
        System.out.println("  \"AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT\" => "
            + findRepeatedDnaSequences("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT"));

        System.out.println("\nLeetCode #1044 Longest Duplicate Substring:");
        System.out.println("  \"banana\" => \"" + longestDupSubstring("banana") + "\""); // "ana"
        System.out.println("  \"abcd\"   => \"" + longestDupSubstring("abcd") + "\"");   // ""
        System.out.println();
    }

    // =========================================================================
    // TOPIC #97: Z-ALGORITHM
    // =========================================================================
    // The Z-array: Z[i] = length of the longest substring starting from s[i]
    //              that is also a PREFIX of s.
    //   Z[0] is undefined (or set to n by convention, we leave it 0 here).
    //
    //   s = "aabxaa"
    //       i: 0 1 2 3 4 5
    //       Z: 0 1 0 0 2 1
    //
    //   Maintain a window [l, r] = rightmost Z-box found so far.
    //   For i inside [l, r]: Z[i] >= min(Z[i-l], r-i+1) => avoid recomputing.
    //
    // HOW TO USE FOR PATTERN SEARCH:
    //   Build s = P + "$" + T  ($ is a separator not in alphabet)
    //   Compute Z-array.
    //   Wherever Z[i] == m, position i-(m+1) in T is a match.
    //
    // Time: O(n+m)  Space: O(n+m)
    //
    // LeetCode:
    //   #28  strStr
    //   #2223 Sum of Scores of Built Strings (Z-array directly)

    static int[] zFunction(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0; // current Z-box [l, r]
        for (int i = 1; i < n; i++) {
            if (i < r)
                z[i] = Math.min(r - i, z[i - l]);
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i]))
                z[i]++;
            if (i + z[i] > r) { l = i; r = i + z[i]; }
        }
        return z;
    }

    // Z-algorithm pattern search
    static List<Integer> zSearch(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        String combined = pattern + "$" + text;
        int[] z = zFunction(combined);
        int m = pattern.length();
        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m)
                result.add(i - m - 1);
        }
        return result;
    }

    // LeetCode #2223 -- Sum of Scores of Built Strings
    // Score of i = length of longest common prefix of s and s[i..]
    // = Z[i] for i>0, and n for i=0
    static long sumScores(String s) {
        int n = s.length();
        int[] z = zFunction(s);
        long sum = n; // z[0] = n by convention
        for (int i = 1; i < n; i++) sum += z[i];
        return sum;
    }

    // Count occurrences of each rotation of a string in the string itself
    // Classic Z trick: s + s, then Z-array
    static int[] countRotationOccurrences(String s) {
        int n = s.length();
        int[] z = zFunction(s + s);
        int[] result = new int[n];
        for (int i = 0; i < n; i++)
            result[i] = Math.min(z[i], n); // each rotation match
        return result;
    }

    static void demoZAlgorithm() {
        System.out.println("--- TOPIC #97: Z-Algorithm ---");
        String s = "aabxaabxcaabxaabxay";
        String p = "aabx";
        System.out.println("Text:    " + s);
        System.out.println("Pattern: " + p);
        System.out.println("Z-array of pattern+$+text: " + Arrays.toString(zFunction(p + "$" + s)));
        System.out.println("Matches: " + zSearch(s, p));

        System.out.println("\nZ-array of \"aabxaa\":");
        System.out.println("  " + Arrays.toString(zFunction("aabxaa")));
        // [0, 1, 0, 0, 2, 1]

        System.out.println("\nZ-array of \"aaaa\":");
        System.out.println("  " + Arrays.toString(zFunction("aaaa")));
        // [0, 3, 2, 1]

        System.out.println("\nLeetCode #2223 sumScores:");
        System.out.println("  \"babab\" => " + sumScores("babab")); // 9
        System.out.println("  \"azbazbaz\" => " + sumScores("azbazbaz")); // 14
        System.out.println();
    }

    // =========================================================================
    // TOPIC #98: SUFFIX ARRAY (Concept + O(n log^2 n) build)
    // =========================================================================
    // SUFFIX ARRAY (SA): array of indices of ALL suffixes of s, sorted lexicographically.
    //
    //   s = "banana"
    //   Suffixes:
    //     0: "banana"
    //     1: "anana"
    //     2: "nana"
    //     3: "ana"
    //     4: "na"
    //     5: "a"
    //
    //   SA = [5, 3, 1, 0, 4, 2]  (indices sorted by suffix)
    //       "a" < "ana" < "anana" < "banana" < "na" < "nana"
    //
    // LCP ARRAY: lcp[i] = length of Longest Common Prefix of SA[i] and SA[i-1]
    //   LCP = [0, 1, 3, 0, 0, 2]
    //
    // USES of Suffix Array:
    //   1. Pattern search: binary search on SA => O(m log n)
    //   2. Longest repeated substring: max(LCP)
    //   3. Longest common substring of 2 strings
    //   4. Number of distinct substrings: n*(n+1)/2 - sum(LCP)
    //
    // BUILD: naive O(n^2 log n), O(n log^2 n) with doubling, O(n) with SA-IS
    //
    // LeetCode:
    //   #1044 Longest Duplicate Substring (can solve with SA + LCP)
    //   #718  Maximum Length of Repeated Subarray
    //   Hard: #1062 Longest Repeating Substring

    // Build Suffix Array -- O(n log^2 n) using suffix doubling
    static int[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank = new int[n];
        int[] tmp  = new int[n];

        // Initial ranking by first character
        for (int i = 0; i < n; i++) { sa[i] = i; rank[i] = s.charAt(i); }

        for (int gap = 1; gap < n; gap <<= 1) {
            final int g = gap;
            final int[] r = rank.clone();

            // Sort by (rank[i], rank[i+gap])
            Comparator<Integer> cmp = (a, b) -> {
                if (r[a] != r[b]) return r[a] - r[b];
                int ra = a + g < n ? r[a + g] : -1;
                int rb = b + g < n ? r[b + g] : -1;
                return ra - rb;
            };
            Arrays.sort(sa, cmp);

            // Recompute ranks
            tmp[sa[0]] = 0;
            for (int i = 1; i < n; i++)
                tmp[sa[i]] = tmp[sa[i-1]] + (cmp.compare(sa[i], sa[i-1]) != 0 ? 1 : 0);
            rank = tmp.clone();
            if (rank[sa[n-1]] == n-1) break; // all ranks unique, done
        }
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }

    // Build LCP Array using Kasai's algorithm -- O(n)
    // lcp[i] = LCP of suffix SA[i] and SA[i-1]
    static int[] buildLCP(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n]; // inverse of SA
        for (int i = 0; i < n; i++) rank[sa[i]] = i;

        int[] lcp = new int[n];
        int h = 0; // current LCP length
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1]; // previous suffix in sorted order
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h))
                    h++;
                lcp[rank[i]] = h;
                if (h > 0) h--; // LCP can decrease by at most 1
            }
        }
        return lcp;
    }

    // Pattern search using Suffix Array -- O(m log n)
    static int saSearch(String text, String pattern, int[] sa) {
        int lo = 0, hi = sa.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int start = sa[mid];
            String suffix = text.substring(start, Math.min(start + pattern.length(), text.length()));
            int cmp = suffix.compareTo(pattern);
            if (cmp == 0) return sa[mid]; // found
            if (cmp < 0)  lo = mid + 1;
            else           hi = mid - 1;
        }
        return -1;
    }

    // Longest repeated substring = max value in LCP array
    static String longestRepeatedSubstring(String s) {
        int[] sa  = buildSuffixArray(s);
        int[] lcp = buildLCP(s, sa);
        int maxLen = 0, idx = 0;
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLen) { maxLen = lcp[i]; idx = sa[i]; }
        }
        return maxLen == 0 ? "" : s.substring(idx, idx + maxLen);
    }

    // Count distinct substrings = total substrings - sum(LCP)
    static long countDistinctSubstrings(String s) {
        int n = s.length();
        int[] sa  = buildSuffixArray(s);
        int[] lcp = buildLCP(s, sa);
        long total = (long) n * (n + 1) / 2;
        long lcpSum = 0;
        for (int v : lcp) lcpSum += v;
        return total - lcpSum;
    }

    static void demoSuffixArray() {
        System.out.println("--- TOPIC #98: Suffix Array ---");
        String s = "banana";
        int[] sa  = buildSuffixArray(s);
        int[] lcp = buildLCP(s, sa);

        System.out.println("String: \"" + s + "\"");
        System.out.println("Suffix Array (SA): " + Arrays.toString(sa));
        System.out.println("Suffixes in sorted order:");
        for (int i = 0; i < sa.length; i++)
            System.out.printf("  SA[%d]=%d  lcp=%d  \"%s\"%n", i, sa[i], lcp[i], s.substring(sa[i]));

        System.out.println("\nPattern search:");
        System.out.println("  Search \"ana\" in \"banana\" => index " + saSearch(s, "ana", sa)); // 1 or 3
        System.out.println("  Search \"nan\" in \"banana\" => index " + saSearch(s, "nan", sa)); // 2

        System.out.println("\nLongest repeated substring of \"banana\": \""
            + longestRepeatedSubstring("banana") + "\""); // "ana"
        System.out.println("Longest repeated substring of \"abcbc\": \""
            + longestRepeatedSubstring("abcbc") + "\""); // "bc"

        System.out.println("\nDistinct substrings:");
        System.out.println("  \"banana\" => " + countDistinctSubstrings("banana")); // 15
        System.out.println("  \"aaaa\"   => " + countDistinctSubstrings("aaaa"));   // 4

        System.out.println();
        System.out.println("SUMMARY -- String Algorithm Cheat Sheet:");
        System.out.println("  KMP           : single pattern search  O(n+m)         -- failure function");
        System.out.println("  Rabin-Karp    : multi-pattern search   O(n+m) avg     -- rolling hash");
        System.out.println("  Z-Algorithm   : prefix matching        O(n+m)         -- Z-box");
        System.out.println("  Suffix Array  : all substrings queries O(n log^2 n)   -- SA + LCP");
    }
}
