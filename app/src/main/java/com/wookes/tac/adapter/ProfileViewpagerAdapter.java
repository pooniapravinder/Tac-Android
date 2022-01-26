package com.wookes.tac.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wookes.tac.ui.a.PostsData;
import com.wookes.tac.urlconfig.URLConfig;

import java.util.HashMap;

public class ProfileViewpagerAdapter extends FragmentStatePagerAdapter {

    private final int totalTabs;
    private final int bottomInset;
    private final long userId;

    public ProfileViewpagerAdapter(FragmentManager fragmentManager, int totalTabs, int bottomInset, long userId) {
        super(fragmentManager);
        this.totalTabs = totalTabs;
        this.bottomInset = bottomInset;
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", String.valueOf(userId));
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", map);
        bundle.putInt("bottomInset", bottomInset);
        switch (position) {
            case 0:
                fragment = PostsData.newInstance();
                bundle.putString("fragmentType", "videos");
                bundle.putString("url", URLConfig.RETRIEVE_PROFILE_VIDEOS);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                fragment = PostsData.newInstance();
                bundle.putString("fragmentType", "likes");
                bundle.putString("url", URLConfig.RETRIEVE_PROFILE_LIKES);
                fragment.setArguments(bundle);
                return fragment;
            case 2:
                fragment = PostsData.newInstance();
                bundle.putString("fragmentType", "saved");
                bundle.putString("url", URLConfig.RETRIEVE_PROFILE_SAVED);
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