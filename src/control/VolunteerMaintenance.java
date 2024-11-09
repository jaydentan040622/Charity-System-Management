package control;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import ADT.LinkedList;
import ADT.ListInterface;
import DAO.FileDao;
import boundary.VolunteerMaintenanceUI;
import entity.Event;
import entity.Volunteer;
import utility.MessageUI;

/**
 *
 * @author TAN HAN SHEN
 */
public class VolunteerMaintenance {
    // Fields for managing volunteer data
    private final FileDao<Volunteer> fileDao;
    private static final String FILE_NAME = "VolunteerData.csv";
    private final ListInterface<String> headers;
    private ListInterface<Volunteer> VolunteerList = new LinkedList<>();
    private VolunteerMaintenanceUI VolunteerUI = new VolunteerMaintenanceUI();
    private int nextId;

    // Constructor initializes headers, loads volunteers from CSV, and sets the next ID
    public VolunteerMaintenance() {
        headers = new LinkedList<>();
        headers.add("ID");
        headers.add("Volunteer Type");
        headers.add("Name");
        headers.add("Phone number");
        headers.add("Email");
        headers.add("Address");

        fileDao = new FileDao<>();
        VolunteerList = fileDao.loadDataFromCSV(FILE_NAME, this::mapRowToVolunteer);
        this.nextId = calculateNextId(); // Initialize nextId based on existing IDs

    }
    
