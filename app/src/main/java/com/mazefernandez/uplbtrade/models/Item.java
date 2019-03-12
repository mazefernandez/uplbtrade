package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Blob;

public class Item implements Serializable{
    @SerializedName("item_id")
    private Integer itemId;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("price")
    private Double price;
    @SerializedName("image")
    private String image;
    @SerializedName("condition")
    private String condition;
    @SerializedName("customer_id")
    private Integer customerId;

    public Item(String name, String description, Double price, String image, String condition, Integer customerId){
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.condition = condition;
        this.customerId = customerId;
    }

    public Item(String name, String description, Double price, String image, String condition) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.condition = condition;
    }

    public Integer getitemId() {
        return itemId;
    }

    public void setitemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return name;
    }

    public void setItemName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getcustomerId() {
        return customerId;
    }

    public void setcustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}


