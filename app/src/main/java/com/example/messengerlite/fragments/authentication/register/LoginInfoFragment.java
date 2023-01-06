package com.example.messengerlite.fragments.authentication.register;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.messengerlite.R;
import com.example.messengerlite.adapters.RegisterPagerAdapter;
import com.example.messengerlite.services.UserService;
import com.example.messengerlite.viewmodels.MainViewModel;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginInfoFragment extends Fragment
{
    private EditText userNameEditTxt, passwordEditTxt, retypeEditTxt, userDisplayNameEditTxt;

    private MainViewModel mainViewModel;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.login_info_fragment, container, false);

        userNameEditTxt = view.findViewById(R.id.birthDayEditTxt);
        passwordEditTxt = view.findViewById(R.id.hometownEditTxt);
        retypeEditTxt = view.findViewById(R.id.workingEditTxt);
        userDisplayNameEditTxt = view.findViewById(R.id.userDisplayNameEditTxt);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        mainViewModel.observeRegisterState().observe(getViewLifecycleOwner(), new Observer<Integer>()
        {
            @Override
            public void onChanged(Integer state)
            {
                if(state != RegisterPagerAdapter.BIRTHDAY) // if next is birthDay
                    return;

                if(!retypeEditTxt.getText().toString().equals(passwordEditTxt.getText().toString()))
                {
                    Toast.makeText(context,
                            "Your retyped password must match with the first password", Toast.LENGTH_SHORT).show();
                    mainViewModel.typeUserInfoAgain();

                    return;
                }

                String str = userNameEditTxt.getText().toString();

                if(str.equals(""))
                    str = " ";

                UserService.service.checkUserName(str).enqueue(new Callback<Boolean>()
                {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response)
                    {
                        if(response.code() == HttpURLConnection.HTTP_ACCEPTED)
                            return;

                        if(!response.body())
                        {
                            mainViewModel.setUserInfo(
                                    userNameEditTxt.getText().toString(),
                                    passwordEditTxt.getText().toString(),
                                    userDisplayNameEditTxt.getText().toString());

                            return;
                        }

                        Toast.makeText(context, "User name already exists", Toast.LENGTH_SHORT).show();
                        mainViewModel.typeUserInfoAgain();
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t)
                    {
                        Toast.makeText(context,
                                "Check your internet connection! We can't continue without internet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
    }
}
