package tech.threekilogram.overscroll;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ScrollingView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.OverScroller;
import com.example.engine.TimeEngine;

/**
 * 为 recycler view 增加overScroll 功能,当recyclerView滑动到顶部,可以继续下拉,下拉有阻尼效果,
 * 当当recyclerView滑动到底部,可以继续上拉,上拉有阻尼效果,支持recycler fling, fling 到顶部/底部时,会继续fling
 * <p>
 * {@link #setOverScrollDistance(int)} 设置OverScroll 距离 {@link #setFlingOverScrollDistance(int)} 设置
 * fling 的 OverScroll 距离
 * <p>
 * 只能包含一个 recycler 子view
 *
 * @author wuxio 2018-04-08:6:28
 */
public abstract class OverScrollContainer<V extends ViewGroup>
    extends ViewGroup
    implements NestedScrollingParent {

      protected V mChild;

      /**
       * 用于fling ,OverScroll之后回滚
       */
      protected OverScroller mScroller;
      /**
       * 记录{@link RecyclerView#computeVerticalScrollExtent()}距离
       */
      protected int          mScrollExtent;
      /**
       * 记录{@link RecyclerView#computeVerticalScrollOffset()}距离
       */
      protected int          mScrollOffset;
      /**
       * 记录{@link RecyclerView#computeVerticalScrollRange()}距离
       */
      protected int          mScrollRange;
      /**
       * 最大的 overScroll 距离
       */
      protected int mOverScrollDistance      = 400;
      /**
       * fling 时最大 overScroll 距离
       */
      protected int mFlingOverScrollDistance = 400;

      /**
       * recycler 滑动
       */
      protected static final int NORMAL      = 0;
      /**
       * recycler滑动到顶部了,继续下拉的状态
       */
      protected static final int OVER_TOP    = 1;
      /**
       * recycler滑动到底部了,继续上拉的状态
       */
      protected static final int OVER_BOTTOM = 2;
      /**
       * 正在 fling
       */
      protected static final int FLING       = 3;
      /**
       * 手指抬起后正在回弹
       */
      protected static final int SCROLL_BACK = 4;
      /**
       * 当前状态标记
       */
      protected              int state       = NORMAL;
      /**
       * {@link #SCROLL_BACK} 状态时起始坐标
       */
      protected int mStartY;
      /**
       * {@link #SCROLL_BACK} 状态时结束坐标
       */
      protected int mEndY;
      /**
       * {@link #SCROLL_BACK} 时长
       */
      protected int mScrollBackDuration = 240;
      /**
       * {@link #SCROLL_BACK} 用于回弹
       */
      protected TimeEngine mTimeEngine;
      /**
       * 用于结束状态
       */
      protected boolean    mIsEngineRunning;

      public OverScrollContainer (Context context) {

            this(context, null, 0);
      }

      public OverScrollContainer (Context context, AttributeSet attrs) {

            this(context, attrs, 0);
      }

      public OverScrollContainer (Context context, AttributeSet attrs, int defStyleAttr) {

            super(context, attrs, defStyleAttr);
            init();
      }

      protected void init () {

            mScroller = new OverScroller(getContext());
            mTimeEngine = new TimeEngine(new AccelerateInterpolator());
      }

      /**
       * 设置最大 over scroll 距离
       *
       * @param overScrollDistance touch overScroll distance
       */
      public void setOverScrollDistance (int overScrollDistance) {

            mOverScrollDistance = overScrollDistance;
      }

      /**
       * 设置最大 fling over 距离
       *
       * @param flingOverScrollDistance fling touch overScroll distance
       */
      public void setFlingOverScrollDistance (int flingOverScrollDistance) {

            mFlingOverScrollDistance = flingOverScrollDistance;
      }

      @Override
      protected void onFinishInflate () {

            super.onFinishInflate();
            initChild();
            addScrollListener();
      }

      @SuppressWarnings("unchecked")
      public void initChild () {

            mChild = (V) getChildAt(0);
      }

      /**
       * 为布局中的子view添加滚动监听,该监听必须在子view滑动时回调{@link #observeScroll(ScrollingView)}, 或者完成{@link
       * #observeScroll(ScrollingView)}相同的操作
       */
      public abstract void addScrollListener ();

      @Override
      protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {

            measureChildWithMargins(mChild,
                                    widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                                    heightMeasureSpec, getPaddingTop() + getPaddingBottom()
            );

            int measuredWidth = mChild.getMeasuredWidth();
            int measuredHeight = mChild.getMeasuredHeight();

            setMeasuredDimension(
                measuredWidth + getPaddingLeft() + getPaddingRight(),
                measuredHeight + getPaddingTop() + getPaddingBottom()
            );
      }

      @Override
      protected void onLayout (boolean changed, int l, int t, int r, int b) {

            LayoutParams params = ((LayoutParams) mChild.getLayoutParams());

            int left = getPaddingLeft() + params.leftMargin;
            int top = getPaddingTop() + params.topMargin;
            int right = left + mChild.getMeasuredWidth();
            int bottom = top + mChild.getMeasuredHeight();

            mChild.layout(
                left,
                top,
                right,
                bottom
            );
      }

      /**
       * 如果{@link #mScroller}正在运行,停止他
       * <p>
       * abort scroller
       */
      protected void finishScrollerIfNotFinish () {

            if(!mScroller.isFinished()) {
                  mScroller.abortAnimation();
            }
            if(mTimeEngine.isRunning()) {
                  mEndY = mStartY = getScrollY();
                  mTimeEngine.stop();
            }
      }

      @Override
      public boolean onStartNestedScroll (View child, View target, int nestedScrollAxes) {

            return child == mChild;
      }

      @Override
      public void onNestedPreScroll (View target, int dx, int dy, int[] consumed) {

            super.onNestedPreScroll(target, dx, dy, consumed);

            finishScrollerIfNotFinish();

            int scrollY = getScrollY();

            /* scroll to top , go on scroll min to -mOverScrollDistance */

            if(overTopScroll(scrollY, dy, consumed)) {
                  return;
            }

            /* top still have shown, scroll to hide  */

            if(overTopScrollBack(scrollY, dy, consumed)) {
                  return;
            }

            /* scroll to bottom , go on scroll, max to mOverScrollDistance*/

            if(overBottomScroll(scrollY, dy, consumed)) {
                  return;
            }

            /* bottom still have shown, scroll to hide  */

            overBottomScrollBack(scrollY, dy, consumed);
      }

      /**
       * 当recycler滑动到顶部,继续向下滑动的话,开始overScroll
       *
       * @param scrollY 当前
       * @param dy 新的滑动距离
       * @param consumed 输出
       *
       * @return true:{@link #onNestedPreScroll(View, int, int, int[])}将返回,不再执行后面的代码
       */
      protected boolean overTopScroll (int scrollY, int dy, int[] consumed) {

            if(mScrollOffset == 0 && dy < 0) {
                  state = OVER_TOP;
                  dy = calculateScrollDownDy(scrollY, dy, -mOverScrollDistance);
                  scrollBy(0, dy);
                  consumed[1] = dy;
                  return true;
            }

            return false;
      }

      /**
       * 当 top 已经发生overScroll时,向上滑动,将top的overScroll向上移回
       *
       * @param scrollY 当前
       * @param dy 新的滑动距离
       * @param consumed 输出
       *
       * @return true:{@link #onNestedPreScroll(View, int, int, int[])}将返回,不再执行后面的代码
       */
      protected boolean overTopScrollBack (int scrollY, int dy, int[] consumed) {

            if(scrollY < 0 && dy > 0) {

                  if(scrollY + dy > 0) {
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
       * 当recycler滑动到底部,继续向上滑动的话,开始overScroll
       *
       * @param scrollY 当前
       * @param dy 新的滑动距离
       * @param consumed 输出
       *
       * @return true:{@link #onNestedPreScroll(View, int, int, int[])}将返回,不再执行后面的代码
       */
      protected boolean overBottomScroll (int scrollY, int dy, int[] consumed) {

            if(mScrollOffset + mScrollExtent == mScrollRange && dy > 0) {
                  state = OVER_BOTTOM;
                  dy = calculateScrollUpDy(scrollY, dy, mOverScrollDistance);
                  scrollBy(0, dy);
                  consumed[1] = dy;
                  return true;
            }
            return false;
      }

      /**
       * 当 bottom 已经发生overScroll时,向下滑动,将bottom的overScroll向下移回
       *
       * @param scrollY 当前
       * @param dy 新的滑动距离
       * @param consumed 输出
       *
       * @return true:{@link #onNestedPreScroll(View, int, int, int[])}将返回,不再执行后面的代码
       */
      protected boolean overBottomScrollBack (int scrollY, int dy, int[] consumed) {

            if(scrollY > 0 && dy < 0) {

                  if(scrollY + dy < 0) {
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
       * @param dy 新收到的的滑动距离
       * @param minY 可滑动到的最小y点
       *
       * @return 阻尼后滑动距离
       */
      protected int calculateScrollDownDy (int scrollY, int dy, int minY) {

            int toY = scrollY + dy;

            if(toY < 0) {

                  final float base = 1f;

                  float nearByPercent = toY * 1.f / minY;
                  float newDY = dy * (1 - nearByPercent);

                  if(-newDY < base) {
                        newDY = -1;
                  }

                  toY = scrollY + (int) newDY;

                  if(toY < minY) {
                        dy = minY - scrollY;
                        return dy;
                  }

                  return (int) newDY;
            }

            return dy;
      }

      /**
       * recycler滑动到底部时,继续向上滑动,阻尼系数计算
       *
       * @param scrollY 当前的scrollY
       * @param dy 新收到的的滑动距离
       *
       * @return 阻尼后滑动距离
       */
      protected int calculateScrollUpDy (int scrollY, int dy, int maxY) {

            int toY = scrollY + dy;

            if(toY > 0) {

                  final float base = 1f;

                  float nearByPercent = toY * 1.f / maxY;
                  float newDY = dy * (1 - nearByPercent);

                  if(newDY < base) {
                        newDY = 1;
                  }

                  toY = scrollY + (int) newDY;

                  if(toY > maxY) {
                        dy = maxY - scrollY;
                        return dy;
                  }

                  return (int) newDY;
            }

            return dy;
      }

      @Override
      public boolean onNestedFling (
          @NonNull View target, float velocityX, float velocityY, boolean consumed) {

            boolean result = super.onNestedFling(target, velocityX, velocityY, consumed);

            finishScrollerIfNotFinish();

            /* when is showing top or bottom , don't  fling  */

            if(state == OVER_TOP) {
                  return super.onNestedFling(target, velocityX, velocityY, consumed);
            }

            if(state == OVER_BOTTOM) {
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

            return result;
      }

      @Override
      public void computeScroll () {

            if(state == NORMAL) {
                  return;
            }

            if(mScroller.computeScrollOffset()) {
                  int y = mScroller.getCurrY();
                  scrollTo(getScrollX(), y);
                  invalidate();
                  return;
            }

            if(mTimeEngine.isRunning()) {
                  float fraction = mTimeEngine.getFraction();
                  float currentY = mStartY + fraction * (mEndY - mStartY);
                  scrollTo(getScrollX(), (int) currentY);
                  invalidate();
                  mIsEngineRunning = true;
                  return;
            }

            if(mIsEngineRunning) {
                  scrollTo(getScrollX(), 0);
                  mIsEngineRunning = false;
            }

            /* 之前都是 return 能执行到这,说明是invalidate调用了, computeScrollOffset结束了,需要更改状态标记*/

            if(state == SCROLL_BACK || state == FLING) {
                  state = NORMAL;
            }
      }

      @Override
      public boolean dispatchTouchEvent (MotionEvent ev) {

            boolean event = super.dispatchTouchEvent(ev);

            int action = ev.getAction();

            if(action == MotionEvent.ACTION_DOWN) {
                  finishScrollerIfNotFinish();
            }

            if(action == MotionEvent.ACTION_UP) {

                  if(state == OVER_TOP) {
                        springBackFromTop();
                  }

                  if(state == OVER_BOTTOM) {
                        springBackFromBottom();
                  }
            }

            return event;
      }

      /**
       * 设置处于overScroll 模式下回弹时长
       *
       * @param scrollBackDuration 新的时长
       */
      public void setScrollBackDuration (@IntRange(from = 0) int scrollBackDuration) {

            if(scrollBackDuration < 0) {
                  mScrollBackDuration = 240;
                  return;
            }
            mScrollBackDuration = scrollBackDuration;
      }

      /**
       * 手指抬起后,回弹到原点
       */
      protected void springBackFromTop () {

            state = SCROLL_BACK;
            mStartY = getScrollY();
            mEndY = 0;

            mTimeEngine.setDuration(mScrollBackDuration).start();
            mIsEngineRunning = true;
            invalidate();
      }

      /**
       * 手指抬起后,回弹到原点
       */
      protected void springBackFromBottom () {

            state = SCROLL_BACK;
            mStartY = getScrollY();
            mEndY = 0;

            mTimeEngine.setDuration(mScrollBackDuration).start();
            mIsEngineRunning = true;
            invalidate();
      }

      //============================layout params============================

      @Override
      protected ViewGroup.LayoutParams generateDefaultLayoutParams () {

            return new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
      }

      @Override
      protected ViewGroup.LayoutParams generateLayoutParams (ViewGroup.LayoutParams p) {

            return new LayoutParams(p);
      }

      @Override
      public ViewGroup.LayoutParams generateLayoutParams (AttributeSet attrs) {

            return new LayoutParams(getContext(), attrs);
      }

      public static class LayoutParams extends MarginLayoutParams {

            public LayoutParams (Context c, AttributeSet attrs) {

                  super(c, attrs);
            }

            public LayoutParams (int width, int height) {

                  super(width, height);
            }

            public LayoutParams (MarginLayoutParams source) {

                  super(source);
            }

            public LayoutParams (ViewGroup.LayoutParams source) {

                  super(source);
            }
      }

      //============================ 滚动监听 ============================

      public <T extends ScrollingView> void observeScroll (T t) {

            mScrollExtent = t.computeVerticalScrollExtent();
            mScrollOffset = t.computeVerticalScrollOffset();
            mScrollRange = t.computeVerticalScrollRange();

            /* begin fling */
            /* only recycler fling to edge,  container begin fling*/

            if(mScrollOffset == 0) {
                  if(!mScroller.isFinished()) {
                        state = FLING;
                        invalidate();
                        return;
                  }
            }

            if(mScrollOffset + mScrollExtent == mScrollRange) {
                  if(!mScroller.isFinished()) {
                        state = FLING;
                        invalidate();
                  }
            }
      }
}
