package com.wookes.tac.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wookes.tac.R;
import com.wookes.tac.adapter.VideoPrivacyDialogAdapter;
import com.wookes.tac.model.VideoPrivacyDialogModel;
import com.wookes.tac.ui.Notifications;
import com.wookes.tac.ui.Post;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationsFilterDialog extends BottomSheetDialogFragment {
    public static final String TAG = "NotificationsFilterDialog";
    private Dialog dialog;
    private ArrayList<VideoPrivacyDialogModel> videoPrivacyDialogModels = new ArrayList<>();

    public static NotificationsFilterDialog newInstance() {
        return new NotificationsFilterDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout_notification_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        Context context = getActivity();
        dialog = getDialog();
        String[] notificationFilters = context.getResources().getStringArray(R.array.notification_filters);
        TypedArray notificationFiltersIcons = context.getResources().obtainTypedArray(R.array.notification_filters_icons);
        for (int i = 0; i < notificationFilters.length; i++) {
            videoPrivacyDialogModels.add(new VideoPrivacyDialogModel(notificationFilters[i], null, notificationFiltersIcons.getDrawable(i)));
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        VideoPrivacyDialogAdapter adapter = new VideoPrivacyDialogAdapter(context, videoPrivacyDialogModels, bundle != null ? bundle.getInt("position") : 1);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((position, v) -> {
            dialog.dismiss();
            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            ((Notifications) fragment).setResultFromFragment(position);
        });
    }
}