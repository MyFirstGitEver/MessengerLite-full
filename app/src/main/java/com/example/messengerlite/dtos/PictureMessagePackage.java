package com.example.messengerlite.dtos;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.messengerlite.commontools.StompConnection;
import com.example.messengerlite.entities.MessageEntity;

import java.util.Date;
import java.util.Map;

public class PictureMessagePackage implements UploadCallback
{
    private int fromId, toId;
    private SystemPictureDTO picture;
    private StompConnection connection;

    public PictureMessagePackage(int fromId, int toId, SystemPictureDTO picture, StompConnection connection)
    {
        this.fromId = fromId;
        this.toId = toId;
        this.connection = connection;
        this.picture = picture;
    }

    @Override
    public void onStart(String requestId)
    {

    }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes)
    {

    }

    @Override
    public void onSuccess(String requestId, Map resultData)
    {
        MessageEntity message = new MessageEntity(
                MessageDTO.PICTURE,
                fromId,
                toId, -1, false, false, false,
                (String) resultData.get("url"), new Date());

        PictureMessageDTO dto = new PictureMessageDTO(false, message,
                picture.getDimension().first, picture.getDimension().second);

        connection.sendPicture(dto);
    }

    @Override
    public void onError(String requestId, ErrorInfo error)
    {

    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {

    }
}
