package phase4_graphs;

import java.util.*;

/**
 * PHASE 4.4 — ADVANCED GRAPH ALGORITHMS
 * ========================================
 * Topics covered:
 *  67. Union-Find / DSU (Disjoint Set Union)
 *  68. Minimum Spanning Tree — Kruskal's algorithm
 *  69. Minimum Spanning Tree — Prim's algorithm
 *  70. Strongly Connected Components — Kosaraju & Tarjan
 *  71. Bridges & Articulation Points
 *  72. Bipartite Check
 */
public class AdvancedGraph {

    // =========================================================
    // TOPIC 67 — UNION-FIND / DSU
    // =========================================================

    /**
     * Union-Find (Disjoint Set Union) — tracks connected components.
     *
     * Two key operations:
     *   find(x)  — which component does x belong to? (representative/root)
     *   union(x,y) — merge the components of x and y
     *
     * Optimizations:
     *   Path compression: make every node point directly to root → O(α(n)) find
     *   Union by rank:    always attach smaller tree under larger → O(log n) without compression
     *   Combined:         nearly O(1) amortized — effectively O(α(n)) ≈ O(1) for all practical n
     *
     * α(n) = inverse Ackermann function, grows so slowly it's ≤ 4 for n < 10^600.
     */
    static class UnionFind {
        private final int[] parent;
        private final int[] rank;   // tree height upper bound
        private int components;

        UnionFind(int n) {
            parent = new int[n];
            rank   = new int[n];
            components = n;
            for (int i = 0; i < n; i++) parent[i] = i; // each node is its own root
        }

