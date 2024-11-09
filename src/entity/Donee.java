package entity;

public class Donee {

    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String doneeType;  // Individual / Organization
    private String organizationName; // Optional , Only doneeType = Organization , will need to fill in

//    public Donee(String id,String name, String address, String phoneNumber, String email, String doneeType) {
//        this.id = id;
//        this.name = name;
//        this.address = address;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.doneeType = doneeType;
//        this.organizationName = "";
//    }
    //with Organization Name
    public Donee(String id, String name, String address, String phoneNumber, String email, String doneeType, String organizationName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.doneeType = doneeType;
        this.organizationName = organizationName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDoneeType() {
        return doneeType;
    }

    public void setDoneeType(String doneeType) {
        this.doneeType = doneeType;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

//    @Override
//    public String toString() {
//        return "Donee{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", address='" + address + '\'' +
//                ", phoneNumber='" + phoneNumber + '\'' +
//                ", email='" + email + '\'' +
//                ", doneeType='" + doneeType + '\'' +
//                ", Organization Name='" + organizationName + '\'' +
//                '}';
//    }
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                id == null ? "" : id, // Handle null ID
                name == null ? "" : name, // Handle null name
                address == null ? "" : address, // Handle null address
                phoneNumber == null ? "" : phoneNumber, // Handle null phone number
                email == null ? "" : email, // Handle null email
                doneeType == null ? "" : doneeType, // Handle null doneeType
                organizationName == null ? "" : organizationName // Handle null organizationName
        );
    }

    // Override equals to compare Donee objects by ID or other attributes
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Donee donee = (Donee) obj;
        return id.equals(donee.id); // Compare based on ID or other criteria
    }
}
