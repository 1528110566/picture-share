package com.hf6z.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class User {
    @Getter
    @Setter
    @NotNull
    private String username;
    @Getter
    @Setter
    private String realName;
    @Getter
    @Setter
    @NotNull
    private String password;
    @Getter
    @Setter
    @NotNull
    private int flag;
    @Getter
    @Setter
    private String welcome;

//    public User() {
//        System.out.println(123);
//    }
//
//    public User(@NotNull String username, @NotNull String password) {
//        System.out.println(234);
//        this.username = username;
//        this.password = password;
//    }
//
//    public User(@NotNull String username, String realName, @NotNull String password, @NotNull int flag) {
//        System.out.println(345);
//        this.username = username;
//        this.realName = realName;
//        this.password = password;
//        this.flag = flag;
//    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", password='" + password + '\'' +
                ", flag=" + flag +
                ", welcome='" + welcome + '\'' +
                '}';
    }
}