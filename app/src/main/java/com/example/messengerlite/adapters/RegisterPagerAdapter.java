package com.example.messengerlite.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.messengerlite.fragments.authentication.register.BirthDayFragment;
import com.example.messengerlite.fragments.authentication.register.LoginInfoFragment;
import com.example.messengerlite.fragments.authentication.register.PictureFragment;

public class RegisterPagerAdapter extends FragmentStateAdapter
{
    public final static int LOGIN_INFO = 0;
    public final static int BIRTHDAY = 1;
    public final static int AVATAR = 2;

    private Fragment[] fragments;

    public RegisterPagerAdapter(@NonNull Fragment fragment)
    {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case LOGIN_INFO:
                return new LoginInfoFragment();
            case BIRTHDAY:
                return new BirthDayFragment();
            default:
                return new PictureFragment();
        }
    }

    @Override
    public int getItemCount()
    {
        return 3;
    }
}
