package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Customer implements Serializable {
    @SerializedName("customer_id")
    private Integer customerId;
    @SerializedName("image")
    private String image;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("contact_no")
    private String contactNo;
    @SerializedName("overall_rating")
    private Double overallRating;

    public Customer(){}

    public Customer(Integer customerId, String image, String firstName, String lastName, String email, String address, String contactNo, Double overallRating) {
        this.customerId = customerId;
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.contactNo = contactNo;
        this.overallRating = overallRating;
    }

    public Customer(String image, String firstName, String lastName, String email) {
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Customer(String address, String contactNo) {
        this.address = address;
        this.contactNo = contactNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public void setImage(String image) { this.image = image; }

    public String getImage() { return image; }
}
