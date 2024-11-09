package utility;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ValidationUI {

    private static final Scanner scanner = new Scanner(System.in);

    // Updated email pattern for the format xxx@xxxx.com
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.com$");

    // Phone number should start with "01" and be 10 or 11 characters long
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^01[0-9]{8,9}$");

    private static final Pattern ID_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]{5,20}$");

    //is only digit
    private static final Pattern DIGIT_PATTERN = Pattern.compile(
            "^[0-9]*$");

    //validate total amout with 2 decimal
    private static final Pattern TOTAL_AMOUNT_PATTERN = Pattern.compile(
            "^[0-9]+(\\.[0-9]{1,2})?$");

    // Validate if a string is not null and not empty
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Validate if an email address is in the format xxx@xxxx.com
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    // Validate if a phone number is in the correct format
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return isNotEmpty(phoneNumber) && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    // Validate if a string matches a specific pattern
    public static boolean matchesPattern(String value, String pattern) {
        return isNotEmpty(value) && Pattern.compile(pattern).matcher(value).matches();
    }

    // Validate if a string is a digit
    public static boolean isDigit(String value) {
        return isNotEmpty(value) && DIGIT_PATTERN.matcher(value).matches();
    }

    //validate total amount with 2 decimal
    public static boolean isValidAmount(String totalAmount) {
        return isNotEmpty(totalAmount) && TOTAL_AMOUNT_PATTERN.matcher(totalAmount).matches();
    }

    public static boolean retryOrExit() {
        while (true) {
            System.out.print("Do you want to try again? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y")) {
                return true;
            } else if (choice.equals("n")) {
                return false;
            } else {
                System.err.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

}
