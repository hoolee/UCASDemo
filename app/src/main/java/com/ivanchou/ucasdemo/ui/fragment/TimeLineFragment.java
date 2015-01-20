package com.ivanchou.ucasdemo.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ivanchou.ucasdemo.R;
import com.ivanchou.ucasdemo.ui.QuickReturnListView;
import com.ivanchou.ucasdemo.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanchou on 1/19/2015.
 */
public class TimeLineFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeLayout;
    private QuickReturnListView mListView;
    private List<String> list;// 测试数据


    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private int mState = STATE_ONSCREEN;
    private int mScrollY;
    private int mMinRawY = 0;

    private TranslateAnimation anim;
    private LinearLayout mQuickReturnView;
    private int mQuickReturnHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_maintimeline, container, false);
        mListView = (QuickReturnListView) view.findViewById(R.id.lv_maintimeline);
        mQuickReturnView = (LinearLayout) view.findViewById(R.id.footer);
        mListView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,
                getData()));

        mListView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mQuickReturnHeight = mQuickReturnView.getHeight();
                        mListView.computeScrollY();
                    }
                });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @SuppressLint("NewApi")
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                mScrollY = 0;
                int translationY = 0;

                if (mListView.scrollYIsComputed()) {
                    mScrollY = mListView.getComputedScrollY();
                }

                int rawY = mScrollY;

                switch (mState) {
                    case STATE_OFFSCREEN:
                        if (rawY >= mMinRawY) {
                            mMinRawY = rawY;
                        } else {
                            mState = STATE_RETURNING;
                        }
                        translationY = rawY;
                        break;

                    case STATE_ONSCREEN:
                        if (rawY > mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING:

                        translationY = (rawY - mMinRawY) + mQuickReturnHeight;

                        System.out.println(translationY);
                        if (translationY < 0) {
                            translationY = 0;
                            mMinRawY = rawY + mQuickReturnHeight;
                        }

                        if (rawY == 0) {
                            mState = STATE_ONSCREEN;
                            translationY = 0;
                        }

                        if (translationY > mQuickReturnHeight) {
                            mState = STATE_OFFSCREEN;
                            mMinRawY = rawY;
                        }
                        break;
                }

                /** this can be used if the build is below honeycomb **/
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                    anim = new TranslateAnimation(0, 0, translationY,
                            translationY);
                    anim.setFillAfter(true);
                    anim.setDuration(0);
                    mQuickReturnView.startAnimation(anim);
                } else {
                    mQuickReturnView.setTranslationY(translationY);
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });


        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    private void initViews(View view) {

    }

    /**
     * 填充假数据
     *
     * @return
     */
    private List<String> getData() {
        list.add("Hello");
        list.add("I am ivanchou");
        list.add("An Android Developer");
        list.add("Love Open Source");
        list.add("And this is fake data");
        list.add("For test listview");
        for (int i = 0; i < 10; i++) {
            list.add(i + "");
        }
        return list;
    }

    @Override
    public void onRefresh() {
        Toast.makeText(context, "Refresh start!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
                Toast.makeText(context, "Refresh stop!", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }
}
