package com.artlycode.algs.graphs.vehiclebookingseqs;

import com.artlycode.algs.graphs.vehiclebookingseqs.booking.Booking;
import com.artlycode.algs.graphs.vehiclebookingseqs.booking.BookingsUtilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vehicle Booking Sequence Application
 * <p>Created: 5/15/18
 *
 * @author Artemiy Lysykh
 * @since 1.0
 */
public class Application {

    public static void main(String[] args) {
        try {
            List<Booking> bookings = readBookingsFromFile(
                    args.length > 0 && args[0] != null ? args[0] : "src/main/resources/bookings2.json");

            logBookings("Bookings before sorting: (n=" + bookings.size() + ")", bookings);

            // Optimize bookings to minimize relocations
            List<List<Booking>> bookingsChains = BookingsUtilities.optimizeLogistics(bookings);
            List<Booking> bookingSeq = bookingsChains.stream().flatMap(List::stream).collect(Collectors.toList());

            System.out.println("Number of relocations: " + bookingsChains.size());
            logBookings("Result bookings: (n=" + bookings.size() + ")", bookingSeq);

            writeBookingsIntoFile(args.length > 1 && args[1] != null ? args[1] : "output.json", bookingSeq);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reads bookings from the file.
     *
     * @param file file name to read from
     * @throws FileNotFoundException
     */
    private static List<Booking> readBookingsFromFile(final String file) throws FileNotFoundException {
        System.out.println("Input file name: " + file);

        // Read the file and parse the JSON structure
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Gson gson = new Gson();
        return gson.fromJson(reader, new TypeToken<List<Booking>>() { }.getType());
    }

    /**
     * Writes all bookings into the file
     *
     * @param outFile    file name to write to
     * @param bookingSeq list of bookings which represent a path
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private static void writeBookingsIntoFile(final String outFile, final List<Booking> bookingSeq)
            throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\nOut file name: " + outFile);

        PrintWriter writer = new PrintWriter(outFile, "UTF-8");
        writer.write("[" + String.join(", ",
                bookingSeq.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.toList())) + "]");
        writer.close();
    }

    /**
     * Just performs printing into out put the structured
     *
     * @param message           message to print
     * @param optimizedBookings list of bookings
     */
    private static void logBookings(final String message, final List<Booking> optimizedBookings) {
        if (optimizedBookings.size() > 0) {
            System.out.println(message);
            optimizedBookings.stream()
                    .forEach(x -> System.out.print(" [" + x.getStart() + "-(" + x.getId() + ")->" + x.getEnd() + "]"));
        }
        System.out.println("\n");
    }
}
