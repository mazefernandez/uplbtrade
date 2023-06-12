package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Id implements Serializable {
    @SerializedName("item_id")
    private Integer itemId;

    public Id() {

    }
    public Integer getItemId() {
        return itemId;
    }
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
