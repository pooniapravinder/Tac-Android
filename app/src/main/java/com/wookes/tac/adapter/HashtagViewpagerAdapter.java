package com.wookes.tac.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wookes.tac.ui.a.PostsData;
import com.wookes.tac.urlconfig.URLConfig;

import java.util.HashMap;

public class HashtagViewpagerAdapter extends FragmentStatePagerAdapter {

    private final int totalTabs;
    private final int bottomInset;
    private final long hashtagId;

    public HashtagViewpagerAdapter(FragmentManager fragmentManager, int totalTabs, int bottomInset, long hashtagId) {
        super(fragmentManager);
        this.totalTabs = totalTabs;
        this.bottomInset = bottomInset;
        this.hashtagId = hashtagId;
    }

    @Override
    public Fragment getItem(int position) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hashtag_id", String.valueOf(hashtagId));
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", map);
        bundle.putInt("bottomInset", bottomInset);
        switch (position) {
            case 0:
                fragment = PostsData.newInstance();
                bundle.putString("fragmentType", "videos");
                bundle.putString("url", URLConfig.RETRIEVE_TOP_HASHTAGS);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                fragment = PostsData.newInstance();
                bundle.putString("fragmentType", "videos");
                bundle.putString("url", URLConfig.RETRIEVE_RECENT_HASHTAGS);
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