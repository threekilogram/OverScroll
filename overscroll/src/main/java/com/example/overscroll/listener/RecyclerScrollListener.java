package com.example.overscroll.listener;

/**
 * @author wuxio 2018-04-25:23:14
 */

import android.support.v7.widget.RecyclerView;

import com.example.overscroll.OverScrollContainer;

public class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    OverScrollContainer mContainer;


    public RecyclerScrollListener(OverScrollContainer container) {

        mContainer = container;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        mContainer.observeScroll(recyclerView);
    }
}
