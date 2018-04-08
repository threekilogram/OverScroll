package com.example.wuxio.recyclerheaderfooter;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

/**
 * @author wuxio 2018-04-08:6:28
 */
public class OverScrollContainer extends ViewGroup implements NestedScrollingParent {

    private static final String TAG = "OverScrollContainer";

    protected RecyclerView mRecyclerView;
    protected OverScroller mScroller;

    protected int mScrollExtent;
    protected int mScrollOffset;
    protected int mScrollRange;

    protected int mOverScrollDistance      = 500;
    protected int mFlingOverScrollDistance = 500;

    protected static final int NORMAL      = 0;
    protected static final int OVER_TOP    = 1;
    protected static final int OVER_BOTTOM = 2;
    protected static final int FLING       = 3;
    protected static final int SPRING_BACK = 4;
    protected              int state       = NORMAL;


    public OverScrollContainer(Context context) {

        this(context, null, 0);
    }


    public OverScrollContainer(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }


    public OverScrollContainer(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }


    protected void init() {

        mScroller = new OverScroller(getContext());
    }


    /**
     * @param overScrollDistance touch overScroll distance
     */
    public void setOverScrollDistance(int overScrollDistance) {

        mOverScrollDistance = overScrollDistance;
    }


    /**
     * @param flingOverScrollDistance fling touch overScroll distance
     */
    public void setFlingOverScrollDistance(int flingOverScrollDistance) {

        mFlingOverScrollDistance = flingOverScrollDistance;
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();
        mRecyclerView = (RecyclerView) getChildAt(0);
        mRecyclerView.addOnScrollListener(new ContainerOnScrollListener());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureChildWithMargins(mRecyclerView,
                widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                heightMeasureSpec, getPaddingTop() + getPaddingBottom()
        );

        int measuredWidth = mRecyclerView.getMeasuredWidth();
        int measuredHeight = mRecyclerView.getMeasuredHeight();

        setMeasuredDimension(
                measuredWidth + getPaddingLeft() + getPaddingRight(),
                measuredHeight + getPaddingTop() + getPaddingBottom()
        );
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        LayoutParams params = ((LayoutParams) mRecyclerView.getLayoutParams());

        int left = getPaddingLeft() + params.leftMargin;
        int top = getPaddingTop() + params.topMargin;
        int right = left + mRecyclerView.getMeasuredWidth();
        int bottom = top + mRecyclerView.getMeasuredHeight();

        mRecyclerView.layout(
                left,
                top,
                right,
                bottom
        );
    }


