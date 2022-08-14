package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class CustomerReview {

    @SerializedName("review_id")
    private Integer reviewId;
    @SerializedName("rating")
    private Double rating;
    @SerializedName("review")
    private String review;
    @SerializedName("date")
    private Date date;
    @SerializedName("rater_id")
    private Integer raterId;
    @SerializedName("customer_id")
    private Integer customerId;
    @SerializedName("transaction_id")
    private Integer transactionId;

    public CustomerReview(Double rating, String review, Integer raterId, Integer customerId, Integer transactionId) {
        this.rating = rating;
        this.review = review;
        this.raterId = raterId;
        this.customerId = customerId;
        this.transactionId = transactionId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
}
