package com.wookes.tac.model;

import com.google.gson.annotations.SerializedName;

public class PAuthOutputModel {
    @SerializedName("id")
    private int id;
    @SerializedName("auth")
    private String auth;
    @SerializedName("key")
    private String key;

    public PAuthOutputModel(int id, String auth, String key) {
        this.id = id;
        this.auth = auth;
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
