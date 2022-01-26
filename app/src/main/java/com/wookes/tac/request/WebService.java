package com.wookes.tac.request;

import android.content.Context;
import android.text.TextUtils;

import com.wookes.tac.util.e;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebService {
    private final OkHttpClient client = new OkHttpClient();
    private static PersistentCookieStore msCookieManager;

    public WebService(Context context) {
        msCookieManager = new PersistentCookieStore(context);
    }

    public String sendGet(final String requestURL) {
        if (e.a()) return null;
        Request.Builder builder = new Request.Builder();
        builder.url(requestURL);
        if (msCookieManager.getCookies().size() > 0) {
            //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
            builder.addHeader("Cookie", TextUtils.join(";", msCookieManager.getCookies()));
        }
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                String body = response.body().string();
                List<String> cookiesHeader = response.headers().values("Set-Cookie");
                for (String cookie : cookiesHeader) {
                    try {
                        msCookieManager.add(new URI(requestURL), HttpCookie.parse(cookie).get(0));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendPost(final String requestURL, Map<String, String> urlParameters) {
        if (e.a()) return null;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : urlParameters.entrySet())
            builder.addEncoded(entry.getKey(), entry.getValue());
        RequestBody requestBody = builder.build();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(requestURL);
        if (msCookieManager.getCookies().size() > 0) {
            //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
            requestBuilder.addHeader("Cookie", TextUtils.join(";", msCookieManager.getCookies()));
        }
        requestBuilder.addHeader("Content-Type", "application-x-www-form-urlencoded");
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                String body = response.body().string();
                List<String> cookiesHeader = response.headers().values("Set-Cookie");
                for (String cookie : cookiesHeader) {
                    try {
                        msCookieManager.add(new URI(requestURL), HttpCookie.parse(cookie).get(0));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                return body;
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}