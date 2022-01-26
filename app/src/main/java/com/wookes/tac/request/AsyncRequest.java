package com.wookes.tac.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class AsyncRequest extends AsyncTask<String, String, String> {

    private ProgressDialog pdLoading;
    private final WeakReference<Context> contextRef;
    private final WeakReference<ResponseResult> mCallBack;
    private final WeakReference<String> mMessage;
    private final WebService webService;
    private final RequestData requestData;

    public AsyncRequest(Context context, ResponseResult callback, String message, RequestData requestData) {
        this.contextRef = new WeakReference<>(context);
        this.mCallBack = new WeakReference<>(callback);
        this.mMessage = new WeakReference<>(message);
        webService = new WebService(context);
        this.requestData = requestData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mMessage.get() != null) {
            pdLoading = new ProgressDialog(contextRef.get());
            pdLoading.setMessage(mMessage.get());
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return requestData.getRequestMethod().toUpperCase().equals("GET") ? webService.sendGet(requestData.getUrl()) : webService.sendPost(requestData.getUrl(), requestData.getParams());
    }

    @Override
    protected void onPostExecute(String result) {
        if (pdLoading != null) pdLoading.dismiss();
        final ResponseResult callBack = mCallBack.get();
        if (callBack == null) return;
        if (isJSONInvalid(result)) {
            callBack.onTaskFailed(contextRef.get());
        } else {
            callBack.onTaskDone(result);
        }
    }

    public boolean isJSONInvalid(String test) {
        if (TextUtils.isEmpty(test)) return true;
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return true;
            }
        }
        return false;
    }
}