package com.wookes.tac.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.wookes.tac.R;
import com.wookes.tac.ui.Hashtag;
import com.wookes.tac.ui.LandingUi;
import com.wookes.tac.ui.UserProfile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WookesTextView extends androidx.appcompat.widget.AppCompatTextView {

    private static final String TAG_PATTERN = ".*?(@|#\\w+).*?";
    private Typeface typeface;

    public WookesTextView(Context context) {
        super(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.proxima_nova_reg);
        this.setTypeface(face);
    }

    public WookesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = ResourcesCompat.getFont(context, R.font.proxima_nova_reg);
        this.setTypeface(face);
    }

    public WookesTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = ResourcesCompat.getFont(context, R.font.proxima_nova_reg);
        this.setTypeface(face);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        setMovementMethod(LinkMovementMethod.getInstance());
        super.setText(text == null ? "" : getTags(text.toString()), type);
    }

    public SpannableString getTags(String pTagString) {
        typeface = ResourcesCompat.getFont(getContext(), R.font.proxima_nova_semibold);
        Pattern tagMatcher = Pattern.compile(TAG_PATTERN);
        SpannableString string = new SpannableString(pTagString);
        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if ((pTagString.charAt(i) == '@' || pTagString.charAt(i) == '#') && (start == -1)) {
                start = i;
            } else if (!String.valueOf(pTagString.charAt(i)).matches("\\w") || (i == pTagString.length() - 1 && start != -1)) {
                char currentChar = pTagString.charAt(i);
                int currentPos = i;
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++;
                    }
                    final String tag = pTagString.substring(start, i);
                    Matcher m = tagMatcher.matcher(tag.toLowerCase());
                    if (m.find()) {
                        string.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                Intent intent = new Intent(getContext(), tag.charAt(0) == '@' ? UserProfile.class : Hashtag.class);
                                if (tag.charAt(0) == '@') {
                                    intent.putExtra("user", tag.replace("@", ""));
                                    intent.putExtra("user_type", 1);
                                } else {
                                    intent.putExtra("hashtag", tag.replace("#", ""));
                                    intent.putExtra("hashtag_type", 1);
                                }
                                getContext().startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint textPaint) {
                                textPaint.setTypeface(Typeface.create(typeface, Typeface.NORMAL));
                                textPaint.setUnderlineText(false);
                            }
                        }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    start = (currentChar == '@' || currentChar == '#') ? currentPos : -1;
                }
            }
        }
        return string;
    }
}
