package com.wookes.tac.ui.c;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.SearchResultAccountsAdapter;
import com.wookes.tac.model.ProfileModel;
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SearchResultAccounts extends Fragment implements ResponseResult {
    private final String url;
    private Context context;
    private RecyclerView recyclerView;
    private final ArrayList<ProfileModel> profileModels = new ArrayList<>();
    private SearchResultAccountsAdapter searchResultAccountsAdapter;
    boolean isLoading = false, hasMore = true;
    private View rootView;
    private Timer timer = new Timer();
    private boolean isTyping = false;
    private EditText searchQuery;
    private final Map<String, String> map = new HashMap<>();
    private ProgressBar progressBar;
    private ImageButton clearSearch;
    private TextView info;

    public SearchResultAccounts(String url) {
        this.url = url;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        initView(rootView);
        if (searchQuery.getText().length() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            clearSearch.setVisibility(View.GONE);
            map.put("query", searchQuery.getText().toString());
            new AsyncRequest(context, SearchResultAccounts.this, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        searchResultAccountsAdapter = new SearchResultAccountsAdapter(context, profileModels);
        recyclerView.setAdapter(searchResultAccountsAdapter);
        initScrollListener();
        recyclerView.setPadding(0, 0, 0, getArguments().getInt("bottomInset"));
        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                progressBar.setVisibility(View.VISIBLE);
                clearSearch.setVisibility(View.GONE);
                if (!isTyping) {
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isTyping = false;
                        map.put("query", searchQuery.getText().toString());
                        new AsyncRequest(context, SearchResultAccounts.this, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }, 500);
            }
        });
        clearSearch.setOnClickListener(view -> searchQuery.getText().clear());
        return rootView;
    }

    private void initView(View view) {
        context = getActivity();
        searchQuery = getActivity().findViewById(R.id.search_query);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        progressBar = getActivity().findViewById(R.id.progress_bar);
        clearSearch = getActivity().findViewById(R.id.clear_search);
        info = view.findViewById(R.id.info);
    }

    @Override
    public void onTaskDone(String output) {
        try {
            JSONObject jsonObject = new JSONObject(output);
            Type type = new TypeToken<ArrayList<ProfileModel>>() {
            }.getType();
            profileModels.clear();
            profileModels.addAll(new Gson().fromJson(jsonObject.getString("data"), type));
            if (profileModels.size() == 0 && searchQuery.getText().length() > 0) {
                info.setText(context.getString(R.string.no_search_result, Html.fromHtml("<b>" + searchQuery.getText().toString() + "</b>")));
            } else {
                info.setText("");
            }
            searchResultAccountsAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            if (searchQuery.getText().length() > 0) clearSearch.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                new Handler().post(() -> {
                    InputMethodManager inputMethodManger = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManger.hideSoftInputFromWindow(searchQuery.getWindowToken(), 0);
                });
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && hasMore) {
                    if (linearLayoutManager != null && profileModels.size() > 5 && linearLayoutManager.findLastCompletelyVisibleItemPosition() >= profileModels.size() - 5) {
                        loadData();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadData() {
        recyclerView.post(() -> {
            map.put("pagination", String.valueOf(profileModels.size()));
            int lastData = profileModels.size() - 1;
            profileModels.add(null);
            lastData++;
            searchResultAccountsAdapter.notifyItemInserted(lastData);
            new AsyncRequest(context, responseResult, null, new RequestData(url, map, "POST")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
    }

    private final ResponseResult responseResult = new ResponseResult() {
        @Override
        public void onTaskDone(String output) {
            isLoading = false;
            try {
                JSONObject jsonObject = new JSONObject(output);
                Type type = new TypeToken<ArrayList<ProfileModel>>() {
                }.getType();
                profileModels.remove(profileModels.size() - 1);
                searchResultAccountsAdapter.notifyItemRemoved(searchResultAccountsAdapter.getItemCount());
                int prevSize = searchResultAccountsAdapter.getItemCount();
                ArrayList<ProfileModel> newProfileModels = new Gson().fromJson(jsonObject.getString("data"), type);
                if (newProfileModels.size() <= 0) hasMore = false;
                profileModels.addAll(newProfileModels);
                searchResultAccountsAdapter.notifyItemRangeInserted(prevSize, profileModels.size() - prevSize);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}