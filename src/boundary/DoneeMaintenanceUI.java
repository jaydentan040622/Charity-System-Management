package boundary;

import entity.Donee;
import java.util.Scanner;

public class DoneeMaintenanceUI {

    Scanner scanner = new Scanner(System.in);

    public void printBox(String message) {
        int width = message.length() + 8; // Adjust width based on message length
        String border = "-".repeat(width);
        System.out.println();
        System.out.println(border);
        System.out.println("|   " + message + "   |");
        System.out.println(border);
    }

    public void printDash(int amount) {
        String dash = "-".repeat(amount);
        System.out.print(dash);
    }

    public String getMenuChoice() {
        System.out.println();
        printDash(40);
        System.out.println("\n|              Donee Menu              |");
        printDash(40);
        System.out.println();
        System.out.println("| 1. | Register as new Donee           |");
        System.out.println("| 2. | Remove Donee                    |");
        System.out.println("| 3. | Update Current Donee Details    |");
        System.out.println("| 4. | Search Donee                    |");
        System.out.println("| 5. | Apply for Aid                   |");
        System.out.println("| 6. | Filter Donee                    |");
        System.out.println("| 7. | Generate Report                 |");
        System.out.println("| 8. | Undo Last Action                |");
        System.out.println("| 9. | Redo Last Action                |");
        System.out.println("| 0. | Quit                            |");
        printDash(40);
        System.out.println();
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();
        System.out.println();
        return choice;
    }

    public String getDonationMenuChoice() {
        System.out.println();
        printDash(38);
        System.out.println("\n|              Aid Menu              |");
        printDash(38);
        System.out.println();
        System.out.println("| 1. | Apply for food                |");
        System.out.println("| 2. | Apply for daily expenses      |");
        System.out.println("| 3. | Apply for cash                |");
        System.out.println("| 4. | Show Available Aid            |");
        System.out.println("| 0. | Quit                          |");
        printDash(38);
        System.out.println();
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();
        System.out.println();
        return choice;
    }

    public String getReportMenuChoice() {
        System.out.println();
        printDash(41);
        System.out.println("\n|              Report Menu              |");
        printDash(41);
        System.out.println();
        System.out.println("| 1. | Donee Summary Report             |");
        System.out.println("| 2. | Distribution Summary Report      |");
        System.out.println("| 0. | Quit                             |");
        printDash(41);
        System.out.println();
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();
        System.out.println();
        return choice;
    }

    public void printDoneeDetails(Donee donee) {
        printBox("Donee Details");
        System.out.printf("%-20s: %s%n", "Name", donee.getName());
        System.out.printf("%-20s: %s%n", "Address", donee.getAddress());
        System.out.printf("%-20s: %s%n", "Phone Number", donee.getPhoneNumber());
        System.out.printf("%-20s: %s%n", "Email", donee.getEmail());
        System.out.printf("%-20s: %s%n", "Donee Type", donee.getDoneeType());
        System.out.printf("%-20s: %s%n", "Organization Name", (donee.getOrganizationName() == null || donee.getOrganizationName().trim().isEmpty()) ? "N/A" : donee.getOrganizationName());
    }

    public String inputDoneeID() {
        System.out.print("Enter your ID: ");
        String id = scanner.nextLine();
        return id;
    }

