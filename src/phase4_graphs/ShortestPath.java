package phase4_graphs;

import java.util.*;

/**
 * PHASE 4.3 — SHORTEST PATH ALGORITHMS
 * =======================================
 * Topics covered:
 *  63. Dijkstra's algorithm     — non-negative weights, O((V+E) log V)
 *  64. Bellman-Ford             — negative weights, O(V*E)
 *  65. Floyd-Warshall           — all pairs, O(V³)
 *  66. A* search                — heuristic-guided, O(E log V)
 *
 * COMPARISON TABLE:
 * ┌──────────────────┬────────────┬──────────────┬───────────────────────────┐
 * │ Algorithm        │ Time       │ Neg weights? │ Use case                  │
 * ├──────────────────┼────────────┼──────────────┼───────────────────────────┤
 * │ Dijkstra         │ O(E log V) │ No           │ Single-source, sparse     │
 * │ Bellman-Ford     │ O(V*E)     │ Yes          │ Negative weights/cycles   │
 * │ Floyd-Warshall   │ O(V³)      │ Yes          │ All pairs, small V        │
 * │ A*               │ O(E log V) │ No           │ Grid/map with heuristic   │
 * └──────────────────┴────────────┴──────────────┴───────────────────────────┘
 */
public class ShortestPath {

    static final int INF = Integer.MAX_VALUE / 2;

    // =========================================================
    // TOPIC 63 — DIJKSTRA'S ALGORITHM
    // =========================================================

    /**
     * Dijkstra — single-source shortest path, non-negative weights.
     *
     * Idea (greedy):
     *   Use a min-heap (priority queue) of (dist, node).
     *   Always process the unvisited node with smallest known distance.
     *   Relax all its edges: if dist[u] + w < dist[v] → update dist[v].
     *
     * Key invariant: when a node is popped from heap, its distance is final.
     * This invariant BREAKS with negative edges → use Bellman-Ford instead.
     *
     * Time: O((V + E) log V) with binary heap
     * Space: O(V + E)
     */
    static int[] dijkstra(int V, List<List<int[]>> adj, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // Min-heap: [distance, node]
        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{0, src});

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int d = cur[0], u = cur[1];

            if (d > dist[u]) continue; // stale entry — skip

