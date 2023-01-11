package com.example.messengerlite.fragments.authentication;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.messengerlite.commontools.constants.AuthenticationState;
import com.example.messengerlite.R;
import com.example.messengerlite.dtos.UserNameDTO;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.interfaces.ChangeAuthenticationSceneListener;
import com.example.messengerlite.services.UserService;
import com.example.messengerlite.viewmodels.MainViewModel;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordFragment extends DialogFragment
{
    private ImageView userImg;
    private TextView userDisplayNameTxt;
    private AppCompatButton nextBtn;
    private EditText passwordEditTxt;

    private MainViewModel model;

    private ChangeAuthenticationSceneListener listener;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.password_fragment, container, false);

        userImg = view.findViewById(R.id.userImg);
        userDisplayNameTxt = view.findViewById(R.id.userDisplayNameTxt);
        nextBtn = view.findViewById(R.id.nextBtn);
        passwordEditTxt = view.findViewById(R.id.hometownEditTxt);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        UserNameDTO dto = model.getUserNameDTO();

        Glide.with(getContext()).load(Uri.parse(dto.getAvatarPath())).into(userImg);
        userDisplayNameTxt.setText(dto.getUserDisplayName());

        nextBtn.setOnClickListener(v ->
        {
            UserService.service.getUserInfo(model.getMyUser().getUserName(), passwordEditTxt.getText().toString())
                    .enqueue(new Callback<UserEntity>()
                    {
                        @Override
                        public void onResponse(Call<UserEntity> call, Response<UserEntity> response)
                        {
                            if(response.code() == HttpURLConnection.HTTP_NOT_FOUND)
                            {
                                Toast.makeText(context,
                                        "your password is wong! Please check again!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            model.completeLogin(response.body());
                            listener.changeState(AuthenticationState.AUTHENTICATED);
                            dismiss();
                        }

                        @Override
                        public void onFailure(Call<UserEntity> call, Throwable t)
                        {
                            Toast.makeText(context,
                                    "Sorry! We can't log you in for some reason :(", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
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
                listener.changeState(AuthenticationState.USER_NAME);
                dismiss();
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
