package com.example.messengerlite.dtos;

import com.example.messengerlite.entities.MessageEntity;
import com.example.messengerlite.entities.UserEntity;

public class ChatBoxDTO
{
    private UserEntity user;
    private MessageEntity lastMessage;

    public ChatBoxDTO(UserEntity user, MessageEntity lastMessage) {
        this.user = user;
        this.lastMessage = lastMessage;
    }

    public UserEntity getUser() {
        return user;
    }

    public MessageEntity getLastMessage() {
        return lastMessage;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setLastMessage(MessageEntity lastMessage) {
        this.lastMessage = lastMessage;
    }
}