            for (int[] edge : adj.get(u)) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    heap.offer(new int[]{dist[v], v});
                }
            }
        }
        return dist;
    }

    /**
     * Dijkstra with path reconstruction.
     * Returns [dist[], parent[]] so you can trace any shortest path.
     */
    static int[][] dijkstraWithPath(int V, List<List<int[]>> adj, int src) {
        int[] dist   = new int[V];
        int[] parent = new int[V];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{0, src});

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int d = cur[0], u = cur[1];
            if (d > dist[u]) continue;
            for (int[] edge : adj.get(u)) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v]   = dist[u] + w;
                    parent[v] = u;
                    heap.offer(new int[]{dist[v], v});
                }
            }
        }
        return new int[][]{dist, parent};
    }

    static List<Integer> reconstructPath(int[] parent, int dst) {
        List<Integer> path = new ArrayList<>();
        for (int v = dst; v != -1; v = parent[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }

    // ----- LeetCode problems using Dijkstra -----

    /**
     * LeetCode #743 — Network Delay Time
     * Find time for signal to reach all nodes from src k.
     * = Dijkstra from k, return max(dist[]), -1 if some unreachable.
     */
    static int networkDelayTime(int[][] times, int n, int k) {
        List<List<int[]>> adj = buildAdj(n + 1, times, true);
        int[] dist = dijkstra(n + 1, adj, k);

        int maxDelay = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == INF) return -1;
            maxDelay = Math.max(maxDelay, dist[i]);
        }
        return maxDelay;
    }

    /**
     * LeetCode #1631 — Path With Minimum Effort
     * Grid: effort = max absolute difference of heights along path.
     * Modified Dijkstra: dist[r][c] = min effort to reach (r,c).
     * "Edge weight" = |height difference| at each step.
     */
    static int minimumEffortPath(int[][] heights) {
        int rows = heights.length, cols = heights[0].length;
        int[][] effort = new int[rows][cols];
        for (int[] row : effort) Arrays.fill(row, INF);
        effort[0][0] = 0;

        // heap: [effort, row, col]
        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{0, 0, 0});
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int e = cur[0], r = cur[1], c = cur[2];
            if (r == rows-1 && c == cols-1) return e;
            if (e > effort[r][c]) continue;
            for (int[] d : dirs) {
                int nr = r+d[0], nc = c+d[1];
                if (nr<0||nr>=rows||nc<0||nc>=cols) continue;
                int newEffort = Math.max(e, Math.abs(heights[nr][nc] - heights[r][c]));
                if (newEffort < effort[nr][nc]) {
                    effort[nr][nc] = newEffort;
                    heap.offer(new int[]{newEffort, nr, nc});
                }
            }
        }
        return effort[rows-1][cols-1];
    }

    /**
     * LeetCode #778 — Swim in Rising Water
     * Same pattern: Dijkstra where "cost" = max elevation along path.
     */
    static int swimInWater(int[][] grid) {
        int n = grid.length;
        int[][] time = new int[n][n];
        for (int[] row : time) Arrays.fill(row, INF);
        time[0][0] = grid[0][0];

        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{grid[0][0], 0, 0});
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int t = cur[0], r = cur[1], c = cur[2];
            if (r == n-1 && c == n-1) return t;
            if (t > time[r][c]) continue;
            for (int[] d : dirs) {
                int nr = r+d[0], nc = c+d[1];
                if (nr<0||nr>=n||nc<0||nc>=n) continue;
                int newTime = Math.max(t, grid[nr][nc]);
                if (newTime < time[nr][nc]) {
                    time[nr][nc] = newTime;
                    heap.offer(new int[]{newTime, nr, nc});
                }
            }
        }
        return time[n-1][n-1];
    }

    // =========================================================
    // TOPIC 64 — BELLMAN-FORD
    // =========================================================

    /**
     * Bellman-Ford — single-source, handles NEGATIVE weights.
     *
     * Idea:
     *   Relax ALL edges V-1 times.
     *   After k iterations: dist[v] = shortest path using at most k edges.
     *   After V-1 iterations: all shortest paths found (longest path in DAG = V-1 edges).
     *
     * Negative cycle detection:
     *   Run one more (V-th) relaxation.
     *   If any dist[v] still decreases → negative cycle exists.
     *
     * Time: O(V * E)   Space: O(V)
     * Slower than Dijkstra but handles negative weights.
     */
    static int[] bellmanFord(int V, int[][] edges, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // Relax all edges V-1 times
        for (int i = 0; i < V - 1; i++) {
            boolean updated = false;
            for (int[] e : edges) { // e = [u, v, weight]
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != INF && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    updated = true;
                }
            }
            if (!updated) break; // early exit: no change → converged
        }
        return dist;
    }

    /** Returns true if graph has a negative weight cycle reachable from src */
    static boolean hasNegativeCycle(int V, int[][] edges, int src) {
        int[] dist = bellmanFord(V, edges, src);
        // V-th relaxation: any update → negative cycle
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (dist[u] != INF && dist[u] + w < dist[v]) return true;
        }
        return false;
    }

    /**
     * LeetCode #787 — Cheapest Flights Within K Stops
     * Bellman-Ford variant: limit to at most k+1 edges (k stops).
     * Key: use a COPY of dist at each iteration to prevent using
     *      updated distances within the same round (prevents >1 edge per round).
     *
     * Time: O(K * E)
     */
    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int i = 0; i <= k; i++) {        // k stops = k+1 edges
            int[] tmp = dist.clone();          // snapshot: no chaining within same round
            for (int[] f : flights) {          // f = [from, to, price]
                int u = f[0], v = f[1], w = f[2];
                if (dist[u] != INF && dist[u] + w < tmp[v]) {
                    tmp[v] = dist[u] + w;
                }
            }
            dist = tmp;
        }
        return dist[dst] == INF ? -1 : dist[dst];
    }

    // =========================================================
    // TOPIC 65 — FLOYD-WARSHALL
    // =========================================================

    /**
     * Floyd-Warshall — ALL PAIRS shortest path.
     *
     * Idea (DP):
     *   dist[i][j][k] = shortest path from i to j using only vertices {0..k} as intermediates.
     *   Recurrence: dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])
     *   Try each vertex k as an intermediate: update all (i,j) pairs.
     *
     * Time: O(V³)   Space: O(V²)
     * Handles negative weights (NOT negative cycles).
     *
     * After running: dist[i][i] < 0 → negative cycle involving i.
     */
    static int[][] floydWarshall(int V, int[][] adjMatrix) {
        int[][] dist = new int[V][V];

        // Initialize
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                dist[i][j] = (i == j) ? 0 : adjMatrix[i][j];
            }
        }

        // Try each vertex k as intermediate
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }
        return dist; // dist[i][j] = shortest path from i to j
    }

    /**
     * LeetCode #1334 — Find the City With the Smallest Number of Neighbors
     * Within a threshold distance.
     * Floyd-Warshall → for each city, count reachable cities within threshold.
     */
    static int findTheCity(int n, int[][] edges, int distanceThreshold) {
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;
        for (int[] e : edges) {
            dist[e[0]][e[1]] = e[2];
            dist[e[1]][e[0]] = e[2];
        }

        // Floyd-Warshall
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] != INF && dist[k][j] != INF)
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);

        int city = -1, minNeighbors = n;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++)
                if (i != j && dist[i][j] <= distanceThreshold) count++;
            if (count <= minNeighbors) { minNeighbors = count; city = i; }
        }
        return city;
    }

    // =========================================================
    // TOPIC 66 — A* SEARCH
    // =========================================================

    /**
     * A* — heuristic-guided shortest path.
     *
     * Idea: like Dijkstra but uses f(n) = g(n) + h(n) as priority.
     *   g(n) = exact cost from start to n (same as Dijkstra's dist[n])
     *   h(n) = heuristic estimate from n to goal
     *   f(n) = total estimated cost through n
     *
     * Admissible heuristic: h(n) ≤ actual distance → A* finds optimal path.
     * Common heuristics for grids:
     *   Manhattan distance: |dx| + |dy|         (4-directional moves)
     *   Euclidean distance: sqrt(dx²+dy²)        (any direction)
     *   Chebyshev distance: max(|dx|, |dy|)      (8-directional moves)
     *
     * Time: O(E log V) with good heuristic (same as Dijkstra worst case)
     * A* is faster than Dijkstra in practice because it explores fewer nodes.
     */
    static int aStarGrid(int[][] grid, int[] start, int[] goal) {
        int rows = grid.length, cols = grid[0].length;
        int[][] gCost = new int[rows][cols];
        for (int[] row : gCost) Arrays.fill(row, INF);
        gCost[start[0]][start[1]] = 0;

        // heap: [fCost, row, col]
        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        heap.offer(new int[]{heuristic(start[0], start[1], goal), start[0], start[1]});

        boolean[][] closed = new boolean[rows][cols];
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!heap.isEmpty()) {
            int[] cur = heap.poll();
            int r = cur[1], c = cur[2];

            if (r == goal[0] && c == goal[1]) return gCost[r][c];
            if (closed[r][c]) continue;
            closed[r][c] = true;

            for (int[] d : dirs) {
                int nr = r+d[0], nc = c+d[1];
                if (nr<0||nr>=rows||nc<0||nc>=cols||grid[nr][nc]==1||closed[nr][nc]) continue;
                int newG = gCost[r][c] + 1;
                if (newG < gCost[nr][nc]) {
                    gCost[nr][nc] = newG;
                    int f = newG + heuristic(nr, nc, goal);
                    heap.offer(new int[]{f, nr, nc});
                }
            }
        }
        return -1; // no path
    }

    /** Manhattan distance heuristic (admissible for 4-directional grid) */
    private static int heuristic(int r, int c, int[] goal) {
        return Math.abs(r - goal[0]) + Math.abs(c - goal[1]);
    }

    /**
     * LeetCode #1091 — Shortest Path in Binary Matrix (8 directions)
     * BFS is optimal here (uniform cost), shown as A* variant.
     */
    static int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n-1][n-1] == 1) return -1;

        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{0, 0, 1}); // [row, col, dist]
        grid[0][0] = 1; // mark visited
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1], d = cur[2];
            if (r == n-1 && c == n-1) return d;
            for (int[] dir : dirs) {
                int nr = r+dir[0], nc = c+dir[1];
                if (nr>=0&&nr<n&&nc>=0&&nc<n&&grid[nr][nc]==0) {
                    grid[nr][nc] = 1;
                    q.offer(new int[]{nr, nc, d+1});
                }
            }
        }
        return -1;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    static List<List<int[]>> buildAdj(int V, int[][] edges, boolean directed) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
            if (!directed) adj.get(e[1]).add(new int[]{e[0], e[2]});
        }
        return adj;
    }

    static void printDist(int[] dist, String algo) {
        System.out.print(algo + " distances: ");
        for (int i = 0; i < dist.length; i++)
            System.out.print(i + "=" + (dist[i]==INF ? "∞" : dist[i]) + " ");
        System.out.println();
    }

    // =========================================================
    // DEMO / TEST
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== TOPIC 63: Dijkstra ===");
        //   0 --4-- 1
        //   |       |
        //   2 --1-- 3 --5-- 4
        //       \--3--|
        int V = 5;
        int[][] edges = {{0,1,4},{0,2,1},{2,3,2},{1,3,1},{3,4,5},{2,1,2}};
        List<List<int[]>> adj = buildAdj(V, edges, false);
        int[] dist = dijkstra(V, adj, 0);
        printDist(dist, "Dijkstra from 0");
        // 0=0, 1=3, 2=1, 3=3, 4=8

        int[][] res = dijkstraWithPath(V, adj, 0);
        System.out.println("Path 0→4: " + reconstructPath(res[1], 4));

        // #743 Network Delay
        int[][] times = {{2,1,1},{2,3,1},{3,4,1}};
        System.out.println("#743 networkDelayTime: " + networkDelayTime(times, 4, 2)); // 2

        // #1631 Minimum Effort
        int[][] heights = {{1,2,2},{3,8,2},{5,3,5}};
        System.out.println("#1631 minEffortPath:   " + minimumEffortPath(heights)); // 2

        // #778 Swim in Rising Water
        int[][] water = {{0,2},{1,3}};
        System.out.println("#778 swimInWater:      " + swimInWater(water)); // 3

        System.out.println("\n=== TOPIC 64: Bellman-Ford ===");
        // Graph with negative edge: 0→1 (4), 0→2 (-1), 2→1 (2), 1→3 (5)
        int[][] bfEdges = {{0,1,4},{0,2,-1},{2,1,2},{1,3,5}};
        int[] bfDist = bellmanFord(4, bfEdges, 0);
        printDist(bfDist, "BellmanFord from 0");
        // 0=0, 1=1, 2=-1, 3=6

        // Negative cycle detection
        int[][] negCycle = {{0,1,1},{1,2,-3},{2,0,1}};
        System.out.println("Has negative cycle: " + hasNegativeCycle(3, negCycle, 0)); // true

        // #787 Cheapest Flights
        int[][] flights = {{0,1,100},{1,2,100},{0,2,500}};
        System.out.println("#787 cheapestFlights(k=1): "
                + findCheapestPrice(3, flights, 0, 2, 1)); // 200

        System.out.println("\n=== TOPIC 65: Floyd-Warshall ===");
        //   0  1  2  3
        int[][] matrix = {
            {0,   3,   INF, 7  },
            {8,   0,   2,   INF},
            {5,   INF, 0,   1  },
            {2,   INF, INF, 0  }
        };
        int[][] fw = floydWarshall(4, matrix);
        System.out.println("All-pairs shortest paths:");
        for (int i = 0; i < 4; i++) {
            System.out.print("  From " + i + ": ");
            for (int j = 0; j < 4; j++)
                System.out.print(j + "=" + (fw[i][j]==INF ? "∞" : fw[i][j]) + " ");
            System.out.println();
        }

        int[][] cityEdges = {{0,1,3},{1,2,1},{1,3,4},{2,3,1}};
        System.out.println("#1334 findTheCity(threshold=4): "
                + findTheCity(4, cityEdges, 4)); // 3

        System.out.println("\n=== TOPIC 66: A* Search ===");
        // 0=free, 1=blocked
        int[][] grid = {
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,0,0,1,0},
            {0,1,0,0,0},
            {0,0,0,0,0}
        };
        int pathLen = aStarGrid(grid, new int[]{0,0}, new int[]{4,4});
        System.out.println("A* path length (0,0)→(4,4): " + pathLen);

        int[][] binGrid = {{0,1},{1,0}};
        System.out.println("#1091 shortestPathBinaryMatrix: "
                + shortestPathBinaryMatrix(binGrid)); // 2

        int[][] binGrid2 = {{0,0,0},{1,1,0},{1,1,0}};
        System.out.println("#1091 shortestPathBinaryMatrix: "
                + shortestPathBinaryMatrix(binGrid2)); // 4
    }
}
