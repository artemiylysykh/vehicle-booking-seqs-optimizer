package com.artlycode.algs.graphs.vehiclebookingseqs.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represents a graph vertex with all inbound and outbound arrows.
 * <p>Created: 5/15/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
public class Vertex {

    private int id;

    private int numOutArrows;

    private int numInArrows;

    /**
     * Map containing all vertices connected by this vertex's outbound arrows
     * Keys are the end vertices for this vertex's outbound arrows.
     * Values are the HashSets containing ids of the outbound arrows.
     */
    private Map<Integer, Set<Integer>> outArrows;

    /**
     * Map containing all vertices connected by this vertex's inbound arrows
     * Keys are the start vertices for this vertex's inbound arrows.
     * Values are the HashSets containing ids of the inbound arrows.
     */
    private Map<Integer, Set<Integer>> inArrows;

    public Vertex(final int id) {
        this.id = id;
        this.numOutArrows = 0;
        this.numInArrows = 0;
        this.outArrows = new HashMap<>();
        this.inArrows = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    private Set<Integer> getOutArrows(Integer vertexId) {
        return this.outArrows.get(vertexId);
    }

    public List<Arrow> getOutArrows() {
        List<Arrow> result = new LinkedList<>();
        for (Map.Entry<Integer, Set<Integer>> s : outArrows.entrySet()) {
            for (Integer arrowId : s.getValue()) {
                result.add(new Arrow(this.id, s.getKey(), arrowId));
            }
        }
        return result;
    }

    public void addOutArrow(Integer to, Integer arrowId) {
        Set<Integer> arrowIds = this.outArrows.get(to);
        if (arrowIds == null) {
            arrowIds = new HashSet<>();
            this.outArrows.put(to, arrowIds);
        }
        arrowIds.add(arrowId);
        this.numOutArrows++;
    }

    public void removeOutArrow(Integer to, Integer arrowId) {
        Set<Integer> arrowIds = this.getOutArrows(to);
        if (arrowIds != null) {
            arrowIds.remove(arrowId);
        } else {
            throw new IllegalStateException("The graph vertex doesn't have this arrow");
        }
        this.numOutArrows--;

    }

    public void removeInArrows(Integer from, Integer arrowId) {
        Set<Integer> arrowIds = this.getInArrows(from);
        if (arrowIds != null) {
            arrowIds.remove(arrowId);
        } else {
            throw new IllegalStateException("The graph vertex doesn't have this arrow");
        }
        this.numInArrows--;

    }

    private Set<Integer> getInArrows(Integer vertexId) {
        return this.inArrows.get(vertexId);
    }

    public void addInArrow(Integer from, Integer arrowId) {
        Set<Integer> arrowIds = this.inArrows.get(from);
        if (arrowIds == null) {
            arrowIds = new HashSet<>();
            this.inArrows.put(from, arrowIds);
        }
        arrowIds.add(arrowId);
        this.numInArrows++;
    }

    public int getNumOutArrows() {
        return numOutArrows;
    }

    public int getNumInArrows() {
        return numInArrows;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Vertex{", "}").add("id=" + id)
                .add("numOutArrows=" + numOutArrows)
                .add("numInArrows=" + numInArrows)
                .add("outArrows=" + outArrows)
                .add("inArrows=" + inArrows)
                .toString();
    }
}
