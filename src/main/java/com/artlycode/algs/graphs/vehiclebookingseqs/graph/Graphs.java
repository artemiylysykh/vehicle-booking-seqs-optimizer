package com.artlycode.algs.graphs.vehiclebookingseqs.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class declaring utility methods to work with graphs
 * <p>Created: 5/19/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
public class Graphs {

    /**
     * Groups the arrows of a directed multi-arrow graph into a SHORTEST possible list of uninterrupted paths without
     * repeating arrows, i.e. each arrow can be included only into one identified path.<br>
     * <br>
     * Path is the route in the graph as presented here:<br>
     * <pre>
     * Vi0 -[Ae0]-> Vi1 -[Ae1]-> Vi2 -[Ae2]-> ... Vi(n-1) -[Aen-1]-> Vin   (n - length of the path)
     * ...
     * Vk0 -[Af0]-> Vk1 -[Af1]-> Vk2 -[Af2]-> ... Vk(m-1) -[Afm-1]-> Vim   (m - length of the path)
     * </pre>
     * Condition 1: the graph can contain more than 1 arrow connecting Vi -> Vj. <br>
     * Condition 2: the result paths are not necessarily longest, however deepest possible, i.e. for any path its
     * last vertix doesn't contain any other available outbound arrows connecting it with another vertex. <br>
     * Condition 3: the ids of arrows should be sequential without gaps.<br>
     * <br>
     * The algorithm includes the following major steps:
     * <ol>
     * <li> Build any possible paths starting from all vertices having positive degree (number of outbound arrows minus
     * number of inbound arrows), and exclude them from the graph <p>
     * <li> In the reduced graph while possible, find all possible random paths starting from all available vertices
     * with degree=0. All those paths are cycles <p>
     * <li> Attempt insert the cycles found on step 2 into the identified paths found on step 1. <p>
     * <li> The new paths with inserted cycles and the cycles which are not inserted into the paths (isolated cycles)
     * form the list of all found paths.
     * </ol>
     *
     * @param arrows list of arrows of directed multi-arrows graph.
     * @return shortest possible list of paths with all arrows of the graph without repeating arrows in those paths
     */
    public static List<List<Arrow>> breakAllIntoDeepUniquePaths(final List<Arrow> arrows) {
        // Construct the graph based on the arrows
        MultiArrowGraph g = MultiArrowGraph.fromArrows(arrows);

        // Sort vertices descending by degree and filter only those with positive degree for initial processing
        List<Vertex> posVertices = g.getSortedVertices(
                Comparator.comparingInt(x -> -1 * (x.getNumOutArrows() - x.getNumInArrows())))
                .stream()
                .filter(x -> x.getNumOutArrows() - x.getNumInArrows() > 0)
                .collect(Collectors.toList());

        // Find any paths in the matrix starting from positive degree vertices
        List<List<Arrow>> paths = extractOutboundPaths(g, posVertices, arrows.size());

        // After excluding the paths starting from positive degree vertices, the matrix may only contain cycles.
        // Find all finite paths (cycles) in the graph reduced by this point. All vertices are having 0 degree.
        List<List<Arrow>> cycles = new LinkedList<>();
        while (!g.isEmpty()) {
            Vertex zeroVertex = g.getVertices()
                    .stream()
                    .filter(x -> x.getNumOutArrows() - x.getNumInArrows() == 0)
                    .findFirst()
                    .orElse(null);

            cycles.addAll(extractOutboundPaths(g, Collections.singletonList(zeroVertex), arrows.size()));
        }

        logGraphPath("Paths before merging with cycles:", paths);
        logGraphPath("Cycles before merging with paths:", cycles);

        // Prepare maps of vertices as keys to paths they met in as values.
        Map<Integer, List<Arrow>> vertexToPathMap = new HashMap<>();
        for (List<Arrow> path : paths) {
            for (Arrow arrow : path) {
                // We can extend path even from the first vertex, but no from the last one in the path
                int v = arrow.getFrom();
                if (!vertexToPathMap.containsKey(v)) {
                    vertexToPathMap.put(v, path);
                }
            }
        }

        // Iterate over all cycles and check if we can insert any into the paths found before.
        Iterator<List<Arrow>> iter = cycles.iterator();
        while (iter.hasNext()) {
            List<Arrow> cycle = iter.next();

            // Try to find an arrow of the cycle which is outbound from any of vertices from paths
            Arrow cycleEntryArrow = cycle.stream()
                    .filter(a -> vertexToPathMap.containsKey(a.getFrom()))
                    .findFirst()
                    .orElse(null);

            // If we found common vertex for a path and for the cycle, then merge them
            if (cycleEntryArrow != null) {
                // Roll the cycles arrows to place the lining vertex at the start of the cycle
                cycle = getRolledCycle(cycle, cycleEntryArrow);

                // Find the index in the path list to insert the cycle into
                int v = cycleEntryArrow.getFrom();
                List<Arrow> path = vertexToPathMap.get(v);
                int idx = findArrowIndexByVertexId(path, v);

                if (idx >= 0) {
                    // Add cycle into the path
                    path.addAll(idx, cycle);
                    // Remove cycle from the cycles list
                    iter.remove();
                }
            }

        }
        // Here we sure that there are no cycles which can extend an identified path, so add into the result as
        // independent paths too.
        paths.addAll(cycles);
        logGraphPath("Result list of paths:", paths);
        return paths;
    }

