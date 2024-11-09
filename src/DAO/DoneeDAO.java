package DAO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import ADT.ListInterface;
import entity.Donee;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DoneeDAO {

    public static <T> void saveToFile(String fileName, ListInterface<T> list, String header) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the header
            writer.write(header);
            writer.newLine();

            // Write the data
            for (int i = 1; i <= list.getNumberOfEntries(); i++) {
                T entry = list.getEntry(i);
                if (entry instanceof Donee) {
                    writer.write(convertToCSVLine((Donee) entry));
                    writer.newLine();
                } else {
                    writer.write(entry.toString());
                    writer.newLine();
                }
            }
            System.out.println("Data saved successfully to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    public static <T> void loadFromFile(String fileName, ListInterface<T> list, DataParser<T> parser) {
        File file = new File(fileName);

        // Check if file exists
        if (!file.exists()) {
            try {
                // Create an empty file
                if (file.createNewFile()) {
                    System.out.println("File not found. An empty file has been created: " + fileName);
                } else {
                    System.out.println("Failed to create an empty file.");
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Read and discard the header line
            String header = reader.readLine();

            // Check if file is empty (i.e., only contains header)
            if (header == null) {
                System.out.println("The file doneeData.csv is empty.");
                return;
            }

            // Read and parse the data
            String line;
            boolean hasData = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines if any
                }
                hasData = true;
                T data = parser.parse(line);
                list.add(data);  // Assumes LinkedList's add method is available
            }

            if (!hasData) {
                System.out.println("No data found in the file.");
            } else {
                System.out.println("Data loaded successfully from " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error loading data from file: " + e.getMessage());
        }
    }

    private static String escapeForCSV(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        // Check if the data needs to be quoted
        boolean needsQuotes = data.contains(",") || data.contains("\"") || data.contains("\n");

        if (needsQuotes) {
            StringBuilder sb = new StringBuilder("\"");
            for (char c : data.toCharArray()) {
                if (c == '\"') {
                    sb.append("\"\""); // Escape double quotes
                } else {
                    sb.append(c);
                }
            }
            sb.append("\"");
            return sb.toString();
        } else {
            return data;
        }
    }

    private static String convertToCSVLine(Donee donee) {
        return String.join(",",
                escapeForCSV(donee.getId()),
                escapeForCSV(donee.getName()),
                escapeForCSV(donee.getAddress()),
                escapeForCSV(donee.getPhoneNumber()),
                escapeForCSV(donee.getEmail()),
                escapeForCSV(donee.getDoneeType()),
                escapeForCSV(donee.getOrganizationName())
        );
    }

    // Functional interface for parsing each line of the CSV
    @FunctionalInterface
    public interface DataParser<T> {

        T parse(String line);
    }

}
