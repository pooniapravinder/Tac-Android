package com.wookes.tac.ui.f;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wookes.tac.R;
import com.wookes.tac.adapter.HomeAdapter;
import com.wookes.tac.exo.VideoPlayerView;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseHome extends Fragment implements ResponseResult, SwipeRefreshLayout.OnRefreshListener {
    protected VideoPlayerView videoPlayerView;
    protected AppCompatActivity activity;
    protected BottomNavigationView bottomNavigationView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected boolean isLoading = true, hasMore = true;
    protected Context context;
    protected HomeAdapter homeAdapter;
    protected ArrayList<FeedModel> feedModels = new ArrayList<>();
    protected String url;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        context = getActivity();
        initView(view);
        (new PagerSnapHelper()).attachToRecyclerView(videoPlayerView);
        feedModels.add(null);
        homeAdapter = new HomeAdapter(bottomNavigationView.getHeight(), feedModels, context);
        videoPlayerView.setAdapter(homeAdapter);
        initScrollListener();
        new AsyncRequest(context, this, null, new RequestData(url, new HashMap<>(), "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    protected void initView(View view) {
        bottomNavigationView = activity.findViewById(R.id.navigation);
        videoPlayerView = view.findViewById(R.id.exoPlayerRecyclerView);
        videoPlayerView.setLayoutManager(new LinearLayoutManager(context));
        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
    }

    @Override
    public void onRefresh() {
        new AsyncRequest(context, this, null, new RequestData(url, new HashMap<>(), "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void initScrollListener() {
        videoPlayerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && hasMore) {
                    if (linearLayoutManager != null && feedModels.size() > 5 && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= feedModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public void onTaskDone(String output) {
        if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
        new Handler().postDelayed(() -> {
            try {
                JSONObject jsonObject = new JSONObject(output);
                if (!jsonObject.has("success")) {
                    ToastDisplay.a(context, R.string.something_wrong, 0);
                    return;
                }
                Type type = new TypeToken<ArrayList<FeedModel>>() {
                }.getType();
                feedModels.clear();
                feedModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
                videoPlayerView.setHomeModels(feedModels);
                homeAdapter = new HomeAdapter(0, feedModels, context);
                videoPlayerView.setAdapter(homeAdapter);
                homeAdapter.notifyDataSetChanged();
                isLoading = false;
                videoPlayerView.startupPlayer();
                if (getArguments() != null && getArguments().getBoolean("pausePlayer"))
                    videoPlayerView.initialPause();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, 500);
    }

    @Override
    public void onTaskFailed(Context context) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    protected void loadData() {
        videoPlayerView.post(() -> {
            Map<String, String> map = new HashMap<>();
            map.put("pagination", String.valueOf(feedModels.size()));
            feedModels.add(null);
            homeAdapter.notifyItemInserted(feedModels.size() - 1);
            new AsyncRequest(context, responseResult, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

    protected ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            isLoading = false;
            try {
                JSONObject jsonObject = new JSONObject(output);
                Type type = new TypeToken<ArrayList<FeedModel>>() {
                }.getType();
                feedModels.remove(feedModels.size() - 1);
                homeAdapter.notifyItemRemoved(homeAdapter.getItemCount());
                int prevSize = homeAdapter.getItemCount();
                ArrayList<FeedModel> newFeedModels = new Gson().fromJson(jsonObject.getString("data"), type);
                if (newFeedModels.size() <= 0) hasMore = false;
                feedModels.addAll(newFeedModels);
                videoPlayerView.setHomeModels(feedModels);
                homeAdapter.notifyItemRangeInserted(prevSize, feedModels.size() - prevSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (videoPlayerView != null) {
            videoPlayerView.releasePlayer();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPlayerView.pausePlayer();
        super.onStop();
    }

    @Override
    public void onPause() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPlayerView.pausePlayer();
        super.onPause();
    }

    public void resumeFragment() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPlayerView.resumePlayer();
    }
}
