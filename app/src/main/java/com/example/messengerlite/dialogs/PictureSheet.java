package com.example.messengerlite.dialogs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerlite.R;
import com.example.messengerlite.adapters.PictureListAdapter;
import com.example.messengerlite.dtos.SystemPictureDTO;
import com.example.messengerlite.interfaces.OnEndReachedListener;
import com.example.messengerlite.interfaces.SimpleCallBack;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PictureSheet extends BottomSheetDialogFragment
{
    private RecyclerView picList;
    private AppCompatButton sendBtn;

    private List<SystemPictureDTO> pictures;

    private final ActivityResultLauncher<String> requester = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> getAll());

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.picture_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        int pickCount = 0;

        if(savedInstanceState != null)
            pickCount = savedInstanceState.getInt("count");

        pictures = new ArrayList<>();

        picList = view.findViewById(R.id.picList);
        sendBtn = view.findViewById(R.id.sendBtn);

        picList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        picList.setAdapter(new PictureListAdapter(pictures, new SimpleCallBack()
        {
            @Override
            public void run()
            {
                if(((PictureListAdapter)picList.getAdapter()).getPickCount() == 0)
                    sendBtn.setVisibility(View.GONE);
                else
                    sendBtn.setVisibility(View.VISIBLE);
            }
        }, pickCount));

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED)
            requester.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        else
            getAll();

        sendBtn.setOnClickListener(v ->
        {
            ArrayList<SystemPictureDTO> pics = ((PictureListAdapter)picList.getAdapter()).getPickedPictures();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("picked", pics);
            bundle.putInt("from", getArguments().getInt("from"));
            bundle.putInt("to", getArguments().getInt("to"));

            getParentFragmentManager().setFragmentResult("send picked", bundle);

            dismiss();
        });
    }

    private void getAll()
    {
        int last = pictures.size();

        if(last != 0)
        {
            pictures.remove(pictures.size() - 1);
            picList.getAdapter().notifyItemRemoved(pictures.size() - 1);
        }

        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_ADDED };

        Cursor cursor = context.getContentResolver().query(
                uri,
                projection, null, null, "date_added desc");

        int dataCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int dateCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
        int widthCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH);
        int heightCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT);

        while (cursor.moveToNext())
        {
            pictures.add(new SystemPictureDTO(
                    cursor.getString(dataCol),
                    cursor.getLong(dateCol),false,
                    new Pair<>(cursor.getInt(widthCol), cursor.getInt(heightCol))));
        }

        picList.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt("count", ((PictureListAdapter)picList.getAdapter()).getPickCount());
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
    }
}