package com.example.messengerlite.fragments.image;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.messengerlite.commontools.constants.CaptureUseCase;
import com.example.messengerlite.R;
import com.example.messengerlite.services.UserService;
import com.example.messengerlite.viewmodels.MainViewModel;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageCaptureFragment extends DialogFragment
{
    private PreviewView cameraView;
    private ImageButton doneBtn;

    private ImageCapture imageCapture;
    private ProcessCameraProvider provider;

    private MainViewModel mainViewModel;

    private final ActivityResultLauncher<String> requester = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted ->
                    openCamera());

    private boolean processing = false;
    private int useCase = CaptureUseCase.CHANGE_AVATAR_USE_CASE;

    private Context context;

    private UploadCallback uploadCallback = new UploadCallback()
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
            if(useCase == CaptureUseCase.REGISTER_USE_CASE)
                mainViewModel.changeNewUserAvatar((String) resultData.get("url"));
            else
            {
                mainViewModel.changeAvatar((String) resultData.get("url"));
                UserService.service.updateInfo(mainViewModel.getMyUser()).enqueue(new Callback<Boolean>()
                {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response)
                    {
                        Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t)
                    {
                        Toast.makeText(context, "Can't save your avatar for some reason! :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dismiss();
        }

        @Override
        public void onError(String requestId, ErrorInfo error)
        {
            Toast.makeText(context, "Can't upload your image!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReschedule(String requestId, ErrorInfo error)
        {
        }
    };

    public ImageCaptureFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.image_capture_fragment, container, false);

        cameraView = view.findViewById(R.id.cameraView);
        doneBtn = view.findViewById(R.id.doneBtn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        useCase = getArguments().getInt("useCase");

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(getContext());

        future.addListener(() ->
        {
            try
            {
                provider = ProcessCameraProvider.getInstance(getContext()).get();
                openCamera();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            requester.launch(Manifest.permission.CAMERA);

        doneBtn.setOnClickListener((View v) ->
        {
            processing = true;
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(new File(getContext().getFilesDir(), "image")).build();

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getContext()),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults)
                        {
                            provider.unbindAll();

                            File f = new File(getContext().getFilesDir(), "image");

                            f.getAbsolutePath();

                            MediaManager.get().upload(f.getAbsolutePath()).
                                    unsigned("j4ut3xji").callback(uploadCallback).dispatch();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception)
                        {

                        }
                    });
        });
    }

    private void openCamera()
    {
        if(provider == null)
        {
            Toast.makeText(context, "Please wait!", Toast.LENGTH_SHORT).show();
            return;
        }

        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(cameraView.getSurfaceProvider());

        CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(cameraView.getDisplay().getRotation())
                        .build();

        try
        {
            provider.unbindAll();
            provider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

        } catch(Exception e)
        {
            Log.e("tag", "Use case binding failed", e);
        }
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
                if(processing)
                    return;

                dismiss();
            }
        };

        dialog.getWindow().setWindowAnimations(R.style.fadeStyle);

        return dialog;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
    }
}