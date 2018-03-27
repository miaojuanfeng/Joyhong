package com.joyhong.model;

import java.util.Date;

public class Version {
    private Integer id;

    private String name;

    private Integer lastVersion;

    private String downloadLink;

    private Date createDate;

    private Date modifyDate;

    private Integer deleted;
    
    private String versionDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink == null ? null : downloadLink.trim();
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
    
    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc == null ? null : versionDesc.trim();
    }
}