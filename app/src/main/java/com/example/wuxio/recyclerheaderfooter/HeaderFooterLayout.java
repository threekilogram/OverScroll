package com.example.wuxio.recyclerheaderfooter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author wuxio 2018-04-07:19:03
 */
public class HeaderFooterLayout extends OverScrollContainer {

    private static final String TAG = "HeaderFooterLayout";

    protected View mHeader;
    protected View mFooter;

    private OnOverScrollListener mOverScrollListener;

    private boolean isStopSpringBack = false;


    public HeaderFooterLayout(Context context) {

        this(context, null, 0);
    }


    public HeaderFooterLayout(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }


    public HeaderFooterLayout(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void init() {

        super.init();
    }


    public void setHeader(View header) {

        if (mHeader != null) {
            removeView(mHeader);
        }
        mHeader = header;
        addView(header);
    }


    public void setFooter(View footer) {

        if (mFooter != null) {
            removeView(mFooter);
        }
        mFooter = footer;
        addView(footer);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mHeader != null) {
            measureChildWithMargins(
                    mHeader,
                    widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                    heightMeasureSpec, getPaddingTop() + getPaddingBottom()
            );
        }

        if (mFooter != null) {
            measureChildWithMargins(
                    mFooter,
                    widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                    heightMeasureSpec, getPaddingTop() + getPaddingBottom()
            );
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mHeader != null) {

            LayoutParams params = (LayoutParams) mHeader.getLayoutParams();
            int left = getPaddingLeft() + params.leftMargin;
            int top = getPaddingTop() - params.bottomMargin - mHeader.getMeasuredHeight();
            int right = left + mHeader.getMeasuredWidth();
            int bottom = top + mHeader.getMeasuredHeight();

            mHeader.layout(left, top, right, bottom);
        }

        if (mFooter != null) {
            LayoutParams params = (LayoutParams) mFooter.getLayoutParams();
            int left = getPaddingLeft() + params.leftMargin;
            int top = getHeight() + params.topMargin;
            int right = left + mFooter.getMeasuredWidth();
            int bottom = top + mFooter.getMeasuredHeight();

            mFooter.layout(left, top, right, bottom);
        }
    }


    @Override
    public void scrollTo(int x, int y) {

        super.scrollTo(x, y);

        if (mOverScrollListener == null) {
            return;
        }

        if (state == OVER_TOP) {
            mOverScrollListener.onScrollOverTop(mHeader, getScrollY());
        }

        if (state == OVER_BOTTOM) {
            mOverScrollListener.onScrollOverBottom(mFooter, getScrollY());
        }
    }


    public void stopSpringBack() {

        isStopSpringBack = true;
    }


    @Override
    protected void springBackFromTop() {

        if (isStopSpringBack) {
            return;
        }

        super.springBackFromTop();
    }


    @Override
    protected void springBackFromBottom() {

        if (isStopSpringBack) {
            return;
        }

        super.springBackFromBottom();
    }


    public void springBack() {

        isStopSpringBack = false;

        if (getScrollY() < 0) {
            springBackFromTop();
        }

        if (getScrollY() > 0) {
            springBackFromBottom();
        }
    }


    public void scrollBack(int dy) {

        int scrollY = getScrollY();

        if (scrollY < 0) {
            if (scrollY + dy > 0) {
                dy = -scrollY;
                state = NORMAL;
            }
        }

        if (scrollY > 0) {
            if (scrollY + dy < 0) {
                dy = -scrollY;
                state = NORMAL;
            }
        }

        mScroller.startScroll(0, scrollY, 0, dy);
        invalidate();
    }


    public void reLayout() {

        int scrollY = getScrollY();
        scrollTo(0, 0);
        mRecyclerView.scrollBy(0, scrollY);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean b = super.dispatchTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_UP) {

            if (mOverScrollListener == null) {
                return b;
            }

            if (state == OVER_TOP) {
                mOverScrollListener.onOverTopTouchUp(mHeader, getScrollY());
                return b;
            }

            if (state == OVER_BOTTOM) {
                mOverScrollListener.onOverBottomTouchUp(mFooter, getScrollY());
            }
        }

        return b;
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

    //============================OverScrollListener============================


    public void setOverScrollListener(OnOverScrollListener overScrollListener) {

        mOverScrollListener = overScrollListener;
    }


    public interface OnOverScrollListener {


        void onScrollOverTop(View header, int scrollY);

        void onOverTopTouchUp(View header, int scrollY);

        void onScrollOverBottom(View footer, int scrollY);

        void onOverBottomTouchUp(View footer, int scrollY);

    }
}
