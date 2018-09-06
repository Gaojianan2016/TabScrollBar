package com.gjn.tabscrollbarlibrary;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjn on 2018/6/5.
 */

public class TabScrollBar {
    private static final String TAG = "TabScrollBar";
    private Activity activity;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentManager fragmentManager;
    private List<BarTab> barItems;

    private boolean isAdaptationText = false;

    public TabScrollBar(FragmentActivity activity, ViewPager viewPager,
                        TabLayout tabLayout, List<BarTab> barItems) {
        this.activity = activity;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        this.fragmentManager = activity.getSupportFragmentManager();
        this.barItems = barItems == null ? new ArrayList<BarTab>():barItems;
    }

    public TabScrollBar(Fragment fragment, ViewPager viewPager,
                        TabLayout tabLayout, List<BarTab> barItems) {
        this.activity = fragment.getActivity();
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        this.fragmentManager = fragment.getChildFragmentManager();
        this.barItems = barItems == null ? new ArrayList<BarTab>():barItems;
    }

    public void create(){
        if (viewPager == null) {
            Log.e(TAG, "viewPager is null.");
            return;
        }
        if (barItems.size() == 0) {
            Log.e(TAG, "barItems is null.");
            return;
        }
        //设置bundle
        for (BarTab barTab : barItems) {
            if (barTab.getBundle() != null) {
                barTab.getFragment().setArguments(barTab.getBundle());
            }
        }

        viewPager.setAdapter(new ScrollBarAdapter(fragmentManager));
        tabLayout.setupWithViewPager(viewPager);

        if (isAdaptationText) {
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    setTabTextViewWidth(tabLayout);
                }
            });
        }
    }

    public BarTab getItem(int i){
        return barItems.get(i);
    }

    public TabScrollBar IsAdaptationTextWidth(boolean b){
        isAdaptationText = b;
        return this;
    }

    private void setTabTextViewWidth(TabLayout tabLayout) {
        try {
            LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
            //dip2px
            float scale = activity.getResources().getDisplayMetrics().density;
            int dp = (int) (10 * scale + 0.5f);
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View tabView = mTabStrip.getChildAt(i);
                //不是设置padding 会缩成一团
                tabView.setPadding(0, 0, 0, 0);
                //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);
                TextView mTextView = (TextView) mTextViewField.get(tabView);
                //获取tabView的宽度
                int tabwidth = tabView.getWidth();
                if (tabwidth == 0) {
                    tabView.measure(0, 0);
                    tabwidth = tabView.getMeasuredWidth();
                }
                //获取textview的宽度
                int textwidth = mTextView.getWidth();
                if (textwidth == 0) {
                    mTextView.measure(0, 0);
                    textwidth = mTextView.getMeasuredWidth();
                }
                //根据不同模式设置
                if (tabLayout.getTabMode() == TabLayout.MODE_SCROLLABLE) {
                    //设置tabView的宽度和左右Margin(不设置Margin会整个连在一起)
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = textwidth;
                    params.leftMargin = dp;
                    params.rightMargin = dp;
                    tabView.setLayoutParams(params);
                }else {
                    //设置margin的宽度
                    int margin = (tabwidth - textwidth) / 2;
                    //设置tabView的宽度和左右Margin(不设置Margin会整个连在一起)
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = tabwidth;
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                    tabView.setLayoutParams(params);
                }
                //重新绘制tabView
                tabView.invalidate();
            }
        }catch (Exception e){
            Log.e(TAG, "AdaptationText error", e);
        }
    }

    public static class BarTab{
        private String title;
        private Fragment fragment;
        private Bundle bundle;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        public Bundle getBundle() {
            return bundle;
        }

        public void setBundle(Bundle bundle) {
            this.bundle = bundle;
        }
    }

    private class ScrollBarAdapter extends FragmentPagerAdapter {

        ScrollBarAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return barItems.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return barItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return barItems.get(position).getTitle();
        }
    }
}
