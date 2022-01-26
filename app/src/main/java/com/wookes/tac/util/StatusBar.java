package com.wookes.tac.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import com.wookes.tac.R;
import com.wookes.tac.interfaces.Inset;

public class StatusBar {
    public static void setStatusBarTransparent(Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static void setStatusBarNavigationBarTransparent(Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    public static void setStatusBarNavigationBarBlack(Window window) {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);
    }

    public static void windowTransparentWhite(Context context, Window window, boolean navigationTransparentWhite) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
        window.setStatusBarColor(context.getResources().getColor(R.color.colorTransparentWhite));
        if (navigationTransparentWhite)
            window.setNavigationBarColor(context.getResources().getColor(R.color.white_transparent_05));
    }

    public static void setStatusBarNavigationBarWhite(Window window) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.setStatusBarColor(Color.WHITE);
        window.setNavigationBarColor(Color.WHITE);
    }

    public static void getInset(View view, Inset inset) {
        view.setOnApplyWindowInsetsListener((view1, windowInsets) -> {
            inset.inset(windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetBottom());
            return windowInsets;
        });
    }


    public static void a(Context context, Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
        window.setStatusBarColor(context.getResources().getColor(R.color.colorTransparentWhite));
        window.setNavigationBarColor(Color.TRANSPARENT);
    }
}
