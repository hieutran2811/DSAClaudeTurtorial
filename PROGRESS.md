# DSA Progress Tracker

> Started: 2026-03-30 | Goal: Complete all 6 phases by Week 30

---

## Overall Progress

| Phase | Topic | Status | Completion |
|-------|-------|--------|------------|
| 1 | Foundations | ✅ Complete | 19 / 19 topics |
| 2 | Core Data Structures | ✅ Complete | 20 / 20 topics |
| 3 | Algorithms | ✅ Complete | 20 / 20 topics |
| 4 | Graph Algorithms | ✅ Complete | 19 / 19 topics |
| 5 | Dynamic Programming | ✅ Complete | 17 / 17 topics |
| 6 | Advanced Topics | ⬜ Not Started | 0 / 19 topics |

**Total LeetCode solved**: 0 / 200+

---

## Legend
- ⬜ Not Started
- 🔵 In Progress
- ✅ Done
- 🔁 Need Review

---

## PHASE 1 — Foundations

### 1.1 Complexity Analysis
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 1 | Big-O notation | 🔵 | BigONotation.java |
| 2 | Time vs Space complexity | 🔵 | BigONotation.java |
| 3 | Best / Average / Worst case | 🔵 | ComplexityExercises.java |
| 4 | Amortized analysis | 🔵 | ComplexityExercises.java |

### 1.2 Arrays & Strings
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 5 | Static vs Dynamic array | ✅ | ArrayList internals trong BigONotation.java |
| 6 | Two-pointer technique | 🔵 | TwoPointer.java |
| 7 | Sliding window | 🔵 | SlidingWindow.java |
| 8 | Prefix sum | 🔵 | PrefixSum.java |
| 9 | String manipulation | 🔵 | PrefixSum.java — stringTechniques() |

### 1.3 Linked Lists
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 10 | Singly linked list | 🔵 | SinglyLinkedList.java |
| 11 | Doubly linked list | 🔵 | DoublyLinkedList.java |
| 12 | Cycle detection (Floyd) | 🔵 | LinkedListProblems.java |
| 13 | Merge two sorted lists | 🔵 | SinglyLinkedList.java |
| 14 | Find middle node | 🔵 | SinglyLinkedList.java + LinkedListProblems.java |

### 1.4 Stack & Queue
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 15 | Stack implementation | 🔵 | StackProblems.java — MyStack + 6 bài LeetCode |
| 16 | Queue implementation | 🔵 | QueueAndDeque.java — Circular + 2-Stack Queue |
| 17 | Monotonic stack | 🔵 | MonotonicStack.java + StackProblems.java |
| 18 | Deque | 🔵 | QueueAndDeque.java — Sliding Window Max, CircularDeque |
| 19 | Priority Queue (heap) | 🔵 | QueueAndDeque.java — Kth Largest, Top K, Merge K Lists |

---

## PHASE 2 — Core Data Structures

### 2.1 Hash Table
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 20 | HashMap / HashSet internals | 🔵 | HashMapInternals.java |
| 21 | Collision resolution | 🔵 | HashMapInternals.java — Chaining + Linear Probing |
| 22 | Custom HashMap design | 🔵 | HashMapInternals.java — MyHashMap với resize |
| 23 | Frequency counting patterns | 🔵 | HashMapPatterns.java — 5 patterns |

### 2.2 Trees
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 24 | Binary tree traversals (BFS/DFS) | 🔵 | BinaryTree.java — Rec + Iter + BFS |
| 25 | Binary Search Tree (CRUD) | 🔵 | BinarySearchTree.java |
| 26 | Height, depth, diameter | 🔵 | BinaryTree.java — maxDepth, minDepth, diameter, balanced |
| 27 | Lowest Common Ancestor | 🔵 | LowestCommonAncestor.java — 4 variants |
| 28 | Level-order traversal | 🔵 | BinaryTree.java — levelOrder, zigzag, rightSideView |

### 2.3 Heaps
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 29 | Min-heap / Max-heap | 🔵 | Heap.java — MinHeap + MaxHeap from scratch |
| 30 | Heap operations | 🔵 | Heap.java — insert, extractMin/Max, peek O(1) |
| 31 | Build heap O(n) | 🔵 | Heap.java — buildMinHeap, heapSort |
| 32 | K largest / smallest | 🔵 | Heap.java — kLargest, kSmallest, kthLargest, topKFrequent |
| 33 | Merge K sorted lists | 🔵 | Heap.java — mergeKLists, mergeKSortedArrays, MedianFinder |

