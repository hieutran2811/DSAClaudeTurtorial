package phase4_graphs;

import java.util.*;

/**
 * PHASE 4.1 + 4.2 — GRAPH REPRESENTATION & TRAVERSAL
 * =====================================================
 * Topics covered:
 *  56. Adjacency matrix vs Adjacency list
 *  57. Directed vs Undirected graph
 *  58. Weighted vs Unweighted graph
 *  59. BFS — shortest path in unweighted graph
 *  60. DFS — connected components, cycle detection
 *  61. Topological sort — Kahn's (BFS) + DFS
 *  62. Cycle detection — directed & undirected
 *
 * GRAPH VOCABULARY:
 *   V = vertices (nodes),  E = edges
 *   Degree: number of edges at a vertex
 *   Path: sequence of vertices connected by edges
 *   Cycle: path that starts and ends at the same vertex
 *   DAG: Directed Acyclic Graph (no cycles)
 *   Connected: every vertex reachable from every other (undirected)
 *   Strongly Connected: every vertex reachable from every other (directed)
 */
public class GraphTraversal {

    // =========================================================
    // TOPIC 56-58 — GRAPH REPRESENTATIONS
    // =========================================================

    /**
     * ADJACENCY MATRIX
     * matrix[u][v] = weight (0 = no edge, 1 = unweighted edge)
     *
     * Space:  O(V²) — bad for sparse graphs
     * Add edge:     O(1)
     * Remove edge:  O(1)
     * Check edge:   O(1)   ← advantage over list
     * List neighbors: O(V) ← disadvantage
     *
     * Use when: dense graph (E ≈ V²), or need O(1) edge lookup
     */
    static class AdjacencyMatrix {
        private final int[][] matrix;
        private final int V;

        AdjacencyMatrix(int V) {
            this.V = V;
            matrix = new int[V][V];
        }

        // Undirected: add both directions
        void addEdge(int u, int v) { matrix[u][v] = matrix[v][u] = 1; }

        // Weighted undirected
        void addWeightedEdge(int u, int v, int w) { matrix[u][v] = matrix[v][u] = w; }

        // Directed
        void addDirectedEdge(int u, int v) { matrix[u][v] = 1; }

        boolean hasEdge(int u, int v) { return matrix[u][v] != 0; }

        List<Integer> neighbors(int u) {
            List<Integer> list = new ArrayList<>();
            for (int v = 0; v < V; v++) if (matrix[u][v] != 0) list.add(v);
            return list;
        }

        void print() {
            System.out.println("Adjacency Matrix:");
            for (int[] row : matrix) System.out.println("  " + Arrays.toString(row));
        }
    }

    /**
     * ADJACENCY LIST
     * list[u] = list of (v, weight) pairs
     *
     * Space:  O(V + E) — efficient for sparse graphs
     * Add edge:       O(1)
     * Check edge:     O(degree(u))
     * List neighbors: O(degree(u))  ← advantage
     *
     * Use when: sparse graph (E << V²) — most real-world graphs
     * Java: List<List<int[]>> or Map<Integer, List<int[]>>
     */
    static class Graph {
        final int V;
        final List<List<int[]>> adj; // adj[u] = list of [v, weight]
        final boolean directed;

        Graph(int V, boolean directed) {
            this.V = V;
            this.directed = directed;
            adj = new ArrayList<>();
            for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        }

        void addEdge(int u, int v) { addEdge(u, v, 1); }

        void addEdge(int u, int v, int weight) {
            adj.get(u).add(new int[]{v, weight});
            if (!directed) adj.get(v).add(new int[]{u, weight});
        }

        List<int[]> neighbors(int u) { return adj.get(u); }

        void print() {
            System.out.println("Adjacency List (" + (directed ? "directed" : "undirected") + "):");
            for (int u = 0; u < V; u++) {
                System.out.print("  " + u + " → ");
                for (int[] e : adj.get(u)) System.out.print("[" + e[0] + ",w=" + e[1] + "] ");
                System.out.println();
            }
        }
    }

    // =========================================================
    // TOPIC 59 — BFS (Breadth-First Search)
    // =========================================================

