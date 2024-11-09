package entity;


public class Volunteer {
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String VolunteerId;
    private boolean isExperienced;
    private String Volunteertype;

    private Event assignedEvent;

    public Volunteer(String VolunteerId, String Volunteertype, String name, String phoneNumber, String email,
            String address) {
        this.VolunteerId = VolunteerId;
        this.Volunteertype = Volunteertype;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.assignedEvent = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExperienced() {
        return isExperienced;
    }

    public void setExperienced(boolean isExperienced) {
        this.isExperienced = isExperienced;
    }

    public String getVolunteertype() {
        return Volunteertype;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getVolunteerId() {
        return VolunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        VolunteerId = volunteerId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {

        return Volunteertype;

    }

    public Event getAssignedEvent() {
        return assignedEvent;
    }

    public void setAssignedEvent(Event assignedEvent) {
        this.assignedEvent = assignedEvent;
    }

    @Override
    public String toString() {
        return String.format(
                "\n" +
                        "=====================================%n" +
                        "           Volunteer Details         %n" +
                        "=====================================%n" +
                        "Volunteer ID  : %s%n" +
                        "Name          : %s%n" +
                        "Phone Number  : %s%n" +
                        "Email         : %s%n" +
                        "Address       : %s%n" +
                        "=====================================%n",
                VolunteerId,
                name,
                phoneNumber,
                email,
                address);
    }

}
