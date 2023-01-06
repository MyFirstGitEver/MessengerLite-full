package com.example.messengerlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.messengerlite.fragments.authentication.PasswordFragment;
import com.example.messengerlite.fragments.authentication.RegisterFragment;
import com.example.messengerlite.fragments.authentication.UserNameFragment;
import com.example.messengerlite.fragments.chat.PersonalWallFragment;
import com.example.messengerlite.interfaces.ChangeAuthenticationSceneListener;
import com.example.messengerlite.viewmodels.MainViewModel;

public class MainActivity extends AppCompatActivity implements ChangeAuthenticationSceneListener
{
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.observeAppState().observe(this, new Observer<Integer>()
        {
            @Override
            public void onChanged(Integer state)
            {
                if(state == -1)
                {
                    new UserNameFragment().show(getSupportFragmentManager(), "user name");
                    mainViewModel.lockState();
                }
            }
        });
    }

    @Override
    public void changeState(int state)
    {
        if(state == AuthenticationState.USER_NAME)
            new UserNameFragment().show(getSupportFragmentManager(), "user name");
        else if(state == AuthenticationState.PASSWORD)
            new PasswordFragment().show(getSupportFragmentManager(), "password");
        else if(state == AuthenticationState.AUTHENTICATED)
            new PersonalWallFragment().show(getSupportFragmentManager(), "personal wall");
        else
            new RegisterFragment().show(getSupportFragmentManager(), "register");
    }
}