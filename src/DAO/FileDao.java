package DAO;

import ADT.LinkedList;
import ADT.ListInterface;
import java.io.*;
import java.util.function.Function;

public class FileDao<T> {

    // Method to load data from a CSV file into a LinkedList
    public ListInterface<T> loadDataFromCSV(String fileName, Function<String[], T> mapper) {
        ListInterface<T> list = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip the header line
                    continue;
                }
                String[] values = parseCSVLine(line);
                T item = mapper.apply(values);
                if (item != null) {
                    list.add(item);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Method to write data from a LinkedList to a CSV file
    public void writeDataToCSV(String fileName, ListInterface<String> headers, ListInterface<T> data,
            Function<T, ListInterface<String>> mapper) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println(joinList(headers));
            for (int i = 0; i < data.size(); i++) {
                T item = data.get(i);
                if (item != null) {
                    ListInterface<String> row = mapper.apply(item);
                    writer.println(escapeCSV(row));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to join ListInterface<String> into a single CSV line
    private String joinList(ListInterface<String> list) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    // Helper method to parse a CSV line into an array of Strings
    private String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Splits on commas, but not within quotes
    }

    // Helper method to escape special characters in a CSV field
    private String escapeCSV(ListInterface<String> fields) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            if (field.contains(",") || field.contains("\"")) {
                field = "\"" + field.replace("\"", "\"\"") + "\"";
            }
            escaped.append(field);
            if (i < fields.size() - 1) {
                escaped.append(",");
            }
        }
        return escaped.toString();
    }
}
