package com.picshare.model;

import java.util.Date;

public class Picture {
    private Integer id;

    private String createUser;

    private Date createDate;

    private String sourceLocation;

    private String zippedLocation;

    private String description;

    private String sourceLink;

    private String zippedLink;
    private Date updateDate;

    private Integer flag;
    private int readNum;
    private int likeNum;

    public Picture() {
    }

    public Picture(String zippedLink, String description) {
        this.zippedLink = zippedLink;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation == null ? null : sourceLocation.trim();
    }

    public String getZippedLocation() {
        return zippedLocation;
    }

    public void setZippedLocation(String zippedLocation) {
        this.zippedLocation = zippedLocation == null ? null : zippedLocation.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink == null ? null : sourceLink.trim();
    }

    public String getZippedLink() {
        return zippedLink;
    }

    public void setZippedLink(String zippedLink) {
        this.zippedLink = zippedLink == null ? null : zippedLink.trim();
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", createUser='" + createUser + '\'' +
                ", createDate=" + createDate +
                ", sourceLocation='" + sourceLocation + '\'' +
                ", zippedLocation='" + zippedLocation + '\'' +
                ", description='" + description + '\'' +
                ", sourceLink='" + sourceLink + '\'' +
                ", zippedLink='" + zippedLink + '\'' +
                ", updateDate=" + updateDate +
                ", flag=" + flag +
                ", readNum=" + readNum +
                ", likeNum=" + likeNum +
                '}';
    }
}