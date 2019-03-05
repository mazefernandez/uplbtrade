package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

public class Customer {
    @SerializedName("customer_id")
    private Integer customerId;
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

    public Customer(Integer customerId, String firstName, String lastName, String email, String address, String contactNo, Double overallRating) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.contactNo = contactNo;
        this.overallRating = overallRating;
    }

    public Customer(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Integer getcustomerId() {
        return customerId;
    }

    public void setcustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
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

    public String getcontactNo() {
        return contactNo;
    }

    public void setcontactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Double getoverallRating() {
        return overallRating;
    }

    public void setoverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }
}
