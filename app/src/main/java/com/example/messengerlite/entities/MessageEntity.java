package com.example.messengerlite.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MessageEntity implements Parcelable
{
    private Integer id;

    private int type, toId, fromId, replyTo;
    private boolean deleted, forwarded, isRead;
    private String content;
    private Date date;

    public MessageEntity(int type, int fromId, int toId, int replyTo,
                         boolean deleted, boolean forwarded, boolean isRead, String content, Date date)
    {
        this.type = type;
        this.toId = toId;
        this.fromId = fromId;
        this.replyTo = replyTo;
        this.deleted = deleted;
        this.forwarded = forwarded;
        this.content = content;
        this.date = date;
        this.isRead = isRead;
    }

    public MessageEntity(int type, String content)
    {
        this.type = type;
        this.content = content;
    }

    protected MessageEntity(Parcel in)
    {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        type = in.readInt();
        toId = in.readInt();
        fromId = in.readInt();
        replyTo = in.readInt();
        deleted = in.readByte() != 0;
        forwarded = in.readByte() != 0;
        isRead = in.readByte() != 0;
        content = in.readString();
        date = new Date(in.readLong());
    }

    public String getContent() {
        return content;
    }

    public Integer getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getToId() {
        return toId;
    }

    public int getFromId() {
        return fromId;
    }

    public int getReplyTo() {
        return replyTo;
    }

    public boolean getIsDeleted() {
        return deleted;
    }

    public boolean getIsForwarded() {
        return forwarded;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public Date getDate() {
        return date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public void setReplyTo(int replyTo) {
        this.replyTo = replyTo;
    }

    public void setIsDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setIsForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static final Creator<MessageEntity> CREATOR = new Creator<MessageEntity>() {
        @Override
        public MessageEntity createFromParcel(Parcel in) {
            return new MessageEntity(in);
        }

        @Override
        public MessageEntity[] newArray(int size) {
            return new MessageEntity[size];
        }
    };

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
        parcel.writeInt(type);
        parcel.writeInt(toId);
        parcel.writeInt(fromId);
        parcel.writeInt(replyTo);
        parcel.writeByte((byte) (deleted ? 1 : 0));
        parcel.writeByte((byte) (forwarded ? 1 : 0));
        parcel.writeByte((byte) (isRead ? 1 : 0));
        parcel.writeString(content);
        parcel.writeLong(date.getTime());
    }
}
