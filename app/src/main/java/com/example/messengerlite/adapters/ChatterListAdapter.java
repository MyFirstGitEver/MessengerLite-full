package com.example.messengerlite.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerlite.R;
import com.example.messengerlite.dtos.ChatBoxDTO;
import com.example.messengerlite.dtos.MessageDTO;
import com.example.messengerlite.entities.MessageEntity;
import com.example.messengerlite.entities.UserEntity;
import com.example.messengerlite.interfaces.ChatBoxListener;

import java.util.List;

public class ChatterListAdapter extends RecyclerView.Adapter<ChatterListAdapter.ChatterViewHolder>
{
    private List<ChatBoxDTO> chatBoxes;
    private ChatBoxListener listener;

    private int myUser;

    public ChatterListAdapter(int myUser, List<ChatBoxDTO> chatBoxes, ChatBoxListener listener)
    {
        this.chatBoxes = chatBoxes;
        this.listener = listener;

        this.myUser = myUser;
    }

    @NonNull
    @Override
    public ChatterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ChatterViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.a_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatterViewHolder holder, int position)
    {
        UserEntity user = chatBoxes.get(position).getUser();

        Glide.with(holder.itemView).load(user.getAvatarPath()).into(holder.userImg);

        MessageEntity msg = chatBoxes.get(position).getLastMessage();

        if(msg.getIsRead() || myUser == msg.getFromId())
            holder.lastMsgTxt.setText(chatBoxes.get(position).getLastMessage().getContent());
        else
            holder.lastMsgTxt.setText(
                    Html.fromHtml("<b>" + chatBoxes.get(position).getLastMessage().getContent() +"</b>"));

        holder.userDisplayNameTxt.setText(user.getUserDisplayName());

        holder.container.setOnClickListener(v ->
        {
            int pos = holder.getBindingAdapterPosition();

            listener.onChatBoxClick(
                    chatBoxes.get(pos).getUser());

            msg.setIsRead(true);
            notifyItemChanged(pos);
        });
    }

    @Override
    public int getItemCount()
    {
        return chatBoxes.size();
    }

    public void replaceList(List<ChatBoxDTO> chatBoxes)
    {
        this.chatBoxes = chatBoxes;
        notifyDataSetChanged();
    }

    public UserEntity bumpUserWithId(int id, MessageDTO msg, boolean isRead)
    {
        UserEntity user = null;

        int index = 0;
        for(ChatBoxDTO box : chatBoxes)
        {
            if(box.getUser().getId() == id)
            {
                String prefix;

                if(msg.getIsMyUser())
                    prefix = "You: ";
                else
                    prefix = box.getUser().getUserDisplayName() + ": ";

                ChatBoxDTO foundBox = chatBoxes.get(index);

                if(msg.getMessage().getType() == MessageDTO.PICTURE)
                    foundBox.getLastMessage().setContent(prefix + "Đã gửi một hình ảnh");
                else
                    foundBox.getLastMessage().setContent(prefix + msg.getMessage().getContent());

                foundBox.getLastMessage().setIsRead(isRead);
                notifyItemChanged(index);

                chatBoxes.remove(index);
                chatBoxes.add(0, foundBox);
                notifyItemMoved(index, 0);

                user = foundBox.getUser();
                break;
            }

            index++;
        }

        return user;
    }

    public class ChatterViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView userImg;
        private TextView userDisplayNameTxt, lastMsgTxt;
        private ConstraintLayout container;

        public ChatterViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userImg = itemView.findViewById(R.id.userImg);
            userDisplayNameTxt = itemView.findViewById(R.id.userDisplayNameTxt);
            lastMsgTxt = itemView.findViewById(R.id.lastMsgTxt);
            container = itemView.findViewById(R.id.container);
        }
    }
}
