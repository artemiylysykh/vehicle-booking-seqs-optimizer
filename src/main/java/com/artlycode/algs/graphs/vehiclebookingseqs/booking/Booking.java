package com.artlycode.algs.graphs.vehiclebookingseqs.booking;

import java.util.StringJoiner;

/**
 * Booking
 * <p>Created: 5/15/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
public class Booking {

    private int id;

    private int start;

    private int end;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(final int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Booking{", "}").add("id=" + id)
                .add("start=" + start)
                .add("end=" + end)
                .toString();
    }
}
