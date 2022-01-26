package com.wookes.tac.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.model.GalleryDialogModel;
import com.wookes.tac.util.GlideApp;

import java.util.ArrayList;

public class GalleryDialogAdapter extends RecyclerView.Adapter<GalleryDialogAdapter.viewHolder> {

    private Context context;
    private ArrayList<GalleryDialogModel> galleryDialogModels;
    private OnItemClickListener onItemClickListener;

    public GalleryDialogAdapter(Context context, ArrayList<GalleryDialogModel> galleryDialogModels) {
        this.context = context;
        this.galleryDialogModels = galleryDialogModels;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.inflate_layout_video_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int i) {
        GlideApp.loadThumbnailGallery(context, galleryDialogModels.get(i).getVideoUri().toString(), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return galleryDialogModels.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        viewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
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