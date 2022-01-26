package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class Media {
    @SerializedName("id")
    private long id;
    @SerializedName("thumbnail")
    private String thumbnail;

    public Media(long id, String thumbnail) {
        this.id = id;
        this.thumbnail = thumbnail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
