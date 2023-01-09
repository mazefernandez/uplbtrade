package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class CustomerReport {

    @SerializedName("report_id")
    private Integer reportId;
    @SerializedName("message")
    private String message;
    @SerializedName("date")
    private Date date;
    @SerializedName("reporter_id")
    private Integer reporterId;
    @SerializedName("customer_id")
    private Integer customerId;

    public CustomerReport(String message, int reporterId, int customerId) {
        this.message = message;
        this.reporterId = reporterId;
        this.customerId = customerId;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }
}
