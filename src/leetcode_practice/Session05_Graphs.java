package leetcode_practice;

import java.util.*;

/**
 * LEETCODE PRACTICE -- SESSION 05
 * Pattern: GRAPHS
 *
 * Graph Mental Model:
 *   - BFS: shortest path (unweighted), level-by-level, use Queue
 *   - DFS: connectivity, cycle detection, topological sort, use Stack/Recursion
 *   - Union-Find: connected components, cycle detection in undirected graph
 *   - Dijkstra: shortest path (weighted, non-negative), use PriorityQueue
 *   - Topological Sort: DAG ordering (Kahn's BFS or DFS post-order)
 *
 * Template decision:
 *   Shortest path, unweighted?      => BFS
 *   Shortest path, weighted >= 0?   => Dijkstra
 *   Connectivity / components?      => BFS/DFS or Union-Find
 *   Dependency ordering?            => Topological Sort (Kahn's)
 *   Cycle in undirected?            => Union-Find or DFS (parent tracking)
 *   Cycle in directed?              => DFS 3-color (WHITE/GRAY/BLACK)
 *
 * Problems:
 *   Medium: #200 Number of Islands
 *           #133 Clone Graph
 *           #207 Course Schedule (cycle in directed graph)
 *           #210 Course Schedule II (topological sort)
 *           #743 Network Delay Time (Dijkstra)
 *           #787 Cheapest Flights Within K Stops (Bellman-Ford variant)
 *           #684 Redundant Connection (Union-Find)
 *           #785 Is Graph Bipartite?
 *   Hard:   #127 Word Ladder (BFS shortest path)
 *           #269 Alien Dictionary (topological sort)
 */
public class Session05_Graphs {

    public static void main(String[] args) {
        System.out.println("=== SESSION 05: GRAPHS ===\n");
        testBFSDFS();
        testTopSort();
        testShortestPath();
        testUnionFind();
        testHard();
    }

    // =========================================================================
    // BFS / DFS ON GRIDS & GRAPHS
    // =========================================================================

