package com.example.wuxio.recyclerheaderfooter;

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
import android.widget.TextView;

/**
 * @author Liujin 2018-10-28:20:38
 */
public class CoolFragment extends Fragment {

      private TextView     mToolText;
      private AppBarLayout mAppbar;
      private RecyclerView mRecycler;

      public static CoolFragment newInstance ( ) {

            Bundle args = new Bundle();

            CoolFragment fragment = new CoolFragment();
            fragment.setArguments( args );
            return fragment;
      }

      @Nullable
      @Override
      public View onCreateView (
          @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState ) {

            return inflater.inflate( R.layout.fragment_coor, container, false );
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
            mRecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );
            mRecycler.setAdapter( new Adapter() );
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
}