### 2.4 Trie
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 34 | Trie structure + CRUD | 🔵 | Trie.java — BasicTrie: insert, search, delete |
| 35 | Prefix search | 🔵 | Trie.java — startsWith, countWithPrefix |
| 36 | Autocomplete | 🔵 | Trie.java — AutocompleteTrie, suggestProducts #1268 |
| 37 | Trie with wildcards | 🔵 | Trie.java — WordDictionary '.' wildcard #211 |

---

## PHASE 3 — Algorithms

### 3.1 Sorting
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 38 | Bubble, Selection, Insertion | 🔵 | Sorting.java — 3 variants + early-exit optimization |
| 39 | Merge sort | 🔵 | Sorting.java — mergeSort, countInversions |
| 40 | Quick sort | 🔵 | Sorting.java — random pivot, 3-way (Dutch National Flag) |
| 41 | Heap sort | 🔵 | Sorting.java — in-place, O(n log n) guaranteed |
| 42 | Counting sort, Radix sort | 🔵 | Sorting.java — stable counting, LSD radix |

### 3.2 Binary Search
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 43 | Classic binary search | 🔵 | BinarySearch.java — iterative + recursive |
| 44 | First/last position | 🔵 | BinarySearch.java — lowerBound, upperBound, searchRange #34 |
| 45 | Search rotated array | 🔵 | BinarySearch.java — #33, #81 (dups), #153 findMin |
| 46 | Binary search on answer | 🔵 | BinarySearch.java — #875 Koko, #1011 Ship, #410 Split, #69 sqrt |
| 47 | Peak finding | 🔵 | BinarySearch.java — #162, #852 mountain, #1095 find in mountain |

### 3.3 Recursion & Backtracking
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 48 | Recursion fundamentals | 🔵 | Backtracking.java — factorial, power, flatten |
| 49 | Memoization | 🔵 | Backtracking.java — fibMemo, #70 climbStairs, #322 coinChange |
| 50 | Permutations & combinations | 🔵 | Backtracking.java — #78 #90 subsets, #77 #39 #40 combSum, #46 #47 permute, #17 phone |
| 51 | N-Queens | 🔵 | Backtracking.java — #51 solveNQueens, #52 totalNQueens |
| 52 | Sudoku solver | 🔵 | Backtracking.java — #37 solveSudoku with constraint sets |

### 3.4 Divide & Conquer
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 53 | Merge sort pattern | 🔵 | DivideConquer.java — mergeSort, countInversions, maxSubarray, majorityElement |
| 54 | Quick select | 🔵 | DivideConquer.java — #215 quickSelect O(n), #378 matrix, #703 stream |
| 55 | Closest pair of points | 🔵 | DivideConquer.java — O(n log n) strip algorithm |

---

## PHASE 4 — Graph Algorithms

### 4.1 Graph Representation
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 56 | Adjacency matrix vs list | 🔵 | GraphTraversal.java — AdjacencyMatrix + Graph (adj list) |
| 57 | Directed / Undirected | 🔵 | GraphTraversal.java — Graph.directed flag |
| 58 | Weighted / Unweighted | 🔵 | GraphTraversal.java — int[]{v, weight} edge format |

### 4.2 Graph Traversal
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 59 | BFS | 🔵 | GraphTraversal.java — shortest path, #200 islands, #994 oranges, #127 word ladder |
| 60 | DFS | 🔵 | GraphTraversal.java — recursive+iterative, components, #133 clone, #417 pacific |
| 61 | Topological sort | 🔵 | GraphTraversal.java — Kahn's BFS + DFS post-order, #207 #210 |
| 62 | Cycle detection | 🔵 | GraphTraversal.java — undirected (parent), directed (3-color), #684 |

### 4.3 Shortest Path
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 63 | Dijkstra's algorithm | 🔵 | ShortestPath.java — with path, #743 network, #1631 effort, #778 swim |
| 64 | Bellman-Ford | 🔵 | ShortestPath.java — neg weights, neg cycle detect, #787 cheapest flights |
| 65 | Floyd-Warshall | 🔵 | ShortestPath.java — all pairs, #1334 findTheCity |
| 66 | A* search | 🔵 | ShortestPath.java — Manhattan heuristic, #1091 binary matrix |

### 4.4 Advanced Graph
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 67 | Union-Find / DSU | 🔵 | AdvancedGraph.java — path compression + rank, #200 #547 #684 #1202 |
| 68 | MST — Kruskal | 🔵 | AdvancedGraph.java — sort edges + UF, #1584 |
| 69 | MST — Prim | 🔵 | AdvancedGraph.java — min-heap vertex expansion, #1584 |
| 70 | SCC — Kosaraju | 🔵 | AdvancedGraph.java — 2-pass DFS + transpose graph |
| 71 | Bridges & Articulation points | 🔵 | AdvancedGraph.java — Tarjan disc/low, #1192 |
| 72 | Bipartite check | 🔵 | AdvancedGraph.java — BFS 2-coloring, #785 #886 |

