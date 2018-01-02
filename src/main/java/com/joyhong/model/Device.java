package com.joyhong.model;

import java.util.Date;

public class Device {
    private Integer id;

    private String deviceToken;

    private String deviceFcmToken;

    private Date createDate;

    private Date modifyDate;

    private Integer deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken == null ? null : deviceToken.trim();
    }

    public String getDeviceFcmToken() {
        return deviceFcmToken;
    }

    public void setDeviceFcmToken(String deviceFcmToken) {
        this.deviceFcmToken = deviceFcmToken == null ? null : deviceFcmToken.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}