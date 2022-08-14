package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("tag_id")
    private Integer tagId;
    @SerializedName("tag_name")
    private String tagName;
    @SerializedName("item_id")
    private Integer itemId;

    public Tag(String tagName, Integer itemId) {
        this.tagName = tagName;
        this.itemId = itemId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
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
