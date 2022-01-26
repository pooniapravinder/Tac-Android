package com.wookes.tac.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wookes.tac.R;
import com.wookes.tac.adapter.GalleryDialogAdapter;
import com.wookes.tac.model.GalleryDialogModel;
import com.wookes.tac.ui.ProfilePhoto;
import com.wookes.tac.util.GridSpacingItemDecoration;
import com.wookes.tac.util.ToastDisplay;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryDialogImage extends BottomSheetDialogFragment {
    public static final String TAG = "GalleryDialogImage";
    private static boolean signUpFlow = false;
    private Context context;
    private RecyclerView recyclerView;
    private TextView loader;

    public static GalleryDialogImage newInstance() {
        return new GalleryDialogImage();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null && ((boolean) bundle.getSerializable("signUpFlow"))) {
            signUpFlow = true;
        }
        ((TextView) view.findViewById(R.id.title)).setText(R.string.select_profile_photo);
        recyclerView = view.findViewById(R.id.recycler_view);
        loader = view.findViewById(R.id.loader);
        int spanCount = 3; // 3 columns
        int spacing = 3; // 3px
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), spanCount, GridLayoutManager.VERTICAL, false));
        if (checkReadWritePermission()) {
            new BackgroundTask((AppCompatActivity) getActivity(), recyclerView, loader, getDialog()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    static class BackgroundTask extends AsyncTask<String, String, String> {
        private WeakReference<Context> context;
        private WeakReference<AppCompatActivity> activity;
        private WeakReference<RecyclerView> recyclerView;
        private ArrayList<GalleryDialogModel> galleryDialogModels = new ArrayList<>();
        private WeakReference<TextView> loader;
        private WeakReference<Dialog> dialog;

        public BackgroundTask(AppCompatActivity activity, RecyclerView recyclerView, TextView loader, Dialog dialog) {
            this.context = new WeakReference<>(activity);
            this.activity = new WeakReference<>(activity);
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
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, orderBy + " DESC")) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                        galleryDialogModels.add(new GalleryDialogModel(null, Uri.parse(data), mimeType));
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
                dialog.get().dismiss();
                Intent intent = new Intent(context.get(), ProfilePhoto.class);
                intent.putExtra("imageUri", galleryDialogModel.getVideoUri().toString());
                intent.putExtra("signUpFlow", signUpFlow);
                if (signUpFlow) {
                    activity.get().startActivity(intent);
                } else {
                    activity.get().startActivityForResult(intent, 1);
                }
            });
            loader.get().setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Integer> perms = new HashMap<>();
        if (requestCode == 4) {
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new BackgroundTask((AppCompatActivity) getActivity(), recyclerView, loader, getDialog()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showDialogOK("Read and Write Permissions required for profile photo",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            checkReadWritePermission();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            getDialog().dismiss();
                                            break;
                                    }
                                });
                    } else {
                        ToastDisplay.a(context, context.getResources().getString(R.string.go_to_settings_enable_permission), 0);
                    }
                }
            }
        }
    }

    private boolean checkReadWritePermission() {
        int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[0]), 4);
            return false;
        } else {
            return true;
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}