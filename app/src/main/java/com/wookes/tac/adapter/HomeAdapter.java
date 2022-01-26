package com.wookes.tac.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.bcd.FollowHolder;
import com.wookes.tac.cz.ConstantsHome;
import com.wookes.tac.exo.PlayerViewHolder;
import com.wookes.tac.model.FeedModel;
import com.wookes.tac.util.BlurBuilder;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int bottomInset;
    private final ArrayList<FeedModel> feedModels;
    private final Context context;
    private final int VIEW_TYPE_FOLLOW = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 2;
    private final int VIEW_TYPE_ADS = ConstantsHome.VIEW_TYPE_ADS;
    private final int ADS_DELTA = ConstantsHome.ADS_DELTA;
    private final Drawable placeholder;

    public HomeAdapter(int bottomInset, ArrayList<FeedModel> feedModels, Context context) {
        this.bottomInset = bottomInset;
        this.feedModels = feedModels;
        this.context = context;
        this.placeholder = new BitmapDrawable(context.getResources(), BlurBuilder.blur(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_preview_holder)));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_FOLLOW) {
            return new FollowHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_follow_home, viewGroup, false));
        } else if (i == VIEW_TYPE_ITEM) {
            return new PlayerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_home, viewGroup, false));
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading_home, viewGroup, false);
            return new LoadingViewHolder(view);
        } else if (i == VIEW_TYPE_ADS) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_reward_ads, viewGroup, false);
            return new AdsHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid ViewType Provided : " + this.getClass());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof FollowHolder) {
            ((FollowHolder) viewHolder).onBind(context);
        } else if (viewHolder instanceof LoadingViewHolder) {
            populateLoader((LoadingViewHolder) viewHolder, i);
        } else if (viewHolder instanceof PlayerViewHolder) {
            ((PlayerViewHolder) viewHolder).onBind(bottomInset, feedModels.get(getRealPosition(i)), context, placeholder);
        } else if (viewHolder instanceof AdsHolder) {
            populateAds((AdsHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        int itemsSize = feedModels.size() > 0 && feedModels.get(feedModels.size() - 1) == null ? feedModels.size() - 1 : feedModels.size();
        int additionalContent = 0;
        if (ADS_DELTA > 0 && itemsSize > ADS_DELTA) {
            additionalContent = itemsSize / (ADS_DELTA);
        }
        return feedModels.size() <= 0 ? 1 : feedModels.size() + additionalContent;
    }

    @Override
    public int getItemViewType(int position) {
        int realPosition = getRealPosition(position);
        if (feedModels.size() <= 0) {
            return VIEW_TYPE_FOLLOW;
        } else if (realPosition> -1 && realPosition < feedModels.size() && feedModels.get(realPosition) == null) {
            return VIEW_TYPE_LOADING;
        } else if (isShowAds(position)) {
            return VIEW_TYPE_ADS;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private void populateLoader(LoadingViewHolder viewHolder, int position) {
    }


    private static class AdsHolder extends RecyclerView.ViewHolder {

        public AdsHolder(View itemView) {
            super(itemView);
        }
    }

    private void populateAds(AdsHolder holder, int position) {
    }

    private int getRealPosition(int position) {
        return ADS_DELTA > 0 && position > ADS_DELTA ? position - ((position + 1) / (ADS_DELTA + 1)) : position;
    }

    private boolean isShowAds(int position) {
        position++;
        return ADS_DELTA > 0 && (position % (ADS_DELTA + 1)) == 0;
    }
}