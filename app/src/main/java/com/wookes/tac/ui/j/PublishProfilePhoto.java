package com.wookes.tac.ui.j;

import android.content.Context;

import com.google.gson.Gson;
import com.wookes.tac.R;
import com.wookes.tac.model.PAuthInputModel;
import com.wookes.tac.request.PAuth;
import com.wookes.tac.request.UploadPost;
import com.wookes.tac.ui.h.PublishUtil;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.AssD;
import com.wookes.tac.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class PublishProfilePhoto {
    private final MyProfilePhotoListener profilePhotoListener;
    private final UploadPost uploadPost;
    private final Context context;
    private final PAuth pAuth;
    private String imageUri;
    private String authKey;
    private String auth;

    public PublishProfilePhoto(Context context, MyProfilePhotoListener profilePhotoListener) {
        this.profilePhotoListener = profilePhotoListener;
        pAuth = new PAuth(pAuthListener, context);
        this.uploadPost = new UploadPost(mUploadCallback);
        this.context = context;
    }

    public void startUpload(String imageUri) {
        this.imageUri = imageUri;
        getAuth();
    }

    public void getAuth() {
        File profilePhoto = new File(imageUri);
        ArrayList<PAuthInputModel> pAuthInputModels = new ArrayList<>();
        pAuthInputModels.add(new PAuthInputModel(101, "image/jpg", profilePhoto.length(), MD5.getMD5(profilePhoto)));
        pAuth.getAuth(new Gson().toJson(pAuthInputModels), URLConfig.UPLOAD_PROFILE_IMAGE_AUTH);
    }

    public void startUploading() {
        File profilePhoto = new File(imageUri);
        performUpload(imageUri, PublishUtil.parseUrl(101, authKey, auth), "image/jpg", profilePhoto.length(), MD5.getMD5(profilePhoto));
    }

    public void performUpload(String filePath, String url, String contentType, long contentLength, String contentMD5) {
        uploadPost.uploadPost(filePath, url, contentType, contentLength, contentMD5);
    }

    private final PAuth.PAuthListener pAuthListener = new PAuth.PAuthListener() {
        @Override
        public void onAuthResponse(String response, String key) {
            if (isAuthorizationSuccess(key, response)) {
                authKey = key;
                auth = response;
                startUploading();
            } else {
                if (profilePhotoListener != null) {
                    profilePhotoListener.onUploadFailed(context.getResources().getString(R.string.authorization_failed));
                }
            }
        }

        @Override
        public void onAuthFailed() {
            if (profilePhotoListener != null) {
                profilePhotoListener.onUploadFailed(context.getResources().getString(R.string.authorization_failed));
            }
        }
    };
    private final UploadPost.UploadPostListener mUploadCallback = new UploadPost.UploadPostListener() {
        @Override
        public void onUploadStart() {
            if (profilePhotoListener != null) {
                profilePhotoListener.onUploadStart();
            }
        }

        @Override
        public void onUploadProgress(long bytesRead, long contentLength) {
            if (profilePhotoListener != null) {
                final int progress = (int) (((double) bytesRead) * 100);
                profilePhotoListener.onUploadProgress(progress);
            }
        }

        @Override
        public void onUploadFailed() {
            if (profilePhotoListener != null) {
                profilePhotoListener.onUploadFailed(context.getResources().getString(R.string.alivc_little_main_net_error));
            }
        }

        @Override
        public void onUploadSuccess(String response) {
            if (profilePhotoListener != null) {
                profilePhotoListener.onUploadSuccess(response, authKey, auth);
            }
        }
    };

    public interface MyProfilePhotoListener {

        void onUploadStart();

        void onUploadProgress(int progress);

        void onUploadFailed(String error);

        void onUploadSuccess(String response, String authKey, String auth);
    }


    private boolean isAuthorizationSuccess(String key, String data) {
        try {
            JSONObject jsonObject = new JSONObject(AssD.a(key, data));
            if (jsonObject.has("success")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
