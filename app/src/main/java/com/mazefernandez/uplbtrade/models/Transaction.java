package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable {
    @SerializedName("transaction_id")
    private Integer transactionId;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;
    @SerializedName("venue")
    private String venue;
    @SerializedName("item_id")
    private Integer itemId;
    @SerializedName("offer_id")
    private Integer offerId;
    @SerializedName("seller_id")
    private Integer sellerId;
    @SerializedName("buyer_id")
    private Integer buyerId;

    public Transaction(String date, String time, String venue, Integer itemId, Integer offerId, Integer sellerId, Integer buyerId) {
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.itemId = itemId;
        this.offerId = offerId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getVenue() { return venue; }

    public void setVenue(String venue) { this.venue = venue; }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }
}
