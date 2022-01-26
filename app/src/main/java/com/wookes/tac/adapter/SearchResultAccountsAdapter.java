package com.wookes.tac.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.ProfileModel;
import com.wookes.tac.ui.UserProfile;
import com.wookes.tac.util.GlideApp;

import java.util.ArrayList;

public class SearchResultAccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<ProfileModel> profileModels;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public SearchResultAccountsAdapter(Context context, ArrayList<ProfileModel> profileModels) {
        this.context = context;
        this.profileModels = profileModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_layout_search_result_accounts, viewGroup, false);
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
        return profileModels == null ? 0 : profileModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return profileModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profilePic;
        private final TextView username;
        private final TextView fullName;

        ItemViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
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
        GlideApp.loadProfilePic(context, profileModels.get(i).getProfilePic(), holder.profilePic);
        holder.username.setText(String.valueOf(profileModels.get(i).getUserName()));
        holder.fullName.setText(String.valueOf(profileModels.get(i).getFullName()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("user", String.valueOf(profileModels.get(i).getUserId()));
            intent.putExtra("user_type", 0);
            context.startActivity(intent);
        });
    }

}