---

## PHASE 5 — Dynamic Programming

### 5.1 DP Fundamentals
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 73 | Memoization (top-down) | 🔵 | DPFundamentals.java — fibMemo, #120 triangle |
| 74 | Tabulation (bottom-up) | 🔵 | DPFundamentals.java — fibTab, fill table from base up |
| 75 | State & recurrence | 🔵 | DPFundamentals.java — annotated throughout all problems |
| 76 | Space optimization | 🔵 | DPFundamentals.java — O(1) fib, rolling 1D array for 2D DP |

### 5.2 Classic DP
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 77 | 1D DP (Fibonacci, stairs) | 🔵 | DPFundamentals.java — #70 #198 #213 #53 #152 #139 |
| 78 | 0/1 Knapsack | 🔵 | DPFundamentals.java — #416 canPartition, #494 targetSum |
| 79 | Unbounded Knapsack | 🔵 | DPFundamentals.java — #322 coinChange, #518 combos, #279 squares |
| 80 | LCS | 🔵 | DPFundamentals.java — #1143 LCS, #712 minDeleteSum, #115 distinct |
| 81 | LIS | 🔵 | DPFundamentals.java — O(n²) DP + O(n log n) binary search, #354 envelopes |
| 82 | Edit distance | 🔵 | DPFundamentals.java — #72 + space-optimized O(n) |

### 5.3 2D DP
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 83 | Grid path problems | 🔵 | AdvancedDP.java — #62 #63 #64 #931 #741 cherry pickup |
| 84 | Matrix chain multiplication | 🔵 | AdvancedDP.java — MCM O(n³), #312 burst balloons |
| 85 | Palindrome partitioning | 🔵 | AdvancedDP.java — #132 minCut, #5 longest palindrome |

### 5.4 Advanced DP
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 86 | Bitmask DP | 🔵 | AdvancedDP.java — TSP O(2^n*n²), #847 shortestPath, #1986 sessions |
| 87 | Interval DP | 🔵 | AdvancedDP.java — #1000 mergeStones, #1039 triangulation |
| 88 | Tree DP | 🔵 | AdvancedDP.java — #337 robTree, #124 maxPathSum, #968 cameras |
| 89 | DP on graphs | 🔵 | AdvancedDP.java — longestPathDAG, #329 matrix LIP, #403 frog jump |

---

## PHASE 6 — Advanced Topics

### 6.1 Advanced Trees
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 90 | AVL Tree | ⬜ | |
| 91 | Red-Black Tree (concepts) | ⬜ | |
| 92 | Segment Tree | ⬜ | |
| 93 | Fenwick Tree / BIT | ⬜ | |
| 94 | Sparse Table | ⬜ | |

### 6.2 String Algorithms
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 95 | KMP | ⬜ | |
| 96 | Rabin-Karp | ⬜ | |
| 97 | Z-algorithm | ⬜ | |
| 98 | Suffix Array (concept) | ⬜ | |

### 6.3 Math & Bit Manipulation
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 99 | Bit tricks | ⬜ | |
| 100 | Power set using bits | ⬜ | |
| 101 | Sieve of Eratosthenes | ⬜ | |
| 102 | GCD / LCM | ⬜ | |
| 103 | Modular arithmetic | ⬜ | |

### 6.4 System Design DSA
| # | Topic | Status | Notes |
|---|-------|--------|-------|
| 104 | LRU Cache | ⬜ | |
| 105 | LFU Cache | ⬜ | |
| 106 | Consistent hashing | ⬜ | |
| 107 | Skip list | ⬜ | |
| 108 | Bloom filter | ⬜ | |

---

## LeetCode Log

| Date | Problem | Difficulty | Pattern | Status |
|------|---------|------------|---------|--------|
| — | — | — | — | — |

---

## Weekly Review

| Week | Dates | Topics Covered | Problems Solved | Notes |
|------|-------|----------------|-----------------|-------|
| 1 | 2026-03-30 → 2026-04-05 | | 0 | |
| 2 | 2026-04-06 → 2026-04-12 | | 0 | |
| 3 | 2026-04-13 → 2026-04-19 | | 0 | |
| 4 | 2026-04-20 → 2026-04-26 | | 0 | |
| 5 | 2026-04-27 → 2026-05-03 | | 0 | |

---

## Insights & Patterns Learned

> Add key "aha moments" here — patterns that clicked, common mistakes, tricks to remember.

