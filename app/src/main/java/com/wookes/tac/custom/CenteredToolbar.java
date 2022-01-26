package com.wookes.tac.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.wookes.tac.R;

public class CenteredToolbar extends Toolbar {

    private TextView centeredTitleTextView;

    public CenteredToolbar(Context context) {
        super(context);
    }

    public CenteredToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        String s = getResources().getString(resId);
        setTitle(s);
    }

    @Override
    public void setTitle(CharSequence title) {
        getCenteredTitleTextView().setText(title);
    }

    @Override
    public CharSequence getTitle() {
        return getCenteredTitleTextView().getText().toString();
    }

    public void setTypeface(Typeface font) {
        getCenteredTitleTextView().setTypeface(font);
    }

    private TextView getCenteredTitleTextView() {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = new TextView(getContext());
            centeredTitleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.proxima_nova_reg));
            centeredTitleTextView.setSingleLine();
            centeredTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            centeredTitleTextView.setGravity(Gravity.CENTER);
            centeredTitleTextView.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
            centeredTitleTextView.setTextSize(16);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            centeredTitleTextView.setLayoutParams(lp);
            addView(centeredTitleTextView);
        }
        return centeredTitleTextView;
    }
}