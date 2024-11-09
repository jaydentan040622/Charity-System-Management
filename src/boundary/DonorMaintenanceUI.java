//author: Choo Mun Chun

package boundary;

import ADT.TreeMapImplementation;
import ADT.TreeMapInterface;
import control.DonorMaintenance;
import entity.Donor;
import java.util.Scanner;
import utility.ValidationUI;

public class DonorMaintenanceUI {
    private final Scanner scanner;
    private final DonorMaintenance donorMaintenance;

    public DonorMaintenanceUI() {
        scanner = new Scanner(System.in);
        donorMaintenance = new DonorMaintenance();
    }

    public void start() {
        int choice = -1; // Initial value outside of valid range

        do {
            showMenu();

            // Input validation loop
            while (true) {
                System.out.print("Enter your choice: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    if (choice >= 1 && choice <= 8) {
                        break; // Valid choice, exit input validation loop
                    } else {
                        System.err.println("Invalid choice. Please enter a number between 1 and 8.");
                    }
                } else {
                    System.err.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Consume invalid input
                }
            }

            // Handle menu choices
            switch (choice) {
                case 1:
                    addDonor();
                    break;
                case 2:
                    updateDonor();
                    break;
                case 3:
                    deleteDonor();
                    break;
                case 4:
                    viewAllDonors();
                    break;
                case 5:
                    searchDonors();
                    break;
                case 6:
                    filterDonors();
                    break;
                case 7:
                    showReportMenu();
                    break;
                case 8:
                    System.out.println("Exiting Donor Maintenance System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 8); // Correct exit condition
    }

    private void showMenu() {
        System.out.println("\n====================================");
        System.out.println("       Donor Maintenance System");
        System.out.println("====================================");
        System.out.println(" 1. Add Donor");
        System.out.println(" 2. Update Donor");
        System.out.println(" 3. Delete Donor");
        System.out.println(" 4. View All Donors");
        System.out.println(" 5. Search Donors");
        System.out.println(" 6. Filter Donors");
        System.out.println(" 7. Donor Report");
        System.out.println(" 8. Exit");
        System.out.println("------------------------------------");
    }

    // ------------------------------------------------------------------------------------------------
    // add donor
    private void addDonor() {
        String name, contactNumber, email, address, donorType, donationPreference, donorTimes, totalAmount;

        System.out.println("\n====================================");
        System.out.println("      Donor Account Creation");
        System.out.println("====================================");
        while (true) {
            System.out.print("Enter Name: ");
            name = scanner.nextLine();
            if (ValidationUI.isNotEmpty(name))
                break;
            System.out.println("Name cannot be empty.");
            if (!ValidationUI.retryOrExit())
                return;
        }

        while (true) {
            System.out.print("Enter Contact Number: ");
            contactNumber = scanner.nextLine();
            if (ValidationUI.isValidPhoneNumber(contactNumber))
                break;
            System.err.println("Invalid contact number format. It should start with '01' and be 10 or 11 digits long.");
            if (!ValidationUI.retryOrExit())
                return;
        }

        while (true) {
            System.out.print("Enter Email: ");
            email = scanner.nextLine();
            if (ValidationUI.isValidEmail(email))
                break;
            System.err.println("Invalid email format.");
            if (!ValidationUI.retryOrExit())
                return;
        }

        while (true) {
            System.out.print("Enter Address: ");
            address = scanner.nextLine();
            if (ValidationUI.isNotEmpty(address))
                break;
            System.out.println("Address cannot be empty.");
            if (!ValidationUI.retryOrExit())
                return;
        }

        while (true) {
            System.out.print("Enter Donor Type (Government, Private, Public): ");
            donorType = scanner.nextLine();

            if (ValidationUI.isNotEmpty(donorType) && donorType.matches("(?i)^(Government|Private|Public)$")) {
                break;
            }

            System.err.println("Donor Type is invalid! It must be one of: Government, Private, Public.");

            if (!ValidationUI.retryOrExit()) {
                return;
            }
        }

        while (true) {
            System.out.print("Enter Donation Preference (Cash, Online): ");
            donationPreference = scanner.nextLine();

            if (ValidationUI.isNotEmpty(donationPreference) &&
                    donationPreference.matches("(?i)^(Cash|Online)$")) {
                break;
            }

            System.err.println("Donation Preference is invalid! It must be one of: Cash, Online.");

            if (!ValidationUI.retryOrExit()) {
                return;
            }
        }

        while (true) {
            System.out.print("Enter Donation Times: ");
            donorTimes = scanner.nextLine();

            if (ValidationUI.isNotEmpty(donorTimes) && ValidationUI.isDigit(donorTimes)) {
                break;
            }

            System.out.println("Donation Times cannot be empty and must be a valid number.");

            if (!ValidationUI.retryOrExit()) {
                return;
            }
        }

        while (true) {
            System.out.print("Enter Total Amount (RM): ");
            totalAmount = scanner.nextLine();

            if (ValidationUI.isNotEmpty(totalAmount) && ValidationUI.isValidAmount(totalAmount)) {
                break;
            }

            System.out.println("Total Amount cannot be empty and must be a valid number.");

            if (!ValidationUI.retryOrExit()) {
                return;
            }
        }

        // Generate sequential Donor ID after all details are entered
        // Inside the addDonor method
        String donorId = donorMaintenance.generateUniqueDonorId();
        System.out.println("Generated Donor ID: " + donorId);

        donorMaintenance.addDonor(donorId, name, contactNumber, email, address, donorType, donationPreference,
                donorTimes, totalAmount);

    }

    // ------------------------------------------------------------------------------------------------
    // update donor
    private void updateDonor() {
        System.out.println("\n--- Update Donor ---");
        System.out.print("Enter Donor ID to update: ");
        String donorId = scanner.nextLine();
        Donor donor = donorMaintenance.findDonorById(donorId);

        if (donor != null) {
            boolean updating = true;

            while (updating) {
                System.out.println("\nSelect the field to update:");
                System.out.println(String.format("1. %-20s : %s", "Name", donor.getName()));
                System.out.println(String.format("2. %-20s : %s", "Contact Number", donor.getContactNumber()));
                System.out.println(String.format("3. %-20s : %s", "Email", donor.getEmail()));
                System.out.println(String.format("4. %-20s : %s", "Address", donor.getAddress()));
                System.out.println(String.format("5. %-20s : %s", "Donor Type", donor.getDonorType()));
                System.out
                        .println(String.format("6. %-20s : %s", "Donation Preference", donor.getDonationPreference()));
                System.out.println("7. Save changes and return");
                System.out.print("Enter your choice: ");

                int choice = -1;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input, please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        String name;
                        while (true) {
                            System.out.print("Enter new Name: ");
                            name = scanner.nextLine();
                            if (ValidationUI.isNotEmpty(name)) {
                                donor.setName(name);
                                break;
                            } else {
                                System.out.println("Name cannot be empty.");
                                if (!ValidationUI.retryOrExit())
                                    return;
                            }
                        }
                        break;
                    case 2:
                        String contactNumber;
                        while (true) {
                            System.out.print("Enter new Contact Number: ");
                            contactNumber = scanner.nextLine();
                            if (ValidationUI.isValidPhoneNumber(contactNumber)) {
                                donor.setContactNumber(contactNumber);
                                break;
                            } else {
                                System.err.println(
                                        "Invalid contact number format. It should start with '01' and be 10 or 11 digits long.");
                                if (!ValidationUI.retryOrExit())
                                    return;
                            }
                        }
                        break;
                    case 3:
                        String email;
                        while (true) {
                            System.out.print("Enter new Email: ");
                            email = scanner.nextLine();
                            if (ValidationUI.isValidEmail(email)) {
                                donor.setEmail(email);
                                break;
                            } else {
                                System.err.println("Invalid email format.");
                                if (!ValidationUI.retryOrExit())
                                    return;
                            }
                        }
                        break;
                    case 4:
                        String address;
                        while (true) {
                            System.out.print("Enter new Address: ");
                            address = scanner.nextLine();
                            if (ValidationUI.isNotEmpty(address)) {
                                donor.setAddress(address);
                                break;
                            } else {
                                System.out.println("Address cannot be empty.");
                                if (!ValidationUI.retryOrExit())
                                    return;
                            }
                        }
                        break;
                    case 5:
                        String donorType;
                        while (true) {
                            System.out.print("Enter new Donor Type (Government, Private, Public): ");
                            donorType = scanner.nextLine();
                            if (ValidationUI.isNotEmpty(donorType)
                                    && donorType.matches("(?i)^(Government|Private|Public)$")) {
                                donor.setDonorType(donorType);
                                break;
                            } else {
                                System.err.println(
                                        "Donor Type is invalid! It must be one of: Government, Private, Public.");
                                if (!ValidationUI.retryOrExit())
                                    return;
                            }
                        }
                        break;
                    case 6:
                        String donationPreference;
                        while (true) {
                            System.out.print("Enter new Donation Preference (Cash, Bank In, Tng): ");
                            donationPreference = scanner.nextLine();
                            if (ValidationUI.isNotEmpty(donationPreference) &&
                                    donationPreference.matches("(?i)^(Cash|Bank In|Tng)$")) {
                                donor.setDonationPreference(donationPreference);
                                break;
                            } else {
                                System.err.println(
                                        "Donation Preference is invalid! It must be one of: Cash, Bank In, Tng.");
                                if (!ValidationUI.retryOrExit())
                                    return;
                            }
                        }
                        break;
                    case 7:
                        updating = false;
                        break;
                    default:
                        System.err.println("Invalid choice, please try again.");
                }
            }

            if (donorMaintenance.updateDonor(donor.getDonorId(), donor.getName(), donor.getContactNumber(),
                    donor.getEmail(), donor.getAddress(),
                    donor.getDonorType(), donor.getDonationPreference())) {
            } else {
                System.out.println("Failed to update donor details.");
            }
        } else {
            System.out.println("Donor not found.");
        }
    }

    // ------------------------------------------------------------------------------------------------
    // delete donor
    private void deleteDonor() {
        System.out.print("Enter Donor ID to delete: ");
        String donorId = scanner.nextLine();

        Donor donor = donorMaintenance.findDonorById(donorId);
        if (donor == null) {
            System.out.println("Donor not found.");
            return;
        }

        while (true) {
            System.out.println("Are you sure you want to delete the donor with ID: " + donorId + "?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine();

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        // User confirmed deletion
                        if (donorMaintenance.deleteDonor(donorId)) {
                            System.err.println("Donor deleted successfully.");
                        } else {
                            System.err.println("Failed to delete donor.");
                        }
                        return;

                    case 2:
                        // User canceled deletion
                        System.err.println("Deletion canceled.");
                        return;

                    default:
                        // Invalid choice
                        System.err.println("Invalid choice. Please enter 1 for Yes or 2 for No.");
                        break;
                }
            } else {
                // Invalid input
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // ------------------------------------------------------------------------------------------------
    // view all donors

    private void viewAllDonors() {
        boolean continueViewing = true; // Flag to control the loop

        while (continueViewing) {
            // Display the list of all donors
            donorMaintenance.viewAllDonors();

            // Present sorting options to the user
            displaySortingOptions();

            // Read and validate the user's choice
            String input = getUserChoice();

            // Process the user's choice
            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);

                // Process the sorting choice and check if the user wants to exit
                continueViewing = handleSortingChoice(choice);
            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
        System.out.println("Exiting the sorting menu...");
    }

    private void displaySortingOptions() {
        System.out.println("\n--- Sort Donors ---");
        System.out.println("1. Sort by Donor ID");
        System.out.println("2. Sort by Donor Name");
        System.out.println("3. Sort by Total Amount");
        System.out.println("4. Exit");
    }

    private String getUserChoice() {
        System.out.print("Enter your choice: ");
        return scanner.nextLine(); // Read input as String for validation
    }

    private boolean handleSortingChoice(int choice) {
        switch (choice) {
            case 1:
                sortById();
                break;
            case 2:
                sortByName();
                break;
            case 3:
                sortByAmount();
                break;
            case 4:
                return false; // Return false to exit the loop
            default:
                System.err.println("Invalid choice. Please enter a number between 1 and 4.");
                break;
        }
        return true; // Continue looping
    }

    // Sort by ID
    private void sortById() {
        while (true) {
            System.out.println("\nSort by Donor ID");
            System.out.println("1. Sort by Descending Order");
            System.out.println("2. Sort by Ascending Order");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine(); // Read input as String for validation

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        donorMaintenance.sortByIdDescending();
                        donorMaintenance.viewAllDonors(); // Display sorted donors
                        break;
                    case 2:
                        donorMaintenance.sortByIdAscending();
                        donorMaintenance.viewAllDonors(); // Display sorted donors
                        break;
                    case 3:
                        return; // Exit to the previous menu
                    default:
                        System.err.println("Invalid choice. Please try again.");
                        continue; // Re-display the sorting menu
                }
            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Sort by Name
    private void sortByName() {
        while (true) {
            System.out.println("\nSort by Donor Name");
            System.out.println("1. Sort by Descending Order");
            System.out.println("2. Sort by Ascending Order");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine(); // Read input as String for validation

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        donorMaintenance.sortByNameDescending();
                        donorMaintenance.viewAllDonors(); // Display sorted donors
                        break;
                    case 2:
                        donorMaintenance.sortByNameAscending();
                        donorMaintenance.viewAllDonors(); // Display sorted donors
                        break;
                    case 3:
                        return; // Exit to the previous menu
                    default:
                        System.err.println("Invalid choice. Please try again.");
                        continue; // Re-display the sorting menu
                }
            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Sort by Amount
    private void sortByAmount() {
        while (true) {
            System.out.println("\nSort by Total Amount");
            System.out.println("1. Sort by Descending Order");
            System.out.println("2. Sort by Ascending Order");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine(); // Read input as String for validation

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        donorMaintenance.sortByAmountDescending();
                        donorMaintenance.viewAllDonors(); // Display sorted donors
                        break;
                    case 2:
                        donorMaintenance.sortByAmountAscending();
                        donorMaintenance.viewAllDonors(); // Display sorted donors
                        break;
                    case 3:
                        return; // Exit to the previous menu
                    default:
                        System.err.println("Invalid choice. Please try again.");
                        continue; // Re-display the sorting menu
                }
            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private void searchDonors() {
        boolean continueSearching = true; // Flag to control the loop

        while (continueSearching) {
            // Display search menu
            System.out.println("\n--- Search Donors ---");
            System.out.println("1. Search by Donor ID");
            System.out.println("2. Search by Donor Name");
            System.out.println("3. Search by Contact Number");
            System.out.println("4. Search by Email");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine(); // Read input as String for validation

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);
                Donor foundDonor = null; // Variable to store the found donor
                String searchValue;

                // Handle search options based on user choice
                switch (choice) {
                    case 1:
                        System.out.print("Enter Donor ID to search: ");
                        searchValue = scanner.nextLine();
                        foundDonor = donorMaintenance.findDonorById(searchValue);
                        break;
                    case 2:
                        System.out.print("Enter Donor Name to search: ");
                        searchValue = scanner.nextLine();
                        foundDonor = donorMaintenance.findDonorByName(searchValue);
                        break;
                    case 3:
                        System.out.print("Enter Contact Number to search: ");
                        searchValue = scanner.nextLine();
                        foundDonor = donorMaintenance.findDonorByContactNumber(searchValue);
                        break;
                    case 4:
                        System.out.print("Enter Email to search: ");
                        searchValue = scanner.nextLine();
                        foundDonor = donorMaintenance.findDonorByEmail(searchValue);
                        break;
                    case 5:
                        continueSearching = false; // Set flag to false to exit the loop
                        break;
                    default:
                        System.err.println("Invalid choice. Please enter a number between 1 and 5.");
                        continue; // Re-prompt the menu
                }

                // Display the search result if a search was performed
                if (choice >= 1 && choice <= 4) {
                    if (foundDonor != null) {
                        System.out.println("\n" + foundDonor);
                    } else {
                        System.err.println("Donor not found.");
                    }
                }
            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
        System.out.println("Exiting the search menu...");
    }

    // ------------------------------------------------------------------------------------------------
    // filter donors
    private void filterDonors() {
        // Initialize TreeMap to store filtered donors
        TreeMapInterface<String, Donor> filteredDonors = new TreeMapImplementation<>();

        while (true) {
            System.out.println("\n--- Filter Donors ---");
            // Show the categories to filter in the menu
            System.out.println("1. Filter by Donor Type");
            System.out.println("2. Filter by Donation Preference");
            System.out.println("3. Filter by Donation Times");
            System.out.println("4. Filter by Total Amount (RM)");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine(); // Read input as String for validation

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);
                String filterValue;

                switch (choice) {
                    case 1:
                        System.out.print("Enter Donor Type to filter: ");
                        filterValue = scanner.nextLine();
                        filteredDonors = donorMaintenance.filterByDonorType(filterValue);
                        break;
                    case 2:
                        System.out.print("Enter Donation Preference to filter: ");
                        filterValue = scanner.nextLine();
                        filteredDonors = donorMaintenance.filterByDonationPreference(filterValue);
                        break;
                    case 3:
                        System.out.print("Enter Donation Times to filter: ");
                        filterValue = scanner.nextLine();
                        filteredDonors = donorMaintenance.filterByDonationTimes(filterValue);
                        break;
                    case 4:
                        System.out.print("Enter minimum Total Amount (RM) to filter: ");
                        String minAmountStr = scanner.nextLine();
                        System.out.print("Enter maximum Total Amount (RM) to filter: ");
                        String maxAmountStr = scanner.nextLine();

                        double minAmount;
                        double maxAmount;
                        try {
                            minAmount = Double.parseDouble(minAmountStr);
                            maxAmount = Double.parseDouble(maxAmountStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid amount entered. Please enter valid numbers.");
                            continue; // Re-prompt the menu
                        }

                        filteredDonors = donorMaintenance.filterByTotalAmount(minAmount, maxAmount);
                        break;
                    case 5:
                        return; // Exit the method
                    default:
                        System.err.println("Invalid choice. Please try again.");
                        continue; // Continue to re-prompt the menu
                }

                // Display the filtered donors
                donorMaintenance.displayFilteredDonors(filteredDonors);

            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // ------------------------------------------------------------------------------------------------

    private void showReportMenu() {
        while (true) {
            System.out.println("\n====================================");
            System.out.println("       Donor Report Menu");
            System.out.println("====================================");
            System.out.println("1. Detailed Donor Report");
            System.out.println("2. Summary Donor Report");
            System.out.println("3. Exit to Main Menu");
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine(); // Read input as String for validation

            if (ValidationUI.isDigit(input)) {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        donorMaintenance.generateDetailedDonorReport();
                        break;
                    case 2:
                        donorMaintenance.generateDonorSummaryReport();
                        break;
                    case 3:
                        return; // Exit to main menu
                    default:
                        System.err.println("Invalid choice. Please try again.");
                        break;
                }
            } else {
                System.err.println("Invalid input. Please enter a valid number.");
            }
        }
    }

}
