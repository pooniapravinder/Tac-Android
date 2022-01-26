package com.wookes.tac.ui.a;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.PostsDataAdapter;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.util.GridSpacingItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class PostsData extends Fragment implements ResponseResult {
    private String url;
    private String fragmentType;
    private Context context;
    private RecyclerView recyclerView;
    private TextView dataInfo;
    private final ArrayList<FeedModel> feedModels = new ArrayList<>();
    private PostsDataAdapter postsDataAdapter;
    boolean isLoading = true, hasMore = true;
    private HashMap<String, String> map;

    public static PostsData newInstance() {
        return new PostsData();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_data, container, false);
        initView(rootView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString("url");
            map = (HashMap<String, String>) bundle.getSerializable("map");
            fragmentType = bundle.getString("fragmentType");
        }
        feedModels.add(null);
        postsDataAdapter = new PostsDataAdapter(context, feedModels, url, map);
        recyclerView.setAdapter(postsDataAdapter);
        initScrollListener();
        recyclerView.setPadding(0, 0, 0, getArguments().getInt("bottomInset"));
        new AsyncRequest(context, PostsData.this, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return rootView;
    }

    private void initView(View view) {
        context = getActivity();
        recyclerView = view.findViewById(R.id.recycler_view);
        dataInfo = view.findViewById(R.id.data_info);
        int spanCount = 3; // 3 columns
        int spacing = 2; // 3px
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing));
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false));
    }

    @Override
    public void onTaskDone(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Type type = new TypeToken<ArrayList<FeedModel>>() {
            }.getType();
            feedModels.remove(0);
            feedModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
            if (feedModels.size() <= 0) {
                dataInfo.setText(noData());
            }
            postsDataAdapter.notifyDataSetChanged();
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
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && hasMore) {
                    if (gridLayoutManager != null && feedModels.size() > 5 && gridLayoutManager.findLastCompletelyVisibleItemPosition() >= feedModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadData() {
        recyclerView.post(() -> {
            map.put("pagination", String.valueOf(feedModels.size()));
            feedModels.add(null);
            postsDataAdapter.notifyItemInserted(feedModels.size() - 1);
            new AsyncRequest(context, responseResult, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

    private final ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            isLoading = false;
            try {
                JSONObject jsonObject = new JSONObject(output);
                Type type = new TypeToken<ArrayList<FeedModel>>() {
                }.getType();
                feedModels.remove(feedModels.size() - 1);
                postsDataAdapter.notifyItemRemoved(postsDataAdapter.getItemCount());
                int prevSize = postsDataAdapter.getItemCount();
                ArrayList<FeedModel> newFeedModels = new Gson().fromJson(jsonObject.getString("data"), type);
                if (newFeedModels.size() <= 0) hasMore = false;
                feedModels.addAll(newFeedModels);
                postsDataAdapter.notifyItemRangeInserted(prevSize, feedModels.size() - prevSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private int noData() {
        switch (fragmentType) {
            case "videos":
                return R.string.no_videos_available;
            case "likes":
                return R.string.no_likes_available;
            case "saved":
                return R.string.no_saved_available;
            default:
        }
        return 0;
    }
}