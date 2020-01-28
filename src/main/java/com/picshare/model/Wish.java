package com.picshare.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;

public class Wish  implements Comparable<Wish>{
    private Integer id;

    private String username;

    private String detail;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wish wish = (Wish) o;
        return Objects.equals(id, wish.id) &&
                Objects.equals(username, wish.username) &&
                Objects.equals(detail, wish.detail) &&
                Objects.equals(createDate, wish.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, detail, createDate);
    }

    @Override
    public int compareTo(Wish o) {
        return o.getCreateDate().compareTo(this.getCreateDate());
    }
}