    public String inputDoneeName() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        return name;
    }

    public String inputDoneeAddress() {
        System.out.print("Enter your address: ");
        String address = scanner.nextLine();
        return address;
    }

    public String inputDoneePhoneNumber() {
        System.out.print("Enter your phone number: ");
        String phoneNumber = scanner.nextLine();
        return phoneNumber;
    }

    public String inputDoneeEmail() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        return email;
    }

    public String inputDoneeType() {
        System.out.print("Do you represent any organization?(Y/N)");
        String doneeType = scanner.nextLine();
        return doneeType;
    }

    public String inputDoneeOrganizationName() {
        System.out.print("Enter your organization name: ");
        String organizationName = scanner.nextLine();
        return organizationName;
    }

    public String inputRemovedDoneeID() {
        System.out.print("Enter the ID of the Donee to remove: ");
        String IDToRemove = scanner.nextLine();
        return IDToRemove;
    }

    public String inputDoneeTypeFilter() {
        System.out.print("Enter the type of donee to filter (Organization/Individual), or leave blank to skip: ");
        String doneeTypeFilter = scanner.nextLine();
        return doneeTypeFilter;
    }

    public String inputDoneeIdToSearch() {
        System.out.print("Enter the ID of the Donee to search: ");
        String doneeIdToSearch = scanner.nextLine();
        return doneeIdToSearch;

    }

    public String inputDoneeAddressFilter() {
        System.out.print("Enter the location to filter by address (e.g., Kuala Lumpur), or leave blank to skip: ");
        String addressFilter = scanner.nextLine();
        return addressFilter;
    }

    public String inputDoneeIdUpdate() {
        System.out.print("Enter the ID of the Donee to update: ");
        String doneeIdUpdate = scanner.nextLine();
        return doneeIdUpdate;
    }

    public String getUpdateDoneeChoice() {
        System.out.println();
        printDash(40);
        System.out.println("\n|           Update Donee Details       |");
        printDash(40);
        System.out.println();
        System.out.println("| 1. | Update Name                     |");
        System.out.println("| 2. | Update Address                  |");
        System.out.println("| 3. | Update Phone Number             |");
        System.out.println("| 4. | Update Email                    |");
        System.out.println("| 5. | Update Donee Type               |");
        System.out.println("| 6. | Update Organization Name        |");
        System.out.println("| 0. | Finish Updating                 |");
        printDash(40);
        System.out.println();
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();
        System.out.println();
        return choice;
    }

    public String askUpdateDonee() {
        System.out.print("Do you want to update donee details?(Y/N) :");
        String updateDonee = scanner.nextLine();
        return updateDonee;
    }

    public String inputAmount() {
        System.out.print("Enter your amount needed (RM) :");
        String amount = scanner.nextLine();
        return amount;
    }

    public void printDoneeDetailsRow(Donee donee) {

        // Print the donee details
        printRow(
                donee.getId(),
                donee.getName(),
                donee.getAddress(),
                donee.getPhoneNumber(),
                donee.getEmail(),
                donee.getDoneeType(),
                donee.getOrganizationName() != null && !donee.getOrganizationName().trim().isEmpty()
                ? donee.getOrganizationName()
                : "N/A"
        );
    }

    public void printHeader() {
        int idWidth = 6;
        int nameWidth = 15;
        int addressWidth = 40;
        int phoneNumberWidth = 15;
        int emailWidth = 25;
        int doneeTypeWidth = 15;
        int organizationNameWidth = 20;
        int totalWidth = idWidth + nameWidth + addressWidth + phoneNumberWidth + emailWidth + doneeTypeWidth + organizationNameWidth;
        System.out.printf("%-" + idWidth + "s%-" + nameWidth + "s%-" + addressWidth + "s%-" + phoneNumberWidth + "s%-" + emailWidth + "s%-" + doneeTypeWidth + "s%-" + organizationNameWidth + "s%n",
                "ID", "Name", "Address", "Phone Number", "Email", "Donee Type", "Organization Name");
        System.out.println("-".repeat(totalWidth));
    }

    public void printRow(String id, String name, String address, String phoneNumber, String email, String doneeType, String organizationName) {
        int idWidth = 6;
        int nameWidth = 15;
        int addressWidth = 40;
        int phoneNumberWidth = 15;
        int emailWidth = 25;
        int doneeTypeWidth = 15;
        int organizationNameWidth = 20;
        System.out.printf("%-" + idWidth + "s%-" + nameWidth + "s%-" + addressWidth + "s%-" + phoneNumberWidth + "s%-" + emailWidth + "s%-" + doneeTypeWidth + "s%-" + organizationNameWidth + "s%n",
                id, name, address, phoneNumber, email, doneeType, organizationName);
    }

}
