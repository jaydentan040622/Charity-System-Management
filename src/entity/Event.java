package entity;

public class Event{


    private String eventId;
    private String eventName;

   


    public Event(String eventId, String eventName) {
        this.eventId = eventId;
        this.eventName = eventName;
    }

    public String geteventId() {
        return eventId;
    }

    public String geteventName() {
        return eventName;
    }



}
