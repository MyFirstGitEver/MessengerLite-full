package com.example.messengerlite.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengerlite.commontools.Tools;
import com.example.messengerlite.dtos.MessageDTO;
import com.example.messengerlite.dtos.PictureMessageDTO;
import com.example.messengerlite.entities.MessageEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainChatViewModel extends ViewModel
{
    private static final int TEN_MINUTES = 300_000;

    private MutableLiveData<List<MessageDTO>> messages = new MutableLiveData<>(null);
    private MutableLiveData<Integer> pageNumber = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> anyChanges = new MutableLiveData<>(false);

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLastPage = new MutableLiveData<>(false);

    public int concat(List<PictureMessageDTO> messages)
    {
        List<MessageDTO> filtered = new ArrayList<>();
        int offset = 0;

        for(PictureMessageDTO message : messages)
        {
            if(message.getMessage().getType() != MessageDTO.PICTURE)
                filtered.add(new MessageDTO(message.getIsMyUser(), message.getMessage()));
            else
                filtered.add(message);
        }

        if(getLastSeen() != null)
        {
            long diff = getLastSeen().getDate().getTime() -
                    this.messages.getValue().get(0).getMessage().getDate().getTime();

            if(diff >= TEN_MINUTES)
            {
                filtered.add(0, new MessageDTO(
                        MessageDTO.STAMP,
                        Tools.fromToday(this.messages.getValue().get(0).getMessage().getDate())));
                offset++;
            }
        }

        for(int i=0;i<filtered.size() - 1;i++)
        {
            MessageDTO message = filtered.get(i);

            if(message.getMessage().getType() == MessageDTO.STAMP)
                continue;

            MessageDTO nextMessage = filtered.get(i + 1);

            if(message.getMessage().getDate().getTime() -
                    nextMessage.getMessage().getDate().getTime() >= TEN_MINUTES)
            {
                filtered.add(i + 1, new MessageDTO(
                        MessageDTO.STAMP,
                        Tools.fromToday(message.getMessage().getDate())));
                offset++;
            }
        }

        if(this.messages.getValue() == null)
            this.messages.setValue(filtered);
        else
            this.messages.getValue().addAll(filtered);

        if(messages.size() == 10)
            this.messages.getValue().add(null);
        else
        {

            this.isLastPage.setValue(true);
        }

        return offset;
    }

    public void removeFooter()
    {
        if(messages.getValue() == null)
            return;

        this.messages.getValue().remove(this.messages.getValue().size() - 1);
    }

    public MutableLiveData<List<MessageDTO>> observeMessages()
    {
        return messages;
    }

    public int getCurrentPageNumber()
    {
        return pageNumber.getValue();
    }

    public int getLastPosition()
    {
        if(messages.getValue() == null)
            return -1;

        return messages.getValue().size() - 1;
    }

    public boolean isRacing()
    {
        return messages.getValue() == null;
    }

    public boolean isLoading()
    {
        return isLoading.getValue();
    }

    public void incrementPageNumber()
    {
        pageNumber.setValue(pageNumber.getValue() + 1);
    }

    public void setLoading(boolean isLoading)
    {
        this.isLoading.setValue(isLoading);
    }

    public void setLastPage(boolean isLastPage)
    {
        this.isLoading.setValue(isLastPage);
    }

    public boolean isLastPage()
    {
        return isLastPage.getValue();
    }

    public MessageDTO getLastMessage()
    {
        for(MessageDTO message : messages.getValue())
        {
            if(message.getMessage().getType() != MessageDTO.STAMP)
                return message;
        }

        return null;
    }

    public void notifyChanges()
    {
        anyChanges.setValue(true);
    }

    public boolean checkIfAnyChangesHappen()
    {
        return anyChanges.getValue();
    }

    private MessageEntity getLastSeen()
    {
        if(this.messages.getValue() == null || this.messages.getValue().size() == 0)
            return null;

        return this.messages.getValue().get(messages.getValue().size() - 1).getMessage();
    }
}