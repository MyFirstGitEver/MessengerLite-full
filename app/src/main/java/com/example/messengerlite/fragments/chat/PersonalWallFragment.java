package com.example.messengerlite.fragments.chat;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerlite.AuthenticationState;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.fragments.image.ImageViewFragment;
import com.example.messengerlite.interfaces.AddLinkListener;
import com.example.messengerlite.R;
import com.example.messengerlite.adapters.InfoListAdapter;
import com.example.messengerlite.dialogs.AddLinkSheet;
import com.example.messengerlite.entities.WebLinkEntity;
import com.example.messengerlite.services.MediaService;
import com.example.messengerlite.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalWallFragment extends DialogFragment
{
    private MainViewModel mainViewModel;

    private ImageView userImg;
    private ImageButton editBtn;
    private TextView displayNameTxt;
    private RecyclerView infoList;

    private int lastPos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.personal_wall_fragment, container, false);

        userImg = view.findViewById(R.id.userImg);
        displayNameTxt = view.findViewById(R.id.displayNameTxt);
        infoList = view.findViewById(R.id.infoList);
        editBtn = view.findViewById(R.id.editBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null)
            lastPos = savedInstanceState.getInt("last pos");

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        infoList.setLayoutManager(new LinearLayoutManager(getContext()));

        userImg.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putBoolean("replaceable", true);

            ImageViewFragment imageViewFragment = new ImageViewFragment();
            imageViewFragment.setArguments(bundle);
            imageViewFragment.show(requireActivity().getSupportFragmentManager(), "image view");
        });

        editBtn.setOnClickListener(v ->
        {
            new EditPersonalDataFragment()
                    .show(requireActivity().getSupportFragmentManager(), "edit data");
        });

        mainViewModel.observeMyUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>()
        {
            @Override
            public void onChanged(UserEntity user)
            {
                Glide.with(getContext()).load(user.getAvatarPath()).into(userImg);
                displayNameTxt.setText(user.getUserDisplayName());
            }
        });

        mainViewModel.observeInfoList().observe(getViewLifecycleOwner(), new Observer<List<Object>>()
        {
            @Override
            public void onChanged(List<Object> infos)
            {
                if(infos == null)
                {
                    MediaService.service.getLinksUsingOwnerId(mainViewModel.getMyUser().getId()).enqueue(
                            new Callback<List<WebLinkEntity>>()
                            {
                                @Override
                                public void onResponse(Call<List<WebLinkEntity>> call, Response<List<WebLinkEntity>> response)
                                {
                                    mainViewModel.initPersonalData(mainViewModel.getMyUser(), response.body());
                                }

                                @Override
                                public void onFailure(Call<List<WebLinkEntity>> call, Throwable t)
                                {
                                    Toast.makeText(getContext(), "Can't load personal links!", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    return;
                }

                infoList.setAdapter(new InfoListAdapter(mainViewModel.getPersonalData(), new AddLinkListener()
                {
                    @Override
                    public void openLinkSheet()
                    {
                        new AddLinkSheet()
                                .show(getChildFragmentManager(), "add link");
                    }

                    @Override
                    public void deleteLink()
                    {
                    }
                }, false, lastPos));
            }
        });

        getChildFragmentManager().setFragmentResultListener("addLink", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                WebLinkEntity link = result.getParcelable("link");

                ((InfoListAdapter)infoList.getAdapter()).insertNewLink(link);
                MediaService.service.editLink(MediaService.SAVE, link).enqueue(new Callback<Integer>()
                {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response)
                    {
                        ((InfoListAdapter)infoList.getAdapter()).getLastLink().setId(response.body());
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t)
                    {
                        Toast.makeText(getContext(),
                                "Can't save your link :(. Check your internet connection",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
                getActivity().finish();
            }
        };

        dialog.getWindow().setWindowAnimations(R.style.fadeStyle);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt("last pos", ((InfoListAdapter)infoList.getAdapter()).getLastClickPos());
    }
}