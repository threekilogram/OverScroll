package com.example.wuxio.recyclerheaderfooter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected RecyclerView mRecycler;

    private static final String TAG = "MainActivity";
    protected HeaderFooterLayout mHeaderFooterLayout;
    private   MainAdapter        mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {

        mRecycler = findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        //mRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        //mRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new MainAdapter();
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(new MainOnScrollListener());

        mHeaderFooterLayout = (HeaderFooterLayout) findViewById(R.id.recyclerWrapper);
        LayoutInflater inflater = getLayoutInflater();
        TextView header = (TextView) inflater.inflate(R.layout.item_main_recycler, mHeaderFooterLayout,
                false);
        mHeaderFooterLayout.setHeader(header);
        TextView footer = (TextView) inflater.inflate(R.layout.item_main_recycler, mHeaderFooterLayout,
                false);
        mHeaderFooterLayout.setFooter(footer);
        mHeaderFooterLayout.setOverScrollListener(new HeaderFooterLayout.OnOverScrollListener() {
            @Override
            public void onScrollOverTop(View header, int scrollY) {

                ((TextView) header).setText(String.valueOf(scrollY));
                if (scrollY < -200) {
                    mHeaderFooterLayout.stopSpringBack();

                }
            }


            @Override
            public void onOverTopTouchUp(View header, int scrollY) {

                int dy = scrollY - -200;
                mHeaderFooterLayout.scrollBack(-dy);
            }


            @Override
            public void onScrollOverBottom(View footer, int scrollY) {

                ((TextView) footer).setText(String.valueOf(scrollY));
                mHeaderFooterLayout.stopSpringBack();
            }


            @Override
            public void onOverBottomTouchUp(View footer, int scrollY) {

                if (scrollY > 200) {
                    int dy = scrollY - 200;
                    mHeaderFooterLayout.scrollBack(-dy);
                }

            }
        });

        mHeaderFooterLayout.postDelayed(new Runnable() {
            @Override
            public void run() {

                List< Integer > list = mAdapter.getList();
                for (int i = 0; i < 50; i++) {
                    list.add(50 + i);
                }

                mAdapter.notifyDataSetChanged();
                Log.i(TAG, "run:" + "notifyDataSetChanged");

                mHeaderFooterLayout.reLayout();
            }
        }, 10000);

    }


    private class MainAdapter extends RecyclerView.Adapter< MainTextHolder > {

        private LayoutInflater mInflater;

        List< Integer > mList = new ArrayList<>();

        {
            for (int i = 0; i < 50; i++) {
                mList.add(i);
            }

        }

        public List< Integer > getList() {

            return mList;
        }


        @NonNull
        @Override
        public MainTextHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (mInflater == null) {
                mInflater = LayoutInflater.from(parent.getContext());
            }

            View view = mInflater.inflate(R.layout.item_main_recycler, parent, false);
            return new MainTextHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull MainTextHolder holder, int position) {

            holder.bind(mList.get(position));
        }


        @Override
        public int getItemCount() {

            return mList.size();
        }
    }

    private class MainTextHolder extends RecyclerView.ViewHolder {


        private final TextView mTextView;


        public MainTextHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView;
        }


        public void bind(int position) {

            String s = String.format(Locale.CHINA, "Item %d", position);
            mTextView.setText(s);
        }
    }

    private class MainOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            super.onScrolled(recyclerView, dx, dy);
        }
    }
}
