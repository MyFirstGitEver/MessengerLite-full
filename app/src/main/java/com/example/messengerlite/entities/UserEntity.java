package com.example.messengerlite.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserEntity implements Parcelable
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

    protected UserEntity(Parcel in)
    {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        userName = in.readString();
        userDisplayName = in.readString();
        password = in.readString();
        avatarPath = in.readString();
        workingCity = in.readString();
        hometown = in.readString();
        birthDay = new Date(in.readLong());
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

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>()
    {
        @Override
        public UserEntity createFromParcel(Parcel in)
        {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size)
        {
            return new UserEntity[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(userName);
        parcel.writeString(userDisplayName);
        parcel.writeString(password);
        parcel.writeString(avatarPath);
        parcel.writeString(workingCity);
        parcel.writeString(hometown);
        parcel.writeLong(birthDay.getTime());
    }
}
