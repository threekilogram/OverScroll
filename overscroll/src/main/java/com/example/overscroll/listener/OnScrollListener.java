package com.example.overscroll.listener;

import android.support.v4.widget.NestedScrollView;

import com.example.overscroll.OverScrollContainer;
import com.example.overscroll.OverScrollNested;

/**
 * 监听recycler 是否已经滚动到顶部/底部,如果 recycler 在fling中到达边界,触发{@link OverScrollNested} fling效果
 *
 * @author wuxio
 */
public class OnScrollListener implements NestedScrollView.OnScrollChangeListener {

    public OverScrollContainer mContainer;


    public OnScrollListener(OverScrollContainer container) {

        mContainer = container;
    }


    @Override
    public void onScrollChange(
            NestedScrollView v,
            int scrollX, int scrollY,
            int oldScrollX, int oldScrollY) {

        mContainer.observeScroll(v);
    }
}
