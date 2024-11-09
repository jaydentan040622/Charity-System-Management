package entity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Donation {
    private String donationId;
    private String donorId;
    private double amount;
    private Date date;
    private String paymentMethod;
    private String receiptNumber;
    private DonationType donationType;
    private String notes;

    public enum DonationType {
        FOOD,
        DAILY_EXPENSES,
        CASH
    }

    // Constructors
    public Donation() {
    }

    public Donation(String donationId, String donorId, double amount, Date date,
            String paymentMethod, String receiptNumber, DonationType donationType,
            String notes) {
        this.donationId = donationId;
        this.donorId = donorId;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
        this.donationType = donationType;
        this.notes = notes;
    }

    // Getters and Setters
    public String getDonationId() {
        return donationId;
    }

    public void setDonationId(String donationId) {
        this.donationId = donationId;
    }

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public DonationType getDonationType() {
        return donationType;
    }

    public void setDonationType(DonationType donationType) {
        this.donationType = donationType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format(
            "╔══════════════════════════════════════════╗%n" +
            "║               Donation Details           ║%n" +
            "╠══════════════════════════════════════════╣%n" +
            "║ Donation ID     : %-23s ║%n" +
            "║ Donor ID        : %-23s ║%n" +
            "║ Amount (RM)     : %-23.2f ║%n" +
            "║ Date            : %-23s ║%n" +
            "║ Payment Method  : %-23s ║%n" +
            "║ Receipt Number  : %-23s ║%n" +
            "║ Donation Type   : %-23s ║%n" +
            "║ Notes           : %-23s ║%n" +
            "╚══════════════════════════════════════════╝",
            donationId,
            donorId,
            amount,
            new SimpleDateFormat("yyyy-MM-dd").format(date),
            paymentMethod,
            receiptNumber,
            donationType,
            notes
        );
    }
    

}
