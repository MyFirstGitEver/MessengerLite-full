package com.example.messengerlite.dtos;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.messengerlite.entities.MessageEntity;

public class MessageDTO implements Parcelable
{
    public static final int TEXT = 0;
    public static final int PICTURE = 1;
    public static final int LIKE = 2;
    public static final int STAMP = 3;
    public static final int NULL = 4;

    private boolean isMyUser;
    private MessageEntity message;

    public MessageDTO(boolean isMyUser, MessageEntity message)
    {
        this.isMyUser = isMyUser;
        this.message = message;
    }

    public MessageDTO(int type, String stamp)
    {
        message = new MessageEntity(type, stamp);
    }

    protected MessageDTO(Parcel in)
    {
        isMyUser = in.readByte() != 0;
        message = in.readParcelable(MessageEntity.class.getClassLoader());
    }

    public static final Creator<MessageDTO> CREATOR = new Creator<MessageDTO>()
    {
        @Override
        public MessageDTO createFromParcel(Parcel in)
        {
            return new MessageDTO(in);
        }

        @Override
        public MessageDTO[] newArray(int size)
        {
            return new MessageDTO[size];
        }
    };

    public boolean getIsMyUser()
    {
        return isMyUser;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setIsMyUser(boolean isMyUser)
    {
        this.isMyUser = isMyUser;
    }

    public void setMessage(MessageEntity message)
    {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isMyUser ? 1 : 0));
        parcel.writeParcelable(message, i);
    }
}