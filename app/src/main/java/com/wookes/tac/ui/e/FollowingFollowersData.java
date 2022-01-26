package com.wookes.tac.ui.e;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.FollowingFollowersDataAdapter;
import com.wookes.tac.model.FollowingFollowersModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class FollowingFollowersData extends Fragment implements ResponseResult {
    private String url;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<FollowingFollowersModel> followingFollowersModels = new ArrayList<>();
    private FollowingFollowersDataAdapter followingFollowersDataAdapter;
    boolean isLoading = true, hasMore = true;
    private View rootView;
    private HashMap<String, String> map = new HashMap<>();
    private long userId;
    private boolean areFollowers;

    public FollowingFollowersData(String url, long userId, boolean areFollowers) {
        this.url = url;
        this.userId = userId;
        this.areFollowers = areFollowers;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_profile_data, container, false);
        initView(rootView);
        followingFollowersModels.add(null);
        followingFollowersDataAdapter = new FollowingFollowersDataAdapter(context, followingFollowersModels, areFollowers, userId);
        recyclerView.setAdapter(followingFollowersDataAdapter);
        initScrollListener();
        map.put("user", String.valueOf(userId));
        new AsyncRequest(context, FollowingFollowersData.this, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return rootView;
    }

    private void initView(View view) {
        context = getActivity();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onTaskDone(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Type type = new TypeToken<ArrayList<FollowingFollowersModel>>() {
            }.getType();
            followingFollowersModels.remove(0);
            followingFollowersModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
            followingFollowersDataAdapter.notifyDataSetChanged();
            isLoading = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && hasMore) {
                    if (linearLayoutManager != null && followingFollowersModels.size() > 5 && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= followingFollowersModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadData() {
        recyclerView.post(() -> {
            map.put("pagination", String.valueOf(followingFollowersModels.size()));
            int lastData = followingFollowersModels.size() - 1;
            followingFollowersModels.add(null);
            lastData++;
            int finalLastData1 = lastData;
            followingFollowersDataAdapter.notifyItemInserted(finalLastData1);
            int finalLastData = lastData;
            new AsyncRequest(context, output -> {
                isLoading = false;
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    Type type = new TypeToken<ArrayList<FollowingFollowersModel>>() {
                    }.getType();
                    followingFollowersModels.remove(finalLastData);
                    followingFollowersDataAdapter.notifyItemRemoved(followingFollowersDataAdapter.getItemCount());
                    int prevSize = followingFollowersDataAdapter.getItemCount();
                    ArrayList<FollowingFollowersModel> newFollowingFollowersModels = new Gson().fromJson(jsonObject.getString("data"), type);
                    if (newFollowingFollowersModels.size() <= 0) hasMore = false;
                    followingFollowersModels.addAll(newFollowingFollowersModels);
                    followingFollowersDataAdapter.notifyItemRangeInserted(prevSize, followingFollowersModels.size() - prevSize);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }
}