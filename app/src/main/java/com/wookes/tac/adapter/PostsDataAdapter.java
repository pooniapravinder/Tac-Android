package com.wookes.tac.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.ui.b.ExploreVideo;
import com.wookes.tac.util.GlideApp;

import java.util.ArrayList;
import java.util.HashMap;

public class PostsDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<FeedModel> feedModels;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final String url;
    private final HashMap<String, String> map;

    public PostsDataAdapter(Context context, ArrayList<FeedModel> feedModels, String url, HashMap<String, String> map) {
        this.context = context;
        this.feedModels = feedModels;
        this.url = url;
        this.map = map;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_posts_data, viewGroup, false);
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
        return feedModels == null ? 0 : feedModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return feedModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final TextView likes;

        ItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            likes = itemView.findViewById(R.id.no_of_likes);
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
        GlideApp.load(context, feedModels.get(i).getPost().getThumbnail(), holder.thumbnail);
        if (context instanceof LandingUi) {
            Drawable drawable = context.getDrawable(R.drawable.icon_comment_like_nor);
            drawable.setBounds(0, 0, 25, 25);
            holder.likes.setCompoundDrawables(drawable, null, null, null);
            holder.likes.getCompoundDrawables()[0].setTint(Color.WHITE);
            holder.likes.setVisibility(View.VISIBLE);
            holder.likes.setText(String.valueOf(feedModels.get(i).getPost().getLikes()));
        } else {
            holder.likes.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ExploreVideo.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("feedModels", feedModels);
            intent.putExtras(bundle);
            intent.putExtra("parameters", map);
            intent.putExtra("position", i);
            intent.putExtra("url", url);
            context.startActivity(intent);
        });
    }

}