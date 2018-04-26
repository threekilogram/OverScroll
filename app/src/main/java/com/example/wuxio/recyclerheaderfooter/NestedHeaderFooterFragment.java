package com.example.wuxio.recyclerheaderfooter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
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

import com.example.overscroll.HeaderFooterLayout;

import java.util.Locale;

/**
 * @author wuxio 2018-04-08:13:12
 */
public class NestedHeaderFooterFragment extends Fragment {

    protected View               rootView;
    protected HeaderFooterLayout mHeaderFooter;


    @SuppressWarnings("UnnecessaryLocalVariable")
    public static NestedHeaderFooterFragment newInstance() {

        NestedHeaderFooterFragment fragment = new NestedHeaderFooterFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_nested_header_footer, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(final View rootView) {

        final NestedScrollView scrollView = rootView.findViewById(R.id.nestedScrollview);
        final LinearLayout content = rootView.findViewById(R.id.content);

        final int itemCount = 30;

        for (int i = 0; i < itemCount; i++) {
            content.addView(getItemView(i), getParams());
        }

        mHeaderFooter = (HeaderFooterLayout) rootView.findViewById(R.id.headerFooter);
        mHeaderFooter.setHeader(getItemView("Header", Color.parseColor("#ffe4c4")));
        mHeaderFooter.setFooter(getItemView("Header", Color.parseColor("#ffe4c4")));

        mHeaderFooter.setOverScrollListener(new HeaderFooterLayout.OnOverScrollListener() {
            @Override
            public void onScrollOverTop(View header, int scrollY) {

                ((TextView) header).setText(String.valueOf(scrollY));
            }


            @Override
            public void onOverTopTouchUp(View header, int scrollY) {

                ((TextView) header).setText(" refreshing ");
            }


            @Override
            public void onScrollOverBottom(View footer, int scrollY) {

                ((TextView) footer).setText(String.valueOf(scrollY));
            }


            @Override
            public void onOverBottomTouchUp(View footer, int scrollY) {

            }
        });
    }


    private TextView getItemView(int i) {

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setGravity(Gravity.CENTER);
        textView.setText(String.format(Locale.CHINA, "Item %d", i));
        return textView;
    }


    private TextView getItemView(String text, @ColorInt int color) {

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(color);
        textView.setText(text);
        return textView;
    }


    private LinearLayout.LayoutParams getParams() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 50;
        params.bottomMargin = 50;
        return params;
    }
}
