package com.wookes.tac.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.VideoPrivacyDialogModel;

import java.util.ArrayList;

public class VideoPrivacyDialogAdapter extends RecyclerView.Adapter<VideoPrivacyDialogAdapter.viewHolder> {

    private Context context;
    private ArrayList<VideoPrivacyDialogModel> videoPrivacyDialogModels;
    private OnItemClickListener onItemClickListener;
    private int selected;

    public VideoPrivacyDialogAdapter(Context context, ArrayList<VideoPrivacyDialogModel> videoPrivacyDialogModels, int selected) {
        this.context = context;
        this.videoPrivacyDialogModels = videoPrivacyDialogModels;
        this.selected = selected;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_layout_video_privacy, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int i) {
        holder.title.setText(videoPrivacyDialogModels.get(i).getTitle());
        if (videoPrivacyDialogModels.get(i).getSubtitle() != null) {
            holder.subtitle.setText(videoPrivacyDialogModels.get(i).getSubtitle());
            holder.subtitle.setVisibility(View.VISIBLE);
        } else {
            holder.subtitle.setVisibility(View.GONE);
        }
        if(videoPrivacyDialogModels.get(i).getIcon()!=null){
            holder.title.setCompoundDrawablesWithIntrinsicBounds(videoPrivacyDialogModels.get(i).getIcon(),null,null,null);
            holder.title.setCompoundDrawablePadding(20);
        }
        if ((selected - 1) == i) {
            holder.title.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.title.getCompoundDrawables()[0].setTint(context.getResources().getColor(android.R.color.black));
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return videoPrivacyDialogModels.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView title, subtitle;
        private ImageView selected;

        viewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            selected = itemView.findViewById(R.id.selected);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition(), v));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}