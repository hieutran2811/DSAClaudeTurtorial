# DSA Learning Roadmap — From Zero to Advanced

> Language: Java | Target: Software Architecture & Interview-ready

---

## PHASE 1 — Foundations (Weeks 1–3)

### 1.1 Complexity Analysis
- [ ] Big-O notation (O(1), O(log n), O(n), O(n log n), O(n²))
- [ ] Time complexity vs Space complexity
- [ ] Best / Average / Worst case
- [ ] Amortized analysis

### 1.2 Arrays & Strings
- [ ] Static array vs Dynamic array (ArrayList)
- [ ] Two-pointer technique
- [ ] Sliding window
- [ ] Prefix sum
- [ ] String manipulation (StringBuilder, char[])

### 1.3 Linked Lists
- [ ] Singly linked list (insert, delete, reverse)
- [ ] Doubly linked list
- [ ] Cycle detection (Floyd's algorithm)
- [ ] Merge two sorted lists
- [ ] Find middle node

### 1.4 Stack & Queue
- [ ] Stack with array / LinkedList
- [ ] Queue with array / LinkedList
- [ ] Monotonic stack
- [ ] Deque (double-ended queue)
- [ ] Priority Queue (min-heap, max-heap)

---

## PHASE 2 — Core Data Structures (Weeks 4–7)

### 2.1 Hash Table
- [ ] HashMap / HashSet internals
- [ ] Collision resolution (chaining vs open addressing)
- [ ] Design custom HashMap
- [ ] Frequency counting patterns

### 2.2 Trees
- [ ] Binary tree (BFS, DFS — inorder, preorder, postorder)
- [ ] Binary Search Tree (insert, delete, search, validate)
- [ ] Height, depth, diameter
- [ ] Lowest Common Ancestor (LCA)
- [ ] Level-order traversal

### 2.3 Heaps
- [ ] Min-heap / Max-heap
- [ ] Heap operations (insert, extract, heapify)
- [ ] Build heap O(n)
- [ ] K largest / smallest elements
- [ ] Merge K sorted lists

### 2.4 Trie
- [ ] Trie structure and insert/search/delete
- [ ] Prefix search
- [ ] Word dictionary / autocomplete
- [ ] Trie with wildcards

---

## PHASE 3 — Algorithms (Weeks 8–12)

### 3.1 Sorting
- [ ] Bubble, Selection, Insertion sort — O(n²)
- [ ] Merge sort — O(n log n)
- [ ] Quick sort — O(n log n) avg
- [ ] Heap sort
- [ ] Counting sort, Radix sort — O(n)
- [ ] Know when to use which

### 3.2 Binary Search
- [ ] Classic binary search
- [ ] Find first/last position
- [ ] Search rotated array
- [ ] Binary search on answer (min/max problems)
- [ ] Peak finding

### 3.3 Recursion & Backtracking
- [ ] Recursion fundamentals & call stack
- [ ] Memoization
- [ ] Permutations & combinations
- [ ] N-Queens
- [ ] Sudoku solver
- [ ] Subsets / power set

### 3.4 Divide & Conquer
- [ ] Merge sort pattern
- [ ] Quick select (kth element)
- [ ] Matrix multiplication
- [ ] Closest pair of points

---

## PHASE 4 — Graph Algorithms (Weeks 13–17)

### 4.1 Graph Representation
- [ ] Adjacency matrix vs Adjacency list
- [ ] Directed vs Undirected
- [ ] Weighted vs Unweighted

### 4.2 Graph Traversal
- [ ] BFS (shortest path in unweighted graph)
- [ ] DFS (connected components, cycle detection)
- [ ] Topological sort (Kahn's + DFS)
- [ ] Detect cycle in directed/undirected graph

### 4.3 Shortest Path
- [ ] Dijkstra's algorithm (non-negative weights)
- [ ] Bellman-Ford (negative weights)
- [ ] Floyd-Warshall (all pairs)
- [ ] A* search (heuristic)

### 4.4 Advanced Graph
- [ ] Union-Find / Disjoint Set Union (DSU)
- [ ] Minimum Spanning Tree (Kruskal, Prim)
- [ ] Strongly Connected Components (Tarjan, Kosaraju)
- [ ] Bridges & Articulation points

---

## PHASE 5 — Dynamic Programming (Weeks 18–24)

### 5.1 DP Fundamentals
- [ ] Memoization (top-down)
- [ ] Tabulation (bottom-up)
- [ ] State definition & recurrence relation
- [ ] Space optimization

### 5.2 Classic DP Patterns
- [ ] 1D DP: Fibonacci, climbing stairs, house robber
- [ ] Knapsack (0/1 knapsack, unbounded)
- [ ] Longest Common Subsequence (LCS)
- [ ] Longest Increasing Subsequence (LIS)
- [ ] Edit distance

### 5.3 2D DP
- [ ] Grid path problems
- [ ] Matrix chain multiplication
- [ ] Palindrome partitioning
- [ ] Burst balloons

### 5.4 Advanced DP
- [ ] Bitmask DP
- [ ] Interval DP
- [ ] Tree DP
- [ ] DP on graphs

---

## PHASE 6 — Advanced Topics (Weeks 25–30)

### 6.1 Advanced Trees
- [ ] AVL Tree (rotations, balancing)
- [ ] Red-Black Tree (concepts)
- [ ] Segment Tree (range query, point update)
- [ ] Fenwick Tree / Binary Indexed Tree (BIT)
- [ ] Sparse Table (range minimum query)

### 6.2 String Algorithms
- [ ] KMP (pattern matching)
- [ ] Rabin-Karp (rolling hash)
- [ ] Z-algorithm
- [ ] Suffix Array & Suffix Tree (concept)

### 6.3 Math & Bit Manipulation
- [ ] Bit tricks (XOR, AND, OR, shifts)
- [ ] Power set using bits
- [ ] Sieve of Eratosthenes
- [ ] GCD / LCM (Euclidean)
- [ ] Modular arithmetic & fast exponentiation

### 6.4 System Design DSA Connections
- [ ] LRU Cache (HashMap + DoublyLinkedList)
- [ ] LFU Cache
- [ ] Consistent hashing
- [ ] Skip list
- [ ] Bloom filter

---

## Recommended Practice Problems by Phase

| Phase | LeetCode Tags | Count |
|-------|--------------|-------|
| 1 | Array, String, Two Pointers, Sliding Window | ~30 |
| 2 | Hash Table, Tree, Heap, Trie | ~30 |
| 3 | Sorting, Binary Search, Backtracking | ~25 |
| 4 | Graph, BFS, DFS, Union Find | ~30 |
| 5 | Dynamic Programming | ~40 |
| 6 | Segment Tree, String, Bit Manipulation | ~25 |

---

## Study Resources

- **Book**: "Introduction to Algorithms" (CLRS) — reference
- **Book**: "Cracking the Coding Interview" — interview prep
- **Platform**: LeetCode (primary), NeetCode.io (roadmap)
- **Video**: NeetCode YouTube, Abdul Bari (algorithms)
- **Java specifics**: Java Collections Framework docs

---

## Milestones

| Milestone | Target |
|-----------|--------|
| Phase 1 complete | Week 3 |
| Phase 2 complete | Week 7 |
| Phase 3 complete | Week 12 |
| Phase 4 complete | Week 17 |
| Phase 5 complete | Week 24 |
| Phase 6 complete | Week 30 |
| 200+ LeetCode solved | Week 30 |
