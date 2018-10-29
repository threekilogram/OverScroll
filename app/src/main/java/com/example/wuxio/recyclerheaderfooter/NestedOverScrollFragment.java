package com.example.wuxio.recyclerheaderfooter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import tech.threekilogram.overscroll.NestedOverScrollLayout;

/**
 * @author wuxio 2018-04-08:13:12
 */
public class NestedOverScrollFragment extends Fragment {

      protected View                   rootView;
      protected NestedOverScrollLayout mOverScrollLayout;
      private   NestedScrollView       mNested;
      private   LinearLayout           mContent;

      @SuppressWarnings("UnnecessaryLocalVariable")
      public static NestedOverScrollFragment newInstance ( ) {

            NestedOverScrollFragment fragment = new NestedOverScrollFragment();
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            rootView = inflater.inflate( R.layout.fragment_overscroll_nested, container, false );
            return rootView;
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
      }

      private void initView ( View rootView ) {

            mOverScrollLayout = rootView.findViewById( R.id.overScroll );
            mOverScrollLayout.setMaxOverScrollDistance( 400 );
            mNested = rootView.findViewById( R.id.nested );
            mContent = rootView.findViewById( R.id.content );

            final int itemCount = 20;

            for( int i = 0; i < itemCount; i++ ) {
                  mContent.addView( getItemView( i ), getParams() );
            }
      }

      private TextView getItemView ( int i ) {

            TextView textView = new TextView( getContext() );
            textView.setTextSize( TypedValue.COMPLEX_UNIT_SP, 24 );
            textView.setGravity( Gravity.CENTER );
            textView.setText( String.format( Locale.CHINA, "linear Item %d", i ) );
            return textView;
      }

      private LinearLayout.LayoutParams getParams ( ) {

            return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200
            );
      }
}
