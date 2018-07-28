package tech.threekilogram.overscroll;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import tech.threekilogram.overscroll.listener.OnScrollListener;
import tech.threekilogram.overscroll.listener.RecyclerScrollListener;

/**
 * 为 recyclerView 添加头布局/底布局
 *
 * @author wuxio 2018-04-07:19:03
 */
public class HeaderFooterLayout extends OverScrollContainer {

      /**
       * 头布局
       */
      protected View mHeader;

      /**
       * 底布局
       */
      protected View mFooter;

      /**
       * overScroll 监听,用于通知overScroll 情况
       */
      private OnOverScrollListener mOverScrollListener;

      /**
       * 标记手指抬起后是否运行,回弹
       */
      private boolean isStopSpringBack = false;

      public HeaderFooterLayout (Context context) {

            this(context, null, 0);
      }

      public HeaderFooterLayout (Context context, AttributeSet attrs) {

            this(context, attrs, 0);
      }

      public HeaderFooterLayout (Context context, AttributeSet attrs, int defStyleAttr) {

            super(context, attrs, defStyleAttr);
      }

      //============================ implements method ============================

      @Override
      public void addScrollListener () {

            if(mChild instanceof RecyclerView) {
                  ((RecyclerView) mChild).addOnScrollListener(new RecyclerScrollListener(this));
                  return;
            }
            if(mChild instanceof NestedScrollView) {
                  ((NestedScrollView) mChild).setOnScrollChangeListener(new OnScrollListener(this));
            }
      }

      //============================ 设置 header footer ============================

      /**
       * 设置 header View
       *
       * @param headerLayoutId 布局ID
       */
      public void setHeader (@LayoutRes int headerLayoutId) {

            View view = LayoutInflater.from(getContext()).inflate(headerLayoutId, this, false);
            setHeader(view);
      }

      /**
       * 设置 header View
       *
       * @param header view
       */
      public void setHeader (View header) {

            setHeader(header, generateWrapHeightLayoutParams());
      }

      /**
       * 设置 header View
       *
       * @param header view
       */
      public void setHeader (View header, LayoutParams params) {

            if(mHeader != null) {
                  removeView(mHeader);
            }
            mHeader = header;
            addView(header, 0, params);
      }

      /**
       * 设置 footer View
       *
       * @param footerLayoutId 布局ID
       */
      public void setFooter (@LayoutRes int footerLayoutId) {

            View view = LayoutInflater.from(getContext()).inflate(footerLayoutId, this, false);
            setFooter(view);
      }

      /**
       * 设置 footer View
       *
       * @param footer view
       */
      public void setFooter (View footer) {

            setFooter(footer, generateWrapHeightLayoutParams());
      }

      /**
       * 设置 footer View
       *
       * @param footer view
       */
      public void setFooter (View footer, LayoutParams params) {

            if(mFooter != null) {
                  removeView(mFooter);
            }
            mFooter = footer;
            addView(footer, params);
      }

      //============================ 布局 ============================

      @Override
      protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if(mHeader != null) {
                  measureChildWithMargins(
                      mHeader,
                      widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                      heightMeasureSpec, getPaddingTop() + getPaddingBottom()
                  );
            }

