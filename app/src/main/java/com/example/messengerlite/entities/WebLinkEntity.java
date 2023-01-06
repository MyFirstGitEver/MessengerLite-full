package com.example.messengerlite.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class WebLinkEntity implements Cloneable, Parcelable
{
    private Integer id;

    private String title, link;
    private int ownerId;

    public WebLinkEntity()
    {

    }

    public WebLinkEntity(String title, String link, int ownerId)
    {
        this.title = title;
        this.link = link;
        this.ownerId = ownerId;
    }

    protected WebLinkEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        link = in.readString();
        ownerId = in.readInt();
    }

    public static final Creator<WebLinkEntity> CREATOR = new Creator<WebLinkEntity>() {
        @Override
        public WebLinkEntity createFromParcel(Parcel in) {
            return new WebLinkEntity(in);
        }

        @Override
        public WebLinkEntity[] newArray(int size) {
            return new WebLinkEntity[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public WebLinkEntity clone()
    {
        WebLinkEntity cloned = new WebLinkEntity(
                title,
                link,
                ownerId);
        cloned.setId(id);

        return cloned;
    }

    @Override
    public int describeContents() {
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
        parcel.writeString(title);
        parcel.writeString(link);
        parcel.writeInt(ownerId);
    }
}
