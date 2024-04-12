package com.example.onlinecomplaintmanagement.model;

public class complaint {
    private String id, userName, department, description, image, status, reply;
    private double latitude, longitude;
    private int priority;
    private boolean solved; // New field to indicate whether complaint is solved or not

    // Required default constructor for Firebase
    public complaint() {
        // Default constructor required for Firebase
    }

    public complaint(String id, String userName, String department, String description, String image, String status, String reply, double latitude, double longitude, int priority, boolean solved) {
        this.id = id;
        this.userName = userName;
        this.department = department;
        this.description = description;
        this.image = image;
        this.status = status;
        this.reply = reply;
        this.latitude = latitude;
        this.longitude = longitude;
        this.priority = priority;
        this.solved = solved;
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void setComplaintNumber(String s) {
    }
}
