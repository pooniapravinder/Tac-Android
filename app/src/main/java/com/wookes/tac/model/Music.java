package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Music implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("url")
    private String url;
    @SerializedName("album")
    private String album;
    @SerializedName("artist")
    private String artist;
    @SerializedName("coverImage")
    private String coverImage;
    @SerializedName("sourceType")
    private int sourceType;
    @SerializedName("username")
    private String username;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("profilePic")
    private String profilePic;

    public Music(long id, String url, String album, String artist, String coverImage, int sourceType, String username, String fullName, String profilePic) {
        this.id = id;
        this.url = url;
        this.album = album;
        this.artist = artist;
        this.coverImage = coverImage;
        this.sourceType = sourceType;
        this.username = username;
        this.fullName = fullName;
        this.profilePic = profilePic;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