            if(mFooter != null) {
                  measureChildWithMargins(
                      mFooter,
                      widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                      heightMeasureSpec, getPaddingTop() + getPaddingBottom()
                  );
            }
      }

      @Override
      protected void onLayout (boolean changed, int l, int t, int r, int b) {

            super.onLayout(changed, l, t, r, b);

            /* 布局header到 recyclerView 上边 */

            if(mHeader != null) {

                  LayoutParams params = (LayoutParams) mHeader.getLayoutParams();
                  int left = getPaddingLeft() + params.leftMargin;
                  int top = getPaddingTop() - params.bottomMargin - mHeader.getMeasuredHeight();
                  int right = left + mHeader.getMeasuredWidth();
                  int bottom = top + mHeader.getMeasuredHeight();

                  mHeader.layout(left, top, right, bottom);
            }

            /* 布局footer到 recyclerView 下边 */

            if(mFooter != null) {
                  LayoutParams params = (LayoutParams) mFooter.getLayoutParams();
                  int left = getPaddingLeft() + params.leftMargin;
                  int top = getHeight() + params.topMargin;
                  int right = left + mFooter.getMeasuredWidth();
                  int bottom = top + mFooter.getMeasuredHeight();

                  mFooter.layout(left, top, right, bottom);
            }
      }

      //============================ 控制滑动 ============================

      @Override
      public void scrollTo (int x, int y) {

            super.scrollTo(x, y);

            /* 根据当前状态回调监听 */

            if(mOverScrollListener == null) {
                  return;
            }

            if(state == OVER_TOP) {
                  mOverScrollListener.onScrollOverTop(mHeader, getScrollY());
            }

            if(state == OVER_BOTTOM) {
                  mOverScrollListener.onScrollOverBottom(mFooter, getScrollY());
            }
      }

      /**
       * 根据{@link #isStopSpringBack}控制回弹
       */
      @Override
      protected void springBackFromTop () {

            if(isStopSpringBack) {
                  return;
            }

            super.springBackFromTop();
      }

      /**
       * 根据{@link #isStopSpringBack}控制回弹
       */
      @Override
      protected void springBackFromBottom () {

            if(isStopSpringBack) {
                  return;
            }

            super.springBackFromBottom();
      }

      /**
       * 调用之后,发生overScroll后,手指抬起不会回弹,使用{@link #scrollBack()}回弹到原点
       */
      public void stopScrollBack () {

            isStopSpringBack = true;
      }

      /**
       * 回弹到原点
       */
      public void scrollBack () {

            isStopSpringBack = false;

            if(getScrollY() < 0) {
                  springBackFromTop();
            }

            if(getScrollY() > 0) {
                  springBackFromBottom();
            }
      }

      /**
       * 处于 overScroll 状态时,调用该方法回弹一小段距离
       */
      public void scrollBack (int dy) {

            int scrollY = getScrollY();

            /* 修正距离过大/过小,不要超过overScrollDistance */

            if(scrollY < 0) {

                  /* over top */

                  if(scrollY + dy > 0) {
                        dy = -scrollY;
                  }
                  state = SCROLL_BACK;
            }

            if(scrollY > 0) {

                  /* over bottom */

                  if(scrollY + dy < 0) {
                        dy = -scrollY;
                  }
                  state = SCROLL_BACK;
            }

            mScroller.startScroll(0, scrollY, 0, dy);
            invalidate();
      }

      /**
       * 当recyclerView添加新的数据时,调用该方法,将header和footer重新放置到原始位置
       */
      public void reLayout () {

            int scrollY = getScrollY();
            scrollTo(0, 0);
            mChild.scrollBy(0, scrollY);
            state = NORMAL;
      }

      @Override
      public boolean dispatchTouchEvent (MotionEvent ev) {

            boolean b = super.dispatchTouchEvent(ev);

            /* 回调监听,通知overScroll 结束 */

            if(ev.getAction() == MotionEvent.ACTION_UP) {

                  if(mOverScrollListener == null) {
                        return b;
                  }

                  if(getScrollY() < 0) {
                        mOverScrollListener.onOverTopTouchUp(mHeader, getScrollY());
                        return b;
                  }

                  if(getScrollY() > 0) {
                        mOverScrollListener.onOverBottomTouchUp(mFooter, getScrollY());
                  }
            }

            return b;
      }

      //============================layout params============================

      @Override
      protected LayoutParams generateDefaultLayoutParams () {

            return new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );
      }

      protected LayoutParams generateWrapHeightLayoutParams () {

            return new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
      }

      @Override
      protected LayoutParams generateLayoutParams (ViewGroup.LayoutParams p) {

            return new LayoutParams(p);
      }

      @Override
      public LayoutParams generateLayoutParams (AttributeSet attrs) {

            return new LayoutParams(getContext(), attrs);
      }

      public static class LayoutParams extends OverScrollContainer.LayoutParams {

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

      //============================OverScrollListener============================

      public void setOverScrollListener (OnOverScrollListener overScrollListener) {

            mOverScrollListener = overScrollListener;
      }

      /**
       * 发生overScroll时,回调该接口
       */
      public interface OnOverScrollListener {

            /**
             * 当发生top overScroll时,回调
             *
             * @param header header view
             * @param scrollY 当前scrollY
             */
            void onScrollOverTop (View header, int scrollY);

            /**
             * 当发生top overScroll后,手指抬起后回调
             *
             * @param header header view
             * @param scrollY 当前scrollY
             */
            void onOverTopTouchUp (View header, int scrollY);

            /**
             * 当发生bottom overScroll时,回调
             *
             * @param footer footer view
             * @param scrollY 当前scrollY
             */
            void onScrollOverBottom (View footer, int scrollY);

            /**
             * 当发生bottom overScroll后,手指抬起后回调
             *
             * @param footer footer view
             * @param scrollY 当前scrollY
             */
            void onOverBottomTouchUp (View footer, int scrollY);
      }
}
