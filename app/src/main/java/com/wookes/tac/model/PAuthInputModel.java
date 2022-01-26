package com.wookes.tac.model;

import androidx.annotation.Keep;

public class PAuthInputModel {
    @Keep
    private int id;
    @Keep
    private String contentType;
    @Keep
    private long contentLength;
    @Keep
    private String contentMD5;

    public PAuthInputModel(int id, String contentType, long contentLength, String contentMD5) {
        this.id = id;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.contentMD5 = contentMD5;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentMD5() {
        return contentMD5;
    }

    public void setContentMD5(String contentMD5) {
        this.contentMD5 = contentMD5;
    }
}
