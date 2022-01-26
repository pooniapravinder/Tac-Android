package com.wookes.tac.request;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wookes.tac.util.e;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadPost {
    private final UploadPostListener uploadPostListener;

    public UploadPost(UploadPostListener uploadPostListener) {
        this.uploadPostListener = uploadPostListener;
    }

    public void uploadPost(String filePath, String url, String contType, long contLength, String contentMD5) {
        if(e.a()) uploadPostListener.onUploadFailed();
        RequestBody requestBody = RequestBody.create(MediaType.parse(""), new File(filePath));
        final CountingRequestBody.Listener progressListener = (bytesRead, contentLength) -> {
            if (bytesRead >= contentLength) {
                uploadPostListener.onUploadStart();
            } else {
                if (contentLength > 0) {
                    uploadPostListener.onUploadProgress(bytesRead, contentLength);
                }
            }
        };
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, progressListener);
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.addHeader("Content-Type", contType);
        requestBuilder.addHeader("Content-Length", String.valueOf(contLength));
        requestBuilder.addHeader("Content-MD5", contentMD5);
        requestBuilder.put(countingRequestBody);
        request = requestBuilder.build();
        OkHttpClient.Builder builder2 = new OkHttpClient.Builder();
        builder2.connectTimeout(300, TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder2.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                uploadPostListener.onUploadFailed();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String mResponse = Objects.requireNonNull(response.body()).string();
                if (TextUtils.isEmpty(mResponse)) {
                    uploadPostListener.onUploadSuccess(mResponse);
                } else {
                    uploadPostListener.onUploadFailed();
                }
            }
        });
    }

    public interface UploadPostListener {
        void onUploadStart();

        void onUploadProgress(long bytesRead, long contentLength);

        void onUploadFailed();

        void onUploadSuccess(String response);
    }

}