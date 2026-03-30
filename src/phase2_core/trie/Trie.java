package phase2_core.trie;

import java.util.*;

/**
 * PHASE 2.4 — TRIE (Prefix Tree)
 * ================================
 * Topics covered:
 *  34. Trie structure + insert / search / delete
 *  35. Prefix search (startsWith)
 *  36. Autocomplete / word suggestion
 *  37. Trie with wildcards ('.' matches any letter)
 *
 * KEY CONCEPTS:
 *  - Trie = tree where each path from root → node spells a prefix
 *  - Each node has up to 26 children (lowercase a-z)
 *  - isEnd flag marks where a complete word ends
 *  - insert / search / startsWith: O(m) where m = word length
 *  - Space: O(ALPHABET * N * M) worst case
 *
 *  Visual for ["apple", "app", "apt"]:
 *        root
 *         |
 *         a
 *         |
 *         p
 *        / \
 *       p*  t
 *       |    \
 *       l     (end*)
 *       |
 *       e*
 *  (* = isEnd)
 */
public class Trie {

    // =========================================================
    // TOPIC 34 — BASIC TRIE: insert / search / delete
    // =========================================================

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    static class BasicTrie {
        private final TrieNode root = new TrieNode();

        /** Insert word — O(m) */
        void insert(String word) {
            TrieNode cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) {
                    cur.children[idx] = new TrieNode();
                }
                cur = cur.children[idx];
            }
            cur.isEnd = true;
        }

        /** Search exact word — O(m) */
        boolean search(String word) {
            TrieNode node = getNode(word);
            return node != null && node.isEnd;
        }

        // -------------------------------------------------------
        // TOPIC 35 — PREFIX SEARCH
        // -------------------------------------------------------

        /** Check if any word starts with prefix — O(m) */
        boolean startsWith(String prefix) {
            return getNode(prefix) != null;
        }

        /** Walk to the node at end of prefix, null if path missing */
        private TrieNode getNode(String prefix) {
            TrieNode cur = root;
            for (char c : prefix.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) return null;
                cur = cur.children[idx];
            }
            return cur;
        }

        /** Count words with given prefix */
        int countWithPrefix(String prefix) {
            TrieNode node = getNode(prefix);
            if (node == null) return 0;
            return countWords(node);
        }

        private int countWords(TrieNode node) {
            int count = node.isEnd ? 1 : 0;
            for (TrieNode child : node.children) {
                if (child != null) count += countWords(child);
            }
            return count;
        }

        /** Delete word — O(m)
         *  Strategy: recursive delete; remove node only if it has no children
         *  and is not prefix of another word.
         */
        boolean delete(String word) {
            return deleteHelper(root, word, 0);
        }

        private boolean deleteHelper(TrieNode cur, String word, int i) {
            if (i == word.length()) {
                if (!cur.isEnd) return false; // word not found
                cur.isEnd = false;
                return isLeaf(cur); // true = caller can delete this node
            }
            int idx = word.charAt(i) - 'a';
            TrieNode child = cur.children[idx];
            if (child == null) return false;

            boolean shouldDelete = deleteHelper(child, word, i + 1);
            if (shouldDelete) {
                cur.children[idx] = null;
                return !cur.isEnd && isLeaf(cur); // delete cur if also unused
            }
            return false;
        }

        private boolean isLeaf(TrieNode node) {
            for (TrieNode c : node.children) if (c != null) return false;
            return true;
        }
    }

    // =========================================================
    // TOPIC 36 — AUTOCOMPLETE / WORD SUGGESTION
    // =========================================================

    static class AutocompleteTrie {
        private final TrieNode root = new TrieNode();

        void insert(String word) {
            TrieNode cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) cur.children[idx] = new TrieNode();
                cur = cur.children[idx];
            }
            cur.isEnd = true;
        }

        /**
         * Return all words with given prefix — O(m + output)
         * Used in autocomplete systems.
         */
        List<String> autocomplete(String prefix) {
            List<String> results = new ArrayList<>();
            TrieNode node = getNode(prefix);
            if (node == null) return results;
            dfsCollect(node, new StringBuilder(prefix), results);
            return results;
        }

        private void dfsCollect(TrieNode node, StringBuilder path, List<String> results) {
            if (node.isEnd) results.add(path.toString());
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    path.append((char) ('a' + i));
                    dfsCollect(node.children[i], path, results);
                    path.deleteCharAt(path.length() - 1); // backtrack
                }
            }
        }

        /**
         * Return top-k suggestions sorted lexicographically — O(m + output)
         * LeetCode #1268 variant
         */
        List<String> topKSuggestions(String prefix, int k) {
            List<String> all = autocomplete(prefix);
            Collections.sort(all); // already lex order from DFS left→right
            return all.subList(0, Math.min(k, all.size()));
        }

        private TrieNode getNode(String prefix) {
            TrieNode cur = root;
            for (char c : prefix.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) return null;
                cur = cur.children[idx];
            }
            return cur;
        }
    }

    /**
     * LeetCode #1268 — Search Suggestions System
     * Given a list of products and a searchWord,
     * after each character typed return up to 3 lexicographic suggestions.
     *
     * Time: O(N*M + L²) where N=products, M=avg length, L=searchWord length
     */
    static List<List<String>> suggestProducts(String[] products, String searchWord) {
        AutocompleteTrie trie = new AutocompleteTrie();
        for (String p : products) trie.insert(p);

        List<List<String>> result = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (char c : searchWord.toCharArray()) {
            prefix.append(c);
            result.add(trie.topKSuggestions(prefix.toString(), 3));
        }
        return result;
    }

    // =========================================================
    // TOPIC 37 — TRIE WITH WILDCARDS
    // =========================================================

    /**
     * WordDictionary — LeetCode #211
     * addWord(word)  : standard insert
     * search(word)   : supports '.' which matches any single letter
     *
     * Time: search O(m * 26^k) where k = number of '.' in word
     *       In practice fast because most paths are pruned.
     */
    static class WordDictionary {
        private final TrieNode root = new TrieNode();

        void addWord(String word) {
            TrieNode cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) cur.children[idx] = new TrieNode();
                cur = cur.children[idx];
            }
            cur.isEnd = true;
        }

        boolean search(String word) {
            return searchHelper(root, word, 0);
        }

        private boolean searchHelper(TrieNode node, String word, int i) {
            if (i == word.length()) return node.isEnd;

            char c = word.charAt(i);
            if (c == '.') {
                // wildcard: try all 26 children
                for (TrieNode child : node.children) {
                    if (child != null && searchHelper(child, word, i + 1)) return true;
                }
                return false;
            } else {
                TrieNode child = node.children[c - 'a'];
                return child != null && searchHelper(child, word, i + 1);
            }
        }
    }

    // =========================================================
    // BONUS — TRIE WITH COUNT (for prefix frequency)
    // =========================================================

    /**
     * Each node stores:
     *   passCount — how many words pass through this node (prefix count)
     *   endCount  — how many words end here (exact count)
     *
     * Useful for: "how many words start with prefix X?"
     *             "how many times was word X inserted?"
     * LeetCode #14 (Longest Common Prefix), #820 (Short Encoding of Words)
     */
    static class CountTrie {
        static class CNode {
            CNode[] children = new CNode[26];
            int passCount = 0; // words passing through
            int endCount  = 0; // words ending here
        }

        private final CNode root = new CNode();

        void insert(String word) {
            CNode cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) cur.children[idx] = new CNode();
                cur = cur.children[idx];
                cur.passCount++;
            }
            cur.endCount++;
        }

        /** How many inserted words have this prefix? */
        int prefixCount(String prefix) {
            CNode cur = root;
            for (char c : prefix.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) return 0;
                cur = cur.children[idx];
            }
            return cur.passCount;
        }

        /** How many times was this exact word inserted? */
        int wordCount(String word) {
            CNode cur = root;
            for (char c : word.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) return 0;
                cur = cur.children[idx];
            }
            return cur.endCount;
        }
    }

    // =========================================================
    // BONUS — LONGEST COMMON PREFIX — LeetCode #14
    // =========================================================
    /**
     * Insert all words into trie.
     * Walk from root while: only one child AND not an end node.
     * Time: O(N*M)
     */
    static String longestCommonPrefix(String[] words) {
        if (words.length == 0) return "";
        BasicTrie trie = new BasicTrie();
        for (String w : words) trie.insert(w);

        StringBuilder lcp = new StringBuilder();
        TrieNode cur = trie.root;
        while (true) {
            // count non-null children
            int childCount = 0;
            int nextIdx = -1;
            for (int i = 0; i < 26; i++) {
                if (cur.children[i] != null) { childCount++; nextIdx = i; }
            }
            if (childCount != 1 || cur.isEnd) break;
            lcp.append((char) ('a' + nextIdx));
            cur = cur.children[nextIdx];
        }
        return lcp.toString();
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 34: Basic Trie (insert/search/delete) ===");
        BasicTrie bt = new BasicTrie();
        for (String w : new String[]{"apple", "app", "apt", "bat", "ball"}) bt.insert(w);
        System.out.println("search 'apple': " + bt.search("apple")); // true
        System.out.println("search 'app':   " + bt.search("app"));   // true
        System.out.println("search 'ap':    " + bt.search("ap"));    // false (not a word)
        System.out.println("search 'apt':   " + bt.search("apt"));   // true

        System.out.println("\n--- Delete 'app' ---");
        bt.delete("app");
        System.out.println("search 'app' after delete: " + bt.search("app"));    // false
        System.out.println("search 'apple' still ok:   " + bt.search("apple"));  // true

        System.out.println("\n=== TOPIC 35: Prefix Search ===");
        System.out.println("startsWith 'ap': " + bt.startsWith("ap"));  // true
        System.out.println("startsWith 'ba': " + bt.startsWith("ba"));  // true
        System.out.println("startsWith 'ca': " + bt.startsWith("ca"));  // false
        System.out.println("countWithPrefix 'ap': " + bt.countWithPrefix("ap")); // 2 (apple, apt)

        System.out.println("\n=== TOPIC 36: Autocomplete ===");
        AutocompleteTrie at = new AutocompleteTrie();
        for (String w : new String[]{"mobile", "mouse", "moneypot", "monitor", "mousepad"}) at.insert(w);
        System.out.println("autocomplete 'mo': " + at.autocomplete("mo"));
        System.out.println("top3 'mo':         " + at.topKSuggestions("mo", 3));

        System.out.println("\nLeetCode #1268 suggestProducts:");
        String[] products = {"mobile", "mouse", "moneypot", "monitor", "mousepad"};
        List<List<String>> suggestions = suggestProducts(products, "mouse");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println("  prefix 'mouse'[0.." + i + "]: " + suggestions.get(i));
        }

        System.out.println("\n=== TOPIC 37: Trie with Wildcards ===");
        WordDictionary wd = new WordDictionary();
        for (String w : new String[]{"bad", "dad", "mad"}) wd.addWord(w);
        System.out.println("search 'pad': " + wd.search("pad"));   // false
        System.out.println("search 'bad': " + wd.search("bad"));   // true
        System.out.println("search '.ad': " + wd.search(".ad"));   // true (bad/dad/mad)
        System.out.println("search 'b..': " + wd.search("b.."));   // true
        System.out.println("search '...': " + wd.search("..."));   // true
        System.out.println("search '....': " + wd.search("...."));  // false (no 4-letter word)

        System.out.println("\n=== BONUS: Count Trie ===");
        CountTrie ct = new CountTrie();
        for (String w : new String[]{"apple", "apple", "app", "apt", "bat"}) ct.insert(w);
        System.out.println("prefixCount 'app': " + ct.prefixCount("app")); // 3 (apple×2, app)
        System.out.println("wordCount 'apple': " + ct.wordCount("apple")); // 2
        System.out.println("wordCount 'app':   " + ct.wordCount("app"));   // 1

        System.out.println("\n=== BONUS: Longest Common Prefix ===");
        System.out.println(longestCommonPrefix(new String[]{"flower", "flow", "flight"})); // fl
        System.out.println(longestCommonPrefix(new String[]{"dog", "racecar", "car"}));    // ""
        System.out.println(longestCommonPrefix(new String[]{"interview", "interact", "internal"})); // inter
    }
}
