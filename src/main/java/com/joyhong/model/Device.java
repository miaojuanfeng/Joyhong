package com.joyhong.model;

import java.util.Date;

public class Device {
    private Integer id;
    
    private Integer orderId;

    private String deviceToken;

    private String deviceFcmToken;

    private Date loginTime;
    
    private Date heartbeatTime;
    
    private Date createDate;

    private Date modifyDate;

    private Integer deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

    public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getHeartbeatTime() {
		return heartbeatTime;
	}

	public void setHeartbeatTime(Date heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
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