package com.example.messengerlite.fragments.authentication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.messengerlite.AuthenticationState;
import com.example.messengerlite.R;
import com.example.messengerlite.dtos.UserNameDTO;
import com.example.messengerlite.interfaces.ChangeAuthenticationSceneListener;
import com.example.messengerlite.services.UserService;
import com.example.messengerlite.viewmodels.MainViewModel;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNameFragment extends DialogFragment
{
    private EditText userNameEditTxt;
    private AppCompatButton nextBtn, createBtn;

    private MainViewModel mainViewModel;

    private ChangeAuthenticationSceneListener listener;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_name_fragment, container, false);

        userNameEditTxt = view.findViewById(R.id.hometownEditTxt);
        nextBtn = view.findViewById(R.id.nextBtn);
        createBtn = view.findViewById(R.id.createBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        nextBtn.setOnClickListener(v ->
        {
            String str = userNameEditTxt.getText().toString();

            if(str.equals(""))
                str = " ";

            UserService.service.getAvatar(str).enqueue(
                    new Callback<UserNameDTO>()
            {
                @Override
                public void onResponse(Call<UserNameDTO> call, Response<UserNameDTO> response)
                {
                    if(response.code() == HttpURLConnection.HTTP_NOT_FOUND)
                    {
                        Toast.makeText(context,
                                "This user name not exists in our server!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserNameDTO dto = response.body();

                    mainViewModel.setUserNameAndAvatar(userNameEditTxt.getText().toString(), dto);
                    listener.changeState(AuthenticationState.PASSWORD);

                    dismiss();
                }

                @Override
                public void onFailure(Call<UserNameDTO> call, Throwable t)
                {
                    Toast.makeText(context,
                            "Failed to load your image. Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        });

        createBtn.setOnClickListener(v -> listener.changeState(AuthenticationState.REGISTER));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed()
            {
                getActivity().finish();
            }
        };

        dialog.getWindow().setWindowAnimations(R.style.fadeStyle);

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        listener = (ChangeAuthenticationSceneListener) context;
        this.context = context;
    }
}
