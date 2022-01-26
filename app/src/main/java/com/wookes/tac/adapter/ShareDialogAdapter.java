package com.wookes.tac.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.interfaces.h;
import com.wookes.tac.model.ShareModel;

import java.util.ArrayList;

public class ShareDialogAdapter extends RecyclerView.Adapter<ShareDialogAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShareModel> shareModels;
    private h h;

    public ShareDialogAdapter(Context context, ArrayList<ShareModel> shareModels, h h) {
        this.context = context;
        this.shareModels = shareModels;
        this.h = h;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_layout_share, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        holder.title.setText(shareModels.get(i).getTitle());
        holder.icon.setImageDrawable(shareModels.get(i).getDrawable());
        holder.itemView.setOnClickListener(view -> h.sharePlatform(shareModels.get(i).getId()));
    }

    @Override
    public int getItemCount() {
        return shareModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }

}