-

---

## Files Created in `/src`

| File | Topic | Date |
|------|-------|------|
| Main.java | Project init | 2026-03-30 |
| phase1_foundations/complexity/BigONotation.java | Big-O, Time/Space complexity | 2026-03-30 |
| phase1_foundations/complexity/ComplexityExercises.java | Bài tập phân tích độ phức tạp + Amortized | 2026-03-30 |
| phase1_foundations/arrays/TwoPointer.java | Two Pointer: opposite ends, slow/fast, two arrays | 2026-03-30 |
| phase1_foundations/arrays/SlidingWindow.java | Sliding Window: fixed & dynamic, template | 2026-03-30 |
| phase1_foundations/arrays/PrefixSum.java | Prefix Sum 1D/2D, Subarray Sum K, String techniques | 2026-03-30 |
| phase1_foundations/linkedlist/SinglyLinkedList.java | Singly LL: CRUD, Reverse, Merge, Find Middle | 2026-03-30 |
| phase1_foundations/linkedlist/DoublyLinkedList.java | Doubly LL: CRUD O(1), Browser History | 2026-03-30 |
| phase1_foundations/linkedlist/LinkedListProblems.java | Floyd Cycle, Remove Nth, Reorder, Palindrome, Add 2 Numbers | 2026-03-30 |
| phase1_foundations/stackqueue/StackProblems.java | Stack: Valid Parens, MinStack, RPN, Daily Temp, Histogram, Decode | 2026-03-30 |
| phase1_foundations/stackqueue/QueueAndDeque.java | Queue: Circular, 2-Stack, 2-Queue, Deque, PriorityQueue | 2026-03-30 |
| phase1_foundations/stackqueue/MonotonicStack.java | Mono Stack: NGE, Circular, Trap Rain, Subarray Min, Remove K | 2026-03-30 |
| phase2_core/hashtable/HashMapInternals.java | HashMap internals: Chaining, Open Addressing, Resize, LeetCode #706 | 2026-03-30 |
| phase2_core/hashtable/HashMapPatterns.java | 5 patterns: Freq, TwoSum, Group, SlidingWindow, PrefixSum | 2026-03-30 |
| phase2_core/trees/BinaryTree.java | DFS (rec+iter), BFS, Height, Diameter, PathSum, MaxPathSum | 2026-03-30 |
| phase2_core/trees/BinarySearchTree.java | BST CRUD, Validate, KthSmallest, LCA-BST, Iterator | 2026-03-30 |
| phase2_core/trees/LowestCommonAncestor.java | LCA: Recursive, Iterative, Parent-ptr, Multiple, Distance | 2026-03-30 |
| phase2_core/heaps/Heap.java | MinHeap/MaxHeap, Build O(n), K Largest/Smallest, Merge K, MedianFinder, HeapSort | 2026-03-30 |
| phase2_core/trie/Trie.java | BasicTrie CRUD, Prefix Search, Autocomplete, Wildcard, CountTrie, LCP | 2026-03-30 |
| phase3_algorithms/sorting/Sorting.java | Bubble/Selection/Insertion, MergeSort, QuickSort 3-way, HeapSort, Counting/RadixSort | 2026-03-30 |
| phase3_algorithms/binarysearch/BinarySearch.java | Classic, First/Last, Rotated, BS on Answer, Peak Finding, 2D Matrix | 2026-03-30 |
| phase3_algorithms/backtracking/Backtracking.java | Recursion, Memoization, Subsets/Combos/Perms, N-Queens, Sudoku | 2026-03-30 |
| phase3_algorithms/divideconquer/DivideConquer.java | MergeSort pattern, QuickSelect, Closest Pair, Median 2 Arrays, Merge K Lists | 2026-03-30 |
| phase4_graphs/GraphTraversal.java | Adj Matrix/List, BFS/DFS, Topo Sort (Kahn+DFS), Cycle Detection | 2026-03-30 |
| phase4_graphs/ShortestPath.java | Dijkstra, Bellman-Ford, Floyd-Warshall, A* | 2026-03-30 |
| phase4_graphs/AdvancedGraph.java | Union-Find, Kruskal, Prim, Kosaraju SCC, Bridges/AP, Bipartite | 2026-03-30 |
| phase5_dp/DPFundamentals.java | Memo/Tab/SpaceOpt, 1D DP, 0-1 Knapsack, Unbounded, LCS, LIS, EditDist | 2026-03-30 |
| phase5_dp/AdvancedDP.java | Grid DP, Matrix Chain, Palindrome, Bitmask, Interval, Tree DP, DAG DP | 2026-03-30 |
