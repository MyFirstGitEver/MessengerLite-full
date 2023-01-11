package com.example.messengerlite.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengerlite.dtos.PictureDTO;

import java.util.List;

public class StorageViewModel extends ViewModel
{
    private MutableLiveData<List<PictureDTO>> pictures = new MutableLiveData<>(null);
    private MutableLiveData<Integer> currentPage = new MutableLiveData<>(0);

    public void setPictures(List<PictureDTO> pictures)
    {
        this.pictures.setValue(pictures);
    }

    public MutableLiveData<List<PictureDTO>> observePictures()
    {
        return pictures;
    }

    public void setCurrentPage(int number)
    {
        currentPage.setValue(number);
    }

    public int getCurrentPage()
    {
        return currentPage.getValue();
    }
}
