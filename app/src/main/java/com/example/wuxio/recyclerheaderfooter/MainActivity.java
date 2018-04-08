package com.example.wuxio.recyclerheaderfooter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected RecyclerView mRecycler;

    private static final String TAG = "MainActivity";
    protected HeaderFooterLayout mHeaderFooterLayout;


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

        mRecycler.setAdapter(new MainAdapter());
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
            }


            @Override
            public void onScrollOverBottom(View footer, int scrollY) {

                ((TextView) footer).setText(String.valueOf(scrollY));
            }
        });

    }


    private class MainAdapter extends RecyclerView.Adapter< MainTextHolder > {

        private LayoutInflater mInflater;


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

            holder.bind(position);
        }


        @Override
        public int getItemCount() {

            return 50;
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
            //            int scrollExtent = recyclerView.computeVerticalScrollExtent();
            //            Log.i(TAG, "scrollExtent:" + scrollExtent);
            //
            //            int scrollOffset = recyclerView.computeVerticalScrollOffset();
            //            Log.i(TAG, "scrollOffset:" + scrollOffset);
            //
            //            int scrollRange = recyclerView.computeVerticalScrollRange();
            //            Log.i(TAG, "scrollRange:" + scrollRange);
        }
    }
}
