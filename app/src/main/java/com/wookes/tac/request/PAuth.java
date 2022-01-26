package com.wookes.tac.request;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wookes.tac.util.AsD;
import com.wookes.tac.util.e;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PAuth {

    private static PersistentCookieStore msCookieManager;
    private final PAuthListener pAuthListener;

    public PAuth(PAuthListener pAuthListener, Context context) {
        this.pAuthListener = pAuthListener;
        msCookieManager = new PersistentCookieStore(context);
    }

    public void getAuth(String reqBody, String url) {
        if (e.a()) pAuthListener.onAuthFailed();
        Map<String, String> map = AsD.a(reqBody);
        String text = "";
        String auth = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("text")) {
                text = entry.getValue();
            }
            if (entry.getKey().equals("key")) {
                auth = entry.getValue();
            }
        }
        RequestBody requestBody = RequestBody.create( MediaType.parse(""), encode(text));
        Request.Builder requestBuilder = new Request.Builder();
        if (msCookieManager.getCookies().size() > 0) {
            //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
            requestBuilder.addHeader("Cookie", TextUtils.join(";", msCookieManager.getCookies()));
        }
        requestBuilder.url(url);
        requestBuilder.addHeader("Content-Type", "text/plain");
        requestBuilder.addHeader("Authorization", encode(auth));
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();
        OkHttpClient okHttpClient;
        OkHttpClient.Builder builder2 = new OkHttpClient.Builder();
        builder2.connectTimeout(300, TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS);
        okHttpClient = builder2.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                pAuthListener.onAuthFailed();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String mResponse = Objects.requireNonNull(response.body()).string();
                pAuthListener.onAuthResponse(decode(mResponse), decode(response.header("Authorization")));
            }
        });
    }

    private String decode(String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public interface PAuthListener {
        void onAuthResponse(String response, String key);

        void onAuthFailed();
    }
}