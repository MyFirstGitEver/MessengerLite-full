package com.example.messengerlite.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messengerlite.R;
import com.example.messengerlite.dtos.PictureDTO;
import com.example.messengerlite.dtos.SystemPictureDTO;
import com.example.messengerlite.interfaces.CurrentPageListener;

import java.util.List;

public class MainPicturePagerAdapter extends RecyclerView.Adapter<MainPicturePagerAdapter.ImageViewHolder>
{
    private List<PictureDTO> pictures;
    private int maxWidth, maxHeight;
    private CurrentPageListener listener;

    public MainPicturePagerAdapter(List<PictureDTO> pictures, int maxWidth, int maxHeight, CurrentPageListener listener)
    {
        this.pictures = pictures;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.a_media_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position)
    {
        PictureDTO picture = pictures.get(position);

        int finalW = picture.getWidth(), finalH = picture.getHeight();

        if(finalW >= maxWidth)
        {
            finalH = (int) ((float)maxWidth / finalW * finalH);
            finalW = maxWidth;
        }

        if(finalH >= maxHeight)
        {
            finalW = (int) ((float)maxHeight / finalH * finalW);
            finalH = maxHeight;
        }

        holder.picImg.getLayoutParams().width = finalW;
        holder.picImg.getLayoutParams().height = finalH;
        holder.picImg.requestLayout();

        Glide.with(holder.itemView.getContext()).load(picture.getPath()).into(holder.picImg);

        if(listener.getCurrentPage() == position)
            holder.picImg.setBackgroundResource(R.drawable.blue_stroke_bg);
        else
            holder.picImg.setBackgroundResource(0);
    }

    @Override
    public int getItemCount()
    {
        return pictures.size();
    }

    public void replaceList(List<PictureDTO> pictures)
    {
        this.pictures = pictures;
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView picImg;

        public ImageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            picImg = itemView.findViewById(R.id.picImg);
        }
    }
}
