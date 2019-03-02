package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("tag_id")
    private Integer tagId;
    @SerializedName("tag_name")
    private String tagName;
    @SerializedName("item_id")
    private Integer itemId;

    public Tag(Integer tagId, String tagName, Integer itemId) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.itemId = itemId;
    }

    public Integer gettagId() {
        return tagId;
    }

    public void settagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String gettagName() {
        return tagName;
    }

    public void settagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getitemId() {
        return itemId;
    }

    public void setitemId(Integer itemId) {
        this.itemId = itemId;
    }
}
