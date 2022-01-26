package com.wookes.tac.interfaces;

public interface OnUploadCompleteListener {

    void onSuccess(String response);

    void onFailure(String msg);
}
