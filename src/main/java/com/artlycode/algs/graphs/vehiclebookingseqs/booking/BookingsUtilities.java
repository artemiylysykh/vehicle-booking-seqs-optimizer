package com.artlycode.algs.graphs.vehiclebookingseqs.booking;

import com.artlycode.algs.graphs.vehiclebookingseqs.graph.Arrow;
import com.artlycode.algs.graphs.vehiclebookingseqs.graph.Graphs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class containing utility methods working with bookings.
 * <p>Created: 5/15/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
public class BookingsUtilities {

    /**
     * Analyzes the bookings and places them in a sequences (graph paths) so there is minimal number of vehicle
     * relocations between orders.
     *
     * @param bookings list of booking orders
     * @return Any optimal sequence of bookings with minimal number of vehicle relocations between orders.
     */
    public static List<List<Booking>> optimizeLogistics(List<Booking> bookings) {
        // Prepare new sequential indexes for every booking instead of using internal id of bookings
        Map<Integer, Booking> indexMap = IntStream.range(0, bookings.size())
                .boxed()
                .collect(Collectors.toMap(k -> k, v -> bookings.get(v.intValue())));

        // Convert bookings into arrows of the graph
        List<Arrow> arrows = indexMap.entrySet()
                .stream()
                .map(x -> new Arrow(x.getValue().getStart(), x.getValue().getEnd(), x.getKey()))
                .collect(Collectors.toList());
        List<List<Arrow>> paths = Graphs.breakAllIntoDeepUniquePaths(arrows);

        // Convert all paths of the graph into the list of bookings
        return paths.stream()
                .map(x -> x.stream().map(y -> indexMap.get(y.getId())).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

}
