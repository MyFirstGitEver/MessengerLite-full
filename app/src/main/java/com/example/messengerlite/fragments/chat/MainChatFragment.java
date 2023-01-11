package com.example.messengerlite.fragments.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerlite.R;
import com.example.messengerlite.adapters.MessageListAdapter;
import com.example.messengerlite.dialogs.PictureSheet;
import com.example.messengerlite.dtos.MessageDTO;
import com.example.messengerlite.dtos.PictureMessageDTO;
import com.example.messengerlite.dtos.SystemPictureDTO;
import com.example.messengerlite.entities.MessageEntity;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.fragments.storage.StorageViewFragment;
import com.example.messengerlite.interfaces.OnTopReachedListener;
import com.example.messengerlite.services.MessageService;
import com.example.messengerlite.viewmodels.MainChatViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainChatFragment extends DialogFragment
{
    private ImageView userImg;
    private RecyclerView messageList;
    private MotionLayout mainChatContainer;
    private ImageButton reduceBtn, moreBtn, likeBtn, picBtn;
    private AppCompatButton storageBtn;
    private EditText msgEditTxt;

    private Context context;
    private MainChatViewModel mainChatViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_chat_fragment, container, false);

        userImg = view.findViewById(R.id.userImg);

        likeBtn = view.findViewById(R.id.likeBtn);
        reduceBtn = view.findViewById(R.id.reduceBtn);
        moreBtn = view.findViewById(R.id.moreBtn);
        picBtn = view.findViewById(R.id.picBtn);
        storageBtn = view.findViewById(R.id.storageBtn);

        messageList = view.findViewById(R.id.messageList);
        mainChatContainer = view.findViewById(R.id.main_chat_container);

        msgEditTxt = view.findViewById(R.id.msgEditTxt);

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog)
    {
        super.onDismiss(dialog);



        if(!mainChatViewModel.checkIfAnyChangesHappen())
            getParentFragmentManager().setFragmentResult("turn off", null);
        else
        {
            MessageDTO msg = mainChatViewModel.getLastMessage();
            UserEntity other = getArguments().getParcelable("other");

            Bundle bundle = new Bundle();
            bundle.putInt("user", other.getId());
            bundle.putParcelable("message", msg);

            getParentFragmentManager().setFragmentResult("turn off", bundle);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mainChatViewModel = new ViewModelProvider(this).get(MainChatViewModel.class);

        UserEntity user = getArguments().getParcelable("other");
        Glide.with(view).load(user.getAvatarPath()).into(userImg);

        reduceBtn.setOnClickListener(v -> mainChatContainer.transitionToEnd());
        moreBtn.setOnClickListener(v -> mainChatContainer.transitionToStart());

        picBtn.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putInt("from", getArguments().getInt("myUser"));
            bundle.putInt("to", ((UserEntity)getArguments().getParcelable("other")).getId());

            PictureSheet sheet = new PictureSheet();
            sheet.setArguments(bundle);
            sheet.show(getParentFragmentManager(), "picture");
        });

        userImg.setOnClickListener(v ->
        {
            PersonalWallFragment fragment = new PersonalWallFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("stranger", true);
            bundle.putParcelable("info", getArguments().getParcelable("other"));

            fragment.setArguments(bundle);
            fragment.show(getChildFragmentManager(), "personal wall");
        });

        msgEditTxt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(charSequence.toString().equals(""))
                    likeBtn.setImageResource(R.drawable.ic_like);
                else
                    likeBtn.setImageResource(R.drawable.ic_send);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        storageBtn.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();

            bundle.putInt("user", getArguments().getInt("myUser"));
            bundle.putInt("other", ((UserEntity)getArguments().getParcelable("other")).getId());

            StorageViewFragment fragment = new StorageViewFragment();
            fragment.setArguments(bundle);
            fragment.show(getChildFragmentManager(), "storage");
        });

        likeBtn.setOnClickListener(v ->
        {
            MessageEntity msg;

            mainChatViewModel.notifyChanges();

            if(!msgEditTxt.getText().toString().equals(""))
            {
                msg = new MessageEntity(
                        MessageDTO.TEXT,
                        getArguments().getInt("myUser"),
                        ((UserEntity)getArguments().getParcelable("other")).getId(),
                        -1,
                        false,false, false, msgEditTxt.getText().toString(), new Date());
                msgEditTxt.setText("");
            }
            else
            {
                msg = new MessageEntity(
                        MessageDTO.LIKE,
                        getArguments().getInt("myUser"),
                        ((UserEntity)getArguments().getParcelable("other")).getId(),
                        -1,
                        false,false, false, "Đã gửi một like", new Date());
            }


            ((MessageListAdapter)messageList.getAdapter()).insertNewMessage(new MessageDTO(true, msg));
            messageList.smoothScrollToPosition(mainChatViewModel.getLastPosition());

            Bundle bundle = new Bundle();
            bundle.putParcelable("message", msg);
            getParentFragmentManager().setFragmentResult("send", bundle);
        });

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);

        messageList.setLayoutManager(manager);
        messageList.setAdapter(new MessageListAdapter(new ArrayList<>()));

        messageList.addOnScrollListener(new OnTopReachedListener()
        {
            @Override
            public void onTopReached()
            {
                mainChatViewModel.setLoading(true);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getNextPage();
                        mainChatViewModel.setLoading(false);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.removeCallbacks(this);
                    }
                }, 1500);
            }

            @Override
            public boolean isLastPage()
            {
                return mainChatViewModel.isLastPage();
            }

            @Override
            public boolean isLoading()
            {
                return mainChatViewModel.isLoading();
            }
        });

        mainChatViewModel.observeMessages().observe(getViewLifecycleOwner(), new Observer<List<MessageDTO>>()
        {
            @Override
            public void onChanged(List<MessageDTO> messages)
            {
                if(messages == null)
                    getNextPage();
                else
                    ((MessageListAdapter)messageList.getAdapter()).replaceList(messages);
            }
        });

        getParentFragmentManager().setFragmentResultListener("new message", getViewLifecycleOwner(),
                new FragmentResultListener()
                {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
                    {
                        if(mainChatViewModel.isRacing())
                            return;

                        MessageDTO dto = result.getParcelable("message");
                        MessageEntity msg = dto.getMessage();

                        if(msg.getFromId() != ((UserEntity)getArguments().getParcelable("other")).getId())
                            return;

                        ((MessageListAdapter)messageList.getAdapter()).insertNewMessage(dto);
                        messageList.smoothScrollToPosition(mainChatViewModel.getLastPosition());
                    }
                });

        getParentFragmentManager().setFragmentResultListener("picture", getViewLifecycleOwner(), new FragmentResultListener()
        {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                mainChatViewModel.notifyChanges();

                SystemPictureDTO picture = result.getParcelable("picture");

                ((MessageListAdapter)messageList.getAdapter()).insertNewImage(
                        picture,
                        getArguments().getInt("myUser"),
                        ((UserEntity)getArguments().getParcelable("other")).getId());

                messageList.smoothScrollToPosition(mainChatViewModel.getLastPosition());
            }
        });
    }

    private void getNextPage()
    {
        MessageService.service.getConversations(
                getArguments().getInt("myUser"),
                ((UserEntity)getArguments().getParcelable("other")).getId(),
                mainChatViewModel.getCurrentPageNumber()).enqueue(new Callback<List<PictureMessageDTO>>()
        {
            @Override
            public void onResponse(Call<List<PictureMessageDTO>> call, Response<List<PictureMessageDTO>> response)
            {
                int lastSize = mainChatViewModel.getLastPosition();

                if(lastSize != -1)
                {
                    mainChatViewModel.removeFooter();
                    messageList.getAdapter().notifyItemRemoved(lastSize);
                }

                if(response.body() == null || response.body().size() == 0)
                    mainChatViewModel.setLastPage(true);
                else
                {
                    int offset = mainChatViewModel.concat(response.body());

                    if(lastSize == -1)
                    {
                        messageList.getAdapter().notifyDataSetChanged();
                        messageList.smoothScrollToPosition(0);
                    }
                    else
                    {
                        for(int i=lastSize;i<=lastSize + response.body().size() + offset;i++) // including null
                            messageList.getAdapter().notifyItemInserted(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PictureMessageDTO>> call, Throwable t)
            {
                Toast.makeText(context, "Failed to load conversations!", Toast.LENGTH_SHORT).show();
            }
        });

        mainChatViewModel.incrementPageNumber();
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        this.context = context;
    }

    private String notifyMessageContent(MessageEntity message)
    {
        switch (message.getType())
        {
            case MessageDTO.TEXT:
                return message.getContent();
            case MessageDTO.LIKE:
                return "Đã gửi 1 like";
            default:
                return "Đã gửi hình ảnh";
        }
    }
}