package com.example.wuxio.recyclerheaderfooter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wuxio.recyclerheaderfooter.drawable.BiliBiliDrawable;
import tech.threekilogram.overscroll.NestedRefreshLayout;

/**
 * @author Liujin 2018-10-28:20:38
 */
public class NestedRefreshFragment extends Fragment {

      private static final String TAG = NestedRefreshFragment.class.getSimpleName();

      private TextView            mToolText;
      private AppBarLayout        mAppbar;
      private RecyclerView        mRecycler;
      private NestedRefreshLayout mRefresh;

      public static NestedRefreshFragment newInstance ( ) {

            Bundle args = new Bundle();

            NestedRefreshFragment fragment = new NestedRefreshFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_nested_refresh, container, false );
      }

      @Override
      public void onViewCreated ( @NonNull View view, @Nullable Bundle savedInstanceState ) {

            super.onViewCreated( view, savedInstanceState );
            initView( view );
      }

      private void initView ( @NonNull final View itemView ) {

            mToolText = itemView.findViewById( R.id.toolText );
            mAppbar = itemView.findViewById( R.id.appbar );
            mRecycler = itemView.findViewById( R.id.recycler );
            mRefresh = itemView.findViewById( R.id.refresh );

            mRecycler.setAdapter( new Adapter() );
            mRecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );

            mRefresh.setRefreshAdapter( new RefreshAdapter() );
      }

      private class Adapter extends RecyclerView.Adapter<Holder> {

            @NonNull
            @Override
            public Holder onCreateViewHolder ( @NonNull ViewGroup parent, int viewType ) {

                  View view = getLayoutInflater()
                      .inflate( R.layout.item_main_recycler, parent, false );
                  return new Holder( view );
            }

            @Override
            public void onBindViewHolder (
                @NonNull Holder holder, int position ) {

                  holder.bind( position );
            }

            @Override
            public int getItemCount ( ) {

                  return 15;
            }
      }

      private class Holder extends ViewHolder {

            Holder ( View itemView ) {

                  super( itemView );
            }

            void bind ( int position ) {

                  ( (TextView) itemView ).setText( String.valueOf( position ) );
            }
      }

      private class RefreshAdapter implements NestedRefreshLayout.RefreshAdapter {

            private TextView         mTextView;
            private ImageView        mImageView;
            private BiliBiliDrawable mDrawable;

            @Override
            public View getRefreshView ( Context context ) {

                  LayoutInflater from = LayoutInflater.from( context );
                  View view = from.inflate( R.layout.header, null );
                  view.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, 200 ) );
                  initView( view );
                  return view;
            }

            private void initView ( @NonNull final View itemView ) {

                  mTextView = itemView.findViewById( R.id.textView );
                  mImageView = itemView.findViewById( R.id.imageView );

                  mDrawable = new BiliBiliDrawable();
                  mImageView.setImageDrawable( mDrawable );
            }

            @Override
            public void onMove ( int orientation, int scrollY, int maxScrollY ) {

                  if( scrollY > -180 ) {

                        mTextView.setText( "返回" );
                  } else {
                        mTextView.setText( "刷新" );
                  }

                  float v = scrollY * 1f / maxScrollY;
                  float abs = Math.abs( v );
                  mDrawable.setDrawProgress( abs );
            }

            @Override
            public void release ( int scrollY, int maxScrollY ) {

                  if( scrollY > -180 ) {
                        mRefresh.startScroll( -scrollY );
                  } else {
                        mRefresh.startScroll( -200 - scrollY );
                  }
            }

            @Override
            public void onScrollBack ( int scrollY ) {

            }
      }
}
