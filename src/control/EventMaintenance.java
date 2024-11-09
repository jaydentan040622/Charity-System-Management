package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import entity.Event;
import entity.Volunteer;
import ADT.ListInterface;
import ADT.LinkedList;

public class EventMaintenance {

    private static final String EVENT_FILE_NAME = "Event.csv";
    private ListInterface<Volunteer> assignedVolunteers = new LinkedList<>();

    public EventMaintenance() {
    }

    // Method to assign a registered volunteer to a hardcoded event and save to Event.csv
    public void assignVolunteerToEventAndSave(Volunteer volunteer, Event event) {
        // Check if the volunteer is already assigned to the event
        if (!isVolunteerAlreadyAssigned(volunteer, event)) {
            // Assign the event to the volunteer
            volunteer.setAssignedEvent(event);

            // Write the event assignment to Event.csv
            saveAssignedVolunteerToEvent(volunteer, event);

            // Add the volunteer to the list of assigned volunteers (in memory)
            assignedVolunteers.add(volunteer);

        } else {
            System.out.println("Volunteer " + volunteer.getName() + " is already assigned to event " + event.geteventName() + ".");
        }
    }

    // Method to check if the volunteer is already assigned to the event
    public boolean isVolunteerAlreadyAssigned(Volunteer volunteer, Event event) {
        // Check if the volunteer is assigned to any event
        if (volunteer.getAssignedEvent() != null) {
            // Compare the assigned event with the provided event
            return volunteer.getAssignedEvent().geteventId().equals(event.geteventId());
        }
        return false; // Volunteer is not assigned to any event
    }

    // Method to save the assigned volunteer to Event.csv
    private void saveAssignedVolunteerToEvent(Volunteer volunteer, Event event) {
        if (isVolunteerAlreadyAssigned(volunteer, event)) {
            System.out.println("Volunteer " + volunteer.getName() + " have been assigned to the event '"
                    + event.geteventName() + "'.");
            return; // Exit the method if the volunteer is already assigned
        }
    
        File file = new File(EVENT_FILE_NAME);
        boolean isNewFile = !file.exists();
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // Write the header if the file is new
            if (isNewFile) {
                writer.write("EventId,EventName,VolunteerId,VolunteerName\n");
            }
    
            // Write the volunteer assignment
            writer.write(event.geteventId() + "," + event.geteventName() + "," + 
                         volunteer.getVolunteerId() + "," + volunteer.getName() + "\n");
    
            // Update the volunteer's assigned event
            volunteer.setAssignedEvent(event);
    
        } catch (IOException e) {
            System.out.println("Error writing to Event.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    


}
