package com.example.messengerlite.fragments.storage;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.messengerlite.R;
import com.example.messengerlite.adapters.MainPicturePagerAdapter;
import com.example.messengerlite.dtos.PictureDTO;
import com.example.messengerlite.services.MessageService;
import com.example.messengerlite.viewmodels.StorageViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StorageViewFragment extends DialogFragment
{
    private StorageViewModel storageViewModel;
    private Context context;

    private ViewPager2 mainPager, miniPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.storage_view_fragment, container, false);

        mainPager = view.findViewById(R.id.mainPager);
        miniPager = view.findViewById(R.id.miniPager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mainPager.setAdapter(new MainPicturePagerAdapter(
                new ArrayList<>(),
                displayMetrics.widthPixels, 450, () -> -1));

        miniPager.setAdapter(new MainPicturePagerAdapter(
                new ArrayList<>(),
                150, 200, () -> mainPager.getCurrentItem()));

        miniPager.setOffscreenPageLimit(3);

        miniPager.setPageTransformer((page, position) -> {
            float myOffset = position * -(2 * 30 + 150);
            if (position < -1) {
                page.setTranslationX(-myOffset);
            } else if (position <= 1) {
                float scaleFactor = Math.max(0.7f, 1 - Math.abs(position - 0.14285715f));
                page.setTranslationX(myOffset);
                page.setScaleY(scaleFactor);
                page.setAlpha(scaleFactor);
            } else {
                page.setAlpha(0);
                page.setTranslationX(myOffset);
            }
        });

        mainPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);

                miniPager.setCurrentItem(position);

                miniPager.getAdapter().notifyItemChanged(storageViewModel.getCurrentPage());
                miniPager.getAdapter().notifyItemChanged(position);

                storageViewModel.setCurrentPage(position);
            }
        });

        miniPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);

                mainPager.setCurrentItem(position);

                miniPager.getAdapter().notifyItemChanged(storageViewModel.getCurrentPage());
                miniPager.getAdapter().notifyItemChanged(position);

                storageViewModel.setCurrentPage(position);
            }
        });

        storageViewModel = new ViewModelProvider(this).get(StorageViewModel.class);

        storageViewModel.observePictures().observe(getViewLifecycleOwner(), new Observer<List<PictureDTO>>()
        {
            @Override
            public void onChanged(List<PictureDTO> pictures)
            {
                if(pictures == null)
                {
                    MessageService.service.getAllPictures(
                            getArguments().getInt("user"),
                            getArguments().getInt("other")).enqueue(new Callback<List<PictureDTO>>()
                    {
                        @Override
                        public void onResponse(Call<List<PictureDTO>> call, Response<List<PictureDTO>> response)
                        {
                            storageViewModel.setPictures(response.body());
                        }

                        @Override
                        public void onFailure(Call<List<PictureDTO>> call, Throwable t)
                        {
                            Toast.makeText(context, "Failed to load your media!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    ((MainPicturePagerAdapter)mainPager.getAdapter()).replaceList(pictures);
                    ((MainPicturePagerAdapter)miniPager.getAdapter()).replaceList(pictures);
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
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