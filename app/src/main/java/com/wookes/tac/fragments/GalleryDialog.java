package com.wookes.tac.fragments;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.GalleryDialogAdapter;
import com.wookes.tac.model.GalleryDialogModel;
import com.wookes.tac.ui.Post;
import com.wookes.tac.util.GridSpacingItemDecoration;
import com.wookes.tac.util.ToastDisplay;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GalleryDialog extends BottomSheetDialogFragment {
    public static final String TAG = "GalleryDialog";

    public static GalleryDialog newInstance() {
        return new GalleryDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        TextView loader = view.findViewById(R.id.loader);
        int spanCount = 3; // 3 columns
        int spacing = 3; // 3px
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false));

    }

    static class BackgroundTask extends AsyncTask<String, String, String> {
        private WeakReference<Context> context;
        private WeakReference<RecyclerView> recyclerView;
        private ArrayList<GalleryDialogModel> galleryDialogModels = new ArrayList<>();
        private WeakReference<TextView> loader;
        private WeakReference<Dialog> dialog;

        public BackgroundTask(Context context, RecyclerView recyclerView, TextView loader, Dialog dialog) {
            this.context = new WeakReference<>(context);
            this.recyclerView = new WeakReference<>(recyclerView);
            this.loader = new WeakReference<>(loader);
            this.dialog = new WeakReference<>(dialog);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            ContentResolver contentResolver = context.get().getContentResolver();
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String orderBy = MediaStore.Video.Media.DATE_TAKEN;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, orderBy + " DESC")) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                        galleryDialogModels.add(new GalleryDialogModel(duration, Uri.parse(data), mimeType));
                    } while (cursor.moveToNext());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            GalleryDialogAdapter adapter = new GalleryDialogAdapter(context.get(), galleryDialogModels);
            recyclerView.get().setAdapter(adapter);
            adapter.setOnItemClickListener((position, v) -> {
                GalleryDialogModel galleryDialogModel = galleryDialogModels.get(position);
                long duration = Long.parseLong(galleryDialogModel.getDuration()) / 1000;
                if (duration < 5 || duration > 60) {
                    ToastDisplay.a(context.get(), context.get().getResources().getString(R.string.video_duration_exceed), 0);
                } else if (!galleryDialogModel.getMimeType().equals("video/mp4")) {
                    ToastDisplay.a(context.get(), context.get().getResources().getString(R.string.video_invalid_extension), 0);
                } else {
                    dialog.get().dismiss();
                    Intent intent = new Intent(context.get(), Post.class);
                    intent.putExtra("videoUri", galleryDialogModel.getVideoUri().toString());
                    context.get().startActivity(intent);
                }
            });
            loader.get().setVisibility(View.GONE);
        }
    }
}