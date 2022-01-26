package com.wookes.tac.model;

import android.graphics.drawable.Drawable;

public class ShareModel {
    private int id;
    private String title;
    private Drawable drawable;

    public ShareModel(int id, String title, Drawable drawable) {
        this.id = id;
        this.title = title;
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
