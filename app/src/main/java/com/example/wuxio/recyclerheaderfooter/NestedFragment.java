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

/**
 * @author wuxio 2018-04-08:13:12
 */
public class NestedFragment extends Fragment {

    protected View rootView;
    private static final String TAG = "NestedFragment";


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_nested, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    @SuppressWarnings("UnnecessaryLocalVariable")
    public static NestedFragment newInstance() {

        NestedFragment fragment = new NestedFragment();
        return fragment;
    }


    private void initView(final View rootView) {

        final NestedScrollView scrollView = rootView.findViewById(R.id.nestedScrollview);
        final LinearLayout content = rootView.findViewById(R.id.content);

        final int itemCount = 50;

        for (int i = 0; i < itemCount; i++) {
            content.addView(getItemView(i), getParams());
        }
    }


    private TextView getItemView(int i) {

        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        textView.setGravity(Gravity.CENTER);
        textView.setText(String.format(Locale.CHINA, "linear Item %d", i));
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
