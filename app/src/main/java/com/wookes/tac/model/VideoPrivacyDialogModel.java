package com.wookes.tac.model;

import android.graphics.drawable.Drawable;

public class VideoPrivacyDialogModel {
    private String title;
    private String subtitle;
    private Drawable icon;

    public VideoPrivacyDialogModel(String title, String subtitle, Drawable icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
