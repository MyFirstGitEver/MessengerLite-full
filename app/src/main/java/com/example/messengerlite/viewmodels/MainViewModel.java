package com.example.messengerlite.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengerlite.Tools;
import com.example.messengerlite.adapters.RegisterPagerAdapter;
import com.example.messengerlite.dtos.InfoDTO;
import com.example.messengerlite.dtos.UserNameDTO;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.entities.WebLinkEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainViewModel extends ViewModel
{
    private MutableLiveData<UserEntity> myUserInfo = new MutableLiveData<>(new UserEntity());
    private MutableLiveData<UserEntity> newUserInfo = new MutableLiveData<>(new UserEntity("https://res.cloudinary.com/dk8hbcln1/image/upload/v1672941338/default_tsd7os.png"));

    private MutableLiveData<List<Object>> infos = new MutableLiveData<>(null);

    private MutableLiveData<Integer> registerState = new MutableLiveData<>(new Integer(RegisterPagerAdapter.LOGIN_INFO));
    private MutableLiveData<Integer> appState = new MutableLiveData<>(-1);

    // getters

    public UserNameDTO getUserNameDTO()
    {
        UserEntity user = myUserInfo.getValue();

        return new UserNameDTO(user.getUserDisplayName(), user.getAvatarPath());
    }

    public UserEntity getNewUser()
    {
        return newUserInfo.getValue();
    }

    public UserEntity getMyUser()
    {
        return myUserInfo.getValue();
    }

    public List<Object> getPersonalData()
    {
        return infos.getValue();
    }

    //setters

    public void setUserNameAndAvatar(String userName, UserNameDTO dto)
    {
        UserEntity myUser = myUserInfo.getValue();

        myUser.setUserName(userName);
        myUser.setAvatarPath(dto.getAvatarPath());
        myUser.setUserDisplayName(dto.getUserDisplayName());
    }

    public void completeLogin(UserEntity userInfo)
    {
        UserEntity myUser = myUserInfo.getValue();

        myUser.setHometown(userInfo.getHometown());
        myUser.setId(userInfo.getId());
        myUser.setWorkingCity(userInfo.getWorkingCity());
        myUser.setBirthDay(userInfo.getBirthDay());
    }

    public void changeAvatar(String url)
    {
        myUserInfo.getValue().setAvatarPath(url);
        myUserInfo.setValue(myUserInfo.getValue());
    }

    public void changeNewUserAvatar(String url)
    {
        newUserInfo.getValue().setAvatarPath(url);
        newUserInfo.setValue(newUserInfo.getValue());
    }

    public void changePersonalData(UserEntity userInfo)
    {
        UserEntity currentInfo = myUserInfo.getValue();

        userInfo.setId(currentInfo.getId());
        userInfo.setUserName(currentInfo.getUserName());
        userInfo.setAvatarPath(currentInfo.getAvatarPath());

        myUserInfo.setValue(userInfo);
    }

    public void setUserInfo(String userName, String password, String userDisplayName)
    {
        UserEntity user = newUserInfo.getValue();

        user.setUserName(userName);
        user.setPassword(password);
        user.setUserDisplayName(userDisplayName);
    }

    public void lockState()
    {
        appState.setValue(0);
    }

    public void setUserBirthDay(Date date)
    {
        newUserInfo.getValue().setBirthDay(date);
    }

    public void initPersonalData(UserEntity myUser, List<WebLinkEntity> links)
    {
        List<Object> allInfos = new ArrayList<>();

        allInfos.add(new InfoDTO(InfoDTO.BIRTH_DAY, Tools.convertToReadable(myUser.getBirthDay())));
        allInfos.add(new InfoDTO(InfoDTO.HOME_TOWN, myUser.getHometown()));
        allInfos.add(new InfoDTO(InfoDTO.WORKING_CITY, myUser.getWorkingCity()));

        for(WebLinkEntity link : links)
            allInfos.add(link);

        allInfos.add(null);
        infos.setValue(allInfos);
    }

    public void reloadLinks()
    {
        infos.setValue(null);
    }

    public void clearRegistrationState()
    {
        newUserInfo.setValue(new UserEntity("https://res.cloudinary.com/dk8hbcln1/image/upload/v1672941338/default_tsd7os.png"));
        registerState.setValue(RegisterPagerAdapter.LOGIN_INFO);
    }

    // states
    public void incrementRegisterState()
    {
        registerState.setValue(registerState.getValue() + 1);
    }
    public void typeUserInfoAgain()
    {
        registerState.setValue(RegisterPagerAdapter.LOGIN_INFO);
    }

    // Observers
    public MutableLiveData<Integer> observeAppState()
    {
        return appState;
    }
    public MutableLiveData<UserEntity> observeMyUser()
    {
        return myUserInfo;
    }
    public MutableLiveData<Integer> observeRegisterState()
    {
        return registerState;
    }
    public MutableLiveData<List<Object>> observeInfoList()
    {
        return infos;
    }
    public MutableLiveData<UserEntity> observeNewUser()
    {
        return newUserInfo;
    }
}