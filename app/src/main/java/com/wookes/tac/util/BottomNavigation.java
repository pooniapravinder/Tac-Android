package com.wookes.tac.util;

import android.content.res.ColorStateList;
import android.graphics.Color;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigation {

    public static void bottomNavColor(boolean isBlack, BottomNavigationView bottomNavigationView) {
        ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{android.R.attr.state_enabled}}, isBlack ? new int[]{Color.WHITE} : new int[]{Color.BLACK});
        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);
        bottomNavigationView.setBackgroundColor(isBlack ? Color.BLACK : Color.WHITE);
    }
}
