package com.gjn.tabscrollbarlibrary;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
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
    private int textNormal = -1;
    private int textSelect = -1;
    private int customViewId = -1;
    private OnCustomViewListener onCustomViewListener;

    public TabScrollBar(FragmentActivity activity, ViewPager viewPager,
                        TabLayout tabLayout, List<BarTab> barItems) {
        this.activity = activity;
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        this.fragmentManager = activity.getSupportFragmentManager();
        this.barItems = barItems == null ? new ArrayList<BarTab>() : barItems;
    }

    public TabScrollBar(Fragment fragment, ViewPager viewPager,
                        TabLayout tabLayout, List<BarTab> barItems) {
        this.activity = fragment.getActivity();
        this.viewPager = viewPager;
        this.tabLayout = tabLayout;
        this.fragmentManager = fragment.getChildFragmentManager();
        this.barItems = barItems == null ? new ArrayList<BarTab>() : barItems;
    }

    private LinearLayout getTabStrip() {
        return (LinearLayout) tabLayout.getChildAt(0);
    }

    private void setTabTextViewWidth() {
        try {
            LinearLayout mTabStrip = getTabStrip();
            int dp = dip2px(10);
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View tabView = mTabStrip.getChildAt(i);
                //不是设置padding 会缩成一团
                tabView.setPadding(0, 0, 0, 0);
                //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);
                TextView mTextView = (TextView) mTextViewField.get(tabView);
                //获取tabView的宽度
                int tabwidth = getViewWidth(tabView);
                //获取textview的宽度
                int textwidth = getViewWidth(mTextView);
                //根据不同模式设置
                if (tabLayout.getTabMode() == TabLayout.MODE_SCROLLABLE) {
                    //设置tabView的宽度和左右Margin(不设置Margin会整个连在一起)
                    setLayoutParams(tabView, textwidth, dp);
                } else {
                    //设置margin的宽度
                    int margin = (tabwidth - textwidth) / 2;
                    //设置tabView的宽度和左右Margin(不设置Margin会整个连在一起)
                    setLayoutParams(tabView, tabwidth, margin);
                }
                //重新绘制tabView
                tabView.invalidate();
            }
        } catch (Exception e) {
            Log.e(TAG, "AdaptationText error", e);
        }
    }

    private int dip2px(int dp) {
        float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int getViewWidth(View view) {
        int width = view.getWidth();
        if (width == 0) {
            view.measure(0, 0);
            width = view.getMeasuredWidth();
        }
        return width;
    }

    private void setLayoutParams(View view, int width, int margin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.leftMargin = margin;
        params.rightMargin = margin;
        view.setLayoutParams(params);
    }

    private TextView getTextView(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view == null) {
            tab.setCustomView(R.layout.tab_textview);
        }
        return tab.getCustomView().findViewById(R.id.tv_ttv);
    }

    private View getTabView(int i) {
        if (customViewId == -1) {
            TextView textView = (TextView) LayoutInflater.from(activity).inflate(R.layout.tab_textview, null);
            textView.setText(barItems.get(i).title);
            if (textSelect != -1 && textNormal != -1) {
                if (i == 0) {
                    textView.setTextAppearance(activity, textSelect);
                } else {
                    textView.setTextAppearance(activity, textNormal);
                }
            }
            return textView;
        } else {
            View view = LayoutInflater.from(activity).inflate(customViewId, null);
            if (onCustomViewListener != null) {
                onCustomViewListener.initView(view, barItems, i);
            }
            return view;
        }
    }

    public void create() {
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
                Log.i(TAG, barTab.getTitle() + "设置Bundle成功");
                barTab.getFragment().setArguments(barTab.getBundle());
            }
        }

        viewPager.setAdapter(new ScrollBarAdapter(fragmentManager));
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < barItems.size(); i++) {
            tabLayout.getTabAt(i).setCustomView(getTabView(i));
        }

        addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (onCustomViewListener != null) {
                    onCustomViewListener.onTabSelected(tab.getCustomView());
                } else if (textSelect != -1) {
                    TextView tv = getTextView(tab);
                    tv.setText(tab.getText());
                    tv.setTextAppearance(activity, textSelect);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (onCustomViewListener != null) {
                    onCustomViewListener.onTabUnselected(tab.getCustomView());
                } else if (textNormal != -1) {
                    TextView tv = getTextView(tab);
                    tv.setText(tab.getText());
                    tv.setTextAppearance(activity, textNormal);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        if (isAdaptationText) {
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    setTabTextViewWidth();
                }
            });
        }
    }

    public BarTab getItem(int i) {
        return barItems.get(i);
    }

    public TabScrollBar setTabText(@StyleRes int normal, @StyleRes int select) {
        textNormal = normal;
        textSelect = select;
        customViewId = -1;
        return this;
    }

    public TabScrollBar setCustomTabView(int viewId, OnCustomViewListener listener) {
        customViewId = viewId;
        onCustomViewListener = listener;
        textNormal = -1;
        textSelect = -1;
        return this;
    }

    public TabScrollBar setIsAdaptationTextWidth(boolean b) {
        isAdaptationText = b;
        return this;
    }

    public TabScrollBar setTabMode(@TabLayout.Mode int mode) {
        tabLayout.setTabMode(mode);
        return this;
    }

    public TabScrollBar setTabTextColors(int normalColor, int selectedColor) {
        tabLayout.setTabTextColors(normalColor, selectedColor);
        return this;
    }

    public TabScrollBar setSelectedTabIndicatorColor(@ColorInt int color) {
        tabLayout.setSelectedTabIndicatorColor(color);
        return this;
    }

    public TabScrollBar setSelectedTabIndicatorHeight(int height) {
        tabLayout.setSelectedTabIndicatorHeight(height);
        return this;
    }

    public TabScrollBar addOnTabSelectedListener(TabLayout.OnTabSelectedListener listener) {
        if (listener != null) {
            tabLayout.addOnTabSelectedListener(listener);
        }
        return this;
    }

    public static class BarTab {
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

    public interface OnCustomViewListener {
        void initView(View view, List<BarTab> barItems, int i);

        void onTabSelected(View customView);

        void onTabUnselected(View customView);
    }
}
