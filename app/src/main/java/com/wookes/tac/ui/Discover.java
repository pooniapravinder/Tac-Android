package com.wookes.tac.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wookes.tac.R;
import com.wookes.tac.adapter.DiscoverItemsAdapter;
import com.wookes.tac.model.DiscoverBannerModel;
import com.wookes.tac.model.DiscoverItemsModel;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GlideApp;
import com.wookes.tac.util.SelfUser;
import com.wookes.tac.util.ToastDisplay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Discover extends Fragment implements ResponseResult, SwipeRefreshLayout.OnRefreshListener {
    private View searchBadge;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final ArrayList<DiscoverItemsModel> discoverItemsModels = new ArrayList<>();
    private final ArrayList<DiscoverBannerModel> discoverBannerModels = new ArrayList<>();
    private DiscoverItemsAdapter discoverItemsAdapter;
    private boolean isLoading = true, hasMore = true;

    public static Discover newInstance() {
        return new Discover();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        View rootView = inflater.inflate(R.layout.activity_layout_discover, container, false);
        initView(rootView);
        discoverItemsModels.add(null);
        discoverItemsAdapter = new DiscoverItemsAdapter(context, discoverItemsModels, discoverBannerModels);
        recyclerView.setAdapter(discoverItemsAdapter);
        initScrollListener();
        new AsyncRequest(context, Discover.this, null, new RequestData(URLConfig.RETRIEVE_DISCOVER_ITEMS, new HashMap<>(), "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        searchBadge.setOnClickListener(view -> startActivity(new Intent(context, Search.class)));
        return rootView;
    }

    private void initView(View view) {
        searchBadge = view.findViewById(R.id.search_badge);
        recyclerView = view.findViewById(R.id.discover_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ImageView userPic = view.findViewById(R.id.user_pic);
        if (new SelfUser(context).getSelfUser() != null) {
            GlideApp.loadSimple(context, (new SelfUser(context).getSelfUser().getProfilePic()), userPic);
        }
        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
    }

    @Override
    public void onTaskDone(String output) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            discoverItemsModels.clear();
            discoverBannerModels.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        try {
            JSONObject jsonObject = new JSONObject(output);
            if (!jsonObject.has("success")) {
                ToastDisplay.a(context, R.string.something_wrong, 0);
                return;
            }
            Type type = new TypeToken<ArrayList<DiscoverItemsModel>>() {
            }.getType();
            if (isLoading) discoverItemsModels.clear();
            discoverItemsModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
            type = new TypeToken<ArrayList<DiscoverBannerModel>>() {
            }.getType();
            discoverBannerModels.addAll(new Gson().fromJson(jsonObject.getString("banner"), type));
            discoverItemsAdapter.notifyDataSetChanged();
            isLoading = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskFailed(Context context) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new AsyncRequest(context, Discover.this, null, new RequestData(URLConfig.RETRIEVE_DISCOVER_ITEMS, new HashMap<>(), "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    if (linearLayoutManager != null && discoverItemsModels.size() > 5 && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= discoverItemsModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadData() {
        Map<String, String> map = new HashMap<>();
        map.put("pagination", String.valueOf(discoverItemsModels.size()));
        discoverItemsModels.add(null);
        discoverItemsAdapter.notifyItemInserted(discoverItemsModels.size() - 1);
        new AsyncRequest(context, responseResult, null, new RequestData(URLConfig.RETRIEVE_DISCOVER_ITEMS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private final ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            isLoading = false;
            try {
                JSONObject jsonObject = new JSONObject(output);
                Type type = new TypeToken<ArrayList<DiscoverItemsModel>>() {
                }.getType();
                discoverItemsModels.remove(discoverItemsModels.size() - 1);
                discoverItemsAdapter.notifyItemRemoved(discoverItemsAdapter.getItemCount());
                int prevSize = discoverItemsAdapter.getItemCount();
                ArrayList<DiscoverItemsModel> newDiscoverItemsModels = new Gson().fromJson(jsonObject.getString("data"), type);
                if (newDiscoverItemsModels.size() <= 0) hasMore = false;
                discoverItemsModels.addAll(newDiscoverItemsModels);
                discoverItemsAdapter.notifyItemRangeInserted(prevSize, discoverItemsModels.size() - prevSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}