package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class TransactionTracking {
    @SerializedName("trackingId")
    private Integer trackingId;
    @SerializedName("status")
    private String status;
    @SerializedName("date")
    private Timestamp date;
    @SerializedName("transaction_id")
    private Integer transactionId;

    public TransactionTracking(Integer trackingId, String status, Timestamp date, Integer transactionId) {
        this.trackingId = trackingId;
        this.status = status;
        this.date = date;
        this.transactionId = transactionId;
    }

    public Integer gettrackingId() {
        return trackingId;
    }

    public void settrackingId(Integer trackingId) {
        this.trackingId = trackingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer gettransactionId() {
        return transactionId;
    }

    public void settransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
}