    /**
     * abort scroller
     */
    protected void finishScrollerIfNotFinish() {

        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {

        return child == mRecyclerView;
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        super.onNestedPreScroll(target, dx, dy, consumed);

        finishScrollerIfNotFinish();

        int scrollY = getScrollY();

        /* scroll to top , go on scroll min to -mOverScrollDistance */

        if (showOverTop(scrollY, dy, consumed)) {
            return;
        }

        /* top still have shown, scroll to hide  */

        if (hideOverTop(scrollY, dy, consumed)) {
            return;
        }

        /* scroll to bottom , go on scroll, max to mOverScrollDistance*/

        if (showOverBottom(scrollY, dy, consumed)) {
            return;
        }

        /* bottom still have shown, scroll to hide  */

        hideOverBottom(scrollY, dy, consumed);

    }


    protected boolean showOverTop(int scrollY, int dy, int[] consumed) {

        if (mScrollOffset == 0 && dy < 0) {
            state = OVER_TOP;
            dy = calculateScrollDownDy(scrollY, dy, -mOverScrollDistance);
            scrollBy(0, dy);
            consumed[1] = dy;
            return true;
        }

        return false;
    }


    protected boolean hideOverTop(int scrollY, int dy, int[] consumed) {

        if (scrollY < 0 && dy > 0) {

            if (scrollY + dy > 0) {
                dy = -scrollY;
                state = NORMAL;
            }
            scrollBy(0, dy);
            consumed[1] = dy;

            return true;
        }

        return false;
    }


    protected boolean showOverBottom(int scrollY, int dy, int[] consumed) {

        if (mScrollOffset + mScrollExtent == mScrollRange && dy > 0) {
            state = OVER_BOTTOM;
            dy = calculateScrollUpDy(scrollY, dy, mOverScrollDistance);
            scrollBy(0, dy);
            consumed[1] = dy;
            return true;
        }
        return false;
    }


    protected boolean hideOverBottom(int scrollY, int dy, int[] consumed) {

        if (scrollY > 0 && dy < 0) {

            if (scrollY + dy < 0) {
                dy = -scrollY;
                state = NORMAL;
            }
            scrollBy(0, dy);
            consumed[1] = dy;
            return true;
        }

        return false;
    }


    /**
     * recycler滑动到顶部时,继续向下滑动,阻尼系数计算
     *
     * @param scrollY 当前的scrollY
     * @param dy      新收到的的滑动距离
     * @param minY    可滑动到的最小y点
     * @return 阻尼后滑动距离
     */
    protected int calculateScrollDownDy(int scrollY, int dy, int minY) {

        int toY = scrollY + dy;

        if (toY < minY) {
            dy = minY - scrollY;
            return dy;
        }

        if (toY < 0) {

            final float base = 1f;

            float nearByPercent = toY * 1.f / minY;
            float newDY = dy * (1 - nearByPercent);

            if (-newDY < base) {
                newDY = -1;
            }

            return (int) newDY;
        }

        return dy;
    }


    /**
     * recycler滑动到底部时,继续向上滑动,阻尼系数计算
     *
     * @param scrollY 当前的scrollY
     * @param dy      新收到的的滑动距离
     * @return 阻尼后滑动距离
     */
    protected int calculateScrollUpDy(int scrollY, int dy, int maxY) {

        int toY = scrollY + dy;

        if (toY > maxY) {
            dy = maxY - scrollY;
            return dy;
        }

        if (toY > 0) {

            final float base = 1f;

            float nearByPercent = toY * 1.f / maxY;
            float newDY = dy * (1 - nearByPercent);

            if (newDY < base) {
                newDY = 1;
            }

            return (int) newDY;
        }

        return dy;
    }


    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {

        boolean fling = super.onNestedFling(target, velocityX, velocityY, consumed);

        finishScrollerIfNotFinish();

        /* when is showing top or bottom , don't  fling  */

        if (state == OVER_TOP) {
            return super.onNestedFling(target, velocityX, velocityY, consumed);
        }

        if (state == OVER_BOTTOM) {
            return super.onNestedFling(target, velocityX, velocityY, consumed);
        }

        /* record fling, when recycler is fling,but not invalidate() */

        mScroller.fling(
                0, getScrollY(),
                0, (int) velocityY,
                0, 0,
                0, 0,
                0, mOverScrollDistance
        );

        /* only when recycler can scroll to top or bottom , begin invalidate(), fling start */
        /* no invalidate() there , invalidate() when recycler fling to edge */

        return fling;
    }


    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            int y = mScroller.getCurrY();
            scrollTo(0, y);
            invalidate();
            return;
        }

        if (state == SPRING_BACK) {
            state = NORMAL;
        }

        if (state == FLING) {
            state = NORMAL;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean event = super.dispatchTouchEvent(ev);

        int action = ev.getAction();

        if (action == MotionEvent.ACTION_UP) {

            if (state == OVER_TOP) {
                springBackFromTop();
            }

            if (state == OVER_BOTTOM) {
                springBackFromBottom();
            }
        }

        return event;
    }


    private void springBackFromTop() {

        mScroller.springBack(0, getScrollY(), 0, 0, 0, 0);
        invalidate();
        state = SPRING_BACK;
    }


    private void springBackFromBottom() {

        mScroller.springBack(0, getScrollY(), 0, 0, 0, 0);
        invalidate();
        state = SPRING_BACK;
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


    public static class LayoutParams extends MarginLayoutParams {

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

    //============================ 滚动监听 ============================

    protected class ContainerOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            mScrollExtent = recyclerView.computeVerticalScrollExtent();
            mScrollOffset = mRecyclerView.computeVerticalScrollOffset();
            mScrollRange = recyclerView.computeVerticalScrollRange();

            /* begin fling */

            /* only recycler fling to edge,  container begin fling*/

            if (mScrollOffset == 0) {
                if (!mScroller.isFinished()) {
                    invalidate();
                    state = FLING;
                    return;
                }
            }

            if (mScrollOffset + mScrollExtent == mScrollRange) {
                if (!mScroller.isFinished()) {
                    invalidate();
                    state = FLING;
                }
            }
        }
    }
}
