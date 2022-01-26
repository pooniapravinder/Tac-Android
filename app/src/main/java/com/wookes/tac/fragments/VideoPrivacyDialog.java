package com.wookes.tac.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.VideoPrivacyDialogAdapter;
import com.wookes.tac.model.VideoPrivacyDialogModel;
import com.wookes.tac.ui.Post;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

public class VideoPrivacyDialog extends BottomSheetDialogFragment {
    public static final String TAG = "VideoPrivacyDialog";
    private Dialog dialog;
    private ArrayList<VideoPrivacyDialogModel> videoPrivacyDialogModels = new ArrayList<>();

    public static VideoPrivacyDialog newInstance() {
        return new VideoPrivacyDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout_video_privacy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        Context context = getActivity();
        dialog = getDialog();
        String[] videoPrivacy = context.getResources().getStringArray(R.array.video_privacy);
        String[] videoPrivacyDescription = context.getResources().getStringArray(R.array.video_privacy_description);
        for (int i = 0; i < videoPrivacy.length; i++) {
            videoPrivacyDialogModels.add(new VideoPrivacyDialogModel(videoPrivacy[i], videoPrivacyDescription[i], null));
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        VideoPrivacyDialogAdapter adapter = new VideoPrivacyDialogAdapter(context, videoPrivacyDialogModels, bundle != null ? bundle.getInt("position") : 1);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((position, v) -> {
            dialog.dismiss();
            ((Post) Objects.requireNonNull(getActivity())).setResultFromFragment(position);
        });
    }
}