    // Calculate the next available volunteer ID by finding the max existing ID and incrementing it
    private int calculateNextId() {
        int maxId = 0;
        for (int i = 1; i <= VolunteerList.getNumberOfEntries(); i++) {
            Volunteer donee = VolunteerList.getEntry(i);
            int currentId = Integer.parseInt(donee.getVolunteerId().substring(2)); // Extract numeric part
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        return maxId + 1; // Next available ID
    }

    // file code
     // Map a row of data from the CSV to a Volunteer object
    private Volunteer mapRowToVolunteer(String[] row) {

        if (row.length < 6) {
            System.out.println("Error: Malformed row detected. Expected at least 6 elements but got: " + row.length);
            return null; // Skip this row if it is malformed
        }
        String volunteerId = row[0];
        String volunteerType = row[1];
        String name = row[2];
        String phoneNumber = row[3];
        String email = row[4];
        String address = row[5];

        Volunteer volunteer = new Volunteer(volunteerId, volunteerType, name, phoneNumber, email, address);
        return volunteer;
    }

    // Write volunteer data to a CSV file, either appending or creating new
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
    
    // Save the list of volunteers to the CSV file
    public boolean saveVolunteersToCSV() {
        try {
            ListInterface<Volunteer> validVolunteers = new LinkedList<>();
            for (int i = 0; i < VolunteerList.size(); i++) {
                Volunteer volunteer = VolunteerList.get(i);
                if (volunteer != null) {
                    validVolunteers.add(volunteer);
                } else {
                    System.out.println("Warning: Null donor found at index " + i);
                }
            }
            fileDao.writeDataToCSV(FILE_NAME, headers, validVolunteers, this::mapVolunteerToRow);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving volunteer to CSV: " + e.getMessage());
            return false;
        }
    }

    // Map a Volunteer object to a row format for the CSV file
    private ListInterface<String> mapVolunteerToRow(Volunteer Volunteer) {
        ListInterface<String> row = new LinkedList<>();
        row.add(Volunteer.getVolunteerId());
        row.add(Volunteer.getVolunteertype());
        row.add(Volunteer.getName());
        row.add(Volunteer.getPhoneNumber());
        row.add(Volunteer.getEmail());
        row.add(Volunteer.getAddress());
        return row;
    }

    // Write a single volunteer's data to the CSV file
    public void writeVolunteerToCSV(Volunteer volunteer) {
        File file = new File("VolunteerData.csv");
        boolean isNewFile = !file.exists(); // Check if the file does not exist (new file)

        try (FileWriter writer = new FileWriter(file, true)) {
            // If it's a new file or an empty file, write the header first
            if (isNewFile || file.length() == 0) {
                writer.append("VolunteerId,VolunteerType,Name,phoneNumber,Email,Address\n");
            }

            // Now write the volunteer data
            writer.append(volunteer.getVolunteerId())
                    .append(',')
                    .append(volunteer.getVolunteertype())
                    .append(',')
                    .append(volunteer.getName())
                    .append(',')
                    .append(volunteer.getPhoneNumber())
                    .append(',')
                    .append(volunteer.getEmail())
                    .append(',')
                    .append(volunteer.getAddress())
                    .append('\n');

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Case 1 : registerNewVolunteer
    // Collect and return the volunteer's details by prompting the user
    public Volunteer inputVolunteerDetails() {
        boolean isExperienced = VolunteerMaintenanceUI.inputVolunteerExperience();
        String volunteerType = isExperienced ? "Experienced" : "Non-Experienced";

        String volunteerId = generateVolunteerId();
        String name = VolunteerUI.inputVolunteerName();
        String phoneNumber = VolunteerUI.inputVolunteerPhoneNumber(VolunteerList);
        String email = VolunteerUI.inputVolunteerEmail(VolunteerList);
        String address = VolunteerUI.inputVolunteerAddress();

        return new Volunteer(volunteerId, volunteerType, name, phoneNumber, email, address);
    }

    // Generate a new unique volunteer ID based on the nextId value
    private String generateVolunteerId() {
        String prefix = "V";
        String numericPart = String.format("%03d", nextId);
        nextId++; // Increment for the next volunteer
        return prefix + numericPart;
    }

    // Register a new volunteer and save them to the list and CSV file
    public void registerNewVolunteer() {
        Volunteer newVolunteer = inputVolunteerDetails();
        if (newVolunteer != null) {
            VolunteerList.add(newVolunteer);
            writeVolunteerToCSV(newVolunteer);
            System.out.println("Volunteer registered successfully! \n");
        } else {
            System.out.println("Error: Unable to register new volunteer. Please try again.");
        }
    }

    // Case 2 : deleteVolunteerById
    // Delete a volunteer by ID and update the CSV file
    public void deleteVolunteerById() {
        String volunteerId = VolunteerUI.inputVolunteerId(); // Input the volunteer ID

        // Look for the volunteer 
        Volunteer volunteerToRemove = null;
        for (int i = 0; i < VolunteerList.size(); i++) {
            Volunteer volunteer = VolunteerList.get(i);
            if (volunteer.getVolunteerId().equals(volunteerId)) {
                volunteerToRemove = volunteer;
                break;
            }
        }

        // If the volunteer is found, remove it
        if (volunteerToRemove != null) {
            VolunteerList.remove(volunteerToRemove); // Remove from in-memory list

            // Now update the file by re-saving the list to the CSV
            if (saveVolunteersToCSV()) {
                System.out.println("Volunteer " + volunteerId + " has been deleted successfully.");
            } else {
                System.out.println("Error: Could not save changes to file.");
            }
        } else {
            System.out.println("Volunteer with ID " + volunteerId + " not found.");
        }
    }

    // print the list of volunteers
    private void printVolunteerList() {
        for (int i = 0; i < VolunteerList.getNumberOfEntries(); i++) {
            Volunteer volunteer = VolunteerList.getEntry(i);
            if (volunteer != null) {
                System.out.println(volunteer);
            }
        }
    }

    // case 3 : searchVolunteerById
    // Search for a volunteer by ID and print their details if found
    public void searchVolunteerById() {
        String volunteerId = VolunteerUI.inputVolunteerId();
        Volunteer volunteer = findVolunteerByIdInFile(volunteerId);
        if (volunteer != null) {
            VolunteerUI.printVolunteerDetails(volunteer);
        } else {
            System.out.println("Volunteer with ID " + volunteerId + " not found.");
        }
    }

    // Find a volunteer by ID by reading from the CSV file
    private Volunteer findVolunteerByIdInFile(String volunteerId) {
        String fileName = "VolunteerData.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean skipHeader = true; // To skip the header line

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue; // Skip the header line
                }

                String[] fields = line.split(",");
                if (fields.length >= 6) {
                    String csvVolunteerId = fields[0];
                    if (csvVolunteerId.equals(volunteerId)) {
                        String volunteerType = fields[1];
                        String name = fields[2];
                        String phoneNumber = fields[3];
                        String email = fields[4];
                        String address = fields[5];
                        return new Volunteer(volunteerId, volunteerType, name, phoneNumber, email, address);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the VolunteerData.csv file: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Volunteer ID not found
    }

    // case 4 : filterVolunteersByExperience
    // Filters volunteers based on their experience level and displays the matching volunteers
    public void filterVolunteersByExperience() {
        String filterType = VolunteerMaintenanceUI.inputFilterChoice();
        boolean found = false;

        String fileName = "VolunteerData.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean skipHeader = true; // To skip the header line

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue; // Skip the header line
                }

                String[] fields = line.split(",");
                if (fields.length >= 6) {
                    String volunteerType = fields[1];
                    if (volunteerType.equalsIgnoreCase(filterType)) {
                        String volunteerId = fields[0];
                        String name = fields[2];
                        String phoneNumber = fields[3];
                        String email = fields[4];
                        String address = fields[5];
                        Volunteer volunteer = new Volunteer(volunteerId, volunteerType, name, phoneNumber, email,
                                address);
                        System.out.println(volunteer.toString());
                        found = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the VolunteerData.csv file: " + e.getMessage());
            e.printStackTrace();
        }

        // If no volunteers found for the given type
        if (!found) {
            System.out.println("No volunteers found for type: " + filterType);
        }
    }

    // case 5 : listAllVolunteers
    public String getAllVolunteers() {
        String outputStr = "";
        for (int i = 1; i <= VolunteerList.getNumberOfEntries(); i++) {
            outputStr += VolunteerList.getEntry(i) + "\n";
        }
        return outputStr;
    }

    // case 6 : assignVolunteerEvent
    // Assigns a volunteer to an event if they are not already assigned
    public void assignVolunteerEvent(String volunteerId) {
        EventMaintenance eventMaintenance = new EventMaintenance();

        // Hardcode the event details
        Event event = new Event("E001", "Community Clean-Up");

        Volunteer volunteer = findVolunteerByIdInFile(volunteerId);

        if (volunteer != null) {
            if (eventMaintenance.isVolunteerAlreadyAssigned(volunteer, event)) {
                System.out.println("Volunteer with ID " + volunteerId + " is already assigned to the event.");
                return;
            }
            // Check if the volunteer is already assigned to the event in the Event.csv file
            if (isVolunteerAssignedToEventInFile(volunteerId, event.geteventId())) {
                System.out.println("Volunteer with ID " + volunteerId + " is already assigned to the event.");
                return;
            }
            eventMaintenance.assignVolunteerToEventAndSave(volunteer, event);
            // Add this line to write the volunteer to the Event.csv file
            writeVolunteerToEventCSV(volunteer, event);
        } else {
            System.out.println("Volunteer with ID " + volunteerId + " not found.");
        }
    }

    // Checks if a volunteer is already assigned to an event in the Event.csv file
    private boolean isVolunteerAssignedToEventInFile(String volunteerId, String eventId) {
        String fileName = "Event.csv";
        File file = new File(fileName);
    
        // Check if the file exists before attempting to read
        if (!file.exists()) {
            System.out.println("The Event.csv file does not exist.");
            return false;
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean skipHeader = true;
    
            while ((line = br.readLine()) != null) {
                // Skip the header line
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
    
                // Split the CSV line by comma
                String[] fields = line.split(",");
                if (fields.length >= 4) {
                    String csvVolunteerId = fields[2].trim();
                    String csvEventId = fields[0].trim();
    
                    // Check if volunteer and event IDs match
                    if (csvVolunteerId.equals(volunteerId) && csvEventId.equals(eventId)) {
                        return true; // Volunteer is already assigned to the event
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the Event.csv file: " + e.getMessage());
            e.printStackTrace();
        }
    
        return false; // Volunteer is not assigned to the event
    }
    // Write the volunteer's details to the Event.csv file
    private void writeVolunteerToEventCSV(Volunteer volunteer, Event event) {
        String fileName = "Event.csv";
        File file = new File(fileName);
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Write the header if the file is new or empty
            if (!file.exists() || file.length() == 0) {
                writer.write("EventId,EventName,VolunteerId,VolunteerName");
                writer.newLine();
            }
    
            // Write the event and volunteer details to the file
            writer.write(event.geteventId() + "," + event.geteventName() + "," + volunteer.getVolunteerId() + "," + volunteer.getName());
            writer.newLine();
    
        } catch (IOException e) {
            System.out.println("Error writing to the Event.csv file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Finds and returns an event ID based on the event name from the "Event.csv" file
    private String getEventIdByName(String eventName) {
        String EVENT_FILE_NAME = "Event.csv"; // Declare and initialize the EVENT_FILE_NAME variable with the
                                              // appropriate value
        try (BufferedReader br = new BufferedReader(new FileReader(EVENT_FILE_NAME))) {
            String line;
            boolean skipHeader = true; // To skip the header line

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue; // Skip the header line
                }

                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    String csvEventName = fields[1];

                    if (csvEventName.equalsIgnoreCase(eventName)) {
                        return fields[0]; // Return the event ID
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the Event.csv file: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // Event name not found
    }

    // Prints the volunteers assigned to a specific event based on the event name
    public void printVolunteersByEventName(String eventName) {
        LinkedList<Volunteer> volunteers = (LinkedList<Volunteer>) getVolunteersByEventName(eventName);
    
        System.out.println("\n===========================================");
        System.out.println("  Volunteers for Event: " + eventName);
        System.out.println("===========================================");
        
        if (volunteers.isEmpty()) {
            System.out.println("No volunteers found for the event: " + eventName);
        } else {
            System.out.printf("| %-13s | %-23s |\n", "Volunteer ID", "Name");
            System.out.println("-------------------------------------------");
    
            for (Volunteer volunteer : volunteers) {
                System.out.printf("| %-13s | %-23s |\n", volunteer.getVolunteerId(), volunteer.getName());
            }
            
            System.out.println("===========================================\n");
        }
    }

    // public void printVolunteersByEventName() {
    // String eventName = VolunteerUI.inputEventName(); // Assume this method
    // prompts the user for input and returns the event name
    // printVolunteersByEventName(eventName);
    // }

    // case 7 : getVolunteersByEventName
    // Retrieves a list of volunteers assigned to a specific event by the event name
    public ListInterface<Volunteer> getVolunteersByEventName(String eventName) {
        LinkedList<Volunteer> volunteers = new LinkedList();
        String eventId = getEventIdByName(eventName);

        if (eventId == null) {
            System.out.println("Event not found.");
            return volunteers;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("Event.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 4) {
                    String eventFileId = fields[0];
                    if (eventFileId.equals(eventId)) {
                        String volunteerId = fields[2];
                        Volunteer volunteer = findVolunteerByIdInFile(volunteerId);
                        if (volunteer != null) {
                            volunteers.add(volunteer);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return volunteers;
    }

    // case 8 : generateSummaryReport
    // Generates a summary report of the total number of volunteers, categorized by their experience level
    public void generateSummaryvolunteerReport() {
        int totalVolunteers = VolunteerList.getNumberOfEntries();
        int experiencedCount = 0;
        int nonExperiencedCount = 0;

        for (int i = 1; i <= VolunteerList.getNumberOfEntries(); i++) {
            Volunteer volunteer = VolunteerList.getEntry(i);

            if (volunteer.getVolunteertype().equalsIgnoreCase("Experienced")) {
                experiencedCount++;
            } else {
                nonExperiencedCount++;
            }
        }

        // Print the summary report
        System.out.println();
        System.out.println("=====================================================");
        System.out.println("                 Volunteer Summary Report            ");
        System.out.println("=====================================================");
        System.out.println("| Volunteer Type                   | Total Count     |");
        System.out.println("|----------------------------------|-----------------|");
        System.out.printf("| %-32s | %-15d |\n", "Experienced Volunteers", experiencedCount);
        System.out.printf("| %-32s | %-15d |\n", "Non-Experienced Volunteers", nonExperiencedCount);
        System.out.println("|----------------------------------|-----------------|");
        System.out.printf("| %-32s | %-15d |\n", "Total Volunteers Registered", totalVolunteers);
        System.out.println("=====================================================");
        System.out.println();

    }

    // Opens the "VolunteerData.csv" file in the default CSV viewer on the user's computer
    public void generateSummaryReport() {
        try {
            Desktop.getDesktop().open(new File("VolunteerData.csv"));
            System.out.println(" ");

        } catch (IOException ex) {
            Logger.getLogger(VolunteerMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runVolunteerMaintenance() {
        int choice;
        VolunteerMaintenance volunteerMaintenance = new VolunteerMaintenance(); // Declare and initialize the

        String volunteerId = ""; // Declare and initialize the volunteerId variable
        do {
            choice = VolunteerUI.getMenuChoice();
            switch (choice) {
                case 0:
                    MessageUI.displayExitMessage();
                    break;
                case 1:
                    registerNewVolunteer();
                    break;
                case 2:
                    deleteVolunteerById();
                    break;
                case 3:
                    searchVolunteerById();
                    break;
                case 4:
                    volunteerMaintenance.filterVolunteersByExperience();
                    break;
                case 5:
                    VolunteerUI.listAllProducts(getAllVolunteers());
                    break;
                case 6:
                    volunteerId = VolunteerUI.inputVolunteerId(); // Prompt user for volunteerId
                    assignVolunteerEvent(volunteerId);
                    break;
                case 7:
                    String eventName = VolunteerUI.inputEventName(); // Assume this method prompts the user for input
                    printVolunteersByEventName(eventName);
                    break;

                case 8:
                    String summaryChoiceString = VolunteerMaintenanceUI.getSummaryChoice();
                    int summaryChoice = Integer.parseInt(summaryChoiceString);
                    if (summaryChoice == 1) {
                        generateSummaryvolunteerReport();
                    } else if (summaryChoice == 2) {
                        generateSummaryReport();
                    } else {
                        MessageUI.displayInvalidChoiceMessage();
                    }
                    break; // Add break statement to exit the switch case
                default:
                    MessageUI.displayInvalidChoiceMessage();
            }
        } while (choice != 0);
    }
}
