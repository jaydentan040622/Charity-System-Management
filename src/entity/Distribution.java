package entity;

public class Distribution {

    private String distributionID;
    private String donationID;
    private String doneeID;
    private double distributedAmount;
    private String date;
    private String type;

    public Distribution(String distributionID, String donationID, String doneeID, double distributedAmount, String date, String type) {
        this.distributionID = distributionID;
        this.donationID = donationID;
        this.doneeID = doneeID;
        this.distributedAmount = distributedAmount;
        this.date = date;
        this.type = type;
    }

    public String getDistributionID() {
        return distributionID;
    }

    public String getDonationID() {
        return donationID;
    }

    public String getdoneeID() {
        return doneeID;
    }

    public double getDistributedAmount() {
        return distributedAmount;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    // toString method
    @Override
    public String toString() {
        return String.format(
                "%n"
                + " Distribution ID= %s%n"
                + " Donation ID= %s%n"
                + " Donee ID= %s%n"
                + " Received Amount= RM%.2f%n"
                + " Issued Date= %s%n"
                + " Aid Type= %s%n",
                distributionID,
                donationID,
                doneeID,
                distributedAmount,
                date,
                type
        );
    }
}
