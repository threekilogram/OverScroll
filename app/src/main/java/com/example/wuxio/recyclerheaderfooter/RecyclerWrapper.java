package com.example.wuxio.recyclerheaderfooter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author wuxio 2018-04-07:19:03
 */
public class RecyclerWrapper extends OverScrollContainer {

    protected View mHeader;

    protected View mFooter;


    public RecyclerWrapper(Context context) {

        this(context, null, 0);
    }


    public RecyclerWrapper(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }


    public RecyclerWrapper(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    //============================layout params============================


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {

        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {

        return new LayoutParams(p);
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new LayoutParams(getContext(), attrs);
    }


    public static class LayoutParams extends OverScrollContainer.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {

            super(c, attrs);
        }


        public LayoutParams(int width, int height) {

            super(width, height);
        }


        public LayoutParams(MarginLayoutParams source) {

            super(source);
        }


        public LayoutParams(ViewGroup.LayoutParams source) {

            super(source);
        }
    }
}
