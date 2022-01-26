package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aliyun.svideo.recorder.util.RecordCommon;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wookes.tac.R;
import com.wookes.tac.util.BottomNavigation;
import com.wookes.tac.util.StatusBar;
import com.wookes.tac.util.ToastDisplay;
import com.wookes.tac.util.c;

public class LandingUi extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigation;
    private c c;
    private boolean doubleBack = false;
    private Home homeFragment;
    private Discover discoverFragment;
    private Notifications notificationsFragment;
    private Profile profileFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_holder);
        homeFragment = Home.newInstance();
        discoverFragment = Discover.newInstance();
        notificationsFragment = Notifications.newInstance();
        profileFragment = Profile.newInstance();
        RecordCommon.copyRace(this);
        // 版本升级
        //AutoUpgradeClient.checkUpgrade(this, AutoUpgradeClient.LITTLEVIDEO_JSON);
        Context context = this;
        c = new c(context);
        navigation = findViewById(R.id.navigation);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        navigation.setOnNavigationItemSelectedListener(this);
        if (getIntent() != null && getIntent().getBooleanExtra("hasNotifications", false)) {
            StatusBar.setStatusBarNavigationBarWhite(getWindow());
            BottomNavigation.bottomNavColor(false, navigation);
            loadFragment(notificationsFragment);
            currentFragment = notificationsFragment;
        } else {
            loadFragment(homeFragment);
            currentFragment = homeFragment;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                if (homeFragment.equals(currentFragment)) {

                } else {
                    StatusBar.setStatusBarNavigationBarBlack(getWindow());
                    BottomNavigation.bottomNavColor(true, navigation);
                    loadFragment(homeFragment);
                    currentFragment = homeFragment;
                }
                return true;
            case R.id.navigation_discover:
                if (discoverFragment.equals(currentFragment)) {

                } else {
                    StatusBar.setStatusBarNavigationBarWhite(getWindow());
                    BottomNavigation.bottomNavColor(false, navigation);
                    loadFragment(discoverFragment);
                    currentFragment = discoverFragment;
                }
                return true;
            case R.id.navigation_notifications:
                if (notificationsFragment.equals(currentFragment)) {

                } else {
                    if (!c.isLoggedIn()) {
                        startActivity(new Intent(LandingUi.this, Login.class));
                    } else {
                        StatusBar.setStatusBarNavigationBarWhite(getWindow());
                        BottomNavigation.bottomNavColor(false, navigation);
                        loadFragment(notificationsFragment);
                        currentFragment = notificationsFragment;
                    }
                }
                return true;
            case R.id.navigation_profile:
                if (profileFragment.equals(currentFragment)) {

                } else {
                    if (!c.isLoggedIn()) {
                        startActivity(new Intent(LandingUi.this, Login.class));
                    } else {
                        StatusBar.setStatusBarNavigationBarWhite(getWindow());
                        BottomNavigation.bottomNavColor(false, navigation);
                        loadFragment(profileFragment);
                        currentFragment = profileFragment;
                    }
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (doubleBack) {
                finish();
                return;
            }
            this.doubleBack = true;
            ToastDisplay.a(this, R.string.tap_again_to_exit, 0);
            new Handler().postDelayed(() -> doubleBack = false, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFragment instanceof Home) ((Home) currentFragment).resumeFragment();
    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment.isAdded()) {
            if (fragment instanceof Home) ((Home) fragment).resumeFragment();
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, backStateName);
        }
        if (currentFragment != null) {
            if (currentFragment instanceof Home) currentFragment.onPause();
            fragmentTransaction.hide(currentFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}