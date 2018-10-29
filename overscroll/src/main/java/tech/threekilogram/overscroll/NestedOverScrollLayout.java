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
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Liujin 2018-10-28:21:17
 */
public class NestedOverScrollLayout extends ViewGroup implements NestedScrollingParent2,
                                                                 NestedScrollingChild2 {

      private static final String TAG = NestedOverScrollLayout.class.getSimpleName();

      private NestedScrollingParentHelper mParentHelper;
      private NestedScrollingChildHelper  mChildHelper;

      private int[] mConsumed = new int[ 2 ];
      private int[] mOffsetInWindow = new int[ 2 ];

      private int mMaxOver = 100;

      public NestedOverScrollLayout ( Context context ) {

            this( context, null, 0 );
      }

      public NestedOverScrollLayout ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public NestedOverScrollLayout ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init();
      }

      protected void init ( ) {

            mParentHelper = new NestedScrollingParentHelper( this );
            mChildHelper = new NestedScrollingChildHelper( this );
            setNestedScrollingEnabled( true );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthMode = MeasureSpec.getMode( widthMeasureSpec );
            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightMode = MeasureSpec.getMode( heightMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            View view = getChildAt( 0 );
            view.measure( widthMeasureSpec, heightMeasureSpec );
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
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            View child = getChildAt( 0 );
            child.layout( 0, 0, child.getMeasuredWidth(), child.getMeasuredHeight() );
      }

      // ========================= parent =========================

      @Override
      public boolean onStartNestedScroll (
          @NonNull View child, @NonNull View target, int axes, int type ) {

            //Log.e( TAG, "onStartNestedScroll : " );
            return ( axes & ViewCompat.SCROLL_AXIS_VERTICAL ) != 0;
      }

      @Override
      public void onNestedScrollAccepted (
          @NonNull View child, @NonNull View target, int axes, int type ) {

            //Log.e( TAG, "onNestedScrollAccepted : " );
            mParentHelper.onNestedScrollAccepted( child, target, axes, type );
            startNestedScroll( ViewCompat.SCROLL_AXIS_VERTICAL, type );
      }

      @Override
      public void onStopNestedScroll ( @NonNull View target, int type ) {

            //Log.e( TAG, "onStopNestedScroll : " );
            mChildHelper.stopNestedScroll( type );
            stopNestedScroll( type );
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

//            Log.e(
//                TAG, "onNestedScroll : "
//                    + " consumed: " + dyConsumed
//                    + " unConsumed: " + dyUnconsumed
//                    + " offset: " + offsetY
//            );

            if( dyUnconsumed < 0 ) {
                  /* 向下 */
                  int parentLeft = dyUnconsumed + offsetY;
                  if( parentLeft < 0 ) {

                        int scrollDy = parentLeft;
                        int scrollY = getScrollY();
                        if( scrollY + scrollDy < -mMaxOver ) {
                              scrollDy = -mMaxOver - scrollY;
                        }
                        scrollBy( 0, scrollDy );
                  }
            }
      }

      @Override
      public void onNestedPreScroll (
          @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type ) {

            int scrollY = getScrollY();
            if( dy > 0 && scrollY < 0 ) {

                  int sY = dy;
                  if( sY + scrollY > 0 ) {
                        sY = -scrollY;
                        scrollBy( 0, sY );
                        int left = dy - sY;

                        mConsumed[ 0 ] = mConsumed[ 1 ] = 0;
                        mChildHelper
                            .dispatchNestedPreScroll( dx, left, mConsumed, mOffsetInWindow, type );

                        consumed[ 1 ] = mConsumed[ 1 ] + sY;
                  } else {
                        scrollBy( 0, sY );

                        consumed[ 1 ] = dy;
                  }

                  return;
            }

            mChildHelper
                .dispatchNestedPreScroll( dx, dy, consumed, mOffsetInWindow, type );

//            Log.e(
//                TAG, "onNestedPreScroll : "
//                    + " dy: " + dy
//                    + " consumed: " + consumed[ 1 ]
//                    + " offset: " + mOffsetInWindow[ 1 ]
//            );
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
}