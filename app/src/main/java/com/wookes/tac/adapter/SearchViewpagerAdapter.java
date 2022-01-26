package com.wookes.tac.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wookes.tac.ui.c.SearchResultAccounts;
import com.wookes.tac.ui.c.SearchResultHashtag;
import com.wookes.tac.urlconfig.URLConfig;

public class SearchViewpagerAdapter extends FragmentStatePagerAdapter {

    private final int totalTabs;
    private final int bottomInset;

    public SearchViewpagerAdapter(FragmentManager fragmentManager, int totalTabs, int bottomInset) {
        super(fragmentManager);
        this.totalTabs = totalTabs;
        this.bottomInset = bottomInset;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("bottomInset", bottomInset);
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new SearchResultAccounts(URLConfig.SEARCH_DATA + "?type=popular");
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                fragment = new SearchResultAccounts(URLConfig.SEARCH_DATA + "?type=accounts");
                fragment.setArguments(bundle);
                return fragment;
            case 2:
                fragment = new SearchResultHashtag(URLConfig.SEARCH_DATA + "?type=hashtags");
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