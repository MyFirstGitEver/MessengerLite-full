package com.example.messengerlite.interfaces;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class OnEndReachedListener extends RecyclerView.OnScrollListener
{
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);

        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();

        int totalCount = manager.getItemCount();
        int visibleCount = manager.getChildCount();
        int firstVisible = manager.findFirstVisibleItemPosition();

        if(isLastPage() || isLoading())
            return;

        if(totalCount <= visibleCount + firstVisible)
            onEndReached();
    }

    public abstract void onEndReached();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
