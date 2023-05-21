package com.mazefernandez.uplbtrade.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class ItemReport {
    @SerializedName("report_item_id")
    private Integer reportId;
    @SerializedName("message")
    private String message;
    @SerializedName("date")
    private Date date;
    @SerializedName("reporter_id")
    private Integer reporterId;
    @SerializedName("item_id")
    private Integer itemId;

    public ItemReport(String message, int reporterId, int itemId) {
        this.message = message;
        this.reporterId = reporterId;
        this.itemId = itemId;
    }
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }
}