        /** Find root with PATH COMPRESSION — O(α(n)) */
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]); // compress path to root
            return parent[x];
        }

        /** Union by RANK — attach smaller tree under larger root */
        boolean union(int x, int y) {
            int px = find(x), py = find(y);
            if (px == py) return false; // already connected

            if      (rank[px] < rank[py]) parent[px] = py;
            else if (rank[px] > rank[py]) parent[py] = px;
            else { parent[py] = px; rank[px]++; }

            components--;
            return true; // merged
        }

        boolean connected(int x, int y) { return find(x) == find(y); }
        int components() { return components; }
    }

    // ----- LeetCode problems using Union-Find -----

    /**
     * LeetCode #200 — Number of Islands (Union-Find approach)
     * Convert 2D grid to 1D index: r*cols + c
     */
    static int numIslands(char[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        UnionFind uf = new UnionFind(rows * cols);
        int water = 0;

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '0') { water++; continue; }
                if (r+1 < rows && grid[r+1][c] == '1') uf.union(r*cols+c, (r+1)*cols+c);
                if (c+1 < cols && grid[r][c+1] == '1') uf.union(r*cols+c, r*cols+c+1);
            }
        return uf.components() - water;
    }

    /**
     * LeetCode #547 — Number of Provinces
     * Direct Union-Find on adjacency matrix.
     */
    static int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                if (isConnected[i][j] == 1) uf.union(i, j);
        return uf.components();
    }

    /**
     * LeetCode #684 — Redundant Connection
     * First edge whose endpoints are already connected → forms cycle.
     */
    static int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        UnionFind uf = new UnionFind(n + 1);
        for (int[] e : edges)
            if (!uf.union(e[0], e[1])) return e; // already connected
        return new int[0];
    }

    /**
     * LeetCode #1202 — Smallest String With Swaps
     * Nodes in same component can be freely rearranged.
     * Union all swap pairs → sort each component's chars.
     */
    static String smallestStringWithSwaps(String s, List<List<Integer>> pairs) {
        int n = s.length();
        UnionFind uf = new UnionFind(n);
        for (List<Integer> p : pairs) uf.union(p.get(0), p.get(1));

        // Group indices by component root
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < n; i++)
            groups.computeIfAbsent(uf.find(i), k -> new ArrayList<>()).add(i);

        char[] result = s.toCharArray();
        for (List<Integer> indices : groups.values()) {
            // Collect chars, sort, put back in sorted index order
            char[] chars = new char[indices.size()];
            for (int i = 0; i < indices.size(); i++) chars[i] = s.charAt(indices.get(i));
            Arrays.sort(chars);
            Collections.sort(indices);
            for (int i = 0; i < indices.size(); i++) result[indices.get(i)] = chars[i];
        }
        return new String(result);
    }

    // =========================================================
    // TOPIC 68 — MST: KRUSKAL'S ALGORITHM
    // =========================================================

    /**
     * Kruskal's MST — greedy, edge-based.
     *
     * Idea:
     *   1. Sort all edges by weight (ascending)
     *   2. Process each edge: if endpoints in different components → add to MST
     *      (Union-Find detects if same component → would form cycle → skip)
     *   3. Stop when MST has V-1 edges
     *
     * Time: O(E log E) — dominated by sorting
     * Space: O(V) for Union-Find
     *
     * Best when: edges given explicitly, sparse graph.
     * Cut property: the minimum weight edge crossing any cut belongs to some MST.
     */
    static int[][] kruskalMST(int V, int[][] edges) {
        // Sort edges by weight
        int[][] sorted = edges.clone();
        Arrays.sort(sorted, Comparator.comparingInt(e -> e[2])); // e = [u, v, weight]

        UnionFind uf = new UnionFind(V);
        List<int[]> mst = new ArrayList<>();
        int totalWeight = 0;

        for (int[] e : sorted) {
            if (mst.size() == V - 1) break; // MST complete
            if (uf.union(e[0], e[1])) {     // different components → safe to add
                mst.add(e);
                totalWeight += e[2];
            }
        }

        System.out.println("Kruskal MST total weight: " + totalWeight);
        return mst.toArray(new int[0][]);
    }

    /**
     * LeetCode #1584 — Min Cost to Connect All Points
     * Points: (x,y). Cost = Manhattan distance.
     * Generate all edges → Kruskal.
     * Time: O(n² log n)
     */
    static int minCostConnectPoints(int[][] points) {
        int n = points.length;
        // Generate all edges
        int[][] edges = new int[n*(n-1)/2][3];
        int idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                edges[idx++] = new int[]{i, j,
                    Math.abs(points[i][0]-points[j][0]) + Math.abs(points[i][1]-points[j][1])};

        Arrays.sort(edges, Comparator.comparingInt(e -> e[2]));
        UnionFind uf = new UnionFind(n);
        int cost = 0, edgesUsed = 0;
        for (int[] e : edges) {
            if (uf.union(e[0], e[1])) {
                cost += e[2];
                if (++edgesUsed == n-1) break;
            }
        }
        return cost;
    }

    // =========================================================
    // TOPIC 69 — MST: PRIM'S ALGORITHM
    // =========================================================

    /**
     * Prim's MST — greedy, vertex-based.
     *
     * Idea:
     *   Start from any vertex. Grow MST one vertex at a time.
     *   Always pick the minimum weight edge that connects
     *   a vertex IN the MST to a vertex NOT YET in the MST.
     *   Use a min-heap to efficiently find the minimum crossing edge.
     *
     * Time: O((V + E) log V) with binary heap
     * Space: O(V + E)
     *
     * Best when: dense graph (E ≈ V²), or adjacency list given.
     * Both Kruskal and Prim produce a valid MST — they may differ in which edges chosen.
     */
    static int primMST(int V, List<List<int[]>> adj) {
        boolean[] inMST = new boolean[V];
        int[] key = new int[V]; // min weight to connect vertex to MST
        Arrays.fill(key, Integer.MAX_VALUE);
        key[0] = 0;

        // Min-heap: [weight, vertex]
        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{0, 0});
        int totalWeight = 0;

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int w = cur[0], u = cur[1];
            if (inMST[u]) continue;
            inMST[u] = true;
            totalWeight += w;

            for (int[] edge : adj.get(u)) {
                int v = edge[0], wt = edge[1];
                if (!inMST[v] && wt < key[v]) {
                    key[v] = wt;
                    heap.offer(new int[]{wt, v});
                }
            }
        }
        System.out.println("Prim's MST total weight: " + totalWeight);
        return totalWeight;
    }

    /** LeetCode #1584 — Min Cost Connect Points using Prim's */
    static int minCostConnectPointsPrim(int[][] points) {
        int n = points.length;
        boolean[] inMST = new boolean[n];
        int[] minDist = new int[n];
        Arrays.fill(minDist, Integer.MAX_VALUE);
        minDist[0] = 0;

        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{0, 0});
        int cost = 0;

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int d = cur[0], u = cur[1];
            if (inMST[u]) continue;
            inMST[u] = true;
            cost += d;

            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    int dist = Math.abs(points[u][0]-points[v][0])
                             + Math.abs(points[u][1]-points[v][1]);
                    if (dist < minDist[v]) {
                        minDist[v] = dist;
                        heap.offer(new int[]{dist, v});
                    }
                }
            }
        }
        return cost;
    }

    // =========================================================
    // TOPIC 70 — SCC: KOSARAJU'S ALGORITHM
    // =========================================================

    /**
     * Strongly Connected Components (SCC):
     * A maximal set of vertices where every vertex is reachable from every other.
     *
     * KOSARAJU'S ALGORITHM — Two-pass DFS:
     *   Pass 1: DFS on original graph, push to stack in finish order
     *   Pass 2: DFS on TRANSPOSED graph in reverse finish order
     *           Each DFS tree in pass 2 = one SCC
     *
     * Intuition: if u can reach v in G, then v can reach u in G^T.
     *            The finish order ensures we start each SCC from its "leader".
     *
     * Time: O(V + E)   Space: O(V + E)
     */
    static List<List<Integer>> kosarajuSCC(int V, int[][] edges) {
        List<List<Integer>> adj  = buildAdjList(V, edges, false); // original
        List<List<Integer>> radj = buildAdjList(V, edges, true);  // transposed

        // Pass 1: DFS on original, collect finish order
        boolean[] visited = new boolean[V];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int u = 0; u < V; u++)
            if (!visited[u]) dfsFinish(adj, u, visited, stack);

        // Pass 2: DFS on transposed in reverse finish order
        Arrays.fill(visited, false);
        List<List<Integer>> sccs = new ArrayList<>();
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                List<Integer> scc = new ArrayList<>();
                dfsCollect(radj, u, visited, scc);
                sccs.add(scc);
            }
        }
        return sccs;
    }

    private static void dfsFinish(List<List<Integer>> adj, int u,
                                   boolean[] visited, Deque<Integer> stack) {
        visited[u] = true;
        for (int v : adj.get(u)) if (!visited[v]) dfsFinish(adj, v, visited, stack);
        stack.push(u); // push AFTER all descendants — finish order
    }

    private static void dfsCollect(List<List<Integer>> adj, int u,
                                    boolean[] visited, List<Integer> scc) {
        visited[u] = true;
        scc.add(u);
        for (int v : adj.get(u)) if (!visited[v]) dfsCollect(adj, v, visited, scc);
    }

    // =========================================================
    // TOPIC 71 — BRIDGES & ARTICULATION POINTS (TARJAN'S)
    // =========================================================

    /**
     * Bridge: an edge whose removal disconnects the graph.
     * Articulation Point: a vertex whose removal disconnects the graph.
     *
     * TARJAN'S ALGORITHM — single DFS pass:
     *   disc[u]  = discovery time of u
     *   low[u]   = minimum discovery time reachable from subtree of u
     *              (via back edges — edges to ancestors)
     *
     * Bridge condition:    low[v] > disc[u]
     *   (v's subtree cannot reach u or above → removing edge u-v disconnects)
     *
     * Articulation point: low[v] >= disc[u] for some child v (non-root)
     *                     OR u is root with ≥ 2 children in DFS tree
     *
     * Time: O(V + E)
     */
    static int timer = 0;

    static List<int[]> findBridges(int V, List<List<Integer>> adj) {
        int[] disc = new int[V], low = new int[V];
        Arrays.fill(disc, -1);
        List<int[]> bridges = new ArrayList<>();
        timer = 0;

        for (int u = 0; u < V; u++)
            if (disc[u] == -1) dfsBridge(adj, u, -1, disc, low, bridges);
        return bridges;
    }

    private static void dfsBridge(List<List<Integer>> adj, int u, int parent,
                                   int[] disc, int[] low, List<int[]> bridges) {
        disc[u] = low[u] = timer++;
        for (int v : adj.get(u)) {
            if (disc[v] == -1) {               // tree edge
                dfsBridge(adj, v, u, disc, low, bridges);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > disc[u]) bridges.add(new int[]{u, v}); // bridge!
            } else if (v != parent) {          // back edge (not to direct parent)
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    static Set<Integer> findArticulationPoints(int V, List<List<Integer>> adj) {
        int[] disc = new int[V], low = new int[V], childCount = new int[V];
        boolean[] isAP = new boolean[V];
        Arrays.fill(disc, -1);
        timer = 0;

        for (int u = 0; u < V; u++)
            if (disc[u] == -1) dfsAP(adj, u, -1, disc, low, childCount, isAP);

        Set<Integer> aps = new HashSet<>();
        for (int u = 0; u < V; u++) if (isAP[u]) aps.add(u);
        return aps;
    }

    private static void dfsAP(List<List<Integer>> adj, int u, int parent,
                                int[] disc, int[] low, int[] childCount, boolean[] isAP) {
        disc[u] = low[u] = timer++;
        for (int v : adj.get(u)) {
            if (disc[v] == -1) {
                childCount[u]++;
                dfsAP(adj, v, u, disc, low, childCount, isAP);
                low[u] = Math.min(low[u], low[v]);

                // u is AP if: non-root and low[v] >= disc[u]
                if (parent != -1 && low[v] >= disc[u]) isAP[u] = true;
                // root is AP if it has ≥ 2 DFS children
                if (parent == -1 && childCount[u] > 1) isAP[u] = true;
            } else if (v != parent) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    /**
     * LeetCode #1192 — Critical Connections in a Network
     * = Find all bridges in the graph.
     */
    static List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (List<Integer> c : connections) {
            adj.get(c.get(0)).add(c.get(1));
            adj.get(c.get(1)).add(c.get(0));
        }

        int[] disc = new int[n], low = new int[n];
        Arrays.fill(disc, -1);
        List<List<Integer>> result = new ArrayList<>();
        timer = 0;

        for (int u = 0; u < n; u++)
            if (disc[u] == -1) dfsCC(adj, u, -1, disc, low, result);
        return result;
    }

    private static void dfsCC(List<List<Integer>> adj, int u, int parent,
                                int[] disc, int[] low, List<List<Integer>> result) {
        disc[u] = low[u] = timer++;
        for (int v : adj.get(u)) {
            if (disc[v] == -1) {
                dfsCC(adj, v, u, disc, low, result);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > disc[u]) result.add(Arrays.asList(u, v));
            } else if (v != parent) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // =========================================================
    // TOPIC 72 — BIPARTITE CHECK
    // =========================================================

    /**
     * Bipartite graph: vertices can be colored with 2 colors such that
     * no two adjacent vertices share the same color.
     * Equivalently: graph has NO ODD-LENGTH CYCLES.
     *
     * Check: BFS/DFS graph coloring.
     *   Assign color 0 to src, then alternate colors for neighbors.
     *   If a neighbor already has the same color as current → NOT bipartite.
     *
     * Time: O(V + E)
     *
     * Applications: matching problems, scheduling, 2-coloring problems.
     */
    static boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n];
        Arrays.fill(color, -1);

        for (int start = 0; start < n; start++) {
            if (color[start] != -1) continue;
            // BFS coloring
            Queue<Integer> q = new LinkedList<>();
            q.offer(start);
            color[start] = 0;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : graph[u]) {
                    if (color[v] == -1) {
                        color[v] = 1 - color[u]; // alternate color
                        q.offer(v);
                    } else if (color[v] == color[u]) {
                        return false; // same color adjacent → not bipartite
                    }
                }
            }
        }
        return true;
    }

    /**
     * LeetCode #785 — Is Graph Bipartite?
     * Same as above — direct application.
     */

    /**
     * LeetCode #886 — Possible Bipartition
     * Given n people and dislikes pairs → can we split into 2 groups
     * where no two people who dislike each other are in the same group?
     * = Is the dislike graph bipartite?
     */
    static boolean possibleBipartition(int n, int[][] dislikes) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] d : dislikes) {
            adj.get(d[0]).add(d[1]);
            adj.get(d[1]).add(d[0]);
        }

        int[] color = new int[n + 1];
        Arrays.fill(color, -1);
        for (int i = 1; i <= n; i++) {
            if (color[i] != -1) continue;
            Queue<Integer> q = new LinkedList<>();
            q.offer(i); color[i] = 0;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v : adj.get(u)) {
                    if (color[v] == -1) { color[v] = 1-color[u]; q.offer(v); }
                    else if (color[v] == color[u]) return false;
                }
            }
        }
        return true;
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private static List<List<Integer>> buildAdjList(int V, int[][] edges, boolean transpose) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            if (!transpose) adj.get(e[0]).add(e[1]);
            else            adj.get(e[1]).add(e[0]); // reverse direction
        }
        return adj;
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 67: Union-Find ===");
        UnionFind uf = new UnionFind(6);
        uf.union(0,1); uf.union(1,2); uf.union(3,4);
        System.out.println("Components: " + uf.components()); // 3
        System.out.println("0 connected 2: " + uf.connected(0,2)); // true
        System.out.println("0 connected 3: " + uf.connected(0,3)); // false
        uf.union(2,3);
        System.out.println("After union(2,3), components: " + uf.components()); // 2

        char[][] grid = {
            {'1','1','0'},{'0','1','0'},{'0','0','1'}
        };
        System.out.println("#200 numIslands (UF): " + numIslands(grid)); // 2

        int[][] connected = {{1,1,0},{1,1,0},{0,0,1}};
        System.out.println("#547 provinces: " + findCircleNum(connected)); // 2

        System.out.println("#684 redundant: "
                + Arrays.toString(findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}}))); // [2,3]

        System.out.println("\n=== TOPIC 68: Kruskal's MST ===");
        //     1
        //  0-----1
        //  |  \  |
        // 4|  3\ |2
        //  |    \|
        //  3-----2
        //     5
        int[][] edges = {{0,1,1},{0,2,3},{0,3,4},{1,2,2},{2,3,5}};
        int[][] mst = kruskalMST(4, edges); // weight = 1+2+4 = 7
        System.out.println("MST edges: " + Arrays.deepToString(mst));

        int[][] pts = {{0,0},{2,2},{3,10},{5,2},{7,0}};
        System.out.println("#1584 minCostConnectPoints (Kruskal): "
                + minCostConnectPoints(pts)); // 20

        System.out.println("\n=== TOPIC 69: Prim's MST ===");
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        primMST(4, adj); // weight = 7

        System.out.println("#1584 minCostConnectPoints (Prim): "
                + minCostConnectPointsPrim(pts)); // 20

        System.out.println("\n=== TOPIC 70: SCC (Kosaraju) ===");
        // 0→1→2→0 (SCC1),  3→4 (SCC2),  2→3
        int[][] dirEdges = {{0,1},{1,2},{2,0},{2,3},{3,4}};
        List<List<Integer>> sccs = kosarajuSCC(5, dirEdges);
        System.out.println("SCCs: " + sccs); // [[0,1,2], [3], [4]] or similar

        System.out.println("\n=== TOPIC 71: Bridges & Articulation Points ===");
        // Graph: 0-1-2-3, with 1-4 extra edge
        //   0 - 1 - 2 - 3
        //       |
        //       4
        List<List<Integer>> bridgeAdj = new ArrayList<>();
        for (int i = 0; i < 5; i++) bridgeAdj.add(new ArrayList<>());
        int[][] undir = {{0,1},{1,2},{2,3},{1,4}};
        for (int[] e : undir) {
            bridgeAdj.get(e[0]).add(e[1]);
            bridgeAdj.get(e[1]).add(e[0]);
        }
        List<int[]> bridges = findBridges(5, bridgeAdj);
        System.out.print("Bridges: ");
        for (int[] b : bridges) System.out.print(Arrays.toString(b) + " ");
        System.out.println(); // [0,1] [1,2] [2,3] [1,4]

        Set<Integer> aps = findArticulationPoints(5, bridgeAdj);
        System.out.println("Articulation Points: " + aps); // {1, 2}

        // #1192 Critical Connections
        List<List<Integer>> connections = Arrays.asList(
            Arrays.asList(0,1), Arrays.asList(1,2),
            Arrays.asList(2,0), Arrays.asList(1,3)
        );
        System.out.println("#1192 criticalConnections: "
                + criticalConnections(4, connections)); // [[1,3]]

        System.out.println("\n=== TOPIC 72: Bipartite Check ===");
        int[][] biGraph  = {{1,3},{0,2},{1,3},{0,2}};   // square → bipartite
        int[][] oddCycle = {{1,2,3},{0,2},{0,1},{0}};    // triangle+extra → not bipartite
        System.out.println("#785 bipartite square:    " + isBipartite(biGraph));   // true
        System.out.println("#785 bipartite oddCycle:  " + isBipartite(oddCycle));  // false

        int[][] dislikes = {{1,2},{1,3},{2,4}};
        System.out.println("#886 possibleBipartition: "
                + possibleBipartition(4, dislikes)); // true
    }
}
