package com.example.messengerlite.adapters;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerlite.interfaces.AddLinkListener;
import com.example.messengerlite.R;
import com.example.messengerlite.dtos.InfoDTO;
import com.example.messengerlite.entities.WebLinkEntity;

import java.util.List;

public class InfoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Object> infos;
    private AddLinkListener listener;

    private boolean editable;
    private int lastClickPos;

    public InfoListAdapter(List<Object> infos, AddLinkListener listener, boolean editable, int lastClickPos)
    {
        this.infos = infos;
        this.listener = listener;
        this.lastClickPos = lastClickPos;
        this.editable = editable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == -2)
            return new AddViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.add_link, parent, false));

        if(viewType == 0)
            return new InfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.an_info, parent, false));

        return new LinkViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.a_link, parent, false));
    }

    @Override
    public int getItemViewType(int position)
    {
        if(infos.get(position) == null)
            return -2;

        if(infos.get(position) instanceof WebLinkEntity)
            return -1;

        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, int position)
    {
        if(Holder instanceof AddViewHolder)
        {
            AddViewHolder holder = (AddViewHolder) Holder;

            holder.addBtn.setOnClickListener(v ->
            {
                lastClickPos = -1;
                listener.openLinkSheet();
            });
            return;
        }

        if(Holder instanceof InfoViewHolder)
        {
            InfoViewHolder holder = (InfoViewHolder) Holder;

            InfoDTO info = (InfoDTO) infos.get(position);

            holder.infoTxt.setText(info.getContent());

            switch (info.getType())
            {
                case InfoDTO.BIRTH_DAY:
                    holder.typeImg.setImageResource(R.drawable.ic_birthday);
                    break;
                case InfoDTO.HOME_TOWN:
                    holder.typeImg.setImageResource(R.drawable.ic_home);
                    break;
                default:
                    holder.typeImg.setImageResource(R.drawable.ic_work);
                    break;
            }

            return;
        }

        WebLinkEntity link = (WebLinkEntity) infos.get(position);

        LinkViewHolder holder = (LinkViewHolder) Holder;

        holder.titleTxt.setText(link.getTitle());

        String html = "<a href = \"" + link.getLink() + "\">" + link.getLink() + "</a>";

        holder.linkTxt.setText(Html.fromHtml(html));
        holder.linkTxt.setMovementMethod(LinkMovementMethod.getInstance());

        if(editable)
        {
            holder.editPane.setVisibility(View.VISIBLE);
            holder.editBtn.setOnClickListener(v ->
            {
                lastClickPos = holder.getBindingAdapterPosition();
                listener.openLinkSheet();
            });
            holder.deleteBtn.setOnClickListener(v ->
            {
                lastClickPos = holder.getBindingAdapterPosition();
                listener.deleteLink();
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return infos.size();
    }

    public void insertNewLink(WebLinkEntity link)
    {
        if(lastClickPos == -1)
        {
            infos.add(infos.size() - 1, link);
            notifyItemInserted(infos.size() - 1);

            return;
        }

        WebLinkEntity oldLink = (WebLinkEntity) infos.get(lastClickPos);
        oldLink.setLink(link.getLink());
        oldLink.setTitle(link.getTitle());

        notifyItemChanged(lastClickPos);
    }

    public void deleteLastLink()
    {
        infos.remove(lastClickPos);
        notifyItemRemoved(lastClickPos);
    }

    public WebLinkEntity getLastLink()
    {
        if(lastClickPos == -1)
            return (WebLinkEntity) infos.get(infos.size() - 2);

        return (WebLinkEntity) infos.get(lastClickPos);
    }

    public WebLinkEntity getPreContext(int ownerId)
    {
        if(lastClickPos == -1)
            return new WebLinkEntity("", "", ownerId);

        return (WebLinkEntity) infos.get(lastClickPos);
    }

    public int getLastClickPos()
    {
        return lastClickPos;
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder
    {
        ImageView typeImg;
        TextView infoTxt;

        public InfoViewHolder(@NonNull View itemView)
        {
            super(itemView);

            typeImg = itemView.findViewById(R.id.infoTypeImg);
            infoTxt = itemView.findViewById(R.id.titleTxt);
        }
    }

    public class LinkViewHolder extends RecyclerView.ViewHolder
    {
        TextView titleTxt, linkTxt;
        LinearLayout editPane;
        ImageButton editBtn, deleteBtn;

        public LinkViewHolder(@NonNull View itemView)
        {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            linkTxt = itemView.findViewById(R.id.linkTxt);
            editPane = itemView.findViewById(R.id.editPane);

            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder
    {
        AppCompatButton addBtn;
        public AddViewHolder(@NonNull View itemView)
        {
            super(itemView);

            addBtn = itemView.findViewById(R.id.addBtn);
        }
    }
}
