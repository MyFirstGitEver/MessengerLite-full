package com.example.messengerlite.fragments.image;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.messengerlite.commontools.constants.CaptureUseCase;
import com.example.messengerlite.R;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.viewmodels.MainViewModel;

public class ImageViewFragment extends DialogFragment
{
    private ImageView picImg;
    private AppCompatButton changeBtn;

    private boolean replaceable;

    private MainViewModel mainViewModel;

    public ImageViewFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.image_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        replaceable = getArguments().getBoolean("replaceable");

        picImg = view.findViewById(R.id.picImg);
        changeBtn = view.findViewById(R.id.changeBtn);

        mainViewModel.observeMyUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>()
        {
            @Override
            public void onChanged(UserEntity userEntity)
            {
                Glide.with(getContext()).load(userEntity.getAvatarPath()).into(picImg);
            }
        });

        if(replaceable)
        {
            changeBtn.setOnClickListener(v ->
            {
                ImageCaptureFragment fragment =
                        new ImageCaptureFragment();

                Bundle bundle = new Bundle();

                bundle.putInt("useCase", CaptureUseCase.CHANGE_AVATAR_USE_CASE);
                fragment.setArguments(bundle);
                fragment.show(requireActivity().getSupportFragmentManager(), "capture");
            });
        }
        else
            changeBtn.setVisibility(View.GONE);
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
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().setWindowAnimations(R.style.slideStyle);

        return dialog;
    }
}