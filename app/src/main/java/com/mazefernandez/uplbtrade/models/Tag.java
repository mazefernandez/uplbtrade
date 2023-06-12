package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("item_id")
    private Integer itemId;
    @SerializedName("tag_name")
    private String tagName;

    public Tag(String tagName, Integer itemId) {
        this.tagName = tagName;
        this.itemId = itemId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
