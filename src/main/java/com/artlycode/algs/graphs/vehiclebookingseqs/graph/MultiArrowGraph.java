package com.artlycode.algs.graphs.vehiclebookingseqs.graph;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MultiArrowGraph
 * <p>Created: 5/18/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
// TODO: define doc
public class MultiArrowGraph {

    private Map<Integer, Vertex> matrix;

    // Map of vertices, where index is vertex id and value is the vertex.
    private MultiArrowGraph(Map<Integer, Vertex> matrix) {
        this.matrix = matrix;
    }

    /**
     * Prepare matrix for the graph from its arrows
     *
     * @param arrows list of arrows of the graph
     * @return MultiArrowGraph
     */
    public static MultiArrowGraph fromArrows(Collection<Arrow> arrows) {
        Map<Integer, Vertex> matrix = new HashMap<>();

        for (Arrow arrow : arrows) {
            int start = arrow.getFrom();
            int end = arrow.getTo();

            // Add the arrow into outbound arrows structure
            Vertex v1 = matrix.get(start);
            if (v1 == null) {
                v1 = new Vertex(start);
                matrix.put(start, v1);
            }
            v1.addOutArrow(arrow.getTo(), arrow.getId());

            // Add the arrow into inbound arrows structure
            Vertex v2 = matrix.get(end);
            if (v2 == null) {
                v2 = new Vertex(end);
                matrix.put(end, v2);
            }
            v2.addInArrow(arrow.getFrom(), arrow.getId());
        }
        return new MultiArrowGraph(matrix);
    }

    public List<Vertex> getSortedVertices(Comparator<Vertex> comparator) {
        return matrix.values().parallelStream().sorted(comparator).collect(Collectors.toList());
    }

    public List<Vertex> getVertices() {
        return matrix.values().parallelStream().collect(Collectors.toList());
    }

    /**
     * Removes all provided arrows and the nodes becoming isolated after that removal
     *
     * @param arrows arrows
     */
    public void removeSubGraph(List<Arrow> arrows) {
        for (Arrow arrow : arrows) {
            // Remove outbound arrow
            Vertex v1 = matrix.get(arrow.getFrom());
            v1.removeOutArrow(arrow.getTo(), arrow.getId());
            if (v1.getNumOutArrows() + v1.getNumInArrows() == 0) {
                matrix.remove(v1.getId());
            }

            // Remove inbound arrow
            Vertex v2 = matrix.get(arrow.getTo());
            v2.removeInArrows(arrow.getFrom(), arrow.getId());
            if (v2.getNumOutArrows() + v2.getNumInArrows() == 0) {
                matrix.remove(v2.getId());
            }
        }
    }

    public Vertex getVertex(int vertexId) {
        return this.matrix.get(vertexId);
    }

    public boolean isEmpty() {
        return matrix.isEmpty();
    }
}
