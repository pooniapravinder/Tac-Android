package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.custom.RoundRectCornerImageView;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.ui.Hashtag;
import com.wookes.tac.ui.b.ExploreVideo;
import com.wookes.tac.urlconfig.URLConfig;
import com.wookes.tac.util.GlideApp;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscoverSidebySideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<FeedModel> feedModels;
    private final long hashtagId;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_END = 1;

    public DiscoverSidebySideAdapter(Context context, ArrayList<FeedModel> feedModels, long hashtagId) {
        this.context = context;
        this.feedModels = feedModels;
        this.hashtagId = hashtagId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_discover_side_by_side, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (i == VIEW_TYPE_END) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_discover_more_items, viewGroup, false);
            return new EndHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid ViewType Provided : " + this.getClass());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof EndHolder) {
            populateEndHolder((EndHolder) viewHolder);
        } else if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return feedModels.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if ((feedModels.size()) == position) {
            return VIEW_TYPE_END;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    private static class EndHolder extends RecyclerView.ViewHolder {
        public EndHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void populateEndHolder(EndHolder holder) {
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, Hashtag.class);
            intent.putExtra("hashtag", String.valueOf(hashtagId));
            intent.putExtra("hashtag_type", 0);
            context.startActivity(intent);
        });
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final RoundRectCornerImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

    private void populateItemRows(ItemViewHolder holder, int i) {
        GlideApp.loadSimple(context, feedModels.get(i).getPost().getThumbnail(), holder.imageView);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ExploreVideo.class);
            Bundle bundle = new Bundle();
            HashMap<String, String> map = new HashMap<>();
            map.put("hashtag_id", String.valueOf(hashtagId));
            bundle.putSerializable("feedModels", feedModels);
            intent.putExtras(bundle);
            intent.putExtra("parameters", map);
            intent.putExtra("position", i);
            intent.putExtra("url", URLConfig.RETRIEVE_TOP_HASHTAGS);
            context.startActivity(intent);
        });
    }

}