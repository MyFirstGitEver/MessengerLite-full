package com.example.messengerlite.entities;

import java.util.Date;

public class UserEntity
{
    private Integer id;
    private String userName, userDisplayName, password, avatarPath, workingCity, hometown;
    private Date birthDay;

    public UserEntity()
    {

    }

    public UserEntity(String avatarPath)
    {
        this.avatarPath = avatarPath;
    }

    public UserEntity(String userDisplayName, String workingCity, String hometown, Date birthDay)
    {
        this.userDisplayName = userDisplayName;
        this.workingCity = workingCity;
        this.hometown = hometown;
        this.birthDay = birthDay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getWorkingCity() {
        return workingCity;
    }

    public void setWorkingCity(String workingCity) {
        this.workingCity = workingCity;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
}
