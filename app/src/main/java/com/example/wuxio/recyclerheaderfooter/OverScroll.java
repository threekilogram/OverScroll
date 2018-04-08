package com.example.wuxio.recyclerheaderfooter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;

/**
 * Created by LiuJin on 2018-04-02:14:46
 *
 * @author wuxio
 */
public class OverScroll extends FrameLayout {

    private static final String TAG = "OverScroll";
    private OverScroller mScroller;
    private int          mChildHeight;
    private int          mSelfHeight;

    private VelocityTracker mVelocityTracker;
    private int             mFlingVelocity;


    public OverScroll(@NonNull Context context) {

        this(context, null, 0);
    }


    public OverScroll(@NonNull Context context, @Nullable AttributeSet attrs) {

        this(context, attrs, 0);
    }


    public OverScroll(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        mScroller = new OverScroller(context);
        setOverScrollMode(OVER_SCROLL_ALWAYS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(false);
        }
        setClipToPadding(false);

        mFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int size = MeasureSpec.getSize(heightMeasureSpec);

        View child = getChildAt(0);
        if (child != null) {
            int heightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec
                    .UNSPECIFIED);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightSpec, 0);
        }

        mChildHeight = child.getMeasuredHeight();

        if (mChildHeight < size) {
            mSelfHeight = mChildHeight;
        } else {
            mSelfHeight = size;
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mSelfHeight);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        View child = getChildAt(0);
        if (child != null) {
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }


    float mLastY;
    float disY;
    //over 距离
    final int over = 300;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        int masked = event.getActionMasked();

        switch (masked) {

            case MotionEvent.ACTION_DOWN:

                //如果动画正在运行,终止
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mLastY = event.getY();

                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                break;

            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                disY = y - mLastY;
                mLastY = y;

                int dis = changeDisY(disY);

                overScrollBy(
                        //x,y滚动距离
                        0, -dis,
                        //当前scrollX/Y
                        getScrollX(), getScrollY(),
                        // range,决定边界(如:100*100,实际边界250*250,range:150*150)
                        0, mChildHeight - mSelfHeight,
                        // 到边界之后还可以over多少
                        0, over,
                        //是否是在touch中调用
                        true
                );

                mVelocityTracker.addMovement(event);

                break;

            case MotionEvent.ACTION_UP:

                /* 先判断是否越界了 */

                int scrollY = getScrollY();
                if (scrollY <= over) {

                    //抬起后的上边越界回弹,会判断前面的两个参数是不是在后面四个参数组成的矩形里
                    mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, 0);
                    invalidate();
                    break;
                } else if (scrollY >= (mChildHeight - mSelfHeight)) {

                    //抬起后的下边越界回弹
                    mScroller.springBack(getScrollX(), getScrollY(), 0, 0, mChildHeight - mSelfHeight,
                            mChildHeight - mSelfHeight);
                    invalidate();
                    break;
                }

                /* 没处于越界范围,判断速度是否够了,够了就fling */

                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(512);
                float velocity = mVelocityTracker.getYVelocity();
                if (Math.abs(velocity) > mFlingVelocity) {
                    mScroller.fling(getScrollX(), getScrollY(),
                            0, (int) -velocity,
                            0, 0,
                            0, mChildHeight - mSelfHeight,
                            0, over
                    );
                }

                break;

            default:
        }

        return true;
    }


    /**
     * 计算阻尼
     *
     * @param disY 滑动距离
     * @return 阻尼距离
     */
    private int changeDisY(float disY) {

        /* after scroll Y*/
        float nextY = getScrollY() - disY;

        /* 计算上边超出距离的阻尼 */

        /* over scroll minY */
        if (nextY < -over) {

            /* because is scrolling return a value, return 0 is same */
            return 1;
        }

        /* 0~ -over */
        if (nextY < 0) {

            /* 计算超出的top的距离和最大over距离的比值,越接近最大比值,返回的移动距离越小  */
            float v1 = -nextY / over;

            if (v1 > 1f) {

                /* 超出最大over距离,可以返回0,也可以象征性的返回1,毕竟有移动 */

                return 1;
            } else if (v1 > 0f) {

                /* 比值在0~1 之间,转换一下,越接近1移动距离越小 */
                float v2 = disY * (1 - v1);
                if (v2 < 1) {

                    /* 最少移动一个像素 */

                    return 1;
                }

                /* 多于一个像素 */
                return (int) v2;
            }
        }

        /* 计算下边超出距离的阻尼,同上边 */

        int i = mChildHeight - mSelfHeight + over;
        if (nextY > i) {

            return -1;
        }

        int i1 = mChildHeight - mSelfHeight;
        if (nextY > i1) {

            float v1 = nextY - (i1);
            float v2 = v1 / over;

            if (v2 > 1f) {
                return -1;
            } else if (v2 > 0f) {

                float v3 = disY * (1 - v2);

                if (v3 > -1) {
                    return -1;
                }

                return (int) v3;
            }
        }

        return (int) disY;
    }


    /**
     * 和{@link #overScrollBy(int, int, int, int, int, int, int, int, boolean)} 配合越界滚动
     */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {

        scrollTo(scrollX, scrollY);
    }


    @Override
    public void computeScroll() {

        /* fling 操作 */

        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
            return;
        }

        super.computeScroll();
    }


    @Override
    protected void onDetachedFromWindow() {

        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        super.onDetachedFromWindow();
    }
}
