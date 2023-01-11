package com.example.messengerlite.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengerlite.dtos.ChatBoxDTO;

import java.util.List;

public class PersonalWallViewModel extends ViewModel
{
    private MutableLiveData<List<ChatBoxDTO>> chatBoxes = new MutableLiveData<>(null);

    public void setChatBoxes(List<ChatBoxDTO> chatBoxes)
    {
        this.chatBoxes.setValue(chatBoxes);
    }

    public MutableLiveData<List<ChatBoxDTO>> observeChatBoxes()
    {
        return chatBoxes;
    }
}