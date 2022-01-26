package com.wookes.tac.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wookes.tac.R;
import com.wookes.tac.adapter.NotificationsAdapter;
import com.wookes.tac.fragments.NotificationsFilterDialog;
import com.wookes.tac.fragments.VideoPrivacyDialog;
import com.wookes.tac.model.NotificationsModel;
import com.wookes.tac.model.User;
import com.wookes.tac.request.AsyncRequest;
import com.wookes.tac.request.RequestData;
import com.wookes.tac.request.ResponseResult;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.ToastDisplay;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notifications extends Fragment implements ResponseResult, SwipeRefreshLayout.OnRefreshListener {
    private View noNotifications;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final ArrayList<NotificationsModel> notificationsModels = new ArrayList<>();
    private NotificationsAdapter notificationsAdapter;
    private boolean isLoading = true, hasMore = true;
    private int filter = 1, pagination = 0;
    private TextView currentFilter;
    private Map<String, String> map = new HashMap<>();

    public static Notifications newInstance() {
        return new Notifications();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        View rootView = inflater.inflate(R.layout.activity_layout_notifications, container, false);
        initView(rootView);
        notificationsModels.add(null);
        notificationsAdapter = new NotificationsAdapter(context, notificationsModels);
        recyclerView.setAdapter(notificationsAdapter);
        initScrollListener();
        map.put("type", String.valueOf(filter - 1));
        new AsyncRequest(context, Notifications.this, null, new RequestData(URLConfig.RETRIEVE_NOTIFICATIONS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.notification_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        currentFilter = view.findViewById(R.id.current_filter);
        noNotifications = view.findViewById(R.id.no_notifications);
        view.findViewById(R.id.filter).setOnClickListener(view1 -> openNotificationsFilterDialog());
    }

    @Override
    public void onTaskDone(String output) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            notificationsModels.clear();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        try {
            JSONObject jsonObject = new JSONObject(output);
            if (!jsonObject.has("success")) {
                ToastDisplay.a(context, R.string.something_wrong, 0);
                return;
            }
            Type type = new TypeToken<ArrayList<NotificationsModel>>() {
            }.getType();
            if (isLoading) notificationsModels.clear();
            ArrayList<NotificationsModel> newNotificationsModels = new Gson().fromJson(jsonObject.getString("data"), type);
            noNotifications.setVisibility(newNotificationsModels.size() > 0 ? View.GONE : View.VISIBLE);
            pagination = newNotificationsModels.size();
            notificationsModels.addAll(getItems(newNotificationsModels));
            notificationsAdapter.notifyDataSetChanged();
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
        map = new HashMap<>();
        map.put("type", String.valueOf(filter - 1));
        new AsyncRequest(context, Notifications.this, null, new RequestData(URLConfig.RETRIEVE_NOTIFICATIONS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    if (linearLayoutManager != null && notificationsModels.size() > 20 && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= notificationsModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadData() {
        map = new HashMap<>();
        map.put("pagination", String.valueOf(pagination));
        map.put("type", String.valueOf(filter - 1));
        notificationsModels.add(null);
        notificationsAdapter.notifyItemInserted(notificationsModels.size() - 1);
        new AsyncRequest(context, responseResult, null, new RequestData(URLConfig.RETRIEVE_NOTIFICATIONS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private final ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            isLoading = false;
            try {
                JSONObject jsonObject = new JSONObject(output);
                Type type = new TypeToken<ArrayList<NotificationsModel>>() {
                }.getType();
                notificationsModels.remove(notificationsModels.size() - 1);
                notificationsAdapter.notifyItemRemoved(notificationsAdapter.getItemCount());
                int prevSize = notificationsAdapter.getItemCount();
                ArrayList<NotificationsModel> newNotificationsModels = new Gson().fromJson(jsonObject.getString("data"), type);
                pagination = pagination + newNotificationsModels.size();
                if (newNotificationsModels.size() <= 0) hasMore = false;
                notificationsModels.addAll(getItems(newNotificationsModels));
                notificationsAdapter.notifyItemRangeInserted(prevSize, notificationsModels.size() - prevSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void openNotificationsFilterDialog() {
        NotificationsFilterDialog notificationsFilterDialog = NotificationsFilterDialog.newInstance();
        Bundle args = new Bundle();
        args.putInt("position", filter);
        notificationsFilterDialog.setArguments(args);
        notificationsFilterDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VideoPrivacyDialog.TAG);
    }

    public void setResultFromFragment(int position) {
        filter = position + 1;
        map = new HashMap<>();
        map.put("type", String.valueOf(filter - 1));
        currentFilter.setText(context.getResources().getStringArray(R.array.notification_filters)[position]);
        notificationsModels.clear();
        notificationsModels.add(null);
        notificationsAdapter.notifyDataSetChanged();
        isLoading = true;
        new AsyncRequest(context, Notifications.this, null, new RequestData(URLConfig.RETRIEVE_NOTIFICATIONS, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private ArrayList<NotificationsModel> getItems(ArrayList<NotificationsModel> notificationsModels) {
        String tempUsers = null;
        String profilePics = null;
        NotificationsModel notificationsModel;
        ArrayList<NotificationsModel> notificationsModels1 = new ArrayList<>();
        for (int i = 0; i < notificationsModels.size(); i++) {
            tempUsers = tempUsers == null ? "@" + notificationsModels.get(i).getUser().getUserName() : tempUsers + "," + "@" + notificationsModels.get(i).getUser().getUserName();
            profilePics = profilePics == null ? notificationsModels.get(i).getUser().getProfilePic() : profilePics + "," + notificationsModels.get(i).getUser().getProfilePic();
            if (i == notificationsModels.size() - 1 || notificationsModels.get(i).getType() != notificationsModels.get(i + 1).getType() || (notificationsModels.get(i).getMedia() != null && notificationsModels.get(i).getMedia() == null) || (notificationsModels.get(i).getMedia() != null && notificationsModels.get(i + 1).getMedia() != null && notificationsModels.get(i).getMedia().getId() != notificationsModels.get(i + 1).getMedia().getId())) {
                notificationsModel = notificationsModels.get(i);
                notificationsModels1.add(new NotificationsModel(notificationsModel.getActivityId(), notificationsModel.getType(), notificationsModel.getTypeName(), notificationsModel.getIsFollowing(), notificationsModel.getTimestamp(), new User(notificationsModel.getUser().getId(), notificationsModel.getUser().getFullName(), tempUsers, profilePics, notificationsModel.getUser().isVerified()), notificationsModel.getMedia()));
                tempUsers = null;
                profilePics = null;
            }
        }
        return notificationsModels1;
    }
}