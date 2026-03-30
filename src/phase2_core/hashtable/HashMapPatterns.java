package phase2_core.hashtable;

import java.util.*;

/**
 * PHASE 2.1 — HashMap / HashSet Patterns
 *
 * 5 pattern phổ biến nhất trong interview:
 *   1. Frequency Count    — đếm tần suất
 *   2. Two Sum / Lookup   — tìm complement
 *   3. Grouping           — nhóm theo key
 *   4. Sliding Window + Map
 *   5. Prefix Sum + Map
 */
public class HashMapPatterns {

    // =========================================================
    // PATTERN 1: Frequency Count
    // =========================================================

    /**
     * Valid Anagram (LeetCode #242)
     * "anagram" / "nagaram" → true
     */
    static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;
        for (char c : t.toCharArray()) if (--freq[c - 'a'] < 0) return false;
        return true;
    }

    /**
     * First Unique Character (LeetCode #387)
     * "leetcode" → 0  ('l' xuất hiện 1 lần, ở vị trí 0)
     */
    static int firstUniqChar(String s) {
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;
        for (int i = 0; i < s.length(); i++)
            if (freq[s.charAt(i) - 'a'] == 1) return i;
        return -1;
    }

    /**
     * Top K Frequent Words (LeetCode #692)
     * words=["i","love","leetcode","i","love","coding"], k=2 → ["i","love"]
     * Sắp xếp: tần suất giảm, nếu bằng nhau → alphabet tăng
     */
    static List<String> topKFrequentWords(String[] words, int k) {
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) freq.merge(w, 1, Integer::sum);

        // Min-heap: pop phần tử kém nhất khi size > k
        PriorityQueue<String> minHeap = new PriorityQueue<>((a, b) ->
                freq.get(a).equals(freq.get(b))
                        ? b.compareTo(a)   // alphabet ngược (để min-heap giữ lại từ tốt hơn)
                        : freq.get(a) - freq.get(b) // tần suất tăng dần (pop min)
        );

        for (String w : freq.keySet()) {
            minHeap.offer(w);
            if (minHeap.size() > k) minHeap.poll();
        }

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) result.add(0, minHeap.poll()); // đảo ngược
        return result;
    }

    // =========================================================
    // PATTERN 2: Two Sum / Complement Lookup
    // =========================================================

    /**
     * Two Sum (LeetCode #1) — bài kinh điển nhất
     * [2,7,11,15], target=9 → [0,1]
     *
     * Map<value, index> — một lần duyệt
     */
    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement))
                return new int[]{seen.get(complement), i};
            seen.put(nums[i], i);
        }
        return new int[]{};
    }

    /**
     * 4Sum II (LeetCode #454)
     * Đếm số bộ (i,j,k,l) sao cho A[i]+B[j]+C[k]+D[l]=0
     * Time: O(n²)  vs  Brute force O(n⁴)
     *
     * Chia thành 2 nhóm: lưu tổng A+B vào map, tìm -(C+D) trong map
     */
    static int fourSumCount(int[] A, int[] B, int[] C, int[] D) {
        Map<Integer, Integer> abSum = new HashMap<>();
        for (int a : A)
            for (int b : B)
                abSum.merge(a + b, 1, Integer::sum);

        int count = 0;
        for (int c : C)
            for (int d : D)
                count += abSum.getOrDefault(-(c + d), 0);
        return count;
    }

    // =========================================================
    // PATTERN 3: Grouping
    // =========================================================

    /**
     * Group Anagrams (LeetCode #49)
     * ["eat","tea","tan","ate","nat","bat"]
     * → [["bat"],["nat","tan"],["ate","eat","tea"]]
     *
     * Key: sort từng string → anagram có cùng key
     */
    static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(groups.values());
    }

    /**
     * Group Anagrams — không sort, dùng freq array làm key (O(n) per word)
     */
    static List<List<String>> groupAnagramsOptimal(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String s : strs) {
            int[] freq = new int[26];
            for (char c : s.toCharArray()) freq[c - 'a']++;
            String key = Arrays.toString(freq); // "[1,0,...,1,0,...]"
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(groups.values());
    }

    /**
     * Isomorphic Strings (LeetCode #205)
     * "egg" / "add" → true  (e→a, g→d)
     * "foo" / "bar" → false (o→a, o→r — mâu thuẫn)
     *
     * Dùng 2 map: s→t và t→s để đảm bảo bijection
     */
    static boolean isIsomorphic(String s, String t) {
        Map<Character, Character> sToT = new HashMap<>();
        Map<Character, Character> tToS = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char sc = s.charAt(i), tc = t.charAt(i);
            if (sToT.containsKey(sc) && sToT.get(sc) != tc) return false;
            if (tToS.containsKey(tc) && tToS.get(tc) != sc) return false;
            sToT.put(sc, tc);
            tToS.put(tc, sc);
        }
        return true;
    }

    // =========================================================
    // PATTERN 4: Sliding Window + HashMap
    // =========================================================

    /**
     * Longest Substring with At Most 2 Distinct (biến thể của Phase 1)
     * Minimum Window Substring (LeetCode #76) — Hard
     * s="ADOBECODEBANC", t="ABC" → "BANC"
     *
     * Dùng 2 map: cần gì (need) vs có gì trong window (window)
     * Biến formed: đếm ký tự đã thỏa yêu cầu
     */
    static String minWindowSubstring(String s, String t) {
        if (s.isEmpty() || t.isEmpty()) return "";

        Map<Character, Integer> need   = new HashMap<>();
        Map<Character, Integer> window = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

        int left = 0, formed = 0, required = need.size();
        int[] best = {-1, 0, 0}; // length, left, right

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window.merge(c, 1, Integer::sum);

            // Kiểm tra ký tự này đã thỏa yêu cầu chưa
            if (need.containsKey(c) && window.get(c).equals(need.get(c))) formed++;

            // Thu hẹp khi đã thỏa tất cả
            while (formed == required) {
                if (best[0] == -1 || right - left + 1 < best[0]) {
                    best[0] = right - left + 1;
                    best[1] = left;
                    best[2] = right;
                }
                char lc = s.charAt(left++);
                window.merge(lc, -1, Integer::sum);
                if (need.containsKey(lc) && window.get(lc) < need.get(lc)) formed--;
            }
        }
        return best[0] == -1 ? "" : s.substring(best[1], best[2] + 1);
    }

    // =========================================================
    // PATTERN 5: Prefix Sum + HashMap
    // =========================================================

    /**
     * Longest Subarray with Sum = 0
     * [3, -3, 1, -1, 2] → 4  (index 0..3: 3-3+1-1=0)
     *
     * prefix[j] - prefix[i] = 0  →  prefix[i] = prefix[j]
     * Lưu lần đầu gặp prefix sum vào map → tính khoảng cách
     */
    static int longestSubarraySumZero(int[] arr) {
        Map<Integer, Integer> firstSeen = new HashMap<>();
        firstSeen.put(0, -1); // prefix=0 tại vị trí "trước index 0"
        int sum = 0, maxLen = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            if (firstSeen.containsKey(sum))
                maxLen = Math.max(maxLen, i - firstSeen.get(sum));
            else
                firstSeen.put(sum, i); // chỉ lưu lần đầu để maximize length
        }
        return maxLen;
    }

    /**
     * Contiguous Array (LeetCode #525)
     * [0,1,0,1,1,0,1] → 6  (subarray dài nhất có số 0 = số 1)
     *
     * Trick: thay 0 bằng -1, bài toán = longest subarray sum = 0
     */
    static int findMaxLength(int[] nums) {
        for (int i = 0; i < nums.length; i++) if (nums[i] == 0) nums[i] = -1;
        return longestSubarraySumZero(nums);
    }

    // =========================================================
    // HashSet patterns
    // =========================================================

    /**
     * Longest Consecutive Sequence (LeetCode #128) — O(n)
     * [100,4,200,1,3,2] → 4 ([1,2,3,4])
     *
     * Trick: chỉ bắt đầu đếm từ phần tử là "đầu chuỗi" (không có n-1 trong set)
     */
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) set.add(x);

        int best = 0;
        for (int x : set) {
            if (!set.contains(x - 1)) { // x là đầu chuỗi
                int len = 1;
                while (set.contains(x + len)) len++;
                best = Math.max(best, len);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("=== HashMap Patterns ===\n");

        // Pattern 1: Frequency
        System.out.println("--- Pattern 1: Frequency Count ---");
        System.out.println("isAnagram(anagram,nagaram): " + isAnagram("anagram","nagaram")); // true
        System.out.println("firstUniqChar(leetcode):    " + firstUniqChar("leetcode"));      // 0
        System.out.println("topKFrequent(words, k=2):   "
                + topKFrequentWords(new String[]{"i","love","leetcode","i","love","coding"}, 2));

        // Pattern 2: Two Sum
        System.out.println("\n--- Pattern 2: Two Sum / Lookup ---");
        System.out.println("twoSum([2,7,11,15], 9): " + Arrays.toString(twoSum(new int[]{2,7,11,15}, 9)));
        System.out.println("fourSumCount: " + fourSumCount(
                new int[]{1,2}, new int[]{-2,-1}, new int[]{-1,2}, new int[]{0,2})); // 2

        // Pattern 3: Grouping
        System.out.println("\n--- Pattern 3: Grouping ---");
        System.out.println("groupAnagrams: " + groupAnagrams(new String[]{"eat","tea","tan","ate","nat","bat"}));
        System.out.println("isIsomorphic(egg,add): " + isIsomorphic("egg","add")); // true
        System.out.println("isIsomorphic(foo,bar): " + isIsomorphic("foo","bar")); // false

        // Pattern 4: Sliding Window
        System.out.println("\n--- Pattern 4: Sliding Window + Map ---");
        System.out.println("minWindow(ADOBECODEBANC, ABC): " + minWindowSubstring("ADOBECODEBANC","ABC")); // BANC
        System.out.println("minWindow(a, a):               " + minWindowSubstring("a","a")); // a

        // Pattern 5: Prefix Sum
        System.out.println("\n--- Pattern 5: Prefix Sum + Map ---");
        System.out.println("longestSubarraySumZero([3,-3,1,-1,2]): "
                + longestSubarraySumZero(new int[]{3,-3,1,-1,2})); // 4
        System.out.println("findMaxLength([0,1,0,1,1,0,1]):        "
                + findMaxLength(new int[]{0,1,0,1,1,0,1}));        // 6

        // HashSet
        System.out.println("\n--- HashSet: Longest Consecutive ---");
        System.out.println("longestConsecutive([100,4,200,1,3,2]): "
                + longestConsecutive(new int[]{100,4,200,1,3,2})); // 4
        System.out.println("longestConsecutive([0,3,7,2,5,8,4,6,0,1]): "
                + longestConsecutive(new int[]{0,3,7,2,5,8,4,6,0,1})); // 9
    }
}
