package com.example.wuxio.recyclerheaderfooter.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * @author wuxio 2018-05-12:11:03
 */
@SuppressWarnings("WeakerAccess")
public class BiliBiliDrawable extends ProgressDrawable {

      private final int STATE2 = 2;
      private final int STATE4 = 4;
      private final int STATE5 = 5;
      private final int STATE6 = 6;

      private   int         mRadius;
      protected Path        mSrcPath;
      protected PathMeasure mPathMeasure;
      protected Path        mDstPath;
      private   int         mCurrentState;
      private   float       mStateFraction;
      private   int         mSize;

      public BiliBiliDrawable ( ) {

            mPaint.setStyle( Paint.Style.STROKE );

            mSrcPath = new Path();
            mDstPath = new Path();
            mPathMeasure = new PathMeasure();
      }

      @Override
      protected void onBoundsChange ( Rect bounds ) {

            super.onBoundsChange( bounds );

            mSize = Math.min( bounds.width(), bounds.height() );
            mRadius = mSize / 10;
            int strokeWidth = mSize / 20;
            mPaint.setStrokeWidth( strokeWidth );
            int size20Percent = mSize / 5;
            int size80Percent = mSize - size20Percent;

            /* 一个矩形 */
            mSrcPath.moveTo( size20Percent, 0 );
            mSrcPath.rLineTo( mSize / 2 - size20Percent, size20Percent );
            mSrcPath.lineTo( mSize / 2, size20Percent );
            mSrcPath.lineTo( strokeWidth, size20Percent );
            mSrcPath.rLineTo( 0, size80Percent - strokeWidth );
            mSrcPath.rLineTo( mSize - strokeWidth * 2, 0 );
            mSrcPath.rLineTo( 0, -size80Percent + strokeWidth );
            mSrcPath.lineTo( mSize / 2 + 2, size20Percent );
            mSrcPath.lineTo( mSize - size20Percent, 0 );

            mPathMeasure.setPath( mSrcPath, false );
      }

      protected void calculate ( float fraction ) {

            /* 电视外廓 */

            final float flag01 = 0.6f;
            if( fraction <= flag01 ) {

                  mCurrentState = STATE2;
                  mStateFraction = ( fraction ) * 2.5f;
                  return;
            }

            final float flag03 = 0.73f;
            if( fraction <= flag03 ) {

                  mCurrentState = STATE4;
                  mStateFraction = ( fraction - flag01 ) * 5;
                  return;
            }

            final float flag04 = 0.86f;
            if( fraction <= flag04 ) {

                  mCurrentState = STATE5;
                  mStateFraction = ( fraction - flag03 ) * 5;
                  return;
            }

            final float flag05 = 1f;
            if( fraction < flag05 ) {

                  mCurrentState = STATE6;
                  mStateFraction = ( fraction - flag04 ) * 5;
            } else {

                  /* == 1 */

                  mCurrentState = STATE6;
                  mStateFraction = 1;
            }
      }

      @Override
      protected void draw ( @NonNull Canvas canvas, float progress ) {

            int width = getWidth();
            int height = getHeight();
            int dX = ( width - mSize ) >> 1;
            int dY = ( height - mSize ) >> 1;
            canvas.translate( dX, dY );

            calculate( progress );

            int size = mSize;
            int size20Percent = size / 5;

            if( mCurrentState == STATE2 ) {

                  /* 电视轮廓 */

                  drawState2( canvas, size, size20Percent, mStateFraction );
                  return;
            }

            if( mCurrentState == STATE4 ) {

                  /* 第一个点 */

                  drawState2( canvas, size, size20Percent, 1 );
                  drawState4( canvas, size, mStateFraction );
                  return;
            }

            if( mCurrentState == STATE5 ) {

                  /* 第二个点 */

                  drawState2( canvas, size, size20Percent, 1 );
                  drawState4( canvas, size, 1 );
                  drawState5( canvas, size, mStateFraction );
                  return;
            }

            if( mCurrentState == STATE6 ) {

                  /* 第三个点 */

                  drawState2( canvas, size, size20Percent, 1 );
                  drawState4( canvas, size, 1 );
                  drawState5( canvas, size, 1 );
                  drawState6( canvas, size, mStateFraction );
            }
      }

      private void drawState2 (
          @NonNull Canvas canvas, int size, int size20Percent, float stateFraction ) {

            mPaint.setStyle( Paint.Style.STROKE );

            final float length = mPathMeasure.getLength();

            /* 防止bug */
            mDstPath.reset();
            mDstPath.moveTo( size20Percent, 0 );

            float d = length * stateFraction;
            mPathMeasure.getSegment( 0, d, mDstPath, false );
            canvas.drawPath( mDstPath, mPaint );
      }

      private void drawState4 ( @NonNull Canvas canvas, int size, float stateFraction ) {

            int rX = size / 2 - size / 4;
            int rY = size * 60 / 100;

            int radius = mRadius;

            mPaint.setStyle( Paint.Style.FILL );
            int alpha = (int) ( 255 * stateFraction );
            mPaint.setAlpha( alpha );
            canvas.drawCircle( rX, rY, radius, mPaint );
            mPaint.setAlpha( 255 );
      }

      private void drawState5 ( @NonNull Canvas canvas, int size, float stateFraction ) {

            int rX = size / 2;
            int rY = size * 60 / 100;

            int radius = mRadius;

            int alpha = (int) ( 255 * stateFraction );
            mPaint.setAlpha( alpha );
            canvas.drawCircle( rX, rY, radius, mPaint );
            mPaint.setAlpha( 255 );
      }

      private void drawState6 ( @NonNull Canvas canvas, int size, float stateFraction ) {

            int rX = size / 2 + size / 4;
            int rY = size * 60 / 100;

            int radius = mRadius;

            int alpha = (int) ( 255 * stateFraction );
            mPaint.setAlpha( alpha );
            canvas.drawCircle( rX, rY, radius, mPaint );
            mPaint.setAlpha( 255 );
      }
}
