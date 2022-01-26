package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class MentionTagModel {

    @SerializedName("image")
    private String image;
    @SerializedName("titleOne")
    private String titleOne;
    @SerializedName("titleTwo")
    private String titleTwo;
    @SerializedName("titleThree")
    private long titleThree;
    @SerializedName("hashtag")
    private boolean hashtag;

    public MentionTagModel(String image, String titleOne, String titleTwo, long titleThree, boolean hashtag) {
        this.image = image;
        this.titleOne = titleOne;
        this.titleTwo = titleTwo;
        this.titleThree = titleThree;
        this.hashtag = hashtag;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        ;
    }

    public String getTitleOne() {
        return titleOne;
    }

    public void setTitleOne(String titleOne) {
        this.titleOne = titleOne;
    }

    public String getTitleTwo() {
        return titleTwo;
    }

    public void setTitleTwo(String titleTwo) {
        this.titleTwo = titleTwo;
    }

    public long getTitleThree() {
        return titleThree;
    }

    public void setTitleThree(long titleThree) {
        this.titleThree = titleThree;
    }

    public boolean isHashtag() {
        return hashtag;
    }

    public void setHashtag(boolean hashtag) {
        this.hashtag = hashtag;
    }
}
