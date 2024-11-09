//Author : Yew Wen Kang
package control;

import ADT.HashMapImplementation;
import ADT.HashMapInterface;
import ADT.LinkedList;
import ADT.LinkedStack;
import ADT.ListInterface;
import ADT.StackInterface;
import DAO.DoneeDAO;
import boundary.DoneeMaintenanceUI;
import entity.Distribution;
import entity.Donation;
import entity.Donee;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import utility.MessageUI;
import utility.ValidationUI;

public class DoneeMaintenance {

    private ListInterface<Donee> doneeList = new LinkedList<>();
    private DoneeMaintenanceUI doneeUI = new DoneeMaintenanceUI();
    private static final String FILE_NAME = "doneeData.csv";
    private final String headers = "ID,Name,Address,Phone Number,Email,Donee Type,Organization Name";
    private int nextId;
    private StackInterface<Command> undoStack = new LinkedStack<>();
    private StackInterface<Command> redoStack = new LinkedStack<>();

    DonationManager manager = new DonationManager();

    //Constructor
    public DoneeMaintenance() throws ParseException {
        loadDoneeData();
        this.nextId = calculateNextId(); // Initialize nextId based on existing IDs
    }

    // Load Donee From File
    private void loadDoneeData() {
        DoneeDAO.loadFromFile(FILE_NAME, doneeList, line -> {
            // Split on commas not inside quotes
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            // Remove extra quotes around fields if necessary
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].replaceAll("^\"|\"$", "").trim();
            }

            String id = parts[0];
            String name = parts[1];
            String address = parts[2];
            String phoneNumber = parts[3];
            String email = parts[4];
            String doneeType = parts[5];
            String organizationName = parts.length > 6 ? parts[6] : "";

            return new Donee(id, name, address, phoneNumber, email, doneeType, organizationName);
        });
    }

    //Save donee data to file
    public void saveDoneeData() {
        DoneeDAO.saveToFile(FILE_NAME, doneeList, headers);
    }

    //Handling the Donee registration input and also validation
    public Donee inputDoneeDetails() {
        doneeUI.printBox("Donee Registration");
        String doneeId = generateDoneeId();
        String doneeName;
        String doneeAddress;
        String doneePhoneNumber;
        String doneeEmail;
        String doneeType;
        String doneeOrganizationName = "";

        // Input and validation for Donee Name
        doneeName = validateName();

        // Input and validation for Donee Address
        doneeAddress = validateAddress();

        // Input and validation for Donee Phone Number
        doneePhoneNumber = validatePhoneNumber();

        // Input and validation for Donee Email
        doneeEmail = validateEmail();

        // Input and validation for Donee Type
        doneeType = validateDoneeType();

        //Input and validation for Organization name if the doneeType is Organization
        doneeOrganizationName = validateOrganizationName(doneeType);

        return new Donee(doneeId, doneeName, doneeAddress, doneePhoneNumber, doneeEmail, doneeType, doneeOrganizationName);
    }

    //Use to generate Donee ID by following the format DE000
    private String generateDoneeId() {
        String prefix = "DE";
        String numericPart = String.format("%03d", nextId);
        nextId++; // Increment for the next donee
        return prefix + numericPart;
    }

    //Menu Choice
    //Choice 1 Add new Donee
    public void registerNewDonee() {
        Donee newDonee = inputDoneeDetails();

        Command addCommand = new AddDoneeCommand(doneeList, newDonee);
        addCommand.execute();
        undoStack.push(addCommand);
        redoStack.clear(); // Clear redo stack after new action
    }

    //Choice 2 Remove Donee
    public void removeDonee() {
        String idToRemove = doneeUI.inputRemovedDoneeID();

        boolean removed = false;
        for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
            Donee donee = doneeList.getEntry(i);
            if (donee.getId().equalsIgnoreCase(idToRemove)) {
                //undo
                Command removeCommand = new RemoveDoneeCommand(doneeList, donee);
                removeCommand.execute();
                undoStack.push(removeCommand);
                redoStack.clear(); // Clear redo stack after new action

                removed = true;
                System.out.println("Donee " + idToRemove + " has been removed.");

                break;
            }
        }

        if (!removed) {
            System.out.println("Donee " + idToRemove + " not found.");
        }
    }

    //Choice 3 Update Donee Details
    public void updateDoneeDetails() {
        // Display all donees
        printAllDonees();

        String idToUpdate = doneeUI.inputDoneeIdUpdate();

        // Search for the donee with the specified ID
        for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
            Donee donee = doneeList.getEntry(i);
            if (donee.getId().equalsIgnoreCase(idToUpdate)) {
                // Capture the original state of the donee
                Donee originalDonee = new Donee(donee.getId(), donee.getName(), donee.getAddress(), donee.getPhoneNumber(), donee.getEmail(), donee.getDoneeType(), donee.getOrganizationName());

                boolean keepUpdating = true;
                while (keepUpdating) {
                    String choice = validateUpdateDoneeChoice();

                    switch (choice) {
                        case "1":
                            String updatedName = validateName();
                            donee.setName(updatedName);
                            System.out.println("Name updated successfully.");
                            break;
                        case "2":
                            String updatedAddress = doneeUI.inputDoneeAddress();
                            donee.setAddress(updatedAddress);
                            System.out.println("Address updated successfully.");
                            break;
                        case "3":
                            String updatedPhoneNumber = doneeUI.inputDoneePhoneNumber();
                            donee.setPhoneNumber(updatedPhoneNumber);
                            System.out.println("Phone number updated successfully.");
                            break;
                        case "4":
                            String updatedEmail = doneeUI.inputDoneeEmail();
                            donee.setEmail(updatedEmail);
                            System.out.println("Email updated successfully.");
                            break;
                        case "5":
                            String updatedDoneeType = doneeUI.inputDoneeType();
                            if (updatedDoneeType.equalsIgnoreCase("Y")) {
                                donee.setDoneeType("Organization");
                                System.out.println("Donee type updated to Organization.");
                            } else {
                                donee.setDoneeType("Individual");
                                donee.setOrganizationName(""); // Clear organization name if changing to Individual
                                System.out.println("Donee type updated to Individual.");
                            }
                            break;
                        case "6":
                            if (donee.getDoneeType().equalsIgnoreCase("Organization")) {
                                String updatedOrganizationName = doneeUI.inputDoneeOrganizationName();
                                donee.setOrganizationName(updatedOrganizationName);
                                System.out.println("Organization name updated successfully.");
                            } else {
                                System.out.println("This donee is not an organization. Cannot update Organization Name.");
                            }
                            break;
                        case "0":
                            keepUpdating = false;
                            System.out.println("Finished updating donee.");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                }
                doneeList.replace(i, donee);
                Command updateCommand = new UpdateDoneeCommand(doneeList, originalDonee, donee);
                undoStack.push(updateCommand);
                redoStack.clear(); // Clear redo stack after new action
                return;
            }
        }

        System.out.println("Donee with ID " + idToUpdate + " not found.");
    }

    //choice 4 Search Donee by ID , and allow the donee to update details
    public void searchDoneeDetails() {
        String idToSearch = doneeUI.inputDoneeIdToSearch();
        Donee target = new Donee(idToSearch, "", "", "", "", "", ""); // Create Donee with the given ID

        // Perform the linear search
        Donee result = doneeList.linearSearch(target);

        // Print the result
        if (result != null) {
            System.out.println("Found donee: " + result.getId());
            System.out.println();
            doneeUI.printHeader();
            doneeUI.printDoneeDetailsRow(result);
            System.out.println();

            // Ask user if they want to update the donee
            String updateChoice = doneeUI.askUpdateDonee();

            if (updateChoice.equalsIgnoreCase("y")) {
                boolean keepUpdating = true;
                while (keepUpdating) {
                    String choice = validateUpdateDoneeChoice();

                    switch (choice) {
                        case "1":
                            String updatedName = validateName();
                            result.setName(updatedName);
                            System.out.println("Name updated successfully.");
                            break;
                        case "2":
                            String updatedAddress = validateAddress();
                            result.setAddress(updatedAddress);
                            System.out.println("Address updated successfully.");
                            break;
                        case "3":
                            String updatedPhoneNumber = validatePhoneNumber();
                            result.setPhoneNumber(updatedPhoneNumber);
                            System.out.println("Phone number updated successfully.");
                            break;
                        case "4":
                            String updatedEmail = validateEmail();
                            result.setEmail(updatedEmail);
                            System.out.println("Email updated successfully.");
                            break;
                        case "5":
                            String updatedDoneeType = validateDoneeType();
                            if (updatedDoneeType.equalsIgnoreCase("Organization")) {
                                result.setDoneeType("Organization");
                                String updateOrganizationName = validateOrganizationName(updatedDoneeType);
                                result.setOrganizationName(updateOrganizationName);
                                System.out.println("Donee type updated to Organization.");
                            } else {
                                result.setDoneeType("Individual");
                                result.setOrganizationName(""); // Clear organization name if changing to Individual
                                System.out.println("Donee type updated to Individual.");
                            }
                            break;
                        case "6":
                            if (result.getDoneeType().equalsIgnoreCase("Organization")) {
                                String updatedOrganizationName = validateOrganizationName(result.getDoneeType());
                                result.setOrganizationName(updatedOrganizationName);
                                System.out.println("Organization name updated successfully.");
                            } else {
                                System.out.println("This donee is not an organization. Cannot update Organization Name.");
                            }
                            break;
                        case "0":
                            keepUpdating = false;
                            System.out.println("Finished updating donee.");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                }
                // Replace the donee in the list
                for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
                    if (doneeList.getEntry(i).getId().equalsIgnoreCase(idToSearch)) {
                        doneeList.replace(i, result);
                        break;
                    }
                }
            }
        } else {
            System.out.println("Donee with ID " + target.getId() + " not found.");
        }
    }

    //choice 5 Menu for distribution
    public void aidMenu() {
        String aidChoice;
        do {
            Donation.DonationType donationType = null;
            aidChoice = doneeUI.getDonationMenuChoice();
            switch (aidChoice) {
                case "1":
                    donationType = Donation.DonationType.FOOD;
                    break;
                case "2":
                    donationType = Donation.DonationType.DAILY_EXPENSES;
                    break;
                case "3":
                    donationType = Donation.DonationType.CASH;
                    break;
                case "4":
                    manager.printAvailableDonations();
                    aidChoice = "";
                    break;
                case "0":
                    break;
                default:
                    MessageUI.displayInvalidChoiceMessage();
                    break;
            }
            if (donationType != null) {
                String doneeID = doneeUI.inputDoneeID();
                Donee target = new Donee(doneeID, "", "", "", "", "", ""); // Create Donee with the given ID
                double doubleAmount;

                // Perform the linear search
                Donee result = doneeList.linearSearch(target);
                if (result != null) {
                    doubleAmount = validateDistributionAmount();

                    manager.distributeDonation(result.getId(), doubleAmount, new Date(), donationType);

                } else {
                    System.out.println("\nDonee not found!");
                }
            }

        } while (!aidChoice.equals("0"));

    }

    //choice 6 Filter Donee by Type or Address or No filter
    public void filterDonees() {
        String type = doneeUI.inputDoneeTypeFilter();
        String location = doneeUI.inputDoneeAddressFilter();

        // If both filters are empty, print all donees without filtering
        if (type.isEmpty() && location.isEmpty()) {
            System.out.println("No filters applied. Displaying all donees:");
            System.out.println();
            doneeUI.printHeader();
            for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
                Donee donee = doneeList.getEntry(i);
                doneeUI.printDoneeDetailsRow(donee);
            }
            return; // Exit the method early
        }

        // Filter donees based on the provided criteria
        System.out.println();
        System.out.println();
        System.out.println("Filters Applied.");
        if (!type.isEmpty()) {
            System.out.println("Donee Type : " + type);
        }
        if (!location.isEmpty()) {
            System.out.println("Location : " + location);
        }
        System.out.println();
        doneeUI.printHeader();
        for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
            Donee donee = doneeList.getEntry(i);
            boolean matchesType = type.isEmpty() || donee.getDoneeType().equalsIgnoreCase(type);
            boolean matchesLocation = location.isEmpty() || donee.getAddress().toLowerCase().contains(location.toLowerCase());

            if (matchesType && matchesLocation) {
                doneeUI.printDoneeDetailsRow(donee);
            }
        }
    }

    //choice 7 Report 
    //Donee Summary Report
    public void generateDoneeSummaryReport() {
        int totalDonees = doneeList.getNumberOfEntries();
        int organizationCount = 0;
        int individualCount = 0;

        for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
            Donee donee = doneeList.getEntry(i);
            if (donee.getDoneeType().equalsIgnoreCase("Organization")) {
                organizationCount++;
            } else if (donee.getDoneeType().equalsIgnoreCase("Individual")) {
                individualCount++;
            }
        }

        // Print the summary report
        System.out.println("Donee Summary Report:");
        System.out.println("-----------------------------------------------------");
        System.out.printf("| %-30s | %-16s |\n", "Donee Type", "Total Amount");
        System.out.println("|--------------------------------|------------------|");
        System.out.printf("| %-30s | %-16d |\n", "Total Organizations", organizationCount);
        System.out.printf("| %-30s | %-16d |\n", "Total Individuals", individualCount);
        System.out.println("|---------------------------------------------------|");

        System.out.printf("Total Donees registered : " + totalDonees);
        System.out.println();
        System.out.println();
    }

    //Distribution Summary Report
    public void generateDistributionSummaryReport() {
        double totalFoodAmount = 0;
        double totalCashAmount = 0;
        double totalDailyExpensesAmount = 0;

        for (int i = 1; i <= manager.distributionList.getNumberOfEntries(); i++) {
            Distribution distribution = manager.distributionList.getEntry(i);
            if (distribution.getType().equals("CASH")) {
                totalCashAmount += distribution.getDistributedAmount();
            } else if (distribution.getType().equals("DAILY_EXPENSES")) {
                totalDailyExpensesAmount += distribution.getDistributedAmount();
            } else if (distribution.getType().equals("FOOD")) {
                totalFoodAmount += distribution.getDistributedAmount();
            }
        }

        // Print the summary report
        System.out.println("\n\n-----------------------------------------------------");
        System.out.printf("| %-30s | %-16s |\n", "Distribution Type", "Total Amount");
        System.out.println("|--------------------------------|------------------|");
        System.out.printf("| %-30s | RM %-13.2f |\n", "Cash", totalCashAmount);
        System.out.printf("| %-30s | RM %-13.2f |\n", "Food", totalFoodAmount);
        System.out.printf("| %-30s | RM %-13.2f |\n", "Daily Expenses", totalDailyExpensesAmount);
        System.out.println("|---------------------------------------------------|");
    }

    //Distribution report by each donee
    public void distributionReportByDonee() {
        String donationFilePath = "donationDistribution.csv";
        String doneeFilePath = "doneeData.csv";

        // Arrays to hold donee data
        String[] doneeIDs = new String[100]; // Initial size
        String[] doneeNames = new String[100];
        double[] foodAmounts = new double[100];
        double[] cashAmounts = new double[100];
        double[] dailyExpensesAmounts = new double[100];
        double[] totalAmounts = new double[100];
        int doneeCount = 0;

        // Read donee data
        try (BufferedReader br = new BufferedReader(new FileReader(doneeFilePath))) {
            String line;
            // Skip header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String id = values[0];
                String name = values[1];

                // Store donee data in arrays
                doneeIDs[doneeCount] = id;
                doneeNames[doneeCount] = name;
                doneeCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read donation distribution data and aggregate amounts
        try (BufferedReader br = new BufferedReader(new FileReader(donationFilePath))) {
            String line;
            // Skip header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String recipientID = values[2];
                double distributedAmount = Double.parseDouble(values[3]);
                String type = values[5];

                // Find index of recipientID in donee arrays
                int index = findIndex(doneeIDs, doneeCount, recipientID);
                if (index != -1) {
                    switch (type) {
                        case "FOOD":
                            foodAmounts[index] += distributedAmount;
                            break;
                        case "CASH":
                            cashAmounts[index] += distributedAmount;
                            break;
                        case "DAILY_EXPENSES":
                            dailyExpensesAmounts[index] += distributedAmount;
                            break;
                    }
                    // Update total amount last
                    totalAmounts[index] += distributedAmount;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the report
        System.out.println();
        System.out.println();
        System.out.println("Donation Distribution Summary Report:");
        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-8s | %-20s | %-8s | %-8s | %-14s | %-25s |\n", "Donee ID", "Name", "FOOD", "CASH", "DAILY_EXPENSES", "Total Distributed Amount");
        System.out.println("------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < doneeCount; i++) {
            System.out.printf("| %-8s | %-20s | %-8.2f | %-8.2f | %-14.2f | %-25.2f |\n",
                    doneeIDs[i], doneeNames[i], foodAmounts[i], cashAmounts[i], dailyExpensesAmounts[i], totalAmounts[i]);
        }

        System.out.println("|----------------------------------------------------------------------------------------------------|");
    }

    // Find the index of the doneeID in the array (Use in Distribution Report by each Donee)
    private static int findIndex(String[] array, int size, String key) {
        for (int i = 0; i < size; i++) {
            if (array[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    //Show all donee in List
    public void printAllDonees() {
        System.out.println();
        doneeUI.printHeader();
        for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
            Donee donee = doneeList.getEntry(i);
            doneeUI.printDoneeDetailsRow(donee);
        }
        System.out.println();
    }

    //Use to check what is the next ID
    private int calculateNextId() {
        int maxId = 0;
        for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
            Donee donee = doneeList.getEntry(i);
            int currentId = Integer.parseInt(donee.getId().substring(2)); // Extract numeric part
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        return maxId + 1; // Next available ID
    }

    //Donee Main Function Call
    public void runDoneeMaintenance() {
        String choice = "";
        do {
            choice = doneeUI.getMenuChoice();

            switch (choice) {
                case "0":
                    MessageUI.displayExitMessage();
                    saveDoneeData();
                    break;
                case "1":
                    registerNewDonee();
                    if (!doneeList.isEmpty()) {
                        doneeUI.printDoneeDetails(doneeList.getEntry(doneeList.getNumberOfEntries()));
                    } else {
                        System.out.println("No donees to display.");
                    }
                    break;
                case "2":
                    printAllDonees();
                    removeDonee();
                    break;
                case "3":
                    updateDoneeDetails();
                    break;
                case "4":
                    searchDoneeDetails();

                    break;
                case "5":
                    aidMenu();
                    break;

                case "6":
                    filterDonees();
                    break;
                case "7":
                    String reportChoice;
                    do {
                        reportChoice = doneeUI.getReportMenuChoice();;
                        switch (reportChoice) {
                            case "1":
                                generateDoneeSummaryReport();
                                break;
                            case "2":
                                distributionReportByDonee();
                                generateDistributionSummaryReport();

                                break;
                            case "0":
                                break;
                            default:
                                MessageUI.displayInvalidChoiceMessage();
                        }
                    } while (!reportChoice.equals("0"));

                    break;
                case "8":
                    undo();
                    break;
                case "9":
                    redo();
                    break;
                default:
                    MessageUI.displayInvalidChoiceMessage();
                    if (!ValidationUI.isDigit(choice)) {
                        MessageUI.displayDigitOnlyMessage();
                    } else {
                        System.out.println("Please enter choice within 0 to 9.");
                    }
            }
        } while (!choice.equals("0"));
    }

    //Validation Part
    public String validateName() {
        String name;
        // Input and validation for Donee Name
        do {
            name = doneeUI.inputDoneeName();
            if (ValidationUI.isNotEmpty(name)) {
                break;
            } else {
                System.out.println("Name cannot be empty. Please try again.");
            }
        } while (true);
        return name;
    }

    public String validateAddress() {
        String address;
        // Input and validation for Donee Address
        do {
            address = doneeUI.inputDoneeAddress();
            if (ValidationUI.isNotEmpty(address)) {
                break;
            } else {
                System.out.println("Address cannot be empty. Please try again.");
            }
        } while (true);
        return address;
    }

    public String validatePhoneNumber() {
        String phoneNumber;
        // Input and validation for Donee Phone Number
        do {
            phoneNumber = doneeUI.inputDoneePhoneNumber();
            if (ValidationUI.isValidPhoneNumber(phoneNumber)) {
                break;
            } else if (!ValidationUI.isNotEmpty(phoneNumber)) {
                System.out.println("Phone Number cannot be empty. Please try again.");
            } else {
                System.out.println("Invalid phone number. Please enter a valid phone number starting with '01' and 10 or 11 digits long.");
            }
        } while (true);
        return phoneNumber;
    }

    public String validateEmail() {
        String email;
        // Input and validation for Donee Email
        do {
            email = doneeUI.inputDoneeEmail();
            if (ValidationUI.isValidEmail(email)) {
                break;
            } else {
                System.out.println("Invalid email format. Please enter a valid email address in the format xxx@xxxx.com.");
            }
        } while (true);
        return email;
    }

    public String validateDoneeType() {
        String doneeType;
        // Input and validation for Donee Type
        do {
            doneeType = doneeUI.inputDoneeType();
            if (doneeType.equalsIgnoreCase("Y")) {
                doneeType = "Organization";
                break;
            } else if (doneeType.equalsIgnoreCase("N")) {
                doneeType = "Individual";
                break;
            } else {
                System.out.println("Invalid input(Y/N only).");
            }
        } while (true);

        return doneeType;
    }

    public String validateOrganizationName(String doneeType) {
        String organizationName = "";

        //OrganizationName validate
        if (doneeType.equalsIgnoreCase("Organization")) {
            // Input and validation for Donee Organization Name
            do {
                organizationName = doneeUI.inputDoneeOrganizationName();
                if (ValidationUI.isNotEmpty(organizationName)) {
                    break;
                } else {
                    System.out.println("Organization name cannot be empty. Please try again.");
                }
            } while (true);
        }
        return organizationName;
    }

    //Validation Use in aid menu
    public double validateDistributionAmount() {
        double doubleAmount;
        do {
            String strAmount = doneeUI.inputAmount();
            if (ValidationUI.isNotEmpty(strAmount) && ValidationUI.isValidAmount(strAmount)) {
                doubleAmount = Double.parseDouble(strAmount);
                break;
            } else {
                System.out.println("Please enter valid amount.");
            }
        } while (true);
        return doubleAmount;
    }

    //Validation Use in choice 2
    public String validateUpdateDoneeChoice() {
        do {
            String updateDoneeChoice = doneeUI.getUpdateDoneeChoice();
            if (ValidationUI.isDigit(updateDoneeChoice)) {
                if (Integer.parseInt(updateDoneeChoice) >= 0 && Integer.parseInt(updateDoneeChoice) <= 6) {
                    return updateDoneeChoice;
                }

            } else {
                MessageUI.displayInvalidChoiceMessage();
                System.out.println();
            }
        } while (true);

    }

    // Command interface to ensure if a new command is added, they implementing the three core methods
    public interface Command {

        void execute();

        void undo();

        void redo();
    }

// Command implementation for adding a Donee
    public class AddDoneeCommand implements Command {

        private ListInterface<Donee> doneeList;
        private Donee donee;

        public AddDoneeCommand(ListInterface<Donee> doneeList, Donee donee) {
            this.doneeList = doneeList;
            this.donee = donee;
        }

        @Override
        public void execute() {
            doneeList.add(donee);
        }

        @Override
        public void undo() {
            doneeList.remove(donee);
            System.out.println("Add command has been undo.");
        }

        @Override
        public void redo() {
            System.out.println("Add command has been redo.");
            doneeList.add(donee);
        }
    }

// Command implementation for removing a Donee
    public class RemoveDoneeCommand implements Command {

        private ListInterface<Donee> doneeList;
        private Donee donee;

        public RemoveDoneeCommand(ListInterface<Donee> doneeList, Donee donee) {
            this.doneeList = doneeList;
            this.donee = donee;
        }

        @Override
        public void execute() {
            doneeList.remove(donee);
        }

        @Override
        public void undo() {
            doneeList.add(donee);
            System.out.println("Remove command has been undo.");
        }

        @Override
        public void redo() {
            doneeList.remove(donee);
            System.out.println("Remove command has been undo.");
        }
    }

    // Command implementation for removing a Donee
    public class UpdateDoneeCommand implements Command {

        private ListInterface<Donee> doneeList;
        private Donee originalDonee;
        private Donee updatedDonee;

        public UpdateDoneeCommand(ListInterface<Donee> doneeList, Donee originalDonee, Donee updatedDonee) {
            this.doneeList = doneeList;
            this.originalDonee = originalDonee;
            this.updatedDonee = updatedDonee;
        }

        @Override
        public void execute() {
            // Apply the update
            updateDonee(updatedDonee);
            System.out.println("Update command has been undo.");
        }

        @Override
        public void undo() {
            // Revert to the original state
            updateDonee(originalDonee);
            System.out.println("Update command has been undo.");
        }

        @Override
        public void redo() {
            // Reapply the update
            updateDonee(updatedDonee);
        }

        private void updateDonee(Donee donee) {
            for (int i = 1; i <= doneeList.getNumberOfEntries(); i++) {
                if (doneeList.getEntry(i).getId().equals(donee.getId())) {
                    doneeList.replace(i, donee);
                    break;
                }
            }
        }
    }

    //Undo use in Main
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        } else {
            System.out.println("No actions to undo.");
        }
    }

    //Redo use in Main
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.redo();
            undoStack.push(command);
        } else {
            System.out.println("No actions to redo.");
        }
    }

    //Use to manage the distribution
    public static class DonationManager {

        private LinkedList<Donation> donationList;
        private LinkedList<Distribution> distributionList;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private DoneeMaintenanceUI doneeUI = new DoneeMaintenanceUI();

        public DonationManager() throws ParseException {
            this.donationList = new LinkedList<>();
            this.distributionList = new LinkedList<>();
            loadDonations();
            loadDistributions();
        }

        // Method to load donations from donation.csv
        public void loadDonations() throws ParseException {

            try (Scanner scanner = new Scanner(new File("Donation.csv"))) {
                // Skip the header line
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
                while (scanner.hasNextLine()) {
                    String[] data = scanner.nextLine().split(",");
                    String donationID = data[0];
                    String donorID = data[1];
                    double amount = Double.parseDouble(data[2]);
                    Date date;
                    try {
                        date = dateFormat.parse(data[3]);
                    } catch (ParseException e) {
                        System.err.println("Error parsing date: " + e.getMessage());
                        continue; // Skip this line and continue with the next
                    }
                    String paymentMethod = data[4];
                    String receiptNumber = data[5];
                    Donation.DonationType donationType;
                    try {
                        donationType = Donation.DonationType.valueOf(data[6].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing donation type: " + e.getMessage());
                        continue; // Skip this line and continue with the next
                    }
                    String notes = data[7];

                    donationList.add(new Donation(donationID, donorID, amount, date, paymentMethod, receiptNumber, donationType, notes));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Method to load distributions from donationDistribution.csv
        public void loadDistributions() throws ParseException {
            File file = new File("donationDistribution.csv");

            // Check if the file exists; if not, create an empty file
            if (!file.exists()) {
                System.out.println("File not found. An empty file has been created: " + file.getName());

                try {
                    file.createNewFile(); // Creates an empty file
                    distributionList = new LinkedList<>(); // Initialize an empty distribution list
                    return; // Exit the method since there is nothing to load
                } catch (IOException e) {
                    System.err.println("Error creating file: " + e.getMessage());
                    return; // Exit the method if there's an error creating the file
                }
            }

            // File exists; proceed to load the data
            try (Scanner scanner = new Scanner(file)) {
                // Skip the header line
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
                while (scanner.hasNextLine()) {
                    String[] data = scanner.nextLine().split(",");
                    String distributionID = data[0];
                    String donationID = data[1];
                    String doneeID = data[2];
                    double distributedAmount = Double.parseDouble(data[3]);
                    Date date;
                    try {
                        date = dateFormat.parse(data[4]);
                    } catch (ParseException e) {
                        System.err.println("Error parsing date: " + e.getMessage());
                        continue; // Skip this line and continue with the next
                    }
                    String type = data[5];

                    distributionList.add(new Distribution(distributionID, donationID, doneeID, distributedAmount, dateFormat.format(date), type));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace(); // This should no longer occur due to the file existence check
            }
        }

        //Distribute the donation
        public boolean distributeDonation(String doneeID, double distributedAmount, Date date, Donation.DonationType donationType) {
            LinkedList<Donation> eligibleDonations = new LinkedList<>();
            HashMapInterface<String, Double> donationDistributedMap = new HashMapImplementation<>();
            double totalAvailableAmount = 0.0;

            // total distributed amounts for each donation is put into map
            for (Distribution distribution : distributionList) {
                String donationID = distribution.getDonationID();
                double amount = distribution.getDistributedAmount();

                donationDistributedMap.put(donationID, donationDistributedMap.getOrDefault(donationID, 0.0) + amount);
            }

            // filter donations by the donation type and calculate total available amounts
            for (Donation donation : donationList) {
                if (donation.getDonationType().equals(donationType)) {
                    double totalDistributedAmount = donationDistributedMap.getOrDefault(donation.getDonationId(), 0.0);
                    double availableAmount = donation.getAmount() - totalDistributedAmount;

                    if (availableAmount > 0) {
                        eligibleDonations.add(new Donation(donation.getDonationId(), donation.getDonorId(), availableAmount, donation.getDate(), donation.getPaymentMethod(), donation.getReceiptNumber(), donation.getDonationType(), donation.getNotes()));
                        totalAvailableAmount += availableAmount;
                    }
                }
            }

            // Check if the total available amount for the request donation type is sufficient
            if (totalAvailableAmount < distributedAmount) {
                System.out.println("Error: The requested amount exceeds the total available amount for the specified donation type.");
                return false;
            }

            System.out.println("Distribution successfully created.");
            doneeUI.printBox("Distribution Details");

            // Distribute the amount across eligible donations
            double remainingAmount = distributedAmount;
            for (Donation donation : eligibleDonations) {
                double availableAmount = donation.getAmount();
                if (remainingAmount <= availableAmount) {
                    // Create a new Distribution entry for the remaining amount
                    String distributionID = generateDistributionID();
                    Distribution newDistribution = new Distribution(distributionID, donation.getDonationId(), doneeID, remainingAmount, dateFormat.format(date), donationType.name());

                    // Add the new distribution to the distribution list
                    distributionList.add(newDistribution);

                    // Save the updated distribution data into file when it is last one.
                    saveDistributionData();

                    System.out.println(distributionList.getEntry(distributionList.getNumberOfEntries()));
                    return true;
                } else {
                    // Create a new Distribution entry with the maximum available amount
                    // So each donation of amount can be totally distributed out
                    String distributionID = generateDistributionID();
                    Distribution newDistribution = new Distribution(distributionID, donation.getDonationId(), doneeID, availableAmount, dateFormat.format(date), donationType.name());

                    // Add the new distribution to the distribution list
                    distributionList.add(newDistribution);
                    System.out.println(distributionList.getEntry(distributionList.getNumberOfEntries()));

                    // Update the remaining amount to be distributed
                    remainingAmount -= availableAmount;
                }
            }

            return true;
        }

        //Generate distributionID start with DIST
        private String generateDistributionID() {
            return "DIST" + (distributionList.size() + 1);
        }

        // Method to save distribution data to donationDistribution.csv
        private void saveDistributionData() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("donationDistribution.csv"))) {
                // Write the header line
                writer.write("DistributionID,DonationID,DoneeID,DistributedAmount,IssuedDate,AidType");
                writer.newLine();

                // Write the distribution data
                for (Distribution distribution : distributionList) {
                    writer.write(distribution.getDistributionID() + ","
                            + distribution.getDonationID() + ","
                            + distribution.getdoneeID() + ","
                            + distribution.getDistributedAmount() + ","
                            + distribution.getDate() + ","
                            + distribution.getType());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Method to print all donations
        public void printDonations() {

            for (Donation donation : donationList) {
                System.out.println(donation);
            }
        }

        // Method to print available donations based on the remaining amount
        public void printAvailableDonations() {

            // Calculate total distributed amounts using a map
            HashMapInterface<String, Double> distributedAmountsMap = new HashMapImplementation<>();

            for (Distribution distribution : distributionList) {
                distributedAmountsMap.put(
                        distribution.getDonationID(),
                        distributedAmountsMap.getOrDefault(distribution.getDonationID(), 0.0) + distribution.getDistributedAmount()
                );
            }

            // Print header
            doneeUI.printBox("Available Donations");
            System.out.println();
            System.out.printf("%-15s %-25s %-20s %-30s%n", "Donation ID", "Available Amount(RM)", "Donation Type", "Notes");
            doneeUI.printDash(66);
            System.out.println();

            // Print donations with available amounts
            for (Donation donation : donationList) {
                double totalDistributedAmount = distributedAmountsMap.getOrDefault(donation.getDonationId(), 0.0);
                double availableAmount = donation.getAmount() - totalDistributedAmount;

                if (availableAmount > 0) {
                    System.out.printf("%-15s %-25.2f %-20s %-30s%n",
                            donation.getDonationId(),
                            availableAmount,
                            donation.getDonationType(),
                            donation.getNotes());
                }
            }
        }

    }

}
