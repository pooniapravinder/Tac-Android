package com.wookes.tac.interfaces;

public interface g {
    void onDownloadProgress(long downloaded, long total);

    void onDownloadStart();

    void onDownloadSuccess(String path);

    void onDownloadFailed();
}
