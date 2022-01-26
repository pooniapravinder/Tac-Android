package com.wookes.tac.cv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected AppCompatActivity mActivity;

    private boolean isFragmentVisible;

    private boolean isPrepared;

    private boolean isFirstLoad = true;

    private boolean isForceLoad = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (AppCompatActivity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isFirstLoad = true;
        View view = LayoutInflater.from(mActivity).inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void lazyLoad() {
        if (isPrepared() && isFragmentVisible()) {
            if (isForceLoad || isFirstLoad()) {
                isForceLoad = false;
                isFirstLoad = false;
                initData();
            }
        }
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isFirstLoad() {
        return isFirstLoad;
    }

    public boolean isFragmentVisible() {
        return isFragmentVisible;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInvisible();
        }
    }

    protected void onVisible() {
        isFragmentVisible = true;
        lazyLoad();
    }

    protected void onInvisible() {
        isFragmentVisible = false;
    }

    public void setForceLoad(boolean forceLoad) {
        this.isForceLoad = forceLoad;
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected abstract void initData();

}