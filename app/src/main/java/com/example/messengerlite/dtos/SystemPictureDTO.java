package com.example.messengerlite.dtos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

public class SystemPictureDTO implements Parcelable
{
    private String path;
    private long epochTimeStamp;
    private boolean isPicked;
    private Pair<Integer, Integer> dimension;

    public SystemPictureDTO(String path, long epochTimeStamp, boolean isPicked, Pair<Integer, Integer> dimension)
    {
        this.path = path;
        this.epochTimeStamp = epochTimeStamp;
        this.isPicked = isPicked;
        this.dimension = dimension;
    }

    protected SystemPictureDTO(Parcel in) {
        path = in.readString();
        epochTimeStamp = in.readLong();
        isPicked = in.readByte() != 0;

        int first = in.readInt();
        int second = in.readInt();

        dimension = new Pair<>(first, second);
    }

    public static final Creator<SystemPictureDTO> CREATOR = new Creator<SystemPictureDTO>() {
        @Override
        public SystemPictureDTO createFromParcel(Parcel in) {
            return new SystemPictureDTO(in);
        }

        @Override
        public SystemPictureDTO[] newArray(int size) {
            return new SystemPictureDTO[size];
        }
    };

    public String getPath()
    {
        return path;
    }

    public long getEpochTimeStamp()
    {
        return epochTimeStamp;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public Pair<Integer, Integer> getDimension() {
        return dimension;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeLong(epochTimeStamp);
        parcel.writeByte((byte) (isPicked ? 1 : 0));

        parcel.writeInt(dimension.first);
        parcel.writeInt(dimension.second);
    }
}
