package boundary;
import java.util.InputMismatchException;
import java.util.Scanner;


public class MainUI {
    private Scanner scanner = new Scanner(System.in);

    public int displayMenu() {
        System.out.println("\n===========================================");
        System.out.println("   Charity Centre Management System        ");
        System.out.println("===========================================");
        System.out.println(" 1. Donation Management");
        System.out.println(" 2. Donor Management");
        System.out.println(" 3. Donee Management");
        System.out.println(" 4. Volunteer Management");
        System.out.println(" 0. Quit");
        System.out.println("===========================================");
        int choice = -1;
        while (choice < 0 || choice > 4) {
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice < 0 || choice > 4) {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
        System.out.println();
        return choice;
    }
}