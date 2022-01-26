package com.wookes.tac.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wookes.tac.ui.e.FollowingFollowersData;
import com.wookes.tac.urlconfig.URLConfig;

import java.util.HashMap;

public class FollowingFollowersViewpagerAdapter extends FragmentStatePagerAdapter {

    private int totalTabs;
    private HashMap<String, String> map;
    private long userId;

    public FollowingFollowersViewpagerAdapter(FragmentManager fragmentManager, int totalTabs, long userId) {
        super(fragmentManager);
        this.totalTabs = totalTabs;
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new FollowingFollowersData(URLConfig.RETRIEVE_USER_FOLLOWING, userId, false);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                fragment = new FollowingFollowersData(URLConfig.RETRIEVE_USER_FOLLOWERS, userId, true);
                fragment.setArguments(bundle);
                return fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}