    private static void logGraphPath(final String message, final List<List<Arrow>> paths) {
        System.out.println(message + " (n=" + paths.size() + ")");
        int count = 0;
        for (List<Arrow> p : paths) {
            System.out.print("#" + count + ":\t");
            p.stream().forEach(x -> System.out.print(" [" + x.getFrom() + "-(" + x.getId() + ")->" + x.getTo() + "]"));
            System.out.println();
            count++;
        }
        System.out.println();
    }

    /**
     * Returns new list where the elements are rolled so the arrow0 becomes the first element and all the elements
     * before it are moved to the tail. We do not do additional check whether it's cycle or not.
     *
     * @param cycle
     * @param arrow0
     */
    public static List<Arrow> getRolledCycle(List<Arrow> cycle, Arrow arrow0) {
        // Check if this is a cycle, i.e. there is arrow from end to the start of the cycle list
        if (cycle.get(0).getFrom() != cycle.get(cycle.size() - 1).getTo()) {
            throw new IllegalStateException("Doesn't seem this is a cycle. There might be a problem");
        }

        List<Arrow> tail = new LinkedList<>();
        List<Arrow> head = new LinkedList<>();

        boolean found = false;
        for (Arrow arrow : cycle) {
            if (arrow == arrow0) {
                found = true;
            }
            if (!found) {
                tail.add(arrow);
            } else {
                head.add(arrow);
            }
        }
        return Stream.concat(head.stream(), tail.stream()).collect(Collectors.toList());
    }

    /**
     * Returns index of Arrow in the list containing vertexId as start point.
     *
     * @param path
     * @param vertexId
     * @return positive value equal to the index, and -1 if not found
     */
    private static int findArrowIndexByVertexId(List<Arrow> path, int vertexId) {
        int i = 0;
        int p = -1;
        for (Arrow arrow : path) {
            if (arrow.getFrom() == vertexId) {
                p = i;
                break;
            } else {
                i++;
            }
        }
        return p;
    }

    /**
     * Searches all outbound paths for the provided vertices
     *
     * @param g        graph
     * @param vertices vertices to handle
     */
    private static List<List<Arrow>> extractOutboundPaths(MultiArrowGraph g, List<Vertex> vertices, int maxArrowId) {
        List<List<Arrow>> result = new ArrayList<>();
        // For each node with positive degree find the longest paths
        for (Vertex startV : vertices) {
            int degree = Integer.max(1, startV.getNumOutArrows() - startV.getNumInArrows());

            // We need to find and exclude one by one degree number of longest paths for the start node and exclude
            for (int i = 0; i < degree; i++) {
                List<Arrow> path = findAnyDeepPath(g, startV.getId(), maxArrowId);
                result.add(path);
                g.removeSubGraph(path);
            }
        }
        return result;
    }

    /**
     * Searches any path in the graph without repeating the arrows starting from startVertex
     *
     * @param g          graph
     * @param v0         start vertex
     * @param maxArrowId maximum id of arrow in the graph
     * @return list of arrows representing the path in the graph
     */
    public static List<Arrow> findAnyDeepPath(MultiArrowGraph g, int v0, int maxArrowId) {
        boolean[] vMarked = new boolean[maxArrowId];
        List<Arrow> path = new LinkedList<>();

        Vertex v = g.getVertex(v0);
        while (v != null) {
            Arrow nextArrow = v.getOutArrows().stream().filter(a -> !vMarked[a.getId()]).findFirst().orElse(null);
            if (nextArrow != null) {
                path.add(nextArrow);
                vMarked[nextArrow.getId()] = true;
                v = g.getVertex(nextArrow.getTo());
            } else {
                v = null;
            }
        }
        return path;
    }
}

