//author: Choo Mun Chun

package control;

import ADT.DictionaryInterface;
import ADT.HashMapImplementation;
import ADT.HashMapInterface;
import ADT.HashedDictionary;
import ADT.LinkedList;
import ADT.ListInterface;
import ADT.TreeMapImplementation;
import ADT.TreeMapInterface;
import DAO.FileDao;
import entity.Donation;
import entity.Donation.DonationType;
import entity.Donor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.function.Function;

public class DonationMaintenance {
    private final DictionaryInterface<String, ListInterface<Donation>> donationHashMap;
    private final TreeMapInterface<Date, ListInterface<Donation>> donationTreeMap;
    public ListInterface<Donation> donationLinkedList;
    private static final String DONATION_CSV_PATH = "Donation.csv";
    private int donationCounter;

    // Constructor
    public DonationMaintenance() {
        donationHashMap = new HashedDictionary<>();
        donationTreeMap = new TreeMapImplementation<>();
        donationLinkedList = new LinkedList<>();
        this.donationCounter = loadHighestDonationCounter();
        createDonationCSV();
    }

    // Method to create Donation CSV file with headers if it doesn't exist
    private void createDonationCSV() {
        File file = new File(DONATION_CSV_PATH);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("Donation ID,Donor ID,Amount,Date,Payment Method,Receipt Number,Donation Type,Notes\n");
                writer.flush();
                System.out.println("Created Donation.csv with headers.");
            } catch (IOException e) {
                System.out.println("Error creating Donation.csv: " + e.getMessage());
            }
        }
    }

    private int loadHighestDonationCounter() {
        int maxCounter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(DONATION_CSV_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {

                String id = line.split(",")[0];
                if (id.startsWith("DO")) {
                    int idNumber = Integer.parseInt(id.substring(2));
                    if (idNumber > maxCounter) {
                        maxCounter = idNumber;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
        return maxCounter;
    }

    public String generateDonationId() {
        donationCounter++;
        return String.format("DO%03d", donationCounter);
    }

    public String generateReceiptNumber() {
        Random random = new Random();
        int number = random.nextInt(1000);
        return String.format("RN%03d", number);
    }

    // Method to add a donation
    public void addDonation(Donation donation) {
        // Add donation to the HashMap
        ListInterface<Donation> donations = donationHashMap.getValue(donation.getDonationId());
        if (donations == null) {
            donations = new LinkedList<>();
            donationHashMap.add(donation.getDonationId(), donations);
        }
        donations.add(donation);

        // Add donation to the TreeMap
        Date date = donation.getDate();
        if (!donationTreeMap.containsKey(date)) {
            donationTreeMap.put(date, new LinkedList<>());
        }
        donationTreeMap.get(date).add(donation);

        // Add donation to the LinkedList
        donationLinkedList.add(donation);

        // Write the donation to the CSV file
        ListInterface<String> headers = new LinkedList<>();
        headers.add("Donation ID");
        headers.add("Donor ID");
        headers.add("Amount");
        headers.add("Date");
        headers.add("Payment Method");
        headers.add("Receipt Number");
        headers.add("Donation Type");
        headers.add("Notes");

        ListInterface<Donation> donationList = new LinkedList<>();
        donationList.add(donation);

        try {
            writeDataToCSV(DONATION_CSV_PATH, headers, donationList, this::mapDonationToRow);
        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        }

        // Update donor's total amount and donation times
        updateDonorDetails(donation.getDonorId(), donation.getAmount());
    }

    // Method to update donor's total amount and donation times
    private void updateDonorDetails(String donorId, double donationAmount) {
        // Load the existing donors from CSV
        FileDao<Donor> fileDao = new FileDao<>();
        ListInterface<Donor> donors = fileDao.loadDataFromCSV("donorData.csv", this::mapRowToDonor);

        // Find the donor with the given donorId
        Donor donor = donors.stream()
                .filter(d -> d.getDonorId().equals(donorId))
                .findFirst()
                .orElse(null);

        if (donor != null) {
            // Update the total amount
            double currentTotalAmount = Double.parseDouble(donor.getTotalAmount());
            double newTotalAmount = currentTotalAmount + donationAmount;
            donor.setTotalAmount(String.format("%.2f", newTotalAmount));

            // Increment the donation times
            int currentDonationTimes = Integer.parseInt(donor.getDonorTimes());
            int newDonationTimes = currentDonationTimes + 1;
            donor.setDonorTimes(String.valueOf(newDonationTimes));

            ListInterface<String> headers = new LinkedList<>();
            headers.add("ID");
            headers.add("Name");
            headers.add("Contact Number");
            headers.add("Email");
            headers.add("Address");
            headers.add("Donor Type");
            headers.add("Donation Preference");
            headers.add("Donation Times");
            headers.add("Total Amount(RM)");

            fileDao.writeDataToCSV("donorData.csv", headers, donors, this::mapDonorToRow);
            System.out.println("Donor details updated successfully.");
        } else {
            System.out.println("Donor with ID " + donorId + " not found.");
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

    // Method to map a Donation object to a CSV row
    private ListInterface<String> mapDonationToRow(Donation donation) {
        ListInterface<String> row = new LinkedList<>();
        row.add(donation.getDonationId());
        row.add(donation.getDonorId());
        row.add(String.format("%.2f", donation.getAmount()));
        row.add(new SimpleDateFormat("yyyy-MM-dd").format(donation.getDate()));
        row.add(donation.getPaymentMethod());
        row.add(donation.getReceiptNumber());
        row.add(donation.getDonationType() != null ? donation.getDonationType().toString() : "");
        row.add(donation.getNotes());
        return row;
    }

    // Method to write data to a CSV file
    public <T> void writeDataToCSV(String fileName, ListInterface<String> headers, ListInterface<T> data,
            Function<T, ListInterface<String>> mapper) throws IOException {
        File file = new File(fileName);
        boolean append = file.exists(); // Append if file already exists

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, append))) {
            if (!append) {
                // Write headers if creating a new file
                writer.write(String.join(",", headers));
                writer.newLine();
            }

            // Write data rows
            for (T item : data) {
                ListInterface<String> row = mapper.apply(item);
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }

    // Method to retrieve donations by donation ID
    public ListInterface<Donation> getDonationsById(String donationId) {
        ListInterface<Donation> donations = new LinkedList<>();
        for (Donation donation : donationLinkedList) {
            if (donation.getDonationId().equals(donationId)) {
                donations.add(donation);
            }
        }
        return donations;
    }

    // Method to get donations by donor ID
    public ListInterface<Donation> getDonationsByDonorId(String donorId) {
        ListInterface<Donation> donations = donationHashMap.getValue(donorId);
        if (donations == null) {
            donations = new LinkedList<>();
        }
        return donations;
    }

    // Method to retrieve all donations
    public ListInterface<Donation> getAllDonations() {
        // Load the existing donations from CSV
        FileDao<Donation> fileDao = new FileDao<>();
        donationLinkedList = fileDao.loadDataFromCSV("Donation.csv", this::mapRowToDonation);
        return donationLinkedList;
    }

    // Mapping function to convert a CSV row to a Donation object
    private Donation mapRowToDonation(String[] values) {
        if (values.length < 8) {
            throw new IllegalArgumentException("CSV row does not contain enough values to map to Donation.");
        }

        String donationId = values[0];
        String donorId = values[1];
        double amount = Double.parseDouble(values[2]);
        Date date = parseDate(values[3]);
        String paymentMethod = values[4];
        String receiptNumber = values[5];
        DonationType donationType = DonationType.valueOf(values[6]); // Assuming DonationType is an enum
        String notes = values[7];

        return new Donation(donationId, donorId, amount, date, paymentMethod, receiptNumber, donationType, notes);
    }

    public ListInterface<Donation> getDonationsByDonorIdFromCSV(String donorId) {
        FileDao<Donation> fileDao = new FileDao<>();
        ListInterface<Donation> allDonations = fileDao.loadDataFromCSV(DONATION_CSV_PATH, this::mapRowToDonation);
        ListInterface<Donation> filteredDonations = new LinkedList<>();

        for (Donation donation : allDonations) {
            if (donation.getDonorId().equals(donorId)) {
                filteredDonations.add(donation);
            }
        }

        return filteredDonations;
    }

    public void displayDonationsForDonor(String donorId, ListInterface<Donation> donationList) {
        System.out.println("\n--- Donation List for Donor ID: " + donorId + " ---");
        System.out.printf("%-12s %-10s %-10s %-20s %-15s %-15s %-10s %-30s%n",
                "Donation ID", "Donor ID", "Amount", "Date", "Payment Method",
                "Receipt No.", "Donation Type", "Notes");
        System.out.println(
                "------------------------------------------------------------------------------------------------" +
                        "-------------------------------");

        for (Donation donation : donationList) {
            System.out.printf("%-12s %-10s %-10.2f %-20s %-15s %-15s %-10s %-30s%n",
                    donation.getDonationId(),
                    donation.getDonorId(),
                    donation.getAmount(),
                    new SimpleDateFormat("yyyy-MM-dd").format(donation.getDate()),
                    donation.getPaymentMethod(),
                    donation.getReceiptNumber(),
                    donation.getDonationType(),
                    donation.getNotes());
        }
    }

    // ------------------------------------------------------------------------------------------------
    // sort part

    private ListInterface<Donation> mergeSort(ListInterface<Donation> list, Comparator<Donation> comparator) {
        if (list.size() <= 1) {
            return list;
        }

        int middle = list.size() / 2;
        ListInterface<Donation> left = new LinkedList<>();
        ListInterface<Donation> right = new LinkedList<>();

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

    private ListInterface<Donation> merge(ListInterface<Donation> left, ListInterface<Donation> right,
            Comparator<Donation> comparator) {
        ListInterface<Donation> result = new LinkedList<>();

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

    // Sorting methods
    public ListInterface<Donation> sortDonationsByDate(ListInterface<Donation> donations, boolean ascending) {
        Comparator<Donation> comparator = Comparator.comparing(Donation::getDate);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return mergeSort(donations, comparator);
    }

    public ListInterface<Donation> sortDonationsByAmount(ListInterface<Donation> donations, boolean ascending) {
        Comparator<Donation> comparator = Comparator.comparing(Donation::getAmount);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return mergeSort(donations, comparator);
    }

    // ------------------------------------------------------------------------------------------------

    // filter
    public ListInterface<Donation> filterDonationsByDate(ListInterface<Donation> donations, Date startDate,
            Date endDate) {
        ListInterface<Donation> result = new LinkedList<>();
        for (Donation donation : donations) {
            Date date = donation.getDate();
            if ((date.after(startDate) || date.equals(startDate)) &&
                    (date.before(endDate) || date.equals(endDate))) {
                result.add(donation);
            }
        }
        return result;
    }

    public ListInterface<Donation> filterDonationsByDonationType(ListInterface<Donation> donations,
            DonationType donationType) {
        ListInterface<Donation> result = new LinkedList<>();
        for (Donation donation : donations) {
            if (donation.getDonationType() == donationType) {
                result.add(donation);
            }
        }
        return result;
    }

    public ListInterface<Donation> filterDonationsByAmount(ListInterface<Donation> donations, double minAmount,
            double maxAmount) {
        ListInterface<Donation> result = new LinkedList<>();
        for (Donation donation : donations) {
            double amount = donation.getAmount();
            if (amount >= minAmount && amount <= maxAmount) {
                result.add(donation);
            }
        }
        return result;
    }

    public ListInterface<Donation> filterDonationsByPaymentMethod(ListInterface<Donation> donations,
            String paymentMethod) {
        ListInterface<Donation> result = new LinkedList<>();
        for (Donation donation : donations) {
            if (donation.getPaymentMethod().equalsIgnoreCase(paymentMethod)) {
                result.add(donation);
            }
        }
        return result;
    }

    public void displayFilteredDonations(ListInterface<Donation> donations) {
        System.out.println("\n--- Filtered Donation List ---");
        System.out.printf("%-12s %-10s %-10s %-20s %-15s %-15s %-10s %-30s%n",
                "Donation ID", "Donor ID", "Amount", "Date", "Payment Method",
                "Receipt No.", "Donation Type", "Notes");
        System.out.println(
                "------------------------------------------------------------------------------------------------" +
                        "-------------------------------");
    
        for (Donation donation : donations) {
            System.out.printf("%-12s %-10s %-10.2f %-20s %-15s %-15s %-10s %-30s%n",
                    donation.getDonationId(),
                    donation.getDonorId(),
                    donation.getAmount(),
                    new SimpleDateFormat("yyyy-MM-dd").format(donation.getDate()),
                    donation.getPaymentMethod(),
                    donation.getReceiptNumber(),
                    donation.getDonationType(),
                    donation.getNotes());
        }
    }


    // ------------------------------------------------------------------------------------------------

    // Method to find donations by Date Range
    public ListInterface<Donation> getDonationsByDateRange(Date startDate, Date endDate) {
        ListInterface<Donation> result = new LinkedList<>();

        // Assuming TreeMapInterface has a method entries() returning CustomEntry<Date,
        // ListInterface<Donation>>
        for (TreeMapInterface.CustomEntry<Date, ListInterface<Donation>> entry : donationTreeMap.entries()) {
            Date date = entry.getKey();
            if ((date.after(startDate) || date.equals(startDate)) &&
                    (date.before(endDate) || date.equals(endDate))) {
                result.addAll(entry.getValue());
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------------------------------

    // Method to delete a donation
    public void deleteDonation(String donationId) {
        // Load the existing donations from CSV
        FileDao<Donation> fileDao = new FileDao<>();
        donationLinkedList = fileDao.loadDataFromCSV("Donation.csv", this::mapRowToDonation);

        // Find the donation with the given donationId
        Donation donationToDelete = donationLinkedList.stream()
                .filter(d -> d.getDonationId().equals(donationId))
                .findFirst()
                .orElse(null);

        if (donationToDelete != null) {
            System.out.println("Deleting donation: " + donationId);

            // Update the donor details after deleting the donation
            updateDeletedDetails(donationToDelete.getDonorId(), donationToDelete.getAmount());

            // Remove the donation from the list
            donationLinkedList.remove(donationToDelete);

            // Reassign donation IDs
            reassignDonationIds();

            // Write the updated donation list back to CSV
            ListInterface<String> headers = new LinkedList<>();
            headers.add("Donation ID");
            headers.add("Donor ID");
            headers.add("Amount");
            headers.add("Date");
            headers.add("Payment Method");
            headers.add("Receipt Number");
            headers.add("Donation Type");
            headers.add("Notes");

            fileDao.writeDataToCSV("Donation.csv", headers, donationLinkedList, this::mapDonationToRow);
            System.out.println("Donation deleted successfully.");
        } else {
            System.out.println("Donation with ID " + donationId + " not found.");
        }
    }

    private void updateDeletedDetails(String donorId, double donationAmount) {
        // Load the existing donors from CSV
        FileDao<Donor> fileDao = new FileDao<>();
        ListInterface<Donor> donors = fileDao.loadDataFromCSV("donorData.csv", this::mapRowToDonor);

        // Find the donor with the given donorId
        Donor donor = donors.stream()
                .filter(d -> d.getDonorId().equals(donorId))
                .findFirst()
                .orElse(null);

        if (donor != null) {
            System.out.println("Updating donor: " + donorId);
            // Update the total amount by subtracting the donation amount
            double currentTotalAmount = Double.parseDouble(donor.getTotalAmount());
            double newTotalAmount = currentTotalAmount - donationAmount;
            donor.setTotalAmount(String.format("%.2f", newTotalAmount));

            // Decrement the donation times
            int currentDonationTimes = Integer.parseInt(donor.getDonorTimes());
            int newDonationTimes = currentDonationTimes - 1;
            donor.setDonorTimes(String.valueOf(newDonationTimes));

            // Write updated donor details back to CSV
            ListInterface<String> headers = new LinkedList<>();
            headers.add("ID");
            headers.add("Name");
            headers.add("Contact Number");
            headers.add("Email");
            headers.add("Address");
            headers.add("Donor Type");
            headers.add("Donation Preference");
            headers.add("Donation Times");
            headers.add("Total Amount(RM)");

            fileDao.writeDataToCSV("donorData.csv", headers, donors, this::mapDonorToRow);
            System.out.println("Donor details updated successfully.");
        } else {
            System.out.println("Donor with ID " + donorId + " not found.");
        }
    }

    // Method to reassign donation IDs
    private void reassignDonationIds() {
        int newIdCounter = 1;
        for (int i = 0; i < donationLinkedList.size(); i++) {
            Donation donation = donationLinkedList.get(i);
            String newId = String.format("D%03d", newIdCounter);
            donation.setDonationId(newId);
            newIdCounter++;
        }
    }

    public void clearAllDonations() {
        // Initialize variables to keep track of total amount and donation count per
        // donor
        HashMapInterface<String, Double> donorTotalAmounts = new HashMapImplementation<>();
        HashMapInterface<String, Integer> donorDonationCounts = new HashMapImplementation<>();

        // Accumulate donation data
        for (Donation donation : donationLinkedList) {
            String donorId = donation.getDonorId();
            double donationAmount = donation.getAmount();

            // Calculate total donation amount and count per donor
            donorTotalAmounts.put(donorId, donorTotalAmounts.getOrDefault(donorId, 0.0) + donationAmount);
            donorDonationCounts.put(donorId, donorDonationCounts.getOrDefault(donorId, 0) + 1);
        }

        // Print accumulated totals for debugging
        System.out.println("\n--- Total Donations and Counts ---");
        System.out.println("Donor ID   | Total Amount (RM) | Donation Count");
        System.out.println("-----------------------------------------------");
        for (HashMapInterface.Entry<String, Double> entry : donorTotalAmounts.entrySet()) {
            String donorId = entry.getKey();
            double totalAmountToSubtract = entry.getValue();
            int donationCountToSubtract = donorDonationCounts.get(donorId);
            System.out.printf("%-10s | %.2f            | %d%n", donorId, totalAmountToSubtract,
                    donationCountToSubtract);
        }

        // Clear all entries from the data structures
        donationHashMap.clear();
        donationTreeMap.clear();
        donationLinkedList.clear();

        // Clear the donation CSV file
        clearDonationCSV();

        // Update donor details by subtracting the accumulated amounts and counts
        for (HashMapInterface.Entry<String, Double> entry : donorTotalAmounts.entrySet()) {
            String donorId = entry.getKey();
            double totalAmountToSubtract = entry.getValue();
            int donationCountToSubtract = donorDonationCounts.get(donorId);

            // Update donor details
            clearDetails(donorId, totalAmountToSubtract, donationCountToSubtract);
        }
    }

    private void clearDetails(String donorId, double totalAmountToSubtract, int donationCountToSubtract) {
        // Load the existing donors from CSV
        FileDao<Donor> fileDao = new FileDao<>();
        ListInterface<Donor> donors = fileDao.loadDataFromCSV("donorData.csv", this::mapRowToDonor);

        // Find the donor with the given donorId
        Donor donor = donors.stream()
                .filter(d -> d.getDonorId().equals(donorId))
                .findFirst()
                .orElse(null);

        if (donor != null) {
            // Update the total amount by subtracting the accumulated donation amount
            double currentTotalAmount = Double.parseDouble(donor.getTotalAmount());
            double newTotalAmount = currentTotalAmount - totalAmountToSubtract;
            donor.setTotalAmount(String.format("%.2f", newTotalAmount));

            // Decrement the donation times
            int currentDonationTimes = Integer.parseInt(donor.getDonorTimes());
            int newDonationTimes = currentDonationTimes - donationCountToSubtract;
            donor.setDonorTimes(String.valueOf(newDonationTimes));

            // Write updated donor details back to CSV
            ListInterface<String> headers = new LinkedList<>();
            headers.add("ID");
            headers.add("Name");
            headers.add("Contact Number");
            headers.add("Email");
            headers.add("Address");
            headers.add("Donor Type");
            headers.add("Donation Preference");
            headers.add("Donation Times");
            headers.add("Total Amount(RM)");

            fileDao.writeDataToCSV("donorData.csv", headers, donors, this::mapDonorToRow);
            System.out.println("\nDonor details updated successfully for donor ID: " + donorId);
            System.out.println("All donations cleared.");
        } else {
            System.out.println("Donor with ID " + donorId + " not found.");
        }
    }

    private void clearDonationCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Donation.csv", false))) {
            // Write headers
            ListInterface<String> headers = new LinkedList<>();
            headers.add("Donation ID");
            headers.add("Donor ID");
            headers.add("Amount");
            headers.add("Date");
            headers.add("Payment Method");
            headers.add("Receipt Number");
            headers.add("Donation Type");
            headers.add("Notes");
            writer.write(String.join(",", headers));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error clearing Donation.csv: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------------------------------------
    // report
    public void generateDonationSummaryReport() {
        // Load the existing donations from CSV
        FileDao<Donation> fileDao = new FileDao<>();
        ListInterface<Donation> donations = fileDao.loadDataFromCSV("Donation.csv", this::mapRowToDonation);

        double totalAmountDonated = 0;
        int totalDonations = donations.size();
        HashMapInterface<String, Integer> donorDonationCount = new HashMapImplementation<>();
        HashMapInterface<String, Double> donorDonationAmount = new HashMapImplementation<>();

        // Calculate total amounts and donations for each donor
        for (int i = 0; i < donations.size(); i++) {
            Donation donation = donations.get(i);
            double amount = donation.getAmount();
            totalAmountDonated += amount;

            String donorId = donation.getDonorId();

            // Ensure getOrDefault and put methods work properly in your custom map
            // implementation
            int currentCount = donorDonationCount.containsKey(donorId) ? donorDonationCount.get(donorId) : 0;
            donorDonationCount.put(donorId, currentCount + 1);

            double currentAmount = donorDonationAmount.containsKey(donorId) ? donorDonationAmount.get(donorId) : 0.0;
            donorDonationAmount.put(donorId, currentAmount + amount);
        }

        double averageDonationAmount = (totalDonations > 0) ? totalAmountDonated / totalDonations : 0;

        // Displaying the summary report
        System.out.println("\n==============================");
        System.out.println("  Donation Summary Report");
        System.out.println("==============================");
        System.out.printf(" Total Donations: %d%n", totalDonations);
        System.out.printf(" Total Amount Donated: RM %.2f%n", totalAmountDonated);
        System.out.printf(" Average Donation Amount: RM %.2f%n", averageDonationAmount);
        System.out.println("==============================");
        System.out.println(" Donations by Donor:");
        System.out.println("==============================");

        // Correct iteration over the custom HashMapInterface
        for (HashMapInterface.Entry<String, Integer> entry : donorDonationCount.entrySet()) {
            String donorId = entry.getKey();
            int totalDonorDonations = entry.getValue();
            double totalDonorAmount = donorDonationAmount.get(donorId);

            System.out.printf(" Donor ID: %s | Total Donations: %d | Total Amount: RM %.2f%n",
                    donorId, totalDonorDonations, totalDonorAmount);
        }
        System.out.println("==============================");
    }

    // ------------------------------------------------------------------------------------------------
    // Method to display donations
    public void displayDonations() {
        System.out.println("Donations in HashMap:");
        for (String id : donationHashMap.getKeys()) {
            System.out.println(donationHashMap.getValue(id));
        }

        System.out.println("Donations in TreeMap (by Date):");
        for (TreeMapInterface.CustomEntry<Date, ListInterface<Donation>> entry : donationTreeMap.entries()) {
            System.out.println("Date: " + entry.getKey());
            for (Donation donation : entry.getValue()) {
                System.out.println(donation);
            }
        }

        System.out.println("Donations in LinkedList (by Insertion Order):");
        for (Donation donation : donationLinkedList) {
            System.out.println(donation);
        }
    }

    // Method to load donor IDs from a CSV file
    public void loadDonorIds(String filePath) {
        FileDao<Donor> fileDao = new FileDao<>();
        ListInterface<Donor> donors = fileDao.loadDataFromCSV(filePath, this::mapRowToDonor);

        for (Donor donor : donors) {
            System.out.println("Donor ID: " + donor.getDonorId());
        }
    }

    // Mapping function to convert a CSV row to a Donor object
    private Donor mapRowToDonor(String[] values) {
        if (values.length < 9) {
            throw new IllegalArgumentException("CSV row does not contain enough values to map to Donor.");
        }

        String donorId = values[0];
        String name = values[1];
        String contactNumber = values[2];
        String email = values[3];
        String address = values[4];
        String donorType = values[5];
        String donationPreference = values[6];
        String donorTimes = values[7];
        String totalAmount = values[8];

        return new Donor(donorId, name, contactNumber, email, address, donorType, donationPreference, donorTimes,
                totalAmount);
    }

    // Helper method to parse date
    public Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateString);
        } catch (Exception e) {
            System.err.println("Invalid date format. Using current date.");
            return new Date();
        }
    }

    // Method to check if donor ID exists in CSV
    public static boolean donorIdExistsInCSV(String donorId, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length > 0 && fields[0].trim().equals(donorId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the CSV file: " + e.getMessage());
        }
        return false;
    }

}
