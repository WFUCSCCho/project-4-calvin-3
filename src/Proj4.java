import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.io.File;

/*∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*
  @file: Proj4.java
  @description: This file is responsible for executing the program
                - creates lists (sorted, shuffled, reversed) with objects of my dataset
                - times the sorting algorithm performance
                - prints the results and appends them to analysis.txt
  @author: Calvin Malaney
  @date: November 26, 2025
∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗∗*/

public class Proj4 {
    public static void main(String[] args) throws IOException {
        // Use command line arguments to specify the input file
        if (args.length != 2) {
            System.err.println("Usage: java TestAvl <input file> <number of lines>");
            System.exit(1);
        }

        String inputFileName = args[0];
        int numLines = Integer.parseInt(args[1]);

        // For file input
        FileInputStream inputFileNameStream = null;
        Scanner inputFileNameScanner = null;

        // Open the input file
        inputFileNameStream = new FileInputStream(inputFileName);
        inputFileNameScanner = new Scanner(inputFileNameStream);

        // ignore first line
        inputFileNameScanner.nextLine();

        ArrayList<FIFARecord> list = new ArrayList<>();

        //read dataset
        int count = 0;
        while (inputFileNameScanner.hasNextLine() && count < numLines) {
            String line = inputFileNameScanner.nextLine();

            String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            if (tokens.length < 13) {
                continue;
            }

            String slug = tokens[0];
            String name = tokens[2];
            String fullName = tokens[3];
            String bestPosition = tokens[10];

            int overall;
            int potential;

            try {
                overall = Integer.parseInt(tokens[11].trim());
                potential = Integer.parseInt(tokens[12].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            list.add(new FIFARecord(slug, name, bestPosition, fullName, overall, potential));
            count++;
        }
        inputFileNameScanner.close();

        //create lists
        ArrayList<FIFARecord> sorted = new ArrayList<>(list);
        ArrayList<FIFARecord> shuffled = new ArrayList<>(list);
        ArrayList<FIFARecord> reversed = new ArrayList<>(list);

        Collections.sort(sorted);                       // sorted by compareTo()
        Collections.shuffle(shuffled);                  // shuffled
        Collections.sort(reversed, Collections.reverseOrder());  //reversed

        //time in nanoseconds
        long sortedInsertTime, sortedSearchTime, sortedDeleteTime;
        long shuffleInsertTime, shuffleSearchTime, shuffleDeleteTime;
        long reverseInsertTime, reverseSearchTime, reverseDeleteTime;

        //sorted
        SeparateChainingHashTable<FIFARecord> table = new SeparateChainingHashTable<>();

        long start = System.nanoTime();
        for (FIFARecord r : sorted) {
            table.insert(r);
        }
        long end = System.nanoTime();
        sortedInsertTime = end - start;

        start = System.nanoTime();
        for (FIFARecord r : sorted) {
            table.contains(r);
        }
        end = System.nanoTime();
        sortedSearchTime = end - start;

        start = System.nanoTime();
        for (FIFARecord r : sorted) {
            table.remove(r);
        }
        end = System.nanoTime();
        sortedDeleteTime = end - start;
        // table is now empty

        //shuffled
        table = new SeparateChainingHashTable<>();

        start = System.nanoTime();
        for (FIFARecord r : shuffled) {
            table.insert(r);
        }
        end = System.nanoTime();
        shuffleInsertTime = end - start;

        start = System.nanoTime();
        for (FIFARecord r : shuffled) {
            table.contains(r);
        }
        end = System.nanoTime();
        shuffleSearchTime = end - start;

        start = System.nanoTime();
        for (FIFARecord r : shuffled) {
            table.remove(r);
        }
        end = System.nanoTime();
        shuffleDeleteTime = end - start;
        // table is now empty

        //reversed
        table = new SeparateChainingHashTable<>();

        start = System.nanoTime();
        for (FIFARecord r : reversed) {
            table.insert(r);
        }
        end = System.nanoTime();
        reverseInsertTime = end - start;

        start = System.nanoTime();
        for (FIFARecord r : reversed) {
            table.contains(r);
        }
        end = System.nanoTime();
        reverseSearchTime = end - start;

        start = System.nanoTime();
        for (FIFARecord r : reversed) {
            table.remove(r);
        }
        end = System.nanoTime();
        reverseDeleteTime = end - start;

        //print
        System.out.println("Number of lines evaluated: " + count);
        System.out.println();

        System.out.println("Sorted list timings:");
        System.out.printf("  Insert: %.3f ms%n", sortedInsertTime / 1_000_000.0);
        System.out.printf("  Search: %.3f ms%n", sortedSearchTime / 1_000_000.0);
        System.out.printf("  Delete: %.3f ms%n%n", sortedDeleteTime / 1_000_000.0);

        System.out.println("Shuffled list timings:");
        System.out.printf("  Insert: %.3f ms%n", shuffleInsertTime / 1_000_000.0);
        System.out.printf("  Search: %.3f ms%n", shuffleSearchTime / 1_000_000.0);
        System.out.printf("  Delete: %.3f ms%n%n", shuffleDeleteTime / 1_000_000.0);

        System.out.println("Reversed list timings:");
        System.out.printf("  Insert: %.3f ms%n", reverseInsertTime / 1_000_000.0);
        System.out.printf("  Search: %.3f ms%n", reverseSearchTime / 1_000_000.0);
        System.out.printf("  Delete: %.3f ms%n%n", reverseDeleteTime / 1_000_000.0);

        //append to analysis.txt (csv format)
        // columns: N, sortedInsert, sortedSearch, sortedDelete,
        //          shuffledInsert, shuffledSearch, shuffledDelete,
        //          reversedInsert, reversedSearch, reversedDelete
        File analysisFile = new File("analysis.txt");
        boolean fileExists = analysisFile.exists() && analysisFile.length() > 0;

        FileOutputStream out = new FileOutputStream(analysisFile, true);

        if (!fileExists) {
            String header =
                    "N,sorted_insert_ns,sorted_search_ns,sorted_delete_ns," +
                            "shuffled_insert_ns,shuffled_search_ns,shuffled_delete_ns," +
                            "reversed_insert_ns,reversed_search_ns,reversed_delete_ns\n";
            out.write(header.getBytes());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(count).append(",");
        sb.append(sortedInsertTime).append(",");
        sb.append(sortedSearchTime).append(",");
        sb.append(sortedDeleteTime).append(",");
        sb.append(shuffleInsertTime).append(",");
        sb.append(shuffleSearchTime).append(",");
        sb.append(shuffleDeleteTime).append(",");
        sb.append(reverseInsertTime).append(",");
        sb.append(reverseSearchTime).append(",");
        sb.append(reverseDeleteTime).append("\n");

        out.write(sb.toString().getBytes());
        out.close();
    }

    // helper for parsing
    private static boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            Integer.parseInt(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
