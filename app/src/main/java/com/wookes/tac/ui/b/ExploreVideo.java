package com.wookes.tac.ui.b;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.HomeAdapter;
import com.wookes.tac.cz.ConstantsHome;
import com.wookes.tac.exo.VideoPlayerView;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.util.StatusBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExploreVideo extends AppCompatActivity implements ResponseResult {
    private VideoPlayerView videoPlayerView;
    private Toolbar toolbar;
    private Context context;
    private String url;
    private ArrayList<FeedModel> feedModels = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    private HomeAdapter homeAdapter;
    private boolean isLoading = false, hasMore = true;

    @SuppressWarnings({"unchecked"})
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StatusBar.setStatusBarNavigationBarBlack(getWindow());
        setContentView(R.layout.activity_layout_explore_video);
        initView();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        (new PagerSnapHelper()).attachToRecyclerView(videoPlayerView);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        int position = intent.getIntExtra("position", 0);
        if (intent.getExtras() != null) {
            feedModels = (ArrayList<FeedModel>) intent.getExtras().getSerializable("feedModels");
            map = (Map<String, String>) intent.getExtras().getSerializable("parameters");
        }
        /*StatusBar.getInset(appBarLayout, (topInset, bottomInset) -> {
            appBarLayout.setPadding(0, topInset, 0, 0);
            videoPlayerView.setHomeModels(feedModels);
            homeAdapter = new HomeAdapter(bottomInset, feedModels, context, ExploreVideo.this);
            videoPlayerView.setAdapter(homeAdapter);
            initScrollListener();
            videoPlayerView.scrollToPosition(position + getRealPosition(position));
            videoPlayerView.startupPlayer();
        });*/
        videoPlayerView.setHomeModels(feedModels);
        homeAdapter = new HomeAdapter(0, feedModels, context);
        videoPlayerView.setAdapter(homeAdapter);
        initScrollListener();
        videoPlayerView.scrollToPosition(position + getRealPosition(position));
        videoPlayerView.startupPlayer();
        if (position == feedModels.size() - 1) {
            isLoading = true;
            loadData();
        }
    }

    private void initView() {
        context = this;
        toolbar = findViewById(R.id.toolbar);
        videoPlayerView = findViewById(R.id.exoPlayerRecyclerView);
        videoPlayerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onDestroy() {
        if (videoPlayerView != null) {
            videoPlayerView.releasePlayer();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPlayerView.pausePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPlayerView.pausePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoPlayerView.resumePlayer();
    }

    @Override
    public void onTaskDone(String output) {
        isLoading = false;
        try {
            JSONObject jsonObject = new JSONObject(output);
            Type type = new TypeToken<ArrayList<FeedModel>>() {
            }.getType();
            int prevSize = homeAdapter.getItemCount();
            ArrayList<FeedModel> newFeedModels = new Gson().fromJson(jsonObject.getString("data"), type);
            if (newFeedModels.size() <= 0) hasMore = false;
            feedModels.addAll(newFeedModels);
            videoPlayerView.setHomeModels(feedModels);
            homeAdapter.notifyItemRangeInserted(prevSize, newFeedModels.size() - prevSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initScrollListener() {
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

    private void loadData() {
        map.put("pagination", String.valueOf(feedModels.size()));
        new AsyncRequest(context, ExploreVideo.this, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int getRealPosition(int position) {
        return ConstantsHome.ADS_DELTA > 0 ? (position + 1) / (ConstantsHome.ADS_DELTA + 1) : 0;
    }
}
