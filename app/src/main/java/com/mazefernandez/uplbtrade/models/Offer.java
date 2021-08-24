package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Offer implements Serializable{
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
    @SerializedName("buyer_id")
    private Integer buyerId;
    @SerializedName("seller_id")
    private Integer sellerId;

    public Offer(Double price, String status, String message, Integer itemId, Integer buyerId, Integer sellerId) {
        this.price = price;
        this.status = status;
        this.message = message;
        this.itemId = itemId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
    }

    public Offer(Offer offer, int offerId) {
        this.offerId = offerId;
        this.price = offer.price;
        this.status = offer.status;
        this.message = offer.message;
        this.itemId = offer.itemId;
        this.buyerId = offer.buyerId;
        this.sellerId = offer.sellerId;
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

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }
}
