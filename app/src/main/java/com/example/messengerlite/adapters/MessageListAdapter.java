package com.example.messengerlite.adapters;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerlite.R;
import com.example.messengerlite.commontools.Tools;
import com.example.messengerlite.dtos.MessageDTO;
import com.example.messengerlite.dtos.SystemPictureDTO;
import com.example.messengerlite.dtos.PictureMessageDTO;
import com.example.messengerlite.entities.MessageEntity;

import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<MessageDTO> messages;

    public MessageListAdapter(List<MessageDTO> messages)
    {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == MessageDTO.TEXT)
            return new TextViewHolder(inflater.inflate(R.layout.a_text, parent, false));
        else if(viewType == MessageDTO.LIKE)
            return new LikeViewHolder(inflater.inflate(R.layout.a_like, parent, false));
        else if(viewType == MessageDTO.PICTURE)
            return new PictureViewHolder(inflater.inflate(R.layout.a_picture, parent, false));
        else if(viewType == MessageDTO.STAMP)
            return new StampViewHolder(inflater.inflate(R.layout.a_stamp, parent, false));
        else
            return new NullViewHolder(inflater.inflate(R.layout.loading_more, parent, false));
    }

    public void replaceList(List<MessageDTO> messages)
    {
        this.messages = messages;
        notifyDataSetChanged();
    }

    private static final int TEN_MINUTES = 600000;

    public void insertNewMessage(MessageDTO msg)
    {
        Date now = new Date();

        if(messages.size() == 0)
        {
            messages.add(0, new MessageDTO(MessageDTO.STAMP, Tools.fromToday(now)));
            notifyItemInserted(0);
        }
        else if(now.getTime() -
                messages.get(0).getMessage().getDate().getTime() >= TEN_MINUTES)
        {
            messages.add(0, new MessageDTO(MessageDTO.STAMP, Tools.fromToday(now)));
            notifyItemInserted(0);
        }

        messages.add(0, msg);
        notifyItemInserted(0);
    }

    public void insertNewImage(SystemPictureDTO picture, int from, int to)
    {
        MessageEntity message = new MessageEntity(
                MessageDTO.PICTURE,
                from,
                to,
                -1,
                false, false, false, picture.getPath(), new Date());

        messages.add(new PictureMessageDTO(true, message,
                picture.getDimension().first, picture.getDimension().second));
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position)
    {
        if(messages.get(position) == null)
            return MessageDTO.NULL;

        return messages.get(position).getMessage().getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        MessageDTO message = messages.get(position);

        if(holder instanceof NullViewHolder)
            return;
        else if(holder instanceof StampViewHolder)
        {
            StampViewHolder Holder = (StampViewHolder) holder;
            Holder.stampTxt.setText(message.getMessage().getContent());
            return;
        }

        if(message.getIsMyUser())
            ((MessageViewHolder)holder).container.setGravity(Gravity.END);
        else
            ((MessageViewHolder)holder).container.setGravity(Gravity.START);

        if(holder instanceof TextViewHolder)
        {
            ((TextViewHolder) holder).msgTxt.setText(message.getMessage().getContent());

            Resources resources = holder.itemView.getContext().getResources();

            if(message.getIsMyUser())
                ((TextViewHolder) holder).msgTxt.setBackgroundTintList(ColorStateList
                        .valueOf(resources.getColor(R.color.blue)));
            else
                ((TextViewHolder) holder).msgTxt.setBackgroundTintList(ColorStateList
                        .valueOf(resources.getColor(R.color.greatergray)));
        }
        else if(holder instanceof PictureViewHolder)
        {
            PictureMessageDTO picture = (PictureMessageDTO) message;

            ImageView picImg = ((PictureViewHolder) holder).picImg;

            resizeImage(picture.getWidth(), picture.getHeight(), picImg);
            Glide.with(holder.itemView.getContext()).
                    load(picture.getMessage().getContent()).centerCrop().into(picImg);
        }
    }

    private static final float MAX_WIDTH = 230.0f;
    private static final float MAX_HEIGHT = 360.0f;

    private void resizeImage(int first, int second, ImageView imgView)
    {
        int finalW = first, finalH = second;

        if(finalW > MAX_WIDTH)
        {
            finalH = (int) (MAX_WIDTH / finalW * finalH);
            finalW = (int) MAX_WIDTH;
        }

        if(finalH > MAX_HEIGHT)
        {
            finalW = (int) (MAX_HEIGHT / finalH * finalW);
            finalH = (int) MAX_HEIGHT;
        }

        imgView.getLayoutParams().width = finalW;
        imgView.getLayoutParams().height = finalH;

        imgView.requestLayout();
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    public class TextViewHolder extends MessageViewHolder
    {
        private TextView msgTxt;

        public TextViewHolder(@NonNull View itemView)
        {
            super(itemView);

            msgTxt = itemView.findViewById(R.id.msgTxt);
            container = itemView.findViewById(R.id.container);
        }
    }

    public class LikeViewHolder extends MessageViewHolder
    {
        public LikeViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }

    public class StampViewHolder extends RecyclerView.ViewHolder
    {
        TextView stampTxt;

        public StampViewHolder(@NonNull View itemView)
        {
            super(itemView);
            stampTxt = itemView.findViewById(R.id.stampTxt);
        }
    }

    public class PictureViewHolder extends MessageViewHolder
    {
        ImageView picImg;

        public PictureViewHolder(@NonNull View itemView)
        {
            super(itemView);
            picImg = itemView.findViewById(R.id.picImg);
        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout container;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            container = itemView.findViewById(R.id.container);
        }
    }

    public class NullViewHolder extends RecyclerView.ViewHolder
    {
        public NullViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
