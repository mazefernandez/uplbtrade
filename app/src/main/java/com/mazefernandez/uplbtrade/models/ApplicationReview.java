package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class ApplicationReview {

    @SerializedName("review_id")
    private Integer reviewId;
    @SerializedName("rating")
    private Double rating;
    @SerializedName("review")
    private String review;
    @SerializedName("date")
    private Date date;
    @SerializedName("customer_id")
    private Integer customerId;
    @SerializedName("rater_id")
    private Integer raterId;

    public ApplicationReview(Integer reviewId, Double rating, String review, Date date, Integer customerId, Integer raterId) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.review = review;
        this.date = date;
        this.customerId = customerId;
        this.raterId = raterId;
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

    public Integer getRaterId() {
        return raterId;
    }

    public void setRaterId(Integer raterId) {
        this.raterId = raterId;
    }
}
