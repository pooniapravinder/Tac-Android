package com.wookes.tac.bcd;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.FollowHolderAdapter;
import com.wookes.tac.cx.CenterZoomLayoutManager;
import com.wookes.tac.model.NewUserFollowModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.ToastDisplay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class FollowHolder extends RecyclerView.ViewHolder implements ResponseResult {
    private Context context;
    private final RecyclerView recyclerView;
    private final ArrayList<NewUserFollowModel> newUserFollowModels = new ArrayList<>();

    public FollowHolder(@NonNull View itemView) {
        super(itemView);
        recyclerView = itemView.findViewById(R.id.recycler_view);
    }

    public void onBind(Context context) {
        this.context = context;
        recyclerView.setLayoutManager(new CenterZoomLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        (new PagerSnapHelper()).attachToRecyclerView(recyclerView);
        new AsyncRequest(context, FollowHolder.this, null, new RequestData(URLConfig.RETRIEVE_TOP_USERS, new HashMap<>(), "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTaskDone(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            if (!jsonObject.has("success")) {
                ToastDisplay.a(context, R.string.something_wrong, 0);
                return;
            }
            Type type = new TypeToken<ArrayList<NewUserFollowModel>>() {
            }.getType();
            newUserFollowModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
            FollowHolderAdapter followHolderAdapter = new FollowHolderAdapter(context, newUserFollowModels);
            recyclerView.setAdapter(followHolderAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}