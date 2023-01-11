package com.example.messengerlite.commontools;

import android.util.Log;

import com.example.messengerlite.dtos.PictureDTO;
import com.example.messengerlite.dtos.PictureMessageDTO;
import com.example.messengerlite.entities.MessageEntity;
import com.example.messengerlite.interfaces.MessageReceivedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class StompConnection
{
    private StompClient client;
    private Gson gson;

    public StompConnection()
    {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();

        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://" + Tools.IP_AND_PORT + "/ws");
        client.connect();
    }

    public void sendMessage(MessageEntity msg)
    {
        client.send("/app/sendMessage", gson.toJson(msg)).subscribe();
    }

    public void sendPicture(PictureMessageDTO dto)
    {
        client.send("/app/sendPicture", gson.toJson(dto)).subscribe();
    }

    public void listenText(int channelId, MessageReceivedListener listener)
    {
        client.topic("/user/" + channelId + "/text").subscribe(new Consumer<StompMessage>() {
            @Override
            public void accept(StompMessage stompMessage) throws Exception {
                listener.onNewMessage(stompMessage.getPayload());
            }
        });
    }

    public void listenPicture(int channelId, MessageReceivedListener listener)
    {
        client.topic("/user/" + channelId + "/picture").subscribe(new Consumer<StompMessage>() {
            @Override
            public void accept(StompMessage stompMessage) throws Exception {
                listener.onNewMessage(stompMessage.getPayload());
            }
        });
    }
}