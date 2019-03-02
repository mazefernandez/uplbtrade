package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

public class Transaction {
    @SerializedName("transaction_id")
    private Integer transactionId;
    @SerializedName("buyer_rating")
    private Double buyerRating;
    @SerializedName("buyer_review")
    private String buyerReview;
    @SerializedName("date")
    private java.sql.Date date;
    @SerializedName("item_id")
    private Integer itemId;
    @SerializedName("offer_id")
    private Integer offerId;
    @SerializedName("seller_id")
    private Integer sellerId;
    @SerializedName("buyer_id")
    private Integer buyerId;

    public Transaction(Integer transactionId, Double buyerRating, String buyerReview, java.sql.Date date, Integer itemId, Integer offerId, Integer sellerId, Integer buyerId) {
        this.transactionId = transactionId;
        this.buyerRating = buyerRating;
        this.buyerReview = buyerReview;
        this.date = date;
        this.itemId = itemId;
        this.offerId = offerId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    public Integer gettransactionId() {
        return transactionId;
    }

    public void settransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Double getbuyerRating() {
        return buyerRating;
    }

    public void setbuyerRating(Double buyerRating) {
        this.buyerRating = buyerRating;
    }

    public String getbuyerReview() {
        return buyerReview;
    }

    public void setbuyerReview(String buyerReview) {
        this.buyerReview = buyerReview;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public Integer getitemId() {
        return itemId;
    }

    public void setitemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getofferId() {
        return offerId;
    }

    public void setofferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getsellerId() {
        return sellerId;
    }

    public void setsellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getbuyerId() {
        return buyerId;
    }

    public void setbuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }
}
