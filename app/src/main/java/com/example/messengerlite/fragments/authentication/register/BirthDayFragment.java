package com.example.messengerlite.fragments.authentication.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.messengerlite.R;
import com.example.messengerlite.Tools;
import com.example.messengerlite.adapters.RegisterPagerAdapter;
import com.example.messengerlite.viewmodels.MainViewModel;

import java.util.Date;

public class BirthDayFragment extends Fragment
{
    private DatePicker birthDayPicker;

    private MainViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.birthday_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        birthDayPicker = view.findViewById(R.id.birthDayPicker);

        mainViewModel.observeRegisterState().observe(getViewLifecycleOwner(), new Observer<Integer>()
        {
            @Override
            public void onChanged(Integer state)
            {
                if(state != RegisterPagerAdapter.AVATAR) // if next is avatar
                    return;

                mainViewModel.setUserBirthDay(Tools.getDateFromDatePicker(birthDayPicker));
            }
        });
    }
}
