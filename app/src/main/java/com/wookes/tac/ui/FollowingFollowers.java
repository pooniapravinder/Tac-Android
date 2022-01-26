package com.wookes.tac.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.wookes.tac.R;
import com.wookes.tac.adapter.FollowingFollowersViewpagerAdapter;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.StatusBar;
import com.google.android.material.tabs.TabLayout;

public class FollowingFollowers extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private int currentTab;
    private long followingTotal;
    private long followersTotal;
    private long userId;
    private TextView username;

    @Override
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        StatusBar.setStatusBarNavigationBarWhite(getWindow());
        setContentView(R.layout.activity_layout_following_followers);
        initView();
        currentTab = getIntent().getIntExtra("tab", 0);
        followingTotal = getIntent().getLongExtra("followingTotal", 0);
        followersTotal = getIntent().getLongExtra("followersTotal", 0);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        username.setText(getString(R.string.profile_username, getIntent().getStringExtra("username")));
        userId = getIntent().getLongExtra("userId", 0);
        addTabs();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.username);
    }

    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.following_tab, CustomCounter.format(followingTotal))));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.followers_tab, CustomCounter.format(followersTotal))));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(new FollowingFollowersViewpagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), userId == 0 ? new SelfUser(this).getSelfUser().getUserId() : userId));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(currentTab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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
