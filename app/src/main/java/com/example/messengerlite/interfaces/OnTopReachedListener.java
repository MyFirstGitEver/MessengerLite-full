package com.example.messengerlite.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class OnTopReachedListener extends RecyclerView.OnScrollListener
{
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if(isLastPage() || isLoading())
            return;

        int firstVisible = manager.findFirstVisibleItemPosition();
        int totalCount = manager.getItemCount();
        int visibleCount = manager.getChildCount();

        if(totalCount <= firstVisible + visibleCount && dy < 0)
                onTopReached();
    }

    public abstract void onTopReached();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}