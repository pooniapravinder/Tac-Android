package com.wookes.tac.ui.f;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wookes.tac.R;
import com.wookes.tac.urlconfig.URLConfig;

public class FollowingHome extends BaseHome {

    public static FollowingHome newInstance() {
        return new FollowingHome();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = URLConfig.RETRIEVE_HOME_DATA_FOLLOWING;
        return inflater.inflate(R.layout.activity_layout_home_fragment, container, false);
    }
}