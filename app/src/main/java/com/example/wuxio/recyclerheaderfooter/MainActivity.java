package com.example.wuxio.recyclerheaderfooter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    protected ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();

    }


    private void initView() {

        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
    }


    private class MainPagerAdapter extends FragmentPagerAdapter {

        Fragment[] mFragments = {
                ShowFragment.newInstance(),
                HeaderFooterFragment.newInstance()
        };


        public MainPagerAdapter(FragmentManager fm) {

            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            return mFragments[position];
        }


        @Override
        public int getCount() {

            return mFragments.length;
        }
    }

}
