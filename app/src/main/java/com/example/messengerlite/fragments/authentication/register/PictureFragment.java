package com.example.messengerlite.fragments.authentication.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.messengerlite.R;
import com.example.messengerlite.dialogs.MediaPickerSheet;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.viewmodels.MainViewModel;

public class PictureFragment extends Fragment
{
    private ImageButton pickAvatarBtn;

    private MainViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.picture_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        pickAvatarBtn = view.findViewById(R.id.pickAvatarBtn);

        pickAvatarBtn.setOnClickListener(v ->
                new MediaPickerSheet().show(getActivity().getSupportFragmentManager(), "media picker"));

        mainViewModel.observeNewUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity userEntity)
            {
                Glide.with(getContext()).load(userEntity.getAvatarPath()).into(pickAvatarBtn);
            }
        });
    }
}