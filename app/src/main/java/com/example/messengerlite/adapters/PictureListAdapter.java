package com.example.messengerlite.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerlite.R;
import com.example.messengerlite.dtos.SystemPictureDTO;
import com.example.messengerlite.interfaces.SimpleCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<SystemPictureDTO> pictures;
    private SimpleCallBack callback;
    private int pickCount = 0;

    public PictureListAdapter(List<SystemPictureDTO> pictures, SimpleCallBack callback, int pickCount)
    {
        this.pictures = pictures;
        this.callback = callback;
        this.pickCount = pickCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == -1)
            return new NullViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.loading_more, parent, false));

        return new PictureViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.a_system_picture, parent, false));
    }

    @Override
    public int getItemViewType(int position)
    {
        if(pictures.get(position) == null)
            return -1;

        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position)
    {
        if(Holder instanceof NullViewHolder)
            return;

        PictureViewHolder holder = (PictureViewHolder) Holder;

        File file =  new File(pictures.get(position).getPath());

        Glide.with(holder.itemView.getContext()).load(Uri.fromFile(file)).into(holder.picImg);

        if(pictures.get(position).isPicked())
        {
            holder.picImg.setBackgroundResource(R.drawable.blue_stroke_bg);
            holder.tickImg.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.picImg.setBackgroundResource(0);
            holder.tickImg.setVisibility(View.INVISIBLE);
        }

        holder.container.setOnClickListener(v ->
        {
            pictures.get(position).setPicked(!pictures.get(position).isPicked());
            notifyItemChanged(position);

            if(pictures.get(position).isPicked())
                pickCount++;
            else
                pickCount--;

            callback.run();
        });
    }

    @Override
    public int getItemCount()
    {
        return pictures.size();
    }

    public int getPickCount()
    {
        return pickCount;
    }

    public ArrayList<SystemPictureDTO> getPickedPictures()
    {
        ArrayList<SystemPictureDTO> picked = new ArrayList<>();
        for(SystemPictureDTO pic : pictures)
        {
            if(pic == null)
                break;

            if(pic.isPicked())
                picked.add(pic);
        }

        return picked;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout container;
        ImageView picImg, tickImg;

        public PictureViewHolder(@NonNull View itemView)
        {
            super(itemView);

            picImg = itemView.findViewById(R.id.picImg);
            tickImg = itemView.findViewById(R.id.tickImg);
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
