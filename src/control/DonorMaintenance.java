//author: Choo Mun Chun

package control;

import ADT.DictionaryInterface;
import ADT.HashedDictionary;
import ADT.LinkedList;
import ADT.ListInterface;
import ADT.TreeMapImplementation;
import ADT.TreeMapInterface;
import DAO.FileDao;
import entity.Donor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonorMaintenance {
    private ListInterface<Donor> donorList = new LinkedList<>();
    private static final String FILE_NAME = "donorData.csv";
    private static final String CSV_FILE_NAME = "donorData.csv"; // Specify the CSV file name
    private final ListInterface<String> headers;
    private final Random random;
    private final FileDao<Donor> fileDao;
    private final Scanner scanner = new Scanner(System.in);

    // Declare sets for unique contact numbers and emails
    private final ListInterface<String> contactNumbers = new LinkedList<>();
    private final ListInterface<String> emails = new LinkedList<>();

    public DonorMaintenance() {
        headers = new LinkedList<>();
        headers.add("ID");
        headers.add("Name");
        headers.add("Contact Number");
        headers.add("Email");
        headers.add("Address");
        headers.add("Donor Type");
        headers.add("Donation Preference");
        headers.add("Donation Times");
        headers.add("Total Amount(RM)");

        fileDao = new FileDao<>();
        donorList = fileDao.loadDataFromCSV(FILE_NAME, this::mapRowToDonor);
        random = new Random();

        // Initialize the sets with existing donor data
        initializeContactAndEmailSets();
    }

    private Donor mapRowToDonor(String[] row) {
        if (row.length == 9) {
            return new Donor(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8]);
        } else {
            System.out.println("Warning: Incomplete or malformed row detected, skipping: " + String.join(",", row));
        }
        return null;
    }

    private void initializeContactAndEmailSets() {
        for (Donor donor : donorList) {
            if (donor != null) {
                contactNumbers.add(donor.getContactNumber());
                emails.add(donor.getEmail());
            }
        }
    }

    // Method to generate a unique Donor ID
    public String generateUniqueDonorId() {
        DictionaryInterface<Integer, Void> existingIds = getExistingDonorIds();
        int newId;

        do {
            newId = random.nextInt(999) + 1; // Generate a random number between 1 and 999
        } while (existingIds.contains(newId));

        return String.format("DA%03d", newId); // Format the ID as DA### with leading zeros
    }

    // Method to read existing donor IDs from the CSV file
    private DictionaryInterface<Integer, Void> getExistingDonorIds() {
        DictionaryInterface<Integer, Void> existingIds = new HashedDictionary<>();
        Pattern pattern = Pattern.compile("DA(\\d{3})");

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_NAME))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip the header line
                    continue;
                }
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group(1));
                    existingIds.add(id, null); // Add ID to dictionary with null value
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + CSV_FILE_NAME);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existingIds;
    }

    public boolean addDonor(String donorId, String name, String contactNumber, String email, String address,
            String donorType, String donationPreference, String donorTimes, String totalAmount) {
        if (contactNumbers.contains(contactNumber)) {
            System.out.println("\nError: Contact number already exists.");
            return false;
        } else {
            System.out.println("\nDonor added successfully!");
        }

        if (emails.contains(email)) {
            System.out.println("\nError: Email already exists.");
            return false;
        } else {
            System.out.println("\nDonor added successfully!");
        }

        Donor donor = new Donor(donorId, name, contactNumber, email, address, donorType, donationPreference, donorTimes,
                totalAmount);
        donorList.add(donor);
        contactNumbers.add(contactNumber); // Update the set with the new contact number
        emails.add(email); // Update the set with the new email
        saveDonorsToCSV();
        System.out.println("Donor added: " + donor);
        return true;
    }

    // -------------------------------------------------------------------------------------------------
    // Method to update donor details
    public boolean updateDonor(String donorId, String name, String contactNumber, String email,
            String address, String donorType, String donationPreference) {
        // Find the donor using the ADT method
        Donor donor = findDonorById(donorId); // Assume this uses ADT internally
        if (donor == null) {
            System.out.println("\nError: Donor not found.");
            return false;
        }

        // Check if the contact number already exists in another donor's record using
        // ADT methods
        for (int i = 0; i < donorList.size(); i++) {
            Donor d = donorList.get(i);
            if (!d.getDonorId().equals(donorId) && d.getContactNumber().equals(contactNumber)) {
                System.out.println("\nError: Contact number already exists.");
                return false;
            }
        }

        // Check if the email already exists in another donor's record using ADT methods
        for (int i = 0; i < donorList.size(); i++) {
            Donor d = donorList.get(i);
            if (!d.getDonorId().equals(donorId) && d.getEmail().equals(email)) {
                System.out.println("\nError: Email already exists.");
                return false;
            }
        }

        // Update donor details
        donor.setName(name);
        donor.setContactNumber(contactNumber);
        donor.setEmail(email);
        donor.setAddress(address);
        donor.setDonorType(donorType);
        donor.setDonationPreference(donationPreference);

        // Save changes using ADT methods
        if (saveDonorsToCSV()) {
            System.out.println("\nDonor updated successfully!");
            return true;
        } else {
            System.out.println("\nError: Failed to save donor updates.");
            return false;
        }
    }

    // -------------------------------------------------------------------------------------------------
    // Method to delete a donor

    public boolean deleteDonor(String donorId) {
        Donor donor = findDonorById(donorId);

        // Check if donor exists
        if (donor != null) {
            donorList.remove(donor);
            saveDonorsToCSV();
            return true;
        } else {
            return false;
        }
    }

    public ListInterface<Donor> getAllDonors() {
        return donorList;
    }

    // -------------------------------------------------------------------------------------------------
    // Method to display all donors

    public void viewAllDonors() {
        ListInterface<Donor> donorList = getAllDonors();
        if (donorList.size() == 0) {
            System.out.println("No donors found.");
        } else {
            System.out.println("\n--- Donor List ---");
            System.out.printf("%-10s %-20s %-15s %-25s %-20s %-20s %-15s %-15s%n",
                    "Donor ID", "Name", "Contact No.", "Email", "Address",
                    "Donor Type", "Donor Times", "Total Amount (RM)");
            System.out.println(
                    "---------------------------------------------------------------------------------------------"
                            + "-------------------------------------------------------------");

            for (int i = 0; i < donorList.size(); i++) {
                Donor donor = donorList.get(i);
                System.out.printf("%-10s %-20s %-15s %-25s %-20s %-20s %-15s %-15s%n",
                        donor.getDonorId(),
                        donor.getName(),
                        donor.getContactNumber(),
                        donor.getEmail(),
                        donor.getAddress(),
                        donor.getDonorType(),
                        donor.getDonorTimes(),
                        donor.getTotalAmount());
            }
        }
    }

    // merge sort
    private ListInterface<Donor> mergeSort(ListInterface<Donor> list, Comparator<Donor> comparator) {
        if (list.size() <= 1) {
            return list;
        }
    
        int middle = list.size() / 2;
        ListInterface<Donor> left = new LinkedList<>();
        ListInterface<Donor> right = new LinkedList<>();
    
        for (int i = 0; i < middle; i++) {
            left.add(list.get(i));
        }
        for (int i = middle; i < list.size(); i++) {
            right.add(list.get(i));
        }
    
        left = mergeSort(left, comparator);
        right = mergeSort(right, comparator);
    
        return merge(left, right, comparator);
    }
    
    private ListInterface<Donor> merge(ListInterface<Donor> left, ListInterface<Donor> right,
            Comparator<Donor> comparator) {
        ListInterface<Donor> result = new LinkedList<>();
    
        while (!left.isEmpty() && !right.isEmpty()) {
            if (comparator.compare(left.get(0), right.get(0)) <= 0) {
                result.add(left.remove(0));
            } else {
                result.add(right.remove(0));
            }
        }
    
        while (!left.isEmpty()) {
            result.add(left.remove(0));
        }
    
        while (!right.isEmpty()) {
            result.add(right.remove(0));
        }
    
        return result;
    }
    
    public void sortByIdAscending() {
        ListInterface<Donor> sortedList = mergeSort(getAllDonors(), Comparator.comparing(Donor::getDonorId));
        updateDonorList(sortedList);
    }
    
    public void sortByIdDescending() {
        ListInterface<Donor> sortedList = mergeSort(getAllDonors(), Comparator.comparing(Donor::getDonorId).reversed());
        updateDonorList(sortedList);
    }
    
    public void sortByNameAscending() {
        ListInterface<Donor> sortedList = mergeSort(getAllDonors(), Comparator.comparing(Donor::getName));
        updateDonorList(sortedList);
    }
    
    public void sortByNameDescending() {
        ListInterface<Donor> sortedList = mergeSort(getAllDonors(), Comparator.comparing(Donor::getName).reversed());
        updateDonorList(sortedList);
    }
    
    public void sortByAmountAscending() {
        ListInterface<Donor> sortedList = mergeSort(getAllDonors(), Comparator.comparing(Donor::getTotalAmountAsDouble));
        updateDonorList(sortedList);
    }
    
    public void sortByAmountDescending() {
        ListInterface<Donor> sortedList = mergeSort(getAllDonors(), Comparator.comparing(Donor::getTotalAmountAsDouble).reversed());
        updateDonorList(sortedList);
    }
    
    private void updateDonorList(ListInterface<Donor> sortedList) {
        donorList.clear();
        for (int i = 0; i < sortedList.size(); i++) {
            donorList.add(sortedList.get(i));
        }
    }
    

    // -------------------------------------------------------------------------------------------------
    // Method to search for a donor by ID, name, email, or contact number

    public Donor findDonorById(String donorId) {
        return donorList.stream()
                .filter(donor -> donor != null && donor.getDonorId().equals(donorId))
                .findFirst()
                .orElse(null);
    }

    public Donor findDonorByName(String name) {
        return donorList.stream()
                .filter(donor -> donor != null && donor.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Donor findDonorByEmail(String email) {
        return donorList.stream()
                .filter(donor -> donor != null && donor.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public Donor findDonorByContactNumber(String contactNumber) {
        return donorList.stream()
                .filter(donor -> donor != null && donor.getContactNumber().equals(contactNumber))
                .findFirst()
                .orElse(null);
    }

    // -------------------------------------------------------------------------------------------------

    // filter
    // Method to display the filtered donors
    public void displayFilteredDonors(TreeMapInterface<String, Donor> filteredDonors) {
        if (filteredDonors.isEmpty()) {
            System.out.println("No donors found with the specified criteria.");
        } else {
            System.out.println("\n--- Filtered Donors ---");
            System.out.println("-------------------------------------------------------------"
                    + "------------------------------------------------------------------------------");
            System.out.printf("%-10s %-20s %-15s %-25s %-20s %-15s %-15s %-15s%n",
                    "Donor ID", "Name", "Contact No.", "Email", "Address",
                    "Donor Type", "Donation Times", "Total Amount (RM)");
            System.out.println("-------------------------------------------------------------"
                    + "-----------------------------------------------------------------------------");

            for (TreeMapInterface.CustomEntry<String, Donor> entry : filteredDonors.entries()) {
                Donor donor = entry.getValue();
                System.out.printf("%-10s %-20s %-15s %-25s %-20s %-20s %-15s %-15s%n",
                        donor.getDonorId(),
                        donor.getName(),
                        donor.getContactNumber(),
                        donor.getEmail(),
                        donor.getAddress(),
                        donor.getDonorType(),
                        donor.getDonorTimes(),
                        donor.getTotalAmount());
            }
        }
    }

    public TreeMapInterface<String, Donor> filterByDonorType(String donorType) {
        TreeMapInterface<String, Donor> result = new TreeMapImplementation<>();
        for (Donor donor : donorList) {
            if (donor.getDonorType().equalsIgnoreCase(donorType)) {
                result.put(donor.getDonorId(), donor);
            }
        }
        return result;
    }

    public TreeMapInterface<String, Donor> filterByDonationPreference(String donationPreference) {
        TreeMapInterface<String, Donor> result = new TreeMapImplementation<>();
        for (Donor donor : donorList) {
            if (donor.getDonationPreference().equalsIgnoreCase(donationPreference)) {
                result.put(donor.getDonorId(), donor);
            }
        }
        return result;
    }

    public TreeMapInterface<String, Donor> filterByDonationTimes(String donationTimes) {
        TreeMapInterface<String, Donor> result = new TreeMapImplementation<>();
        for (Donor donor : donorList) {
            if (donor.getDonorTimes().equalsIgnoreCase(donationTimes)) {
                result.put(donor.getDonorId(), donor);
            }
        }
        return result;
    }

    public TreeMapInterface<String, Donor> filterByTotalAmount(double minAmount, double maxAmount) {
        TreeMapInterface<String, Donor> result = new TreeMapImplementation<>();
        for (Donor donor : donorList) {
            double totalAmount;
            try {
                totalAmount = Double.parseDouble(donor.getTotalAmount());
            } catch (NumberFormatException e) {
                System.err.println("Invalid total amount for donor " + donor.getDonorId());
                continue;
            }
            if (totalAmount >= minAmount && totalAmount <= maxAmount) {
                result.put(donor.getDonorId(), donor);
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------------------------------

    public boolean saveDonorsToCSV() {
        try {
            ListInterface<Donor> validDonors = new LinkedList<>();
            for (int i = 0; i < donorList.size(); i++) {
                Donor donor = donorList.get(i);
                if (donor != null) {
                    validDonors.add(donor);
                } else {
                    System.err.println("Warning: Null donor found at index " + i);
                }
            }
            fileDao.writeDataToCSV(FILE_NAME, headers, validDonors, this::mapDonorToRow);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving donors to CSV: " + e.getMessage());
            return false;
        }
    }

    private ListInterface<String> mapDonorToRow(Donor donor) {
        ListInterface<String> row = new LinkedList<>();
        row.add(donor.getDonorId());
        row.add(donor.getName());
        row.add(donor.getContactNumber());
        row.add(donor.getEmail());
        row.add(donor.getAddress());
        row.add(donor.getDonorType());
        row.add(donor.getDonationPreference());
        row.add(donor.getDonorTimes());
        row.add(donor.getTotalAmount());
        return row;
    }

    // -------------------------------------------------------------------------------------------------
    // Donor Report
    public void generateDetailedDonorReport() {
        System.out.println("\n--- Detailed Donor Report ---");
        System.out.printf("%-10s %-20s %-15s %-25s %-20s %-20s %-15s %-15s%n",
                "Donor ID", "Name", "Contact No.", "Email", "Address",
                "Donor Type", "Donor Times", "Total Amount (RM)");
        System.out.println(
                "---------------------------------------------------------------------------------------------"
                        + "-------------------------------------------------------------");

        for (Donor donor : donorList) {
            System.out.printf("%-10s %-20s %-15s %-25s %-20s %-20s %-15s %-15s%n",
                    donor.getDonorId(),
                    donor.getName(),
                    donor.getContactNumber(),
                    donor.getEmail(),
                    donor.getAddress(),
                    donor.getDonorType(),
                    donor.getDonorTimes(),
                    donor.getTotalAmount());
        }
    }

    public void generateDonorSummaryReport() {
        // Calculate summary statistics
        int totalDonors = donorList.size();
        int totalDonorTypes = (int) donorList.stream().map(Donor::getDonorType).distinct().count();

        // Convert String to double and handle potential NumberFormatException
        double totalAmountDonated = donorList.stream()
                .mapToDouble(donor -> {
                    try {
                        return Double.parseDouble(donor.getTotalAmount());
                    } catch (NumberFormatException e) {
                        return 0.0; // In case of an invalid amount format, return 0.0
                    }
                })
                .sum();

        // Get highest donation times
        int highestDonationTimes = donorList.stream()
                .mapToInt(donor -> {
                    try {
                        return Integer.parseInt(donor.getDonorTimes());
                    } catch (NumberFormatException e) {
                        return 0; // In case of an invalid times format, return 0
                    }
                })
                .max()
                .orElse(0);

        // Get top donor
        Donor topDonor = donorList.stream()
                .max(Comparator.comparing(donor -> {
                    try {
                        return Double.parseDouble(donor.getTotalAmount());
                    } catch (NumberFormatException e) {
                        return 0.0; // In case of an invalid amount format, return 0.0
                    }
                }))
                .orElse(null);

        System.out.println("\n" + "-".repeat(50));
        System.out.println("              Donor Summary Report");
        System.out.println("-".repeat(50) + "\n");

        System.out.printf("%-30s : %d%n", "Total Number of Donors", totalDonors);
        System.out.printf("%-30s : %d%n", "Number of Unique Donor Types", totalDonorTypes);
        System.out.printf("%-30s : RM %.2f%n", "Total Amount Donated", totalAmountDonated);
        System.out.printf("%-30s : %d%n", "Highest Number of Donations", highestDonationTimes);

        System.out.println();

        if (topDonor != null) {
            System.out.println("Top Donor:");
            System.out.printf("  %-20s : %s%n", "Name", topDonor.getName());
            System.out.printf("  %-20s : RM %.2f%n", "Total Amount", Double.parseDouble(topDonor.getTotalAmount()));
        } else {
            System.out.println("No donors found.");
        }

        System.out.println("\n" + "-".repeat(50));
    }
}
