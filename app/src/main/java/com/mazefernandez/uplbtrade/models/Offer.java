package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

public class Offer {
    @SerializedName("offer_id")
    private Integer offerId;
    @SerializedName("price")
    private Double price;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("item_id")
    private Integer itemId;

    public Offer(Integer offerId, Double price, String status, String message, Integer itemId) {
        this.offerId = offerId;
        this.price = price;
        this.status = status;
        this.message = message;
        this.itemId = itemId;
    }

    public Integer getofferId() {
        return offerId;
    }

    public void setofferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getitemId() {
        return itemId;
    }

    public void setitemId(Integer itemId) {
        this.itemId = itemId;
    }
}
