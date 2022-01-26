package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.HashtagModel;
import com.wookes.tac.ui.Hashtag;
import com.wookes.tac.util.CustomCounter;

import java.util.ArrayList;

public class SearchResultHashtagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<HashtagModel> hashtagModels;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public SearchResultHashtagAdapter(Context context, ArrayList<HashtagModel> hashtagModels) {
        this.context = context;
        this.hashtagModels = hashtagModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_search_result_hashtags, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid ViewType Provided : " + this.getClass());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, i);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return hashtagModels == null ? 0 : hashtagModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return hashtagModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView hashtagName;
        private final TextView noOfHashtags;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            hashtagName = itemView.findViewById(R.id.hashtag_name);
            noOfHashtags = itemView.findViewById(R.id.no_of_hashtags);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void populateItemRows(ItemViewHolder holder, int i) {
        holder.hashtagName.setText(hashtagModels.get(i).getHashtagName());
        holder.noOfHashtags.setText(context.getString(R.string.hashtag_total_videos, CustomCounter.format(hashtagModels.get(i).getHashtagTotal())));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, Hashtag.class);
            intent.putExtra("hashtag", String.valueOf(hashtagModels.get(i).getHashtagId()));
            intent.putExtra("hashtag_type", 0);
            context.startActivity(intent);
        });
    }

}