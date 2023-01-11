package com.example.messengerlite.interfaces;

import com.example.messengerlite.dtos.ChatBoxDTO;
import com.example.messengerlite.entities.UserEntity;

public interface ChatBoxListener
{
    void onChatBoxClick(UserEntity user);
}