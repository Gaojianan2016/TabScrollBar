# TabScrollBar
自定义TabLayoutBar

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.Gaojianan2016:TabScrollBar:1.0.2'
}
```


```
public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.ViewPager);
        TabLayout tabLayout = findViewById(R.id.tl);

        context = this;

        List<TabScrollBar.BarTab> list = new ArrayList<>();

        TabScrollBar.BarTab barTab;
        Bundle bundle;

        for (int i = 0; i < 3; i++) {
            barTab = new TabScrollBar.BarTab();
            barTab.setTitle("标题"+i);
            barTab.setFragment(new TestFm());
            if (i != 2) {
                bundle = new Bundle();
                bundle.putString("title","标题"+i);
                barTab.setBundle(bundle);
            }
            list.add(barTab);
        }
        TabScrollBar bar = new TabScrollBar(this, viewPager, tabLayout, list);

//        bar.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.colorPrimary))
//                .setTabMode(TabLayout.MODE_SCROLLABLE)
//                .setIsAdaptationTextWidth(true)
//                .setTabText(R.style.TabTextNormal, R.style.TabTextSelect)
//                .create();


        bar.setCustomTabView(R.layout.item_tabview, new TabScrollBar.OnCustomViewListener() {
            @Override
            public void initView(View view, List<TabScrollBar.BarTab> barItems, int i) {
                TextView tv = view.findViewById(R.id.tv_it);
                View line = view.findViewById(R.id.line_it);
                tv.setText(barItems.get(i).getTitle());
                line.setBackgroundColor(Color.RED);
                if (i == 0) {
                    tv.setTextAppearance(context, R.style.TabTextSelect);
                    line.setVisibility(View.VISIBLE);
                }else {
                    tv.setTextAppearance(context, R.style.TabTextNormal);
                    line.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabSelected(View customView) {
                TextView tv = customView.findViewById(R.id.tv_it);
                View line = customView.findViewById(R.id.line_it);
                tv.setTextAppearance(context, R.style.TabTextSelect);
                line.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(View customView) {
                TextView tv = customView.findViewById(R.id.tv_it);
                View line = customView.findViewById(R.id.line_it);
                tv.setTextAppearance(context, R.style.TabTextNormal);
                line.setVisibility(View.GONE);
            }
        }).setTabMode(TabLayout.MODE_SCROLLABLE).setSelectedTabIndicatorHeight(0).create();
    }
}
```
