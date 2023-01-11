package com.example.messengerlite.fragments.chat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.example.messengerlite.commontools.StompConnection;
import com.example.messengerlite.adapters.ChatterListAdapter;
import com.example.messengerlite.dtos.ChatBoxDTO;
import com.example.messengerlite.dtos.MessageDTO;
import com.example.messengerlite.dtos.PictureMessageDTO;
import com.example.messengerlite.dtos.PictureMessagePackage;
import com.example.messengerlite.dtos.SystemPictureDTO;
import com.example.messengerlite.entities.MessageEntity;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.fragments.image.ImageViewFragment;
import com.example.messengerlite.interfaces.AddLinkListener;
import com.example.messengerlite.R;
import com.example.messengerlite.adapters.InfoListAdapter;
import com.example.messengerlite.dialogs.AddLinkSheet;
import com.example.messengerlite.entities.WebLinkEntity;
import com.example.messengerlite.interfaces.ChatBoxListener;
import com.example.messengerlite.interfaces.MessageReceivedListener;
import com.example.messengerlite.services.MediaService;
import com.example.messengerlite.services.MessageService;
import com.example.messengerlite.viewmodels.MainViewModel;
import com.example.messengerlite.viewmodels.PersonalWallViewModel;
import com.example.messengerlite.viewmodels.StrangerDataViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalWallFragment extends DialogFragment
{
    private MainViewModel mainViewModel;
    private StrangerDataViewModel strangerDataViewModel;
    private PersonalWallViewModel personalWallViewModel;

    private Context context;

    private ImageView userImg;
    private ImageButton editBtn, newMessageBtn, quickCallBtn;
    private TextView displayNameTxt;
    private RecyclerView infoList;

    private RecyclerView chatterList;
    private DrawerLayout drawer;

    private StompConnection messageListener;

    private boolean isOpeningChat;
    private int lastPos;

    private final MessageReceivedListener listener = new MessageReceivedListener()
    {
        @Override
        public void onNewMessage(String json)
        {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Gson gson = new Gson();
                    MessageDTO dto = gson.fromJson(json, PictureMessageDTO.class);

                    if(dto.getMessage().getType() != MessageDTO.PICTURE)
                        dto = new MessageDTO(false, dto.getMessage());

                    MessageEntity msg = dto.getMessage();

                    if(isOpeningChat)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("message", dto);
                        getChildFragmentManager().setFragmentResult("new message", bundle);
                    }

                    UserEntity user =
                            ((ChatterListAdapter)chatterList.getAdapter()).bumpUserWithId(
                                    msg.getFromId(), dto, false);

                    //notifyNewMessage(user, msg);
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.personal_wall_fragment, container, false);

        userImg = view.findViewById(R.id.userImg);
        displayNameTxt = view.findViewById(R.id.displayNameTxt);
        infoList = view.findViewById(R.id.infoList);

        editBtn = view.findViewById(R.id.editBtn);
        newMessageBtn = view.findViewById(R.id.newMessageBtn);
        quickCallBtn = view.findViewById(R.id.quickCallBtn);

        chatterList = view.findViewById(R.id.chatterList);
        drawer = view.findViewById(R.id.drawer);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        infoList.setLayoutManager(new LinearLayoutManager(getContext()));

        if(getArguments().getBoolean("stranger"))
        {
            loadStrangerData();
            return;
        }

        if(savedInstanceState != null)
        {
            lastPos = savedInstanceState.getInt("last pos");
            isOpeningChat = savedInstanceState.getBoolean("open chat");
        }

        editBtn.setOnClickListener(v ->
                new EditPersonalDataFragment()
                        .show(requireActivity().getSupportFragmentManager(), "edit data"));

        newMessageBtn.setOnClickListener(v ->
        {
            drawer.openDrawer(GravityCompat.END);
        });

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        personalWallViewModel = new ViewModelProvider(this).get(PersonalWallViewModel.class);

        chatterList.setLayoutManager(new LinearLayoutManager(getContext()));

        chatterList.setAdapter(new ChatterListAdapter(mainViewModel.getMyUser().getId(),
                new ArrayList<>(), new ChatBoxListener()
        {
            @Override
            public void onChatBoxClick(UserEntity user)
            {
                isOpeningChat = true;

                Bundle bundle = new Bundle();
                bundle.putInt("myUser", mainViewModel.getMyUser().getId());
                bundle.putParcelable("other", user);

                MainChatFragment fragment = new MainChatFragment();
                fragment.setArguments(bundle);
                fragment.show(getChildFragmentManager(), "main chat");
            }
        }));

        userImg.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putBoolean("replaceable", true);

            ImageViewFragment imageViewFragment = new ImageViewFragment();
            imageViewFragment.setArguments(bundle);
            imageViewFragment.show(requireActivity().getSupportFragmentManager(), "image view");
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

                int lastPos = 0;

                if(savedInstanceState != null)
                    lastPos = savedInstanceState.getInt("last pos");

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

        personalWallViewModel.observeChatBoxes().observe(getViewLifecycleOwner(), new Observer<List<ChatBoxDTO>>()
        {
            @Override
            public void onChanged(List<ChatBoxDTO> chatBoxes)
            {
                if(chatBoxes == null)
                {
                    MessageService.service.getLastMessages(mainViewModel.getMyUser().getId()).enqueue(
                            new Callback<List<ChatBoxDTO>>()
                            {
                                @Override
                                public void onResponse(Call<List<ChatBoxDTO>> call, Response<List<ChatBoxDTO>> response)
                                {
                                    personalWallViewModel.setChatBoxes(response.body());
                                }

                                @Override
                                public void onFailure(Call<List<ChatBoxDTO>> call, Throwable t)
                                {
                                    Toast.makeText(context, "Check your internet connection!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                    ((ChatterListAdapter)chatterList.getAdapter()).replaceList(chatBoxes);
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

        messageListener = new StompConnection();

        messageListener.listenText(mainViewModel.getMyUser().getId(), listener);
        messageListener.listenPicture(mainViewModel.getMyUser().getId(), listener);

        getChildFragmentManager().setFragmentResultListener("send", getViewLifecycleOwner(), new FragmentResultListener()
        {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                messageListener.sendMessage(result.getParcelable("message"));
            }
        });

        getChildFragmentManager().setFragmentResultListener("turn off", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                if(result == null)
                    return;

                int userId = result.getInt("user");
                MessageDTO dto = result.getParcelable("message");

                ((ChatterListAdapter)chatterList.getAdapter()).bumpUserWithId(userId, dto, true);

                isOpeningChat = false;
            }
        });

        getChildFragmentManager().setFragmentResultListener("send picked", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                ArrayList<SystemPictureDTO> pics = result.getParcelableArrayList("picked");
                int fromId = result.getInt("from");
                int toId = result.getInt("to");

                for(SystemPictureDTO pic : pics)
                {
                    MediaManager.get().upload(pic.getPath()).unsigned("j4ut3xji").callback(
                                    new PictureMessagePackage(fromId, toId, pic, messageListener)).dispatch();

                    if(isOpeningChat)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("picture", pic);
                        getChildFragmentManager().setFragmentResult("picture", bundle);
                    }
                }
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
                if(!getArguments().getBoolean("stranger"))
                    getActivity().finish();
                else
                    super.onBackPressed();
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
        outState.putBoolean("open chat", isOpeningChat);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
    }

    private void notifyNewMessage(UserEntity user, MessageEntity msg)
    {
        Glide.with(context).load(user.getAvatarPath()).into(newMessageBtn);

        View v = LayoutInflater.from(context).inflate(R.layout.a_noti, drawer, false);

        TextView notiTxt = v.findViewById(R.id.notiTxt);
        notiTxt.setText(user.getUserDisplayName() + "\n" + msg.getContent());

        PopupWindow window = new PopupWindow(
                v,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        window.showAsDropDown(newMessageBtn);
    }

    private void loadStrangerData()
    {
        editBtn.setVisibility(View.GONE);
        newMessageBtn.setVisibility(View.GONE);
        quickCallBtn.setVisibility(View.GONE);

        UserEntity user = getArguments().getParcelable("info");

        Glide.with(getContext()).load(user.getAvatarPath()).into(userImg);
        displayNameTxt.setText(user.getUserDisplayName());

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        strangerDataViewModel = new ViewModelProvider(this).get(StrangerDataViewModel.class);

        strangerDataViewModel.observeInformation().observe(getViewLifecycleOwner(), new Observer<List<Object>>()
        {
            @Override
            public void onChanged(List<Object> data)
            {
                if(data == null)
                {
                    UserEntity user = getArguments().getParcelable("info");

                    MediaService.service.getLinksUsingOwnerId(user.getId()).enqueue(
                            new Callback<List<WebLinkEntity>>()
                            {
                                @Override
                                public void onResponse(Call<List<WebLinkEntity>> call, Response<List<WebLinkEntity>> response)
                                {
                                    UserEntity user = getArguments().getParcelable("info");
                                    strangerDataViewModel.initInformation(response.body(), user);
                                }

                                @Override
                                public void onFailure(Call<List<WebLinkEntity>> call, Throwable t)
                                {
                                    Toast.makeText(getContext(), "Can't load personal links!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                    infoList.setAdapter(new InfoListAdapter(data, null, false, 0));
            }
        });
    }
}