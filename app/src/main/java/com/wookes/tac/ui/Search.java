package com.wookes.tac.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.wookes.tac.R;
import com.wookes.tac.adapter.SearchViewpagerAdapter;
import com.wookes.tac.util.StatusBar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class Search extends AppCompatActivity {
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int topInset;
    private int bottomInset;
    private Toolbar toolbar;
    private EditText searchQuery;
    private boolean isAdded = false;

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.windowTransparentWhite(this, getWindow(), true);
        setContentView(R.layout.activity_layout_search);
        initView();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        StatusBar.getInset(appBarLayout, (topInset1, bottomInset1) -> {
            if(isAdded) return;
            topInset = topInset1;
            bottomInset = bottomInset1;
            appBarLayout.setPadding(0, topInset, 0, 0);
            addTabs();
            isAdded = true;
        });
    }

    private void initView() {
        appBarLayout = findViewById(R.id.appBar);
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        searchQuery = findViewById(R.id.search_query);
    }

    private void addTabs(){
        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("Accounts"));
        tabLayout.addTab(tabLayout.newTab().setText("Hashtags"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(new SearchViewpagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), bottomInset));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        searchQuery.setHint(R.string.search_popular);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    searchQuery.setHint(R.string.search_popular);
                } else if (tab.getPosition() == 1) {
                    searchQuery.setHint(R.string.search_accounts);
                } else {
                    searchQuery.setHint(R.string.search_hashtag);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