    // #200 Number of Islands
    // DFS/BFS: flood-fill each unvisited '1', mark visited by changing to '0'
    // Each flood-fill call = one island.
    static int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++)
            for (int c = 0; c < grid[0].length; c++)
                if (grid[r][c] == '1') { dfsIsland(grid, r, c); count++; }
        return count;
    }

    static void dfsIsland(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1')
            return;
        grid[r][c] = '0'; // mark visited
        dfsIsland(grid, r+1, c); dfsIsland(grid, r-1, c);
        dfsIsland(grid, r, c+1); dfsIsland(grid, r, c-1);
    }

    // #133 Clone Graph
    // BFS + HashMap<original, clone>
    // For each node dequeued, clone its neighbors and add unvisited ones to queue.
    static class Node {
        int val;
        List<Node> neighbors;
        Node(int val) { this.val = val; this.neighbors = new ArrayList<>(); }
    }

    static Node cloneGraph(Node node) {
        if (node == null) return null;
        Map<Node, Node> cloned = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        cloned.put(node, new Node(node.val));
        queue.add(node);
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            for (Node neighbor : cur.neighbors) {
                if (!cloned.containsKey(neighbor)) {
                    cloned.put(neighbor, new Node(neighbor.val));
                    queue.add(neighbor);
                }
                cloned.get(cur).neighbors.add(cloned.get(neighbor));
            }
        }
        return cloned.get(node);
    }

    // #785 Is Graph Bipartite?
    // Try 2-coloring with BFS: alternate colors for each neighbor.
    // If a neighbor has the same color => NOT bipartite.
    static boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n]; // 0=uncolored, 1=red, -1=blue
        for (int start = 0; start < n; start++) {
            if (color[start] != 0) continue;
            Queue<Integer> q = new LinkedList<>();
            q.add(start);
            color[start] = 1;
            while (!q.isEmpty()) {
                int node = q.poll();
                for (int neighbor : graph[node]) {
                    if (color[neighbor] == 0) {
                        color[neighbor] = -color[node]; // opposite color
                        q.add(neighbor);
                    } else if (color[neighbor] == color[node]) {
                        return false; // same color => not bipartite
                    }
                }
            }
        }
        return true;
    }

    static void testBFSDFS() {
        System.out.println("--- BFS / DFS ---");

        char[][] grid1 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        System.out.println("#200 numIslands (1 island) = " + numIslands(grid1)); // 1

        char[][] grid2 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("#200 numIslands (3 islands) = " + numIslands(grid2)); // 3

        // #785 Bipartite
        System.out.println("#785 isBipartite([[1,3],[0,2],[1,3],[0,2]]) = "
            + isBipartite(new int[][]{{1,3},{0,2},{1,3},{0,2}})); // true
        System.out.println("#785 isBipartite([[1,2,3],[0,2],[0,1,3],[0,2]]) = "
            + isBipartite(new int[][]{{1,2,3},{0,2},{0,1,3},{0,2}})); // false
        System.out.println();
    }

    // =========================================================================
    // TOPOLOGICAL SORT
    // =========================================================================

    // #207 Course Schedule -- can you finish all courses? (cycle detection in DAG)
    // Kahn's algorithm: track in-degrees, BFS from nodes with in-degree 0.
    // If processed count == numCourses => no cycle => can finish.
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]); // pre[1] -> pre[0]
            inDegree[pre[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++)
            if (inDegree[i] == 0) q.add(i);
        int processed = 0;
        while (!q.isEmpty()) {
            int course = q.poll();
            processed++;
            for (int next : adj.get(course))
                if (--inDegree[next] == 0) q.add(next);
        }
        return processed == numCourses;
    }

    // #210 Course Schedule II -- return one valid topological order
    // Same as #207 but collect the order while processing.
    static int[] findOrder(int numCourses, int[][] prerequisites) {
        int[] inDegree = new int[numCourses];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            inDegree[pre[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++)
            if (inDegree[i] == 0) q.add(i);
        int[] order = new int[numCourses];
        int idx = 0;
        while (!q.isEmpty()) {
            int course = q.poll();
            order[idx++] = course;
            for (int next : adj.get(course))
                if (--inDegree[next] == 0) q.add(next);
        }
        return idx == numCourses ? order : new int[0]; // empty if cycle
    }

    static void testTopSort() {
        System.out.println("--- TOPOLOGICAL SORT ---");

        System.out.println("#207 canFinish(2,[[1,0]])        = "
            + canFinish(2, new int[][]{{1,0}}));        // true
        System.out.println("#207 canFinish(2,[[1,0],[0,1]]) = "
            + canFinish(2, new int[][]{{1,0},{0,1}}));  // false (cycle)

        System.out.println("#210 findOrder(4,[[1,0],[2,0],[3,1],[3,2]]) = "
            + Arrays.toString(findOrder(4, new int[][]{{1,0},{2,0},{3,1},{3,2}}))); // [0,1,2,3] or similar
        System.out.println("#210 findOrder(2,[[1,0],[0,1]])             = "
            + Arrays.toString(findOrder(2, new int[][]{{1,0},{0,1}})));             // [] cycle
        System.out.println();
    }

    // =========================================================================
    // SHORTEST PATH
    // =========================================================================

    // #743 Network Delay Time -- Dijkstra
    // Find time for signal to reach all nodes from source k.
    // = single-source shortest path on weighted directed graph.
    // Answer = max of all shortest distances (-1 if any node unreachable).
    static int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list
        List<int[]>[] adj = new List[n + 1];
        for (int i = 1; i <= n; i++) adj[i] = new ArrayList<>();
        for (int[] t : times) adj[t[0]].add(new int[]{t[1], t[2]});

        // Dijkstra with min-heap [dist, node]
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, k});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], u = cur[1];
            if (d > dist[u]) continue; // stale entry
            for (int[] edge : adj[u]) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }
        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }

    // #787 Cheapest Flights Within K Stops -- Bellman-Ford (K relaxation rounds)
    // KEY: do exactly K+1 relaxation rounds (K stops = K+1 edges).
    // Use a COPY of previous dist array each round to avoid using same edge twice.
    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        for (int i = 0; i <= k; i++) {       // K+1 rounds
            int[] temp = dist.clone();        // copy: freeze current round
            for (int[] f : flights) {
                int u = f[0], v = f[1], w = f[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < temp[v])
                    temp[v] = dist[u] + w;
            }
            dist = temp;
        }
        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }

    static void testShortestPath() {
        System.out.println("--- SHORTEST PATH ---");

        System.out.println("#743 networkDelayTime([[2,1,1],[2,3,1],[3,4,1]], n=4, k=2) = "
            + networkDelayTime(new int[][]{{2,1,1},{2,3,1},{3,4,1}}, 4, 2)); // 2
        System.out.println("#743 networkDelayTime([[1,2,1]], n=2, k=1)                = "
            + networkDelayTime(new int[][]{{1,2,1}}, 2, 1));                  // 1
        System.out.println("#743 networkDelayTime([[1,2,1]], n=2, k=2)                = "
            + networkDelayTime(new int[][]{{1,2,1}}, 2, 2));                  // -1

        System.out.println("#787 findCheapestPrice(4,[[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]],0,3,1) = "
            + findCheapestPrice(4, new int[][]{{0,1,100},{1,2,100},{2,0,100},{1,3,600},{2,3,200}}, 0, 3, 1)); // 700
        System.out.println("#787 findCheapestPrice(3,[[0,1,100],[1,2,100],[0,2,500]],0,2,1) = "
            + findCheapestPrice(3, new int[][]{{0,1,100},{1,2,100},{0,2,500}}, 0, 2, 1)); // 200
        System.out.println();
    }

    // =========================================================================
    // UNION-FIND
    // =========================================================================

    static class UnionFind {
        int[] parent, rank;
        int components;

        UnionFind(int n) {
            parent = new int[n]; rank = new int[n];
            components = n;
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]); // path compression
            return parent[x];
        }

        boolean union(int x, int y) {
            int px = find(x), py = find(y);
            if (px == py) return false; // already connected
            if (rank[px] < rank[py])    { int t = px; px = py; py = t; }
            parent[py] = px;
            if (rank[px] == rank[py]) rank[px]++;
            components--;
            return true;
        }

        boolean connected(int x, int y) { return find(x) == find(y); }
    }

    // #684 Redundant Connection
    // Find the edge that creates a cycle in an undirected graph (tree + 1 edge).
    // Process edges one by one; the first edge where both endpoints already connected
    // (same component) is the redundant edge.
    static int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        UnionFind uf = new UnionFind(n + 1);
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) return edge; // cycle detected
        }
        return new int[0];
    }

    static void testUnionFind() {
        System.out.println("--- UNION-FIND ---");

        System.out.println("#684 findRedundantConnection([[1,2],[1,3],[2,3]]) = "
            + Arrays.toString(findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}}))); // [2,3]
        System.out.println("#684 findRedundantConnection([[1,2],[2,3],[3,4],[1,4],[1,5]]) = "
            + Arrays.toString(findRedundantConnection(new int[][]{{1,2},{2,3},{3,4},{1,4},{1,5}}))); // [1,4]
        System.out.println();
    }

    // =========================================================================
    // HARD
    // =========================================================================

    // #127 Word Ladder
    // BFS shortest path: each step changes exactly one letter to form a valid word.
    // Brute: try all pairs at each step -- too slow.
    // Key optimization: for each word, generate all 26-letter patterns ("h*t", "*ot"),
    //   group words by pattern. BFS on patterns avoids O(n^2) comparison.
    static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Queue<String> q = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        q.add(beginWord); visited.add(beginWord);
        int steps = 1;

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                String word = q.poll();
                char[] arr = word.toCharArray();
                for (int j = 0; j < arr.length; j++) {
                    char orig = arr[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == orig) continue;
                        arr[j] = c;
                        String next = new String(arr);
                        if (next.equals(endWord)) return steps + 1;
                        if (wordSet.contains(next) && !visited.contains(next)) {
                            visited.add(next); q.add(next);
                        }
                    }
                    arr[j] = orig; // restore
                }
            }
            steps++;
        }
        return 0;
    }

    // #269 Alien Dictionary
    // Given sorted words in alien language, find character order.
    // 1. Build graph: for each adjacent word pair, compare char by char.
    //    First differing char: w1[i] -> w2[i] (w1[i] comes before w2[i]).
    //    Edge case: if w1 is prefix of w2 but w1 is longer => invalid ("abc","ab").
    // 2. Topological sort the char graph.
    static String alienOrder(String[] words) {
        // Collect all unique chars
        Map<Character, Set<Character>> adj = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        for (String w : words)
            for (char c : w.toCharArray()) { adj.putIfAbsent(c, new HashSet<>()); inDegree.putIfAbsent(c, 0); }

        // Build edges
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i], w2 = words[i + 1];
            int len = Math.min(w1.length(), w2.length());
            boolean found = false;
            for (int j = 0; j < len; j++) {
                char c1 = w1.charAt(j), c2 = w2.charAt(j);
                if (c1 != c2) {
                    if (!adj.get(c1).contains(c2)) {
                        adj.get(c1).add(c2);
                        inDegree.merge(c2, 1, Integer::sum);
                    }
                    found = true; break;
                }
            }
            if (!found && w1.length() > w2.length()) return ""; // invalid
        }

        // Kahn's topological sort
        Queue<Character> q = new LinkedList<>();
        for (char c : inDegree.keySet())
            if (inDegree.get(c) == 0) q.add(c);
        StringBuilder sb = new StringBuilder();
        while (!q.isEmpty()) {
            char c = q.poll();
            sb.append(c);
            for (char next : adj.get(c))
                if (inDegree.merge(next, -1, Integer::sum) == 0) q.add(next);
        }
        return sb.length() == inDegree.size() ? sb.toString() : ""; // "" if cycle
    }

    static void testHard() {
        System.out.println("--- HARD ---");

        System.out.println("#127 ladderLength(\"hit\",\"cog\",[\"hot\",\"dot\",\"dog\",\"lot\",\"log\",\"cog\"]) = "
            + ladderLength("hit", "cog", Arrays.asList("hot","dot","dog","lot","log","cog"))); // 5
        System.out.println("#127 ladderLength(\"hit\",\"cog\",[\"hot\",\"dot\",\"dog\",\"lot\",\"log\"]) = "
            + ladderLength("hit", "cog", Arrays.asList("hot","dot","dog","lot","log")));       // 0

        System.out.println("#269 alienOrder([\"wrt\",\"wrf\",\"er\",\"ett\",\"rftt\"]) = \""
            + alienOrder(new String[]{"wrt","wrf","er","ett","rftt"}) + "\""); // "wertf"
        System.out.println("#269 alienOrder([\"z\",\"x\"])                            = \""
            + alienOrder(new String[]{"z","x"}) + "\"");                      // "zx"
        System.out.println("#269 alienOrder([\"z\",\"x\",\"z\"])                      = \""
            + alienOrder(new String[]{"z","x","z"}) + "\"");                  // "" (cycle)

        System.out.println();
        System.out.println("PATTERN SUMMARY -- Graphs:");
        System.out.println("  BFS grid         : flood-fill, mark visited in-place (grid[r][c]='0')");
        System.out.println("  Clone graph      : HashMap<orig,clone> + BFS");
        System.out.println("  Bipartite check  : BFS 2-coloring, conflict => false");
        System.out.println("  Topo sort        : in-degree[] + Kahn's BFS => detect cycle if count<n");
        System.out.println("  Dijkstra         : PQ[dist,node], skip stale: if d > dist[u] continue");
        System.out.println("  Bellman-Ford K   : K+1 rounds, freeze dist with clone each round");
        System.out.println("  Union-Find       : path compress + rank => near O(1) amortized");
        System.out.println("  Word ladder BFS  : mutate each position a..z, O(26 * L * N)");
        System.out.println("  Alien dict       : adjacent words => char edges => topo sort");
    }
}
