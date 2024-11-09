package control;

import boundary.DonationMaintenanceUI;
import boundary.DonorMaintenanceUI;
import boundary.MainUI;
import java.text.ParseException;
import utility.MessageUI;

public class Main {
    public static void main(String[] args) throws ParseException {
        MainUI mainUI = new MainUI();
        int choice = mainUI.displayMenu();
        while (choice != 0) {
            switch (choice) {
                case 0:
                    MessageUI.displayExitMessage();
                    break;
                case 1:
                    DonationMaintenance donationMaintenance = new DonationMaintenance();
                    DonationMaintenanceUI donationUI = new DonationMaintenanceUI(donationMaintenance);
                    donationUI.displayMenu();
                    break;
                case 2:
                    DonorMaintenanceUI donorUI = new DonorMaintenanceUI();
                    donorUI.start();
                    break;
                case 3:
                    DoneeMaintenance doneeMaintenance = new DoneeMaintenance();
                    doneeMaintenance.runDoneeMaintenance();
                    break;
                case 4:
                    VolunteerMaintenance volunteerMaintenance = new VolunteerMaintenance();
                    volunteerMaintenance.runVolunteerMaintenance();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
            choice = mainUI.displayMenu();
        }
    }
}