package com.artlycode.algs.graphs.vehiclebookingseqs.graph;

import java.util.StringJoiner;

/**
 * Arrow of a graph
 * <p>Created: 5/16/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
public class Arrow {

    private int id;

    private int from;

    private int to;

    public Arrow(final int from, final int to, final int arrowId) {
        this.from = from;
        this.to = to;
        this.id = arrowId;
    }

    public int getId() {
        return id;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Arrow{", "}").add("from=" + from).add("id=" + id).add("to=" + to).toString();
    }
}
