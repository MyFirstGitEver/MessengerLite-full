package com.example.messengerlite.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.messengerlite.commontools.constants.CaptureUseCase;
import com.example.messengerlite.R;
import com.example.messengerlite.fragments.image.ImageCaptureFragment;
import com.example.messengerlite.interfaces.ChangeAuthenticationSceneListener;
import com.example.messengerlite.viewmodels.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Map;

public class MediaPickerSheet extends BottomSheetDialogFragment
{
    private AppCompatButton libBtn, newBtn;

    private ChangeAuthenticationSceneListener listener;

    private final UploadCallback uploadCallback = new UploadCallback()
    {
        @Override
        public void onStart(String requestId)
        {

        }

        @Override
        public void onProgress(String requestId, long bytes, long totalBytes)
        {

        }

        @Override
        public void onSuccess(String requestId, Map resultData)
        {
            mainViewModel.changeNewUserAvatar((String) resultData.get("url"));
            dismiss();
        }

        @Override
        public void onError(String requestId, ErrorInfo error)
        {

        }

        @Override
        public void onReschedule(String requestId, ErrorInfo error)
        {
        }
    };

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->
            {
                if(result.getData() == null)
                    return;

                MediaManager.get().upload(result.getData().getData()).
                        unsigned("j4ut3xji").callback(uploadCallback).dispatch();
            });

    private MainViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.media_picker_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        libBtn = view.findViewById(R.id.libBtn);
        newBtn = view.findViewById(R.id.newBtn);

        libBtn.setOnClickListener(v ->
        {
            Intent i = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);

            launcher.launch(i);
        });

        newBtn.setOnClickListener(v ->
        {
            ImageCaptureFragment fragment =
                    new ImageCaptureFragment();

            Bundle bundle = new Bundle();

            bundle.putInt("useCase", CaptureUseCase.REGISTER_USE_CASE);
            fragment.setArguments(bundle);
            fragment.show(requireActivity().getSupportFragmentManager(), "capture");
        });
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        listener = (ChangeAuthenticationSceneListener) context;
    }
}