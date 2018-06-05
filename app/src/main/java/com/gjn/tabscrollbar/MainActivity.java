package com.gjn.tabscrollbar;

import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gjn.tabscrollbarlibrary.TabScrollBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.ViewPager);
        TabLayout tabLayout = findViewById(R.id.tl);


        List<TabScrollBar.BarTab> list = new ArrayList<>();

        TabScrollBar.BarTab barTab;
        Bundle bundle;

        for (int i = 0; i < 3; i++) {
            barTab = new TabScrollBar.BarTab();
            barTab.setTitle("标题"+i);
            barTab.setFragment(new TestFm());
            bundle = new Bundle();
            bundle.putString("title","标题"+i);
            barTab.setBundle(bundle);
            list.add(barTab);
        }
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.colorAccent));
        TabScrollBar bar = new TabScrollBar(this, viewPager, tabLayout, list);
        bar.create();
    }
}
