package com.example.messengerlite.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengerlite.commontools.Tools;
import com.example.messengerlite.dtos.InfoDTO;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.entities.WebLinkEntity;

import java.util.ArrayList;
import java.util.List;

public class StrangerDataViewModel extends ViewModel
{
    private MutableLiveData<List<Object>> infos = new MutableLiveData<>(null);

    public void initInformation(List<WebLinkEntity> links, UserEntity user)
    {
        List<Object> allInfos = new ArrayList<>();

        allInfos.add(new InfoDTO(InfoDTO.BIRTH_DAY, Tools.convertToReadable(user.getBirthDay())));
        allInfos.add(new InfoDTO(InfoDTO.HOME_TOWN, user.getHometown()));
        allInfos.add(new InfoDTO(InfoDTO.WORKING_CITY, user.getWorkingCity()));

        for(WebLinkEntity link : links)
            allInfos.add(link);

        infos.setValue(allInfos);
    }

    public MutableLiveData<List<Object>> observeInformation()
    {
        return infos;
    }
}