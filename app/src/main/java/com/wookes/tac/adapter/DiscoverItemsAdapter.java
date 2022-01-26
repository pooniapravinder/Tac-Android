package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.custom.ContentViewFlipper;
import com.wookes.tac.cz.ConstantsHome;
import com.wookes.tac.model.DiscoverBannerModel;
import com.wookes.tac.model.DiscoverItemsModel;
import com.wookes.tac.ui.Hashtag;
import com.wookes.tac.util.CustomCounter;
import com.wookes.tac.util.GlideApp;

import java.util.ArrayList;

public class DiscoverItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DiscoverItemsModel> discoverItemsModels;
    private ArrayList<DiscoverBannerModel> discoverBannerModels;
    private final int VIEW_TYPE_BANNER = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 2;
    private final int VIEW_TYPE_ADS = 3;
    private final int ADS_DELTA = ConstantsHome.ADS_DELTA;

    public DiscoverItemsAdapter(Context context, ArrayList<DiscoverItemsModel> discoverItemsModels, ArrayList<DiscoverBannerModel> discoverBannerModels) {
        this.context = context;
        this.discoverItemsModels = discoverItemsModels;
        this.discoverBannerModels = discoverBannerModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_BANNER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_discover_top, viewGroup, false);
            return new TopBannerHolder(view);
        } else if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_discover_items, viewGroup, false);
            return new ItemViewHolder(view);
        } else if (i == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        } else if (i == VIEW_TYPE_ADS) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_banner_ads, viewGroup, false);
            return new AdsHolder(view);
        } else {
            throw new IllegalArgumentException("Invalid ViewType Provided : " + this.getClass());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof TopBannerHolder) {
            populateBanner((TopBannerHolder) viewHolder);
        } else if (viewHolder instanceof LoadingViewHolder) {
            populateLoader((LoadingViewHolder) viewHolder, i);
        } else if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, getRealPosition(i));
        } else if (viewHolder instanceof AdsHolder) {
            populateAds((AdsHolder) viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        int itemsSize = discoverItemsModels.size() > 0 && discoverItemsModels.get(discoverItemsModels.size() - 1) == null ? discoverItemsModels.size() - 1 : discoverItemsModels.size();
        int additionalContent = 0;
        if (ADS_DELTA > 0 && itemsSize > ADS_DELTA) {
            additionalContent = itemsSize / (ADS_DELTA);
        }
        return discoverItemsModels.size() + additionalContent + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_BANNER;
        } else if (discoverItemsModels.get(getRealPosition(position)) == null) {
            return VIEW_TYPE_LOADING;
        } else if (isShowAds(position)) {
            return VIEW_TYPE_ADS;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }


    private static class TopBannerHolder extends RecyclerView.ViewHolder {
        private ContentViewFlipper viewFlipper;

        public TopBannerHolder(@NonNull View itemView) {
            super(itemView);
            viewFlipper = itemView.findViewById(R.id.discover_view_flip);
        }
    }

    private void populateBanner(TopBannerHolder holder) {
        ImageView imageView;
        for (DiscoverBannerModel discoverBannerModel : discoverBannerModels) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.viewFlipper.addView(imageView);
            GlideApp.loadSimple(context, discoverBannerModel.getBannerImage(), imageView);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView hastagName, noOfHashtags;
        private RecyclerView recyclerView;

        ItemViewHolder(View itemView) {
            super(itemView);
            hastagName = itemView.findViewById(R.id.hashtag_name);
            noOfHashtags = itemView.findViewById(R.id.no_of_hashtags);
            recyclerView = itemView.findViewById(R.id.side_by_side);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        }
    }

    private void populateItemRows(ItemViewHolder holder, int i) {
        holder.hastagName.setText(discoverItemsModels.get(i).getHashtagName());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, Hashtag.class);
            intent.putExtra("hashtag", String.valueOf(discoverItemsModels.get(i).getHashtagId()));
            intent.putExtra("hashtag_type", 0);
            context.startActivity(intent);
        });
        holder.noOfHashtags.setText(CustomCounter.format(discoverItemsModels.get(i).getHashtagTotal()));
        DiscoverSidebySideAdapter discoverSidebySideAdapter = new DiscoverSidebySideAdapter(context, discoverItemsModels.get(i).getFeedModels(), discoverItemsModels.get(i).getHashtagId());
        holder.recyclerView.setAdapter(discoverSidebySideAdapter);
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
        private ViewFlipper viewFlipper;

        public AdsHolder(@NonNull View itemView) {
            super(itemView);
            viewFlipper = itemView.findViewById(R.id.discover_view_flip);
        }
    }

    private void populateAds(AdsHolder holder, int position) {
        ImageView imageView;
    }

    private int getRealPosition(int position) {
        return ADS_DELTA > 0 ? (position - ((position) / (ADS_DELTA + 1))) - 1 : position;
    }

    private boolean isShowAds(int position) {
        return ADS_DELTA > 0 && (position % (ADS_DELTA + 1)) == 0;
    }
}