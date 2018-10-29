package tech.threekilogram.overscroll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

/**
 * @author Liujin 2018-10-28:21:17
 */
public class NestedRefreshLayout extends ViewGroup implements NestedScrollingParent2,
                                                              NestedScrollingChild2 {

      private static final String TAG = NestedRefreshLayout.class.getSimpleName();

      private NestedScrollingParentHelper mParentHelper;
      private NestedScrollingChildHelper  mChildHelper;

      private int[] mConsumed = new int[ 2 ];
      private int[] mOffsetInWindow = new int[ 2 ];

      private int mMaxOverScrollDistance = 200;
      private int mAction;

      private OverScroller mScroller;

      private RefreshAdapter mRefreshAdapter;

      public NestedRefreshLayout ( Context context ) {

            this( context, null, 0 );
      }

      public NestedRefreshLayout ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public NestedRefreshLayout ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init( context );
      }

      protected void init ( Context context ) {

            mParentHelper = new NestedScrollingParentHelper( this );
            mChildHelper = new NestedScrollingChildHelper( this );
            setNestedScrollingEnabled( true );

            mScroller = new OverScroller( context, new DecelerateInterpolator() );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthMode = MeasureSpec.getMode( widthMeasureSpec );
            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightMode = MeasureSpec.getMode( heightMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            View view = getChildAt( 0 );
            measureChild( view, widthMeasureSpec, heightMeasureSpec );
            int minContentWidth = view.getMeasuredWidth();
            int minContentHeight = view.getMeasuredHeight();

            int finalWidth = 0;
            int finalHeight = 0;

            if( widthMode == MeasureSpec.EXACTLY ) {

                  finalWidth = widthSize;
            } else if( widthMode == MeasureSpec.AT_MOST ) {

                  finalWidth = Math
                      .min( ( minContentWidth ), widthSize );
            } else {

                  finalWidth = minContentWidth;
            }

            if( heightMode == MeasureSpec.EXACTLY ) {

                  finalHeight = heightSize;
            } else if( widthMode == MeasureSpec.AT_MOST ) {

                  finalHeight = Math
                      .min( ( minContentHeight ), heightSize );
            } else {

                  finalHeight = minContentHeight;
            }

            setMeasuredDimension( finalWidth, finalHeight );

            try {
                  View child = getChildAt( 1 );
                  measureChild(
                      child,
                      MeasureSpec.makeMeasureSpec( finalWidth, MeasureSpec.EXACTLY ),
                      MeasureSpec.makeMeasureSpec( finalHeight, MeasureSpec.AT_MOST )
                  );

                  int measuredHeight = child.getMeasuredHeight();

                  if( measuredHeight > mMaxOverScrollDistance ) {
                        mMaxOverScrollDistance = measuredHeight;
                  }
            } catch(Exception e) {
                  /* nothing */
            }
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            View child = getChildAt( 0 );
            child.layout( 0, 0, child.getMeasuredWidth(), child.getMeasuredHeight() );

            try {
                  View view = getChildAt( 1 );
                  view.layout( 0, -view.getMeasuredHeight(), view.getMeasuredWidth(), 0 );
            } catch(Exception e) {
                  /* nothing */
            }
      }

      @Override
      public boolean dispatchTouchEvent ( MotionEvent ev ) {

            mAction = ev.getAction();
            return super.dispatchTouchEvent( ev );
      }

      @Override
      public void computeScroll ( ) {

            super.computeScroll();
            if( mScroller.computeScrollOffset() ) {
                  int currY = mScroller.getCurrY();
                  scrollTo( getScrollX(), currY );
                  if( mRefreshAdapter != null ) {
                        mRefreshAdapter.onScrollBack( getScrollY() );
                  }
                  invalidate();
            }
      }

      public void setMaxOverScrollDistance ( int maxOverScrollDistance ) {

            mMaxOverScrollDistance = maxOverScrollDistance;
      }

      public void setRefreshAdapter ( RefreshAdapter refreshAdapter ) {

            mRefreshAdapter = refreshAdapter;
            addView(
                refreshAdapter.getRefreshView( getContext() ),
                1
            );
      }

      public RefreshAdapter getRefreshAdapter ( ) {

            return mRefreshAdapter;
      }

      // ========================= parent =========================

      @Override
      public boolean onStartNestedScroll (
          @NonNull View child, @NonNull View target, int axes, int type ) {

            return ( axes & ViewCompat.SCROLL_AXIS_VERTICAL ) != 0;
      }

      @Override
      public void onNestedScrollAccepted (
          @NonNull View child, @NonNull View target, int axes, int type ) {

            mParentHelper.onNestedScrollAccepted( child, target, axes, type );
            startNestedScroll( ViewCompat.SCROLL_AXIS_VERTICAL, type );
      }

      @Override
      public void onStopNestedScroll ( @NonNull View target, int type ) {

            mChildHelper.stopNestedScroll( type );
            stopNestedScroll( type );

            if( mAction == MotionEvent.ACTION_UP ) {
                  mAction = 0;
                  if( mRefreshAdapter != null ) {
                        mRefreshAdapter.release( getScrollY(), mMaxOverScrollDistance );
                  }
            }
      }

      /**
       * 向回滚动一段距离
       */
      public void startScroll ( int dy ) {

            int scrollY = getScrollY();
            int duration = Math.abs( scrollY ) / 100 * 200;
            if( !mScroller.isFinished() ) {
                  mScroller.forceFinished( true );
            }
            duration = Math.min( duration, 300 );
            mScroller.startScroll( 0, scrollY, 0, dy, duration );
            invalidate();
      }

      @Override
      public void onNestedScroll (
          @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
          int type ) {

            mChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed,
                mOffsetInWindow,
                type
            );

            int offsetY = mOffsetInWindow[ 1 ];

            if( mAction == MotionEvent.ACTION_MOVE && dyUnconsumed < 0 ) {
                  /* 向下 */
                  int parentLeft = dyUnconsumed + offsetY;
                  if( parentLeft < 0 ) {
                        int scrollY = getScrollY();
                        int scrollDy = calculateScrollDy(
                            parentLeft, scrollY, -mMaxOverScrollDistance );
                        if( scrollY + scrollDy < -mMaxOverScrollDistance ) {
                              scrollDy = -mMaxOverScrollDistance - scrollY;
                              scrollBy( 0, scrollDy );
                        } else {

                              scrollBy( 0, scrollDy );
                        }

                        if( mRefreshAdapter != null ) {
                              mRefreshAdapter.onMove( -1, getScrollY(), mMaxOverScrollDistance );
                        }
                  }
            }
      }

      /**
       * 计算阻尼后移动距离
       */
      private int calculateScrollDy ( int dy, int scrollY, int maxScrollY ) {

            float v = scrollY * 1f / maxScrollY;
            int i = (int) ( dy * Math.abs( 1 - v ) );
            if( i == 0 ) {
                  i = dy / Math.abs( dy );
            }
            return i;
      }

      @Override
      public void onNestedPreScroll (
          @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type ) {

            int scrollY = getScrollY();

            if( mAction == MotionEvent.ACTION_MOVE && dy > 0 && scrollY < 0 ) {

                  int scrollDy = dy;
                  if( scrollDy + scrollY > 0 ) {
                        scrollDy = -scrollY;
                        scrollBy( 0, scrollDy );
                        int left = dy - scrollDy;

                        mConsumed[ 0 ] = mConsumed[ 1 ] = 0;
                        mChildHelper
                            .dispatchNestedPreScroll( dx, left, mConsumed, mOffsetInWindow, type );

                        consumed[ 1 ] = mConsumed[ 1 ] + scrollDy;
                  } else {

                        scrollBy( 0, scrollDy );
                        consumed[ 1 ] = scrollDy;
                  }

                  if( mRefreshAdapter != null ) {
                        mRefreshAdapter.onMove( 1, getScrollY(), mMaxOverScrollDistance );
                  }

                  return;
            }

            mChildHelper.dispatchNestedPreScroll(
                dx, dy,
                consumed,
                mOffsetInWindow,
                type
            );
      }

      // ========================= child =========================

      @Override
      public void setNestedScrollingEnabled ( boolean enabled ) {

            super.setNestedScrollingEnabled( enabled );
            mChildHelper.setNestedScrollingEnabled( enabled );
      }

      @Override
      public boolean isNestedScrollingEnabled ( ) {

            return mChildHelper.isNestedScrollingEnabled();
      }

      @Override
      public boolean startNestedScroll ( int axes, int type ) {

            return mChildHelper.startNestedScroll( axes, type );
      }

      @Override
      public void stopNestedScroll ( int type ) {

            mChildHelper.stopNestedScroll( type );
      }

      @Override
      public boolean hasNestedScrollingParent ( int type ) {

            return mChildHelper.hasNestedScrollingParent( type );
      }

      @Override
      public boolean dispatchNestedScroll (
          int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
          @Nullable int[] offsetInWindow, int type ) {

            return mChildHelper
                .dispatchNestedScroll( dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                                       offsetInWindow, type
                );
      }

      @Override
      public boolean dispatchNestedPreScroll (
          int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type ) {

            return mChildHelper.dispatchNestedPreScroll( dx, dy, consumed, offsetInWindow, type );
      }

      @Override
      public boolean dispatchNestedFling ( float velocityX, float velocityY, boolean consumed ) {

            return mChildHelper.dispatchNestedFling( velocityX, velocityY, consumed );
      }

      @Override
      public boolean dispatchNestedPreFling ( float velocityX, float velocityY ) {

            return mChildHelper.dispatchNestedPreFling( velocityX, velocityY );
      }

      public interface RefreshAdapter {

            /**
             * 获取refresh view
             *
             * @param context context
             *
             * @return refresh view
             */
            View getRefreshView ( Context context );

            /**
             * 触摸刷新时回调
             *
             * @param orientation 方向
             * @param scrollY 当前
             * @param maxScrollY 最大
             */
            void onMove ( int orientation, int scrollY, int maxScrollY );

            /**
             * 手指释放
             *
             * @param scrollY 当前
             * @param maxScrollY 最大
             */
            void release ( int scrollY, int maxScrollY );

            /**
             * 自动回滚时
             *
             * @param scrollY 当前
             */
            void onScrollBack ( int scrollY );
      }
}