    /**
     * BFS — explores level by level using a Queue.
     *
     * Properties:
     *   - Finds SHORTEST path in UNWEIGHTED graph (fewest edges)
     *   - Time:  O(V + E)
     *   - Space: O(V) for visited + queue
     *
     * Template:
     *   queue.add(start); visited[start] = true
     *   while queue not empty:
     *     u = queue.poll()
     *     process(u)
     *     for each neighbor v of u:
     *       if not visited: visited[v]=true, queue.add(v)
     */
    static int[] bfsShortestPath(Graph g, int src) {
        int[] dist = new int[g.V];
        Arrays.fill(dist, -1);
        dist[src] = 0;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(src);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int[] edge : g.neighbors(u)) {
                int v = edge[0];
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    queue.offer(v);
                }
            }
        }
        return dist; // dist[v] = shortest hops from src to v, -1 if unreachable
    }

    /**
     * BFS path reconstruction — trace back via parent array
     */
    static List<Integer> bfsPath(Graph g, int src, int dst) {
        int[] parent = new int[g.V];
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[g.V];
        visited[src] = true;
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(src);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == dst) break;
            for (int[] edge : g.neighbors(u)) {
                int v = edge[0];
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    queue.offer(v);
                }
            }
        }

        if (!visited[dst]) return Collections.emptyList();
        List<Integer> path = new ArrayList<>();
        for (int v = dst; v != -1; v = parent[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }

    // ----- LeetCode BFS problems -----

    /**
     * LeetCode #200 — Number of Islands
     * BFS/DFS to count connected components of '1's.
     */
    static int numIslands(char[][] grid) {
        int rows = grid.length, cols = grid[0].length, count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    bfsSink(grid, r, c); // sink island
                    count++;
                }
            }
        }
        return count;
    }

    private static void bfsSink(char[][] grid, int r, int c) {
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{r, c});
        grid[r][c] = '0';
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            for (int[] d : dirs) {
                int nr = cur[0]+d[0], nc = cur[1]+d[1];
                if (nr>=0 && nr<rows && nc>=0 && nc<cols && grid[nr][nc]=='1') {
                    grid[nr][nc] = '0';
                    q.offer(new int[]{nr, nc});
                }
            }
        }
    }

    /**
     * LeetCode #994 — Rotting Oranges
     * Multi-source BFS: start from all rotten oranges simultaneously.
     * Time: O(rows * cols)
     */
    static int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> q = new LinkedList<>();
        int fresh = 0;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) q.offer(new int[]{r, c});
                else if (grid[r][c] == 1) fresh++;
            }

        if (fresh == 0) return 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        int minutes = 0;

        while (!q.isEmpty() && fresh > 0) {
            minutes++;
            int size = q.size();
            for (int i = 0; i < size; i++) {
                int[] cur = q.poll();
                for (int[] d : dirs) {
                    int nr = cur[0]+d[0], nc = cur[1]+d[1];
                    if (nr>=0 && nr<rows && nc>=0 && nc<cols && grid[nr][nc]==1) {
                        grid[nr][nc] = 2;
                        fresh--;
                        q.offer(new int[]{nr, nc});
                    }
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }

    /**
     * LeetCode #127 — Word Ladder
     * BFS on word graph: each step changes one letter.
     * Time: O(M² * N) where M=word length, N=wordList size
     */
    static int ladderLength(String begin, String end, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(end)) return 0;

        Queue<String> q = new LinkedList<>();
        q.offer(begin);
        Set<String> visited = new HashSet<>();
        visited.add(begin);
        int steps = 1;

        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                String word = q.poll();
                char[] chars = word.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char orig = chars[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == orig) continue;
                        chars[j] = c;
                        String next = new String(chars);
                        if (next.equals(end)) return steps + 1;
                        if (wordSet.contains(next) && !visited.contains(next)) {
                            visited.add(next);
                            q.offer(next);
                        }
                        chars[j] = orig;
                    }
                }
            }
            steps++;
        }
        return 0;
    }

    // =========================================================
    // TOPIC 60 — DFS (Depth-First Search)
    // =========================================================

    /**
     * DFS — explores as deep as possible before backtracking.
     *
     * Properties:
     *   - Finds connected components
     *   - Detects cycles
     *   - Generates topological order
     *   - Time:  O(V + E)
     *   - Space: O(V) for visited + call stack
     *
     * Two implementations: recursive (elegant) & iterative (explicit stack)
     */

    // Recursive DFS — visits all nodes reachable from u
    static void dfsRecursive(Graph g, int u, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        order.add(u);
        for (int[] edge : g.neighbors(u)) {
            int v = edge[0];
            if (!visited[v]) dfsRecursive(g, v, visited, order);
        }
    }

    // Iterative DFS — uses explicit stack
    static List<Integer> dfsIterative(Graph g, int src) {
        boolean[] visited = new boolean[g.V];
        List<Integer> order = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(src);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (visited[u]) continue;
            visited[u] = true;
            order.add(u);
            for (int[] edge : g.neighbors(u)) {
                if (!visited[edge[0]]) stack.push(edge[0]);
            }
        }
        return order;
    }

    // Count connected components
    static int countComponents(Graph g) {
        boolean[] visited = new boolean[g.V];
        int components = 0;
        for (int u = 0; u < g.V; u++) {
            if (!visited[u]) {
                dfsRecursive(g, u, visited, new ArrayList<>());
                components++;
            }
        }
        return components;
    }

    /**
     * LeetCode #133 — Clone Graph
     * DFS with HashMap to track cloned nodes.
     */
    static class Node {
        int val;
        List<Node> neighbors = new ArrayList<>();
        Node(int v) { val = v; }
    }

    static Node cloneGraph(Node node) {
        if (node == null) return null;
        Map<Node, Node> cloned = new HashMap<>();
        return dfsClone(node, cloned);
    }

    private static Node dfsClone(Node node, Map<Node, Node> cloned) {
        if (cloned.containsKey(node)) return cloned.get(node);
        Node copy = new Node(node.val);
        cloned.put(node, copy);
        for (Node nb : node.neighbors) copy.neighbors.add(dfsClone(nb, cloned));
        return copy;
    }

    /**
     * LeetCode #417 — Pacific Atlantic Water Flow
     * Reverse BFS/DFS: start from ocean borders, find cells that can reach both.
     */
    static List<List<Integer>> pacificAtlantic(int[][] heights) {
        int rows = heights.length, cols = heights[0].length;
        boolean[][] pacific  = new boolean[rows][cols];
        boolean[][] atlantic = new boolean[rows][cols];
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        // BFS from Pacific (top & left borders)
        Queue<int[]> pq = new LinkedList<>(), aq = new LinkedList<>();
        for (int r = 0; r < rows; r++) {
            pq.offer(new int[]{r, 0});       pacific[r][0]        = true;
            aq.offer(new int[]{r, cols-1});  atlantic[r][cols-1]  = true;
        }
        for (int c = 0; c < cols; c++) {
            pq.offer(new int[]{0, c});       pacific[0][c]        = true;
            aq.offer(new int[]{rows-1, c});  atlantic[rows-1][c]  = true;
        }

        bfsOcean(heights, pq, pacific, dirs);
        bfsOcean(heights, aq, atlantic, dirs);

        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (pacific[r][c] && atlantic[r][c])
                    result.add(Arrays.asList(r, c));
        return result;
    }

    private static void bfsOcean(int[][] h, Queue<int[]> q, boolean[][] visited, int[][] dirs) {
        int rows = h.length, cols = h[0].length;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            for (int[] d : dirs) {
                int nr = cur[0]+d[0], nc = cur[1]+d[1];
                if (nr>=0 && nr<rows && nc>=0 && nc<cols
                        && !visited[nr][nc] && h[nr][nc] >= h[cur[0]][cur[1]]) {
                    visited[nr][nc] = true;
                    q.offer(new int[]{nr, nc});
                }
            }
        }
    }

    // =========================================================
    // TOPIC 61 — TOPOLOGICAL SORT
    // =========================================================

    /**
     * Topological Sort — linear ordering of vertices such that
     * for every directed edge u→v, u comes before v.
     * Only possible on DAGs (Directed Acyclic Graphs).
     *
     * Two approaches:
     *   A) Kahn's Algorithm (BFS) — uses in-degree
     *   B) DFS post-order (push to stack after all neighbors)
     */

    /**
     * Kahn's Algorithm (BFS-based) — LeetCode #207, #210
     *
     * Steps:
     *   1. Compute in-degree for all vertices
     *   2. Add all 0-in-degree vertices to queue
     *   3. Poll u → add to result → decrement neighbors' in-degree
     *      → add neighbor to queue if in-degree becomes 0
     *   4. If result.size() < V → cycle exists (not a DAG)
     *
     * Time: O(V + E)
     */
    static int[] topoSortKahn(int V, int[][] edges) {
        int[] inDegree = new int[V];
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());

        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            inDegree[e[1]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < V; i++) if (inDegree[i] == 0) queue.offer(i);

        int[] order = new int[V];
        int idx = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order[idx++] = u;
            for (int v : adj.get(u)) {
                if (--inDegree[v] == 0) queue.offer(v);
            }
        }
        return idx == V ? order : new int[0]; // empty = cycle detected
    }

    /**
     * DFS-based Topological Sort
     * Post-order DFS: push to stack AFTER visiting all neighbors.
     * Reverse the stack → topological order.
     */
    static List<Integer> topoSortDFS(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);

        boolean[] visited = new boolean[V];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int u = 0; u < V; u++)
            if (!visited[u]) dfsTopoSort(adj, u, visited, stack);

        List<Integer> result = new ArrayList<>(stack);
        return result; // stack is already in reverse post-order
    }

    private static void dfsTopoSort(List<List<Integer>> adj, int u,
                                     boolean[] visited, Deque<Integer> stack) {
        visited[u] = true;
        for (int v : adj.get(u)) if (!visited[v]) dfsTopoSort(adj, v, visited, stack);
        stack.push(u); // push AFTER all descendants processed
    }

    /**
     * LeetCode #207 — Course Schedule (can finish all courses?)
     * Detect if DAG: Kahn's → check if all nodes processed.
     */
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        int[] order = topoSortKahn(numCourses, prerequisites);
        return order.length == numCourses;
    }

    /**
     * LeetCode #210 — Course Schedule II (return order)
     */
    static int[] findOrder(int numCourses, int[][] prerequisites) {
        return topoSortKahn(numCourses, prerequisites);
    }

    // =========================================================
    // TOPIC 62 — CYCLE DETECTION
    // =========================================================

    /**
     * CYCLE DETECTION — UNDIRECTED GRAPH (DFS with parent tracking)
     *
     * Key: in undirected DFS, a back edge to a node other than
     * the immediate parent indicates a cycle.
     */
    static boolean hasCycleUndirected(Graph g) {
        boolean[] visited = new boolean[g.V];
        for (int u = 0; u < g.V; u++)
            if (!visited[u] && dfsCycleUndirected(g, u, -1, visited)) return true;
        return false;
    }

    private static boolean dfsCycleUndirected(Graph g, int u, int parent, boolean[] visited) {
        visited[u] = true;
        for (int[] edge : g.neighbors(u)) {
            int v = edge[0];
            if (!visited[v]) {
                if (dfsCycleUndirected(g, v, u, visited)) return true;
            } else if (v != parent) {
                return true; // back edge to non-parent → cycle!
            }
        }
        return false;
    }

    /**
     * CYCLE DETECTION — DIRECTED GRAPH (DFS with 3-color marking)
     *
     * Colors:
     *   0 = WHITE: not visited
     *   1 = GRAY:  in current DFS path (on stack)
     *   2 = BLACK: fully processed
     *
     * Cycle exists if we reach a GRAY node (back edge to ancestor).
     * Back edge to BLACK node = safe (already fully explored).
     */
    static boolean hasCycleDirected(int V, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);

        int[] color = new int[V]; // 0=white, 1=gray, 2=black
        for (int u = 0; u < V; u++)
            if (color[u] == 0 && dfsCycleDirected(adj, u, color)) return true;
        return false;
    }

    private static boolean dfsCycleDirected(List<List<Integer>> adj, int u, int[] color) {
        color[u] = 1; // mark GRAY (on stack)
        for (int v : adj.get(u)) {
            if (color[v] == 1) return true;  // back edge → cycle!
            if (color[v] == 0 && dfsCycleDirected(adj, v, color)) return true;
        }
        color[u] = 2; // mark BLACK (done)
        return false;
    }

    /**
     * LeetCode #684 — Redundant Connection
     * Find the edge that creates a cycle in an undirected graph.
     * Use Union-Find: the edge whose two endpoints are already
     * in the same component is the redundant edge.
     * (Union-Find covered in detail in 4.4)
     */
    static int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        for (int i = 0; i <= n; i++) parent[i] = i;

        for (int[] e : edges) {
            int pu = find(parent, e[0]), pv = find(parent, e[1]);
            if (pu == pv) return e;   // same component → this edge is redundant
            parent[pu] = pv;          // union
        }
        return new int[0];
    }

    private static int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]); // path compression
        return parent[x];
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 56-58: Graph Representations ===");

        AdjacencyMatrix am = new AdjacencyMatrix(4);
        am.addEdge(0, 1); am.addEdge(1, 2); am.addEdge(2, 3); am.addEdge(3, 0);
        am.print();
        System.out.println("hasEdge(0,1): " + am.hasEdge(0, 1));
        System.out.println("hasEdge(0,2): " + am.hasEdge(0, 2));

        Graph g = new Graph(5, false);
        g.addEdge(0, 1); g.addEdge(0, 2); g.addEdge(1, 3); g.addEdge(2, 4);
        g.print();

        Graph wg = new Graph(4, true);
        wg.addEdge(0, 1, 5); wg.addEdge(0, 2, 3); wg.addEdge(1, 3, 2); wg.addEdge(2, 3, 7);
        System.out.println("\nWeighted directed graph:");
        wg.print();

        System.out.println("=== TOPIC 59: BFS ===");
        int[] dist = bfsShortestPath(g, 0);
        System.out.println("BFS distances from 0: " + Arrays.toString(dist));
        System.out.println("BFS path 0→4: " + bfsPath(g, 0, 4));

        char[][] grid = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("#200 numIslands: " + numIslands(grid)); // 3

        int[][] oranges = {{2,1,1},{1,1,0},{0,1,1}};
        System.out.println("#994 orangesRotting: " + orangesRotting(oranges)); // 4

        System.out.println("#127 wordLadder hit→cog: "
                + ladderLength("hit", "cog",
                    Arrays.asList("hot","dot","dog","lot","log","cog"))); // 5

        System.out.println("\n=== TOPIC 60: DFS ===");
        Graph g2 = new Graph(6, false);
        g2.addEdge(0,1); g2.addEdge(0,2); g2.addEdge(1,3);
        g2.addEdge(4,5); // separate component
        System.out.println("DFS iterative from 0: " + dfsIterative(g2, 0));
        System.out.println("Connected components:  " + countComponents(g2)); // 2

        System.out.println("\n=== TOPIC 61: Topological Sort ===");
        // 5→2, 5→0, 4→0, 4→1, 2→3, 3→1
        int[][] deps = {{5,2},{5,0},{4,0},{4,1},{2,3},{3,1}};
        System.out.println("Kahn's topoSort:  " + Arrays.toString(topoSortKahn(6, deps)));
        System.out.println("DFS topoSort:     " + topoSortDFS(6, deps));

        int[][] courses = {{1,0},{2,0},{3,1},{3,2}};
        System.out.println("#207 canFinish(4):  " + canFinish(4, courses));       // true
        System.out.println("#210 findOrder(4):  " + Arrays.toString(findOrder(4, courses)));

        System.out.println("\n=== TOPIC 62: Cycle Detection ===");
        Graph cyclic = new Graph(3, false);
        cyclic.addEdge(0,1); cyclic.addEdge(1,2); cyclic.addEdge(2,0);
        Graph acyclic = new Graph(3, false);
        acyclic.addEdge(0,1); acyclic.addEdge(1,2);
        System.out.println("Cycle undirected (0-1-2-0): " + hasCycleUndirected(cyclic)); // true
        System.out.println("Cycle undirected (0-1-2):   " + hasCycleUndirected(acyclic)); // false

        // Directed: 0→1→2→0 (cycle)
        int[][] dirEdges  = {{0,1},{1,2},{2,0}};
        int[][] dirAcyclic = {{0,1},{1,2}};
        System.out.println("Cycle directed (0→1→2→0): " + hasCycleDirected(3, dirEdges));  // true
        System.out.println("Cycle directed (0→1→2):   " + hasCycleDirected(3, dirAcyclic)); // false

        int[][] redund = {{1,2},{1,3},{2,3}};
        System.out.println("#684 redundantConnection: " + Arrays.toString(findRedundantConnection(redund))); // [2,3]
    }
}
