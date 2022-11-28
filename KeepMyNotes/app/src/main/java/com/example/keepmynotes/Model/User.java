package com.example.keepmynotes.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "User")
public class User implements Serializable {

    @PrimaryKey()
    @NonNull

    private String email;
    private String Uid;
    private String name;
    private String password;
    private Boolean isVertify;

    public User() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getVertify() {
        return isVertify;
    }

    public void setVertify(Boolean vertify) {
        isVertify = vertify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", Uid='" + Uid + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", isVertify=" + isVertify +
                '}';
    }
}
